
## 3.1 단위 테스트를 구성하는 방법
### 3.1.1 AAA 패턴 사용
테스트 코드를 작성할때 **준비(Arrange), 실행(Action), 검증(Assert)** 라는 3가지 부분으로 나누어서 구현합니다. 예제는 다음과 같습니다.
```java
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
```

**AAA 패턴 구조**
- 준비(Arrange) : 테스트 대상 객체 생성과 실행시 필요한 매개변수를 준비한다.  이 책에서 설명하는 테스트 대상 객체를 **테스트 대상 시스템(SUT, System Under Test)** 라고 부른다.
- 실행(Action) : 테스트 대상 객체의 메서드 호출 및 매개변수를 전달한다. 반환값이 있으면 저장한다.
- 검증(Assert) : 반환값을 검증하거나, 테스트 대상 객체와 협력 객체의 최종 상태를 검증한다.

**Given-When-Then 패턴**
AAA 패턴과 유사하게 준비, 실행, 검증 구절을 Given, When, Then 구절로 나눈다. AAA, Given-When-Then 패턴 사이에 차이점은 없으며 Given-When-Then 패턴이 비기술자들에게 더 읽기 쉽다.

앞으로의 예제 코드의 주석에서는 Given-When-Then 패턴을 사용할 것입니다.

### 3.1.2 여러개의 준비, 실행, 검증 피하기
준비, 실행 또는 검증 구절이 여러개인 경우가 존재합니다. 예를 들어 다음과 같이 실행 2번, 검증 2번을 교차로 하는 경우가 있습니다.
```text
테스트 데이터 준비
=>
실행
=>
검증
=>
실행
=>
검증
```

위와 같은 경우에는 단위 테스트를 여러번 실행하기 때문에 단위 테스트가 아니라 통합 테스트입니다. 위와 같은 구조는 단위 테스트에서 피하는 것이 좋습니다. 이러한 경우가 발생하면 여러개의 단위 테스트로 쪼개는 것이 좋습니다.

### 3.1.3 테스트 내 if 문 피하기
단위 테스트에 `if` 구문이 들어있으면 안태 패턴이다. 이러한 경우 여러개의 단위 테스트로 나누세요.

### 3.1.4 각 구절은 얼마나 커야 하는가?
#### **준비 구절**
준비 구절이 다른 구절(실행, 검증)보다 가장 크다면 테스트 클래스 안에 비공개 메서드나 별도의 팩토리 클래스로 추출하는 것이 좋다.
준비 구절에서 코드 재사용에 도움 되는 두가지 패턴으로 **오브젝트 마더(Object Monther)** 와 **테스트 데이터 빌더(Test Data Builder)** 가 존재한다.

**오브젝트 마더(Object Monther)**
오브젝트 마더는 단위 테스트에서 테스트에 필요한 복잡한 객체(픽스처, Test Fixture)를 손쉽게 생성해주는 헬퍼 클래스 또는 팩토리 패턴입니다. 예제 코드는 다음과 같습니다.
```java
public record User(String name, int age, Level level, boolean isBlocked) {}

public class UserObjectMother { 
	// 기본 정상 사용자 
	public static User createDefaultUser() { 
		return new User("홍길동", 25, Level.BASIC, false); 
	} 
	// VIP 사용자 
	public static User createVipUser() { 
		return new User("이순신", 40, Level.VIP, false); 
	} 
	// 정지된 사용자 
	public static User createBlockedUser() { 
		return new User("차단유저", 30, Level.BASIC, true); 
	} 
}
```

