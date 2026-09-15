
## 개요
- 단위 테스트의 런던파와 고전파 논쟁 다루기
	- 런던파 : 불변 의존성을 제외한 모든 의존성을 목 객체 사용하기
	- 고전파 : 공유 의존성을 대상으로만 목 객체 사용하기
- 목과 테스트 취약성간에 관계 보기
- 리팩토링 내성 저하 없이 목 사용하기

## 5.1 목과 스텁 구분
### 5.1.1 테스트 대역 유형
**테스트 대역(Test Double)**
- 목
	- 목
	- 스파이
- 스텁
	- 스텁
	- 더미
	- 페이크

**목과 스파이의 차이**
- 목은 가짜 객체로써 기본적으로 모든 메서드가 아무 동작도 하지 않는다. 하지만 스파이는 진짜 객체를 감싼 래퍼 객체로써 별도로 모킹하지 않은 메서드는 실제 로직이 그대로 작동된다.
- 목은 사용하려고 하는 모든 메서드의 동작을 처음부터 모킹 설정해야 한다. 스파이는 필요한 메서드만 선택적으로 모킹해서 사용할 수 있다.
- 목의 주 목적은 SUT(테스트 대상)가 외부 객체의 특정 메서드를 호출했는지 검증하는 것이고, 스파이는 진짜 객체의 동작을 유지하면서 호출 횟수나 인자 등을 기록 및 확인하는 것이다.

목은 `Mockito.mock()` 으로 만든 객체이고 내부가 완전히 비어있는 껍데기이다. 목 객체가 호출하는 모든 메서드에 대해서 모킹을 해주어야 한다.
```java
@Test
void mock_example() {
    // 1. Mock 객체 생성
    PaymentNotificationService mockService = Mockito.mock(PaymentNotificationService.class);

    // 2. 메서드 호출
    mockService.sendEmail("user@test.com"); // ❌ 아무것도 출력되지 않음 (실제 로직 실행 X)
    String status = mockService.getStatus(); // ❌ null 반환 (기본값)

    // 3. Stubbing (반환값 지정)
    Mockito.when(mockService.getStatus()).thenReturn("MOCK_READY");
    Assertions.assertThat(mockService.getStatus()).isEqualTo("MOCK_READY");

    // 4. 행위 검증 (호출 여부 확인)
    Mockito.verify(mockService).sendEmail("user@test.com");
}
```

스파이는 `Mockito.spy()`로 만든 객체이고 실제 인스턴스를 감싸서 수행한다. 별도의 모킹 처리를 하지 않으면 실제 로직이 수행된다.
```java
@Test
void spy_example() {
    // 1. 실제 객체를 생성 후 Spy로 감쌈
    PaymentNotificationService realService = new PaymentNotificationService();
    PaymentNotificationService spyService = Mockito.spy(realService);

    // 2. 메서드 호출 -> ⭕ 실제 로직 실행! ("실제 이메일 발송: user@test.com" 콘솔 출력)
    spyService.sendEmail("user@test.com");

    // 3. Stubbing을 하지 않은 메서드는 실제 반환값을 유지
    Assertions.assertThat(spyService.getStatus()).isEqualTo("READY"); // ⭕ "READY" 반환

    // 4. 필요한 특정 메서드만 동작 재정의
    Mockito.doReturn("SPY_READY").when(spyService).getStatus();
    Assertions.assertThat(spyService.getStatus()).isEqualTo("SPY_READY");

    // 5. 실제 로직이 수행됨과 동시에 호출 여부 검증 가능
    Mockito.verify(spyService).sendEmail("user@test.com");
}
```


**목과 스텁의 차이**
목(목, 스파이)은 외부로 나가는 상호작용을 모방하고 검사하는데 사용된다. 이러한 상호작용은 SUT가 상태를 변경하기 위해서 의존성을 호출될 때 사용된다.
스텁(스텁, 더미, 페이크)은 데이터 조회와 같이 상태 변경이 일어나지 않는 상호작용에 사용된다. 상호작용을 할때 스텁은 정해진 값을 반환하기만 한다.

**스텁, 더미, 페이크의 차이**
더미는 메서드 실행이나 객체 생성시 매개변수를 채우기 위한 객체이다. 내부는 `null`값이나 문자열과 같이 하드코딩되어 전혀 동작하지 않는다. 코드로 표현하면 다음과 같다.
```java
// 주소 객체가 필요하지만, 테스트하는 로직(이름 검증)에서는 주소를 쓰지 않을 때
Address dummyAddress = new Address(); 
User user = new User("홍길동", dummyAddress);

Assertions.assertThat(user.getName()).isEqualTo("홍길동");
```

스텁은 정해진 시나리오에서 특정 값이 들어오면 미리 정해진 값을 반환하는 객체이다.
```java
// 외부 환율 조회 서비스 스텁
public class StubExchangeRateService implements ExchangeRateService {
    @Override
    public BigDecimal getRate(String currency) {
        // 실제 환율 API를 부르지 않고 무조건 미리 지정한 1,300원을 반환
        return new BigDecimal("1300"); 
    }
}
```

페이크는 실제 프로덕션 코드에서 사용하는 객체와 동일한 인터페이스를 사용하지만 테스트를 위해서 단순하게 구현된 가짜 구현체입니다. 주로 In-Memory 방식의 페이크를 많이 사용한다.
```java
// 실제 DB 대신 메모리 Hash Map을 이용해 작동하는 Fake Repository
public class FakeUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user); // 실제 DB SQL 대신 메모리에 저장
    }

    @Override
    public User findById(Long id) {
        return users.get(id); // 메모리에서 조회
    }
}
```

