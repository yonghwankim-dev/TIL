# docker-compose
### docker-compose 개념
Docker Compose는 YAML 파일을 사용하여 여러 컨테이너를 정의합니다. YAML 파일에서는 각 컨테이너의 이미지, 포트 번호, 환경 변수, 데이터 볼륨 등을 정의할 수 있습니다. 또한 컨테이너 간의 연결을 설정할 수 있으며, 이를 통해 애플리케이션 사용하는 다양한 서비스를 함께 구동시킬 수 있습니다.

### docker-compose 장점
Docker Compose를 사용하면 컨테이너 애플리케이션을 구성하는 데 필요한 모든 구성요소를 하나의 YAML 파일로 정의 할 수 있으므로, 배포 및 관리 과정이 훨씬 간편해집니다.

### docker-compose 설치
```shell
$ sudo apt-get install docker-compose
```

### docker-compose를 이용하여 mysql server 컨테이너 생성
1. "docker-compose.yml" 파일 생성
```yaml
version: "3" # 파일 규격 버전, 최신버전: 3.x.x

# 이 항목 밑에 실행하려는 컨테이너 들을 정의
services:
  # 서비스 명
  db:
    # 사용할 이미지
    image: a4ad24fe52cd
    # 컨테이너 이름 설정
    container_name: my-mysql
    # 접근 포트 설정 (컨테이너 외부:컨테이너 내부)
    ports:
      - "3306:3306"
    # -e 옵션
    environment:
      # MYSQL 패스워드 설정 DHQTUS
      MYSQL_ROOT_PASSWORD: "root"
      MYSQL_DATABASE: mydb
      MYSQL_USER: user1
      MYSQL_PASSWORD: user1
      LC_ALL: "C.UTF-8"
    # 명령어 실행 (인코딩 설정)
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    volumes:
      # -v 옵션 (디렉토리 마운트 설정)
      - /home/yonghwan/CS14:/home
      - /home/yonghwan/CS14/init.sql:/docker-entrypoint-initdb.d/init.sql
```

2. yml 파일을 생성한 디렉토리에서 docker-compose 명령어 실행
```shell
$ docker-compose up -d
```

3. 컨테이너 생성 확인
```shell
$ docker ps -a
CONTAINER ID   IMAGE                    COMMAND                   CREATED          STATUS                            PORTS
                        NAMES
7d612b294c58   a4ad24fe52cd             "/entrypoint.sh --ch…"   49 seconds ago   Up 3 seconds (health: starting)   0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp   my-mysql
```