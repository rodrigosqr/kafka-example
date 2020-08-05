package br.com.rodrigo.ecommerce;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.rodrigo.ecommerce.consumer.ConsumerService;
import br.com.rodrigo.ecommerce.consumer.ServiceRunner;
import br.com.rodrigo.ecommerce.dispatcher.KafkaDispatcher;

public class FraudDetectorService implements ConsumerService<Order> {
	
	private final LocalDatabase database;
	
	private FraudDetectorService () throws SQLException {
		this.database = new LocalDatabase("frauds_database");
		this.database.createIfNotExists("create table Orders (uuid varchar(200) primary key, is_fraud boolean)");
	}
	
	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
    	var message = record.value();
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        
        var order = message.getPayload();
        if (wasProcessed(order)) {
        	System.out.println("Order " + order.getOrderId() + " was already processed");
        	return;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignoring
            e.printStackTrace();
        }
        if (isFraud(order)) {
        	database.update("insert into Orders(uuid, is_fraud) values(?, true)", order.getOrderId());
        	System.out.println("Order is a fraud!!!!");
        	orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), message.getId().continueWith(this.getClass().getSimpleName()), order);
        } else {
        	database.update("insert into Orders(uuid, is_fraud) values(?, false)", order.getOrderId());
        	System.out.println("Approved: " + order);
        	orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), message.getId().continueWith(this.getClass().getSimpleName()), order);
        }
    }

	private boolean wasProcessed(Order order) throws SQLException {
		var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
		return results.next();
	}

	private boolean isFraud(Order order) {
		return order.getAmount().compareTo(BigDecimal.valueOf(4500)) >= 0;
	}

	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return FraudDetectorService.class.getSimpleName();
	}

}