### 5.1.2 도구로서의 목과 테스트 대역으로서의 목
도구로서의 목은 `Mockito`와 같은 라이브러리/프레임워크를 이용해서 생성한 **포괄적인 의미의 목 객체**를 의미한다. 반면에 테스트 대역으로서의 목은 해당 목 객체가 수행하는 역할이 **테스트 대역의 5가지 형태(목, 스파이, 스텁, 더미, 페이크) 중 하나**를 의미한다.

예를 들어 `Mockito.mock()`을 통해서 만든 객체는 상호작용 모방 및 검증이 가능한 실제 목 객체이지만, 테스트 대역의 관점에서는 해당 객체를 목 객체라고 바라보면 안된다. 해당 목 객체가 Mocktio 라이브러리에서 실제 목 객체로 생성 되어도 실제 역할은 목이 아니라 스텁 역할 일수도 있다. 스텁 역할을 수행하는 목 객체를 대상으로 호출횟수와 같은 상태 검증을 하게 되면 추후 리팩토링 작업시 테스트가 실패하는 일이 발생한다.

예를 들어 다음 테스트는 UserRepository 타입의 목 객체를 생성하고, 정해진 값을 반환하는 코드입니다.
```java
// Mockito(도구)의 메서드 이름이 mock()입니다.
UserRepository userRepository = Mockito.mock(UserRepository.class);

// 하지만 가짜 데이터를 반환하도록 설정(Stubbing)합니다.
Mockito.when(userRepository.findById(1L)).thenReturn(new User("홍길동"));
```

위 코드를 보면 `Mockito.mock()` 를 통해서 UserRepository 목 객체를 생성하였지만 정해진 값(User 객체)만을 반환하도록 설정되어 있습니다. 즉, **Mockito 라이브러리를 통해서 목 객체를 생성하였지만, 실제 역할은 스텁의 역할을 수행**하고 있는 것입니다.
따라서  `Mockio.mock()`을 통해서 생성한 것은 목 객체이지만, **테스트 대역의 관점에서 보면 실질적으로 UserRepository는 스텁 역할을 수행하고 있는 것**이다. 이 부분을 헷갈리면 안된다.

정리하면, Mockito(도구) 관점에서 바라보면 `mock()` 메서드를 호출해서 만든 모든 객체는 목 객체이다. 하지만 테스트 대역의 관점에서 바라보면 해당 목 객체는 목, 스파이, 스텁, 더미, 페이크 역할 중 하나일 수 있다.

### 5.1.3 스텁으로 상호 작용을 검증하지 말라
테스트 대역에서 목 역할은 상호작용을 모방하고 검사하는 반면에 스텁 역할은 상호작용을 모방만 하고 테스트에서 따로 검사하면 안된다. 
스텁 역할을 하는 객체를 테스트에서 따로 검사하지 않는 이유는 스텁 역할을 수행하는 객체는 최종 결과를 만들기 위한 데이터를 제공하는 수단이기 때문입니다.

스텁 역할을 하는 객체를 대상으로 테스트에서 검사를 수행하게 되면 거짓 양성이 발생하고 리팩토링 내성이 감소하게 됩니다. 예를 들어 다음 테스트는 Report 객체를 생성하는 테스트이다. `createReport` 메서드 수행 도중에 다른 의존성 객체인 Database 객체를 통해서 사용자수를 가져오는 메서드(getNumberOfUsers)를 호출합니다.
```java
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
```

Database 객체는 정해진 사용자수를 반환만 하기 때문에 스텁 역할을 수행하는 테스트 대역입니다. 이러한 객체를 대상으로 테스트에서 호출횟수(상태 검증)를 검증하고 있다. 그런데 추후 `getNumberOfUsers` 메서드가 아닌 다른 방법으로 사용자수를 구한다고 리팩토링 작업을 하게 되면 해당 테스트는 실패하게 된다.
따라서 스텁 역할을 수행하는 Database 객체를 대상으로 상태 검증을 하지말고, 최종 결과물로 나온 Report 객체를 대상으로 검증을 수행해야 한다.

정리하면, **스텁 역할을 수행하는 목 객체를 대상으로는 테스트에서 상태 검증을 수행하면 안된다.** 추후 리팩토링 작업시 기능은 잘 동작하지만 테스트가 실패하는 거짓 양성이 발생하게 되고, 이는 리팩토링 내성 감소로 이어진다.

### 5.1.4 목과 스텁 함께 쓰기
목과 스텁을 함께 쓴다는 의미는 하나의 테스트 대역(Test Double) 객체가 "테스트 대상에 필요한 입력 데이터나 상태 데이터를 제공하는 역할"과 "테스트 대상이 내보낸 출력 메시지나 호출 행위를 검증하는 역할(Mock)"을 동시에 수행하는 것을 의미합니다. 하나의 외부 의존성 객체가 조회(Read)와 저장/응답(Write)의 역할을 동시에 맡는 경우에 이러한 형태가 자연스럽게 발생합니다.

