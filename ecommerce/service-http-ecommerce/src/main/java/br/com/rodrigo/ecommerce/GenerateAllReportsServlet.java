package br.com.rodrigo.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateAllReportsServlet extends HttpServlet {
	private static final long serialVersionUID = -8832522497682836796L;

	private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

	@Override
	public void destroy() {
		super.destroy();
		batchDispatcher.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			batchDispatcher.send("ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", "ECOMMERCE_USER_GENERATE_READING_REPORT", new CorrelationId(this.getClass().getSimpleName()), "ECOMMERCE_USER_GENERATE_READING_REPORT");
			
			System.out.println("Sent generate report to all users.");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("Report requests generate");
		} catch (ExecutionException | InterruptedException e) {
			throw new ServletException(e);
		}

	}
}
