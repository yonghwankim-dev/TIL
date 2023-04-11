# 그림으로 배우는 Http & Network Basic

## 1장 웹과 네트워크의 기본에 대해 알아보자

[1.1 웹은 HTTP로 나타낸다](HTTP는이렇게태어났고성장했다/README.md)

[1.2 HTTP는 이렇게 태어났고 성장했다](HTTP는이렇게태어났고성장했다/README.md)

- 웹은 지식 공유를 위해 고안되었다
- 웹이 성장한 시대
- 진보 안하는 HTTP

[1.3 네트워크의 기본은 TCP/IP](네트워크의기본은TCP-IP/README.md)

- TCP/IP는 프로토콜의 집합
- 계층으로 관리하는 TCP/IP
- TCP/IP 통신의 흐름

[1.4 HTTP와 관계가 깊은 프로토콜은 IP/TCP/DNS](IP_TCP_DNS/README.md)

- 배송을 담당하는 IP
    - IP, MAC, ARP
- 신뢰성을 담당하는 TCP
    - TCP, 쓰리웨이 핸드셰이킹

[1.5 이름 해결을 담당하는 DNS](DNS/README.md)

- DNS

[1.6 IP/TCP/DNS와 HTTP와의 관계](IP_TCP_DNS_HTTP관계/README.md)

[1.7 URI와 URL](URI_URL/README.md)

- URI는 리소스 식별자
    - URI, URL
- URL 포맷

## 2장. 간단한 프로토콜 HTTP

[2.1 HTTP는 클라이언트와 서버간에 통신을 한다](HTTP는클라이언트와서버간에통신/README.md)

[2.2 리퀘스트와 리스폰스를 교환하여 성립](HTTP는클라이언트와서버간에통신/README.md)

[2.3 HTTP는 상태를 유지하지 않는 프로토콜](HTTP_특징/README.md)

[2.4 리퀘스트 URI로 리소스를 식별](리퀘스트URI로리소스식별/README.md)

[2.5 서버에 임무를 부여하는 HTTP 메소드](HTTP_메소드/README.md)

[2.6 메소드를 사용해서 지시를 내리다](HTTP_메소드/README.md)

[2.7 지속 연결로 접속량을 절약](지속연결/README.md)

- 지속 연결
- 파이프라인화

[2.8 쿠키를 사용한 상태 관리](쿠키를이용한상태관리/README.md)

## 3장. HTTP 정보는 HTTP 메시지에 있다

[3.1 HTTP 메시지](HTTP메시지/README.md)

[3.2 리퀘스트 메시지와 리스폰스 메시지의 구조](HTTP메시지/README.md)

[3.3 인코딩으로 전송 효율을 높이다](HTTP메시지/README.md)

- 메시지 바디와 엔티티 바디의 차이
- 압축해서 보내는 콘텐츠 코딩
- 분해해서 보내는 청크 전송 코딩

[3.4 여러 데이터를 보내는 멀티 파트](HTTP메시지/README.md)

[3.5 일부분만 받는 레인지 리퀘스트](HTTP메시지/README.md)

[3.6 최적의 콘텐츠를 돌려주는 콘텐츠 네고시에이션]((HTTP메시지/README.md))

## 4장. 결과를 전달하는 HTTP 상태 코드

[4.1 상태 코드는 서버로부터 리퀘스트 결과를 전달한다](HTTP_상태코드/README.md)

[4.2 2xx 성공(Success)](HTTP_상태코드/README.md)

- 200 OK
- 204 No Content
- 206 Partial Content

[4.3 3xx 리다이렉트(Redirection)](HTTP_상태코드/README.md)

- 301 Moved Permanently
- 302 Found
- 303 See Other
- 304 Not Modified
- 307 Temporary Redirect

[4.4 4xx 클라이언트 에러(Client Error)](HTTP_상태코드/README.md)

- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden

[4.5 5xx 서버 에러(Server Error)](HTTP_상태코드/README.md)

- 500 Internal Server Error
- 503 Service Unavailable

**제 5장 HTTP와 연계하는 웹 서버**

[5.1 1대로 멀티 도메인을 가능하게 하는 가상 호스트](HTTP_연계웹서버/README.md)  
[5.2 통신을 중계하는 프로그램 : 프록시, 게이트웨이, 터널](HTTP_연계웹서버/README.md)

- 프록시
- 게이트웨이
- 터널

[5.3 리소스를 보관하는 캐시](HTTP_연계웹서버/README.md)

- 캐시는 유효기간이 있다
- 클라이언트 측에도 캐시가 있다

**제 6장 HTTP 헤더**  
6.1 [HTTP 메시지 헤더](HTTP_헤더/README.md)  
6.2 [HTTP 헤더 필드](HTTP_헤더/README.md)

- HTTP 헤더 필드는 중요한 정보를 전달한다
- HTTP 헤더 필드의 구조
- 4종류의 HTTP 헤더 필드
- HTTP/1.1 헤더 필드 일람
- HTTP/1.1 이외의 헤더 필드
- End-to-end 헤더와 Hop-by-hop 헤더

6.3 [HTTP/1.1 일반 헤더 필드](HTTP_일반헤더필드/README.md)