예를 들어 다음 테스트 코드는 Customer 객체의 `purchase()` 메서드를 테스트한다. 
```java
@Test  
void purchase_fails_when_not_enough_inventory(){  
    // given  
    Store storeMock = BDDMockito.mock(Store.class);  
    Product shampoo = new Product("shampoo");  
    int quantity = 5;  
    // 준비된 응답을 설정  
    BDDMockito.given(storeMock.hasEnoughInventory(shampoo, quantity))  
       .willReturn(false);  
    Customer sut = new Customer();  
    // when  
    boolean success = sut.purchase(storeMock, shampoo, quantity);  
    // then  
    Assertions.assertThat(success).isFalse();  
    // SUT에서 수행한 호출을 검사  
    BDDMockito.verify(storeMock, Mockito.never())  
       .removeInventory(shampoo, quantity);  
}
```

Customer 객체를 통해서 상품을 구매하는 도중에 의존성 객체인 Store 객체를 통해서 상품 재고 개수가 구매하고자 하는 상품 개수보다 큰지 검사하는 부분이 존재한다. Store 의존성 객체는 목 객체로써 `Store.hasEnoughInventory()` 메서드 호출시 `false`를 반환하도록 응답을 설정(스터빙, Stubbing)한다. 즉, `storeMock` 객체가 스텁 역할을 수행하도록 한다.
그리고 테스트에서 `purchase()` 메서드 실행후에 최종 결과물(sucess)을 검증하고 Store 목 객체를 대상으로 `removeInventory` 메서드의 호출횟수를 상태 검증한다.

위 예제를 보면 2가지 목적으로 `storeMock` 객체를 사용하고 있다. `Store.hasEnoughInventory`  메서드 호출에 대한 준비된 응답을 반환하고 `sut` 객체의 `purchase` 메서드에서 수행했을때 `Store.removeInventory` 메서드를 호출했는지 검증한다.

`storeMock` 목 객체는 정해진 값을 반환하고, 메서드 호출에 대한 상태 검증을 했기 때문에 스텁과의 상호작용을 검증하지말라는 규칙을 위배했다고 생각할 수 있다. 그러나 `storeMock` 목 객체는 이 규칙을 위배하지 않았다. 그 이유는 `hasEnoughInventory` 메서드를 대상으로 상태 검증을 하지 않았고, 상태 변경을 일으키는 `removeInventory` 메서드를 대상으로는 상태 검증을 수행하였기 때문이다. 
비록 `storeMock` 목 객체가 정해진 응답을 반환하는 스터빙(Stubbing)을 수행하였지만, 테스트 코드에서 상태 검증을 할때 스터빙을 설정한 메서드를 대상으로 상태 검증을 하지 않았고, 상태 변경을 일으키는 메서드를 대상으로 상태 검증을 하였기 때문에 규칙 위배가 아니다.

위 예제를 통해서 우리가 기억해야 할 것은 목 객체가 상호작용 모방 및 상태 검증을 수행할때 **스텁 역할을 수행하는 메서드를 대상으로는 테스트에서 상태 검증을 수행하지 말아야 하고**, **상태 변경을 일으키는 메서드에 대해서는 테스트에서 상태 검증**을 해야 한다는 점이다.


### 5.1.5 목과 스텁은 명령과 조회에 어떻게 관련돼 있는가?
**CQS(Command Query Separation) 원칙**
- 소프트웨어 개발에서 함수(메서드)가 명령(Command)과 조회(Query) 중 하나만을 수행해야 한다는 설계 원칙
- 명령(Command) : 상태 변경을 발생시키고 어떤 값도 반환하지 않는 함수(메서드, void 반환)
- 조회(Query) : 상태 변경을 발생시키지 않고, 값을 반환하는 함수(메서드)

모든 메서드는 CQS 원칙을 기반으로해서 명령이거나 조회여야 한다. 이 둘을 혼용해서는 안된다. 

다음 예제는 CQS를 위반한 사례입니다. 조회한 객체를 대상으로 상태를 변경한 다음에 상태가 변경된 객체를 그대로 반환하고 있습니다. 상태가 변경된 객체를 반환했다는 것은 Query 했다고 볼수 있습니다. 그런데 해당 메서드를 호출했을때 상태도 변경했기 때문에 CQS 원칙 위반입니다.
```java
// ❌ CQS 위반 예시: 상태 변경(Command)과 데이터 반환(Query)을 한 메서드에서 수행
public User updateAndReturnUser(Long userId, String newName) {
    User user = userRepository.findById(userId).get();
    user.changeName(newName); // 상태 변경 (Command)
    
    return user; // 수정된 객체 반환 (Query) -> 위반!
}
```

만약 다음과 같이 데이터를 조회한 다음에 상태만 변경했다면 CQS 원칙 위반이 안됩니다.
```java
public void updateUser(Long userId, String newName){
	User user = userRepository.findById(userId).get();
	user.changeName(newName); // 상태 변경 (Command);
} 
```

**CQS 예외적 허용 케이스**
스레드 안전(Thread-Safety)이 필요한 `Stock.pop()`이나 DB의 Auto-increment ID를 반환하는 `save()` 처럼 기술적/성능적 이유로 CQS를 타협하는 경우가 있지만, 이는 명시적인 예외 규칙에 해당합니다. 하지만 그래도 가능한 한 CQS 원칙을 따르는 것이 좋습니다.

**목과 스텁, 명령(Command)와 조회(Query) 간의 관계**
- 명령(Command)을 대체하는 테스트 대역은 목
- 조회(Query)을 대체하는 테스트 대역은 스텁

어떤 메서드가 상태 변경을 일으키는 명령 타입의 메서드라면 목 역할을 수행하는 테스트 대역을 사용하고, 상태 변경을 일으키지 않는 조회하는 메서드라면 스텁 역할을 수행하는 테스트 대역을 사용하세요.

