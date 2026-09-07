package com.nemo.unit_test.ch03.ex04;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CalculatorTests {
	static class Calculator{

		public double sum(double first, double second) {
			return first + second;
		}
	}

	/**
	 * 테스트 대상 시스템 구별하기
	 * - 테스트 대상(SUT)이 많은 경우에는 변수 이름을 sut로 설정하라.
	 * - 개발자는 쉽게 테스트 대상을 쉽게 식별할 수 있다.
	 */
	@Test
	void sum_of_two_numbers(){
		// arrange
		double first = 10;
		double second = 20;
		Calculator sut = new Calculator();
		// action
		double result = sut.sum(first, second);
		// assert
		Assertions.assertThat(result).isEqualTo(30);
	}
}
