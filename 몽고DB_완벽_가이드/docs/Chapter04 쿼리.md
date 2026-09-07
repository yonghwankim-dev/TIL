
## 학습 목표
- $ 조건절을 이용해 범위 쿼리, 셋의 포함관계, 부동 관계 쿼리 등을 수행한다.
- 쿼리는 필요할때마다 도큐먼트 배치(batch)을 반환하는 데이터베이스 커서(database cursor)를 반환한다.
- 커서를 이용해 결과를 몇개 건너뛰거나, 반환하는 결과 수를 제한하거나 결과를 정렬하는 등 다양한 메타 연산을 수행한다.

## 4.1 find 소개
users 컬렉션 내 모든 도큐먼트 반환
```js
db.users.find()
```

age가 27인 도큐먼트 찾기
```js
db.users.find({"age": 27})
```

username이 "joe"인 도큐먼트 찾기
```js
db.users.find({"username": "joe"})
```

username이 "joe"이고 age가 27인 도큐먼트 찾기
```js
db.users.find(
	{
		"username": "joe", 
		"age": 27
	}
)
```

### 4.1.1 반환받을 키 지정
find 연산자의 두번째 매개변수에 받고자 하는 키만을 설정하여 도큐먼트의 키값 데이터를 선택적으로 받을수 있다. 이렇게 하면 도큐먼트를 디코딩하는데 드는 시간과 메모리를 줄여준다.
두번째 매개변수에 별도로 "_id"를 설정하지 않으면 기본적으로 항상 같이 반환된다.

데이터 초기화
```js
db.users.drop();
db.users.insertMany([
	{
		"username": "joe",
		"email": "joe@example.com",
		"phone": "010-1111-1111"
	},
	{
		"username": "bob",
		"email": "bob@example.com",
		"phone": "010-2222-2222"
	},
	{
		"username": "bread",
		"email": "bread@example.com",
		"phone": "010-3333-3333"
	}
]);
```

username과 email 키의 값만 조회하는 경우
```js
db.users.find({}, {"username": 1, "email": 1})
```
![](imgs/Pasted%20image%2020260806161625.png)

fatal_weakness 키 값 제외하면서 조회하기
- 값을 0으로 설정하면 제외 가능하다.
```js
db.users.find({}, {"fatal_weakness": 0})
```

"_id" 값을 반환하는 것을 제외하기
```js
db.users.find({}, {"username": 1, "_id": 0})
```
![](imgs/Pasted%20image%2020260806162130.png)

### 4.1.2 제약 사항
- 데이터베이스에서 쿼리 도큐먼트 값은 반드시 상수여야 함
	- 작성한 코드 안에서 일반 변수는 상관없음
	- 도큐먼트 안에 다른 키의 값을 참조할 수 없음을 의미함

```js
db.stock.find({"in_stock": "this.num_sold"}) // 작동하지 않음
```

## 4.2 쿼리 조건
OR 쿼리 방법
- `$in` : 하나의 키를 다양한 값과 비교하는 쿼리
- `$or` : 여러 키를 주어진 값과 비교하는 쿼리

raffle 컬렉션 도큐먼트 초기화
```js
db.raffle.drop();
db.raffle.insertMany(
	[
		{"ticker_no": [725, 542, 390]},
		{"ticker_no": [1,2,3]},
		{"ticker_no": [725, 542, 390]}
	]
)
```

당첨 번호(ticker_no)가 725, 542, 390인 당첨자를 찾는 쿼리
```js
db.raffle.find(
	{
		"ticker_no": {
			"$in": [725, 542, 390]
		}
	}
);
```
![](imgs/Pasted%20image%2020260806163515.png)

사용자 아이디(user_id)가 12345이거나 "joe"인 사용자를 조회하기
- 다음과 같이 "user_id" 키 값이 숫자든 문자열이든 상관없이 조회가 가능하다.
```js
db.users.find(
	{
		"user_id": 
			{
				"$in": [12345, "joe"]
			}
	}
)
```

추첨에서 당첨되지 않은 사용자들 조회하기 (`$nin`)
- `$nin`은 `$in`과 반대로 배열 내 조건과 일치하지 않는 도큐먼트를 반환한다.
```js
db.raffle.find(
	{
		"ticker_no" : {
			"$nin" : [725, 542, 390]
		}
	}
)
```
![](imgs/Pasted%20image%2020260807161047.png)

당첨번호가 725이거나 "winner"가 true인 도큐먼트 찾기
```js
db.raffle.find(
	{
		"$or": [{"ticker_no": 725}, {"winner": true}]
	}
)
```
![](imgs/Pasted%20image%2020260807161302.png)

당첨번호(ticker_no)가 세 번호 중 적어도 하나와 일치하거나 "winner"가 true인 경우를 찾기
```js
db.raffle.find(
	{
		"$or": [
			{"ticker_no" : {"$in": [725, 542, 390]}},
			{"winner": true}
		]
	}
)
```
![](imgs/Pasted%20image%2020260807161519.png)

