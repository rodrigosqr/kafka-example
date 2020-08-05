package br.com.rodrigo.ecommerce;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.rodrigo.ecommerce.consumer.ConsumerService;
import br.com.rodrigo.ecommerce.consumer.ServiceRunner;

public class CreateUserService implements ConsumerService<Order> {
	
	private final LocalDatabase database;
	
	private CreateUserService () throws SQLException {
		this.database = new LocalDatabase("users_database");
		this.database.createIfNotExists("create table users (uuid varchar(200) primary key, email varchar(200))");
	}

    public static void main(String[] args) {
	    new ServiceRunner<>(CreateUserService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
    	var message = record.value();
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.key());
        System.out.println(message);
        var order = message.getPayload();
        if(isNewUser(order.getEmail())) {
        	insertNewUser(order.getEmail());
        }
    }

	private void insertNewUser(String email) throws SQLException {
		String uuid = UUID.randomUUID().toString();
		this.database.update("insert into users(uuid, email) values(?,?)", uuid, email);
		System.out.println("Usuário " + uuid + " e " + email + " adicionado");
	}

	private boolean isNewUser(String email) throws SQLException {
		var results = this.database.query("select uuid from users where email = ? limit 1", email);
		return !results.next();
	}

	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return CreateUserService.class.getSimpleName();
	}
}
