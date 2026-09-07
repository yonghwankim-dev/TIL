package com.nemo.unit_test.ch03.ex03;

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

		public void removeInventory(boolean success, Product shampoo, int quantity) {
			if(success){
				inventory.put(shampoo, inventory.get(shampoo) - quantity);
			}
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
			// store.addInventory(product, store.getInventory(product) - quantity);
			return store.getInventory(product) >= quantity;
		}
	}

	/**
	 * 코드 문제점
	 * - 단일 작업(물품 구매)을 수행하는데 2개의 메서드 호출(purchase, removeInventory)이 필요함
	 * - Customer 클래스의 API(purchase)에 문제가 존재함
	 * - 클라이언트 코드가 첫번째 메서드(purchase)를 호출하고 두번재 메서드를 호출하지 않으면 재고 수량 부분에서 문제가 발생함
	 *   - 고객은 제품을 얻을수 있지만 재고 수량은 줄어들지 않음
	 *
	 * 해결 방법
	 * - purcahse 메서드에 캡슐화를 적용하기.
	 * - 물품 구매시 재고 수량도 같이 변경하기.
	 *
	 * 키워드
	 * - 불변 위반(invariant violation)
	 * - 캡슐화(encapsulation)
	 *
	 *
	 */
	@Test
	void purchase_succeeds_when_enough_inventory(){
		// given
		Store store = new Store();
		Product shampoo = new Product("Shampoo");
		store.addInventory(shampoo, 10);
		Customer customer = new Customer();
		// when
		boolean success = customer.purchase(store, shampoo, 5);
		store.removeInventory(success, shampoo, 5);
		// then
		Assertions.assertThat(success).isTrue();
		Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);
	}
}
