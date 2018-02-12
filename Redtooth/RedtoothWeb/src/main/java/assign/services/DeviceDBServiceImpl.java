package assign.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import assign.domain.Device;

public class DeviceDBServiceImpl implements DeviceDBService {

	String dbURL = "";
	String dbUsername = "";
	String dbPassword = "";
	DataSource ds;

	// DB connection information would typically be read from a config file.
	public DeviceDBServiceImpl(String dbUrl, String username, String password) {
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
			conn.close();
			throw new SQLException("Test failed, no rows affected.");
		}
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if (!generatedKeys.next()) {
			//throw new SQLException("Test failed, no ID obtained.");        
		}     
		conn.close();
	}

	public boolean hasDevice(String deviceID) throws Exception {
		String query = "select * from devices where deviceID=?";
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		s.setString(1, String.valueOf(deviceID));
		ResultSet r = s.executeQuery();
		if (!r.next()) {
			conn.close();
			return false;
		}
		conn.close();
		return true;
	}

	public void clearAll() throws Exception {
		Connection conn = ds.getConnection();
		String delete = "DELETE FROM devices";
		PreparedStatement stmt = conn.prepareStatement(delete,
				Statement.RETURN_GENERATED_KEYS);
		stmt.executeUpdate();
		conn.close();
	}

	public void addDevice(String deviceID, int priority, boolean status) throws Exception {
		if (checkEmpty(deviceID)) {
			throw new IllegalArgumentException("deviceID Cannot be empty");
		}
		Connection conn = ds.getConnection();
		String insert = "INSERT INTO devices(deviceID, updateTimestamp, priority, playing) VALUES(?, ?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, deviceID);
		Date time = new Date();
		long timestamp = time.getTime(); 
		stmt.setLong(2, timestamp);
		stmt.setInt(3, priority);
		stmt.setBoolean(4, status);
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			conn.close();
			throw new SQLException("Creating project failed, no rows affected.");
		}
		conn.close();
	}

	public void updateDevice(String deviceID, int priority, boolean status) throws Exception {
		Connection conn = ds.getConnection();

		String update = "UPDATE devices SET updateTimestamp = ?, priority = ?, playing = ? WHERE deviceID = ?";
		PreparedStatement stmt = conn.prepareStatement(update,
				Statement.RETURN_GENERATED_KEYS);
		Date time = new Date();
		stmt.setLong(1, time.getTime());
		stmt.setInt(2, priority);
		stmt.setBoolean(3,  status);
		stmt.setString(4, deviceID);
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			conn.close();
			throw new SQLException("Project Not Found");
		}
		conn.close();
	}

	public String makeDecision() throws SQLException {
		String query = "select * from devices where playing=?";
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		s.setBoolean(1, true);
		ResultSet r = s.executeQuery();
		ArrayList<Device> devices = new ArrayList<Device>();

		while (r.next()) {
			devices.add(new Device(r.getString(1), r.getLong(2), r.getInt(3), r.getBoolean(4)));
		}
		Collections.sort(devices);
		if (devices.size() == 0) { //TODO: Fix this so that an empty DB won't throw exception
			devices = analytics();
			Collections.sort(devices);
			conn.close();
			return devices.get(devices.size()-1).getID();
		}
		conn.close();
		return devices.get(0).getID();
	}

	public Device getDevice(String deviceID) throws SQLException {
		String query = "select * from devices where deviceID=?";
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		s.setString(1, deviceID);
		ResultSet r = s.executeQuery();		
		if (!r.next()) {
			conn.close();
			throw new SQLException();
		}
		Device d = new Device(r.getString(1), r.getLong(2), r.getInt(3), r.getBoolean(4));
		conn.close();
		return d;
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

	public ArrayList<Device> analytics() throws SQLException {
		String query = "select * from devices where priority=?";
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		s.setInt(1, 0);
		ResultSet r = s.executeQuery();		
		ArrayList<Device> devices = new ArrayList<Device>();
		while (r != null && r.next()) {
			devices.add(new Device(r.getString(1), r.getLong(2), r.getInt(3), r.getBoolean(4)));
		}
		conn.close();
		return devices;
	}
}