### 4.2.3 `$not`
id_num 키값이 5로 나누었을때 1인 도큐먼트들을 조회하기
```js
db.users.find(
	{
		"id_num": {"$mod": [5,1]}
	}
)
```
```js
[
  {
    _id: ObjectId('6a7434c028fefb64e3f019f6'),
    username: 'joe',
    email: 'joe@example.com',
    phone: '010-1111-1111',
    id_num: 1
  },
  {
    _id: ObjectId('6a758836fe656228a498d37b'),
    username: 'david',
    email: 'david@example.com',
    phone: '010-6666-6666',
    id_num: 6
  },
  {
    _id: ObjectId('6a758836fe656228a498d380'),
    username: 'ian',
    email: 'ian@example.com',
    phone: '010-2020-2020',
    id_num: 11
  },
  {
    _id: ObjectId('6a758836fe656228a498d385'),
    username: 'nathan',
    email: 'nathan@example.com',
    phone: '010-7070-7070',
    id_num: 16
  },
  {
    _id: ObjectId('6a758836fe656228a498d38a'),
    username: 'sam',
    email: 'sam@example.com',
    phone: '010-3344-5566',
    id_num: 21
  }
]
```

id_num 키값을 5로 나누었을때 1이 아닌 사용자들을 조회하기 (`$not`)
```js
db.users.find(
	{
		"id_num": {"$not" : {"$mod": [5,1]}}
	}
)
```

```js
[
  {
    _id: ObjectId('6a7434c028fefb64e3f019f7'),
    username: 'bob',
    email: 'bob@example.com',
    phone: '010-2222-2222',
    id_num: 2
  },
  {
    _id: ObjectId('6a7434c028fefb64e3f019f8'),
    username: 'bread',
    email: 'bread@example.com',
    phone: '010-3333-3333',
    id_num: 3
  },
  {
    _id: ObjectId('6a758836fe656228a498d379'),
    username: 'alice',
    email: 'alice@example.com',
    phone: '010-4444-4444',
    id_num: 4
  },
  {
    _id: ObjectId('6a758836fe656228a498d37a'),
    username: 'charlie',
    email: 'charlie@example.com',
    phone: '010-5555-5555',
    id_num: 5
  },
  {
    _id: ObjectId('6a758836fe656228a498d37c'),
    username: 'eva',
    email: 'eva@example.com',
    phone: '010-7777-7777',
    id_num: 7
  },
  {
    _id: ObjectId('6a758836fe656228a498d37d'),
    username: 'frank',
    email: 'frank@example.com',
    phone: '010-8888-8888',
    id_num: 8
  },
  {
    _id: ObjectId('6a758836fe656228a498d37e'),
    username: 'grace',
    email: 'grace@example.com',
    phone: '010-9999-9999',
    id_num: 9
  },
  {
    _id: ObjectId('6a758836fe656228a498d37f'),
    username: 'hannah',
    email: 'hannah@example.com',
    phone: '010-1010-1010',
    id_num: 10
  },
  {
    _id: ObjectId('6a758836fe656228a498d381'),
    username: 'jack',
    email: 'jack@example.com',
    phone: '010-3030-3030',
    id_num: 12
  },
  {
    _id: ObjectId('6a758836fe656228a498d382'),
    username: 'kate',
    email: 'kate@example.com',
    phone: '010-4040-4040',
    id_num: 13
  },
  {
    _id: ObjectId('6a758836fe656228a498d383'),
    username: 'leo',
    email: 'leo@example.com',
    phone: '010-5050-5050',
    id_num: 14
  },
  {
    _id: ObjectId('6a758836fe656228a498d384'),
    username: 'mia',
    email: 'mia@example.com',
    phone: '010-6060-6060',
    id_num: 15
  },
  {
    _id: ObjectId('6a758836fe656228a498d386'),
    username: 'olivia',
    email: 'olivia@example.com',
    phone: '010-8080-8080',
    id_num: 17
  },
  {
    _id: ObjectId('6a758836fe656228a498d387'),
    username: 'peter',
    email: 'peter@example.com',
    phone: '010-9090-9090',
    id_num: 18
  },
  {
    _id: ObjectId('6a758836fe656228a498d388'),
    username: 'quinn',
    email: 'quinn@example.com',
    phone: '010-1122-3344',
    id_num: 19
  },
  {
    _id: ObjectId('6a758836fe656228a498d389'),
    username: 'rachel',
    email: 'rachel@example.com',
    phone: '010-2233-4455',
    id_num: 20
  },
  {
    _id: ObjectId('6a758836fe656228a498d38b'),
    username: 'tina',
    email: 'tina@example.com',
    phone: '010-4455-6677',
    id_num: 22
  },
  {
    _id: ObjectId('6a758836fe656228a498d38c'),
    username: 'victor',
    email: 'victor@example.com',
    phone: '010-5566-7788',
    id_num: 23
  }
]
```

## 4.3 형 특정 쿼리
### 4.3.1 null
데이터 초기화
```js
db.c.drop();
db.c.insertMany([
	{
		"y": null
	},
	{
		"y": 1
	},
	{
		"y": 2
	}
]);
```

`c` 컬렉션의 도큐먼트 전체 조회
```js
db.c.find()
```
![](imgs/Pasted%20image%2020260824142950.png)

"y"키가 null인 도큐먼트를 조회
```js
db.c.find({"y": null});
```
![](imgs/Pasted%20image%2020260824143210.png)

