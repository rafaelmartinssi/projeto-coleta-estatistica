package com.departamento.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.departamento.model.Role;
import com.departamento.model.Unidade;
import com.departamento.model.Usuario;
import com.departamento.repository.RoleRepository;
import com.departamento.repository.UnidadeRepository;
import com.departamento.repository.UsuarioRepository;

@Controller
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired 
	private RoleRepository roleRepository;
	
	@Autowired
	private UnidadeRepository unidadeRepository;
	
	@GetMapping(value = "/cadastroUsuario")
	public ModelAndView cadastroPage() {
		
		ModelAndView view = new ModelAndView("usuario/cadastro-usuario");
		
		List<Unidade> unidades = unidadeRepository.findAll();
		
		view.addObject("unidades", unidades);
		
		return view;
	}
	
	@GetMapping(value = "/relatorioUsuario")
	public String relatorio() {
		return "usuario/relatorio-usuario";
	}
	
	@GetMapping(value = "/atualizarSenha")
	public ModelAndView atualizarPage() {
		
		ModelAndView view = new ModelAndView("usuario/atualizar-senha-usuario");
		
		Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
				
		Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
		
		view.addObject("usuario", usuario);
		
		return view;
	}
	
	@PostMapping(value = "/salvarAtualizarSenha")
	public ModelAndView salvarAtualizarSenha(@RequestParam("senha") String senha, 
			@RequestParam("novaSenha") String novaSenha,
			RedirectAttributes attributes) {
		
		ModelAndView view;
		
		Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
		Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
		
		if(BCrypt.checkpw(senha, usuario.getPassword())) {
			
			view = new ModelAndView("redirect:/");
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			usuario.setSenha(encoder.encode(novaSenha));
			
			usuarioRepository.save(usuario);
			
			attributes.addFlashAttribute("msg", "Senha alterada com sucesso");
		}else {

			view = new ModelAndView("usuario/atualizar-senha");
			view.addObject("usuario", usuario);
			
			view.addObject("msgerror", "Senha não confere!");
		}
		
		return view;
	}
	
	@GetMapping(value = "/buscarPorNome")
	public ModelAndView buscarPorNome(@RequestParam("nomeBusca") String nome) {
		ModelAndView view = new ModelAndView("usuario/relatorio-usuario");
		
		List<Usuario> list = new ArrayList<Usuario>();
		
		if(nome.isEmpty()) {
		
			list = (List<Usuario>) usuarioRepository.findAll();
			
		}else {
			
			list = usuarioRepository.findByNomeContainingIgnoreCase(nome);
			
		}
		
		view.addObject("usuario", list);
		
		return view;
	}
	
	@PostMapping("/salvarUsuario")
	public ModelAndView salvar(Usuario usuario, RedirectAttributes attributes, 
			@RequestParam("senhaValor") String senha) {
		
		ModelAndView view = new ModelAndView("redirect:cadastroUsuario");
		
		if(usuarioRepository.findByMasp(usuario.getMasp()) == null) {
			
			Date date = new Date();
			usuario.setDataRegistro(date);
			
			//inicio - adicionando ROLE_usuario para todos inicialmente
			List<Role> list = new ArrayList<Role>();
			Role auth = roleRepository.findByRole("ROLE_USER");
			list.add(auth);
			usuario.setRoles(list);
			//fim - adicionando authorities
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			usuario.setSenha(encoder.encode(senha));
			
			usuarioRepository.save(usuario);
			
			attributes.addFlashAttribute("msg", "Salvo com sucesso!");
			
		}else {
			attributes.addFlashAttribute("maspblock", "Masp já possui cadastro!");
		}
		
		return view;
	}
	
	@PostMapping("/salvarAtualizarUsuario")
	public ModelAndView atualizar(Usuario usuario, RedirectAttributes attributes) {
		
		ModelAndView view = new ModelAndView("redirect:atualizarUsuario");
			
			Optional<Usuario> u = usuarioRepository.findById(usuario.getId());
			usuario.setSenha(u.get().getSenha());
			usuario.setDataRegistro(u.get().getDataRegistro());
			
			usuarioRepository.save(usuario);
			
			view.addObject("usuarioId", usuario.getId());
			attributes.addFlashAttribute("msg", "Atualizado com sucesso!");
		
		return view;
	}
	
	@GetMapping(value = "/atualizarUsuario")
	public ModelAndView atualizarPage (@RequestParam("usuarioId") Long id) {
		ModelAndView view = new ModelAndView("usuario/atualizar-usuario");
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		List<Unidade> unidades = unidadeRepository.findAll();
		view.addObject("unidades", unidades);
		
		view.addObject("usuario", usuario.get());
		
		return view;
	}
	
	@GetMapping(value = "/registroPessoa")
	public ModelAndView informacoes() {
		ModelAndView view = new ModelAndView("compartilhada/registro-pessoa");
		
		Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication(); 
		Usuario usuario = usuarioRepository.findByMasp(authentication.getName());
		
		view.addObject("usuario", usuario);
		
		return view;
	}
	
	@GetMapping(value = "/autorizarUsuario")
	public ModelAndView usuarioroles(@RequestParam("usuarioId") Long id) {
		ModelAndView view = new ModelAndView("usuario/autorizar-usuario");
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		view.addObject("usuario", usuario.get());
		view.addObject("roles", usuario.get().getRoles());
		
		return view;
	}
	
	@PostMapping(value = "/salvarAutorizar")
	private ModelAndView salvarautorizar(@RequestParam("id") Long id,
			@RequestParam("authorization") String role,
			RedirectAttributes attributes) {
		ModelAndView view = new ModelAndView("redirect:autorizarUsuario");
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		Role auth = roleRepository.findByRole(role);
		
		List<Role> list = usuario.get().getRoles();
		
		if(!list.contains(auth)) {
		
			list.add(auth);
			
			usuario.get().setRoles(list);
	
			usuarioRepository.save(usuario.get());
			
			view.addObject("usuarioId", usuario.get().getId());
			attributes.addFlashAttribute("msg", "Nova permissão atribuida!");
		
		}else {
			view.addObject("usuarioId", usuario.get().getId());
			attributes.addFlashAttribute("msg", "Permissão já cadastrada!");
		}
		
		return view;
	}
	
	@GetMapping(value = "/excluirRole")
	public ModelAndView excluirrole(@RequestParam("roleNome") String role,
			@RequestParam("usuarioId") Long idusuario,
			RedirectAttributes attributes) {
		ModelAndView view = new ModelAndView("redirect:autorizarUsuario");
		
		Optional<Usuario> usuario = usuarioRepository.findById(idusuario);
		
		List<Role> list = usuario.get().getRoles();
		
		Role auth = roleRepository.findByRole(role);
		
		list.remove(auth);
		
		usuario.get().setRoles(list);
		
		usuarioRepository.save(usuario.get());
 		
		view.addObject("usuarioId", usuario.get().getId());
		attributes.addFlashAttribute("msg", "Permissão excluida!");
		
		return view;
	}

}
