package assign.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import assign.domain.Project;

public class CourseStudentServiceImpl implements CourseStudentService {

	String dbURL = "";
	String dbUsername = "";
	String dbPassword = "";
	DataSource ds;

	// DB connection information would typically be read from a config file.
	public CourseStudentServiceImpl(String dbUrl, String username, String password) {
		this.dbURL = dbUrl;
		this.dbUsername = username;
		this.dbPassword = password;

		ds = setupDataSource();
	}

	public DataSource setupDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUsername(this.dbUsername);
		ds.setPassword(this.dbPassword);
		ds.setUrl(this.dbURL);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		return ds;
	}

	public Project addProject(Project project) throws Exception {
		if (checkEmpty(project.getName()) || checkEmpty(project.getDescription())) {
			throw new IllegalArgumentException("Creating project failed. Name and Description cannot be empty");
		}
		Connection conn = ds.getConnection();
		System.out.println("Adding Project to Database");
		String insert = "INSERT INTO projects(name, description) VALUES(?, ?)";
		PreparedStatement stmt = conn.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, project.getName());
		stmt.setString(2, project.getDescription());
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException("Creating project failed, no rows affected.");
		}
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if (!generatedKeys.next()) {
			throw new SQLException("Creating course failed, no ID obtained.");        
		}     
		project.setProjectID(generatedKeys.getInt(1));
		conn.close();
		return project;
	}
	
	public void testDatabase(String deviceID) throws Exception {
		Connection conn = ds.getConnection();
		String insert = "insert into devices (deviceID, updateTimestamp, priority, playing) values (?, ?, ?, ?);";
		PreparedStatement stmt = conn.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, deviceID);
		stmt.setLong(2, 0);
		stmt.setInt(3, 1);
		stmt.setBoolean(4, false);
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException("Test failed, no rows affected.");
		}
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if (!generatedKeys.next()) {
			//throw new SQLException("Test failed, no ID obtained.");        
		}     
		conn.close();
	}

	public void deleteProject(int project_ID) throws Exception {
		Connection conn = ds.getConnection();
		System.out.println("Deleting Project from Database");
		String delete = "DELETE FROM projects WHERE project_ID="+project_ID;
		PreparedStatement stmt = conn.prepareStatement(delete,
				Statement.RETURN_GENERATED_KEYS);
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException("Deleting project failed, no rows affected.");
		}
		conn.close();
	}

	public Project updateProject(int project_ID, Project project) throws Exception {
		if (checkEmpty(project.getName()) || checkEmpty(project.getDescription())) {
			throw new IllegalArgumentException("Updating project failed. Name and Description cannot be empty");
		}
		Connection conn = ds.getConnection();
		System.out.println("Updating Project to Database");
		String insert = "UPDATE projects SET name = ?, description = ? WHERE project_ID = ?";
		PreparedStatement stmt = conn.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, project.getName());
		stmt.setString(2, project.getDescription());
		stmt.setString(3,  project_ID + "");
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException("Project Not Found");
		}
		conn.close();
		return project;
	}

	public Project getProject(int project_ID) throws Exception {
		String query = "select * from projects where project_id=?";
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		s.setString(1, String.valueOf(project_ID));
		ResultSet r = s.executeQuery();
		if (!r.next()) {
			throw new SQLException("Project Not Found");
		}
		Project project = new Project();
		project.setDescription(r.getString("description"));
		project.setName(r.getString("name"));
		project.setProjectID(r.getInt("project_id"));
		return project;
	}

	public boolean checkEmpty(String string) {
		if (string == null)
			return true;
		for (int index = 0; index < string.length(); index++) {
			if (string.charAt(index) != ' ') {
				return false;
			}
		}
		return true;
	}
}
