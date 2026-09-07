
### 학습 목표
- 컬렉션에 새로운 도큐먼트 추가하기
- 컬렉션에 도큐먼트 삭제하기
- 기존 도큐먼트 갱신하기
- 연산 수행시 안전성과 속도 중 맞는 수준 선택하기

## 목차
- 3.1 도큐먼트 삽입
	- 3.1.1 insertMany
	- 3.1.2 삽입 유효성 검사
	- 3.1.3 삽입
- 3.2 도큐먼트 삭제
	- 3.2.1 drop
- 3.3 도큐먼트 갱신
	- 3.3.1 도큐먼트 치환
	- 3.3.2 갱신 연산자
	- 3.3.3 갱신 입력
	- 3.3.4 다중 도큐먼트 갱신
	- 3.3.5 갱신한 도큐먼트 반환

## 3.1 도큐먼트 삽입
`insertOne` 함수를 사용해서 도큐먼트를 삽입할 수 있습니다. 예를 들어 movies 컬렉션에 도큐먼트를 다음과 같이 추가합니다.
```js
db.movies.insertOne({"title": "Stand by Me"})
```
![](imgs/Pasted%20image%2020260724141335.png)

### 3.1.1 insertMany
`insertMany` 함수를 사용해서 함수에 도큐먼트 배열을 전달해서 대량으로 도큐먼트를 추가할 수 있습니다.
```js
db.movies.drop()

db.movies.insertMany(
	[
		{"title": "Ghostbusters"},
		{"title": "E.T."},
		{"title": "Balde Runner"}
	]
);
```
![](imgs/Pasted%20image%2020260724143218.png)

옵션 도큐먼트(options document)
- `insertMany` 함수의 두번째 매개변수로 옵션 도큐먼트를 설정할 수 있습니다.
- 옵션 도큐먼트 `ordered` 키에 `true` 값 지정 : 도큐먼트가 제공된 순서대로 삽입 (**기본값**)
	- **정렬된 삽입(ordered insert)**이라고 부름
- 옵션 도큐먼트 `ordered` 키에 `false` 값 지정. : 몽고DB가 성능을 개선하기 위해서 삽입을 재배열 할 수 있음
	- **정렬되지 않은 삽입(unordered insert)**이라고 부름
- 도큐먼트 삽입 오류 발생시 `ordered=true`(정렬된 삽입)인 경우에는 오류가 발생한 도큐먼트를 포함한 그 뒤에 도큐먼트 요소들을 삽입 시도하지 않는다. 반대로 `ordered=false`(정렬되지 않은 삽입)인 경우에는 **일부 도큐먼트 삽입이 오류와 관계없이 모든 도큐먼트 데이터를 삽입을 시도**한다.

**정렬된 삽입인 상태의 `insertMany` 수행할때 도큐먼트 삽입 에러가 발생하는 경우**
- "Gremlins" 값을 가진 도큐먼트의 `_id`값을 보면 중복되는 것을 볼수 있습니다. 해당 도큐먼트 삽입시 `_id` 중복으로 삽입 오류가 발생할 것입니다.
- 해당 insertMany 함수는 기본값인 정렬된 삽입인 상태에서 수행됩니다.
- 삽입 오류가 발생하는 경우 "Aliens" 도큐먼트는 삽입되는지 확인해봅니다.
```js
db.movies.insertMany(
	[
		{"_id": 0, "title": "Top Gun"},
		{"_id": 1, "title": "Back to the Future"},
		{"_id": 1, "title": "Gremlins"},
		{"_id": 2, "title": "Aliens"}
	]
);
```

실행 결과를 보면 `_id`키에 대한 값이 중복되어 세번째 도큐먼트(index=2)에서 삽입 오류가 발생하였습니다.
![](imgs/Pasted%20image%2020260724145600.png)
![](imgs/Pasted%20image%2020260724145753.png)

movies 컬렉션의 도큐먼트들을 조회해봅니다.
```js
db.movies.find()
```
실행 결과를 보면 2개의 도큐먼트만 삽입되고 "Aliens" 도큐먼트는 삽입되지 않았습니다.
![](imgs/Pasted%20image%2020260724150242.png)

**정렬되지 않은 삽입인 상태의 `insertMany` 수행할때 도큐먼트 삽입 에러가 발생하는 경우**
- 옵션 도큐먼트 전달시 `ordered` 프로퍼티를 `false`로 지정하여 **정렬되지 않은 삽입**으로 다중 삽입을 시도합니다.
- 일부 도큐먼트에 삽입 에러가 발생하는 경우 다른 정상적인 도큐먼트는 삽입이 되는지 확인한다.
```js
db.movies.drop();
db.movies.insertMany(
	[
		{"_id": 3, "title": "Sixteen Candles"},
		{"_id": 4, "title": "The Terminator"},
		{"_id": 4, "title": "The Princess Bride"},
		{"_id": 5, "title": "Scarface"}
	],
	{"ordered" : false}
)
```

![](imgs/Pasted%20image%2020260724150714.png)
![](imgs/Pasted%20image%2020260724150721.png)

다시 movies 컬렉션의 도큐먼트들을 조회해봅니다.
```js
db.movies.find()
```
실행 결과를 보면 삽입 오류가 발생한 "The Princess Bride" 도큐먼트를 제외한 도큐먼트는 정상적으로 삽입된. 것을 볼수 있습니다.
![](imgs/Pasted%20image%2020260724150805.png)

대량 쓰기(bulk write) 지원
- 삽입을 제외한 다른 작업(update, delete) 또한 대량 쓰기에서 지원할 수 있다.
- 대량 쓰기를 지원한다는 의미는 **삽입 외에도 수정, 삭제, 교체 같은 여러가지 종류의 데이터 변경 작업들을 하나의 명령으로 묶어서 단 한번의 요청(대량 작업)으로 처리할 수 있다는 의미**입니다.
- 즉, 한번의 호출로 여러가지 유형의 변경 작업을 일괄 처리하는 대량 쓰기 API를 몽고 DB는 지원한다.

예를 들어 users 컬렉션에 대량 쓰기(bulkWrite) 함수 실행시 삽입, 수정, 삭제, 교체를 한번의 요청으로 처리할 . 수있습니다.
```js
db.users.bulkWrite([
  // 1. 삽입 (Insert)
  { insertOne: { document: { _id: 1, name: "Alice", status: "active" } } },

  // 2. 수정 (Update)
  { updateOne: { 
      filter: { _id: 2 }, 
      update: { $set: { status: "inactive" } } 
  } },

  // 3. 삭제 (Delete)
  { deleteOne: { 
      filter: { _id: 3 } 
  } },

  // 4. 교체 (Replace)
  { replaceOne: { 
      filter: { _id: 4 }, 
      replacement: { name: "Bob", status: "pending" } 
  } }
]);
```

