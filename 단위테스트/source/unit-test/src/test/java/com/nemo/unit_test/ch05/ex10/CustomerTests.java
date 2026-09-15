package com.nemo.unit_test.ch05.ex10;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

class CustomerTests {

	static class Customer{

		public boolean purchase(Store store, Product product, int quantity) {
			if(store.hasEnoughInventory(product, quantity)){
				store.removeInventory(product, quantity);
				return true;
			}
			return false;
		}
	}

	static class Product{
		private final String name;

		public Product(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	static class Store{
		private final Map<Product, Integer> inventory = new HashMap<>();

		public int getInventory(Product product) {
			return inventory.getOrDefault(product, 0);
		}

		public void removeInventory(Product product, int quantity){
			if (!hasEnoughInventory(product, quantity)){
				throw new IllegalStateException("not enough product inventory");
			}
			inventory.put(product, getInventory(product) - quantity);
		}

		public boolean hasEnoughInventory(Product product, int quantity) {
			return getInventory(product) >= quantity;
		}
	}

	@Test
	void purchase_succeeds_when_enough_inventory(){
		// given
		Store storeMock = BDDMockito.mock(Store.class);
		Product shampoo = new Product("shampoo");
		BDDMockito.given(storeMock.hasEnoughInventory(shampoo, 5))
			.willReturn(true);
		Customer customer = new Customer();
		// when
		boolean success = customer.purchase(storeMock, shampoo, 5);
		// then
		Assertions.assertThat(success).isTrue();
		BDDMockito.verify(storeMock, Mockito.times(1))
			.removeInventory(shampoo, 5);
	}
}
