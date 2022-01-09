package com.departamento.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.departamento.model.Estatistica;
import com.departamento.model.Usuario;
import com.departamento.repository.EstatisticaRepository;
import com.departamento.repository.UsuarioRepository;

@Controller
public class EstatisticaController {
	
	@Autowired 
	private EstatisticaRepository estatisticaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping("/cadastroEstatistica")
	public ModelAndView estatisticaPage() {
		ModelAndView view = new ModelAndView("estatistica/cadastro-estatistica");
		return view;
	}
	
	@GetMapping("/relatorioEstatistica")
	public ModelAndView relatorioEstatistica() {
		ModelAndView view = new ModelAndView("estatistica/relatorio-estatistica-unidade");
		
		return view;
	}
	
	@GetMapping("/gerenciaEstatistica")
	public ModelAndView gerenciaEstatistica() {
		ModelAndView view = new ModelAndView("estatistica/gerencia-estatistica");
		
		return view;
	}
	
	@GetMapping("/bloqueioEstatistica")
	public ModelAndView bloqueioEstatistica() {
		ModelAndView view = new ModelAndView("estatistica/bloqueio-estatistica");
		
		return view;
	}
	
	@GetMapping("/bloqueioEditarEstatistica")
	public ModelAndView bloqueioEditarEstatistica(@RequestParam("estatisticaId") Long id,
			@RequestParam(value = "ano") String ano,
			@RequestParam(value = "mes") String mes) {
		ModelAndView view = new ModelAndView("estatistica/bloqueio-estatistica");
		
		Optional<Estatistica> estatistica = estatisticaRepository.findById(id);
		estatistica.get().setEditar(!estatistica.get().getEditar());
		
		estatisticaRepository.save(estatistica.get());
		
		List<Estatistica> estatisticas = estatisticaRepository.findByAnoAndMes(ano, mes);
		
		view.addObject("estatistica", estatisticas);
		view.addObject("ano", ano);
		view.addObject("mes", mes);
		
		return view;
	}
	
	@GetMapping("/registroEstatistica")
	public ModelAndView registroPage(@RequestParam("estatisticaId") Long id) {
		ModelAndView view = new ModelAndView("estatistica/registro-estatistica");
		
		Optional<Estatistica> estatistica = estatisticaRepository.findById(id);
		
		view.addObject("estatistica", estatistica.get());
		return view;
	}
	
	@GetMapping("/atualizarEstatistica")
	public ModelAndView atualizarPage(@RequestParam("estatisticaId") Long id) {
		ModelAndView view = new ModelAndView("estatistica/atualizar-estatistica");
		
		Optional<Estatistica> estatistica = estatisticaRepository.findById(id);
		
		view.addObject("estatistica", estatistica.get());
		return view;
	}
	
	@PostMapping("/salvarEstatistica")
	public ModelAndView salvar(Estatistica estatistica,
			RedirectAttributes attributes)	{
		ModelAndView view = new ModelAndView("redirect:registroEstatistica");
		
		Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
		Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
		
		estatistica.setUsuario(usuario);
		estatistica.setUnidade(usuario.getUnidade());
		
		estatisticaRepository.save(estatistica);
		
		view.addObject("estatisticaId", estatistica.getId());
		attributes.addFlashAttribute("msg", "Salvo com sucesso!");
		
		return view;
	}
	
	@PostMapping("/salvarAtualizarEstatistica")
	public ModelAndView atualizar(Estatistica estatistica,
			RedirectAttributes attributes) {
		ModelAndView view = new ModelAndView("redirect:registroEstatistica");
		
		Optional<Estatistica> estatisticaAtual = estatisticaRepository.findById(estatistica.getId());
		estatistica.setUnidade(estatisticaAtual.get().getUnidade());
		estatistica.setUsuario(estatisticaAtual.get().getUsuario());
		
		estatisticaRepository.save(estatistica);
		
		view.addObject("estatisticaId", estatistica.getId());
		attributes.addFlashAttribute("msg", "Atualizado com sucesso!");
		
		return view;
	}
	
	@GetMapping("/buscarMinhasEstatisticas")
	public ModelAndView buscarMinhasEstatistica(@RequestParam(value = "ano", required = false) String ano,
			@RequestParam(value = "mes", required = false) String mes) {
		ModelAndView view = new ModelAndView("estatistica/relatorio-estatistica-unidade");
		
		Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
		Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
		
			List<Estatistica> estatistica = null;
		if(!ano.isEmpty() && !mes.isEmpty()) {
			estatistica = estatisticaRepository.findByAnoAndMesAndUnidade(ano, mes, usuario.getUnidade());
		}else if(ano.isEmpty() && !mes.isEmpty()) {
			estatistica = estatisticaRepository.findByMesAndUnidade(mes, usuario.getUnidade());
		}else if(mes.isEmpty() && !ano.isEmpty()){
			estatistica = estatisticaRepository.findByAnoAndUnidade(ano, usuario.getUnidade());
		}
		
		view.addObject("estatistica", estatistica);
		view.addObject("ano", ano);
		view.addObject("mes", mes);
		
		return view;
	}
	
	@GetMapping("/buscarEstatisticasBloqueio")
	public ModelAndView buscarEstatisticaBloqueio(@RequestParam(value = "ano", required = false) String ano,
			@RequestParam(value = "mes", required = false) String mes) {
		ModelAndView view = new ModelAndView("estatistica/bloqueio-estatistica");
		
		List<Estatistica> estatistica = null;
		if(!ano.isEmpty() && !mes.isEmpty()) {
			estatistica = estatisticaRepository.findByAnoAndMes(ano, mes);
		}
		
		view.addObject("estatistica", estatistica);
		view.addObject("ano", ano);
		view.addObject("mes", mes);
		
		return view;
	}

	
}
