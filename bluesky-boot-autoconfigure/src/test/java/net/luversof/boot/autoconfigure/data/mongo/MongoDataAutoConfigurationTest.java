package net.luversof.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoDataAutoConfigurationTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("net-profile:opdev")
			.withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class,
					MongoAutoConfiguration.class, org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class, MongoDataAutoConfiguration.class));

	@Configuration
	@EnableMongoRepositories(basePackages = "net.luversof.boot.autoconfigure.data.mongo", mongoTemplateRef = "userMongoTemplate")
	public class Config {
		
	}
	
	@Test
	public void templateExists() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(MongoTemplate.class));
	}
//	
//	@Test
//	public void test() {
//		repository.deleteAll();
//
//		// save a couple of customers
//		repository.save(new User("Alice", "Smith"));
//		repository.save(new User("Bob", "Smith"));
//
//		// fetch all customers
//		System.out.println("Customers found with findAll():");
//		System.out.println("-------------------------------");
//		for (User customer : repository.findAll()) {
//			System.out.println(customer);
//		}
//		System.out.println();
//
//		// fetch an individual customer
//		System.out.println("Customer found with findByFirstName('Alice'):");
//		System.out.println("--------------------------------");
//		System.out.println(repository.findByFirstName("Alice"));
//
//		System.out.println("Customers found with findByLastName('Smith'):");
//		System.out.println("--------------------------------");
//		for (User customer : repository.findByLastName("Smith")) {
//			System.out.println(customer);
//		}
//
//	}
//	
//	@Test
//	public void test2() {
//		this.contextRunner.run((context) -> {
//			CityRepository repository = context.getBean(CityRepository.class);
//			log.debug("result : {}", repository.findAll());
//		});
//	}
}
