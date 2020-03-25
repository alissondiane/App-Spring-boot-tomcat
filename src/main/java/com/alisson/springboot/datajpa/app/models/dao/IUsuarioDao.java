package com.alisson.springboot.datajpa.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.alisson.springboot.datajpa.app.model.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario,Long>{
	public Usuario findByUsername(String username);
}
