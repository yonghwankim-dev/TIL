
## 4.1 좋은 단위 테스트의 4대 요소 자세히 살펴보기
좋은 단위 테스트 4대 요소
- 회귀 방지
- 리팩토링 내성
- 빠른 피드백
- 유지 보수성

### 4.1.1 첫번재 요소 : 회귀 방지
여기서 **회귀라는 것은 소프트웨어에 기능 추가, 버그 수정, 리팩토링과 같은 작업을 수행하였을때 소프트웨어에 기능이 고장나는 현상**을 의미한다.
좋은 단위 테스트는 "회귀 방지" 특성을 가지고 있는데, 좋은 단위 테스트를 작성할수록 소프트웨어에 버그가 발생하는 것을 줄인다.

다음 예시는 코드량이 적어서 회귀 오류가 많이 발생하지 않는 경우입니다.
```java
class User{
	private String name;
	
	// getter, setter
}
```

위 예시와는 다르게 코드량이 많거나 코드가 복잡하거나, 코드의 도메인 유의성(실질적으로 가치가 있음) 부분이 많을수록 회귀 오류가 많이 발생한다.

**회귀 방지를 최대화 하기 위해서는 테스트가 가능한 한 많은 코드를 실행하는 것을 목표로 해야 한다.**

### 4.1.2 두번째 요소 : 리팩토링 내성
**리팩토링 내성이 낮다는 것은 소프트웨어의 기능은 정상 작동하는데, 테스트는 실패했다고 뜨는 특성**입니다.

반대로 리팩토링 내성이 높다는 것은 소프트웨어의 내부 구현을 변경했음에도 기능도 정상 작동하고 테스트도 성공하는 특성입니다.

리팩토링이란 소프트웨어의 동작을 수정하지 않고 기존 내부 코드를 변경하는 작업을 의미합니다. 예를 들어 코드 조각을 새로운 메서드 또는 클래스로 추출하는 작업 등이 있습니다.

**거짓 양성(False Positive)**
- 허위 경보
- 기능이 의도대로 정상 작동하지만 테스트 결과는 실패로 나타나는 현상
- 리팩토링 작업을 수행한후 종종 발생함

좋은 단위 테스트는 리팩토링 내성이 높아야 한다. 좋은 단위 테스트일수록 리팩토링 내성이 높아서 거짓 양성 현상(기능 정상 작동하지만 테스트는 실패하는 현상)이 적다.

**거짓 양성이 적을수록 좋은점**
- 기존 기능이 고장 났을때 테스트가 조기 경고(테스트 실패)를 제공한다. 
	- 기능도 고장나고 테스트도 실패하는 경우
	- 테스트가 빨리 실패하기 때문에 배포전에 문제를 해결할 수 있음
- 코드의 변경이 회귀로 이어지지 않을거라고 확신하게 된다.
	- 회귀로 어이지는 경우는 기능은 실패하는데, 테스트는 통과하는 경우

**거짓 양성이 높을수록 않좋은점**
- 기능은 정상 작동하는데 테스트가 실패해서 해당 테스트를 무시(또는 비활성화)하기 시작하면서 실제 기능 고장이 발생해도 운영 환경에 들어가게 된다.
- 테스트에 대한 신뢰도가 떨어지고, 안정망으로 인식하지 못하게 된다. 이는 리팩토링 작업 횟수 감소로 이어진다.


### 4.1.3 무엇이 거짓 양성의 원인인가?
**테스트 코드와 테스트 대상 시스템(SUT)의 구현 세부 사항과 많이 결합되어 있을수록** 거짓 양성이 많이 발생한다.

**거짓 양성을 줄이는 방법**
- 테스트 코드를 테스트 대상 시스템(SUT)의 구현 세부 사항과 분리하기
- SUT가 제공하는 최종 결과를 검증하고 최종 사용자에게 의미 있는 결과만 확인하기

