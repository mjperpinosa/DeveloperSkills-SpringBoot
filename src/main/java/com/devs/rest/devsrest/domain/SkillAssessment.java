package com.devs.rest.devsrest.domain;

public class SkillAssessment {
	
	private Developer developer;
	private Skill skill;
	
	private int monthsOfExperience;
	private String skillLevel;
	
	public Developer getDeveloper() {
		return developer;
	}
	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	
	public int getMonthsOfExperience() {
		return monthsOfExperience;
	}
	public void setMonthsOfExperience(int monthsOfExperience) {
		this.monthsOfExperience = monthsOfExperience;
	}
	public String getSkillLevel() {
		return skillLevel;
	}
	public void setSkillLevel(String skillLevel) {
		this.skillLevel = skillLevel;
	}
	
}
