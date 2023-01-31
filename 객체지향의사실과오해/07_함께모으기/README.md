# 함께 모으기

## 객체지향 설계 안에 존재하는 3가지 관점
1. 개념 관점
- 도메인 안에 존재하는 개념과 개념들에 사이의 관계를 표현함
- 도메인 : 특정 분야나 주제

2. 명세 관점
- 객체의 인터페이스의 관점을 표현함
- 객체가 협력을 위해 '무엇'을 할수 있는가에 초점을 맞춤
- 인터페이스와 구현을 분리해야함

3. 구현 관점
- 객체들이 책임을 수행하는데 필요한 동작하는 코드를 작성하는 관점을 표현함
- 객체가 책임을 '어떻게' 수행할 것인가에 초점을 맞춤
- 인터페이스를 구현하는데 필요한 속성과 메서드를 클래스에 추가함

객체지향 설계는 전체적으로 개념->인터페이스->구현의 과정을 거침

## 커피 전문점 도메인
### 메뉴판 타입과 메뉴 항목 타입 간의 포함 관계
![image](https://user-images.githubusercontent.com/33227831/215431650-8dc3d6dd-26ee-4923-9c6c-5e7f4b635e94.png)

### 손님과 메뉴판 사이의 연관 관계
![image](https://user-images.githubusercontent.com/33227831/215431828-b76c9fe5-a191-4394-82b9-06563fab515a.png)

### 커피 전문점을 구성하는 타입들
![image](https://user-images.githubusercontent.com/33227831/215432111-3e6c1f70-0f58-4f90-b599-f6f12a814673.png)

## 설계하고 구현하기
## 설계하기
### 협력을 시작하게 하는 첫번째 메시지
![image](https://user-images.githubusercontent.com/33227831/215434594-7ebe8bc3-4a21-4307-9027-299b7834c4fc.png)

### 첫번째 메시지가 손님이라는 객체를 선택
![image](https://user-images.githubusercontent.com/33227831/215434656-aaf92a4e-fcd3-4276-9cdd-d3b95eb617c1.png)

### 스스로 할 수 없는 일은 메시지를 전송해 다른 객체에게 도움을 요청함
![image](https://user-images.githubusercontent.com/33227831/215434873-622a68ae-d162-4d77-8665-3c6fb2178dfa.png)

### 두번째 객체를 찾음
![image](https://user-images.githubusercontent.com/33227831/215434953-7a8cfc62-506c-4bff-a72f-acadc3c9ed3c.png)

### 세로운 메시지를 찾음
![image](https://user-images.githubusercontent.com/33227831/215435219-8de7e81b-28c5-43fc-abe4-be746fe301ac.png)

### 커피를 제조하라는 메시지가 바리스타라는 객체를 선택
![image](https://user-images.githubusercontent.com/33227831/215435343-ca1a9b8d-99f6-4460-9ccb-dbd9240f39e7.png)

### 커피 주문을 위한 객체 협력
![image](https://user-images.githubusercontent.com/33227831/215435466-8872c341-defc-4b8c-9223-07365ed9c402.png)

### 각 객체들이 수신하는 메시지는 객체의 인터페이스를 구성함
![image](https://user-images.githubusercontent.com/33227831/215435804-1750ca1c-a060-4936-b2bb-e2591df94c80.png)

## 구현하기
### 손님의 구현은 메뉴판과 바리스타와 협력해야함
![image](https://user-images.githubusercontent.com/33227831/215435466-8872c341-defc-4b8c-9223-07365ed9c402.png)

### 커피 전문점을 구현한 최종 클래스 구조
![image](https://user-images.githubusercontent.com/33227831/215437796-191fc85a-5df4-4606-b4bc-978d92db321e.png)