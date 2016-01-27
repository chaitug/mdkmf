package in.vasista.hr.attendance;

public class Attendance {
	String employeeId;
	String inTime;
	String outTime;
	String duration;
	public Attendance(String employeeId, String inTime, String outTime,
			String duration) {
		super();
		this.employeeId = employeeId;
		this.inTime = inTime;
		this.outTime = outTime;
		this.duration = duration;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public String getInTime() {
		return inTime;
	}
	public String getOutTime() {
		return outTime;
	}
	public String getDuration() {
		return duration;
	}
	
}
