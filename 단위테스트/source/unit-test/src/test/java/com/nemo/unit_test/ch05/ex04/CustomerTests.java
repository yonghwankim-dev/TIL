package com.nemo.unit_test.ch05.ex04;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

class CustomerTests {

	static class Product{
		private final String name;

		public Product(String name) {
			this.name = name;
		}
	}
	interface Store{
		boolean hasEnoughInventory(Product product, int quantity);
		void removeInventory(Product product, int quantity);
	}

	static class Customer{

		public boolean purchase(Store store, Product product, int quantity) {
			if (!store.hasEnoughInventory(product, quantity)){
				return false;

			}
			store.removeInventory(product, quantity);
			return true;
		}
	}

	/**
	 * 목이자 스텁인 storeMock
	 * <pre>
	 *     storeMock 객체는 목이자 스텁 역할을 동시에 수행한다.
	 *     테스트는 hasEnoughInventory()에서 응답을 설정한 다음, removeInventory()에 대한 호출을 검증한다.
	 *     스텁과의 상호작용을 검증하지 말라는 규칙은 여기서도 위배하지 않는다.
	 *     목과 스텁 역할을 동시에 수행하는 객체를 대체로 목이라고 한다
	 *     분해해서 봤을때 hasEnoughInventory 메서드 호출은 스텁 역할이지만 removeInventory()는 스텁 역할을 하지 않아서
	 *     검증해도 규칙에 위배되지 않음.
	 * </pre>
	 */
	@Test
	void purchase_fails_when_not_enough_inventory(){
		// given
		Store storeMock = BDDMockito.mock(Store.class);
		Product shampoo = new Product("shampoo");
		int quantity = 5;
		// 준비된 응답을 설정
		BDDMockito.given(storeMock.hasEnoughInventory(shampoo, quantity))
			.willReturn(false);
		Customer sut = new Customer();
		// when
		boolean success = sut.purchase(storeMock, shampoo, quantity);
		// then
		Assertions.assertThat(success).isFalse();
		// SUT에서 수행한 호출을 검사
		BDDMockito.verify(storeMock, Mockito.never())
			.removeInventory(shampoo, quantity);
	}
}
