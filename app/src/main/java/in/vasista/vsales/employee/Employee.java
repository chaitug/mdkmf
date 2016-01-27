package in.vasista.vsales.employee;

import java.util.Date;

public class Employee {
	String id;
	String name;
	String dept;
	String position;		
	String phoneNum;
	Date joinDate;
	String weeklyOff;

	Date leaveBalanceDate;
	float earnedLeave;
	float casualLeave;
	float halfPayLeave;
	
	public Employee(String id, String name, String dept, String position, String phoneNum,
			Date joinDate, String weeklyOff) {
		super();
		this.id = id;
		this.name = name;
		this.dept = dept;
		this.position = position;
		this.phoneNum = phoneNum;
		this.joinDate = joinDate;
		this.weeklyOff = weeklyOff;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDept() {
		return dept;
	}
	public String getPosition() {
		return position;
	}	
	public String getPhoneNum() {
		return phoneNum;
	}
	public Date getJoinDate() {
		return joinDate;
	}	
	public String getWeeklyOff() {
		return weeklyOff;
	}	
	public Date getLeaveBalanceDate() {
		return leaveBalanceDate;
	}

	public void setLeaveBalanceDate(Date leaveBalanceDate) {
		this.leaveBalanceDate = leaveBalanceDate;
	}

	public float getEarnedLeave() {
		return earnedLeave;
	}

	public void setEarnedLeave(float earnedLeave) {
		this.earnedLeave = earnedLeave;
	}

	public float getCasualLeave() {
		return casualLeave;
	}

	public void setCasualLeave(float casualLeave) {
		this.casualLeave = casualLeave;
	}

	public float getHalfPayLeave() {
		return halfPayLeave;
	}

	public void setHalfPayLeave(float halfPayLeave) {
		this.halfPayLeave = halfPayLeave;
	}

	@Override
	public String toString() {
		return id;
	}	
}
