package microservices.book.socialmultiplication.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import microservices.book.socialmultiplication.domain.User;

/**
 * This repository interface will allow us to store and retrieve {@link User}.
 * 
 * @author keshav
 *
 */
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByAlias(final String alias);
	
}