### 3.1.2 삽입 유효성 검사
몽고 DB는 삽입된 데이터에 최소한의 검사를 수행한다.
- 모든 도큐먼트가 16MB 보다 작은지 검사
- UTF-8이 아닌 문자열 사용하는지 검사
- 인식할 수 없는 데이터 타입을 포함하는지 검사

### 3.1.3 삽입
- 몽고 DB 3.0 이전에는 도큐먼트 삽입 방법으로 `insert` 를 사용함
- 몽고 DB 드라이버는 3.0 버전 서버 릴리스와 동시에 새로운 CRUD API를 선보임
	- `insertOne`, `insertMany` 등
- 3.0버전 이후에도 `insert` 함수는 호환성을 위해서 사용가능하지만 되도록 `insertOne`, `insertMany`를 사용하자

## 3.2 도큐먼트 삭제
몽고 DB에서 도큐먼트를 삭제하기 위해서 `deleteOne`, `deleteMany` 함수를 사용한다. 두 함수 모두 첫번째 매개변수로 **필터 도큐먼트**를 사용한다. 필터 도큐먼트는 도큐먼트를 삭제하기 위한 기준 조건입니다.

**deleteOne 예시**
예를 들어 다음 예시는 `_id`값이 4인 도큐먼트를 제거한다.
```js
db.movies.find()
```
![](imgs/Pasted%20image%2020260724153606.png)

```js
db.movies.deleteOne({"_id": 4})
db.movies.find()
```
![](imgs/Pasted%20image%2020260724153633.png)
![](imgs/Pasted%20image%2020260724153648.png)

deleteOne 함수 특징
- 하나의 도큐먼트만 삭제한다.
- 필터와 일치하는 첫번째 도큐먼트만 삭제한다.
	- 어떤 도큐먼트가 먼저 발견되는지는 도큐먼트가 삽입된 순서, 도큐먼트에 어떤 갱신이 이루어졌는지, 어떤 인덱스를 지정하는지 등 몇가지 요인에 따라서 달라진다.

**deleteMany 예시**
deleteMany 함수 사용전에 도큐먼트 준비를 한다.
```js
db.movies.drop();
db.movies.insertMany(
	[
		{"_id": 0, "title": "Top Gun", "year": 1986},
		{"_id": 1, "title": "Back to the Future", "year": 1985},
		{"_id": 3, "title": "Sixteen Candles", "year": 1984},
		{"_id": 4, "title": "The Terminator", "year": 1984},
		{"_id": 5, "title": "Scarface", "year": 1983}
	]
);
db.movies.find()
```
![](imgs/Pasted%20image%2020260724154135.png)

도큐먼트의 year 프로퍼티가 1984년인 도큐먼트를 제거한다.
```js
db.movies.deleteMany({"year": 1984})
db.movies.find()
```
![](imgs/Pasted%20image%2020260724154227.png)
![](imgs/Pasted%20image%2020260724154244.png)

### 3.2.1 drop
컬렉션의 `drop` 함수를 이용해서 해당 컬렉션의 모든 도큐먼트를 제거한다.
```js
db.movies.drop()
db.movies.find()
```
![](imgs/Pasted%20image%2020260724155524.png)

## 3.3 도큐먼트 갱신
컬렉션의 `updateOne`, `updateMany`, `replaceOne` 함수를 사용하여 도큐먼트를 갱신한다.

`updateOne`, `updateMany`
- 첫번째 매개변수 : 필터 도큐먼트
- 두번째 매개변수 : **수정자 도큐먼트(modifier document)**
	- 변경 사항을 설명하는 도큐먼트

`replaceOne`
- 첫번째 매개변수 : 필터 도큐먼트
- 두번째 매개변수 : 필터와 일치하는 도큐먼트를 교체할 도큐먼트

도큐먼트 갱신 특징
- 갱신 작업은 원자적으로 이루어지기 때문에 먼저 도착한 요청을 순차적으로 처리하고, 여러 요청이 들어오면 마지막 요청이 최종적으로 반영된다.

### 3.3.1 도큐먼트 치환
`replaceOne` 함수는 도큐먼트를 새로운 도큐먼트로 덮어쓰기한다. 

다음 예시는 `replaceOne` 을 사용하기전에 사용자 도큐먼트를 교체하는 작업입니다.
다음과 같은 사용자 도큐먼트가 있다고 가정합니다.
```json
db.users.insertOne(
	{
		"name": "joe",
		"friends": 32,
		"enemies": 2 
	}
)
db.users.find()
```
![](imgs/Pasted%20image%2020260724161342.png)

방금 삽입한 도큐먼트에서 `friends`와 `enemies` 필드를 `relationships`라는 **서브 도큐먼트**로 옮겨봅니다.

```js
var joe = db.users.findOne({"name": "joe"});
joe.relationships = {"friends": joe.friends, "enemies": joe.enemies};
joe.username=joe.name;
delete joe.friends;
delete joe.enemies;
delete joe.name
db.users.replaceOne({"name":"joe"}, joe); // replaceOne을 사용해 데이터베이스 버전 교체
db.users.find()
```
joe 변수는 relationships 서브 도큐먼트를 가진 도큐먼트로 변경되었고 이를 데이터베이스에 있는 기존 joe 도큐먼트를 치환하기 위해서 마지막에 `replaceOne`을 사용해 치환한다.
![](imgs/Pasted%20image%2020260724162107.png)
실행 결과를 보면 정상적으로 relationships 서브 도큐먼트로 이동된 것을 볼수 있다.
![](imgs/Pasted%20image%2020260724162244.png)

필터 도큐먼트에서 2개 이상의 도큐먼트가 일치되게 한후 두번째 매개변수로 중복된 `_id`값을 갖는 도큐먼트를 생성하는 경우 어떻게 되는가?
=> 데이터베이스는 오류를 반환하고 아무것도 변경되지 않는다.

예를 들어 동일한 "name" 값을 갖는 도큐먼트를 여러개 만들었다고 가정합니다.
```js
db.people.insertMany(
	[
		{"_id": ObjectId("aaaaaaaaaaaaaaaaaaaa7a7b"), "name": "joe", "age": 65},
		{"_id": ObjectId("aaaaaaaaaaaaaaaaaaaa7a7c"), "name": "joe", "age": 20},
		{"_id": ObjectId("aaaaaaaaaaaaaaaaaaaa7a7d"), "name": "joe", "age": 49}
	]
);
```
![](imgs/Pasted%20image%2020260724163631.png)