키가 null인 값을 도큐먼트를 조회하는 경우
- null은 "존재하지 않음"과도 일치한다.
	- null 조건으로 필드를 조회할때 필드의 값이 null인 도큐먼트뿐만 아니라 해당 필드(key) 자체가 아예 존재하지 않는 문서까지 함께 검색된다는 뜻입니다.
- 키가 null인 값을 쿼리하면 해당 키를 갖지 않는 도큐먼트도 반환된다.
```js
db.c.find({"z": null})
```
![](imgs/Pasted%20image%2020260824143339.png)

값(value)이 null인 키만 찾고 싶은 경우
- `$exists` : true인 경우 "z" 키가 존재하는 경우에만 조회함
- "z" 키가 존재하고 "z" 키값이 null인 경우만을 조회한다.
```js
db.c.find({"z" : {"$eq" : null, "$exists": true}})
```

"z"키에 대한 값이 null인 데이터를 추가
```js
db.c.insertOne({"z": null});
db.c.find();
```
![](imgs/Pasted%20image%2020260824144023.png)

"z"키에 대한 필드값이 null인 도큐먼트를 조회하기 (단, 키가 존재하는 경우에만)
```js
db.c.find({"z" : {"$eq" : null, "$exists": true}})
```
![](imgs/Pasted%20image%2020260824144155.png)

### 4.3.2 정규 표현식
사용자 이름이 대소문자 구분없이 "joe"로 시작하는 사용자를 조회
```js
db.users.drop();
db.users.insertMany([
	{
		"name": "joe"
	},
	{
		"name": "joea"
	},
	{
		"name": "Joe"
	},
	{
		"name": "John"
	},
]);
db.users.find({"name": {"$regex" : /joe/i }})
```
![](imgs/Pasted%20image%2020260824144625.png)

사용자 이름 패턴이 "joey"인 사용자를 조회하기
- `joey?`에서 `?`를 사용하면 앞의 문자가 0번 또는 1번 등장한다는 의미입니다.
```js
db.users.insertOne(
	{
		"name": "joey"
	}
)
db.users.find({"name": /joey?/i});
```
![](imgs/Pasted%20image%2020260824144941.png)

"bar"키에 대한 필드값이 "/baz/"인 도큐먼트 조회하기
- 정규표현식 또한 스스로와 일치하는 도큐먼트를 찾을 수 있음
- find 사용시 사용한 "/baz/"는 정규식이다. 의미는 "baz"와 일치하는 도큐먼트를 찾는다.
```js
db.foo.insertOne({"bar": /baz/})
db.foo.find({"bar": /baz/})
```
![](imgs/Pasted%20image%2020260824145319.png)

### 4.3.3 배열에 쿼리하기
음식 컬렉션에서 과일(fruit) 키값이 바나나(banana)를 가지고 있는 도큐먼트를 조회
```js
db.food.insertOne(
	{
		"fruit": ["apple", "banana", "peach"]
	}
);
db.food.find({"fruit": "banana"});
```
![](imgs/Pasted%20image%2020260824145850.png)

#### **$all 연산자**
`$all` 연산자를 사용하면 2개 이상의 배열 요소가 일치하는 배열을 찾는데 사용된다.

데이터 준비
```js
db.food.drop();
db.food.insertOne({"_id": 1, "fruit": ["apple", "banana", "peach"]});
db.food.insertOne({"_id": 2, "fruit": ["apple", "kumquat", "orange"]});
db.food.insertOne({"_id": 3, "fruit": ["cherry", "banana", "apple"]});
```

음식(food) 컬렉션에서 과일(fruit) 키값이 "apple"과 "banana"를 전부 가지고 있는 도큐먼트를 조회
- 실행 결과를 보면 배열 요소의 순서는 고려하지 않는다.
```js
db.food.find({ 
		"fruit": {
			$all: ["apple", "banana"]
		}
	}
);
```
![](imgs/Pasted%20image%2020260824150533.png)

음식 컬렉션에서 과일(fruit)이 정확히 "apple", "banana", "peach"를 가지고 있는 도큐먼트를 조회하기
```js
db.food.find({"fruit": ["apple", "banana", "peach"]})
```
![](imgs/Pasted%20image%2020260824151000.png)

음식 컬렉션에서 과일이 정확히 "apple", "banana"를 가지고 있는 도큐먼트를 조회하기
- 실행 결과를 보면 일치하는 도큐먼트가 조회되지 않습니다.
```js
db.food.find({"fruit": ["apple", "banana"]});
```
![](imgs/Pasted%20image%2020260824151144.png)

음식 컬렉션에서 과일이 정확히 "banana", "apple", "peach" 순서로 가지고 있는 도큐먼트를 조회하기
- 실행 결과를 보면 배열 요소의 순서가 정확하지 않으면 도큐먼트는 조회되지 않는다.
```js
db.food.find({"fruit": ["banana", "apple", "peach"]});
```
![](imgs/Pasted%20image%2020260824151937.png)

**key.index 구문**
배열 안에 특정 요소를 쿼리하기 위해서는 `key.index` 구문을 이용해 순서를 지정해야 한다.

음식 컬렉션에서 과일 키값인 배열안에 2번째 요소가 "peach"인 도큐먼트를 조회하기
- 배열의 인덱스는 0번째 부터 시작한다.
```js
db.food.find({"fruit.2": "peach"});
```
![](imgs/Pasted%20image%2020260824152355.png)

