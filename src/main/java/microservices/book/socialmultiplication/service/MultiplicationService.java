package microservices.book.socialmultiplication.service;

import java.util.List;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;

public interface MultiplicationService {

	/**
	 * Creates a Multiplication object with two randomly-generated factors
	 * between 11 and 99.
	 * Generates a random {@link Multiplication} object.
	 * @return a Multiplication object with random factors.
	 */
	Multiplication createRandomMultiplication();
	
	/**
	 * 
	 * @param resultAttempt
	 * @return true if the attempt matches the result of the multiplication, otherwise false
	 */
	boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);
	
	/**
	 * 
	 * @param userAlias
	 * @return 5 {@link MultiplicationResultAttempt} items for {@link User} with alias=userAlias
	 */
	List<MultiplicationResultAttempt> getStatsForUser(final String userAlias);
	
	/**
	 * 
	 * @param id
	 * @return {@link MultiplicationResultAttempt}
	 */
	MultiplicationResultAttempt getResultById(final Long id);
}
