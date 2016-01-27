package in.vasista.hr.payslip;

public class PayslipItem {
	String payheadType;
	float payheadAmount;
	
	public PayslipItem(String payheadType, float payheadAmount) {
		this.payheadType = payheadType;
		this.payheadAmount = payheadAmount;
	}

	public String getPayheadType() {
		return payheadType;
	}

	public float getPayheadAmount() {
		return payheadAmount;
	}

	
}