`age` 의 값이 20인 joe의 age값을 1 증가시킵니다. 그리고 joe 이름을 가진 도큐먼트에 덮어쓰기를 시도합니다.
```js
joe = db.people.findOne({"name": "joe", "age": 20});
joe.age++;
db.people.replaceOne({"name": "joe"}, joe);
```
시행 결과를 보면 몽고DB 에러가 발생하였습니다. 오류 메시지를 보면 교체하려는 joe 변수 안에 들어있는 `_id`값이 DB에 실제로 저장되어  있는 도큐먼트의 `_id`값과 달라서 발생한 문제입니다. 이는 첫번째 joe(age=65)를 발견해서 해당 `_id`값이 달라서 입니다.
![](imgs/Pasted%20image%2020260725124933.png)

위와 같은 문제를 피하기 위해서는 `_id`값을 필터 도큐먼트로 전달하여 교체하는 것이 좋습니다.
```js
db.people.replaceOne({"_id": ObjectId("aaaaaaaaaaaaaaaaaaaa7a7c")}, joe)
```
![](imgs/Pasted%20image%2020260725125523.png)

### 3.3.2 갱신 연산자
갱신 연산자(update operator)
- 키(key)를 변경, 추가, 제거하고, 배열하고 내장 도큐먼트를 조작하는 연산

다음 도큐먼트는 사용자가 페이지를 방문할때마다 URL에 대한 방문횟수를 저장하는 도큐먼트입니다.
```js
db.analytics.insertOne(
	{
		"url": "www.example.com",
		"pageviews": 52
	}
);
db.analytics.find()
```
![](imgs/Pasted%20image%2020260725130114.png)

페이지를 방문할때마다 URL로 페이지를 찾고, `pageviews` 키의 값을 증가시키려면 **`$inc` 제한자**를 사용합니다.
```js
db.analytics.updateOne({"url": "www.example.com"}, {"$inc": {"pageviews" : 1}})
```
실행 결과를 보면 pageviews 키의 값이 53으로 1 증가하였다.
![](imgs/Pasted%20image%2020260725130741.png)

갱신 연산자를 사용할때 `_id`값은 변경할 수 없다. 만약 변경하려면 도큐먼트 전체를 치환해야 한다.

#### "$set" 제한자 사용하기
`$set` 제한자
- 필드 값 설정
- 필드가 존재하지 않으면 새 필드가 생성된다

다음과 같이 사용자 정보가 저장되어 있다.
```js
db.users.insertOne(
	{
		"name": "joe",
		"age": 30,
		"set": "male",
		"location": "Wisconsin"
	}
);
db.users.findOne()
```
![](imgs/Pasted%20image%2020260725131156.png)

사용자가 좋아하는 책을 프로필에 추가하기 위해서 다음과 같이 `$set` 제한자를 사용한다.
```js
db.users.updateOne({"_id": ObjectId("6a6437856884401997d0f1be")}, {"$set": {"favorite book": "War and Peace"}});
db.users.findOne();
```
실행 결과를 보면 "favorite book" 키가 추가되었다.
![](imgs/Pasted%20image%2020260725131713.png)

만약 "War and Peace"가 아닌 "Green Eggs and Ham"으로 수정하고자 하는 경우 다음과 같이 실행합니다.
```js
db.users.updateOne({"_id": ObjectId("6a6437856884401997d0f1be")}, {"$set": {"favorite book": "Green Eggs and Ham"}});
db.users.findOne();
```
![](imgs/Pasted%20image%2020260725131924.png)

만약 "favorite book" 키에 대한 값을 문자열이 아닌 문자열 배열 형태로 변경하기 위해서는 다음과 같이 실행합니다.
```js
db.users.updateOne({"_id": ObjectId("6a6437856884401997d0f1be")}, {"$set": {"favorite book": ["Cat's Cradle", "Foundation Trilogy", "Ender's Game"]}});
db.users.findOne();
```
![](imgs/Pasted%20image%2020260725132941.png)
위 예시를 통해서 `$set` 제한자는 키의 데이터형도 변경할 수 있다.

사용자가 책을 좋아하지 않아서 "favorite book" 키를 제거해야 한다면 **`$unset` 제한자**를 사용한다.
```js
db.users.updateOne({"_id": ObjectId("6a6437856884401997d0f1be")}, {"$unset": {"favorite book": 1}});
db.users.findOne();
```
![](imgs/Pasted%20image%2020260725133230.png)

`$set` 제한자는 내장 도큐먼트 내부의 데이터를 변경할때도 사용 가능합니다. 예를 들어 다음과 같은 데이터가 있다고 가정합니다.
```js
db.blog.posts.insertOne(
	{
		"title": "A Blog Post",
		"content" : "...",
		"author": {
			"name": "joe",
			"email": "joe@example.com"
		}
	}
);
db.blog.posts.findOne();
```
![](imgs/Pasted%20image%2020260725134203.png)

`$set` 제한자를 사용해서 `author.name` 을 "joe"에서 "joe schmoe"로 변경하고자 합니다.
```js
db.blog.posts.updateOne({"author.name": "joe"}, {"$set": {"author.name": "joe schmoe"}});
db.blog.posts.findOne();
```
실행 결과를 보면 내장 도큐먼트인 author의 name 키값이 변경되었다.
![](imgs/Pasted%20image%2020260725134429.png)

갱신 연산자가 작동하지 않는 경우
- 갱신 연산자를 포함하지 않는 경우

다음 실행 결과를 보면 `$set`을 사용하지 않고 사용하다가 에러가 발생한 모습이다.
```js
db.blog.posts.updateOne({"author.name": "joe"}, {"author.name": "joe.schmoe"})
```
![](imgs/Pasted%20image%2020260725135017.png)


#### 증가와 감소 (`$inc`)
`$inc` 연산자
- 이미 존재하는 키의 값을 변경하거나 새 키를 생성하는데 사용됨
- 분석, 분위기, 투표 등과 같이 자주 변하는 수치 값을 갱신하는데 매우 유용함
- `$inc` 연산자는 `int`, `long`, `double`, `decimal`  타입 값에만 사용할 수 있다.

게임 컬렉션 생성
```js
db.games.insertOne({"game": "pinball", "user": "joe"})
```
![](imgs/Pasted%20image%2020260725140351.png)

`$inc` 연산자를 사용해서 joe 플레이어의 핀볼 점수 50점 추가하기
```js
db.games.updateOne({"game": "pinball", "user": "joe"}, {"$inc": {"score": 50}})
db.games.findOne();
```
실행 결과를 보면 score 키 값이 새로 추가되었다.
![](imgs/Pasted%20image%2020260725140552.png)

joe 플레이어의 핀볼 기존 점수에 10,000점 추가하기
```js
db.games.updateOne({"game": "pinball", "user": "joe"}, {"$inc": {"score": 10000}})
db.games.findOne();
```
실행 결과를 보면 기존 점수 50점에 추가 10,000점이 추가되어 최종 10,050점이 되었다.
![](imgs/Pasted%20image%2020260725140708.png)

