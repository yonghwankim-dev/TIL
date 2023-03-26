# 엔티티 헤더 필드

엔티티 헤더 필드는 리퀘스트/리스폰스 메시지에 포함된 엔티티에 사용되는 헤더입니다.

엔티티 헤더 필드는 콘텐츠의 갱신 시간 같은 엔티티에 관한 정보를 포함합니다.

## Allow

```
Allow: GET, HEAD
```

Allow 헤더 필드는 Request-URI에 지정된 리소스가 제공하는 메소드를 알려줍니다.

서버가 제공하지 않는 메소드로 클라이언트가 요청하면 405 Method Not Allowed 리스폰스를 반환합니다.

## Content-Encoding

```text
Content-Encoding: gzip
```

Content-Encdoing 헤더 필드는 서버가 엔티티 바디에 대해서 한 콘턴츠 코딩 형식을 알려줍니다.

콘텐츠 코딩이란 엔티티의 정보가 누락되지 않도록 압축한 것입니다.

콘텐츠 코딩 형식

- Gzip
- Compress
- Deflate
- Identity

## Content-Language

```
Content-Language: en
```

Content-Language 헤더 필드는 엔티티 바디에 사용된 언어를 전달합니다.

예를 들어 위 헤더 필드 값은 en으로 엔티티 바디가 영어로 되어 있다는 의미입니다.

## Content-Length

```
Content-Length: 15000
```

Content-Length 헤더 필드는 엔티티 바디의 길이(단위: bytes)를 알려줍니다.

엔티티 바디에 전송 코딩이 실행되고 있는 경우에 Content-Length 헤더 필드를 사용하면 안됩니다.

## Content-Location

```
Content-Location: http://www.hackr.jp/index-ja.html
```

Content-Location 헤더 필드는 메시지 바디에 해당하는 URI를 알려줍니다.

Location 헤더 필드와의 차이는 Content-Location은 메시지 바디로 반환된 리소스의 URI를 나타냅니다.

## Content-MD5

```text
Content-MD5: OGFkZDUwNGVhNGY3N2MxMDlwZmQ4NTBmY2lyTY==
```

Content-MD5 헤더 필드는 메시지 바디가 변경되지 않고 도착했는지 확인하기 위해 MD5 알고리즘에 의해서 생성된 값을 전달합니다.

서버는 메시지 바디 -> MD5 알고리즘 -> Base64 인코딩 -> 헤더 필드에 저장합니다.

유효성을 확인하기 위해서 수신한 클라이언트 측에서 메시지 바디에 같은 MD5 알고리즘을 실행합니다.

이렇게 해서 얻은 도출한 값과 필드 값을 비교하여 메시지 바디가 올바른지 여부를 알 수 있습니다.

## Content-Range

```text
Content-Range: bytes 5001-10000/10000
```
Content-Range 헤더 필드는 범위를 지정해서 일부분만을 리퀘스트하는 Range 리퀘스트에 대해서 리스폰스를 할때에 사용됩니다.

## Content-Type
```text
Content-Type: text/html; charset=UTF-8
```
Content-Type 헤더 필드는 엔티티 바디에 포함된 오브젝트의 미디어 타입을 알려줍니다.

## Expires
```text
Expires: Wed, 04 Jul 2012 08:26:05 GMT
```
Expires 헤더 필드는 리소스의 유효 기한을 알려줍니다.

프록시도 이 리소스의 복사본을 유지하고 리퀘스트에 캐시로 응답합니다.

프록시의 리소스가 유효기간이 지나면 오리진 서버에 요청합니다.

## Last-Modified
```text
Last-Modified: Web, 23 May 2012 09:59:55 GMT
```
Last-Modified 헤더 필드는 리소스가 마지막으로 갱신된 날짜를 알려줍니다.
