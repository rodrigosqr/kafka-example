package br.com.rodrigo.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.rodrigo.ecommerce.consumer.ConsumerService;
import br.com.rodrigo.ecommerce.consumer.ServiceRunner;

public class ReadingReportService implements ConsumerService<User> {

	private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		new ServiceRunner(ReadingReportService::new).start(5);
	}

	@Override
	public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
		System.out.println("------------------------------------------");
		System.out.println("Processing report for " + record.value());

		var message = record.value();
		var user = message.getPayload();
		var target = new File(user.getReportPath());
		IO.copyTo(SOURCE, target);
		IO.append(target, "Created for " + user.getUuid());

		System.out.println("File created: " + target.getAbsolutePath());

	}


	@Override
	public String getTopic() {
		return "ECOMMERCE_USER_GENERATE_READING_REPORT";
	}

	@Override
	public String getConsumerGroup() {
		return ReadingReportService.class.getSimpleName();
	}

}