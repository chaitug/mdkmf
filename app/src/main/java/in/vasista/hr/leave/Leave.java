package in.vasista.hr.leave;

import java.util.Date;

public class Leave {
	String employeeId;	
	String leaveTypeId;
	String leaveStatus;	
	Date fromDate;
	Date thruDate;
	
	public Leave(String employeeId, String leaveTypeId, String leaveStatus,
			Date fromDate, Date thruDate) {
		this.employeeId = employeeId;
		this.leaveTypeId = leaveTypeId;
		this.leaveStatus = leaveStatus;
		this.fromDate = fromDate;
		this.thruDate = thruDate;  
	}
	
	public String getEmployeeId() {
		return employeeId;
	}

	public String getLeaveTypeId() {
		return leaveTypeId;
	}

	public String getLeaveStatus() {
		return leaveStatus;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getThruDate() {
		return thruDate;
	}	
}