#### $size 연산자
`$size` 연산자는 특정 크기의 배열을 쿼리하는데 사용되는 조건절 연산자입니다.

음식 컬렉션에서 과일 키값인 배열의 크기가 3개인 도큐먼트를 조회하기
```
db.food.find({"fruit": {"$size": 3}});
```
![](imgs/Pasted%20image%2020260824152615.png)

`$size`는 다른 `$조건절`과 결합해서 사용할 수 없습니다. 하지만 도큐먼트에 "size"키를 추가하면 `$조건절`과 결합해서 사용할 수 있습니다.
예를 들어 음식 컬렉션에서 조건에 맞는 과일 배열에 "strawberry" 요소를 추가하고 "size" 키를 1씩 증가시킨다.
```js
db.food.drop();
db.food.insertOne({"_id": 1, "fruit": ["apple", "banana", "peach"]});
db.food.insertOne({"_id": 2, "fruit": ["apple", "kumquat", "orange"]});
db.food.insertOne({"_id": 3, "fruit": ["cherry", "banana", "apple"]});

criteria = {};
db.food.updateMany(criteria, 
	{
		"$push": {
			"fruit": "strawberry"
		},
		"$inc": {
			"size": 1
		}
	}
);
db.food.find();
```
![](imgs/Pasted%20image%2020260824153447.png)

size 키를 이용해서 과일 배열의 요소개수가 3을 초과하는 도큐먼트 조회
```js
db.food.find({"size": {"$gt": 3}})
```

#### $slice 연산자
데이터 초기화
```js
db.blog.posts.drop();
db.blog.posts.insertMany([
  {
    content: 'MongoDB 사용법에 관한 기초 가이드입니다.',
    comments: [
      { comment: 'good post', author: 'Jim', votes: 1 },
      { comment: 'i thought it was too short', author: 'Claire', votes: 3 },
      { comment: 'free watches', author: 'Alice', votes: -5, hidden: true },
      { comment: 'vacation getaways1', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways2', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways3', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways4', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways5', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways6', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways7', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways8', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways9', author: 'Lynn', votes: -7, hidden: true },
      { comment: 'vacation getaways10', author: 'Lynn', votes: -7, hidden: true }
    ]
  },
  {
    content: '오늘의 요리 레시피: 집에서 만드는 파스타',
    comments: [
      { comment: '정말 맛있어 보여요!', author: 'Bob', votes: 12 },
      { comment: '면 삶는 시간을 더 자세히 알려주세요.', author: 'Charlie', votes: 4 },
      { comment: 'buy cheap shoes', author: 'Spammer1', votes: -12, hidden: true }
    ]
  },
  {
    content: '주말 일상 공유 및 영화 추천',
    comments: [
      { comment: '추천해주신 영화 잘 봤습니다.', author: 'Dave', votes: 8 },
      { comment: '저도 이 영화 완전 강추해요!', author: 'Eve', votes: 15 },
      { comment: '약간 지루하긴 했어요.', author: 'Frank', votes: 0 },
      { comment: 'click this link', author: 'BotX', votes: -20, hidden: true }
    ]
  }
]);
```


블로그 게시물에서 먼저 달린 댓글 10개를 조회하기
- `criteria` : 검색 조건 필터
- 두번째 매개변수 : 조건을 만족하는 데이터중에서 comments 배열에서 앞에서부터 10개의 요소(댓글)만 잘라내어 반환하도록 지정함
```js
criterial = {
	"_id": "6a8d41edb90de1d138fdeff4"
};
db.blog.posts.findOne(criteria, 
	{
		"comments": {
			"$slice": 10
		}
	}
);
```

**실행 결과**
실행 결과를 보면 댓글이 13개 중에서 배열의 앞요소에 있는 10개만 가져와서 출력한 것을 볼수 있습니다.
```sh
{
  _id: ObjectId('6a8d41edb90de1d138fdeff4'),
  content: 'MongoDB 사용법에 관한 기초 가이드입니다.',
  comments: [
    { comment: 'good post', author: 'Jim', votes: 1 },
    { comment: 'i thought it was too short', author: 'Claire', votes: 3 },
    { comment: 'free watches', author: 'Alice', votes: -5, hidden: true },
    {
      comment: 'vacation getaways1',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways2',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways3',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways4',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways5',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways6',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways7',
      author: 'Lynn',
      votes: -7,
      hidden: true
    }
  ]
}
```

블로그 게시물에서 나중에 달린 댓글 10개를 조회하기
- `$slice` 키값으로 음수로 설정하면 반대로 배열의 뒷부분에서 가져옵니다.
```js
criterial = {
	"_id": "6a8d41edb90de1d138fdeff4"
};
db.blog.posts.findOne(criteria, 
	{
		"comments": {
			"$slice": -10
		}
	}
);
```

