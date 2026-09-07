MongoDB Docker Container 실행
```shell
docker run -d \
	--name mongodb \
	-p 27017:27017 \
	-v ./mongodb-data:/data/db \
	-e MONGO_INITDB_ROOT_USERNAME=root \
	-e MONGO_INITDB_ROOT_PASSWORD=root \
	mongo
```

### 몽고 쉘 접속
```shell
mongosh -u root -p
```

### 데이터베이스 목록 출력
```js
show dbs;
```

### 데이터베이스 선택
```shell
use video
```

### 특정 컬렉션 생성 및 접근
```shell
db.movies
```

### 2.5.3 셸 기본 작업
#### 생성 (insertOne)
```mongo
movie = {
	"title": "Star Wars: Episode IV - A new Hope",
	"director": "George Lucas",
	"year": 1977
}
```
![](imgs/Pasted%20image%2020260703153130.png)

movies 컬렉션에 movie 데이터 한개 삽입
```shell
db.movies.insertOne(movie)
```
![](imgs/Pasted%20image%2020260703153614.png)

find를 호출해서 컬렉션의 도큐먼트들을 확인합니다.
```shell
db.movies.find().pretty()
```
![](imgs/Pasted%20image%2020260703154116.png)

### 읽기(fineOne)
컬렉션에서 단일 도큐먼트를 읽기 위해서는 `findOne`을 사용해야 한다.
```shell
db.movies.findOne()
```
![](imgs/Pasted%20image%2020260703154413.png)

### 갱신(updateOne)
updateOne 함수 매개변수
1. 수정할 도큐먼트를 찾는 기준
2. 갱신 작업을 설명하는 도큐먼트

도큐먼트에 리뷰 배열을 추가해봅니다.
```shell
db.movies.updateOne(
	{
		title: "Star Wars: Episode IV - A new Hope",
		director: "George Lucas"
	},
	{
		$set: {
			reviews: []
		}
	}
)
```
![](imgs/Pasted%20image%2020260703154918.png)

갱신 결과 확인
```shell
db.movies.find().pretty()
```
![](imgs/Pasted%20image%2020260703154946.png)

### 삭제(deleteOne, deleteMany)
deleteOne, deleteMany 함수는 도큐먼트를 데이터베이스에서 영구적으로 삭제합니다. 매개변수는 한개로써 **필터 도큐먼트 삭제 조건**을 지정합니다.

```shell
db.movies.deleteOne({
	title: "Star Wars: Episode IV - A new Hope",
	director: "George Lucas"
})
```
![](imgs/Pasted%20image%2020260703155656.png)

## 2.6 데이터형
지원하는 데이터형 종류
- null
- Boolean
- Number
	- 64비트 부동소수점 수를 기본으로 사용함
- String
- Date
- 정규 표현식
- 배열
- 내장 도큐먼트
- 객체 ID
- 이진 데이터
	- 이진 데이터는 임의의 바이트 문자열이며 셸에서는 조작이 불가능함
	- 이진 데이터는 데이터베이스에 UTF-8이 아닌 문자열을 저장하는 유일한 방법
- 코드

```js
// null
{"x": null}

// Boolean
{"x": true}

// Number
{"x": 3.14}
{"x": 3}

// String
{"x": "foobar"}

// Date
{"x": new Date()}

// 정규 표현식
{"x": /foobar/i}

// 배열
{"x": ["a", "b", "c"]}

// 내장 도큐먼트
{"x", {"foo": "bar"}}

// 객체 ID
{"x": ObjectId()}

// 코드
{"x": function(){/* */}}
```

### 2.6.2 날짜
- `new Date()`로 호출하여 날짜 데이터를 생성함
- 1970년 1월 1일부터의 시간을 1/1000초 단위로 저장하고 표준 시간대 정보는 없음
	- 표준 시간대 정보는 또다른 키-값으로 저장할 수 있음

