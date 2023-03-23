# 리퀘스트 헤더 필드

## Accept

```
Accpet: text/html, application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8
```

**Accpet 헤더 필드는 사용자 에이전트에서 처리할 수 있는 미디어 타입과 미디어 타입의 우선순위를 전달하기 위해 사용**됩니다.

예를 들어 브라우저가 PNG 이미지를 표시하지 못하는 경우에 Accept에 image/png를 지정하지 않고, image/gif와 image/jpg

등을 지정합니다.

Accept 헤더 필드의 정보

- 세미콜론 : 우선순위
- q= : 품질지수

미디어 타입의 종류

- 텍스트
    - text/html, text/plain, text/css ...
    - application/xhtml+xml, application/xml ...
- 이미지 파일
    - image/jpeg
    - image/gif
    - image/png
- 동영상 파일
    - video/mpeg, video/quicktime...
- 애플리케이션용 바이너리 파일
    - application/octet-stream
    - application/zip

## Accept-Charset

Accpet-Charset 헤더 필드는 유저 에이전트에서 처리할 수 있는 문자셋으로, 처리하고 싶은 문자셋의 우선순위를 전달하기 위해

사용됩니다.

```
Accept-Charset:iso-8859-5, unicode-1-1:q+0.8
```

위 예제를 보면 1순위로 iso-8859-5 문자셋으로 처리하고자 원하고 2순위로 unicode-1-1:q+0.8로 unicode-1-1 문자셋에

품질 지수는 0.8로 요구합니다.

## Accept-Encoding

Accept-Encoding 헤더 필드는 유저 에이전트가 처리할 수 있는 내용 압축방식과 우선순위를 전달하기 위해 사용됩니다.

콘텐츠 코딩 종류

- gzip
    - gzip에서 생성된 인코딩 포맷
- compress
    - 유닉스 파일 압축 프로그램 "compress"에서 만들어진 인코딩 포맷
- deflate
    - Zlib 포맷과 deflate 압축 알고리즘에 의해서 만들어진 인코딩 포맷
- identity
    - 압축과 변형을 하지 않는 디폴트 인코딩 포맷

## Accept-Language

**Accept-Language 헤더 필드는 유저 에이전트가 처리할 수 있는 언어와 언어간의 우선순위를 전달하기 위해서 사용**됩니다.

```
Accept-Language: ko-kr, en-us;q=0.7,en;q=0.3
```

위 예제의 경우 한국어 리소스가 있는 경우 한국어로 없으면 영어로 리스폰스를 받고 싶다는 것을 의미합니다.

## Authorization

Authorization 헤더 필드는 유저 에이전트의 인증 정보을 전달하기 위해서 사용됩니다.

예를 들어 클라이언트가 서버의 특정한 리소스를 요청하는 경우가 있습니다.

서버는 이 리소스는 HTTP 인증이 필요하다며 리스폰스에 인증을 요구합니다.

클라이언트는 인증 정보를 헤더 필드에 저장하여 전송합니다.

서버는 리소스를 전달합니다.

```
Authorization: Basic dWvub3NIbjpwYXNzd29yZA==
```

## Expect

Expect HTTP 요청 헤더는 요청을 적절하게 처리하기 위해 서버가 반환할 기대값을 나타냅니다.

명세에 정의된 유일한 기대값인 Expect: 100-continue에 대해, 서버는 다음과 같이 응답합니다:

100 헤더에 포함된 정보가, 즉시 성공으로 응답하기 충분할 때

417 (en-US) (Expectation Failed) 기대값을 충족하지 못했거나; 어쨌든 4xx 상태일 때

```
Expect: 100-continue
```

## From

From 헤더 필드는 유저 에이전트를 사용하고 있는 유저의 메일 주소를 전달합니다.

## Host

Host 헤더 필드는 리퀘스트한 리소스의 인터넷 호스트와 포트 번호를 전달합니다.

HTTP/1.1에서 필수 헤더 필드입니다. 필수인 이유는 1대의 서버에서 복수의 도메인을 할당할 수 있는 가상 호스트의 구조가

될 수 있기 때문입니다.

```
Host: www.hackr.jp
```

