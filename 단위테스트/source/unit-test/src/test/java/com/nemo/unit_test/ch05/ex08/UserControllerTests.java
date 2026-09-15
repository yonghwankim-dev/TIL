package com.nemo.unit_test.ch05.ex08;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserControllerTests {
	static class User{
		private int userId;
		private String name;

		public User(int userId, String name) {
			this.userId = userId;
			this.name = normalizedName(name);
		}

		public void setName(String name) {
			this.name = normalizedName(name);
		}

		// 공개 API로 설정함
		public String normalizedName(String name) {
			String result = name.trim();

			if (result.length() > 50){
				return result.substring(0, 50);
			}
			return result;
		}

		public int getUserId() {
			return userId;
		}

		public String getName() {
			return name;
		}
	}

	static class UserController{
		private final UserRepository userRepository;

		public UserController(UserRepository userRepository) {
			this.userRepository = userRepository;
		}

		public void renameUser(int userId, String newName){
			User user = userRepository.findById(userId);
			user.setName(newName);
			userRepository.save(user);
		}
	}

	static class UserRepository{
		private final Map<Integer, User> store = new HashMap<>();

		public void save(User user){
			store.put(user.getUserId(), user);
		}

		public User findById(int userId){
			return store.get(userId);
		}
	}

	/**
	 * 애플리케이션 서비스와 도메인 클래스
	 * <pre>
	 * UserController : 애플리케이션 서비스
	 * User : 도메인
	 *
	 * 외부 클라이언트가 사용자 이름을 정규화하는 것과 같은 특정한 목표가 없고, 전적으로 애플리케이션 제약으로 인해서
	 * 모든 이름을 정규화한다고 가정하면, User클래스의 normalizedName 메서드는 클라이언트의 요구사항으로 추적할 수 없다.
	 * 따라서 구현 세부사항이므로 비공개로 해야 한다.
	 *
	 * 테스트에서 이 메서드(normalizedName)를 직접 확인하면 안된다.
	 *
	 * 클래스의 식별할 수 있는 동작(setName)으로서만 검증해야 한다.
	 * </pre>
	 *
	 *
	 */
	@Test
	void rename_user_when_newName_is_longer_than_50_then_slice_newName(){
		UserRepository userRepository = new UserRepository();
		int userId = 1;
		User user = new User(userId, "bob");
		userRepository.save(user);
		UserController userController = new UserController(userRepository);
		String newName = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ";
		// when
		userController.renameUser(userId, newName);
		// then
		Assertions.assertThat(user.getName())
			.isEqualTo("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWX");
	}
}