`new Date()` 예시
![](imgs/Pasted%20image%2020260703161946.png)

### 2.6.3 배열
mongodb 배열 특징
- 서로 다른 데이터형을 값으로 포함할 수 있음
- 어떤 데이터형 값이든 될수 있음

```js
{"things": ["pie", 3.14]}
```


### 2.6.4 내장 도큐먼트
**내장 도큐먼트는 키에 대한 값이 도큐먼트 타입**을 말합니다. 다음 코드는 내장 도큐먼트의 예시입니다. 다음 예시를 보면 address 키에 대한 값이 또다른 도큐먼트가 되어서 내장 도큐먼트가 됩니다. 해당 내장 도큐먼트는 "street", "city", "state" 키-값을 갖는 도큐먼트가 됩니다.
```js
{
	"name": "John Doe",
	"address": {
		"street": "123 Park Street",
		"city": "Anytown",
		"state": "MY"
	}
}
```
- RDB 데이터베이스라면 people과 address 테이블로 분리해서 설계할 것입니다.
- MongoDB에서는 people 도큐먼트 안에 바로 "address" 도큐먼트를 내장할 수 있습니다.

단점
- MongoDB에서는 더 많은 **데이터 반복**이 생길수 있습니다. 예를 들어 관계형 데이터베이스에서는 people과 address 테이블이 분리된 상태에서 주소의 오타를 고쳐야 하면 테이블을 조인해서 같은 주소를 갖는 모든 사람의 주소를 수정할 수 있습니다. 하지만 MongoDB에서는 **각 사람의 도큐먼트에서 오타를 수정**해야 합니다.

### 2.6.5 _id와 ObjectId
- MongoDB에 저장된 모든 도큐먼트는 `_id`키를 가진다.
- `_id`키 값은 어떤 데이터 타입이어도 상관없지만 `ObjectId`가 기본이다.
- 하나의 컬렉션에서 모든 도큐먼트는 고유한 `_id`키를 가진다.
	- 예를 들어 A 컬렉션에서 한 도큐먼트가 123이라는 `_id`키를 가질수 있고, B 컬렉션에서도 한 도큐먼트가 123이라는 `_id`키를 가질수 있음. 하지만 서로 같은 컬렉션에서는 고유한 `_id`키를 가진다.

#### ObjectIds
**특징**
- `ObjectId`는 `_id`의 기본 데이터 타입
- `ObjectId`를 사용하는 이유는 MongoDB의 분산 특성 때문입니다.
	- `ObjectId`를 사용하는 이유는 여러 서버에 걸쳐서 자동으로 증가하는 기본키를 동기화하는 작업은 어렵고 시간이 걸립니다.
	- MongoDB는 분산 데이터베이스로 설계되었기 때문에 샤딩된 환경에서 **고유 식별자**를 생성하는 것이 매우 중요함
- `ObjectId`는 12byte 스토리지를 사용하며 24자리 16진수 문자열 표현이 가능함
	- 예를 들어 "ff"라는 16진수 문자열을 1byte(1111 1111)로 표현이 가능합니다. 
	- 총 12byte를 저장 가능하므로 눈으로 볼때는 16진수 문자열을 24자리로 표현이 가능하고, 디스크에 저장할때는 이진수로 저장되기 때문에 12byte로 저장됩니다.
- `ObjectId` 생성 구조
	- 첫 4byte는 타임스탬프
		- 1970년 1월 1일부터 시간을 1/1000초 단위로 저장
		- 

ObjectId의 타임스탬프 생성 구조
예를 들어 `ObjectId=6688f15f7da1ef4619000001`이고 첫 4byte는 타임스탬프이므로 16진수 문자열은 `6688f15f`가 됩니다.
1. 16진수를 10진수로 변환
	- 16진수 `6688f15f`를 10진수 숫자로 변환하면 `1720234335`가 됩니다.