다음 예제 코드는 거짓 양성을 발생시키는 소스 코드 및 테스트 코드입니다.
```java
class MessageRendererTests {  
  
    static class Message{  
       private final String header;  
       private final String body;  
       private final String footer;  
  
       public Message(String header, String body, String footer) {  
          this.header = header;  
          this.body = body;  
          this.footer = footer;  
       }  
    }  
  
    interface IRenderer{  
       String render(Message message);  
    }  
  
    static class MessageRenderer implements IRenderer{  
  
       private final List<IRenderer> subRenderers;  
  
       public MessageRenderer(List<IRenderer> subRenderers) {  
          this.subRenderers = subRenderers;  
       }  
  
       @Override  
       public String render(Message message) {  
          return subRenderers.stream()  
             .map(x->x.render(message))  
             .collect(Collectors.joining(""));  
       }  
  
       public List<IRenderer> getSubRenderers() {  
          return subRenderers;  
       }  
    }  
  
    static class HeaderRenderer implements IRenderer{  
  
  
       @Override  
       public String render(Message message) {  
          return String.format("<head>%s</head>", message.header);  
       }  
    }  
  
    static class BodyRenderer implements IRenderer{  
  
  
       @Override  
       public String render(Message message) {  
          return String.format("<body>%s</body>", message.body);  
       }  
    }  
  
    static class FooterRenderer implements IRenderer{  
  
  
       @Override  
       public String render(Message message) {  
          return String.format("<footer>%s</footer>", message.footer);  
       }  
    }  
  
  
    // bad case  
    @Test  
    void messageRenderer_uses__correct_sub_renders(){  
       // given  
       IRenderer headerRenderer = new HeaderRenderer();  
       IRenderer bodyRenderer = new BodyRenderer();  
       IRenderer footerRenderer = new FooterRenderer();  
       List<IRenderer> subRenders = List.of(  
          headerRenderer,  
          bodyRenderer,  
          footerRenderer  
       );  
       MessageRenderer sut = new MessageRenderer(subRenders);  
       // when  
       List<IRenderer> renderers = sut.getSubRenderers();  
       // then  
       // 최종 결과는 동일하나 내부 호출순서만 변경되어도 테스트는 실패하게 되어있음. (거짓 양성)  
       Assertions.assertThat(renderers)  
          .hasSize(3)  
          .containsExactly(headerRenderer, bodyRenderer, footerRenderer);  
    }  
}
```

**문제점**
- 하위 렌더링 클래스가 예상하는 특정 타입이고 올바른 순서로 포함되어 있는지 검증하고 있음

**거짓 양성이 발생하는 시나리오1**
1. BodyRenderer 객체를 동일한 기능을 수행하는(String 결과물이 동일함) BoldRenderer 객체로 변경한다.
2. 애플리케이션의 기능은 정상 작동(결과물이 동일함)하지만 테스트 코드는 타입이 달라서 테스트는 실패한다.


위 시나리오에서 거짓 양성이 발생하는 원인은 **테스트가 SUT(MessageRenderer)가 생성한 결과물이 아니라 SUT의 구현 세부사항(subRenderers 리스트)과 결합**되어 있기 때문입니다.

위와 같은 테스트는 구현의 세부사항과 많이 결합되어 있기 때문에 MessageRenderer 클래스를 대상으로 리팩토링 작업을 수행하면 기능은 정상 작동되어도 테스트가 실패할 가능성이 높다.

**거짓 양성 발생시 안좋은점**
- 회귀 발생시 조기 경고를 제공하지 않는다. 대부분 잘못된 것이기 때문에 이러한 경고는 무시하게 된다.
- 리팩토링 작업에 대한 의지를 방해한다.

### 4.1.4 구현 세부 사항 대신 최종 결과를 목표로 하기
테스트 코드가 SUT의 내부 구현사항과의 결합도를 떨어트리고 테스트가 SUT의 최종 결과물만을 검증해야 한다.

예를 들어 이전 예제에서 MessageRenderer의 최종 결과물은 HTML 코드가 됩니다. 테스트는 MessageRenderer의 리스트의 개수 및 구성 요소가 아니라 HTML 코드를 검증해야 합니다. 변경된 테스트 코드는 다음과 같습니다.
```java
@Test  
void rendering_a_message(){  
    // given  
    List<IRenderer> subRenderers = List.of(  
       new HeaderRenderer(),  
       new BodyRenderer(),  
       new FooterRenderer()  
    );  
    IRenderer sut = new MessageRenderer(subRenderers);  
    Message message = new Message("h", "b", "f");  
    // when  
    String html = sut.render(message);  
    // then  
    String expected = "<head>h</head><body>b</body><footer>f</footer>";  
    Assertions.assertThat(html).isEqualTo(expected);  
}
```

다음 그림을 보면 위 테스트 코드를 왼쪽과 같이 표현한 것이고, 오른쪽은 거짓 양성이 발생할 수 있도록 표현한 것입니다. 단위 테스트를 작성할때는 왼쪽과 같이 최종 결과물만을 검증하도록 해야 합니다.
![](imgs/Pasted%20image%2020260908153813.png)

## 4.2 첫번째 특성과 두번째 특성 간의 본질적인 관계
첫번째 특성은 회귀 방지이고 두번째 특성은 리팩토링 내성입니다. 