`$inc` 연산자를 사용할 수 없는 경우
- `$inc` 연산자로 숫자 타입이 아닌 키값을 증감시키려고 하는 경우
```js
db.strcounts.insert({"count": "1"})
```
![](imgs/Pasted%20image%2020260725141117.png)

count 키값을 1 증가시켜본다.
```js
db.strcounts.updateMany({}, {"$inc": {"count": 1}})
```
실행 결과를 보면 count 키값이 숫자 타입이 아닌 문자열 타입이어서 에러가 발생하엿다고 한다. 
![](imgs/Pasted%20image%2020260725141514.png)

#### 배열 연산자

##### 요소 추가하기 (`$push`)
`$push` 연산자 사용시 배열이 이미 존재하면 배열 끝에 요소를 추가함. 존재하지 않으면 새로운 배열을 생성한다.

기본 블로그 게시물 데이터 생성하기
```js
db.blog.posts.insertOne(
	{
		"title": "A blog post",
		"content": "..."
	}
);
db.blog.posts.findOne();
```
![](imgs/Pasted%20image%2020260725142431.png)

존재하지 않는 comments 키에 배열 생성 및 댓글 데이터 추가
```js
db.blog.posts.updateOne({"title": "A blog post"}, {
		"$push": {
			"comments": {
				"name": "joe",
				"email": "joe@example.com",
				"content": "nice post."
			}
		}
	}
);
db.blog.posts.findOne();
```
실행 결과를 보면 comments 키에 배열이 생성되었고 하나의 comment 데이터가 추가된 것을 볼수 있다.
![](imgs/Pasted%20image%2020260725142857.png)

댓글을 더 추가하기
```js
db.blog.posts.updateOne({"title": "A blog post"}, {
		"$push": {
			"comments": {
				"name": "bob",
				"email": "bob@example.com",
				"content": "good post."
			}
		}
	}
);
db.blog.posts.findOne();
```
실행 결과를 보면 기존 생성되었던 comments 키 값의 배열에 bob 댓글이 추가되었다.
![](imgs/Pasted%20image%2020260725143222.png)

---

`$push` 연산자에 `$each` 제한자 사용

종목 정보 데이터 생성하기
```js
db.stock.ticker.insertOne(
	{
		"_id": "GOOG"
	}
);
db.stock.ticker.findOne();
```
![](imgs/Pasted%20image%2020260725143707.png)

`$each` 제한자를 사용해서 hourly 키에 배열을 추가하고 여러개의 요소들을 추가하기
```js
db.stock.ticker.updateOne({"_id": "GOOG"}, {"$push": {
			"hourly": {
				"$each": [562.776, 562.790, 559.123]
			}
		}
	}
);
db.stock.ticker.findOne();
```
![](imgs/Pasted%20image%2020260725144245.png)

---

`$push` 연산자에 `$slice` 제한자 사용
배열을 특정 길이로 늘리기 위해서 `$slice` 제한자를 `$push` 연산자와 결합해 사용할 수 있다. 배열이 특정 크기 이상으로 늘어나지 않게 하고 효과적으로 "top N" 목록을 만들 수 있다.

영화 데이터 추가하기
```js
db.movies.drop();
db.movies.insertOne(
	{
		"genre": "horror"
	}
);
db.movies.findOne();
```
![](imgs/Pasted%20image%2020260725144943.png)

`$push` 연산자와 `$each` 제한자를 사용해서 장르가 호러인 도큐먼트에 top10을 추가하되 `$slice` 제한자를 사용해서 최대 10개까지만 유지하도록 한다.
- 추가후에 배열 요소의 개수가 10개보다 작으면 모든 요소 유지됨
- 10보다 크면 마지막 10개 요소만 유지됨
```js
db.movies.updateOne({"genre": "horror"}, {"$push": {
		"top10": {
			"$each": ["Nightmare on Elm Street", "Saw"],
			"$slice": -10
		}
	}
});
db.movies.findOne();
```
![](imgs/Pasted%20image%2020260725145332.png)

`$push` 연산자와 `$sort` 제한자를 사용하여 정렬하기
- rating 키를 기준으로 내림차순 정렬
```js
db.movies.drop();
db.movies.insertOne(
	{
		"genre": "horror"
	}
);
db.movies.updateOne({"genre": "horror"}, {"$push": {
		"top10": {
			"$each": [
				{
					"name": "Nightmare on Elm Street",
					"rating": 6.6
				}, 
				{
					"name": "Saw",
					"rating": 4.3
				}
			],
			"$slice": -10,
			"$sort": {"rating": -1}
		}
	}
});
db.movies.findOne();
```
![](imgs/Pasted%20image%2020260725150246.png)

##### 배열을 집합으로 사용하기
특정 값이 배열에 존재하지 않을때 해당 값을 추가하기

```js
db.papers.insertMany([ 
	{ 
		"_id": 1, 
		"title": "Deep Learning Fundamentals", 
		"authors cited": ["Knuth", "Turing"] // 💡 "Richie"가 없으므로 updateOne 실행 시 "Richie"가 $push 됨 
	}, 
	{ 
		"_id": 2, 
		"title": "Operating System Concepts", 
		"authors cited": ["Richie", "Thompson"] // 💡 이미 "Richie"가 있으므로 updateOne 조건($ne)에 안 맞아 변경되지 않음 
	} 
]);
db.papers.find();
```
![](imgs/Pasted%20image%2020260725151350.png)

예를 들어 인용 목록(authors cited)에 저자가 존재하지 않을때만 "Richie" 저자를 추가한다.
```js
db.papers.updateOne({"authors cited" : {"$ne": "Richie"}}, {"$push": {
			"authors cited": "Richie"
		}
	}
);
db.papers.find();
```
![](imgs/Pasted%20image%2020260725151408.png)

---

`$addToSet`을 사용하여 `$ne` 제한자와 같은 기능 수행하기

사용자 데이터 준비하기
```js
db.users.drop();
db.users.insertOne(
	{
		"username": "joe",
		"emails": [
			"joe@example.com",
			"joe@gmail.com",
			"joe@yahoo.com"
		]
	}
);
db.users.findOne();
```
![](imgs/Pasted%20image%2020260725152133.png)

`$addToSet` 제한자를 사용하면 emails에 이메일을 추가할때 중복된 이메일 추가를 막을 수 있다.
```js
db.users.updateOne({"_id" : ObjectId("6a6455e56884401997d0f1c8")}, {
	$addToSet: {"emails": "joe@gmail.com"}
});
db.users.findOne();
```
실행 결과를 보면 중복된 이메일 값인 `joe@gmail.com` 값을 추가해도 emails의 개수는 4개가 아닌 3개를 유지한다.
![](imgs/Pasted%20image%2020260725152318.png)

