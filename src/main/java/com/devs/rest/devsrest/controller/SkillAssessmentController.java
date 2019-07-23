package com.devs.rest.devsrest.controller;

import org.springframework.boot.web.server.WebServerException;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.devs.rest.devsrest.domain.SkillAssessment;
import com.devs.rest.devsrest.service.DeveloperService;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@Path("/skillAssessments")
@RestController
@EnableDiscoveryClient
@EnableSwagger2
public class SkillAssessmentController {
	
	private DeveloperService devService;
	
	public SkillAssessmentController() {
		devService = new DeveloperService();
	}
	
//	@GET
	@RequestMapping(method=RequestMethod.GET, value="/skillAssessments")
	public @ResponseBody String getSkillAssessments() {
		return "skill assessssss";
	}
	
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Path("/add")
	@RequestMapping(method=RequestMethod.POST, consumes="application/json", value="/skillAssessments/add")
	public @ResponseBody String addSkillAssessment(SkillAssessment skillAss) {

		try {
			String message = devService.addSkillAssessment(skillAss);
			return message;
		} catch (Exception e) {
			throw new WebServerException(null, e);
		}

	}
	
//	@PUT
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Path("/update")
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json", value="/skillAssessments/update")
	public @ResponseBody String updateSkillAssessment(SkillAssessment skillAss) {

		try {
			String message = devService.updateSkillAssessment(skillAss);
			return message;
		} catch (Exception e) {
			throw new WebServerException(null, e);
		}

	}

}