그리고 메서드를 호출할때 목 역할인지, 스텁 역할인지 인지하세요. 그래야지 테스트에서 해당 메서드를 검증할지 안할지 구분할 수 있다.

## 5.2 식별할 수 있는 동작과 구현 세부 사항
좋은 단위 테스트 일수록 리팩토링 내성이 높다. 리팩토링 내성이 높을 것을 최대한 활용하는 것이 좋다. 
테스트에서 거짓 양성이 발생하여 리팩토링 내성이 떨어지는 원인은 테스트와 테스트 대상의 구현 세부사항과 결합되어 있기 때문이다. 이러한 거짓 양성을 피하기 위해서는 SUT가 생성하는 최종 결과물만을 가지고 검증하는 것이다. 최종 결과물을 반환하지 않는 명령(Command) 타입의 행위라면 메서드 호출을 한후에 변경되는 객체의 상태 검증을 수행한다.

이 섹션에서는 구현 세부 사항은 무엇이고, 식별할 수 있는 동작과는 어떻게 다른지 살펴본다.

### 5.2.1 식별할 수 있는 동작은 공개 API와 다르다
모든 제품 코드 분류
- 공개 API 또는 비공개 API
- 식별할 수 있는 동작 또는 구현 세부사항

코드가 시스템의 식별할 수 있는 동작인 조건(둘중 하나 만족)
- 클라이언트가 목표를 달성하는데 도움이 되는 연산을 노출하라. 연산은 계산을 수행하거나 사이드 이펙트를 초래하거나 둘다 하는 메서드
- 클라이언트가 목표를 달성하는데 도움되는 상태를 노출하라. 상태는 시스템의 현재 상태

구현 세부사항은 위 두가지중 아무것도 하지 않는다.

이상적으로 시스템의 공개 API는 식별할 수 있는 동작과 일치해야 하고, 모든 세부 구현사항은 클라이언트 눈에 보이지 않아야 한다. 모든 세부 구현 사항은 비공개 API 뒤에 숨겨져 있다.

그런데 공개 API가 식별할 수 있는 동작의 범위를 넘어서면 시스템은 구현 세부사항을 유출한다.

### 5.2.2 구현 세부 사항 유출: 연산의 예
구현 세부사항이 공개 API로 유출되는 코드의 예시를 든다.
```java
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
```

**클래스가 구현 세부 사항을 유출하는지 판단하는 데 도움이 되는 유용한 규칙**
- 단일한 목표를 달성하고자 클래스에서 호출해야 하는 연산의 수가 1보다 크면 해당 클래스에서 구현 세부사항이 유출하고 있는 가능성이 있다.
	- 이상적으로는 단일 연산으로 개별 목표를 달성해야 한다.
- 예를 들어 UserController가 사용자 이름을 변경한다는 목표를 달성하기 위해서 2가지 연산을 호출하였다.
	- normalizedName, setName
	- 리팩토링 후에는 1로 감소함 => setName

다음 코드는 위 예제를 개선하여 API가 잘 설계된 User 클래스로 변경된 예제입니다.
```java
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
```


### 5.2.3 잘 설계된 API와 캡슐화
캡슐화 기술을 잘 사용하면 불변성 위반을 막을 수 있다. 예를 들어 User 클래스의 사용자 이름이 50자를 초과하면 안된다는 불변성을 `normalizedName` private 메서드를 이용해서 지킬수 있었다.

클래스의 구현 세부 사항을 노출하게 되면 캡슐화가 깨지게 되고 불변성 위반을 가져오게 됩니다. 이전 예제의 User 클래스는 `normalizedName` 메서드를 public 메서드로 설정하여 구현 세부사항을 유출할 뿐만 아니라 캡슐화를 제대로 유지하지 못하였다. 이로 인해서 **클라이언트는 불변성을 우회해서 이름을 먼저 정규화하지 않고 새로운 이름을 할당할 수 있었다.**

클라이언트 코드가 어느 부분에서 불변성을 우회했는가?
=> 여기서의 불변성은 User 클래스의 `name` 필드값은 최대 50자를 초과할 수 없다는 규칙을 가지고 있다. 그런데 클라이언트가 `normalizedName` 메서드를 실행하지 않고 `setName` 메서드에 이름을 전달시 50자가 넘는 이름을 설정할 수 있다는 의미이다.

이름을 먼저 정규화하지 않았다는 의미
=> 정규호 로직(`normalizedName`)의 호출 책임을 User 클래스가 가지고 있어야 하는데, 클라이언트가 정규화를 잊어버리고 바로 `setName` 을 부를 위험 열려 있다는 의미이다.

새로운 이름을 할당할 수 있는 것이 어디인가?
=> `User.setName 메서드`

실수할 가능성을 최대한 없애기 위해서는 캡슐화가 필요하다. 캡슐화는 단위 테스트와 마찬가지로 소프트웨어 프로젝트의 지속적인 성장을 가능하게 하는것이다.

캡슐화 장점
- 구현 세부사항을 숨겨서 클라이언트 입장에서 내부 손상시킬 가능성을 차단함
- 데이터와 연산을 결합하기 때문에 해당 연산이 불변성을 위반하지 않도록 할 수 있음

