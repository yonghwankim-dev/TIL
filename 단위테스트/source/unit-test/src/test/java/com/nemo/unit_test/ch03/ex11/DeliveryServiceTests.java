package com.nemo.unit_test.ch03.ex11;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * 테스트 매개변수화
 * <pre>
 *     특징
 *     - 테스트 코드는 동일한데, 값이 다양한 경우 매개변수화된 테스트를 사용할 수 있다.
 *     - 유사한 테스트 케이스를 하나의 단위 테스트로 묶을수 있다.
 *     - 테스트 이름을 올바른지, 잘못되었는지 언급하지 않고 좀더 일반적으로 변경됨
 *
 *     장점
 *     - 테스트의 개수를 줄일수 있음
 *
 *     단점
 *     - 테스트 메서드가 무엇을 설명하는지 잘 파악하기가 힘들어짐
 * </pre>
 */
class DeliveryServiceTests {

	public static Stream<Arguments> provideDeliveryDateSource() {
		return Stream.of(
			Arguments.of(-1, false),
			Arguments.of(0, false),
			Arguments.of(1, false),
			Arguments.of(2, true)
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
	@MethodSource(value = {"provideDeliveryDateSource"})
	void can_detect_an_invalid_delivery_date(int daysFromNow, boolean expected){
		// given
		DeliveryService sut = new DeliveryService();
		LocalDateTime now = LocalDateTime.of(2026, 9, 7, 12, 0);
		LocalDateTime pastDate = now.plusDays(daysFromNow);
		Delivery delivery = new Delivery(pastDate);
		// when
		boolean isValid = sut.isDeliveryValid(now, delivery);
		// then
		Assertions.assertThat(isValid).isEqualTo(expected);
	}
}
