package com.nemo.unit_test.ch03.ex01;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CalculatorTests {
	static class Calculator{

		public double sum(double first, double second) {
			return first + second;
		}
	}

	@Test
	void sum_of_two_numbers(){
		// arrange
		double first = 10;
		double second = 20;
		Calculator calculator = new Calculator();
		// action
		double result = calculator.sum(first, second);
		// assert
		Assertions.assertThat(result).isEqualTo(30);
	}
}
