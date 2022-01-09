package com.departamento.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.departamento.model.Estatistica;
import com.departamento.model.Unidade;

@Repository
@Transactional
public interface EstatisticaRepository extends JpaRepository<Estatistica, Long>{
	
	List<Estatistica> findByAnoAndUnidade(String ano, Unidade unidade);
	List<Estatistica> findByMesAndUnidade(String mes, Unidade unidade);
	List<Estatistica> findByAnoAndMesAndUnidade(String ano, String mes, Unidade unidade);
	List<Estatistica> findByAnoAndMes(String ano, String mes);
}
