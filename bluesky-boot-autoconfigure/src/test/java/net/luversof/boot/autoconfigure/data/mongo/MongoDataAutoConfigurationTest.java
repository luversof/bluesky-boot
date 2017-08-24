package net.luversof.boot.autoconfigure.data.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.autoconfigure.TestAutoConfigurationPackage;
import net.luversof.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ImportAutoConfiguration(MongoDataAutoConfiguration.class)
@Slf4j
//@DirtiesContext
public class MongoDataAutoConfigurationTest {

	@Autowired
	private CustomerRepository repository;
	
	@Test
	public void test() {
		repository.deleteAll();

		// save a couple of customers
		repository.save(new Customer("Alice", "Smith"));
		repository.save(new Customer("Bob", "Smith"));

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		// fetch an individual customer
		System.out.println("Customer found with findByFirstName('Alice'):");
		System.out.println("--------------------------------");
		System.out.println(repository.findByFirstName("Alice"));

		System.out.println("Customers found with findByLastName('Smith'):");
		System.out.println("--------------------------------");
		for (Customer customer : repository.findByLastName("Smith")) {
			System.out.println(customer);
		}

	}
	
	@Test
	public void test2() {
		log.debug("result : {}", repository.findAll());
	}
	
	@Configuration
	@SpringBootApplication
	@TestAutoConfigurationPackage(MongoDataAutoConfigurationTest.class)
	protected static class Config {

	}
}