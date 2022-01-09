package com.departamento.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.departamento.model.Pessoa;
import com.departamento.model.Unidade;
import com.departamento.model.Usuario;
import com.departamento.repository.PessoaRepository;
import com.departamento.repository.UnidadeRepository;
import com.departamento.repository.UsuarioRepository;
import com.departamento.service.JasperService;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UnidadeRepository unidadeRepository;
	
	@Autowired
	private JasperService jasperService;
	
	@GetMapping(value = "/geraRelatorio1")
	public void exibirRelatorioR1(HttpServletResponse response) throws IOException {
		String code = "01";
		byte[] bytes = jasperService.exportarPDF(code);
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-disposition", "inline; filename=relatorio-01.pdf");
		response.getOutputStream().write(bytes);
	}
	
	@GetMapping(value = "/geraRelatorio2")
	public void exibirRelatorioR2(HttpServletResponse response,
			@RequestParam(name = "nomeBusca", required = false) String nome,
			@RequestParam(name = "unidadeBusca", required = false) Long unidadeId) throws IOException {
		String code = "02";
		
		jasperService.addParams("NOME", nome.isEmpty() ? null : nome);
		jasperService.addParams("UNIDADE", unidadeId == null ? null : unidadeId);
		
		byte[] bytes = jasperService.exportarPDF(code);
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-disposition", "inline; filename=relatorio-01.pdf");
		response.getOutputStream().write(bytes);
	}
	
	@GetMapping(value = "/cadastroPessoa")
	public ModelAndView cadastroPage () {
		ModelAndView view = new ModelAndView("pessoa/cadastro-pessoa");
		
		Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
		Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
		
		List<Pessoa> pessoas = pessoaRepository.findByUnidade(usuario.getUnidade());
		
		view.addObject("pessoas", pessoas);
		
		return view;
	}
	
	@PostMapping(value = "/salvarPessoa")
	public ModelAndView salvar(Pessoa pessoa,
			RedirectAttributes attributes) {
		ModelAndView view = new ModelAndView("redirect:cadastroPessoa");
		
		if(pessoaRepository.findByMasp(pessoa.getMasp()) == null) {
		
			Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
			Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
			
			pessoa.setUsuario(usuario);
			pessoa.setUnidade(usuario.getUnidade());
			
			Date date = new Date();
			pessoa.setDataRegistro(date);
			
			pessoaRepository.save(pessoa);
			
			attributes.addFlashAttribute("msg", "Incluído com sucesso!");
		
		}else {
			attributes.addFlashAttribute("maspblock", "Masp já possui cadastro");
		}
		
		return view;
	}
	
	@GetMapping(value = "/excluirPessoa")
	public ModelAndView excluir (@RequestParam("pessoaId") Long id,
			RedirectAttributes attributes) {
		ModelAndView view = new ModelAndView("redirect:pessoa/cadastroPessoa");
		
		pessoaRepository.deleteById(id);
		
		attributes.addFlashAttribute("msg", "Excluido com sucesso!");
		
		return view;
	}
	
	@GetMapping(value = "/atualizarPessoa")
	public ModelAndView atualizarPage(@RequestParam("pessoaId") Long id) {
		ModelAndView view = new ModelAndView("pessoa/atualizar-pessoa");
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(id);
		
		view.addObject("pessoa", pessoa.get());
		
		return view;
	}
	
	@PostMapping("/salvarAtualizarPessoa")
	public ModelAndView atualizar(Pessoa pessoa, RedirectAttributes attributes) {
		
		ModelAndView view = new ModelAndView("redirect:atualizarPessoa");			
			
			Optional<Pessoa> p = pessoaRepository.findById(pessoa.getId());
			pessoa.setUsuario(p.get().getUsuario());
			pessoa.setDataRegistro(p.get().getDataRegistro());
			pessoa.setUnidade(p.get().getUnidade());
		
			pessoaRepository.save(pessoa);
			
			view.addObject("pessoaId", pessoa.getId());
			attributes.addFlashAttribute("msg", "Atualizado com sucesso");
		
		return view;
	}
	
	@GetMapping(value = "/relatorioPessoa")
	public ModelAndView relatorio () {
		ModelAndView view = new ModelAndView("pessoa/relatorio-pessoa");
		
		List<Unidade> unidades = unidadeRepository.findAll();
		
		view.addObject("unidades", unidades);
		
		return view;
	}
	
	@GetMapping(value = "/buscaPorParametro")
	public ModelAndView buscaPorParametro(@RequestParam("nomeBusca") String nome,
			@RequestParam("unidadeBusca") Long unidadeId, 
			@PageableDefault(size = 15, sort = "nome") Pageable pageable) {
		ModelAndView view = new ModelAndView("pessoa/relatorio-pessoa");
		
		Page<Pessoa> page = null;
		
		if(nome.isEmpty() & unidadeId == null) {
			page = pessoaRepository.findAll(pageable);
		}else {
			if(unidadeId == null) {
				page = pessoaRepository.findByPage(nome, pageable);
			}else {
				page = pessoaRepository.findByPageWithUnd(nome, unidadeId, pageable);
			}
		}
		
		view.addObject("nomeBusca", nome);
		view.addObject("unidadeBusca", unidadeId);
		view.addObject("pessoa", page);
		
		return view;
	}

}
