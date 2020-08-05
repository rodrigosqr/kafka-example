package br.com.rodrigo.ecommerce;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {
	
	private final LocalDatabase database;
	
	public OrdersDatabase () throws SQLException {
		this.database = new LocalDatabase("orders_database");
		 // you might want to save all data
		this.database.createIfNotExists("create table Orders (uuid varchar(200) primary key)");
	}
	
	private boolean wasProcessed(Order order) throws SQLException {
		var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
		return results.next();
	}

	public boolean saveNew(Order order) throws SQLException {
		if (wasProcessed(order)) {
			return false;
		}
		this.database.update("insert into Orders(uuid) values(?)", order.getOrderId());
		return true;
	}

	@Override
	public void close() throws IOException {
		try {
			this.database.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
