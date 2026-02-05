package fynxt.brand.pspgroup.repository;

import fynxt.brand.pspgroup.entity.PspGroupPsp;
import fynxt.brand.pspgroup.entity.PspGroupPspId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PspGroupPspRepository extends JpaRepository<PspGroupPsp, PspGroupPspId> {

	List<PspGroupPsp> findByPspGroupIdAndPspGroupVersion(Integer pspGroupId, Integer pspGroupVersion);

	List<PspGroupPsp> findByPspId(UUID pspId);

	void deleteByPspGroupId(Integer pspGroupId);

	void deleteByPspGroupIdAndPspGroupVersion(Integer pspGroupId, Integer pspGroupVersion);
}
