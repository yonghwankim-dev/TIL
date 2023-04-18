# RDS 소개와 특징

## Amazon Relational Database Service(RDS)

- AWS에서 관계형 데이터베이스를 쉽게 사용할 수 있게 도와주는 서비스
- 장점 : 관계형 데이터베이스 관리의 부담을 덜어준다.
- 단점 : 추가적인 비용이 발생한다.

## 관계형 데이터베이스(RDBMS)

- 테이블 모양의 2차원 데이터 (행과열, 레코드와 필드)를 저장함
- 데이터와 데이터 사이의 관계를 활용하여 중복데이터를 방지하고 검색 성능을 높임
- 가장 대표적인 데이터베이스의 형태
- Oracle, MySQL, MS-SQL Server 등이 가장 유명함

## RDS에서 지원하는 RDB

- Oracle
- MySQL
- MariaDB
- Aurora
- PostgreSQL
- SQL Server

# RDS 장단점

## RDS를 사용해야 하는 이유

- 쉬운 백업과 복구
- 고가용성 확보
- 수직 확장 수평 확장시 다운타임 최소화
- AWS 타 서비스들과의 통합 편리
- 다양한 기능
- 높은 성능

## RDS를 사용하지 말아야 하는 이유

- 비싸다
- 완전한 최적화는 어렵습니다.
- 특정 벤더들의 경우 벤더사의 클라우드 제품이 더 유리할 수 있다.
- 멀티 클라우드는 어떤가?
- 초보에게는 득보다 실이 많음

# RDS 생성 전 준비작업

## 사전준비 단계

1. 어드맨 계정 접속
2. 최소 2개 이상의 AZ로 구성된 DB 서브넷 그룹 생성하기
3. 데이터베이스가 사용할 보안 그룹 생성하기

## DB 서브넷 그룹 생성하기

1. VPC -> 서브넷 생성 -> VPC 선택

![img_14.png](img_14.png)

2. 서브넷 설정 및 생성

![img_15.png](img_15.png)

3. private 서브넷의 라우팅 테이블을 다음과 같이 private 라우팅 테이블로 설정

![img_16.png](img_16.png)

4. RDS -> 서브넷 그룹 접속

![img_12.png](img_12.png)

5. DB 서브넷 그룹 생성

![img_13.png](img_13.png)

![img_17.png](img_17.png)

![img_18.png](img_18.png)

## 데이터베이스가 사용할 보안 그룹 생성하기

1. EC2 -> 보안 그룹 접속 -> 보안 그룹 생성

![img_19.png](img_19.png)

2. 인바운드 규칙 설정 및 생

![img_20.png](img_20.png)

# RDS 생성

1. RDS 서비스 이동 -> 데이터베이스 생성

![img_21.png](img_21.png)

2. 데이터베이스 생성

![img_2.png](img_2.png)
![img_3.png](img_3.png)
![img_4.png](img_4.png)
![img_5.png](img_5.png)
![img_6.png](img_6.png)
![img_23.png](img_23.png)
![img_24.png](img_24.png)
![img_25.png](img_25.png)
![img_26.png](img_26.png)
![img_27.png](img_27.png)
![img_28.png](img_28.png)

3. 데이터베이스 패스워드 설정

- 데이터베이스 선택 -> 수정

![img_29.png](img_29.png)

![img_30.png](img_30.png)

# RDS 연결 실습

1. EC2 -> 인스턴스 연결
2. mysql 설치

```
$ sudo yum update -y
$ wget https://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm
$ sudo yum localinstall mysql80-community-release-el9-1.noarch.rpm
$ sudo yum install mysql-community-server
$ mysql  
```

![img_31.png](img_31.png)

3. mysql 실행 및 상태 확인

```
$ sudo systemctl start mysqld
$ sudo systemctl status mysqld
```

![img_32.png](img_32.png)

4. RDS 서버에 연결

```
$ mysql -u admin -h {RDS 주소} -p 
```

RDS 주소는 다음과 같이 확인이 가능합니다.

RDS -> 데이터베이스 -> 데이터베이스 선택 -> 엔드포인트 복사 후 -h 옵션 값에 붙여넣기

![img_33.png](img_33.png)

5. mysql 접속 확인

![img_34.png](img_34.png)

6. 데이터베이스 생성

```
mysql > create database yonghwandb;
mysql > use yonghwandb;
```

7. 테이블 생성 및 데이터 삽입

```
mysql > create table game(id int primary key auto_increment, name varchar(64));
mysql > insert into game(name) values('ps2'), ('nes');
```

8. 추가된 데이터 확인

```
mysql > select * from game;
```

![img_35.png](img_35.png)

# GUI tools로 RDS 연결 : SSH Tunneling

EC2 인스턴스가 public에 있고 public 인스턴스를 통해서 RDS에 접속할 수 있을때 사용하는 방법입니다.

Mac OS에서는 Sequel Ace를 통하여 접속할 수있고, 윈도우에서는 HeidiSQL 또는 MySQL WorkBench를 통하여 접속할 수 있습니다.

저 같은 경우 HeidiSQL로 진행하겠습니다.

## HeidiSQL 접속 설정

호스트명/IP명은 RDS 서버의 엔드포인트 주소와 동일합니다.

사용자는 admin, 암호는 RDS 서버의 암호입니다.

![img_37.png](img_37.png)

- 호스트명 / IP : RDS 서버의 엔드포인트 주소입니다.

![img_38.png](img_38.png)

- SSH 호스트 + 포트 : EC2 인스턴스의 public 주소입니다.
- 개인 키 파일 : *.ppk 파일이어야 합니다.
    - puttyGen을 이용하면 pem 파일을 ppk 파일로 변환할 수 있습니다.

![img_39.png](img_39.png)

# RDS 백업

## 방법 1 : RDS 인스턴스 스냅샷 생성

1. RDS -> 스냅샷 -> 스냅샷 생성

![img_40.png](img_40.png)

2. DB 인스턴스 선택 및 이름 설정

![img_41.png](img_41.png)

## 방법 2: Point Time Recovery

특정한 시점을 기준으로 복원하는 기능입니다.

1. RDS -> 데이터베이스 -> 특정 DB 인스턴스 선택 -> 작업 -> 특정 시점으로 복원

![img_42.png](img_42.png)

2. 원하는 옵션을 선택하여 특정한 시점으로 복원합니다.

![img_43.png](img_43.png)

# RDS 확장

## RDS 수평 확장 - 읽기 복제본 추가

- 수평 확장 : Scaling Out <-> Scaling In
- 여러 대의 서버를 이용해서 성능을 높이는 방법
- NoSQL 들은 좀더 유연한 수평확장이 가능한 경우가 많습니다.
- 읽기 복제본 또는 샤딩과 같은 기술이 대표적입니다.
- 개발자의 추가적인 개발 비용이 발생합니다.
- Oracle이나 SQL Server처럼 RDBMS 자체적으로 지원해 주는 경우도 있습니다.

## RDS 수평 확장 실습

1. RDS -> 데이터베이스 -> DB 인스턴스 선택 -> 작업 -> 읽기 복제본 생성

![img_44.png](img_44.png)

2. DB 인스턴스 식별자 작성 및 스토리지 자동 조정 활성화 해제

![img_45.png](img_45.png)

![img_46.png](img_46.png)

3. SSH를 이용하여 읽기 전용 DB에 접속

![img_47.png](img_47.png)

![img_48.png](img_48.png)

![img_49.png](img_49.png)

