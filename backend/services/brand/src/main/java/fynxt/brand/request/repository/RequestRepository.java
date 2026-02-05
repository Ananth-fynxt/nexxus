package fynxt.brand.request.repository;

import fynxt.brand.request.entity.Request;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {}
