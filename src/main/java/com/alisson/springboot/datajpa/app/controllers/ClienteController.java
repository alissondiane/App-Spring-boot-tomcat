package com.alisson.springboot.datajpa.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alisson.springboot.datajpa.app.model.entity.Cliente;
import com.alisson.springboot.datajpa.app.model.service.IClienteService;
import com.alisson.springboot.datajpa.app.model.service.IUploadFileService;
import com.alisson.springboot.datajpa.app.util.paginator.PageRender;
import com.alisson.springboot.datajpa.app.view.xml.ClienteList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private MessageSource messageSource;
	
	//Nueva forma de acceso a la foto almacenada (Forma programatica).
	//Agregando securidad con uso de anotaciones
	@Secured({"ROLE_USER","ROLE_ADMIN"})
	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
	
	//@Secured("ROLE_USER")
	//@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_OTRO')")
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, Map<String,Object> model, RedirectAttributes flash) {
		//Cliente cliente = clienteService.findOne(id);
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		if(cliente==null) {
			flash.addFlashAttribute("error","El cliente no existe en la base de datos");
			return "redirect:/listar";		
		}
		
		model.put("cliente",cliente);
		model.put("titulo", "Detalle cliente : " + cliente.getNombre());
		return "ver";
	}
	//para que maneje json y xml
	//acceso http://localhost:8080/listar-rest /por defecto devuelve xml
	//acceso http://localhost:8080/listar-rest?format=xml
	//acceso http://localhost:8080/listar-rest?format=json
	@GetMapping(value = "/listar-rest")
	public @ResponseBody ClienteList listarRest() {
		   return new ClienteList(clienteService.findAll());
	}
	/*
	//solo para el manejo de json
	//acceso http://localhost:8080/listar-rest
	@GetMapping(value = "/listar-rest")
	public @ResponseBody List<Cliente> listarRest() {
		   return clienteService.findAll();
	}
	*/
	@RequestMapping(value = {"/listar","/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page, 
			Model model,
			Authentication authentication,
			HttpServletRequest request,
			Locale locale) {
		if(authentication!=null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}
		//obtener el usuarioa autenticado de forma estatica
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth!=null) {
			logger.info("Utilizadno forma estatica, Hola usuario autenticado, tu username es: ".concat(auth.getName()));
		}
		
		//1ERA FORMA
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		}else {
			logger.info("Hola ".concat(auth.getName()).concat(" no tienes acceso!"));

		}
		
		//2DA FORMA
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,"");
		
		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("FORMA usando SecurityContextHolderAwareRequestWrapper Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		}else {
			logger.info("FORMA usando SecurityContextHolderAwareRequestWrapper Hola ".concat(auth.getName()).concat(" no tienes acceso!"));

		}
		
		//3RA FORMA NATIVA
		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma con HttpServletRequest Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		}else {
			logger.info("Forma con HttpServletRequest Hola ".concat(auth.getName()).concat(" no tienes acceso!"));

		}
		
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar",clientes);

		
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null,locale));
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);

		return "listar";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form", method=RequestMethod.GET)
	public String crear(Map<String,Object> model) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		
		return "form";
	}
	
	//@Secured("ROLE_ADMIN")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/form/{id}")
	public String editar(@PathVariable (value="id") Long id, Map<String,Object> model,RedirectAttributes flash) {
		Cliente cliente = null;
		if(id>0) {
			cliente = clienteService.findOne(id);
			if(cliente==null) {
				flash.addFlashAttribute("error","El cliente no existe en la BD!");
				return "redirect:/listar";
			}
		}else {
			flash.addFlashAttribute("error","El ID del cliente no puede ser cero!");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar cliente");
		
		return "form";
		
	}
	
	
	///objeto y bindingresulta siempres van juntos
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form",method=RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,@RequestParam("file") MultipartFile foto,RedirectAttributes flash, SessionStatus status) {
		System.out.println("*******cliente: " + cliente.getNombre());
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		if(!foto.isEmpty()) {
			if(cliente.getId() != null
					&& cliente.getId() > 0
					&& cliente.getFoto()!=null
					&& cliente.getFoto().length()>0) {
				uploadFileService.delete(cliente.getFoto());
			}
			//Guardar en el mismo proyecto
			//Path directorioRecursos = Paths.get("src//main//resources//static//uploads");
			//String rootPath = directorioRecursos.toFile().getAbsolutePath();
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flash.addFlashAttribute("info","Has subido correctamente'"+uniqueFilename+"'");
			cliente.setFoto(uniqueFilename);
			
		}
		
		String mensajeFlash = (cliente.getId() != null)? "Cliente editado con éxito":"Cliente creado con éxito";
		clienteService.save(cliente);
		//permite eliminar el objeto cliente y termina el proceso
		status.setComplete();
		flash.addFlashAttribute("success",mensajeFlash);
		return "redirect:listar";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,RedirectAttributes flash) {
		if(id>0) {
			Cliente cliente = clienteService.findOne(id);
			
			clienteService.delete(id);
			flash.addFlashAttribute("success","Cliente eliminado con éxito!");
			
			if(uploadFileService.delete(cliente.getFoto())) {
				flash.addAttribute("info", "Foto "+cliente.getFoto() + " eliminada con éxito");
			}
		}
		return "redirect:/listar";
	}
	
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if(context==null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		if(auth==null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		return authorities.contains(new SimpleGrantedAuthority(role));
		/*
		for(GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				logger.info("Hola ".concat(auth.getName()).concat(" tu role es!".concat(authority.getAuthority())));

				return true;
			}
		}
	
		return false;	*/
	}
	
}
