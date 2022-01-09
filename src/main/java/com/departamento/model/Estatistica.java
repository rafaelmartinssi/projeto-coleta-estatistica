package com.departamento.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Estatistica implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Usuario usuario;
	@ManyToOne
	private Unidade unidade;
	private String mes;
	private String ano;
	
	private Long apfd;
	private Long ip;
	private Long tco;

	private BigDecimal apfdTotal;
	private BigDecimal ipTotal;
	private BigDecimal tcoTotal;
	
	private Boolean editar;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Unidade getUnidade() {
		return unidade;
	}
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getAno() {
		return ano;
	}
	public void setAno(String ano) {
		this.ano = ano;
	}
	public Long getApfd() {
		return apfd;
	}
	public void setApfd(Long apfd) {
		this.apfd = apfd;
	}
	public Long getIp() {
		return ip;
	}
	public void setIp(Long ip) {
		this.ip = ip;
	}
	public Long getTco() {
		return tco;
	}
	public void setTco(Long tco) {
		this.tco = tco;
	}
	public BigDecimal getTcoTotal() {
		return tcoTotal;
	}
	public void setTcoTotal(BigDecimal tcoTotal) {
		this.tcoTotal = tcoTotal;
	}
	public BigDecimal getApfdTotal() {
		return apfdTotal;
	}
	public void setApfdTotal(BigDecimal apfdTotal) {
		this.apfdTotal = apfdTotal;
	}
	public BigDecimal getIpTotal() {
		return ipTotal;
	}
	public void setIpTotal(BigDecimal ipTotal) {
		this.ipTotal = ipTotal;
	}
	public Boolean getEditar() {
		return editar;
	}
	public void setEditar(Boolean editar) {
		this.editar = editar;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Estatistica other = (Estatistica) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
