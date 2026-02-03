package fynxt.brand.brand.repository;

import fynxt.brand.brand.entity.Brand;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

	@Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL AND b.name = :name")
	Optional<Brand> findByName(@Param("name") String name);

	@Query("SELECT COUNT(b) > 0 FROM Brand b WHERE b.deletedAt IS NULL AND b.name = :name")
	boolean existsByName(@Param("name") String name);

	@Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL")
	List<Brand> findAll();

	@Query("SELECT COUNT(b) > 0 FROM Brand b WHERE b.deletedAt IS NULL AND b.id = :id")
	boolean existsById(@Param("id") UUID id);

	@Query("SELECT COUNT(b) > 0 FROM Brand b WHERE b.deletedAt IS NULL AND "
			+ "(:fiId IS NULL AND b.fiId IS NULL OR :fiId IS NOT NULL AND b.fiId = :fiId) "
			+ "AND b.name = :name")
	boolean existsByFiIdAndName(@Param("fiId") Short fiId, @Param("name") String name);

	@Query("SELECT COUNT(b) > 0 FROM Brand b WHERE b.deletedAt IS NULL AND "
			+ "(:fiId IS NULL AND b.fiId IS NULL OR :fiId IS NOT NULL AND b.fiId = :fiId) "
			+ "AND b.name = :name AND b.id != :id")
	boolean existsByFiIdAndNameAndIdNot(@Param("fiId") Short fiId, @Param("name") String name, @Param("id") UUID id);

	@Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL AND b.fiId = :fiId")
	List<Brand> findByFiId(@Param("fiId") Short fiId);

	@Query(
			"SELECT b FROM Brand b JOIN BrandUser bu ON b.id = bu.brandId WHERE b.deletedAt IS NULL AND bu.userId = :userId")
	List<Brand> findByUserId(@Param("userId") Integer userId);

	@Query("SELECT COUNT(b) > 0 FROM Brand b WHERE b.deletedAt IS NULL AND b.email = :email")
	boolean existsByEmail(@Param("email") String email);

	@Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL AND b.id = :id")
	Optional<Brand> findById(@Param("id") UUID id);
}