**실행 결과**
- 실행 결과를 보면 댓글의 뒷부분에서 10개를 가져온 것을 볼수 있다.
```sh
{
  _id: ObjectId('6a8d41edb90de1d138fdeff4'),
  content: 'MongoDB 사용법에 관한 기초 가이드입니다.',
  comments: [
    {
      comment: 'vacation getaways1',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways2',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways3',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways4',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways5',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways6',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways7',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways8',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways9',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways10',
      author: 'Lynn',
      votes: -7,
      hidden: true
    }
  ]
}
```

특정 블로그 게시글을 가져올때 처음 2개의 댓글을 건너뛰고, 10개의 댓글을 가져오기
- 오프셋과 요소 개수를 지정하여 댓글들을 가져올 수 있다.
```js
criterial = {
	"_id": "6a8d41edb90de1d138fdeff4"
};
db.blog.posts.findOne(criteria, 
	{
		"comments": {
			"$slice": [2, 10]
		}
	}
);
```

실행 결과
- 실행 결과를 보면 배열의 0번째, 1번째는 건너뛰고 2번재 인덱스 요소부터 10개를 가져와서 출력합니다.
```js
{
  _id: ObjectId('6a8d41edb90de1d138fdeff4'),
  content: 'MongoDB 사용법에 관한 기초 가이드입니다.',
  comments: [
    { comment: 'free watches', author: 'Alice', votes: -5, hidden: true },
    {
      comment: 'vacation getaways1',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways2',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways3',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways4',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways5',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways6',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways7',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways8',
      author: 'Lynn',
      votes: -7,
      hidden: true
    },
    {
      comment: 'vacation getaways9',
      author: 'Lynn',
      votes: -7,
      hidden: true
    }
  ]
}
```

블로그의 댓글에서 가장 마지막 댓글만 조회한다.
```js
criterial = {
	"_id": "6a8d41edb90de1d138fdeff4"
};
db.blog.posts.findOne(criteria, 
	{
		"comments": {
			"$slice": -1
		}
	}
);
```

실행 결과
- 실행 결과를 보면 "$slice" 연산자 사용할때 특별히 명시하지 않으면 도큐먼트내 모든 키를 반환한다. 예를 들어 다음 결과에서는 comments 외에 content도 출력되는 것을 볼수 있다.
![](imgs/Pasted%20image%2020260825163603.png)

#### 일치하는 배열 요소의 반환
`$` 연산자를 활용하면 특정 기준과 일치하는 배열 요소를 조회할 수 있습니다.

데이터 준비
```js
db.blog.posts.drop();
db.blog.posts.insertMany([
  {
    content: '신작 영화 리뷰 및 솔직한 후기',
    comments: [
      { comment: '저도 이 영화 봤는데 정말 재미있었어요!', author: 'bob', votes: 10 },
      { comment: '스포일러 주의해주세요 ㅠㅠ', author: 'Alice', votes: 2 },
      { comment: '쿠키 영상은 몇 개인가요?', author: 'bob', votes: 5 }, // bob의 두 번째 댓글
      { comment: '광고성 댓글입니다.', author: 'Spammer', votes: -8, hidden: true }
    ]
  },
  {
    content: '주말에 가기 좋은 카페 추천 TOP 5',
    comments: [
      { comment: '첫 번째 카페 분위기 완전 대박이네요.', author: 'Charlie', votes: 7 },
      { comment: '주차 공간은 넉넉한 편인가요?', author: 'bob', votes: 3 }, // bob의 댓글 포함
      { comment: '정보 감사합니다!', author: 'David', votes: 1 }
    ]
  },
  {
    content: '개발자 필독! MongoDB 성능 최적화 팁',
    comments: [
      { comment: '인덱스 관련 부분이 정말 유익했습니다.', author: 'Eve', votes: 15 },
      { comment: 'Aggregation 파이프라인 설명도 부탁드려요.', author: 'Frank', votes: 9 },
      { comment: '좋은 글 잘 읽고 갑니다.', author: 'Grace', votes: 4 }
    ] // [bob 댓글 없음]
  },
  {
    content: '자취생을 위한 간단한 저녁 레시피',
    comments: [
      { comment: '요리 초보도 따라 하기 쉽네요.', author: 'Hannah', votes: 6 },
      { comment: '재료 구매 링크', author: 'Bot123', votes: -15, hidden: true }
    ] // [bob 댓글 없음]
  }
]);
```

**댓글 작성자 중에서 "bob" 사용자가 작성한 게시물을 조회하기**
- `$`는 "첫번째 매개변수(검색 조건)"에서 일치했던 배열의 첫번째 요소 위치(인덱스)"를 가리킵니다.
	- 검색조건이 `"comments.author" : "bob"` 이므로, MongoDB는 `comments` 배열 내부를 순회하다가 `author`가 "bob"인 첫번째 댓글의 인덱스를 기억합니다.
	- `comments.$` 형태로 프로젝션에 사용하면, 전체 배열을 가져오는 대신 조건에 매칭된 첫번째 댓글 요소만 잘라내어 반환합니다.
- MongoDB 프로젝션에서 숫자 `1`은 "해당 필드를 결과에 포함시킨다"라는 의미입니다. (반대는 0)
	- `{"comments$.: 1"}`은 조건에 일치한 배열 요소만 **결과 도큐먼트에 포함해서 출력**하라는 의미
```js
db.blog.posts.find(
	{
		"comments.author": "bob"
	},
	{
		"comments.$": 1
	}
);
```

