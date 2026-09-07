package com.nemo.unit_test.ch03.ex08;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 비공개 팩토리 메서드로 도출한 공통 초기화
 * <p>
 * <pre>
 * 특징
 * - Store 객체 생성 및 인벤토리 초기화를 비공개 팩토리 메서드(createStoreWithInventory)로 추출해서
 * 테스트 진행 상황에 대한 맥락을 유지시킴
 * - 비공개 메서드로 추출했기 때문에 각각의 테스트간 결합도를 떨어트림
 * - 테스트에서 픽스처(Store)를 어떻게 생성할지 설정할 수 있음
 *
 * 장점
 * - 가독성이 좋음
 * - 테스트간 결합도를 떨어트림
 * - 코드가 매우 쉬워지고 코드 재사용이 가능함
 *
 * 테스트 픽스처 재사용 규칙 예외사항
 * - 데이터베이스 연결 초기화 같은 경우에는 클래스 레벨 픽스처(ex: @BeforeAll)에서 수행하는 것이 합러적
 *
 * </pre>
 */
class CustomerTests {

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
		Product shampoo = new Product("shampoo");
		Store store = createStoreWithInventory(shampoo, 10);
		Customer sut = new Customer();
		// when
		boolean success = sut.purchase(store, shampoo, 5);
		// then
		Assertions.assertThat(success).isTrue();
		Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);
	}

	@Test
	void purchase_fails_when_not_enough_inventory(){
		// given
		Product shampoo = new Product("shampoo");
		Store store = createStoreWithInventory(shampoo, 5);
		Customer sut = new Customer();
		// when
		boolean success = sut.purchase(store, shampoo, 15);
		// then
		Assertions.assertThat(success).isFalse();
		Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);
	}

	private Store createStoreWithInventory(Product product, int quantity){
		Store store = new Store();
		store.addInventory(product, quantity);
		return store;
	}
}
