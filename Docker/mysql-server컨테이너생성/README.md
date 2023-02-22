# mysql-server 컨테이너 생성
이 글에서는 docker를 사용하여 mysql-server 5.7 이미지를 가지고 컨테이너를 생성하고

mysql의 character-set을 설정하고 데이터 베이스 생성, 테이블 생성, 유저 생성, 데이터 초기화와

같은 환경 설정을 예제로 소개합니다.

## mysql server 5.7 설치
**1. docker 명령어로 mysql-server 이미지 다운로드**
```shell
$ docker pull mysql/mysql-server:5.7
```

**2. docker images 명령어로 mysql-server 이미지 확인**
```shell
$ docker images
```

![image](https://user-images.githubusercontent.com/33227831/220002278-c5e10708-3a18-451a-831e-40053336b8e9.png)

**3. mysql-server 컨테이너 생성 및 실행**
```shell
$ docker run --name mysql-container \
-e MYSQL_ROOT_PASSWORD=root \
-d -p 3306:3306 mysql/mysql-server:5.7
```
- --name : 컨테이의 이름 설정
- -d : 컨테이너를 백그라운드에서 실행
- -e : 환경변수(PASSWORD) 설정
- -p : 포트 설정(외부포트 : Docker 내부 포트)

**4. mysql-server 컨테이너 실행 확인**
```shell
$ docker ps
CONTAINER ID   IMAGE                    COMMAND                  CREATED         STATUS                            PORTS
                          NAMES
0a7316854209   mysql/mysql-server:5.7   "/entrypoint.sh mysq…"   4 seconds ago   Up 2 seconds (health: starting)   0.0.0.0:3306->3306/tcp, :::3306->
```

위 실행 결과를 보면 컨테이너의 STATUS가 UP인것을 볼 수 있습니다.

## mysql server에 컨테이너 안에서 연결하여 접속하기
### 백그라운드에서 실행중인 mysql-container 실행
```shell
$ docker exec -it mysql-container bash
```
- -it : 두 옵션은 컨테이너를 종료하지 않은 체로 터미널의 입력을 계속해서 컨테이너로 전달할 수 있습니다.

### MySQL 관리자로 접속
```shell
bash-4.2 # mysql -u root -p
Enter password:
mysql>
```

### 데이터베이스 조회
```shell
mysql> show databases
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.00 sec)
```

## ubuntu locale 설정
우분투 운영체제에서 한글을 읽고 쓰기 위해서 locale 설정을 한글로 변경합니다.
```shell
$ sudo apt update
$ sudo apt upgrade
$ sudo apt install language-pack-ko
$ sudo locale-gen ko_KR.UTF-8
$ sudo update-locale LANG=ko_KR.UTF-8 LC_MESSAGES=POSIX
```
ssh 재접속, 또는 재부팅 후 locale 확인
```shell
$ locale
```

![image](https://user-images.githubusercontent.com/33227831/220017842-c9e55569-3477-405b-a0c7-b369f515e829.png)

## container locale 및 mysql config 설정

1. 컨테이너 생성 및 실행
```shell
$ docker run --name mysql-container \
-e MYSQL_ROOT_PASSWORD=root \
-e LC_ALL=C.UTF-8 \
-d -p 3306:3306 mysql/mysql-server:5.7 \
--character-set-server=utf8mb4 \
--collation-server=utf8mb4_unicode_ci
```
- -e MYSQL_ROOT_PASSWORD=root : mysql 데이터베이스 root 계정의 비밀번호를 root로 설정
- -e LC_ALL=C.UTF-8 : 컨테이너의 locale를 C.UTF-8로 설정
- --character-set-server=utf8mb4 : 문자와 인코딩의 집합을 utf8mb4 방식으로 설정
- --collation-server=utf8mb4_unicode_ci : 문자간 정렬 방식을 utf8mb4_unicode_ci 방식으로 설정

2. 컨테이너 연결
```shell
$ docker exec -it mysql-container bash
```

3. mysql root 계정에 접속
```shell
bash > mysql -u root -p 
Enter Password:
mysql >
```

4. mysql의 Character Set 확인
```shell
mysql > status
```

![image](https://user-images.githubusercontent.com/33227831/220244075-44b472ef-3fe0-4df9-9793-a1d63790a302.png)

## scp
scp 명령어는 호스트 컴퓨터에서 원격 컴퓨터로 파일을 전송하는 명령어입니다.

### 일반적인 파일 전송
```shell
$ scp (전송할파일) (아이디@전송할 서버주소):(저장될 서버의 디렉토리)
$ scp ./sampleUser.csv yonghwan@localhost:/home
```

### ssh 포트 번호가 22번(기본)이 아닌 경우
```shell
$ scp -P 22 (전송할파일) (아이디@전송할 서버주소):(저장될 서버의 디렉토리)
$ scp -P 22 ./sampleUser.csv yonghwan@localhost:/home
```

### 디렉토리를 전송
```shell
$ scp -r (전송할디렉토리) (아이디@전송할 서버주소):(저장될 서버의 디렉토리)
$ scp -r ./local yonghwan@localhost:/home
```

## LOAD DATA INFILE
텍스트 파일을 읽어서 데이터를 테이블에 삽입하는 명령어입니다. 기본 INSERT보다 20배정도 빠릅니다.

### 명령어 형식
```sql
LOAD DATA LOCAL INFILE '{file_name}'
INTO TABLE {table_name}
CHARACTER SET utf8
FIELDS
  TERMINATED BY '{field_terminator}' # 각 필드 구분 문자
IGNORE 1 LINES # 제목이 포함된 첫번째 줄은 생략
(col1, col2, ...) # 컬럼명
```

### 명령어 예제
```sql
LOAD DATA LOCAL INFILE 'sampleUser.csv'
INTO TABLE user_log
CHARACTER SET utf8
FIELDS
  TERMINATED BY ' '
IGNORE 1 LINES
(nickname, money, last_visit)
```

## docker 파일 전송
### 호스트에서 컨테이너로 파일 전송
```shell
$ docker cp (전송할파일경로) 컨테이너명:(저장할경로)
$ docker cp ./sampleUser.csv mysql-container:/home/sampleUser.csv
```

### 컨테이너에서 호스트로 파일 전송
```shell
$ docker cp 컨테이너명:(전송할파일경로) (저장할경로)
$ docker cp mysql-container:/home/sampleUser.csv /home/sampleUser.csv

## sql 설정 파일에 직접 입력하여 인코딩 방식을 설정하는 방법
1. mysql 컨테이너에서 vim 설치
```shell
bash > yum install -y vim
```

2. /etc/my.cnf 파일에 접근
```shell
bash > vim /etc/my.cnf
```

3. /etc/my.cnf 파일에 다음과 같이 작성

```text
[client]
default-character-set = utf8mb4

[mysqld]
init_connect = SET collation_connection = utf8mb4_general_ci
init_connect = SET NAMES utf8mb4
character-set-server = utf8mb4
collation-server = utf8mb4_general_ci

[mysqldump]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4
```

4. mysql 컨테이너를 나가서 컨테이너 재시작
```shell
$ docker container restart mysql-container
$ docker exec -it mysql-container bash
bash >
```

9. mysql root 계정으로 접속하여 characterset 확인
```shell
bash > mysql -u root -p 
Enter Password:
mysql > status
```

![image](https://user-images.githubusercontent.com/33227831/220056240-c6409e70-26d9-40d1-9abb-7cdb7a120263.png)

## mysql 입력에 한글이 사라지는 문제
문제를 겪었던 점은 mysql container를 생성하고 mysql 콘솔 입력에서 한글을 입력시 한글이 사라지는 문제를 겪었습니다.

단순히 mysql 데이터베이스의 인코딩 방식을 utf8로 설정하면 해결되는줄 알았으나 utf8로 설정하였음에도 한글이 사라지는 것을 확인하였습니다.

문제의 원인은 mysql 데이터베이스를 실행하는 컨테이너의 locale 설정이 POSIX로 설정되어서 인것을 확인하였습니다.

**사용가능한 로케일 확인 및 설정**
```shell
$ locale -a
$ export LC_ALL=C.utf8
```

**컨테이너 생성 및 실행시 환경변수로 설정하는 방법**
```shell
$ docker run --name mysql-container \
-e MYSQL_ROOT_PASSWORD=root \
-e LC_ALL=C.UTF-8 \
-d -p 3306:3306 mysql/mysql-server:5.7 \
--character-set-server=utf8mb4 \
--collation-server=utf8mb4_unicode_ci
```
- -e LC_ALL=C.UTF-8 설정