`_id=f9ff` 게시물의 원본 `comments` 배열을 보면 "bob" 작성자가 작성한 댓글은 원래는 2개 였습니다. 그리고 실행 결과를 보면 해당 게시물의 "bob" 작성자가 쓴 댓글 2개를 반환하는게 아닌 1개만 반환하는 것을 볼수 있습니다.
![](imgs/Pasted%20image%2020260827151402.png)

#### 배열 및 범위 쿼리의 상호작용

데이터 준비
```js
db.test.drop();
db.test.insertMany(
	[
		{ "x": 5 },
		{ "x": 15 },
		{ "x": 25 },
		{ "x": [5, 25] }
	]
);
```

"x"의 값이 10과 20사이인 도큐먼트 찾기
```js
db.test.find(
	{
		"x": {"$gt": 10, "$lt": 20}
	}
);
```
![](imgs/Pasted%20image%2020260827154643.png)

위 실행 결과를 보면 2개의 도큐먼트가 조회되었습니다. 일반적으로 `{"x": 15}` 에 해당하는 도큐먼트만 조회될 것이라고 예상하였지만, 실제 결과는 `{"x": [5, 25]}` 에 해당하는 도큐먼트도 같이 조회되었습니다.
이는 해당 도큐먼트의 `5`가 `{"$lt": 20}`의 조건(**두번째 절**)을 만족하고 `25`가 `{"$gt": 5}`의 조건(첫번째 절)을 만족하기 때문에 조회된 것입니다.

위 예제를 통해서 알수 있는 것은 **필드가 배열이라면 각 절의 조건을 충족하는 도큐먼트가 일치된다는 점**입니다. 그리고 각 쿼리의 절은 서로 다른 배열 요소와 일치할 수 있습니다.

위 방법의 문제점은 **배열에 대한 범위 쿼리가 본질적으로 쓸모없어진다.** 범위가 모든 다중 요소 배열과 일치하기 때문입니다.

`$elemMatch` 사용한 배열 요소 비교
`$elemMatch` 연산자를 사용하면 몽고DB는 두절을 하나의 배열 요소와 비교하기 시작합니다. 단, 해당 연산자는 비배열 요소를 일치시키지 않는다.
```js
db.test.find(
	{
		"x": {
			"$elemMatch": {"$gt": 10, "$lt": 20}
		}
	}
);
```

### 4.3.4 내장 도큐먼트에 쿼리하기
내장 도큐먼트 쿼리는 도큐먼트 전체를 대상으로 하는 방식과 도큐먼트 안에 키/값 쌍 각각을 대상으로 하는 방식으로 나뉜다.

데이터 준비
```js
db.people.drop();
db.people.insertOne(
	{
		"name": {
			"first": "Joe",
			"last": "Schmoe"
		}
	}
);
```

전체 도큐먼트 대상 쿼리
```js
db.people.find(
	{
		"name": {
			"first": "Joe",
			"last": "Schmoe"
		}
	}
);
```
![](imgs/Pasted%20image%2020260827161316.png)

서브 도큐먼트 전체에 쿼리하려면 서브 도큐먼트와 정확히 일치(순서 및 값)해야 한다. 예를 들어 `name` 서브 도큐먼트에 "middle"이라는 필드를 추가하면 위 쿼리는 작동하지 않는다.
```js
db.people.drop();
db.people.insertOne(
	{
		"name": {
			"first": "Joe",
			"middle": "smith",
			"last": "Schmoe"
		}
	}
);
db.people.find(
	{
		"name": {
			"first": "Joe",
			"last": "Schmoe"
		}
	}
);
```
![](imgs/Pasted%20image%2020260827161912.png)

내장 도큐먼트에 쿼리할때는 특정 키로 쿼리하는 방법이 좋습니다. 스키마가 변경되어도 모든 쿼리가 정상적으로 작동합니다.
```js
db.people.find(
	{
		"name.first": "Joe",
		"name.last": "Schmoe"
	}
);
```
![](imgs/Pasted%20image%2020260827162100.png)

데이터 준비
```js
db.blog.drop();
db.blog.insertOne(
  {
    content: '신작 영화 리뷰 및 솔직한 후기',
    comments: [
		{ comment: '저도 이 영화 봤는데 정말 재미있었어요!', author: 'joe', score: 1, votes: 10 },
      { comment: '저도 이 영화 봤는데 정말 재미있었어요!', author: 'joe', score: 3, votes: 10 },
      { comment: '저도 이 영화 봤는데 정말 재미있었어요!', author: 'joe', score: 5, votes: 10 },
      { comment: '저도 이 영화 봤는데 정말 재미있었어요!', author: 'joe', score: 7, votes: 10 },
      { comment: '스포일러 주의해주세요 ㅠㅠ', author: 'Alice', score: 5, votes: 2 },
      { comment: '쿠키 영상은 몇 개인가요?', author: 'bob', score: 8, votes: 5 },
      { comment: '광고성 댓글입니다.', author: 'Spammer', score: 9, votes: -8, hidden: true }
    ]
  }
);
```