### 5.2.4 구현 세부 사항 유출: 상태의 예
다음 예제는 머리글, 본문, 바닥글이 있는 HTML 코드를 생성하는 렌더링 클래스 예제입니다.
```java
class MessageRendererTests {  
    interface Renderer{  
       String render(Message message);  
    }  
  
    static class MessageRenderer implements Renderer{  
       public final List<Renderer> subRenderers;  
  
       public MessageRenderer() {  
          this.subRenderers = List.of(  
             new HeaderRenderer(),  
             new BodyRenderer(),  
             new FooterRenderer()  
          );  
       }  
  
       @Override  
       public String render(Message message) {  
          return subRenderers.stream()  
             .map(r->r.render(message))  
             .collect(Collectors.joining());  
       }  
    }  
  
    static class HeaderRenderer implements Renderer{  
       @Override  
       public String render(Message message) {  
          return String.format("<head>%s</head>", message.getHeader());  
       }  
    }  
  
    static class BodyRenderer implements Renderer{  
       @Override  
       public String render(Message message) {  
          return String.format("<body>%s</body>", message.getBody());  
       }  
    }  
  
    static class FooterRenderer implements Renderer{  
       @Override  
       public String render(Message message) {  
          return String.format("<footer>%s</footer>", message.getFooter());  
       }  
    }  
  
    static class Message{  
       private final String header;  
       private final String body;  
       private final String footer;  
  
       public Message(String header, String body, String footer) {  
          this.header = header;  
          this.body = body;  
          this.footer = footer;  
       }  
  
       public String getHeader() {  
          return header;  
       }  
  
       public String getBody() {  
          return body;  
       }  
  
       public String getFooter() {  
          return footer;  
       }  
    }  
  
    @Test  
    void render_message(){  
       // given  
       MessageRenderer renderer = new MessageRenderer();  
       // when  
       List<Renderer> subRenderers = renderer.subRenderers;  
       // then  
       // 가장 안좋은 검증 : 내부 리스트의 순서와 구체 클래스 타입까지 검증  
       Assertions.assertThat(subRenderers)  
          .hasSize(3)  
          .hasExactlyElementsOfTypes(  
             HeaderRenderer.class,  
             BodyRenderer.class,  
             FooterRenderer.class  
          );  
    }  
}
```

위 예제에서 주목할 점은 `subRenderers` 컬렉션 필드가 `public` 이라는 점입니다. 그러나 이 컬렉션 필드는 식별할 수 있는 동작이 아니다. 클라이언트에게 필요한 것은 오직 `render` 메서드 뿐이다. 따라서 `subRenderers` 필드는 구현 세부사항 유출이 된다.

이 문제를 해결하기 위해서는 private 제한자로 변경해서 클라이언트가 오직 render 메서드만 호출할 수 있도록 해야 된다.

잘 설계된 API 정의
- 연산과 상태를 최소한으로 노출시키기
- 클라이언트가 목표를 달성하는데 직접적으로 도움되는 코드만 공개해야 함

## 5.3 목과 테스트 취약성 간의 관계
육각형 아키텍처의 개념과 내부 통신과 외부 통신의 차이점, 목과 테스트 취약간의 관계를 알아본다.

### 5.3.1 육각형 아키텍처 정의
전형적인 애플리케이션은 **도메인**과 **애플리케이션 서비스**가 존재하고 애플리케이션 서비스 안에 도메인이 존재한다. 이러한 도메인은 비즈니스 로직을 포함하고 있다. 
애플리케이션 서비스 계층은 도메인 계층 위에 존재하며 외부 환경과 통신합니다. 예를 들어 애플리케이션이 RESTful API인 경우 API에 대한 모든 요청이 먼저 애플리케이션 서비스 계층에 도달하게 된다. 이러한 애플리케이션 서비스 계층은 도메인 클래스와 프로세스 외부 의존성 간의 작업을 조장한다.

**애플리케이션 서비스에 대한 조정의 예시**
- 데이터베이스 조회하고 해당 데이터로 도메인 클래스 인스턴스 구체화
- 해당 인스턴스에 연산 호출
- 결과를 다시 데이터베이스에 저장

**육각형 아키텍처 정의**
- 도메인들과 애플리케이션 서비스들로 구성된 하나의 소프트웨어
- 애플리케이션끼리 다른 애플리케이션과 통신한다.

**도메인 계층과 애플리케이션 서비스 계층 간의 관심사 분리**
- 도메인 계층은 비즈니스 로직에 대해서만 책임을 져야 한다.
- 외부 애플리케이션과 통신하거나 데이터베이스에서 데이터 검색하는 것과 같은 책임은 애플리케이션 서비스에 귀속되어야 한다.
- 애플리케이션 서비스에는 어떤 비즈니스 로직도 있어서는 안된다.
- 도메인 계층을 애플리케이션의 도메인 지식 모음으로, 애플리케이션 서비스 계층을 일련의 비즈니스 유스케이스로 볼수 있다.

**애플리케이션 통신**
- 육각형 아키텍처에서는 애플리케이션 서비스 계층에서 도메인 계층으로 단방향 흐름으로 흘러야 한다.
- 도메인 클래스는 같은 계층의 도메인 클래스끼리 의존하고 상호작용하여야 한다.

애플리케이션 간의 통신
- 외부 애플리케이션은 애플리케이션 서비스 계층에 있는 공통 인터페이스를 통해서만 해당 애플리케이션 연결된다.

코드 베이스의 공개 API를 항상 비즈니스 요구 사항에 따라 추적하라는 이 지침은 대부분의 도메인 클래스와 애플리케이션 서비스에 적용되지만, **유틸리티나 인프라 코드**에는 적용되지 않는다.

