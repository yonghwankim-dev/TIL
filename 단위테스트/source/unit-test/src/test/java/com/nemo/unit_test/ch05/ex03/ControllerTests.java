package com.nemo.unit_test.ch05.ex03;

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
		// 스텁으로 상호 작용을 검증 : 테스트가 취약해짐, getNumberOfUsers가 아닌 다른 메서드나 방법으로 사용자 개수들을 구하면 테스트가 실패한다.
		BDDMockito.verify(stub, Mockito.times(1))
			.getNumberOfUsers();
	}
}
