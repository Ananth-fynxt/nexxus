package fynxt.brand.user.repository;

import fynxt.brand.user.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.email = :email")
	Optional<User> findByEmail(@Param("email") String email);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.deletedAt IS NULL AND u.email = :email")
	boolean existsByEmail(@Param("email") String email);
}
