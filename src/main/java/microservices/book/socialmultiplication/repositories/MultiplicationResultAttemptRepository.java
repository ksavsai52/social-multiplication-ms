package microservices.book.socialmultiplication.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;

/**
 * This repository interface will allow us to store and retrieve
 * {@link MultiplicationResultAttempt}.
 * 
 * @author keshav
 *
 */
public interface MultiplicationResultAttemptRepository extends CrudRepository<MultiplicationResultAttempt, Long> {

	/**
	 * 
	 * @param userAlias
	 * @return the latest 5 attempts for a given user, identified by their alias.
	 */
	List<MultiplicationResultAttempt> findTop5ByUserAliasOrderByIdDesc(String userAlias);

}
