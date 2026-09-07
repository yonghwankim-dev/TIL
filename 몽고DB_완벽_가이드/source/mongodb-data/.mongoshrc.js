var no = function(){
    print("Not on my watch");
}

// 1. 현재 db 인스턴스를 통해 데이터베이스 프로토타입 추출
var DbPrototype = Object.getPrototypeOf(db);
db.dropDatabase = DbPrototype.dropDatabase = no;

// 2. 가상의 컬렉션을 통해 컬렉션 프로토타입 추출
var CollectionPrototype = Object.getPrototypeOf(db.getCollection("dummy"));
//CollectionPrototype.drop = no;
CollectionPrototype.dropIndex = no;
CollectionPrototype.dropIndexes = no;

config.set("editor", "vim")

