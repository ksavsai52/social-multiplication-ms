package microservices.book.socialmultiplication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.repositories.MultiplicationRepository;
import microservices.book.socialmultiplication.repositories.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.repositories.UserRepository;

public class MultiplicationServiceImplTest {

	private MultiplicationServiceImpl multiplicationServiceImpl;

	@Mock
	private RandomGeneratorService randomGeneratorService;

	@Mock
	private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MultiplicationRepository multiplicationRepository;

	@Mock
	private EventDispatcher eventDispatcher;

	@Before
	public void setUp() {
		// with this call to initMocks, we tell Mockito to process the annotations
		MockitoAnnotations.initMocks(this);
		multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService,
				multiplicationResultAttemptRepository, userRepository, multiplicationRepository, eventDispatcher);
	}

	@Test
	public void createRandomMultiplicationTest() throws Exception {
		// given (our mocked Random Generator service will return first 50, then 30)
		given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

		// when
		Multiplication multiplication = multiplicationServiceImpl.createRandomMultiplication();

		// then
		assertThat(multiplication.getFactorA()).isEqualTo(50);
		assertThat(multiplication.getFactorB()).isEqualTo(30);
//		assertThat(multiplication.getResult()).isEqualTo(1500);
	}

	@Test
	public void checkCorrectAttemptTest() throws Exception {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John Doe");
		MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);
		MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, true);
		MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(verifiedAttempt.getId(),
				verifiedAttempt.getUser().getId(), verifiedAttempt.isCorrect());
		given(userRepository.findByAlias("John Doe")).willReturn(Optional.empty());
		given(multiplicationRepository.findByFactorAAndFactorB(multiplication.getFactorA(),
				multiplication.getFactorB())).willReturn(Optional.empty());

		// when
		boolean attemptResult = multiplicationServiceImpl.checkAttempt(resultAttempt);

		// assert
		assertThat(attemptResult).isTrue();
		verify(multiplicationResultAttemptRepository).save(verifiedAttempt);
		verify(eventDispatcher).send(eq(event));
	}

	@Test
	public void checkWrongAttemptTest() throws Exception {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John Doe");
		MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
		MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(resultAttempt.getId(),
				resultAttempt.getUser().getId(), resultAttempt.isCorrect());
		given(userRepository.findByAlias("John Doe")).willReturn(Optional.empty());
		given(multiplicationRepository.findByFactorAAndFactorB(multiplication.getFactorA(),
				multiplication.getFactorB())).willReturn(Optional.empty());

		// when
		boolean attemptResult = multiplicationServiceImpl.checkAttempt(resultAttempt);

		// assert
		assertThat(attemptResult).isFalse();
		verify(multiplicationResultAttemptRepository).save(resultAttempt);
		verify(eventDispatcher).send(eq(event));
	}

	@Test
	public void getStatsForUserTest() throws Exception {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John Doe");
		MultiplicationResultAttempt resultAttempt1 = new MultiplicationResultAttempt(user, multiplication, 3000, false);
		MultiplicationResultAttempt resultAttempt2 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
		List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(resultAttempt1, resultAttempt2);
		given(userRepository.findByAlias("John Doe")).willReturn(Optional.empty());
		given(multiplicationRepository.findByFactorAAndFactorB(multiplication.getFactorA(),
				multiplication.getFactorB())).willReturn(Optional.empty());
		given(multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc("John Doe"))
				.willReturn(latestAttempts);

		// when
		List<MultiplicationResultAttempt> latestAttemptResult = multiplicationServiceImpl.getStatsForUser("John Doe");

		// then
		assertThat(latestAttemptResult).isEqualTo(latestAttempts);
	}

	@Test
	public void getResultByIdTest() throws Exception {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John Doe");
		MultiplicationResultAttempt multiplicationResultAttempt = new MultiplicationResultAttempt(user, multiplication,
				3000, false);
		given(multiplicationResultAttemptRepository.findById(1l)).willReturn(Optional.of(multiplicationResultAttempt));
		
		// when
		MultiplicationResultAttempt resultAttempt = multiplicationServiceImpl.getResultById(1l);
		
		// then
		assertThat(resultAttempt).isEqualTo(multiplicationResultAttempt);
	}

}