2. 숫자의 의미
	- `1720234335` 숫자의 의미는 분산 시스템이나 데이터베이스에서 시간을 기록할때 전세계 표준으로 사용하는 '1970년 1월 1일 0시 0분 0초' 기준의 누적초(Seconds) 값입니다.
3. 우리가 읽을 수 있는 시간으로 변환
	- `1720234335` 초를 달력시간으로 계산하면 다음과 같은 시간이 나옵니다.
		- **UTC (세계 표준시):** 2024년 7월 6일 02:52:15
		- **KST (한국 표준시):** 2024년 7월 6일 11:52:15

12byte ObjectId의 전체 구조 요약
```
[  4 바이트  ] [     5 바이트     ] [  3 바이트  ]
  Timestamp     Random Value     Counter
 (6688f15f)   (7da1ef4619)     (000001)
```
- Random Value(5byte / 중간 10글자) : 해당 ID를 생성한 프로세스/장비마다 고유하게 할당되는 무작위 값
- Counter (3byte / 뒤 6글자) : 같은 장비에서 같은 1초동안 여러 ID가 생성될때 1씩 올라가는 일련번호($2^{24} = 16,777,216$가지 조합 가능)

## 2.7 MongoDB 셸 사용
다음 명령어와 같이 외부(또는 원격)에 존재하는 MongoDB 서버에 터미널을 통해 직접 접속하여 데이터베이스를 제어할 수 있습니다.
```shell
mongosh some-host:27017/myDB
```

`--nodb` : mongosh 셸 시작시 데이터베이스 선택하지 않고 접속하는 옵션
```shell
mongosh --nodb
```

`new Mongo`를 활용하여 mongod에 연결
```js
> conn = new Mongo("localhost:27017")
> db = conn.getDB("video")
```
![](imgs/Pasted%20image%2020260706120219.png)

### 2.7.1 셸 활용 팁
도움말
mongosh 셸에서 명령어 도움말을 출력하기 위해서 `help` 명령어를 실행합니다.
```js
help
```

데이터베이스 수준의 도움말 실행
```js
db.help()
```

컬렉션 수준의 도움말 실행
```js
db.foo.help()
```

함수의 소스코드 출력
- 함수의 기능을 알고 싶은 경우 toString()을 호출합니다.
```js
db.movies.updateOne.toString()
```
![](imgs/Pasted%20image%2020260706114631.png)

### 2.7.2 셸에서 스크립트 실행하기
다음과 같이 자바스크립트 파일을 셸로 전달하여 스크립트를 실행할 수있습니다.
```shell
$ mongosh script1.js script2.js script3.js
```
![](imgs/Pasted%20image%2020260706120930.png)

특정 데이터베이스에 스크립트 실행하기
```sh
$ mongosh localhost:27017/video --quiet script1.js script2.js script3.js
```
- `--quit` : 배너 출력하지 않음

`load` 함수를 이용하여 스크립트 실행하기
```js
load("script1.js")
```
![](imgs/Pasted%20image%2020260706121158.png)

스크립트 파일의 접근 권한
- 스크립트는 db 변수에 대한 접근 권한을 가짐
- 하지만 `use db`, `show collections`와 같은 셸 보조자는 파일에서 작동하지 않음
- 다음 표는 이들 각각에 대응되는 유효한 자바스크립트 용법입니다.

| 셸 보조자            | 같은 의미의 자바 스크립트          |
| ---------------- | ----------------------- |
| use video        | db.getSisterDB("video") |
| show dbs         | db.getMongo().getDBs()  |
| show collections | db.getCollectionNames() |

스크립트를 사용해 셸에 변수 입력
defineConnectTo.js 파일 작성
```shell
cat << 'EOF' > defineConnectTo.js
// defineConnectTo.js

/**
 * 데이터베이스에 연결하고 db를 설정
 */
 var connectTo = function(port, dbname){
     if(!port){
         port = 27017;
     }
     
     if(!dbname){
         dbname = "test";
     }
     
     db = connect("localhost:"+port+"/"+dbname);
     return db;
 }
EOF
```