이번에는 새로운 이메일을 추가해본다.
```js
db.users.updateOne({"_id" : ObjectId("6a6455e56884401997d0f1c8")}, {
	$addToSet: {"emails": "joe@hotmail.com"}
});
db.users.findOne();
```
실행 결과를 보면 `joe@hotmail.com`은 중복되지 않았기 때문에 정상적으로 추가된다.
![](imgs/Pasted%20image%2020260725152556.png)

---

`$addToSet` 과 `$each` 제한자를 결합하여 고유한 값들을 배열에 추가하기
예를 들어 이메일 주소를 2개 이상 추가하려면 다음과 같이 실행합니다.
```js
db.users.updateOne({"_id" : ObjectId("6a6455e56884401997d0f1c8")}, {
	$addToSet: {
		"emails": {
			"$each": ["joe@php.net", "joe@example.com", "joe@python.org"]
		}
	}
});
db.users.findOne();
```
![](imgs/Pasted%20image%2020260725153652.png)

##### 요소 제거하기
`$pop` 연산자
- 배열을 큐나 스택처럼 사용하려면 배열의 양쪽 끝에서 제거하는 연산자
- `{"$pop": {"key": 1}}` : 배열의 마지막부터 요소를 제거
- `{"$pop": {"key": -1}}` : 배열의 처음부터 요소를 제거

`$pull` 연산자
- 지정된 조건에 따라서 요소를 제거하는 연산자
- 조건에 맞는 모든 요소를 제거함

예를 들어 lists 컬렉션의 모든 도큐먼트들을 대상으로 해당 도큐먼트의 todo 배열에서 값이 "laundry"인 요소를 제거한다.
```js
db.lists.insertOne({"todo": ["dishes", "laundry", "dry cleaning"]});

db.lists.updateOne({}, {"$pull": {"todo": "laundry"}});

db.lists.findOne();
```
실행 결과를 보면 "laundry" 요소가 삭제되었다.
![](imgs/Pasted%20image%2020260725154750.png)

##### 배열의 위치 기반 변경

```js
db.blog.posts.drop();
db.blog.posts.insertOne(
	{
		"content": "...",
		"comments": [
			{
				"comment": "good post",
				"author": "John",
				"votes": 0
			},
			{
				"comment": "i thought it was too short",
				"author": "Claire",
				"votes": 3
			},
			{
				"comment": "free watches",
				"author": "Alice",
				"votes": -5
			},
			{
				"comment": "vacation getaways",
				"author": "Lynn",
				"votes": -7
			}
		]
	}
)

db.blog.posts.findOne()
```
![](imgs/Pasted%20image%2020260727153946.png)

첫번째 댓글의 투표수 증가시키기
```js
db.blog.posts.updateOne({"_id": ObjectId("6a66fd27001bc8bb29b9e087")}, 
	{
		"$inc" : 
		{
			"comments.0.votes": 1
		}
	}
);
db.blog.posts.find()
```
![](imgs/Pasted%20image%2020260727155052.png)
![](imgs/Pasted%20image%2020260727155102.png)

위 예시는 배열의 몇번째 투표수를 증가시킬지 알고 있기 때문에 가능하다. 이 문제를 해결하기 위해서는 몽고 DB에서는 **쿼리 도큐먼트와 일치하는 배열 소오 및 요소의 위치를 알아내서 갱신하는 위치 연산자 `$`** 를 제공한다.

"John"이라는 사용자가 이름을 "Jim"으로 갱신하려고 할때 댓글 내 해당 항목 ("author")을 갱신하기
- comments 배열의 author 키 값이 "John"인 내장 도큐먼트들을 찾은 다음에 "$" 부분에 배열의 인덱스 위치를 넣는 개념. 
- 찾은 도큐먼트의 author 키값을 "Jim"으로 변경하기
- updateOne을 사용하였기 때문에 위치 연산자가 첫번째로 일치하는 요소만 갱신함
```js
db.blog.posts.updateOne({"comments.author" : "John"}, {"$set" : {"comments.$.author" : "Jim"}})
db.blog.posts.find()
```
![](imgs/Pasted%20image%2020260727155552.png)
![](imgs/Pasted%20image%2020260727155607.png)

##### 배열 필터를 이용한 갱신
- 몽고 DB 3.6에서는 개별 배열 요소를 갱신하는 배열 필터인 `arrayFilters`를 도입함, 특정 조건에 맞는 배열 요소를 갱신할 수 있음

반대표가 5표 이상인 댓글을 숨기기
```js
db.blog.posts.updateOne(
	{"_id" : ObjectId("6a66fd27001bc8bb29b9e087")},
	{$set : {"comments.$[elem].hidden": true}},
	{
		arrayFilters: [
			{"elem.votes": {$lte: -5}}
		]
	}
);
db.blog.posts.find()
```

실행 결과를 보면 votes 키의 값이 -5 이하인 "Alice", "Lynn" 작성자 댓글의 도큐먼트에 "hidden" 키가 추가되었다.
![](imgs/Pasted%20image%2020260727161402.png)

### 3.3.3 갱신 입력
- 갱신 입력 : 갱신 조건에 맞는 도큐먼트가 없으면 새로운 도큐먼트를 생성함. 조건에 맞는 도큐먼트를 발견하면 일반적인 갱신 기능을 수행한다.
- SaveOrUpdate 기능을 한번에 수행함
- 갱신 입력 장점
	- 시드 도큐먼트가 필요없음. 시드 도큐먼트는 초기 데이터를 의미함
	- 같은 코드로 도큐먼트를 생성하고 갱신할 수도 있음

#### `{"upsert": true}`
갱신 입력을 사용하지 않고 갱신 입력 기능을 수행하는 예시
- 기존 도큐먼트 데이터가 존재하지 않으면 `insertOne`을 이용하여 새로운 도큐먼트 생성
- 만약 기존 도큐먼트가 존재하면 pageviews를 1 증가시키고 `save`을 이용하여 갱신 수행함
```js
db.analytics.drop();
// 이 페이지에 대한 항목이 있는지 확인
blog = db.analytics.findOne({url: "/blog"})

// 항목이 존재하면 조회수에 1을 더하고 저장
if(blog){
	blog.pageviews++;
	db.analytics.save(blog);
}
// 항목이 없으면 이 페이지에 대한 새로운 도큐먼트 생성
else{
	db.analytics.insertOne({url: "/blog", pageviews : 1});
}
```
![](imgs/Pasted%20image%2020260727162531.png)

```js
db.analytics.findOne()
```
실행 결과를 보면 데이터가 아예 존재하지 않기 때문에 새로운 도큐먼트를 생성한다.
![](imgs/Pasted%20image%2020260727162552.png)

