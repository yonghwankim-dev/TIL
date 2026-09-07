package com.nemo.unit_test.ch03.ex05;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

class CalculatorTests {
	static class Calculator{

		public double sum(double first, double second) {
			return first + second;
		}
	}

	static class PriceCalculator{

		private final DiscountPolicy discountPolicy;

		public PriceCalculator(DiscountPolicy discountPolicy) {
			this.discountPolicy = discountPolicy;
		}

		public double sum(double first, double second) {
			return first + second - discountPolicy.getDiscountAmount(first + second);
		}
	}

	static class DiscountPolicy{

		public double getDiscountAmount(double amount) {
			return 5;
		}
	}

	/**
	 * 준비, 실행, 검증 주석 제거하기
	 * <p>
	 * - 기본적으로 AAA(or given-when-then) 패턴을 따르고
	 * 준비(given) 및 검증(then) 구절에 빈줄을 추가하지 않아도 되는 테스트라면 주석을 제거하라.
	 * 그렇지 않으면 주석을 유지하라.
	 */
	@Test
	void sum_of_two_numbers(){
		double first = 10;
		double second = 20;
		Calculator sut = new Calculator();

		double result = sut.sum(first, second);

		Assertions.assertThat(result).isEqualTo(30);
	}

	/**
	 * 준비-실행-검증 주석 유지해야 하는 케이스
	 * <p>
	 * - 준비(given) 구절 안에 내부 요소간의 역할이 서로 다르거나 준비 과정 단계를 논리적으로 구분해야 하는 경우
	 * 구절 내부에 빈줄을 추가한다. 그리고 given-when-then 주석을 유지한다.
	 */
	@Test
	void sum_of_two_numbers_should_discount_price(){
		// given
		// 테스트에 필요한 데이터 및 객체 준비
		double first = 10;
		double second = 20;
		DiscountPolicy mockDiscountPolicy = BDDMockito.mock(DiscountPolicy.class);

		// 목 객체의 행위 정의(Stubbing) - 픽스처(Fixture) 생성과 역할이 다르므로 빈줄로 구분
		BDDMockito.given(mockDiscountPolicy.getDiscountAmount(30))
			.willReturn(5.0);

		PriceCalculator sut = new PriceCalculator(mockDiscountPolicy);

		// when
		double result = sut.sum(first, second);
		// then
		Assertions.assertThat(result).isEqualTo(25);
	}
}
