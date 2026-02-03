package fynxt.brand.brandrole.repository;

import fynxt.brand.brandrole.entity.BrandRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRoleRepository extends JpaRepository<BrandRole, Integer> {

	@Query("SELECT br FROM BrandRole br WHERE br.deletedAt IS NULL AND br.id = :id")
	Optional<BrandRole> findById(@Param("id") Integer id);

	@Query("SELECT br FROM BrandRole br WHERE br.deletedAt IS NULL AND br.name = :name")
	Optional<BrandRole> findByName(@Param("name") String name);

	@Query(
			"SELECT COUNT(br) > 0 FROM BrandRole br WHERE br.deletedAt IS NULL AND br.brandId = :brandId AND br.environmentId = :environmentId AND br.name = :name")
	boolean existsByBrandIdAndEnvironmentIdAndName(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId, @Param("name") String name);

	@Query("SELECT br FROM BrandRole br WHERE br.deletedAt IS NULL")
	List<BrandRole> findAll();

	void deleteById(@Param("id") Integer id);

	@Query("SELECT br FROM BrandRole br WHERE br.deletedAt IS NULL AND br.brandId = :brandId")
	List<BrandRole> findByBrandId(@Param("brandId") UUID brandId);

	@Query(
			"SELECT br FROM BrandRole br WHERE br.deletedAt IS NULL AND br.brandId = :brandId AND br.environmentId = :environmentId")
	List<BrandRole> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);
}