- Cache-Control
- Connection
- Date
- Pragma
- Trailer
- Transfer-Encoding
- Upgrade
- Via
- Warning

6.4 [리퀘스트 헤더 필드](HTTP_리퀘스트헤더필드/README.md)

- Accept
- Accept-Charset
- Accept-Encoding
- Accept-Language
- Authorization
- Expect
- From
- Host
- If-Match
- If-Modified-Since
- If-None-Match
- If-Range
- If-Unmodified-Since
- Max-Forwards
- Proxy-Authorization
- Range
- Referer
- TE
- User-Agent

[6.5 리스폰스 헤더 필드](HTTP_리스폰스헤더필드/README.md)

- Accept-Ranges
- Age
- ETag
- Location
- Proxy-Authenticate
- Retry-After
- Server
- Vary
- WWW-Authenticate

6.6 [엔티티 헤더 필드](HTTP_엔티티헤더필드/README.md)

- Allow
- Content-Encoding
- Content-Language
- Content-Length
- Content-Location
- Content-MD5
- Content-Range
- Content-Type
- Expires
- Last-Midified

6.7 [쿠키를 위한 헤더 필드](HTTP_쿠키헤더/README.md)

- Set-Cookie
- Cookie

6.8 [그 이외의 헤더 필드](HTTP_그이외의헤더필드/README.md)

- X-frame-Option
- X-XSS-Protection
- DNT
- P3P

**제 7장 웹을 안전하게 하는 HTTPS**  
7.1 [HTTP의 약점](HTTP_약점/README.md)

- 평문이기 때문에 도청 가능
- 통신 상대를 확인하지 않기 때문에 위장 가능
- 완전성을 증명할 수 없기 때문에 변조 가능

7.2 [HTTP + 암호화 + 인증 + 완전성 보호 = HTTPS](HTTPS/README.md)

- HTTP에 암호화와 인증과 완전성 보호를 더한 HTTPS
- HTTPS는 SSL의 껍질을 덮어쓴 HTTP
- 상호간에 키를 교환하는 공개키 암호화 방식
- 공개키가 정확한지 아닌지를 증명하는 증명서
- 안전한 통신을 하는 HTTPS의 구조

**제 8장 누가 액세스하고 있는지를 확인하는 인증**  
8.1 [인증이란?](인증/README.md)

8.2 [BASIC 인증](인증/README.md)

- BASIC 인증 수순

8.3 [DIGEST 인증](인증/README.md)

- DIGEST 인증 수순

8.4 [SSL 클라이언트 인증](인증/README.md)

- SSL 클라이언트 인증의 인증 수순
- SSL 클라이언트 인증은 2-factor 인증에서 사용된다
- SSL 클라이언트 인증은 이용하는데 비용이 필요하다

8.5 [폼 베이스 인증](인증/README.md)

- 인증의 대부분은 폼 베이스 인증
- 세션 관리와 쿠키에 의한 구현

**제 9장 HTTP에 기능을 추가한 프로토콜**  
9.1 [HTTP를 기본으로 하는 프로토콜](HTTP_추가_프로토콜/README.md)  
9.2 [HTTP의 병목 현상을 해소하는 SPDY](HTTP_추가_프로토콜/README.md)

- HTTP의 병목 현상
- SPDY 설계와 기능
- SPDY는 웹의 병목 현상을 해결하는가?

9.3 [브라우저에서 양방향 통신을 하는 WebSocket](HTTP_추가_프로토콜/README.md)

- WebSocket의 설계와 기능
- WebSocket 프로토콜

9.4 [등장이 기다려지는 HTTP/2.0](HTTP_추가_프로토콜/README.md)

9.5 [웹 서버 상의 파일을 관리하는 WebDAV](HTTP_추가_프로토콜/README.md)

- HTTP/1.1을 확장한 WebDAV
- WebDAV에서 추가된 메소드와 상태 코드

**제 10장 웹 콘텐츠에서 사용하는 기술**  
10.1 [HTML](웹_콘텐츠_사용_기술/README.md)

- 대부분 웹 페이지는 HTML로 되어 있다
- HTML 버전
- 디자인을 적용하는 CSS

10.2 [다이나믹 HTML]((웹_콘텐츠_사용_기술/README.md))

- 웹 페이지를 동적으로 변경하는 다이나믹 HTML
- HTML을 조작하기 쉽게 해주는 DOM

10.3 [웹 애플리케이션]((웹_콘텐츠_사용_기술/README.md))

- 웹을 사용해서 기능을 제공하는 웹 애플리케이션
- 웹 서버와 프로그램을 연계하는 CGI
- Java에서 보급된 서블릿

10.4 [데이터 송신에 이용되는 포맷이나 언어]((웹_콘텐츠_사용_기술/README.md))

- 범용적으로 사용할 수 있는 마크업 언어 XML
- 갱신 정보를 송신하는 RSS/Atom
- JavaScript에서 이용하기 쉽고 가벼운 JSON

**제 11장 웹 공격 기술**  
11.1 [웹 공격 기술](웹공격기술/README.md)

## References

- [그림으로 배우는 Http & Network Basic](http://www.yes24.com/Product/Goods/15894097)
