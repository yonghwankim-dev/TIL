package com.nemo.unit_test.ch03.ex10;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 테스트 이름 작성 지침
 * <pre>
 *     - 테스트 대상 객체의 메서드 이름을 테스트 이름에 포함시키지 마라.
 *     - 메서드 이름을 테스트 이름에 포함시키면 추후 수정시 결합도가 높아진다.
 *     - 테스트 대상 메서드 이름을 작성하지 말고 애플리케이션의 동작에 집중하여 작성하라
 *     - 개발자가 아닌 다른 일반적인 담당자가 읽어도 이해갈수 있도록 이름을 작성한다.
 *
 * </pre>
 */
class DeliveryServiceTests {

	static class DeliveryService{

		public boolean isDeliveryValid(Delivery delivery) {
			LocalDateTime now = LocalDateTime.now();
			return delivery.date.isAfter(now);
		}
	}

	static class Delivery{
		private final LocalDateTime date;

		public Delivery(LocalDateTime date) {
			this.date = date;
		}
	}

	// 나쁜 테스트 이름 케이스
	@Test
	void isDeliveryValid_invalidDate_returnFalse(){
		// given
		DeliveryService sut = new DeliveryService();
		LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
		Delivery delivery = new Delivery(pastDate);
		// when
		boolean isValid = sut.isDeliveryValid(delivery);
		// then
		Assertions.assertThat(isValid).isFalse();
	}

	// 좋은 테스트 이름 케이스
	@Test
	void delivery_with_a_past_date_is_invalid(){
		// given
		DeliveryService sut = new DeliveryService();
		LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
		Delivery delivery = new Delivery(pastDate);
		// when
		boolean isValid = sut.isDeliveryValid(delivery);
		// then
		Assertions.assertThat(isValid).isFalse();
	}
}
