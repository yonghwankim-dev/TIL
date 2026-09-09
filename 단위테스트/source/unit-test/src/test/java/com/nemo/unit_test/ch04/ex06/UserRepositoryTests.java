package com.nemo.unit_test.ch04.ex06;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserRepositoryTests {
	static class User{
		private int userID;

		public User(int userID) {
			this.userID = userID;
		}
	}
	static class UserRepository{

		private String lastExecutedSqlStatement;

		public User getById(int id){
			String sql = "SELECT * FROM dbo.User WHERE userID = 5";
			setLastExecutedSqlStatement(sql);
			return new User(id);
		}

		public String getLastExecutedSqlStatement() {
			return lastExecutedSqlStatement;
		}

		public void setLastExecutedSqlStatement(String lastExecutedSqlStatement) {
			this.lastExecutedSqlStatement = lastExecutedSqlStatement;
		}
	}

	/**
	 * 실행중인 SQL문을 검증하는 테스트
	 *
	 * <pre>
	 *	getById 호출시 저장되는 SQL문이 여러가지 형태로 변형되어도 기능은 정상작동하지만 테스트는 실패한다. (거짓 양성 발생)
	 *	ex) sql = "SELECT userID FROM dbo.User WHERE userID = 5"와 같이 내부적으로 변형되어도 기능은 정상작동하지만, 테스트는 실패한다.
	 * 	테스트가 내부 구현 세부 사항에 결합되어 있다는 증거다.
	 * </pre>
	 */
	@Test
	void getById_executes_correct_SQL_code(){
		UserRepository sut = new UserRepository();

		User user = sut.getById(5);

		String sql = "SELECT * FROM dbo.User WHERE userID = 5";
		Assertions.assertThat(sql).isEqualTo(sut.getLastExecutedSqlStatement());
		Assertions.assertThat(user).isNotNull();
	}
}
