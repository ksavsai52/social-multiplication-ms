package microservices.book.socialmultiplication.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.repositories.MultiplicationRepository;
import microservices.book.socialmultiplication.repositories.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.repositories.UserRepository;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

	private RandomGeneratorService randomGeneratorService;
	private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;
	private UserRepository userRepository;
	private MultiplicationRepository multiplicationRepository;
	private EventDispatcher eventDispatcher;

	@Autowired
	public MultiplicationServiceImpl(final RandomGeneratorService randomGeneratorService,
			final MultiplicationResultAttemptRepository multiplicationResultAttemptRepository,
			final UserRepository userRepository, final MultiplicationRepository multiplicationRepository,
			final EventDispatcher eventDispatcher) {
		this.randomGeneratorService = randomGeneratorService;
		this.multiplicationResultAttemptRepository = multiplicationResultAttemptRepository;
		this.userRepository = userRepository;
		this.multiplicationRepository = multiplicationRepository;
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public Multiplication createRandomMultiplication() {
		int factorA = randomGeneratorService.generateRandomFactor();
		int factorB = randomGeneratorService.generateRandomFactor();
		return new Multiplication(factorA, factorB);
	}

	@Transactional
	@Override
	public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {
		// check if user already exists with same alias
		Optional<User> user = userRepository.findByAlias(resultAttempt.getUser().getAlias());

		// check if multiplication factors already exists
		Optional<Multiplication> multiplication = multiplicationRepository.findByFactorAAndFactorB(
				resultAttempt.getMultiplication().getFactorA(), resultAttempt.getMultiplication().getFactorB());

		// avoid hack attempts i.e. if user sends correct=true using rest call
		Assert.isTrue(!resultAttempt.isCorrect(), "You can't send an attempt marked as correct!");

		// check if attempt is correct
		boolean correct = resultAttempt.getResultAttempt() == resultAttempt.getMultiplication().getFactorA()
				* resultAttempt.getMultiplication().getFactorB();

		// create a copy of attempt with setting correct according to user's attempt
		MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(
				user.orElse(resultAttempt.getUser()), multiplication.orElse(resultAttempt.getMultiplication()),
				resultAttempt.getResultAttempt(), correct);

		// store checked attempt
		multiplicationResultAttemptRepository.save(checkedAttempt);

		// communicate the result via event
		eventDispatcher.send(new MultiplicationSolvedEvent(checkedAttempt.getId(), checkedAttempt.getUser().getId(),
				checkedAttempt.isCorrect()));

		// return the result
		return correct;
	}

	@Override
	public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
		return multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
	}

	@Override
	public MultiplicationResultAttempt getResultById(Long id) {
		return multiplicationResultAttemptRepository.findById(id).get();
	}

}
