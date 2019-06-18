package microservices.book.socialmultiplication.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import microservices.book.socialmultiplication.domain.Multiplication;

/**
 * This repository interface will allow us to store and retrieve {@link Multiplication}.
 * 
 * @author keshav
 *
 */
public interface MultiplicationRepository extends CrudRepository<Multiplication, Long> {

	Optional<Multiplication> findByFactorAAndFactorB(final int factorA, final int factorB);
	
}
