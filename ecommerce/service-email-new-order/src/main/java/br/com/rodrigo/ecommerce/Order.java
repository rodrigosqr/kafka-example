package br.com.rodrigo.ecommerce;

import java.math.BigDecimal;

/**
 * @author rodrigocoelho
 *
 */
public class Order {

    private final String orderId;
    private final BigDecimal amount;
    private final String email;

    public Order(String orderId, BigDecimal amount, String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

	public String getEmail() {
		return email;
	}

	public String getOrderId() {
		return orderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

}
