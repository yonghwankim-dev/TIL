package com.nemo.unit_test.ch03.ex07;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerTests {

	private final Store store; // 공통 테스트 픽스처(Fixture)
	private final Customer sut;
	private final Product shampoo;

	/**
	 * 각각의 테스트 실행 이전에 생성자 호출된다.
	 */
	CustomerTests() {
		store = new Store();
		shampoo = new Product("shampoo");
		store.addInventory(shampoo, 10);
		sut = new Customer();
	}

	static class Customer{

		public boolean purchase(Store store, Product product, int quantity) {
			if (store.getInventory(product) >= quantity){
				store.removeInventory(product, store.getInventory(product) - quantity);
				return true;
			}
			return false;
		}
	}

	static class Store{

		private final Map<Product, Integer> store = new HashMap<>();

		public int getInventory(Product product) {
			return store.getOrDefault(product, 0);
		}

		public void addInventory(Product product, int quantity) {
			store.put(product, getInventory(product) + quantity);
		}

		public void removeInventory(Product product, int quantity){
			if (getInventory(product) < quantity){
				throw new IllegalStateException("not enough product inventory");
			}
			store.put(product, getInventory(product) - quantity);
		}
	}

	static class Product{
		private final String name;

		public Product(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Product product))
				return false;
			return Objects.equals(name, product.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

	@Test
	void purchase_succeeds_when_enough_inventory(){
		// given

		// when
		boolean success = sut.purchase(store, shampoo, 5);
		// then
		Assertions.assertThat(success).isTrue();
		Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);
	}

	@Test
	void purchase_fails_when_not_enough_inventory(){
		// given

		// when
		boolean success = sut.purchase(store, shampoo, 15);
		// then
		Assertions.assertThat(success).isFalse();
		Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(10);
	}
}
