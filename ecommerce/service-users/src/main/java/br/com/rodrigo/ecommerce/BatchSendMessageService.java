package br.com.rodrigo.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.rodrigo.ecommerce.consumer.KafkaService;
import br.com.rodrigo.ecommerce.dispatcher.KafkaDispatcher;

public class BatchSendMessageService {
	private final Connection connection;
	private final KafkaDispatcher<User> userDipatcher = new KafkaDispatcher<>();

	BatchSendMessageService() throws SQLException {
		String url = "jdbc:sqlite:users_database.db";
		this.connection = DriverManager.getConnection(url);
		try {
			this.connection.createStatement()
					.execute("create table users (uuid varchar(200) primary key, email varchar(200))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
		var batchSendMessageService = new BatchSendMessageService();
		try (var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
				"ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", batchSendMessageService::parse, Map.of())) {
			service.run();
		}
	}

	private void parse(ConsumerRecord<String, Message<String>> record)
			throws ExecutionException, InterruptedException, SQLException {
		var message = record.value();
		System.out.println("------------------------------------------");
		System.out.println("Processing new batch");
		System.out.println("Topic: " + message.getPayload());

		for (User user : getAllUsers()) {
			userDipatcher.sendAsync(message.getPayload(), user.getUuid(), message.getId().continueWith(this.getClass().getSimpleName()), user);
		}
	}

	private List<User> getAllUsers() throws SQLException {
		var results = this.connection.prepareStatement("select uuid from users").executeQuery();
		List<User> users = new ArrayList<>();
		while (results.next()) {
			users.add(new User(results.getString(1)));
		}
		return users;
	}
}
