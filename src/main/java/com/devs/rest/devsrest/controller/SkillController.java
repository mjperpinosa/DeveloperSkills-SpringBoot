package com.devs.rest.devsrest.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.boot.web.server.WebServerException;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RestController;

import com.devs.rest.devsrest.domain.Skill;
import com.devs.rest.devsrest.service.SkillService;

import springfox.documentation.swagger2.annotations.EnableSwagger2;


//@Path("/skills")
@RestController
@EnableDiscoveryClient
@EnableSwagger2
public class SkillController {
	
	private SkillService skillService;
	
	public SkillController() {
		skillService = new SkillService();
	}
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(method=RequestMethod.GET, produces="application/json", value="/skills")
	public @ResponseBody List<Skill> getSkills() {
		try {
			return skillService.getSkills();
		} catch (Exception e) {
			throw new WebServerException(null, e);
		} 
	}
	
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Path("/add")
	@RequestMapping(method=RequestMethod.POST, consumes="application/json", value="/skills/add")
	public @ResponseBody String addDeveloper(Skill skill) {

		try {
			String message = skillService.addSkill(skill);
			return message;
		} catch (Exception e) {
			throw new WebServerException(null, e);
		}

	}
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/levels")
	@RequestMapping(method=RequestMethod.GET, produces="application/json", value="/skills/levels")
	public @ResponseBody List<String> getSkillLevels() {
		try {
			return skillService.getSkillLevels();
		} catch (Exception e) {
			throw new WebServerException(null, e);
		} 
	}
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/capabilityReport")
	@RequestMapping(method=RequestMethod.GET, produces="application/json", value="/skills/capabilityReport")
	public @ResponseBody List<HashMap<String, Object>> generateSkillCapabilityReport() {
		try {
			return skillService.generateSkillCapabilityReport();
		} catch (Exception e) {
			throw new WebServerException(null, e);
		} 
	}

}
