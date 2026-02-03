package fynxt.brand.environment.repository;

import fynxt.brand.environment.dto.EnvironmentCredentialsDto;
import fynxt.brand.environment.entity.Environment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {

	@Query("SELECT e FROM Environment e WHERE e.deletedAt IS NULL AND e.name = :name")
	Optional<Environment> findByName(@Param("name") String name);

	@Query("SELECT COUNT(e) > 0 FROM Environment e WHERE e.deletedAt IS NULL AND e.name = :name")
	boolean existsByName(@Param("name") String name);

	@Query("SELECT e FROM Environment e WHERE e.deletedAt IS NULL")
	List<Environment> findAll();

	@Query(
			"SELECT COUNT(e) > 0 FROM Environment e WHERE e.deletedAt IS NULL AND e.brandId = :brandId AND e.name = :name")
	boolean existsByBrandIdAndName(@Param("brandId") UUID brandId, @Param("name") String name);

	@Query("SELECT COUNT(e) > 0 FROM Environment e WHERE e.deletedAt IS NULL AND e.secret = :secret")
	boolean existsBySecret(@Param("secret") UUID secret);

	@Query("SELECT e FROM Environment e WHERE e.deletedAt IS NULL AND e.secret = :secret")
	Optional<Environment> findBySecret(@Param("secret") UUID secret);

	@Query("SELECT e FROM Environment e WHERE e.deletedAt IS NULL AND e.brandId = :brandId")
	List<Environment> findByBrandId(@Param("brandId") UUID brandId);

	@Query("SELECT e FROM Environment e WHERE e.deletedAt IS NULL AND e.token = :token")
	Optional<Environment> findByToken(@Param("token") UUID token);

	@Query("SELECT new fynxt.brand.environment.dto.EnvironmentCredentialsDto(e.id, e.secret, e.token) "
			+ "FROM Environment e WHERE e.deletedAt IS NULL AND e.id = :id")
	Optional<EnvironmentCredentialsDto> findCredentialsById(@Param("id") UUID id);

	@Query("SELECT e FROM Environment e WHERE e.deletedAt IS NULL AND e.id = :id")
	Optional<Environment> findById(@Param("id") UUID id);
}
