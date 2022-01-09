package com.departamento.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.departamento.model.Unidade;
import com.departamento.repository.UnidadeRepository;

@Controller
public class UnidadeController {
	
	@Autowired
	private UnidadeRepository unidadeRepository;
	
	@GetMapping(value = "/cadastroUnidade")
	public ModelAndView cadastroPage() {
		ModelAndView view = new ModelAndView("unidade/cadastro-unidade");
		return view;
	}
	
	@GetMapping(value = "/relatorioUnidade")
	public ModelAndView relatorioPage() {
		ModelAndView view = new ModelAndView("unidade/relatorio-unidade");
		return view;
	}
	
	@PostMapping(value = "/salvarUnidade")
	public ModelAndView salvar(Unidade unidade,
			RedirectAttributes attributes) {
		ModelAndView view = new ModelAndView("redirect:cadastroUnidade");
		unidadeRepository.save(unidade);
		attributes.addFlashAttribute("msg", "Salvo com sucesso!");
		return view;
	}
	
	@GetMapping(value = "/buscarUnidade")
	public ModelAndView buscar(@RequestParam("nomeBusca") String nome) {
		ModelAndView view = new ModelAndView("unidade/relatorio-unidade");
		
		List<Unidade> unidades = null;
		
		if(nome.isEmpty()) {
			unidades = unidadeRepository.findAll(Sort.by("codigo"));
		}else {
			unidades = unidadeRepository.findByNomeContainingIgnoreCase(nome);
		}
		
		view.addObject("unidades", unidades);
		return view;
	}
	
	@GetMapping(value = "/atualizarUnidade")
	public ModelAndView atualizarPage(@RequestParam("unidadeId") Long id) {
		ModelAndView view = new ModelAndView("unidade/atualizar-unidade");
		Optional<Unidade> unidade = unidadeRepository.findById(id);
		view.addObject("unidade", unidade.get());
		return view;
	}
	
	@PostMapping(value = "/salvarAtualizarUnidade")
	public ModelAndView atualizar(Unidade unidade, 
			RedirectAttributes attributes) throws IllegalAccessException, InvocationTargetException {
		ModelAndView view = new ModelAndView("redirect:atualizarUnidade");
		
		unidadeRepository.save(unidade);
		
		view.addObject("unidadeId", unidade.getId());
		attributes.addFlashAttribute("msg", "Atualizado com sucesso!");
		
		return view;
	}
	
}
