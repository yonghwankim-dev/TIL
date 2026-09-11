package com.nemo.unit_test.ch05.ex05;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTests {

	static class User{
		private int userId;
		private String name;

		public User(int userId, String name) {
			this.userId = userId;
			this.name = name;
		}

		public int getUserId() {
			return userId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		// 양옆의 공백을 제거하고 길이가 50자를 넘어가면 50자까지만 자르고 저장한다
		public String normalizedName(String name) {
			String result = name.trim();

			if (result.length() > 50){
				return result.substring(0, 50);
			}
			return result;
		}
	}

	static class UserController{
		private final UserRepository userRepository;

		public UserController(UserRepository userRepository) {
			this.userRepository = userRepository;
		}

		public void renameUser(int userId, String newName){
			User user = userRepository.findById(userId);

			user.setName(user.normalizedName(newName));

			userRepository.save(user);
		}
	}

	static class UserRepository{
		private final Map<Integer, User> store = new HashMap<>();

		public User findById(int userId){
			return store.get(userId);
		}

		public void save(User user) {
			store.put(user.getUserId(), user);
		}
	}

	/**
	 * 구현 세부 사항을 유출하는 User 클래스
	 * <pre>
	 *     클라이언트 : UserController
	 *     클라이언트가 사용자 이름을 변경하는데 도움이 되는 작업 => User Setter 메서드
	 *     User.normalizedName 메서드도 하나의 작업이지만 목표에 직결되지는 않는다.
	 *     문제점
	 *     - User 클래스의 normalizedName은 클래스의 공개 API로 유출되는 구현 세부사항이다.
	 *     해결방법
	 *     - normalizedName 메서드를 숨기고, name 세터를 클라이언트 코드에 의지하지 않고 내부적으로 호출하기
	 * </pre>
	 */
	@Test
	@DisplayName("사용자 이름을 변경할때 새로운 이름이 50글자가 넘어가면 50글자까지만 자르고 이름을 변경한다")
	void rename_user_when_new_name_length_is_more_than_50_should_be_truncated_to_50_chars(){
		// given
		UserRepository fakeUserRepository = new UserRepository();
		fakeUserRepository.save(new User(1, "john"));
		UserController sut = new UserController(fakeUserRepository);
		int userId = 1;
		String newName = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ";
		// when
		sut.renameUser(userId, newName);
		// then
		String expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWX";
		Assertions.assertThat(fakeUserRepository.findById(userId).getName()).isEqualTo(expected);
	}
}
