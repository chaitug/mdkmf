package in.vasista.vsales.employee;

import java.util.Date;

public class Employee {
	String id;
	String name;
	String dept;
	String position;		
	String phoneNum;
	Date joinDate;

	
	public Employee(String id, String name, String dept, String position, String phoneNum,
			Date joinDate) {
		super();
		this.id = id;
		this.name = name;
		this.dept = dept;
		this.position = position;
		this.phoneNum = phoneNum;
		this.joinDate = joinDate;
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
	
	@Override
	public String toString() {
		return id;
	}	
}
