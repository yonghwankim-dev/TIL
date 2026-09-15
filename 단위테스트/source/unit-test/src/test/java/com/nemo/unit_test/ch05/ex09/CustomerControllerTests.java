package com.nemo.unit_test.ch05.ex09;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


class CustomerControllerTests {
	interface EmailGateway{

		void sendReceipt(String to, String productName, int quantity);
	}

	static class Customer{
		private final int customerId;
		private final String email;

		public Customer(int customerId, String email) {
			this.customerId = customerId;
			this.email = email;
		}

		public int getCustomerId() {
			return customerId;
		}

		public String getEmail() {
			return email;
		}

		public boolean purchase(Store store, Product product, int quantity) {
			if(store.getInventory(product) >= quantity){
				store.removeInventory(product, store.getInventory(product) - quantity);
				return true;
			}
			return false;
		}
	}

	static class Product{
		private final int productId;
		private final String name;

		public Product(int productId, String name) {
			this.productId = productId;
			this.name = name;
		}

		public int getProductId() {
			return productId;
		}

		public String getName() {
			return name;
		}
	}

	static class Store{
		private final Map<Product, Integer> inventory = new HashMap<>();

		public void addInventory(Product product, int quantity) {
			inventory.put(product, quantity);
		}

		public int getInventory(Product product) {
			return inventory.getOrDefault(product, 0);
		}

		public void removeInventory(Product product, int quantity){
			if (getInventory(product) < quantity){
				throw new IllegalStateException("not enough product inventory");
			}
			inventory.put(product, getInventory(product) - quantity);
		}
	}

	static class CustomerController{
		private final EmailGateway emailGateway;
		private final CustomerRepository customerRepository;
		private final ProductRepository productRepository;

		public CustomerController(EmailGateway emailGateway, CustomerRepository customerRepository,
			ProductRepository productRepository) {
			this.emailGateway = emailGateway;
			this.customerRepository = customerRepository;
			this.productRepository = productRepository;
		}

		public boolean purchase(Store store, int customerId, int productId, int quantity) {
			Customer customer = customerRepository.findById(customerId);
			Product product = productRepository.findById(productId);

			boolean isSuccess = customer.purchase(store, product, quantity);

			if (isSuccess){
				emailGateway.sendReceipt(customer.getEmail(), product.getName(), quantity);
			}
			return isSuccess;
		}
	}

	static class CustomerRepository{

		private final Map<Integer, Customer> store = new HashMap<>();

		public void save(Customer customer){
			store.put(customer.getCustomerId(), customer);
		}

		public Customer findById(int customerId) {
			return store.get(customerId);
		}
	}

	static class ProductRepository{
		private final Map<Integer, Product> store = new HashMap<>();

		public void save(Product product){
			store.put(product.getProductId(), product);
		}

		public Product findById(int productId) {
			return store.get(productId);
		}
	}

	/**
	 * 취약한 테스트로 이어지지 않는 목 사용
	 * <pre>
	 *     시스템이 구매에 대한 영수증을 보내는지 검증한다.
	 *     목(EmailGateway) 객체를 대상으로 검증했기 때문에 취약한 테스트가 아님
	 *
	 * </pre>
	 */
	@Test
	void successful_purchase(){
		// given
		EmailGateway mock = BDDMockito.mock(EmailGateway.class);
		CustomerRepository customerRepository = new CustomerRepository();
		Customer customer = new Customer(1, "customer@email.com");
		customerRepository.save(customer);
		ProductRepository productRepository = new ProductRepository();
		Product shampoo = new Product(1, "Shampoo");
		productRepository.save(shampoo);

		CustomerController sut = new CustomerController(mock, customerRepository, productRepository);

		Store store = new Store();
		store.addInventory(shampoo, 10);
		int customerId = 1;
		int productId = 1;
		int quantity = 5;
		// when
		boolean success = sut.purchase(store, customerId, productId, quantity);
		// then
		Assertions.assertThat(success).isTrue();
		BDDMockito.verify(mock, Mockito.times(1))
			.sendReceipt("customer@email.com", "Shampoo", 5);
	}
}
