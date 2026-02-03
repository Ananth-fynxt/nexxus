package fynxt.brand.fi.repository;

import fynxt.brand.fi.entity.Fi;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FiRepository extends JpaRepository<Fi, Short> {

	boolean existsByName(@Param("name") String name);

	boolean existsByEmail(@Param("email") String email);

	List<Fi> findAll();

	Optional<Fi> findByUserId(@Param("userId") Integer userId);
}
