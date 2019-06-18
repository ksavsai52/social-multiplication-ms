package microservices.book.socialmultiplication.event;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import microservices.book.socialmultiplication.domain.Multiplication;

/**
 * Event that models the fact that a {@link Multiplication} has been solved in the system.
 * Provides some context information about the multiplication.
 * @author keshav
 *
 */

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MultiplicationSolvedEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6164910704866973278L;

	private final Long multiplicationResultAttemptId;
	private final Long userId;
	private final boolean correct;
	
}