블로그 게시물에서 5점 이상을 받은 Joe의 댓글을 조회하기
```js
db.blog.find(
	{
		"comments": {"author": "joe", "score": {"$gte": 5}}
	}
);
```
실행 결과를 보면 joe 댓글의 score가 5점이상인것이 2개 있음에도 조회되지 않습니다. 이유는 **내장 도큐먼트가 쿼리 도큐먼트 전체와 일치하지 않기 때문입니다.**
쿼리 도큐먼트는 "comment" 키가 없으므로 맞는 도큐먼트를 찾을 수 없습니다.
![](imgs/Pasted%20image%2020260827162919.png)

내장 도큐먼트에 쿼리시 특정 키로 쿼리하기
```js
db.blog.find(
	{
		"comments.author": "joe",
		"comments.score": {"$gte": 5}
	}
);

```

실행 결과를 보면 score가 5점 미만인 댓글도 조회되었습니다. 이는 실패한 쿼리입니다. 쿼리 도큐먼트에서 댓글의 score 조건과 author 조건은 댓글 배열 내의 각기 다른 도큐먼트와 일치하기 때문입니다.
comments 배열의 실행 결과에서 4개의 데이터는 `comments.author: "joe"` 조건과 일치하고 나머지 3개의 데이터는 `comments.score: {"$gte":6}` 조건과 일치해서 출력된 것입니다.
```shell
[
  {
    _id: ObjectId('6a8fe9f2f3b021f13c15fa0a'),
    content: '신작 영화 리뷰 및 솔직한 후기',
    comments: [
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 1,
        votes: 10
      },
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 3,
        votes: 10
      },
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 5,
        votes: 10
      },
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 7,
        votes: 10
      },
      { comment: '스포일러 주의해주세요 ㅠㅠ', author: 'Alice', score: 5, votes: 2 },
      { comment: '쿠키 영상은 몇 개인가요?', author: 'bob', score: 8, votes: 5 },
      {
        comment: '광고성 댓글입니다.',
        author: 'Spammer',
        score: 9,
        votes: -8,
        hidden: true
      }
    ]
  }
]
```

모든 키를 지정하지 않고도 조건을 정확하게 묶으려면 `$elemMatch`를 사용해야 한다. 해당 조건절은 조건을 부분적으로 지정해서 배열 안에서 하나의 내장 도큐먼트를 찾게 해준다.
```js
db.blog.find(
	{
		"comments": {
			"$elemMatch": {
				"author": "joe",
				"score": {"$gte": 5}
			}
		}
	}
);
```

다음 실행 결과를 보면 author가 joe이고 score가 5점 이상인 블로그 게시물을 조회했습니다.
```js
[
  {
    _id: ObjectId('6a8fe9f2f3b021f13c15fa0a'),
    content: '신작 영화 리뷰 및 솔직한 후기',
    comments: [
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 1,
        votes: 10
      },
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 3,
        votes: 10
      },
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 5,
        votes: 10
      },
      {
        comment: '저도 이 영화 봤는데 정말 재미있었어요!',
        author: 'joe',
        score: 7,
        votes: 10
      },
      { comment: '스포일러 주의해주세요 ㅠㅠ', author: 'Alice', score: 5, votes: 2 },
      { comment: '쿠키 영상은 몇 개인가요?', author: 'bob', score: 8, votes: 5 },
      {
        comment: '광고성 댓글입니다.',
        author: 'Spammer',
        score: 9,
        votes: -8,
        hidden: true
      }
    ]
  }
]
```

## 4.4 $where 쿼리
`$where` 절을 사용해서 임의의 자바스크립트를 쿼리의 일부분으로 사용해서 거의 모든 쿼리를 표현할 수 있습니다. 하지만 보안상의 이유로 `$where` 절 사용을 제한해야 합니다. 최종 사용자(end user)가 임의의 `$where` 절을 실행하지 못하도록 하여야 합니다.

데이터 준비
```js
db.foo.drop();
db.foo.insertOne({
	"apple": 1,
	"banana": 6,
	"peach": 3
});
db.foo.insertOne({
	"apple": 8,
	"spinach": 4,
	"watermelon": 4
});
```

두개의 필드 값이 동일한 도큐먼트를 조회
- 두번째 도큐먼트의 "spinach"와 "watermelon"이 같은 값(4)을 가지기 때문에 해당 도큐먼트를 조회합니다.
```js
db.foo.find({
	"$where" : function(){
		for(var current in this){
			for(var other in this){
				if(current != other && this[current] == this[other]){
					return true;
				}
			}
		}
		return false;
	}
});
```
![](imgs/Pasted%20image%2020260831141607.png)

`$where` 절 주의사항
- 일반 쿼리보다 느려서 반드시 필요한 경우가 아니면 사용하지 않기
- 해당 절 실행시 각 도큐먼트는 BSON에서 자바스크립트 객체로 변환하기 때문에 오래 걸림
- `$where` 절에는 인덱스 사용할 수 없음

몽고 DB 3.6에는 몽고 DB 쿼리 언어로 집계 표현식을 사용할 수 있도록 `$expr` 연산자가 추가되었습니다. `$where` 절대신 `$expr` 절을 사용하자.

## 4.5 커서
커서 생성 예시
```js
for(i=0; i<100; i++){
	db.collection.insertOne({x: i});
}
var cursor = db.collection.find();
cursor
```
![](imgs/Pasted%20image%2020260831143147.png)