위 자바스크립트 코드 문제점
- 페이지를 확인하기 위해서 매번 데이터베이스를 왕복해야하고, 갱신이나 삽입을 보내야 한다.
- 위 코드를 여러 프로세스에서 실행하면 주어진 URL("/blog")에 2개 이상의 도큐먼트가 동시에 삽입되는 **경쟁 상태(race condition)**가 될수도 있다.

갱신 입력을 통한 문제 해결
- 코드를 줄이고 경쟁 상태를 회피할 수 있다.
- `updateOne`과 `updateMany`의 세번째 매개변수로 갱신 입력을 설정하여 갱신 입력을 수행한다
- 갱신 입력이 더 빠르고 원자적인 연산

갱신 입력을 적용하여 해당 데이터 도큐먼트가 존재하지 않으면 새로 도큐먼트를 생성하고, 기존에 조건에 맞는 도큐먼트가 있으면 두번째 매개변수를 이용하여 pageviews를 1 증가시킨다.
```js
db.analytics.updateOne(
	{
		url: "/blog"
	}, 
	{
		"$inc": {"pageviews" : 1}
	}, 
	{
		"upsert": true
	}
);
db.analytics.findOne();
```
실행 결과를 보면 pageviews가 1에서 2로 증가하였다.
![](imgs/Pasted%20image%2020260727164047.png)

갱신 입력을 이용하여 키 값 증가 예시
- "rep"가 25인 도큐먼트를 찾아서 값을 3증가시키는 갱신 입력을 수행함
- 갱신 입력이기 때문에 만약에 도큐먼트가 없으면 "rep"가 25인 새로운 도큐먼트를 만들고 나서 "rep"를 3 증가시켜서 "rep"가 28인 도큐먼트로 갱신합니다.
```js
db.users.updateOne(
	{
		"rep": 25
	},
	{
		"$inc": {
			"rep": 3
		}
	},
	{
		"upsert": true
	}
)
```
![](imgs/Pasted%20image%2020260806105901.png)

다음 실행 결과를 보면 "rep"가 28인 도큐먼트가 새로 생성된 것을 볼수 있습니다.
```js
db.users.findOne({"_id": ObjectId("6a73ea5a80a4d36f520da89c")})
```
![](imgs/Pasted%20image%2020260806110238.png)

**갱신 입력을 지정하지 않으면 어떻게 되는가?**
갱신 입력을 지정하지 않으면 `{"rep": 25}`는 어떤 도큐먼트와도 일치하지 않게 되어 아무일도 발생하지 않습니다.

#### `$setOnInsert`
도큐먼트가 생성될때 필드가 설정돼야 할 때가 있습니다. 이후 갱신에서는 변경되지 않아야 합니다. 이러한 경우에 `$setOnInsert`를 사용합니다. **`$setOnInsert`는 도큐먼트 삽입시 필드값을 설정하는데만 사용하는 연산자**입니다.

다음 예시는 `$setOnInsert`를 사용하여 도큐먼트 생성시 생성일자(createAt)을 추가하는 예시입니다.
```js
db.users.updateOne({}, 
	{
		"$setOnInsert": {
			"createAt" : new Date()
		}
	},
	{
		"upsert": true
	}
);
```

위 예시를 실행전에 결과를 보면 "createAt" 프로퍼티가 존재하지 않는 것을 볼수 있습니다.
```js
db.users.find()
```
![](imgs/Pasted%20image%2020260806112207.png)

**users 컬렉션에서 데이터가 아무것도 없는 상태에서 "$setOnInsert"를 사용 예시**
- users 컬렉션에서 데이터를 비운 상태
- "$setOnInsert"를 사용하여 도큐먼트 새로 생성시 "createdAt" 프로퍼티를 생성하고 생성시간 추가
```js
db.users.drop()
db.users.updateOne({}, 
	{
		"$setOnInsert": {
			"createdAt" : new Date()
		}
	},
	{
		"upsert": true
	}
);
db.users.findOne()
```
실행 결과를 보면 새로운 유저 데이터가 생성되었고 "createdAt"이 정상적으로 추가된 것을 볼수 있습니다.
![](imgs/Pasted%20image%2020260806113612.png)

**users 컬렉션에서 기존 데이터가 있는 상태에서 "$setOnInsert"를 사용한 경우**
- users 컬렉션에 도큐먼트가 존재한 상태
- updateOne의 첫번째 매개변수인 **필터 도큐먼트**에 빈 도큐먼트(`{}`)를 넣어서 이전 쿼리 예시와 동일하게 도큐먼트 생성시 "createdAt"을 추가를 시도합니다.

```js
db.users.updateOne({}, 
	{
		"$setOnInsert": {
			"createdAt" : new Date()
		}
	},
	{
		"upsert": true
	}
);
db.users.find()
```
실행 결과를 보면 새로운 도큐먼트는 생성되지 않았고 기존에 추가한 도큐먼트의 "createdAt" 데이터 또한 변경되지 않았습니다. 다음과 같은 결과가 나온 이유는 다음과 같습니다.
- MongoDB가 빈 도큐먼트 `{}` 조건으로 데이터를 찾습니다.  `{}`는  "조건 없이 아무 도큐먼트나 찾는다"는 의미입니다.
- 쿼리 실행 결과를 보면 **기존에 존재하는 첫번째 도큐먼트를 즉시 찾아냅니다. (Match 성공)**
- Upsert 불발: 조건에 맞는 기존 데이터가 이미 존재하기 때문에, MongoDB는 이 작업을 **"생성(Insert)"이 아닌 "수정(Update)" 작업으로 진행**합니다.
- `$setOnInsert` 무시 : "수정" 작업으로 진행되는데, 쿼리에는 `$setOnInsert`만 들어있습니다. `$setOnInsert` 연산자는 수정 작업시 무시되므로, 기존 데이터에 아무런 변경도 일어나지 않고 작업이 끝납니다. (새로운 데이터 생성도 일어나지 않습니다.)
![](imgs/Pasted%20image%2020260806115511.png)
![](imgs/Pasted%20image%2020260806115525.png)

`$setOnInsert`를 정리하면 다음과 같습니다.
- `update`  쿼리 사용시 `$setOnInsert`를 사용하는 경우 새로운 도큐먼트가 생성되는 경우에 적용된다.
- `$setOnInsert`는 기존 도큐먼트 데이터에 영향을 미치지 않습니다.
- `$setOnInsert`를 사용하면 새로운 도큐먼트 생성시 적용된다.
- `$setOnInsert`는 패딩(padding)을 생성하고 카운터를 초기화하는데 사용되며, `ObjectId`를 사용하지 않는 컬렉션에 유용함
	- **패딩(Padding)** : 디스크에 도큐먼트를 저장할때, 데이터 크기가 나중에 커질것을 대비해 도큐먼트 뒤에 얼마간의 "여유 빈 공간(패딩)"을 붙여서 저장하는 것을 말합니다.

