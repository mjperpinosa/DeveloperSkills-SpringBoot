package com.devs.rest.devsrest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.devs.rest.devsrest.domain.Skill;

public class SkillDao {
	DatabaseConnection dbConnection;
	
	public SkillDao() {
		dbConnection = new DatabaseConnection();
	}
	
	public boolean addSkill(Skill skill) {
		boolean added = false;
		
		String sql = "INSERT INTO skills VALUES ((SELECT max(skillId) FROM skills)+1, ?)";

		try (Connection connection = dbConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, skill.getSkill());
			ps.executeUpdate();
			added = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return added;
	}
	
	public List<Skill> getSkills() {
		List<Skill> skills;
		
		String sql = "SELECT * FROM skills ORDER BY skill";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();
			skills = new ArrayList<Skill>();
			Skill skill = null;
			while (rs.next()) {
				skill = new Skill();
				skill.setSkillId(rs.getInt("skillId"));
				skill.setSkill(rs.getString("skill"));
				skills.add(skill);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return skills;
	}
	
	public List<HashMap<String, Object>> generateSkillCapabilityReport() {
		List<HashMap<String, Object>> listSkillData = null;
		HashMap<String, Object> skillData = null;
		
		String sqlSkills = "SELECT * FROM skills ORDER BY skill";
		String sqlLevels = "SELECT * FROM skill_levels ORDER BY skillLevelId";
		String sqlCount = "SELECT COUNT(1) AS levelCount FROM skill_assessments WHERE skillId = ? AND skillLevelId = ?";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlLevels)) {
			ResultSet rs = ps.executeQuery();
			List<String> levels = new ArrayList<String>();
			while(rs.next()) {
				levels.add(rs.getString("level"));
			}
			
			PreparedStatement psSkills = conn.prepareStatement(sqlSkills);
			ResultSet rsSkills = psSkills.executeQuery();
			listSkillData = new ArrayList<HashMap<String, Object>>();
			while(rsSkills.next()) {
				int totalForEachSkill = 0;
				skillData = new HashMap<String, Object>();
				List<HashMap<String, Object>> listLevelCounts = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> levelCounts;
				for(int i = 0; i < levels.size(); i++) {
					
					PreparedStatement psCount = conn.prepareStatement(sqlCount);
					psCount.setInt(1, rsSkills.getInt("skillId"));
					psCount.setInt(2, getSkillLevelId(levels.get(i)));
					
					ResultSet rsCount = psCount.executeQuery();
					levelCounts = new HashMap<String, Object>();
					while(rsCount.next()) {
						int count = rsCount.getInt("levelCount");
						totalForEachSkill += count;
						levelCounts.put("level", levels.get(i));
						levelCounts.put("count", count);
						listLevelCounts.add(levelCounts);
					}
				}	
				skillData.put("skill", rsSkills.getString("skill"));
				skillData.put("report", listLevelCounts);
				skillData.put("total", totalForEachSkill);
				listSkillData.add(skillData);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		return listSkillData;
	}
	
	public int getSkillLevelId(String skillLevel) {
		int skillLevelId = -1;
		String sql = "SELECT skillLevelId FROM skill_levels WHERE level = ?";
		
		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, skillLevel);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				skillLevelId = rs.getInt("skillLevelId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return skillLevelId;
		
	}
	
	
	public List<String> getSkillLevels() {
		List<String> skillLevels;
		
		String sql = "SELECT * FROM skill_levels ORDER BY skillLevelId";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();
			skillLevels = new ArrayList<String>();
			while (rs.next()) {
				skillLevels.add(rs.getString("level"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return skillLevels;
	}
	
	public boolean skillExists(String skill) {
		boolean exist = false;
		
		String sql = "SELECT * FROM skills WHERE skill = ?";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, skill);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				exist = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return exist;
	}
	
	public boolean isSkillIdExisting(int skillId) {
		boolean doesExists = false;
		
		String sql = "SELECT * FROM skills WHERE skillId = ?";

		try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, skillId);
			
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
}
