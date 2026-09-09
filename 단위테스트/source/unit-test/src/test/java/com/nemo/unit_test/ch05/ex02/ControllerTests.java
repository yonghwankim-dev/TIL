package com.nemo.unit_test.ch05.ex02;

import javax.xml.crypto.Data;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

class ControllerTests {

	interface Database{

		int getNumberOfUsers();
	}

	static class Controller{
		private Database database;
		public Controller(Database database) {
			this.database = database;
		}

		public Report createReport() {
			int numberOfUsers = database.getNumberOfUsers();
			return new Report(numberOfUsers);
		}
	}

	static class Report{
		private int numberOfUser;

		public Report(int numberOfUser) {
			this.numberOfUser = numberOfUser;
		}
	}

	/**
	 * 목 프레임워크를 사용해서 Stub 객체 생성
	 * <pre>
	 *     생성된 Database 객체는 목이 아니라 스텁이다.
	 *     SUT에 입력 데이터를 제공하는 호출을 모방한다.
	 *     이전 예제에서 sendGreetingsEmail 메서드 호출은 외부로 나가는 상호작용
	 *     스텁 객체는 데이터를 제공만 해주는 역할이고, 스텁과의 상호작용을 검증하지 말아야 한다.
	 *
	 * </pre>
	 */
	@Test
	void create_a_report(){
		// given
		Database stub = BDDMockito.mock(Database.class);
		BDDMockito.given(stub.getNumberOfUsers())
			.willReturn(10);
		Controller sut = new Controller(stub);
		// when
		Report report = sut.createReport();
		// then
		Assertions.assertThat(report.numberOfUser).isEqualTo(10);
	}
}
