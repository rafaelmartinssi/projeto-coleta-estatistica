package com.departamento.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.departamento.model.Pessoa;
import com.departamento.model.Unidade;
import java.lang.String;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long>{
	

	Pessoa findByMasp(String masp);
	
	List<Pessoa> findByUnidade(Unidade unidade);
	
	@Query("select p from Pessoa p where lower(p.nome) like lower(concat('%', ?1, '%')) and p.unidade.id = ?2")
	Page<Pessoa> findByPageWithUnd(String nome, Long unidadeId, Pageable pageable);
	
	@Query("select p from Pessoa p where lower(p.nome) like lower(concat('%', ?1, '%'))")
	Page<Pessoa> findByPage(String nome, Pageable pageable);

}