### 5.3.2 시스템 내부 통신과 시스템 간 통신
시스템 내부 통신
- 애플리케이션 내 클래스간 통신
- 시스템 내부 통신은 구현 세부사항

시스템 간 통신
- 애플리케이션이 다른 애플리케이션과 통신
- 시스템 간 통신은 구현 세부사항이 아님

연산을 수행하기 위한 도메인 클래스 간의 협력은 식별할 수 있는 동작이 아니기 때문에 **시스템 내부 통신은 구현 세부 사항**에 해당한다. 테스트가 도메인 간 협력을 검증하면 구현 세부사항에 결합되므로 테스트가 취약해진다.

시스템 외부 환경과 통신하는 방식은 전체적으로 해당 시스템의 식별할 수 있는 동작을 나타낸다. 이러한 시스템 외부 환경과 통신하는 방식은 애플리케이션에 항상 있어야 한다.

**애플리케이션 성장 원칙**
- 하위 호환성 지키기 : 애플리케이션의 API가 업데이트 되어도 기존 다른 클라이언트 시스템이 아무런 코드 수정없이 그대로 정상 작동하는 상태를 의미함
- 예를 들어 API 요청 및 응답 구조 지키기, 메시지 버스로 전송하는 메시지 구조 지키기, SMTP 호출시 매개변수 유형과 개수 등 맞추기

목을 사용하면 시스템과 외부 애플리케이션 간의 통신 패턴을 확인할 때 좋다. 반대로 하면 테스트가 세부 구현사항과 결합해서 리팩토링 내성이 감소한다.

### 5.3.3 시스템 내부 통신과 시스템 간 통신의 예
시스템 내부 통신과 시스템 간 통신의 차이점을 설명

구매(purchase)라는 동작은 시스템 내부 통신과 시스템 간 통신이 모두 있는 비즈니스 유스케이스이다.
- 시스템 내부 통신 : Customer <-> Store
- 시스템 간 통신 : CustomerController <-> EmailGateway

purcahse() 메서드는 구매를 시작하고, getInventory() 메서드는 구매가 완료 된 후 시스템 상태를 보여준다. removeInventory() 메서드 호출은 고객의 목표로 가는 중간 단계에 해당한다.

**외부 애플리케이션과 도메인 모델 연결하기 예제**
- 다음 예제에서 CustomerController 클래스는 도메인 클래스(Customer, Product, Store)와 외부 애플리케이션(SMTP 서비스의 프록시인 EmailGateway) 간의 작업을 조정하는 애플리케이션 서비스이다.
- CustomerController 클래스의 purchase 메서드에서 고객은 상점에 재고가 충분한지 확인하고, 충분하면 재고 수량을 감소시킨다.
- CustomerController 클래스의 구매(purchase)라는 동작은 시스템 내부 통신과 시스템 간 통신이 모두 있는 **비즈니스 유스케이스**이다.
- 시스템 내부 통신은 Customer와 Store 도메인 클래스간의 통신
```java
class CustomerControllerTests {  
    interface EmailGateway{  
  
       void sendReceipt(String to, String productName, int quantity);  
    }  
  
    static class Customer{  
       private final int customerId;  
       private final String email;  
  
       public Customer(int customerId, String email) {  
          this.customerId = customerId;  
          this.email = email;  
       }  
  
       public int getCustomerId() {  
          return customerId;  
       }  
  
       public String getEmail() {  
          return email;  
       }  
  
       public boolean purchase(Store store, Product product, int quantity) {  
          if(store.getInventory(product) >= quantity){  
             store.removeInventory(product, store.getInventory(product) - quantity);  
             return true;  
          }  
          return false;  
       }  
    }  
  
    static class Product{  
       private final int productId;  
       private final String name;  
  
       public Product(int productId, String name) {  
          this.productId = productId;  
          this.name = name;  
       }  
  
       public int getProductId() {  
          return productId;  
       }  
  
       public String getName() {  
          return name;  
       }  
    }  
  
    static class Store{  
       private final Map<Product, Integer> inventory = new HashMap<>();  
  
       public void addInventory(Product product, int quantity) {  
          inventory.put(product, quantity);  
       }  
  
       public int getInventory(Product product) {  
          return inventory.getOrDefault(product, 0);  
       }  
  
       public void removeInventory(Product product, int quantity){  
          if (getInventory(product) < quantity){  
             throw new IllegalStateException("not enough product inventory");  
          }  
          inventory.put(product, getInventory(product) - quantity);  
       }  
    }  
  
    static class CustomerController{  
       private final EmailGateway emailGateway;  
       private final CustomerRepository customerRepository;  
       private final ProductRepository productRepository;  
  
       public CustomerController(EmailGateway emailGateway, CustomerRepository customerRepository,  
          ProductRepository productRepository) {  
          this.emailGateway = emailGateway;  
          this.customerRepository = customerRepository;  
          this.productRepository = productRepository;  
       }  
  
       public boolean purchase(Store store, int customerId, int productId, int quantity) {  
          Customer customer = customerRepository.findById(customerId);  
          Product product = productRepository.findById(productId);  
  
          boolean isSuccess = customer.purchase(store, product, quantity);  
  
          if (isSuccess){  
             emailGateway.sendReceipt(customer.getEmail(), product.getName(), quantity);  
          }  
          return isSuccess;  
       }  
    }  
  
    static class CustomerRepository{  
  
       private final Map<Integer, Customer> store = new HashMap<>();  
  
       public void save(Customer customer){  
          store.put(customer.getCustomerId(), customer);  
       }  
  
       public Customer findById(int customerId) {  
          return store.get(customerId);  
       }  
    }  
  
    static class ProductRepository{  
       private final Map<Integer, Product> store = new HashMap<>();  
  
       public void save(Product product){  
          store.put(product.getProductId(), product);  
       }  
  
       public Product findById(int productId) {  
          return store.get(productId);  
       }  
    }  
    
	// ...  
}
```


