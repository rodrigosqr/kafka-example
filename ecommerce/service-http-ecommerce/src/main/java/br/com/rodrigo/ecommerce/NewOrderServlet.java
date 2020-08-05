package br.com.rodrigo.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.rodrigo.ecommerce.dispatcher.KafkaDispatcher;

public class NewOrderServlet extends HttpServlet {
	private static final long serialVersionUID = -8832522497682836796L;

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
	
	@Override
	public void destroy() {
		super.destroy();
		orderDispatcher.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			var orderId = req.getParameter("uuid");
			var email = req.getParameter("email");
			var amount = new BigDecimal(req.getParameter("amount"));
			var order = new Order(orderId, amount, email);
			
			var database = new OrdersDatabase();
			
			if (database.saveNew(order)) {				
				orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new CorrelationId(this.getClass().getSimpleName()), order);
				
				System.out.println("New order sent successfully.");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().println("New order sent.");
			} else {
				System.out.println("Old order received.");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().println("Old order received.");
			}
		} catch (ExecutionException | InterruptedException | SQLException e) {
			throw new ServletException(e);
		}

	}
}
