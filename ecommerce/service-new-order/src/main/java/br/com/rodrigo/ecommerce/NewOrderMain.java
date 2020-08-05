package br.com.rodrigo.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import br.com.rodrigo.ecommerce.dispatcher.KafkaDispatcher;

public class NewOrderMain {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		try (var orderDispatcher = new KafkaDispatcher<Order>()) {
			for (var i = 0; i < 10; i++) {
				var orderId = UUID.randomUUID().toString();
				var amount = new BigDecimal(Math.random() * 5000 + 1);
				var email = Math.random() + "@email.com";

				var order = new Order(orderId, amount, email);
				var id = new CorrelationId(NewOrderMain.class.getSimpleName());
				orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, id, order);
			}
		}
	}

}
