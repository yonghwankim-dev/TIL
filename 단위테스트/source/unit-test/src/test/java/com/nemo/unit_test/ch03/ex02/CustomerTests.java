package com.nemo.unit_test.ch03.ex02;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerTests {

	static class Store{

		private final Map<Product, Integer> inventory = new HashMap<>();

		public void addInventory(Product product, int quantity) {
			inventory.put(product, quantity);
		}

		public int getInventory(Product product) {
			return inventory.get(product);
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

	static class Customer{

		public boolean purchase(Store store, Product product, int quantity) {
			if (store.getInventory(product) >= quantity){
				store.addInventory(product, store.getInventory(product) - quantity);
				return true;
			}
			return false;
		}
	}

	@Test
	void purchase_succeeds_when_enough_inventory(){
		// given
		Store store = new Store();
		Product shampoo = new Product("Shampoo");
		store.addInventory(shampoo, 10);
		Customer customer = new Customer();
		// when
		boolean success = customer.purchase(store, shampoo, 5);
		// then
		Assertions.assertThat(success).isTrue();
		Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);
	}
}
