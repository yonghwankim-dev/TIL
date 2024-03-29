# 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

## 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지(자기사용) 문서로 남겨야 한다
클래스의 공개된 메서드A에서 클래스 자신의 다른 메서드B를 호출할 수 있습니다.

그런데 메서드 B가 재정의 가능한 메서드라면 메서드 A의 API설명에는 메서드 B를 호출한다고 작성해야 한다.

왜냐하면 메서드 A를 수행하기 위해서는 메서드 B의 수행이 필요한데 만약 메서드 B가 재정의 되면

메서드 A가 수행이 실패할 수 있기 때문입니다.

예를 들어 java.util.AbstractCollection의 remove 메서드는 수행하는데 iterator 메서드를 자기사용 합니다.

만약 iterator 메서드를 재정의하게 되면 remove 메서드는 예외가 발생할 것입니다.

## 상속용 클래스를 설계할때 protected 메서드 결정 기준
결론적으로 없습니다. 실제 하위 클래스를 만들어 시험해보는 것이 최선입니다. 

반드시 필요한 protected 메서드를 놓쳤다면 하위 클래스를 작성시 빈자리를 느낄 것입니다.

반대로 하위 클래스를 여러 개 만들 때까지 신경쓰이지 않는 protected 메서드는 private여야할 가능성이 높습니다.

이러한 하위 클래스는 3개 정도가 적당합니다.

상속용으로 설계한 클래스는 배포 전에 반드시 하위 클래스를 만들어 검증해야 합니다.

## 상속용 클래스의 생성자는 재정의 가능 메서드를 호출해서는 안된다
상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행되므로 하위 클래스에서 재정의한 메서드가 하위 클래스의 생성자보다 먼저 호출되기 때문입니다.

재정의한 메서드가 하위 클래스의 생성자에서 초기화하는 값에 의존한다면 의도대로 동작하지 않을 것입니다.

```java
public class Super {
    // 잘못된 예 - 생성자가 재정의 가능 메서드를 호출
    public Super() {
        overrideMe();
    }

    public void overrideMe() {

    }
}

public final class Sub extends Super {
    // 초기화되지 않은 final 필드. 생성자에서 초기화
    private String name;
    Sub() {
        name = "yonghwan";
    }

    @Override
    public void overrideMe() {
        System.out.println(name);
    }
    
    public static void main(String[] args){
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
```
```
null
yonghwan
``` 

위 실행 결과를 통해 알수 있는 사실은 상위 클래스의 생성자는 하위 클래스의 생성자가 인스턴스 필드를 초기화하기도 전에 overrideMe를 호출하여

null을 출력하게 되는 것입니다.

## 상속용으로 설계하지 않는 클래스는 상속을 금지하라
상속을 금지하는 방법
1. 클래스를 final로 선언
2. 모든 생성자를 private나 default로 선언하고 public 정적 팩토리를 만들기

핵심 기능을 정의한 인터페이스가 있고 클래스가 그 인터페이스를 구현했다면 상속을 금지해도 개발하는데 어려움이 없을 것입니다.

Set, List, Map이 좋은 예시입니다.

상속을 꼭 허용해야 한다면 **클래스 내부에서는 재정의 가능 메서드를 사용하지 않게 만들고** 이 사실을 문서로 남여햐 합니다.