### 4.2.1 테스트 정확도 극대화
다음 표는 기능과 테스트 결과의 오류 유형 표입니다.
- 기능 정상 작동 / 테스트 결과 통과 : Good Case입니다.
- 기능 정상 작동 / 테스트 결과 실패 : 거짓 양성 Case입니다. 리팩토링 내성이 낮음.
- 기능 작동 고장 / 테스트 결과 통과 : 회귀 방지 Case입니다. 회귀 방지가 낮습니다.
- 기능 작동 고장 / 테스트 결과 실패 : Good Case입니다.
![](imgs/Pasted%20image%2020260908154050.png)

회귀 방지와 리팩토링 내성은 테스트 스위트의 정확도를 극대화하는 것을 목표로 합니다. 정확도 지표 구성은 다음과 같습니다.
- 테스트가 버그 있음을 얼마나 잘 나타내는가?(거짓 음성 제외)
- 테스트가 버그 없음을 얼마나 잘 나타내는가(거짓 양성 제외)

저자가 말하고 싶은 것
1. 테스트의 목적은 올바른 신호(참 양성/ 참 음성)만 남기는 것
2. 거짓 양성, 거짓 음성은 서로 다른 치명적인 위험을 만든다.
	- 거짓 음성 : 테스트가 통과되어 운영환경에 배포되었는데 실제 기능에 오류가 발생함
	- 거짓 양성 : 기능은 통과되는데 테스트가 실패하게 되어 리랙토링 작업의 의지를 떨어트림
3. 테스트 정확도는 소음을 줄이는 수학적 곱셈 관계다
	- `테스트 정확도 = 회귀 방지 x 리팩토링 내성`
	- 회귀 방지나 리팩토링 내성 중 하나라도 0에 수렴하면 테스트 전체의 정확도는 0에 가까워져서 정확도가 낮아진다.



### 4.2.2 거짓 양성과 거짓 음성의 중요성 : 역학 관계
좋은 단위 테스트를 구축하려면 버그를 잘 잡아내는 것(거짓 음성 최소화)만으로는 부족하다. 리팩토링 작업을 할때 허위 경보를 울리지 않는것(거짓 양성 최소화) 또한 동등한 비중으로 관리해야 지속 가능한 소프트웨어를 만들 수 있다.

## 4.3 세번째 요소와 네번째 요소 : 빠른 피드백과 유지 보수성
**빠른 피드백**
빠른 피드백 요소는 테스트를 빠르게 실행하고 결과를 빠르게 받아서 오류를 빠르게 고치는 것을 의미합니다. 테스트를 실행하고 결과를 보고 오류를 고치는 이러한 순환 과정을 빠르게 해서 오류를 수정하는 비용을 0으로 수렴시킵니다.

**유지 보수성**
테스트 코드가 작고 테스트를 실행하기 쉬울수록 유지비 보수성이 높아진다.

**유지 보수성 구성**
1. 테스트가 얼마나 이해하기 어려운가?
	- 테스트는 코드 라인이 적을수록 더 읽기 쉽다. 테스트 코드가 작을수록 유지비가 적게 든다.
	- 테스트 코드의 품질을 제품 코드만큼 중요하게 여겨라
2. 테스트가 얼마나 실행하기 어려운가?
	- 테스트가 프로세스 외부 종속성으로 작동하면, 데이터베이스 서버를 재부팅하고, 네트워크 연결 문제를 해결하는 등의 의존성을 상시 운영하는데 시간을 들여야 한다.

## 4.4 이상적인 테스트를 찾아서
### 4.4.1 이상적인 테스트를 만들 수 있는가?
회귀 방지, 리팩토링 내성, 빠른 피드백은 상호 배탁적이다. 3가지 특성 모두를 최대로 하는 것은 불가능하다. 3가지중 1가지를 포기해야 나머지 2개를 극대화 할 수 있다.

4가지 특성 중에서 한가지라도 0점을 받는 테스트는 가치가 없게 된다.

### 4.4.2 극단적인 사례 1: 엔드 투 엔드 테스트
엔드 투 엔드 테스트 특성
- 회귀 방지 높음
- 리팩토링 내성 높음
- **빠른 피드백 낮음**
![](imgs/Pasted%20image%2020260908161730.png)

### 4.4.3 극단적인 사례 2 : 간단한 테스트
간단한 테스트 특성
- **회귀 방지 낮음**
- 리팩토링 내성 높음
- 빠른 피드백 높음