mongosh 셸에서 defineConnectTo.js 스크립트 파일을 로드해봅니다. 실행 결과를 보면 connectTo 함수가 정의되어 있지 않다가 스크립트를 실행한 다음에 함수 정의가 된것을 볼수 있습니다.
```js
typeof connectTo
load('defineConnectTo.js')
typeof connectTo
```
![](imgs/Pasted%20image%2020260706123253.png)

### 2.7.3 `.mongoshrc.js` 만들기
셸이 시작할때마다 실행되는 스크립트를 `.mongoshrc.js` 파일에 넣을 수 있습니다. 예를 들어 로그인할때마다 사용자를 맞이하는 셸을 만들고 싶습니다.
`.mongoshrc.js`
```shell
cat << 'EOF' > .mongoshrc.js
// .mongorc.js

var compliment = ["attractive", "intelligent", "like Batman"];
var index = Math.floor(Math.random()*3);

print("Hello, you're looking particularly " + compliment[index] + " today!");
EOF
```

mongosh 접속해봅니다.
![](imgs/Pasted%20image%2020260706124815.png)

`.mongoshrc.js` 스크립트 파일을 사용하여 다음과 같은 것들을 할수 있습니다.
- 전역 변수 설정
- 별칭 설정
- 내장 함수 재정의

`.mongoshrc.js`의 일반적인 용버중 하나는 더 위험한 셸보조자를 제거하는 것입니다. 다음 스크립트 파일의 코드는 리소스를 삭제 방지하도록 재정의하거나 선언 해제하는 것입니다.
```js
cat << 'EOF' > ~/.mongoshrc.js
var no = function(){
    print("Not on my watch");
}

// 1. 현재 db 인스턴스를 통해 데이터베이스 프로토타입 추출
var DbPrototype = Object.getPrototypeOf(db);
db.dropDatabase = DbPrototype.dropDatabase = no;

// 2. 가상의 컬렉션을 통해 컬렉션 프로토타입 추출
var CollectionPrototype = Object.getPrototypeOf(db.getCollection("dummy"));
CollectionPrototype.drop = no;
CollectionPrototype.dropIndex = no;
CollectionPrototype.dropIndexes = no;
EOF
```

데이터베이스 삭제 테스트
```js
db.dropDatabase()
```
![](imgs/Pasted%20image%2020260706130123.png)

### 2.7.4 프롬프트 커스터마이징하기
`prompt` 변수에 문자열이나 함수를 설정하여 커스터마이징 할 수 있습니다. 예를 들어 프롬프트에 현재 시간을 출력하는 프롬프트로 커스터마이징 할 수 있습니다.
```js
prompt = function(){
	return (new Date()) + "> ";
}
```

현재 사용하는 데이터베이스를 보여주는 프롬프트 커스터마이징 (`.mongoshrc.js` 파일에 작성)
```js
prompt = function(){
    if(typeof db == 'undefined'){
        return '(nodb)> ';
    }
    
    try{
        db.hello();
    }catch(e){
        print("연결 에러 발생: " + e);
        return "(error)> ";
    }
    return db.getName() + "> ";
}
```

### 2.7.5 복잡한 변수 수정하기
외부 편집기 설정
```js
config.set("editor", "vim")
```

새 편집 세션 시작
```js
edit
```
![](imgs/Pasted%20image%2020260706134651.png)
![](imgs/Pasted%20image%2020260706134705.png)

변수 편집
- 예를 들어 albums 변수 배열을 빈 배열로 초기화한 다음에 배열을 편집하고 싶은 경우 다음과 같이 `edit` 명령어를 사용합니다.
```js
var albums = [];
edit albums
```

