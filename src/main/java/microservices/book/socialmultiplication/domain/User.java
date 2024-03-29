package microservices.book.socialmultiplication.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Stores Information to identify the user.
 * @author keshav
 *
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Entity
public final class User {

	@Id
	@GeneratedValue
	@Column(name = "USER_ID")
	private Long id;
	
	private final String alias;
	
	protected User() {
		alias = null;
	}
	
}