**테스트 데이터 빌더(Test Data Builder)**
단위 테스트에서 복잡한 테스트 픽스처(객체)를 유연하고 가독성 있게 생성하기 위해 사용하는 디자인 패턴입니다.
```java
public class User {
    private final String name;
    private final int age;
    private final Level level;
    private final boolean isBlocked;

    public User(String name, int age, Level level, boolean isBlocked) {
        this.name = name;
        this.age = age;
        this.level = level;
        this.isBlocked = isBlocked;
    }
    // getters...
}

public class UserBuilder {
    // 테스트에 필요한 합리적인 기본값 설정
    private String name = "홍길동";
    private int age = 20;
    private Level level = Level.BASIC;
    private boolean isBlocked = false;

    // 빌더 진입점을 위한 정적 팩토리 메서드
    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this.userBuilderWith(b -> b.name = name); // 또는 this.name = name; return this;
    }

    public UserBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public UserBuilder withLevel(Level level) {
        this.level = level;
        return this;
    }

    public UserBuilder isBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
        return this;
    }

    // 최종적으로 도메인 객체 생성
    public User build() {
        return new User(name, age, level, isBlocked);
    }
}

@Test
void user_upgrade_test() {
    // Given: 기본값 기반에서 나이와 레벨만 명시적으로 변경하여 객체 생성
    User user = UserBuilder.aUser()
                    .withName("김철수")
                    .withLevel(Level.SILVER)
                    .build();

    // When & Then ...
}
```

#### 실행 구절
실행 구절은 한줄이어야 한다. 만약 2줄 이상이 되는 경우 SUT의 public 메서드에 문제가 있을 가능성이 있다. 
다음 예제 코드는 실행 구절이 2줄인 경우이다.
```java
@Test  
void purchase_succeeds_when_enough_inventory(){  
    // given  
    Store store = new Store();  
    Product shampoo = new Product("Shampoo");  
    store.addInventory(shampoo, 10);  
    Customer customer = new Customer();  
    // when  
    boolean success = customer.purchase(store, shampoo, 5);  
    store.removeInventory(success, shampoo, 5);  
    // then  
    Assertions.assertThat(success).isTrue();  
    Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);  
}
```

**실행 구절이 2줄 이상인 경우 문제점**
클라이언트 코드가 첫번째 메서드(purchase)만 호출하고 두번째 메서드(removeInventory)를 호출하지 않으면 재고 수량 부분에서 문제가 발생한다.

테스트 성공 자체는 문제 없지만, Customer 클래스의 상품 구매 API(purchase)에 문제가 존재한다는 것을 의미한다. 이 문제를 해결하기 위해서는 캡슐화가 필요하다. purchase 메서드를 구현시 Store의 재고 수량을 업데이트하는 구현이 필요하다.

### 3.1.5 검증 구절에는 검증문이 얼마나 있어야 하는가?
검증 구절은 여러개 있어도 상관없다. **단위 테스트의 단위는 동작에 대한 단위이지 코드의 단위는 아니다.** 단위 테스트는 하나의 동작에 대한 단위이다. 
그래도 검증 구절이 너무 커지는 것은 경계해야 한다. 예를 들어 SUT에서 반환된 객체 내에서 모든 속성을 검증하는 대신에 객체 클래스 내에 적절한 **동등 멤버(equality member)** 를 정의하는 것이 좋다. 그러면 단일 검증문으로 객체를 기대값과 비교할 수 있다. 예를 들어 java언어 같은 경우에는 클래스에 equals()와 hashCode() 메서드를 구현하여 객체를 동등 비교하도록 한다.

### 3.1.6 종료 단계는 어떤가?
준비, 실행, 검증 구절 외에 종료 구절을 추가로 입력하고 구분할 수 있다. 종료 구절을 사용하는 경우는 테스트에 의해서 작성된 파일을 삭제하거나, 데이터베이스 연결을 종료할때 종료 구절을 작성할 수 있다.

종료 구절은 일반적으로 별도의 메서드로 추출해서, 클래스 안에 모든 테스트에서 재사용된다.

하지만 **대부분의 단위 테스트에서는 종료 구절이 필요없다.** 외부 종속성이 없어서 사이드 이펙트가 없기 때문이다. 종료 구절은 통합 테스트의 영역이다.

### 3.1.7 테스트 대상 시스템 구별하기
SUT는 **테스트하는 대상 객체**을 의미합니다. 이 SUT 객체는 테스트에 대한 진입점을 제공합니다. SUT는 다른 의존성과 구분하는것이 중요합니다. 의존성으로는 SUT의 메서드 호출시 전달하는 매개변수나 SUT 객체 생성시 주입되는 필드 멤버 객체가 될수 있습니다.