만약 여유 공간이 없는데 나중에 `update` 작업으로 새로운 필드가 추가되어 도큐먼트 크기가 커지는 경우 어떻게 되는가?
1. 기존 디스크 공간에 데이터가 더이상 들어가지 않음
2. MongoDB는 해당 도큐먼트를 디스크의 전혀 다른 위치로 이동(Reallocation) 시켜야함.
3. 이 과정에서 인덱스 재갱신, 디스크 I/O 증가, 메모리 파편확가 발생하여 전체적인 성능이 급격히 떨어집니다.

**`$setOnInsert`로 패딩을 생성한다는 것의 의미**
새로운 데이터가 처음 저장(insert)될 때, 나중에 수정(update)하면서 채워 넣을 가짜/기본 필드들을 미리 큰 크기로 만들어 둠으로써 디스크 공간을 미리 넉넉하게 확보(패딩)하는 기법입니다.
이때 `$setOnInsert`를 사용하는 이유는 데이터가 처음 생성될때 단 한번만 이 여유 공간(패딩 필드)을 만들어주고, 이후에 수정(update)될 때는 그 공간을 건드리지 않고 내부 값만 채워 넣기 위함입니다.

예를 들어 사용자 활동 로그나 카운터를 기록하는 도큐먼트를 만든다고 가정합니다.
```js
db.getCollection("stats").updateOne(
	{
		"_id": "user_123"
	},
	{
		"$inc": { count: 1 },
		"$setOnInsert": {
			createdAt: new Date(),
			paddingSpace: "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
		}
	},
	{ upsert : true }
)
```
- `"$inc": {count:1}` : 수정할때마다 카운터 1씩 증가시킴
- `$setOnInsert` : 최초 생성(insert) 될때만, 패딩용 여유 필드 및 초기값 세팅
- `paddingSpace` : 100byte 짜리 의미 없는 문자열로 디스크에 여유 공간(패딩)을 미리 확보해둠


```js
db.getCollection("stats").findOne()
```
![](imgs/Pasted%20image%2020260806125355.png)
- 최초 실행시 (insert) : `$setOnInsert`에 의해 `paddingSpace`라는 큰 필드가 함께 생성되면서, 디스크 상에 처음부터 넉넉한 공간을 가진 도큐먼트가 생성됨
- 이후 실행시 (update) : `$setOnInsert`는 무시되고 `$inc`만 실행됩니다. 이미 디스크에 공간이 넉넉히 확보되어 있으므로 도큐먼트 이동 없이 제자리(in-place)에서 빠르게 업데이트가 처리됩니다.

### 3.3.4 다중 도큐먼트 갱신
- updateOne 쿼리는 필터 조건에 맞는 첫번째 도큐먼트만 갱신함
- updateMany 쿼리는 필터 조건에 맞는 모든 도큐먼트를 갱신함
- updateMany는 스키마를 변경하거나 특정 사용자에 새로운 정보를 추가할때 쓰기 좋음

updateMany 예시
특정 날짜에 생일을 맞이하는 모든 사용자에게 선물을 준다고 가정합니다. updateMany를 사용해서 계정에 "gift"를 추가합니다.
```js
db.users.drop()
db.users.insertMany(
	[
		{birthday: "10/13/1978"},
		{birthday: "10/13/1978"},
		{birthday: "10/13/1978"}
	]
)
db.users.find()
```
![](imgs/Pasted%20image%2020260806133245.png)
![](imgs/Pasted%20image%2020260806133257.png)

```js
db.users.updateMany(
	{
		"birthday": "10/13/1978"
	},
	{
		"$set" : { "gift": "Haapy Birthday!" }
	}
)
db.users.find()
```
![](imgs/Pasted%20image%2020260806133430.png)
![](imgs/Pasted%20image%2020260806133442.png)

위 실행 결과를 보면 "birthday" 키가 "10/13/1978"인 사용자 3명의 도큐먼트 데이터에 "gift" 키가 추가되었습니다.

### 3.3.5 갱신한 도큐먼트 반환
수정된 도큐먼트를 반환하기 위해서 다음과 같은 연산자를 사용할 수 있습니다.
- `fineOneAndDelete` : 특정 도큐먼트를 찾은 다음에 삭제하는 연산자
- `fineOneAndReplace` : 특정 도큐먼트를 찾은 다음에 새로운 도큐먼트로 대체하는 연산자
- `fineOneAndUpdate` : 특정 도큐먼트를 찾은 다음에 갱신하는 연산자

`updateOne`과 차이점
- 사용자가 수정된 도큐먼트의 값을 원자적으로 얻을 수 있음

구성할 수 있는 파이프라인
- `$set`
- `$unset`
- `$replaceWith`

**`fineOneAndUpdate`를 사용하지 않고 사용하는 예시**
특정 순서대로 실행하는 프로세스 컬렉션이 있다고 가정합니다. 형식은 다음과 같습니다.
```json
{
	"_id": ObjectId("abcd"),
	"status": "READY",
	"priority": N
}
```
- status는 문자열이며 "READY", "RUNNING", "DONE" 상태 값을 가집니다.

위 형식을 기반으로 예시에서는 우선순위가 가장 높은 "READY" 상태의 작업을 찾아서 프로세스를 실행하고 "status"를 "DONE"으로 갱신해야 합니다.
상태가 "READY"인 프로세스를 찾아서 우선순위가 가장 높은 프로세스의 상태를 "RUNNING"으로 갱신합니다. 프로세스가 끝나면 "status"를 "DONE"으로 갱신합니다.
위 설명에 대한 과정을 스크립트로 표현하면 다음과 같습니다.
```js
db.processes.drop();
db.processes.insertOne(
	{
		"status" : "READY",
		"priority": 1
	}
);
db.processes.insertOne(
	{
		"status" : "READY",
		"priority": 2
	}
);
db.processes.insertOne(
	{
		"status" : "READY",
		"priority": 3
	}
);
```
![](imgs/Pasted%20image%2020260806141840.png)

프로세스 상태 값 변경 예시
```js
function do_something(process) { 
	console.log(`[작업 시작] Process ID: ${process._id}, Priority: ${process.priority} 작업을 처리 중입니다...`); 
}

var cursor = db.processes.find({"status" : "READY"});
ps = cursor.sort({"priority": -1})
			.limit(1)
			.next();
db.processes.updateOne(
	{"_id": ps._id},
	{"$set": {"status": "RUNNING"}}
);
do_something(ps);
db.processes.updateOne(
	{"_id": ps._id},
	{"$set" : {"status": "DONE"}}
)

db.processes.find()
```
1. processes 컬렉션에서 "status" 필드가 "READY" 값을 가진 도큐먼트들을 조회함
2. 조회한 도큐먼트들을 "priority" 필드를 기준으로 내림차순으로 정렬한 다음에 제일 앞에 위치한 프로세스를 반환함
3. 최우선 프로세스의 상태값을 "READY"에서 "RUNNING"으로 변경함
4. 프로세스를 실행함
5. 프로세스 실행을 마치고 나서 "status" 필드값을 "DONE"으로 변경함

