package assign.services;

import java.sql.SQLException;

public interface DeviceDBService {
    
    public void testDatabase(String deviceID) throws Exception;
    
    public boolean hasDevice(String deviceID) throws Exception;
    
    public void addDevice(String deviceID, int priority, boolean status) throws Exception;
    
    public void updateDevice(String deviceID, int priority, boolean status) throws Exception;
    
    public String makeDecision() throws SQLException;

    
}
