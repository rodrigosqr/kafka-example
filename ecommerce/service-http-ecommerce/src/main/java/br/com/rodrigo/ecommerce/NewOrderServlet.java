package br.com.rodrigo.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewOrderServlet extends HttpServlet {
	private static final long serialVersionUID = -8832522497682836796L;

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
	
	@Override
	public void destroy() {
		super.destroy();
		orderDispatcher.close();
		emailDispatcher.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			var orderId = UUID.randomUUID().toString();
			var email = req.getParameter("email");
			var amount = new BigDecimal(req.getParameter("amount"));

			var order = new Order(orderId, amount, email);
			orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new CorrelationId(this.getClass().getSimpleName()), order);

			var emailCode = "Thank you for your order! We are processing your order!";
			emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, new CorrelationId(this.getClass().getSimpleName()), emailCode);
			System.out.println("New order sent successfully.");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("New order sent.");
		} catch (ExecutionException | InterruptedException e) {
			throw new ServletException(e);
		}

	}
}
