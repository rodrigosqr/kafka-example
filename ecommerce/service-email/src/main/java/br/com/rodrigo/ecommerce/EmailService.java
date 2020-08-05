package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.rodrigo.ecommerce.consumer.ConsumerService;
import br.com.rodrigo.ecommerce.consumer.ServiceRunner;

public class EmailService implements ConsumerService<String> {

	public static void main(String[] args) {
		new ServiceRunner(EmailService::new).start(5);
    }

	@Override
    public void parse(ConsumerRecord<String, Message<String>> record) {
    	var message = record.value();
        System.out.println("------------------------------------------");
        System.out.println("Send email");
        System.out.println(record.key());
        System.out.println(message.getPayload());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignoring
            e.printStackTrace();
        }
        System.out.println("Email sent");
    }

	@Override
	public String getTopic() {
		return "ECOMMERCE_SEND_EMAIL";
	}

	@Override
	public String getConsumerGroup() {
		return EmailService.class.getSimpleName();
	}


}