실행 결과를 보면 "priority" 필드값이 3을 가진 프로세스 도큐먼트의 status가 "DONE"으로 변경된 것을 볼수 있습니다.
![](imgs/Pasted%20image%2020260806142111.png)

**문제점**
**경쟁 상태(Race Condition)** 발생할 수 있습니다. 예를 들어 스레드 A가 먼저 도큐먼트를 얻고 "status"를 "RUNNING"으로 변경하기 전에, 스레드 B가 같은 도큐먼트를 받으면 두개의 스레드가 같은 프로세스를 실행하게 됩니다.

다음 스크립트는 갱신 쿼리의 일부로 결과를 확인해서 경쟁 상태를 피하는 스크립트입니다.
- 다음 예시는 processes 컬렉션의 도큐먼트를 전부 제거한 다음에 다시 3개의 도큐먼트로 초기화한 상태에서 수행한 예시
```js
var cursor = db.processes.find(
	{"status": "READY"}
);
cursor.sort({"priority": -1}).limit(1);
while((ps = cursor.next()) != null){
	var result = db.processes.updateOne(
		{
			"_id": ps._id,
			"status": "READY"
		},
		{
			"$set": {"status": "RUNNING"}
		}
	);
	if(result.modifiedCount === 1){
		do_something(ps);
		db.processes.updateOne(
			{
				"_id": ps._id
			},
			{
				"$set": {"status": "DONE"}
			}
		);
		break;
	}
	cursor = db.processes.find({"status": "READY"})
	cursor.sort({"priority": -1}).limit(1);
}
```
1. 스레드 A,B가 동시에 `fine()`을 실행해서 같은 프로세스(`ps`)를 동시에 조회했다고 가정합니다.
2. 스레드 A가 미세하게 먼저 `updateOne`을 실행합니다. `status`가 `READY`이므로 성공적으로 `RUNNING`으로 변경됩니다.
3. 스레드 B가 뒤이어 `ps._id`로 `updateOne`을 실행합니다. 하지만 데이터베이스 상에서 이 도큐먼트의 `status`는 **이미 스레드 A에 의해서 `RUNNING`으로 변경된 상태**입니다.
4. 따라서 스레드 B의 `updateOne` 조건인 `{"status": "READY"}` 조건에 매칭되지 않아서 업데이트에 실패합니다.

**`modifiedCount === 1` 을 통한 원자적(Atomic) 선점 확인**
MongoDB의 단일 도큐먼트 업데이트(`updateOne`)는 **원자적(Atomic)으로 처리**됩니다. 즉, 여러개의 `updateOne` 요청이 들어와도 DB 내부에서 순서대로 처리됩니다.
- 스레드 A : 업데이트 성공 -> `result.modifiedCount`는 `1`
- 스레드 B : 조건 미충적으로 수정 실패 -> `result.modifiedCount`는 `0`
스레드 B는 `result.modifiedCount === 1` 조건문 검사에서 `false` 판정을 받게 되서, `do_something(ps)` **작업을  실행하지 못하고 스킵**하게 됩니다. 이로 인해서 동일한 작업을 두 스레드가 중복해서 실행하는 상황(경쟁 상태)이 방지됩니다.

**실패시 재시도 루프(`while` & cursor 재조회)**
선점에 실패한 스레드 B는 `if`문을 지나쳐서 아래 구문을 실행합니다.
```js
cursor = db.processes.find({"status": "READY"});
cursor.sort({"priority": -1}).limit(1);
```
이미 다른 스레드(A)가 가져가 버린 작업은 포기하고, 아직 아무것도 가져가지 않은 다음 `READY` 상태의 우선순위 작업을 새로 조회하여 작업을 이어갑니다.

**`fineOneAndUpdate` 연산자를 이용한 문제 해결**
`fineOneAndUpdate` 연산자를 이용하면 위 예시에서 보았던 긴 스크립트 코드를 사용하지 않고 한번의 연산으로 반환하고 갱신을 수행할 수 있다.
- 다음 예시는 processes 컬렉션의 도큐먼트를 전부 제거한 다음에 다시 3개의 도큐먼트로 초기화한 상태에서 수행한 예시
```js
db.processes.findOneAndUpdate(
	{"status": "READY"},
	{"$set": {"status": "RUNNING"}},
	{"$sort": {"priority": -1}}
);
```
실행 결과를 보면 기본적으로 도큐먼트의 상태를 수정하기 전에 데이터를 반환합니다. 반한된 도큐먼트의 상태를 보면 "status" 필드가 "READY" 상태입니다.
![](imgs/Pasted%20image%2020260806152315.png)

`findOneAndUpdate` 연산자의 `returnNewDocument` 필드를 이용한 갱신된 도큐먼트 반환하기
- 3번째 매개변수에 "returnNewDocument" 필드에 `true`를 설정하면 연산자의 반환값으로 갱신된 후의 도큐먼트 데이터를 반환합니다. 
- 다음 예시는 processes 컬렉션의 도큐먼트를 전부 제거한 다음에 다시 3개의 도큐먼트로 초기화한 상태에서 수행한 예시
```js
db.processes.findOneAndUpdate(
	{"status": "READY"},
	{"$set": {"status": "RUNNING"}},
	{
		"$sort": {"priority": -1},
		"returnNewDocument": true
	}
);
```
실행 결과를 보면 반환된 도큐먼트의 "status" 필드값이 "RUNNING"인것을 볼수 있다.
![](imgs/Pasted%20image%2020260806153235.png)

**최종 프로그램 스크립트**
```js
var ps = db.processes.findOneAndUpdate(
	{"status": "READY"},
	{"$set": {"status": "RUNNING"}},
	{
		"$sort": {"priority": -1},
		"returnNewDocument": true
	}
);
do_something(ps)
db.process.updateOne(
	{"_id": ps._id},
	{"$set": {"status": "DONE"}}
)
```

**`findOneAndReplace` 연산자**
- `findOneAndReplace`는 `fineOneAndUpdate`와 동일한 매개변수를 사용하고 `returnNewDocument` 값을 설정 가능함

**`findOneAndDelete` 연산자**
- 매개변수 구성
	- 첫번째 매개변수 : 조건 도큐먼트
	- 두번째 매개변수 : 옵션 도큐먼트
- `findOneAndDelete` 연산자를 수행하면 삭제된 도큐먼트를 반환합니다.

