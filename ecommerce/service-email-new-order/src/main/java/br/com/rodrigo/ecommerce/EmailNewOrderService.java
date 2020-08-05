package br.com.rodrigo.ecommerce;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.rodrigo.ecommerce.consumer.ConsumerService;
import br.com.rodrigo.ecommerce.consumer.ServiceRunner;
import br.com.rodrigo.ecommerce.dispatcher.KafkaDispatcher;

public class EmailNewOrderService implements ConsumerService<Order>{
	
	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
       new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException{
    	var message = record.value();
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(message);
        

        var emailCode = "Thank you for your order! We are processing your order!";
        var order = message.getPayload();
        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);
    }

	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return EmailNewOrderService.class.getSimpleName();
	}


}