**육각형 아키텍처 간의 시스템 간 통신**
- 시스템 간 통신은 CustomerController 애플리케이션 서비스와 2개의 외부 시스템인 서드파티 애플리케이션(유스케이스를 시작하는 클라이언트이기도 함, 예를 들어 HTTP 통신에서 클라이언트에 해당)과 이메일 게이트웨이(EmailGateway) 간의 통신
- STMP 서비스에 대한 호출(EmailGateway.sendReceipt 메서드)은 외부 환경에서 볼수 있는 사이드 이펙트이기 때문에 애플리케이션에게는 전체적으로 식별할 수 있는 동작을 나타낸다.
- 애플리케이션의 클라이언트는 서드파티 시스템이다. 서드 파티 시스템의 목표는 구매를 하는 것이고, 고객이 성공적인 결과로서 이메일로 확인 내역을 받는 것을 기대한다.
```
+-------------------+
|  서드파티 시스템  | (외부 시스템 / 클라이언트)
| (Third-Party Sys) |
+-------------------+
          |
          |  [시스템 간 통신]
          |  - HTTP / REST API 요청
          v
+-----------------------------------------------------------------------+
| 애플리케이션 경계 (Application Boundary)                              |
|                                                                       |
|        +-----------------------------------------------------+        |
|        | 도메인 / 시스템 내부 (System Domain)                 |        |
|        |                                                     |        |
|        |     +----------+                     +-------+      |        |
|        |     | Customer | ------------------> | Store |      |        |
|        |     +----------+   RemoveInventory   +-------+      |        |
|        |                     (재고 차감)                     |        |
|        |                                                     |        |
|        |   * [시스템 내부 통신 / In-Process Communication]     |        |
|        |   - Customer가 Store의 RemoveInventory() 메서드 호출    |        |
|        +-----------------------------------------------------+        |
|                                                                       |
+-----------------------------------------------------------------------+
          |
          |  [시스템 간 통신]
          |  - SendReceipt() (영수증 발송 요청)
          v
+-------------------+
|    SMTP 서비스    | (외부 의존성 / 메일 서버)
|   (SMTP Service)  |
+-------------------+
```

다음 예제는 외부 시스템인 STMP 서비스 호출을 모킹 설정 및 상태 검증하여 취약한 테스트로 이어지지 않게 하는 테스트 예제입니다.
- 검증 코드 부분을 보면 외부 시스템 호출을 하는 sendReceipt 메서드를 호출횟수를 검증한다.
```java
@Test  
void successful_purchase(){  
    // given  
    EmailGateway mock = BDDMockito.mock(EmailGateway.class);  
    CustomerRepository customerRepository = new CustomerRepository();  
    Customer customer = new Customer(1, "customer@email.com");  
    customerRepository.save(customer);  
    ProductRepository productRepository = new ProductRepository();  
    Product shampoo = new Product(1, "Shampoo");  
    productRepository.save(shampoo);  
  
    CustomerController sut = new CustomerController(mock, customerRepository, productRepository);  
  
    Store store = new Store();  
    store.addInventory(shampoo, 10);  
    int customerId = 1;  
    int productId = 1;  
    int quantity = 5;  
    // when  
    boolean success = sut.purchase(store, customerId, productId, quantity);  
    // then  
    Assertions.assertThat(success).isTrue();  
    BDDMockito.verify(mock, Mockito.times(1))  
       .sendReceipt("customer@email.com", "Shampoo", 5);  
}
```


다음 예제는 취약한 테스트로 이어지는 목 사용 케이스입니다.
- Store 목 객체의 중간 과정인 `hasEnoughInventory` 메서드에 대해서 정해진 값을 응답해서 스텁 역할을 수행시킴
- 검증 부분에서 Store 목 객체를 대상으로 removeInventory 호출횟수를 검증하고 있다.
```java
@Test  
void purchase_succeeds_when_enough_inventory(){  
    // given  
    Store storeMock = BDDMockito.mock(Store.class);  
    Product shampoo = new Product("shampoo");  
    BDDMockito.given(storeMock.hasEnoughInventory(shampoo, 5))  
       .willReturn(true);  
    Customer customer = new Customer();  
    // when  
    boolean success = customer.purchase(storeMock, shampoo, 5);  
    // then  
    Assertions.assertThat(success).isTrue();  
    BDDMockito.verify(storeMock, Mockito.times(1))  
       .removeInventory(shampoo, 5);  
}
```

문제점
- CustomerController와 SMTP 서비스 간의 통신과는 달리, Customer와 Store 클래스간의 메서드 호출은 애플리케이션 경계를 넘지 않고 있다.
- 호출자(Customer)와 수신자(Store) 모두 애플리케이션 안에 있는 상태이고, 이 `hasEnoughInventory` 메서드는 클라이언트(CustomerController)가 목표를 달성하는데 도움되는 연산이나 상태가 아니다.
- 목표에 관련 있는 멤버는 Customer.purchase()와 Store.getInvetntory() 둘뿐이다.
- removeInventory 메서드 호출은 고객의 목표로 가는 중간 단계에 해당됨