테스트 코드 안에 변수의 이름을 SUT로 설정해서 테스트 대상을 찾는데 쉽게하세요. 예를 들어 다음 테스트 코드를 보면 sut의 이름을 가진 Calculator 객체가 SUT인 것을 쉽게 구분할 수 있습니다. 그리고 sum 메서드에 전달되는 `first`, `second` 값은 **의존성**이 됩니다.
```java
@Test  
void sum_of_two_numbers(){  
    // arrange  
    double first = 10;  
    double second = 20;  
    Calculator sut = new Calculator();  
    // action  
    double result = sut.sum(first, second);  
    // assert  
    Assertions.assertThat(result).isEqualTo(30);  
}
```

### 3.1.8 준비, 실행, 검증 주석 제거하기
AAA 패턴을 따르고 준비, 검증 구절에 빈줄을 추가하지 않아도 되는 테스트라면 구절 주석들을 제거하여도 좋습니다. 하지만 그렇지 않으면 주석을 제거하지 마세요.
다음 예제는 준비 구절에 모킹 작업으로 인해서 빈줄을 추가하여 주석을 제거하지 않은 테스트 코드입니다.
```java
@Test  
void sum_of_two_numbers_should_discount_price(){  
    // given  
    // 테스트에 필요한 데이터 및 객체 준비  
    double first = 10;  
    double second = 20;  
    DiscountPolicy mockDiscountPolicy = BDDMockito.mock(DiscountPolicy.class);  
  
    // 목 객체의 행위 정의(Stubbing) - 픽스처(Fixture) 생성과 역할이 다르므로 빈줄로 구분  
    BDDMockito.given(mockDiscountPolicy.getDiscountAmount(30))  
       .willReturn(5.0);  
  
    PriceCalculator sut = new PriceCalculator(mockDiscountPolicy);  
  
    // when  
    double result = sut.sum(first, second);  
    // then  
    Assertions.assertThat(result).isEqualTo(25);  
}
```

## 3.2 xUnit 테스트 프레임워크 살펴보기
- `xUnit`
- `NUnit`
- `MSTest`

기본적으로 xUnit을 선호한다.

## 3.3 테스트 간 테스트 픽스처 재사용
**테스트 픽스처(Test Fixture)**
테스트 픽스처는 테스트 실행 대상 객체을 의미한다.
- 테스트 픽스처는 정규 의존성으로써, **SUT로 전달되는 인수(필드 멤버, 메서드 매개변수)** 이다.
- SUT와 테스트 픽스처는 다른 개념

**테스트 픽스처 재사용 방법1 : `SetUp`에 픽스처(객체) 생성하기**
- 테스트 픽스처 및 SUT 객체를 필드 멤버로 선언하기
- 각각의 테스트 실행시 공백 생성자를 호출하는데 생성자에 테스트에 필요한 테스트 픽스처 및 SUT 객체 초기화하기
```java
class CustomerTests {  
  
    private final Store store; // 공통 테스트 픽스처(Fixture)  
    private final Customer sut;  
    private final Product shampoo;  
  
    /**  
     * 각각의 테스트 실행 이전에 생성자 호출된다.  
     */    CustomerTests() {  
       store = new Store();  
       shampoo = new Product("shampoo");  
       store.addInventory(shampoo, 10);  
       sut = new Customer();  
    }  
  
    static class Customer{  
  
       public boolean purchase(Store store, Product product, int quantity) {  
          if (store.getInventory(product) >= quantity){  
             store.removeInventory(product, store.getInventory(product) - quantity);  
             return true;  
          }  
          return false;  
       }  
    }  
  
    static class Store{  
  
       private final Map<Product, Integer> store = new HashMap<>();  
  
       public int getInventory(Product product) {  
          return store.getOrDefault(product, 0);  
       }  
  
       public void addInventory(Product product, int quantity) {  
          store.put(product, getInventory(product) + quantity);  
       }  
  
       public void removeInventory(Product product, int quantity){  
          if (getInventory(product) < quantity){  
             throw new IllegalStateException("not enough product inventory");  
          }  
          store.put(product, getInventory(product) - quantity);  
       }  
    }  
  
    static class Product{  
       private final String name;  
  
       public Product(String name) {  
          this.name = name;  
       }  
  
       @Override  
       public boolean equals(Object o) {  
          if (this == o)  
             return true;  
          if (!(o instanceof Product product))  
             return false;  
          return Objects.equals(name, product.name);  
       }  
  
       @Override  
       public int hashCode() {  
          return Objects.hash(name);  
       }  
    }  
  
    @Test  
    void purchase_succeeds_when_enough_inventory(){  
       // given  
  
       // when       
       boolean success = sut.purchase(store, shampoo, 5);  
       // then  
       Assertions.assertThat(success).isTrue();  
       Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);  
    }  
  
    @Test  
    void purchase_fails_when_not_enough_inventory(){  
       // given  
  
       // when       
       boolean success = sut.purchase(store, shampoo, 15);  
       // then  
       Assertions.assertThat(success).isFalse();  
       Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(10);  
    }  
}
```

