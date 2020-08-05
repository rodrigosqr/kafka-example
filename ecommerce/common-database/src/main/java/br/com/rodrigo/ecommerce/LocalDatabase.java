package br.com.rodrigo.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalDatabase {
	private final Connection connection;

	public LocalDatabase(String name) throws SQLException {
		String url = "jdbc:sqlite:target/" + name + ".db";
		this.connection = DriverManager.getConnection(url);
	}

	public void createIfNotExists(String sql) throws SQLException {
		try {
			this.connection.createStatement().execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private PreparedStatement prepare(String statement, String[] params) throws SQLException {
		var preparedStatement = this.connection.prepareStatement(statement);
		for (int i = 0; i < params.length; i++) {
			preparedStatement.setString(i + 1, params[i]);
		}
		return preparedStatement;
	}

	public boolean update(String statement, String... params) throws SQLException {
		return prepare(statement, params).execute();
	}

	public ResultSet query(String statement, String... params) throws SQLException {
		return prepare(statement, params).executeQuery();

	}

	public void close() throws SQLException {
		this.connection.close();
	}

}
