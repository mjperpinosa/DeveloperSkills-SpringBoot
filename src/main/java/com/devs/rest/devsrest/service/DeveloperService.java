package com.devs.rest.devsrest.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.devs.rest.devsrest.dao.DeveloperDao;
import com.devs.rest.devsrest.dao.SkillDao;
import com.devs.rest.devsrest.domain.Developer;
import com.devs.rest.devsrest.domain.SkillAssessment;

public class DeveloperService {
	DeveloperDao devDao;
	SkillDao skillDao;
	
	public DeveloperService() {
		devDao = new DeveloperDao();
		skillDao = new SkillDao();
	}
	
	public HashMap<String, Object> addDeveloper(Developer dev) {
		StringBuffer message = new StringBuffer();
		
		// generate developer Id and check if it exists already
		int newDevId = getNewDevId();
		boolean exist = devDao.doesDeveloperIdAlreadyExist(newDevId);
		while(exist) {
			newDevId = this.getNewDevId();
			exist = devDao.doesDeveloperIdAlreadyExist(newDevId);
		}
		dev.setDeveloperId(newDevId);
		
		if(dev.getFirstName().trim().length() <= 0) {
			message.append("First name cannot be blank.\n");
		} else if(dev.getFirstName().length() < 2 
				|| dev.getFirstName().length() > 255) {
			message.append("First name can only be 2 - 255 characters.");
		}
		
		if(dev.getLastName().trim().length() <= 0) {
			message.append("Last name cannot be blank.\n");
		}  else if(dev.getLastName().length() < 2 
				|| dev.getLastName().length() > 255) {
			message.append("Last name can only be 2 - 255 characters.");
		} 
		
		if(dev.getPosition().trim().length() <= 0) {
			message.append("Position cannot be blank.\n");
		}  else if(dev.getPosition().length() < 2 
				|| dev.getPosition().length() > 255) {
			message.append("Position can only be 2 - 255 characters. ");
		}
		
		if(dev.getBirthDate() == null || !isThisDateValid(dev.getBirthDate().toString(), "yyyy-mm-dd")) {
			message.append("Please provide a valid birth date.\n");
		}
		int status = 406;
		if(message.length() <= 0) {
			// meaning everything valid
			Boolean added = devDao.addDeveloper(dev);
			if(added) {
				message.append("New developer has been successfully added.");
				status = 200;
			} else {
				message.append("Something went wrong. Couldn't add developer.");
			}
		}
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("code", status);
		response.put("message", message.toString());
		
		return response;
	}
	
	public String addSkillAssessment(SkillAssessment skillAss) {
		StringBuffer message = new StringBuffer();
		
		if(!devDao.isDeveloperIdExisting(skillAss.getDeveloper().getDeveloperId())) {
			message.append("Please provide a valid developer.\n");
		} 
		if(!skillDao.isSkillIdExisting(skillAss.getSkill().getSkillId())) {
			message.append("Please provide a valid skill.\n");
		}
		if(String.valueOf(skillAss.getSkillLevel()).isEmpty()) {
			message.append("Please provide a valid skill level.\n ");
		}
		if(String.valueOf(skillAss.getMonthsOfExperience()).equals("0") || skillAss.getMonthsOfExperience() < 0) {
			message.append("Please provide a valid months of experience.\n");
		}
		
		if(devDao.skillAlreadyExistToDeveloper(skillAss.getDeveloper().getDeveloperId(), skillAss.getSkill().getSkillId())) {
			message.append("The developer already has the chosen skill.\n");
		}
		
		if(message.length() <= 0) {
			Boolean added = devDao.addSkillAssessment(skillAss);
			if(added) message.append("New skill assessment has been successfuly added.");
			else message.append("Something went wrong. Couldn't add new skill assessment.");
		}
		
		return message.toString();
	}
	
	public String updateSkillAssessment(SkillAssessment skillAss) {
		StringBuffer message = new StringBuffer();
		
		if(!devDao.isDeveloperIdExisting(skillAss.getDeveloper().getDeveloperId())) {
			message.append("Unrecognised developer ID.\n");
		}
		if(!skillDao.isSkillIdExisting(skillAss.getSkill().getSkillId())) {
			message.append("Unrecognised skill ID.\n");
		}
		
		if(message.length() <= 0) {
			Boolean updated = devDao.updateSkillAssessment(skillAss);
			if(updated) message.append("Skill assessment has been successfuly updated.");
			else message.append("Something went wrong. Couldn't update skill assessment.");
		}
		
		return message.toString();
	}
	
	public List<HashMap<String, Object>> getDevelopers() {
		return devDao.getDevelopers();
	}
	
	public List<HashMap<String, Object>> searchDevelopers(
			String skill, 
			String skillLevel, 
			String firstName,
			String lastName,
			String monthsOfExperience) {
		
		skill = createSearchValue(skill);
		skillLevel = createSearchValue(skillLevel);
		firstName = createSearchValue(firstName);
		lastName = createSearchValue(lastName);
		
		List<HashMap<String, Object>> results;
		
		System.out.println("VALLLLLLLLL = " + StringUtils.isBlank(String.valueOf(monthsOfExperience)));
		if(StringUtils.isBlank(monthsOfExperience)) {
			// call query  function that does not include months of experience
			System.out.println("without months of experience");
			results = devDao.searchDevelopers(skill, skillLevel, firstName, lastName);
		} else {
			// call query that includes all attributes above
			System.out.println("With months of experienc");
			results = devDao.searchDevelopers(skill, skillLevel, firstName, lastName, Integer.parseInt(monthsOfExperience));
		}

		return results;
	}
	
	private String createSearchValue(String string) {
		String value;
		
		if (StringUtils.isBlank(string)) {
			value = "%";
		} else {
			value = "%"+string+"%";
		}
		return value;
	}
	
	@SuppressWarnings("deprecation")
	public int getNewDevId() {
		Date date = new Date();
		String random = get4DigitRandom();
		String id = random + date.getMonth() + date.getYear() + date.getDay();
		
		return Integer.parseInt(id);
	}
	
	public String get4DigitRandom() {
		Random random = new Random();
		return String.format("%04d", random.nextInt(10000));
	}
	
	public boolean isThisDateValid(String dateToValidate, String dateFromat){
		
		if(dateToValidate == null){
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		
		try {
			Date date = sdf.parse(dateToValidate);
			System.out.println(date);
		
		} catch (ParseException e) {
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