단점
- 테스트간 결합도가 증가한다.
- 테스트 가독성이 떨어진다.

### 3.3.1 테스트 간의 높은 결합도는 안티 패턴이다.
이전 예제에서 생성자에 테스트 픽스처를 초기화하면 테스트 결합도가 높아진다는 단점을 가지고 있다. 예를 들어 Store 객체에 샴푸 상품에 대한 인벤토리 및 수량 추가시 수량을 10에서 15로 변경하면 어떤 테스트가 실패할 수 있다.

각각의 테스트는 다른 테스트에 영향을 주어서는 안되고, 독립적이어야 한다.

### 3.3.2 테스트 가독성을 떨어뜨리는 생성자 사용
테스트 픽스처를 초기화하는 코드를 준비 구절이 아닌 생성자에 정의하기 때문에 준비구절의 코드를 제거할 수 있었다. 하지만, 하나의 테스트만 봐서는 준비 구절이 비어있기 때문에 전체적인 맥락을 파악하기 힘들다.

### 3.3.3 더 나은 테스트 픽스처 재사용법
테스트 클래스에 **비공개 팩토리 메서드(private factory method)**를 정의하세요.
```java
@Test  
void purchase_succeeds_when_enough_inventory(){  
    // given  
    Product shampoo = new Product("shampoo");  
    Store store = createStoreWithInventory(shampoo, 10);  
    Customer sut = new Customer();  
    // when  
    boolean success = sut.purchase(store, shampoo, 5);  
    // then  
    Assertions.assertThat(success).isTrue();  
    Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);  
}  
  
@Test  
void purchase_fails_when_not_enough_inventory(){  
    // given  
    Product shampoo = new Product("shampoo");  
    Store store = createStoreWithInventory(shampoo, 5);  
    Customer sut = new Customer();  
    // when  
    boolean success = sut.purchase(store, shampoo, 15);  
    // then  
    Assertions.assertThat(success).isFalse();  
    Assertions.assertThat(store.getInventory(shampoo)).isEqualTo(5);  
}  
  
private Store createStoreWithInventory(Product product, int quantity){  
    Store store = new Store();  
    store.addInventory(product, quantity);  
    return store;  
}
```

**장점**
- 테스트가 다른 테스트에 영향을 주지 않음
- 준비 구절에 코드가 있기 때문에 가독성을 떨어트리지 않음

**테스트 픽스처 재사용 규칙 예외**
테스트 전부 또는 대부분에 사용되는 객체 같은 경우에는 테스트 픽스처를 생성자에서 생성할 수 있다. 대표적으로 데이터베이스 연결 객체 등이 있다.

## 3.4 단위 테스트 명명법
도움이 되지 않는 테스트 명명법
```
[테스트 대상 메서드]_[시나리오]_[예상 결과]
```

Bad Case
```
void sum_of_two_numbers()
```

Better Case
```
sum_twoNumbers_returnSum
```

