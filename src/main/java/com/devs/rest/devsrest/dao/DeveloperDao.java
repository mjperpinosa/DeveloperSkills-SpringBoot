package com.devs.rest.devsrest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.devs.rest.devsrest.domain.Developer;
import com.devs.rest.devsrest.domain.SkillAssessment;

public class DeveloperDao {
	DatabaseConnection dbConnection;
	SkillDao skillDao;
	
	public DeveloperDao() {
		dbConnection = new DatabaseConnection();
		skillDao = new SkillDao();
	}
	
	public boolean addDeveloper(Developer dev) {
		boolean added = false;
		
		String sql = "INSERT INTO developers VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection connection = dbConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, dev.getDeveloperId());
			ps.setString(2, dev.getFirstName());
			ps.setString(3, dev.getMiddleName());
			ps.setString(4, dev.getLastName());
			ps.setDate(5, dev.getBirthDate());
			ps.setString(6, dev.getPosition());
			ps.executeUpdate();
			
			added = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return added;
	}
	
	public List<HashMap<String, Object>> getDevelopers() {
		List<HashMap<String, Object>> listDevs = new ArrayList<HashMap<String, Object>>();
		String sql = "SELECT * FROM developers ORDER BY firstName, middleName, lastName";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();
			Developer dev = null;
			while (rs.next()) {
				dev = new Developer();
				dev.setDeveloperId(rs.getInt("developerId"));
				dev.setFirstName(rs.getString("firstName"));
				dev.setMiddleName(rs.getString("middleName"));
				dev.setLastName(rs.getString("lastName"));
				dev.setBirthDate(rs.getDate("birthDate"));
				dev.setPosition(rs.getString("position"));
				
				System.out.println(dev.getFirstName());
				System.out.println(dev.getMiddleName());
				System.out.println(dev.getLastName());
				
//				String skillSql = "SELECT s.*, sl.level, sa.monthsOfExperience FROM skills s, skill_assessments sa, skill_levels sl "
//						+ " WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ? "
//						+ " GROUP BY sa.skillId";
				//SELECT * FROM skills s, skill_levels sl, skill_assessments sa WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = 422641195;
//				String skillSql = "SELECT * FROM "+dbConnection.getDbName()+".skills s, "+dbConnection.getDbName()+".skill_levels sl, "+dbConnection.getDbName()+".skill_assessments sa"
//						+ " WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ? "
//						+ " GROUP BY sa.skillId;";
//				String skillSql = "SELECT * FROM skills s, skill_levels sl, skill_assessments sa"
//						+ " WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ?"
//						+ " GROUP BY sa.skillId";
				String skillSql = "SELECT * FROM skills s, skill_levels sl, skill_assessments sa WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ?";
				
				try (Connection conn2 = dbConnection.getConnection(); PreparedStatement psSkills = conn.prepareStatement(skillSql)) {
					psSkills.setInt(1, rs.getInt("developerId"));
					ResultSet rsSkills = psSkills.executeQuery();
					
					
					List<HashMap<String, Object>> listSkills = new ArrayList<HashMap<String, Object>>();
					
					
					while(rsSkills.next()) {
						HashMap<String, Object> skill = new HashMap<String, Object>();
						
						skill.put("skillId", rsSkills.getInt("skillId"));
						skill.put("skill", rsSkills.getString("skill"));
						skill.put("skillLevel", rsSkills.getString("level"));
						skill.put("monthsOfExperience", rsSkills.getInt("monthsOfExperience"));
						
						listSkills.add(skill);
					}
					
					HashMap<String, Object> devData = new HashMap<String, Object>();
					devData.put("developer", dev);
					devData.put("skills", listSkills);
					listDevs.add(devData);
				} catch(SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return listDevs;
	}
	
	public boolean isDeveloperIdExisting(int developerId) {
		boolean doesExists = false;
		
		String sql = "SELECT * FROM developers WHERE developerId = ?";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, developerId);
			
			ResultSet results = ps.executeQuery();

			while (results.next()) {
				doesExists = true;
				break;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return doesExists;
	}
	
	public boolean addSkillAssessment(SkillAssessment skillAss) {
		boolean added = false;
		
		String sql = "INSERT INTO skill_assessments VALUES ((SELECT max(id) FROM skill_assessments)+1, ?, ?, ?, ?)";

		try (Connection connection = dbConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, skillAss.getDeveloper().getDeveloperId());
			ps.setInt(2, skillAss.getSkill().getSkillId());
			ps.setInt(3, skillAss.getMonthsOfExperience());
			ps.setInt(4, skillDao.getSkillLevelId(skillAss.getSkillLevel()));
			ps.executeUpdate();
			
			added = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return added;
	}
	
	public boolean skillAlreadyExistToDeveloper(int developerId, int skillId) {
		boolean exist = false;
		
		String sql = "SELECT * FROM skill_assessments WHERE developerId = ? AND skillId = ?";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, developerId);
			ps.setInt(2, skillId);
			
			ResultSet results = ps.executeQuery();

			while (results.next()) {
				exist = true;
				break;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		return exist;
	}
	
	public boolean doesDeveloperIdAlreadyExist(int developerId) {
		boolean exist = false;
		
		String sql = "SELECT * FROM developers WHERE developerId = ?";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, developerId);
			
			ResultSet results = ps.executeQuery();

			while (results.next()) {
				exist = true;
				break;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		return exist;
	}
	
	public boolean updateSkillAssessment(SkillAssessment skillAss) {
		boolean updated = false;
		
		String sql = "UPDATE skill_assessments SET monthsOfExperience = ?, skillLevelId = ? WHERE skillId = ? AND developerId = ?";

		try (Connection connection = dbConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, skillAss.getMonthsOfExperience());
			ps.setInt(2, skillDao.getSkillLevelId(skillAss.getSkillLevel()));
			ps.setInt(3, skillAss.getSkill().getSkillId());
			ps.setInt(4, skillAss.getDeveloper().getDeveloperId());
			ps.executeUpdate();
			
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return updated;
	}
	
	public List<HashMap<String, Object>> searchDevelopers(String skill, String skillLevel, String firstName, String lastName) {
		List<HashMap<String, Object>> listDevs = new ArrayList<HashMap<String, Object>>();
		System.out.println("skill = " + skill + " skillLevel = " + skillLevel + " firstName = " + firstName + " lastName = " + lastName);
//		String sql = "SELECT d.*, s.*, sa.*, sl.* FROM developers d, skills s, skill_assessments sa, skill_levels sl "
//				+ " WHERE s.skill LIKE ? AND s.skillId = sa.skillId AND sl.level LIKE ? AND sl.skillLevelId = sa.skillLevelId "
//				+ " AND d.firstName LIKE ? AND d.lastName LIKE ? AND d.developerId = sa.developerId "
//				+ " GROUP BY d.developerId";
		String sql = "SELECT * FROM developers d, skills s, skill_assessments sa, skill_levels sl WHERE s.skill LIKE ? AND s.skillId = sa.skillId AND sl.level LIKE ? AND sl.skillLevelId = sa.skillLevelId AND d.firstName LIKE ? AND d.lastName LIKE ? AND d.developerId = sa.developerId GROUP BY d.developerId";
				
		System.out.println("SQL = " + sql);

		
		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, skill);
			ps.setString(2, skillLevel);
			ps.setString(3, firstName);
			ps.setString(4, lastName);

			ResultSet rs = ps.executeQuery();
			Developer dev = null;
			while (rs.next()) {
				dev = new Developer();
				dev.setDeveloperId(rs.getInt("developerId"));
				dev.setFirstName(rs.getString("firstName"));
				dev.setMiddleName(rs.getString("middleName"));
				dev.setLastName(rs.getString("lastName"));
				dev.setBirthDate(rs.getDate("birthDate"));
				dev.setPosition(rs.getString("position"));
				
//				String skillSql = "SELECT s.*, sl.level, sa.monthsOfExperience FROM skills s, skill_assessments sa, skill_levels sl "
//						+ " WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ? "
//						+ " GROUP BY sa.skillId";
				
				String skillSql = "SELECT * FROM skills s, skill_levels sl, skill_assessments sa WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ?";
				
				
				try (Connection conn2 = dbConnection.getConnection(); PreparedStatement psSkills = conn2.prepareStatement(skillSql)) {
					psSkills.setInt(1, rs.getInt("developerId"));
					ResultSet rsSkills = psSkills.executeQuery();
					
					
					List<HashMap<String, Object>> listSkills = new ArrayList<HashMap<String, Object>>();
					
					
					while(rsSkills.next()) {
						HashMap<String, Object> skillMap = new HashMap<String, Object>();
						
						skillMap.put("skillId", rsSkills.getInt("skillId"));
						skillMap.put("skill", rsSkills.getString("skill"));
						skillMap.put("skillLevel", rsSkills.getString("level"));
						skillMap.put("monthsOfExperience", rsSkills.getInt("monthsOfExperience"));
						
						listSkills.add(skillMap);
					}
					
					HashMap<String, Object> devData = new HashMap<String, Object>();
					devData.put("developer", dev);
					devData.put("skills", listSkills);
					listDevs.add(devData);
				
				} catch(SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return listDevs;
	}
	
	public List<HashMap<String, Object>> searchDevelopers(String skill, String skillLevel, String firstName, String lastName, int monthsOfExperience) {
		List<HashMap<String, Object>> listDevs = new ArrayList<HashMap<String, Object>>();

//		String sql = "SELECT d.*, s.*, sa.*, sl.* FROM developers d, skills s, skill_assessments sa, skill_levels sl "
//				+ " WHERE s.skill LIKE ? AND s.skillId = sa.skillId AND sl.level LIKE ? AND sl.skillLevelId = sa.skillLevelId "
//				+ " AND d.firstName LIKE ? AND d.lastName LIKE ? AND d.developerId = sa.developerId AND sa.monthsOfExperience = ? "
//				+ " GROUP BY d.developerId";
		
		String sql = "SELECT * FROM developers d, skills s, skill_assessments sa, skill_levels sl "
				+ " WHERE s.skill LIKE ? AND s.skillId = sa.skillId AND sl.level LIKE ? AND sl.skillLevelId = sa.skillLevelId "
				+ " AND d.firstName LIKE ? AND d.lastName LIKE ? AND d.developerId = sa.developerId AND sa.monthsOfExperience = ? ";
//				+ " GROUP BY d.developerId";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, skill);
			ps.setString(2, skillLevel);
			ps.setString(3, firstName);
			ps.setString(4, lastName);
			ps.setInt(5, monthsOfExperience);

			ResultSet rs = ps.executeQuery();
			Developer dev = null;
			while (rs.next()) {
				dev = new Developer();
				dev.setDeveloperId(rs.getInt("developerId"));
				dev.setFirstName(rs.getString("firstName"));
				dev.setMiddleName(rs.getString("middleName"));
				dev.setLastName(rs.getString("lastName"));
				dev.setBirthDate(rs.getDate("birthDate"));
				dev.setPosition(rs.getString("position"));
				
//				String skillSql = "SELECT s.*, sl.level, sa.monthsOfExperience FROM skills s, skill_assessments sa, skill_levels sl "
//						+ " WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ? "
//						+ " x ";
				String skillSql = "SELECT * FROM skills s, skill_levels sl, skill_assessments sa WHERE s.skillId = sa.skillId AND sl.skillLevelId = sa.skillLevelId AND sa.developerId = ?";
				
				
				try (Connection conn2 = dbConnection.getConnection(); PreparedStatement psSkills = conn2.prepareStatement(skillSql);) {
					psSkills.setInt(1, rs.getInt("developerId"));
					ResultSet rsSkills = psSkills.executeQuery();
					
					
					List<HashMap<String, Object>> listSkills = new ArrayList<HashMap<String, Object>>();
					
					
					while(rsSkills.next()) {
						HashMap<String, Object> skillMap = new HashMap<String, Object>();
						
						skillMap.put("skillId", rsSkills.getInt("skillId"));
						skillMap.put("skill", rsSkills.getString("skill"));
						skillMap.put("skillLevel", rsSkills.getString("level"));
						skillMap.put("monthsOfExperience", rsSkills.getInt("monthsOfExperience"));
						
						listSkills.add(skillMap);
					}
					
					HashMap<String, Object> devData = new HashMap<String, Object>();
					devData.put("developer", dev);
					devData.put("skills", listSkills);
					listDevs.add(devData);
				} catch(SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return listDevs;
	}
}
