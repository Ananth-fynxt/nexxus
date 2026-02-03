package fynxt.brand.branduser.repository;

import fynxt.brand.branduser.entity.BrandUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandUserRepository extends JpaRepository<BrandUser, Integer> {

	@Query("SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.id = :id")
	Optional<BrandUser> findById(@Param("id") Integer id);

	@Query("SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.email = :email")
	Optional<BrandUser> findByEmail(@Param("email") String email);

	@Query("SELECT COUNT(bu) > 0 FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.email = :email")
	boolean existsByEmail(@Param("email") String email);

	@Query("SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL")
	List<BrandUser> findAll();

	void deleteById(@Param("id") Integer id);

	@Query(
			"SELECT COUNT(bu) > 0 FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.brandId = :brandId AND bu.environmentId = :environmentId AND bu.email = :email")
	boolean existsByBrandIdAndEnvironmentIdAndEmail(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId, @Param("email") String email);

	@Query("SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.brandId = :brandId")
	List<BrandUser> findByBrandId(@Param("brandId") UUID brandId);

	@Query("SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.userId = :userId")
	List<BrandUser> findByUserId(@Param("userId") Integer userId);

	@Query("SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.userId = :userId AND bu.email = :email")
	Optional<BrandUser> findByUserIdAndEmail(@Param("userId") Integer userId, @Param("email") String email);

	@Query(
			"SELECT COUNT(bu) > 0 FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.userId = :userId AND bu.brandId = :brandId AND bu.environmentId = :environmentId AND bu.brandRoleId = :brandRoleId")
	boolean existsByUserIdAndBrandIdAndEnvironmentIdAndBrandRoleId(
			@Param("userId") Integer userId,
			@Param("brandId") UUID brandId,
			@Param("environmentId") UUID environmentId,
			@Param("brandRoleId") Integer brandRoleId);

	@Query(
			"SELECT bu FROM BrandUser bu WHERE bu.deletedAt IS NULL AND bu.brandId = :brandId AND bu.environmentId = :environmentId")
	List<BrandUser> findByBrandIdAndEnvironmentId(
			@Param("brandId") UUID brandId, @Param("environmentId") UUID environmentId);
}