개선안
- removeInventory 메서드 호출에 대한 상태 검증을 하지 말고 최종적인 결과물 검증과 변경된 상태 검증만 수행함

## 5.4 단위 테스트의 고전파와 런던파 재고
단위 테스트의 고전파와 런던파 간 차이점

|     | 격리 주체  | 단위의 크기           | 테스트 대역 사용 대상    |
| --- | ------ | ---------------- | --------------- |
| 런던파 | 단위     | 단일 클래스           | 불변 의존성 외 모든 의존성 |
| 고전파 | 단위 테스트 | 단일 클래스 또는 클래스 세트 | 공유 의존성          |

런던파는 불변 의존성을 제외한 모든 의존성에 목 사용을 권장하며, **시스템 내 통신과 시스템 간 통신을 구분하지 않는다.** 그결과 테스트는 애플리케이션과 외부 시스템 간의 통신을 확인하는 것처럼 **클래스간 통신도 확인한다.**

**런던파 문제점**
- 도메인 클래스를 대상으로도 목을 무분별하게 사용시 종종 구현 세부 사항에 결합되서 테스트에 리팩토링 내성이 없어짐

**고전파 특징**
- 고전파는 테스트간에 공유하는 의존성(대부분이 SMTP 서비스나 메시지 버스등 프로세스 외부 의존성에 해당)만 교체하자고 하므로 . 이문제에 훨씬 유리함.
- 고전파도 런던파 못지않게 목사용을 지나치게 장려하는 문제는 있다. 고전파 역시 시스템 간 통신에 대한 처리에 이상적이지는 않다.

### 5.4.1 모든 프로세스 외부 의존성을 목으로 해야 하는 것은 아니다.
의존성 종류
- 공유 의존성
	- 테스트 클래스의 필드 멤버들
- 프로세스 외부 의존성
	- 데이터베이스, 메시지 버스, SMTP 서비스
- 비공개 의존성
	- 공유하지 않는 모든 의존성

고전파에서는 공유 의존성을 목과 스텁으로 교체할 것을 권장한다. 테스트간에 공유 의존성을 사용하는 경우 서로 방해할 가능성이 높고, 병렬 처리가 불가능하다. 테스트를 격리시키세요.
- 테스트 격리 : 테스트들을 병렬적, 순차적, 또는 임의적인 순서로 실행시킬 수 있는 것.

**공유 의존성을 피한다는 의미**
- 여러 단위 테스트가 동일한 인스턴스, 데이터, 메모리 상태 또는 외부 자원을 함께 공유하며 읽고 쓰는 구조를 만들지 않는다는 의미

프로세스 외부 의존성을 목 설정 예외 케이스
- 프로세스 외부 의존성을 애플리케이션을 통해서만 접근하는 경우
- 이러한 외부 의존성은 시스템에서 식별할 수 있는 동작이 아니게 된다.
- 외부에서 관찰할 수 없는 프로세스 외부 의존성은 애플리케이션의 일부로써 작동함
	- 예를 들어 애플리케이션 데이터베이스가 존재함

하위 호환성을 지켜야 한다는 점이 무슨 의미인가?
=> 새로운 버전으로 시스템을 업그레이드해도 **기존 버전의 방식을 사용하는 클라이언트나 외부 시스템이 아무런 수정 없이 그대로 정상 작동하게 만드는 것을 의미**한다.

애플리케이션과 외부 시스템 간에 통신 패턴을 지켜야 한다는 것은 하위 호환성에서 비롯된다. 
- 통신 패턴을 항상 지킨다는 것은 외부 시스템과 주고받는 데이터 형식, 호출 방식(protocol), 순서 그리고 예외 처리 규칙을 약소된 명세대로 엄격하게 유지한다는 의미

서드파티시스템(외부 클라이언트)가 애플리케이션의 외부 의존성인 데이터베이스 연결 의존성을 관찰할 수 없기 때문에 애플리케이션의 데이터베이스 의존성은 구현 세부사항에 속한다. 그래서 애플리케이션 데이터베이스는 목으로 검증해서는 안된다.

완전한 통제권을 가진 프로스세 외부 의존성(애플리케이션 데이터베이스)에 목을 사용하면 깨지기 쉬운 테스트로 이어진다.

데이터베이스에서 테이블을 분할하거나 저장 프로시저에서 매개변수 타입을 변경할때마다 테스트가 빨간색이 되는 것을 아무도 원치 않는다. 데이터베이스와 애플리케이션을 하나의 시스템으로 취급해야 한다.

### 5.4.2 목을 사용한 동작 검증
목표를 달성하고자 각 개별 클래스가 이웃 클래스와 소통하는 방식은 식별할 수 있는 동작과는 아무런 관계가 없다. (구현 세부 사항에 해당됨)

클라이언트는 내부 동작이 어떻게 돌아가는지 상관할 필요가 없다. 최종적인 결과물이나 상태 검증이 중요하다.

각각의 개별 클래스가 이웃의 클래스와 상호작용하는 것을 검증하지 마세요.

목은 애플리케이션의 경계를 넘나드는 상호작용을 검증할때와 **이러한 상호 작용의 사이드 이펙트가 외부 환경에서 보일때만 동작과 관련**이 있다.