cursor 클래스는 자바스크립트의 반복자(iterator) 인터페이스를 구현했기 때문에 forEach 반복문에 사용할 수 있습니다.
```js
var cursor = db.collection.find();
cursor.forEach(function(x){
	print(x);
});
```

![](imgs/Pasted%20image%2020260831145341.png)

변수에 커서를 저장하면 find() 호출할때 셸이 데이터베이스를 즉시 쿼리하지 않고 결과를 요청하는 쿼리를 보낼때까지 기다립니다. 그래서 쿼리하기전에 옵션을 추가할 수 있습니다.
```js
var cursor = db.collection.find().sort({"x": 1}).limit(1).skip(10);
```

셸에서 다음과 같이 `hasNext()`를 호출하면 쿼리가 서버로 전송됩니다. 셸은 next나 hasNext 메서드 호출시 서버 왕복 횟수를 줄이기 위해서 한번에 처음 100개 또는 4MB 크기의 결과(둘중 작은것)를 가져온다.
```
cursor.hasNext()
```

### 4.5.1 제한, 건너뛰기, 정렬
데이터 개수 제한 (limit)
```js
db.collection.find().limit(3)
```

데이터 건너뛰기 (skip)
```js
db.collection.find().skip(3)
```

데이터 정렬 (sort)
- username을 기준으로 오름차순, age를 기준으로 내림차순으로 정렬
```js
db.collection.find().sort({username: 1, age: -1})
```

페이지네이션
- mp3 단어를 검색하고 50개의 데이터를 가격을 기준으로 내림차순으로 정렬하여 출력
```js
db.stock.find({"desc": "mp3"}).limit(50).sort({"price": -1})
```

다음 페이지 검색
```
db.stock.find({"desc": "mp3"}).limit(50).skip(50).sort({"price": -1})
```

### 4.5.2 많은 수의 건너뛰기 피하기
skip 연산자는 생략된 결과물을 모두 찾아서 폐기하므로 결과가 많으면 느려진다. 

**skip을 사용하지 않고 페이지 나누기**
다음 쿼리는 limit를 사용해서 첫번째 페이지를 반환하고, 다음 페이지들은 첫 페이지부터 오프셋을 주어서 반환하는 쉬운 방법입니다. 하지만 다음과 같은 방법은 많이 느려지기 때문에 사용하지 않는 것이 좋습니다.
```js
var page1 = db.foo.find(criteria).limit(100);
var page1 = db.foo.find(criteria).skip(100).limit(100);
var page1 = db.foo.find(criteria).skip(200).limit(100);
```

첫번째 페이지를 가져올때 date를 기준으로 내림차순으로 정렬하여 최신 데이터 100개를 가져온다.  두번재 페이지를 가져올때는 latest 도큐먼트의 date를 기준으로 필터링을 수행합니다. 그리고 다시 필터링된 데이터들을 대상으로 date 기준 내림차순으로 정렬후 데이터를 100개 가져온다.
```js
var page1 = db.foo.find().sort({"date": -1}).limit(100);

var latest = null;
// 첫 페이지 보여주기
while(page1.hasNext()){
	latest = page1.next();
	display(latest);
}
// 다음 페이지 가져오기
var page2 = db.foo.find({"date": {"$lt": latest.date}});
page2.sort({"date": -1}).limit(100);
```

**랜덤으로 도큐먼트 찾기**
다음 쿼리는 권장하지 않는 랜덤으로 도큐먼트 찾는 쿼리입니다.
```js
var total = db.foo.count();
var random = Math.floor(Math.random() * total);
db.foo.find().skip(random).limit(1);
```

위 쿼리가 비효율적인 이유
- 전체 도큐먼트를 세어야함
- skip을 사용해서 많은 요소를 건너뛰어야함

**랜덤키를 별도로 추가해서 도큐먼트를 찾는 방법**
Math.random()을 호출하면 반환값으로 0~1사이의 값을 반환합니다.
```js
db.people.drop();
db.people.insertOne({"name": "joe", "random": Math.random()});
db.people.insertOne({"name": "john", "random": Math.random()});
db.people.insertOne({"name": "jim", "random": Math.random()});
```

random값이 컬렉션 내 모든 "random" 값보다 클때는 빈 결과를 반환한다. 컬렉션 안에 도큐먼트가 하나도 존재하지 않으면 null을 반환한다. 
```js
var random = Math.random();
result = db.people.fineOne({"random": {"$gt": random}});
```

### 4.5.3 종료되지 않는 커서
커서 두가지 측면
- 클라이언트가 보는 커서
- 클라이언트 커서가 나타내는 데이터베이스 커서

서버측에서 보면 커서는 메모리와 리소스를 점유한다. 커서가 더는 가져올 결과가 없거나 클라이언트로부터 종료 요청을 받으면 데이터베이스는 점유 중인 리소스를 해제한다.

서버 커서 종료 조건
- 조건에 일치하는 결과를 모두 본후 스스로 정리
- 커서가 클라이언트 측에서 유효 영역을 벗어나면 드라이버는 데이터베이스에 메시지를 보내 커서를 종료한다고 알림
- 10분동안 활동이 없으면 커서는 자동으로 죽는다.

