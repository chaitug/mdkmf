package in.vasista.hr.payslip;

import java.util.Date;

public class Payslip {
	String employeeId;	
	String payrollHeaderId;
	Date payrollDate;
	String payrollPeriod;
	float netAmount;
	
	public Payslip(String employeeId, String payrollHeaderId, Date payrollDate,
			String payrollPeriod, float netAmount) {
		this.employeeId = employeeId;
		this.payrollHeaderId = payrollHeaderId;
		this.payrollDate = payrollDate;
		this.payrollPeriod = payrollPeriod;
		this.netAmount = netAmount;  
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public String getPayrollHeaderId() {
		return payrollHeaderId;
	}

	public Date getPayrollDate() {
		return payrollDate;
	}

	public String getPayrollPeriod() {
		return payrollPeriod;
	}

	public float getNetAmount() {
		return netAmount;
	}
	
	
}
