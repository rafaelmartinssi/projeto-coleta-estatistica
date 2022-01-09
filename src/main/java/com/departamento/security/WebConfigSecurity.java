package com.departamento.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//classe responsavel por toda configuração do spring security

//aciona todos os mecanismos de configuração do spring 
@Configuration
//torna essa classe uma classe de configuração de segurança - ativas vários modulos de segurança
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementUserDetailService implementUserDetailService;
	
	@Override //Configura as solicitações de acesso por http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() //Desativa as configurações padrão de memória do spring
		.authorizeRequests() //Permitir restringir acessos
		.anyRequest().authenticated()
		.and().formLogin()
		.loginPage("/login").permitAll() // permite qualquer usuario acessar o formulário de login
		.defaultSuccessUrl("/")
		.failureUrl("/login?error=true")
		.and().rememberMe() //verificar se está funcionando depois
		.and().logout().logoutSuccessUrl("/login") //mapeia URL de logout e invalida usuario autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));	
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementUserDetailService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		/*  validação em memória
		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("admin")
		.password("$2a$10$I19bzWP0IoZWh/BTyp2xkO/RDFJEx..HWZhLZWcNk0QCEz7sWV1qS")
		.roles("ADMIN");
		*/
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

}
