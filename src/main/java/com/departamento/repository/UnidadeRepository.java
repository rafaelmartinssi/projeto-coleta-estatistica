package com.departamento.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.departamento.model.Unidade;

@Repository
@Transactional
public interface UnidadeRepository extends JpaRepository<Unidade, Long>{
	
	List<Unidade> findByNomeContainingIgnoreCase(String nome);

}