### 3.4.1 단위 테스트 명명 지침
- 엄격한 명명 정책 따르지 않는다. 
- 비개발자들에게 시나리오를 설명하는 것처럼 테스트 이름을 짓는다. 도메인 전문가나 비즈니스 분석가가 좋은 예시
- 단어를 밑줄(_)로 구분한다. 긴 이름 같은 경우에는 가독성을 향샹시킨다.

### 3.4.2 예시: 지침에 따른 테스트 이름 변경
**엄격한 정책으로 명명한 테스트 예제 코드**
```java
@Test  
void isDeliveryValid_invalidDate_returnFalse(){  
    // ...  
}
```

개선안
- 이름이 프로그래머가 아닌 사람들에게 납득되고, 프로그래머도 쉽게 이해할 수 있도록 작성하기
- SUT의 메서드 이름은 더이상 테스트명에 포함시키지 않기

```java
@Test  
void delivery_with_a_past_date_is_invalid(){  
    // ...
}
```

## 3.5 매개변수화된 테스트 리팩토링하기
테스트 코드 로직은 동일한데 값만 변경해서 검증해야 하는 경우가 있습니다. 이러한 경우 매개변수화된 테스트를 사용합니다.
```java
class DeliveryServiceTests {  
  
    public static Stream<Arguments> provideDeliveryDateSource() {  
       return Stream.of(  
          Arguments.of(-1, false),  
          Arguments.of(0, false),  
          Arguments.of(1, false),  
          Arguments.of(2, true)  
       );  
    }  
  
    static class DeliveryService{  
  
       // 배송일이 현재 시간 + 2일인지 체크  
       public boolean isDeliveryValid(LocalDateTime now, Delivery delivery) {  
          LocalDateTime minimumValidDate = now.plusDays(2);  
          return !delivery.date.isBefore(minimumValidDate);  
       }  
    }  
  
    static class Delivery{  
       private final LocalDateTime date;  
  
       public Delivery(LocalDateTime date) {  
          this.date = date;  
       }  
    }  
  
    @ParameterizedTest  
    @MethodSource(value = {"provideDeliveryDateSource"})  
    void can_detect_an_invalid_delivery_date(int daysFromNow, boolean expected){  
       // given  
       DeliveryService sut = new DeliveryService();  
       LocalDateTime now = LocalDateTime.of(2026, 9, 7, 12, 0);  
       LocalDateTime pastDate = now.plusDays(daysFromNow);  
       Delivery delivery = new Delivery(pastDate);  
       // when  
       boolean isValid = sut.isDeliveryValid(now, delivery);  
       // then  
       Assertions.assertThat(isValid).isEqualTo(expected);  
    }  
}
```


## 3.6 검증문 라이브러리를 사용한 테스트 가독성 향상
- C# : Fluent Assertions
- Java : AssertJ

Before(C#, Assert)
```
Assert.Equal(30, result)
```

After(C#, Fluent Assertions)
```java
result.Should().Be(30)
```

---

Before(Java, Assertions)
```java
org.junit.jupiter.api.Assertions.assertEquals(expected, isValid);
```

After(Java, AssertJ)
```java
Assertions.assertThat(isValid).isEqualTo(expected);
```

## 요약
- 모든 단위 테스트는 AAA(준비, 실행, 검증) 패턴을 따라야 한다.
- 실행 구절은 한 줄이어야 한다.
- SUT의 변수 이름을 sut로 설정해서 테스트에서 구별하자.
- SUT의 정규 의존성은 SUT 실행시 전달되는 인수(필드 멤버, 매개변수)입니다.
- 구절 사이에 준비, 실행, 검증 주석을 추가해서 구분하자.
	- given-when-then 주석
- 테스트 픽스처 초기화 코드는 생성자에 두지말고 팩토리 메서드로 추출해서 재사용하자.
- 테스트 이름 작명시 밑줄 표시(_)로 단어를 구분하자.
- 테스트 이름 작명시 테스트 대상 메서드 이름을 넣지 말자.
- 테스트 이름 작명시 비개잘자들에게 시나리오를 설명하는 것처럼 작성하자.