vim 편집기 화면에서 다음과 같이 배열을 작성합니다. 그리고 :wq를 이용해서 저장합니다.
![](imgs/Pasted%20image%2020260706134827.png)
저장하게 되면 터미널 입력창에서 다음과 같이 작성되게 되고, 엔터를 눌러서 albums 변수를 저장합니다.
![](imgs/Pasted%20image%2020260706134834.png)

albums 변수를 호출해봅니다.
![](imgs/Pasted%20image%2020260724134006.png)

`.mongoshrc.js` 파일에 editor 설정
```sh
config.set("editor", "vim")
```
![](imgs/Pasted%20image%2020260706134938.png)

mongosh 셸에서 edit 명령어를 실행하여 vim 편집기를 사용할 수 있습니다.
```js
edit
```

### 2.7.6 불편한 컬렉션명
컬렉션 이름이 예약어가 아니거나 유효하지 않은 자바스크립트 속성명이 아니라면 `db.collectionName` 구문으로 컬렉션을 가져올 수 있습니다.
예를 들어 `movies` 컬렉션을 가져오기 위해서 다음과 같이 사용할 수 있습니다.
```js
db.movies
```
![](imgs/Pasted%20image%2020260724134957.png)

하지만 컬렉션 이름이 예약어라면 컬렉션을 가져올 수 없습니다. 예를 들어 `version` 이라는 컬렉션에 접근한다고 가정합니다. `db.version`은 db의 메서드이기 때문에 `db.version` 구문을 통해서 version 컬렉션에 접근할 수 없다.
```js
db.version
```
다음 실행 결과를 보면 함수 정의가 나오는 것을 볼수 있습니다.
![](imgs/Pasted%20image%2020260724135128.png)

그래서 version 컬렉션에 접근하기 위해서는 `getCollection` 함수를 사용해야 합니다.
```js
db.getCollection("version");
```
실행 결과를 보면 version 컬렉션을 가져온 것을 볼수 있습니다.
![](imgs/Pasted%20image%2020260724135235.png)

또 다른 안되는 예시로 `foo-bar-baz`와 같이 자바스크립트 속성명에 유효하지 않은 문자인 경우 `getCollection`을 사용한다.
```js
db.foo-bar-baz
```
![](imgs/Pasted%20image%2020260724135411.png)

```js
db.getCollection("foo-bar-baz")
```
![](imgs/Pasted%20image%2020260724135426.png)

자바스크립트 속성명이 유효하지 않은 경우에는 **배열 접근 구문**을 이용해서 컬렉션을 가져올 수 있습니다. 예를 들어 "foo-bar-baz" 컬렉션을 다음과 같이 접근 가능하다.
```js
db["foo-bar-baz"]
```
![](imgs/Pasted%20image%2020260724140550.png)

또한 배열 접근 구문을 서브 컬렉션에도 적용하여 서브 컬렉션에도 접근할 수 있다.
```js
var collections = ["posts", "comments", "authors"];
for(var i in collections){
	print(db.blog[collections[i]])
}
```
blog 컬렉션 밑에는 `posts`, `comments`, `authors` 서브 컬렉션이 존재하고 이 3가지의 서브 컬렉션에 접근하기 위해서 배열 접근 구문을 사용하여 접근할 수 있다.
![](imgs/Pasted%20image%2020260724140717.png)

물론 배열 접근 구문을 사용하는 것이 아니고 서브 컬렉션 이름이 예약어나 부적절한 자바스크립트 속성명이 아니라면 점(.)을 통해서 접근할 수 있다.
```js
print(db.blog.posts);
print(db.blog.comments);
print(db.blog.authors);
```
![](imgs/Pasted%20image%2020260724140924.png)

다음과 같이 name 변수는 부적절한 자바스크립트 속성명 값을 가지고 있습니다. 하지만 배열 접근 구문을 통해서 `@#$!` 컬렉션에 접근할 수 있습니다.
```js
var name = "@#&!";
db[name].find()
```
![](imgs/Pasted%20image%2020260724141101.png)

