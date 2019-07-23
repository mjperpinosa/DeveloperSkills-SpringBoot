package com.devs.rest.devsrest.controller;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.boot.web.server.WebServerException;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.devs.rest.devsrest.domain.Developer;
import com.devs.rest.devsrest.exceptions.NoListFoundException;
import com.devs.rest.devsrest.service.DeveloperService;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableDiscoveryClient
@EnableSwagger2
//@Path("/developers")
public class DeveloperController {
	private DeveloperService devService;
	
	public DeveloperController() {
		devService = new DeveloperService();
	}
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(method=RequestMethod.GET, produces="application/json", value="/developers")
	public @ResponseBody Response getDevelopers() {
		List<HashMap<String, Object>> listDevs;
		try {
			listDevs = devService.getDevelopers();
			if(listDevs.isEmpty()) {
				throw new NoListFoundException("No developers found.");
			}
		} catch(NoListFoundException e) {
			e.printStackTrace();
			return Response.status(204).entity("Empty list of developers").build();
		}
		return Response.status(200).entity(listDevs).build();
	}
	
//	@GET
//	@Path("/search")
//	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(method=RequestMethod.GET, produces="application/json", value="/developers/search")
	public @ResponseBody Response searchDevelopers(
			@RequestParam("skill") String skill, 
			@RequestParam("skillLevel") String skillLevel,
			@RequestParam("firstName") String firstName, 
			@RequestParam("lastName") String lastName,
			@RequestParam("monthsOfExperience") String monthsOfExperience 
		) {
		
		List<HashMap<String, Object>> listDevs;
		try {
			listDevs = devService.searchDevelopers(skill, skillLevel, firstName, lastName, monthsOfExperience);
			if(listDevs.isEmpty()) {
				throw new NoListFoundException("No developers found.");
			}
		} catch(NoListFoundException e) {
			e.printStackTrace();
			return Response.status(204).entity("No developers found.").build();
		}
		return Response.status(200).entity(listDevs).build();
	}
	
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Path("/add")
	@RequestMapping(method=RequestMethod.POST, consumes="application/json", value="/developers/add")
	public @ResponseBody Response addDeveloper(Developer dev) {
		HashMap<String, Object> response;
		try {
			response = devService.addDeveloper(dev);	
			
		} catch (Exception e) {
			throw new WebServerException(null, e);
		}
		System.out.println("Status = " + response.get("code"));
		return Response.status((int) response.get("code")).entity(response.get("message")).build();
	}
}
