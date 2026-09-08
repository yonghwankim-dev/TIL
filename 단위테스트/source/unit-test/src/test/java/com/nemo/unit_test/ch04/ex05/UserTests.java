package com.nemo.unit_test.ch04.ex05;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTests {

	static class User{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * 간단한 테스트
	 * <pre>
	 *     - 빠른 피드백 높음
	 *     - 리팩토링 내성 높음
	 *     - 회귀 방지 낮음
	 *
	 * 이러한 테스트는 항상 통과하거나 검증이 무의미해서 어떤것도 테스트한다고 할 수 없음
	 * </pre>
	 */
	@Test
	void test(){
		User sut = new User();

		sut.setName("Jhon Smith");

		Assertions.assertThat(sut.getName()).isEqualTo("Jhon Smith");
	}
}
