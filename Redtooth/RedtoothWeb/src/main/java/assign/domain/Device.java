package assign.domain;

public class Device implements Comparable<Device> {
	private String deviceID;
	private int priority;
	private Long timestamp;
	private boolean status;
	
	public Device(String deviceID, long timestamp, int priority, boolean status) {
		this.deviceID = deviceID;
		this.priority = priority;
		this.timestamp = timestamp;
		this.status = status;
	}
	
	public String getID() {
		return this.deviceID;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public Long getTimestamp() {
		return this.timestamp;
	}
	
	public boolean getStatus() {
		return this.status;
	}

	@Override
	public int compareTo(Device o) {
		return this.priority - o.getPriority();
	}
	
}