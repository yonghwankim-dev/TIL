# CompletableFuture
## source code

## CompletableFuture 이해
### Future의 단점 및 한계
- 외부에서 완료시킬수 없고, get의 타임아웃 설정으로만 완료 가능
- 블로킹 코드(get)를 통해서만 이후의 결과를 처리할 수 있음
- 여러 Future를 조합할 수 없음 ex) 회원 정보를 가져오고, 알림을 발송하는 등
- 여러 작업을 조합하거나 예외 처리할 수 없음

### CompletableFuture 클래스
- Future 인터페이스를 기반으로 외부에서 완료시킬수 있음
- CompletionStage 인터페이스도 구현하고 있는데, CompletionStage는 작업들을 중지시키거나 완료후 콜백을 위해 추가되었음
  - 예를 들어 Future에서는 불가능했던 "몇 초 이내에 응답이 안오면 기본값을 반환한다"와 같은 작업이 가능해짐
- **외부에서 작업을 완료할 수 있고 콜백 등록 및 Future 조합 등이 가능함**

## CompletableFuture의 기능들 및 예시 코드
### Future의 단점 및 한계
- 비동기 작업 실행
- 작업 콜백
- 작업 조합
- 예외 처리

### 비동기 작업 실행
- runAsync
  - 반환값이 없는 경우
  - 비동기로 작업 실행 콜
- supplyAsync
  - 반환값이 있는 경우
  - 비동기로 작업 실행 콜

```java
class Test {
  @Test
  public void runAsync() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> System.out.println("Thread: " + Thread.currentThread().getName()));
    //when
    future.get();
    //then
    System.out.println("Thread: " + Thread.currentThread().getName());
  }
}
```
```
Thread: ForkJoinPool.commonPool-worker-3
Thread: main
```

runAsync는 반환값이 없으므로 Void 타입이고 위 실행 결과처럼 별도의 스레드에서 실행됨을 확인할 수 있습니다.

```java
class Test {
  @Test
  public void testcase3() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
    //when
    String actual = future.get();
    //then
    Assertions.assertThat(actual).isEqualTo("Hello");
  }
}
```

반면 suuplyAsync는 반환값이 있으므로 비동기 작업의 결과를 받을 수 있습니다.

### 작업 콜백
- thenApply
  - 반환 값을 받아서 다른 값을 반환함
  - 함수형 인터페이스 Function을 파라미터로 받음
- thenAccept
  - 반환 값을 받아서 처리하고 값을 반환하지 않음
  - 함수형 인터페이스 Consumer을 파라미터로 받음
- thenRun
  - 반환 값을 받지 않고 다른 작업을 실행함
  - 함수형 인터페이스 Runnable을 파라미터로 받음

```java
class Test{
  @Test
  public void testcase4() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
    //when
    future = future.thenApply(s -> s + " World");
    String actual = future.get();
    //then
    Assertions.assertThat(actual).isEqualTo("Hello World");
  }

  @Test
  public void testcase5() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
    //when
    CompletableFuture<Void> future2 = future.thenAccept(s -> System.out.println("Computation returned: " + s));
    //then
    future2.get();
  }

  @Test
  public void testcase6() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
    //when
    // "Hello" 계산 결과가 필요없이 이전 비동기 함수가 실행되고 난 이후 다른 함수를 호출하고 싶다면 thenRun 사용
    future.thenRun(()->System.out.println("Computation finished."));
    //then
    String actual = future.get();
    System.out.println(actual);
    Assertions.assertThat(actual).isEqualTo("Hello");
  }
}
```

### 작업 조합
- thenCompose
  - 두 작업이 이어서 실행하도록 조합하며, 앞선 작업의 결과를 받아서 사용할 수 있음
  - 함수형 인터페이스 Functio을 파라미터로 받음
- thenCombine
  - 두 작업을 독립적으로 실행하고, 둘다 완료되었을때 콜백을 실행함
  - 함수형 인터페이스 Function을 파라미터로 받음
- allOf
  - 여러 작업들을 동시에 실행하고, 모든 작업 결과에 콜백을 실행함
- anyOf
  - 여러 작업들 중에서 가장 빨리 끝난 하나의 결과에 콜백을 실행함

```java
class Test {
  @Test
  public void testcase7() throws ExecutionException, InterruptedException {
    //given
    // thenCompose는 매개변수를 입력받고 또다른 CompletableFuture 객체를 반환함
    // thenApply는 매개변수를 입력받고 매개변수와 같은 타입의 값을 반환함
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));
    //when
    String actual = future.get();
    //then
    Assertions.assertThat(actual).isEqualTo("Hello World");
  }
}
```

thenCompose는 앞선 결과("Hello")를 입력으로 받아서 다른 작업을 수행합니다.

```java
class Test {
  @Test
  public void testcase8() throws ExecutionException, InterruptedException {
    //given
    // CompletableFuture 객체의 get 메서드 호출시
    // supplyAsync에 들어간 함수와 thenCombine 첫번째 매개변수에 들어간 CompletableFuture 객체를 실행시키고 난 이후
    // thenCombine 두번째 매개변수의 함수를 실행시키고 반환값을 반환함
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello")
            .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> s1 + s2);
    //when
    String actual = future.get();
    //then
    Assertions.assertThat(actual).isEqualTo("Hello World");
  }
}
```

thenCombine은 앞선 결과와 상관없이 독립적으로 실행되고 두 비동기의 실행 결과를 입력으로 콜백을 수행합니다.

```java
class Test{
  @Test
  public void testcase12() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
    CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
    CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");
    //when
    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);
    //then
    combinedFuture.get();
    Assertions.assertThat(future1.isDone()).isTrue();
    Assertions.assertThat(future2.isDone()).isTrue();
    Assertions.assertThat(future3.isDone()).isTrue();
  }
}
```

allOf는 여러 작업들(future1, future2, future3)을 동시에 실행하도록 묶습니다.

반환 값을 받아야 한다면 다음과 같이 할수 있습니다.

```java
class Test{
  @Test
  public void testcase13() {
    //given
    CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
    CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
    CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");
    //when
    String actual = Stream.of(future1, future2, future3)
            .map(CompletableFuture::join)
            .collect(Collectors.joining(" "));
    //then
    Assertions.assertThat(actual).isEqualTo("Hello Beautiful World");
  }
}
```

### 예외 처리
- exceptionally
  - 발생한 에러를 받아서 예외를 처리함
  - 함수형 인터페이스 Function을 파라미터로 받음
- handle, handleAsync
  - (결과값, 에러)를 반환받아 에러가 발생한 경우와 아닌 경우 모두를 처리할 수 있음
  - 함수형 인터페이스 BiFunction을 파라미터로 받음

```java
class Test {
  @Test
  public void testcase16() throws ExecutionException, InterruptedException {
    //given

    //when
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      if (true) {
        throw new IllegalStateException("call exception");
      }
      return "Thread: " + Thread.currentThread().getName();
    }).exceptionally(Throwable::getMessage);
    //then
    String actual = future.get();
    Assertions.assertThat(actual).isEqualTo("java.lang.IllegalStateException: call exception");
  }
}
```

```java
class Test {
  @Test
  public void testcase17() throws ExecutionException, InterruptedException {
    //given
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      if (true) {
        throw new IllegalStateException("call exception");
      }
      return "Thread: " + Thread.currentThread().getName();
    }).handle((result, e) -> e == null ? result : e.getMessage());
    //when
    String actual = future.get();
    //then
    Assertions.assertThat(actual).isEqualTo("java.lang.IllegalStateException: call exception");
  }
}
```