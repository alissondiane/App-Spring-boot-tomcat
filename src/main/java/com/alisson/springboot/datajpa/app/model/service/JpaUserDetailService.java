package com.alisson.springboot.datajpa.app.model.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alisson.springboot.datajpa.app.model.entity.Rol;
import com.alisson.springboot.datajpa.app.model.entity.Usuario;

import com.alisson.springboot.datajpa.app.models.dao.IUsuarioDao;

@Service("jpaUserDetailService")
public class JpaUserDetailService implements UserDetailsService{

	@Autowired
	private IUsuarioDao usuarioDao;
	
	private Logger logger = LoggerFactory.getLogger(JpaUserDetailService.class);
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioDao.findByUsername(username);
		
		if(usuario==null) {
			logger.error("Error login: no existe el usuario '" + username + "'");
			throw new UsernameNotFoundException("Username " + username + " no existe en el sistema");
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		for(Rol rol: usuario.getRoles()) {
			logger.info("Role: ".concat(rol.getAuthority()));
			authorities.add(new SimpleGrantedAuthority(rol.getAuthority()));
		}
		
		if(authorities.isEmpty()) {
			logger.error("Error login:'" + username + "' no tiene roles asignados!");
			throw new UsernameNotFoundException("Error Login: usuario '" + username + "' no tiene roles asignados!");
		}
		
		
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);
	}
	
}
