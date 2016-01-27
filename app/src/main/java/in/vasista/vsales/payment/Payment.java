package in.vasista.vsales.payment;

import java.util.Date;

public class Payment {
	String id;
	Date paymentDate;
	String paymentMethodType;	
	double amount;
	
	public Payment (String id, Date paymentDate, String paymentMethodType, double amount) {
		this.id = id;
		this.paymentDate = paymentDate;
		this.paymentMethodType = paymentMethodType;		
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getPaymentMethodType() {
		return paymentMethodType;
	}
	
	public String getId() {
		return id;
	}
		
	public double getAmount() {
		return amount;
	}
	

	@Override
	public String toString() {
		return "Payment [id=" + id + ", paymentDate=" + paymentDate + ", paymentMethodType=" + paymentMethodType
				+ ", amount=" + amount + "]";
	}
}
