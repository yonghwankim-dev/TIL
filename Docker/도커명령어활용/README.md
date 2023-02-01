# 도커 명령어 활용
## 도커 이미지 명령어
### 도커 이미지 내려받기 : docker pull
- docker [image] pull [OPTIONS] name[:TAG | @IMAGE_DIGEST]
- docker pull 명령어 옵션
  - --all-tags, -a : 저장소에 태그로 지정된 여러 이미지를 모두 다운로드
  - --disable-content-trust : 이미지 검증 작업 스킵
  - --platform : 플랫폼을 지정하여 pull함
    - ex) --platform=linux
  - --quiet, -q : 이미지 다운로드 과정에서 화면에 나타나는 상세 출력 숨김

```shell
# 명시적으로 최신 버전 지정
$ docker pull debian:latest

# 이미지 식별 정보인 다이제스트 지정.
$ docker pull debian:sha256:28579893...258

# 도커 허브 레지스트리 명시적 지정
$ docker pull library/debian:latest
$ docker pull docker.io/library/debian:latest
$ docker pull index.docker.io/library/debian:latest

# 외부 레지스트리 주소를 이용하는 방법(예로 구글에서 제공하는 샘플 애플리케이션 이미지 지정)
# 주의할 것은 웹 주소 URL에서 도메인 주소의 시작인 http://를 붙이지 않고 이미지 주소를 써야함
$ docker pull gcr.io/google-samples/hello-app:1.0
```

#### 다이제스트(Digest)
- 도커 허브에서 관리하는 이미지의 고유 식별값
- 다이제스트 값을 포함한 이미지 조회 명령어는 **docker images --digests**
