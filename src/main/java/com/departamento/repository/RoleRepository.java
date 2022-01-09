package com.departamento.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.departamento.model.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>{
	
	@Query("select r from Role r where r.role = ?1")
	Role findByRole(String role);
}