### 4.4.3 극단적인 사례 3 : 깨지기 쉬운 테스트
깨지기 쉬운 테스트 특성
- 회귀 방지 높음
- **리팩토링 내성 낮음**
- 빠른 피드백 높음

다음은 회원을 등록할때 사용자의 이름을 데이터베이스에 대문자로 저장하는 예시입니다.
```java
public class UserService {
    private final UserRepository userRepository;
    private final StringFormatter stringFormatter;

    public UserService(UserRepository userRepository, StringFormatter stringFormatter) {
        this.userRepository = userRepository;
        this.stringFormatter = stringFormatter;
    }

    public void registerUser(String rawName) {
        // 내부 헬퍼 객체를 호출하여 대문자 처리
        String formattedName = stringFormatter.formatToUpper(rawName);
        userRepository.save(new User(formattedName));
    }
}

@Test
void registerUser_test_low_refactoring_resistance() {
    // Arrange
    UserRepository mockRepo = mock(UserRepository.class);
    StringFormatter mockFormatter = mock(StringFormatter.class);
    UserService sut = new UserService(mockRepo, mockFormatter);

    when(mockFormatter.formatToUpper("john")).thenReturn("JOHN");

    // Act
    sut.registerUser("john");

    // Assert: SUT의 '최종 결과'가 아니라 '내부 구현 단계(How)'를 검증
    verify(mockFormatter, times(1)).formatToUpper("john"); // 👈 거짓 양성의 원인!
}
```

문제점
- 테스트 코드가 StringFormatter를 검증하고 있음.
- 만약 요구사항 변경으로 StringFormatter가 아닌 리팩토링하여 String 클래스의 `toUpperCase()`를 사용하도록 변경하면 기능은 정상 작동하지만 테스트는 실패하게 된다.

### 4.4.5 이상적인 테스트를 찾아서 : 결론
3가지 특성을 모두 완벽한 점수를 얻어서 이상적인 테스트를 만드는 것은 불가능하다.

4번째 특성인 유지 보수성은 엔드 투 엔드 테스트를 제외하고 처음 3가지 특성과 상관없다. 엔드 투 엔드 테스트는 모든 의존성을 설정해야 하기 때문에 일반적으로 크기가 크다. 따라서 유지비 측면에서 더 비싼 경향이 있다.

**이상적인 테스트 해결 방법**
리팩토링 내성을 높히고, 회귀 방지 특성이나 빠른 피드백 특성 사이에서 선택적으로 절충한다.
![](imgs/Pasted%20image%2020260908162602.png)

**리팩토링 내성 포기 못하는 이유**
테스트에 리팩토링 내성이 있거나 없거나 둘중 하나를 선택해야 하고 중간 단계는 없기 때문이다. 그런데 리팩토링 내성을 포기하면 프로젝트가 커지면 커질수록 리팩토링 작업을 하기가 힘들어진다. 따라서 리팩토링 내성을 챙기고 회귀 방지나 빠른 피드백중 하나를 적절히 선택해서 단위 테스트를 작성해야 한다.
회귀 방지와 빠른 피드백 특징 같은 경우에는 리팩토링 내성과 달리 중간 조절이 가능하다.

## 4.5 대중적인 테스트 자동화 개념 살펴보기
### 4.5.1 테스트 피라미드 분해
하단인 단위 테스트 일수록 빠른 피드백 및 리팩토링 내성이 가능하다. 상단인 엔드 투 엔드 테스트일수록 회귀 방지 및 리팩토링 내성이 있다.
![](imgs/Pasted%20image%2020260908162919.png)
위 피라미드에서 어느 테스트도 리팩토링 내성을 포기하지 않는다.

다음 그림은 리팩토링 내성을 선택한 상태에서 회귀 방지와 빠른 피드백중 하나를 선택하는 그림입니다.
![](imgs/Pasted%20image%2020260908163501.png)

### 4.5.2 블랙박스 테스트와 화이트박스 테스트 간의 선택
**블랙박스 테스트**
테스트가 시스템의 내부 구조를 모르는 상태에서 최종 결과물만을 가지고 검증하는 방식이다.

**화이트박스 테스트**
테스트가 시스템의 내부 구조를 아는 상태에서 내부 작업을 검증하는 방식이다.

화이트박스 테스트와 블랙박스 테스트의 장단점
![](imgs/Pasted%20image%2020260908164155.png)

**테스트 선택**
리팩토링 내성을 포기할수 없기 때문에 테스트는 블랙박스 테스트를 선택하는 것이 좋다.

만약 알고리즘 복잡도가 높은 유틸리티 코드를 다루는 경우 화이트박스 테스트를 선택하는 것이 좋다.

