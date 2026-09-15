package com.nemo.unit_test.ch05.ex06;

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
			this.name = normalizedName(name);
		}

		public int getUserId() {
			return userId;
		}

		public String getName() {
			return name;
		}

		// 이름 변경시 normalized 처리
		public void setName(String name) {
			this.name = normalizedName(name);
		}

		// public -> private 변경
		private String normalizedName(String name) {
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

			// 클라이언트 입장에서는 newName을 전달하기만 함
			user.setName(newName);

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
	 * API가 잘 설계된 User 클래스
	 * <pre>
	 *     식별할 수 있는 동작만 공개 = setName
	 *     구현 세부사항 비공개 = normalizedName
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
