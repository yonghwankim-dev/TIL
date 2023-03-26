# 그 이외의 헤더 필드

HTTP 헤더 필드는 독자적으로 확장할 수 있습니다.

그래서 웹서버와 브라우저 기능에 다양한 독자적인 헤더 필드가 존재합니다.

그 중에서 자주 사용되는 아래의 헤더 필드는 다음과 같습니다.

- X-Frame-Option
- X-XSS-Protection
- DNT
- P3P

## X-Frame-Option

```text
X-Frame-Option: DENY
```

X-Frame-Option 헤더 필드는 다른 웹 사이트의 플에ㅣㅁ에서 표시를 제어하는 HTTP 리스폰스 헤더입니다.

X-Frame-Option 헤더 필드는 클릭 재킹(click jacking)이라는 공격을 막는것을 목적으로 하고 있습니다.

### X-Frame-Option 헤더 필드 값 종류

- DENY : 거부
- SAMEORIGIN: Top-level-browsing-context가 일치하는 경우에만 허가합니다.
    - 예를 들면, http://jackr.jp/sample.html이 SAMEORIGIN을 지정하고 있던 경우 hackr.jp 상의 페이지를
      프레임에 읽어들이는 것은 가능하지만 example.com과 같은 다른 도메인의 페이지에서는 불가능합니다.

## X-XSS-Protection

```text
X-XSS-Protection: 1
```

X-XSS-Protection 헤더 필드는 크로스 사이트 스크립팅(XSS) 대책으로서 브라우저의 XSS 보호 기능을 제어하는 HTTP 리스폰스 헤더입니다.

### X-XSS-Protection 헤더 필드 값 종류

- 0 : XSS 필터를 무효화합니다.
- 1 : XSS 필터를 활성화합니다.

## DNT

```text
DNT: 1
```

DNT 헤더 필드는 Do Not Track(DNT)이라는 의미이며, 개인 정보 수집을 거부하는 의사를 알려주는 HTTP 리퀘스트 헤더입니다.

### DNT 헤더 필드 값 종류

- 0 : 트래킹 동의
- 1 : 트래킹 거부

## P3P

```text
P3P: CP="CAO DSP LAW CURa ADMa DEVa TAIa PSAa PSDa IVAa IVDa OUR BUS IND UNI COM NAV INT"
```

P3P 헤더 필드는 웹 사이트 상의 프라이버시 정책에 P3P(The Platform for Privacy Preferences)를 사용하는 것입니다.

프로그램이 읽을 수 있는 형태로 나타내기 위한 HTTP 리스폰스 헤더입니다.

### P3P 설정시 순서

- 순서-1 : P3P 정책 작성
- 순서-2 : P3P 정책 참조 파일을 작성해서 "/w3c/p3p.xml"에 배치
- 순서-3 : P3P 정책으로부터 콤팩트 정책을 작성하고 HTTP 리스폰스 헤더에 출력합니다.
