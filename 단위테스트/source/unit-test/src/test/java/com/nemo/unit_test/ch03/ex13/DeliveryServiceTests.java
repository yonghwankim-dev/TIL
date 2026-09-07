package com.nemo.unit_test.ch03.ex13;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSources;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 매개변수화된 테스트를 위한 복잡한 데이터 생성
 * <pre>
 *     특징
 *     - 런타임에 LocalDateTime을 생성하여 매개변수화된 데이터 전달함
 *     장점
 *     - 컴파일러 제한을 극복하고 매개변수화된 테스트에서 모든 유형의 매개변수 사용 가능함
 * </pre>
 */
class DeliveryServiceTests {

	public static Stream<Arguments> provideDeliveryDateSource() {
		LocalDateTime fixed = LocalDateTime.of(2026, 9, 7, 12, 0);
		return Stream.of(
			Arguments.of(fixed.plusDays(1), false),
			Arguments.of(fixed, false),
			Arguments.of(fixed.plusDays(1), false),
			Arguments.of(fixed.plusDays(2), true)
		);
	}

	static class DeliveryService{

		// 배송일이 현재 시간 + 2일인지 체크
		public boolean isDeliveryValid(LocalDateTime now, Delivery delivery) {
			LocalDateTime minimumValidDate = now.plusDays(2);
			return !delivery.date.isBefore(minimumValidDate);
		}
	}

	static class Delivery{
		private final LocalDateTime date;

		public Delivery(LocalDateTime date) {
			this.date = date;
		}
	}

	@ParameterizedTest
	@MethodSource("provideDeliveryDateSource")
	void detects_an_invalid_delivery_date(LocalDateTime deliveryDate, boolean expected){
		// given
		DeliveryService sut = new DeliveryService();
		LocalDateTime now = LocalDateTime.of(2026, 9, 7, 12, 0);
		Delivery delivery = new Delivery(deliveryDate);
		// when
		boolean isValid = sut.isDeliveryValid(now, delivery);
		// then
		Assertions.assertThat(isValid).isEqualTo(expected);
	}
}
