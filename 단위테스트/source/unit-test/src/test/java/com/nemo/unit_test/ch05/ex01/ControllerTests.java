package com.nemo.unit_test.ch05.ex01;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

class ControllerTests {

	interface EmailGateway{

		void sendGreetingEmail(String email);
	}

	static class Controller{
		private EmailGateway emailGateway;

		public Controller(EmailGateway emailGateway) {
			this.emailGateway = emailGateway;
		}

		public void greetUser(String email) {
			emailGateway.sendGreetingEmail(email);
		}
	}

	/**
	 * BDDMockito 목 프레임워크를 이용해서 EmailGateway 목 객체를 생성하였는데,
	 * <pre>
	 * BDDMockito는 도구로서의 목인데 반해서 EmailGateway 목 객체는 테스트 대역으로서의 목이라는 것을 인지해야 한다.
	 * 도구로서의 목을 사용해서 목과 스텁, 2가지의 테스트 대역을 생성할수 있기 때문에 테스트 대역으로서의 목과 혼동하면 안된다.
	 * </pre>
	 *
	 */
	@Test
	void sending_a_greetings_email(){
		// given
		EmailGateway emailGateway = BDDMockito.mock(EmailGateway.class);
		Controller sut = new Controller(emailGateway);
		String email = "user@email.com";
		// when
		sut.greetUser(email);
		// then
		BDDMockito.verify(emailGateway, Mockito.times(1))
			.sendGreetingEmail(email);
	}
}
