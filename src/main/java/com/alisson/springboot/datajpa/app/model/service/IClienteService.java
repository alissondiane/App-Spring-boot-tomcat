package com.alisson.springboot.datajpa.app.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alisson.springboot.datajpa.app.model.entity.Cliente;
import com.alisson.springboot.datajpa.app.model.entity.Factura;
import com.alisson.springboot.datajpa.app.model.entity.Producto;

public interface IClienteService {
	public List<Cliente> findAll();
	
	public Page<Cliente> findAll(Pageable pageable);
	
	public void save(Cliente cliente);
	
	public Cliente findOne(Long id);
	
	public void delete(Long id);
	
	public List<Producto> finByNombre(String term);
	
	public void saveFactura(Factura factura);
	
	public Producto findProductoById(Long id);
	
	public Factura findFacturaById(Long id);
	
	public void deleteFactura(Long id);
	
	public Factura fetchByIdWithClienteWithItemFacturaWithProcucto(Long id);
	
	public Cliente fetchByIdWithFacturas(Long id);
}
