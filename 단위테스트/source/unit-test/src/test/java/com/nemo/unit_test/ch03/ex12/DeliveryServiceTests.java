package com.nemo.unit_test.ch03.ex12;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 긍정적인 시나리오와 부정적인 시나리오를 검증하는 두가지 테스트
 * <pre>
 *     매개변수화된 테스트 사용시 문제점
 *     - 테스트 메서드가 나타내는 사실을 파악하기가 어려움
 *
 *     해결방법
 *     - 긍정적인 테스트 케이스, 부정적인 테스트 케이스로 나누고 이름을 작성하기
 *
 * </pre>
 */
class DeliveryServiceTests {

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
	@ValueSource(ints = {-1, 0, 1})
	void detects_an_invalid_delivery_date(int daysFromNow){
		// given
		DeliveryService sut = new DeliveryService();
		LocalDateTime now = LocalDateTime.of(2026, 9, 7, 12, 0);
		LocalDateTime pastDate = now.plusDays(daysFromNow);
		Delivery delivery = new Delivery(pastDate);
		// when
		boolean isValid = sut.isDeliveryValid(now, delivery);
		// then
		Assertions.assertThat(isValid).isFalse();
	}

	@Test
	void the_soonest_delivery_date_is_two_days_from_now(){
		// given
		DeliveryService sut = new DeliveryService();
		LocalDateTime now = LocalDateTime.of(2026, 9, 7, 12, 0);
		LocalDateTime pastDate = now.plusDays(2);
		Delivery delivery = new Delivery(pastDate);
		// when
		boolean isValid = sut.isDeliveryValid(now, delivery);
		// then
		Assertions.assertThat(isValid).isTrue();
	}
}
