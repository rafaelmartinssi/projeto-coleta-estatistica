package com.departamento.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.departamento.model.Usuario;
import java.lang.String;

@Repository
@Transactional
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	Usuario findByMasp(String masp);
	
	List<Usuario> findByNomeContainingIgnoreCase(String nome);
	
	

}
