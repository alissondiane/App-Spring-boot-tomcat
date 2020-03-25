package com.alisson.springboot.datajpa.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.alisson.springboot.datajpa.app.model.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente,Long> {
	/*Consultas crud agregadas y gestionadas internamente
	 * Maneja la notacion @Component internamente
	 * */
	
	/*Agregar consultas personalizadas
	 * https://docs.spring.io/spring-data/jpa/docs/2.2.5.RELEASE/reference/html/
	 * https://docs.spring.io/spring-data/jpa/docs/2.2.5.RELEASE/reference/html/#jpa.query-methods
	 * */
	@Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
	public Cliente fetchByIdWithFacturas(Long id);
	
}
