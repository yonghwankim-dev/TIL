# IAM 실습

## MFA 적용하기

1. 루트 계정 접속
2. 내 보안 자격증명 접근

![image](https://user-images.githubusercontent.com/33227831/232283342-d70fc271-3adf-4361-8185-e186ee054061.png)

4. 멀티팩터인증(MFA) -> MFA 활성화

![image](https://user-images.githubusercontent.com/33227831/232283400-bc257061-f078-47fb-ae99-e2f34624ff87.png)

6. MFA 디바이스 선택

![image](https://user-images.githubusercontent.com/33227831/232283447-b05bc501-df8e-471e-aba4-05fdb85da7a8.png)

8. 구글 OTP 앱 설치 후 QR 코드 스캔후 MFA 코드 입력

![image](https://user-images.githubusercontent.com/33227831/232283460-166106a5-998e-431e-a9e3-94093f33c606.png)


## MFA 제거하기

1. 루트 계정 접속
2. 내 보안 자격증명 접근
3. 멀티 팩터 인증에서 삭제하고자 하는 인증 선택
4. 제거 버튼 클릭

![image](https://user-images.githubusercontent.com/33227831/232283500-95895b4f-e1e6-439f-ab2a-bffddbb84408.png)


## IAM admin 사용자를 생성하는 이유

- IAM admin 사용자의 정보가 외부에 노출되었을때 루트 사용자로 차단하기 위해서입니다.

## IAM admin 사용자 추가

1. 루트 계정 접속
2. 서비스 -> IAM
3. 그룹 만들기
    1. 액세스 관리 -> 사용자 그룹 -> 그룹 생성 -> 그룹 이름 설정
    2. 정책 연결 -> AdministratorAccess 선택
    3. 그룹 생성

![image](https://user-images.githubusercontent.com/33227831/232283634-b3a6b9c7-764f-49ee-8626-9c2c54d400c2.png)
![image](https://user-images.githubusercontent.com/33227831/232283644-604e1b05-80b0-4ce7-98a0-d657bcafec13.png)
![image](https://user-images.githubusercontent.com/33227831/232283680-7aa8b46a-86a0-42ff-b294-142d740da17f.png)

4. 생성한 그룹에 사용자 추가
    1. 액세스 관리 -> 사용자 -> 사용자 추가
    2. 사용자 이름 설정
    3. AWS Management Console 액세스 선택
    4. IAM 사용자를 생성하고 싶음 선택
    5. 자동 생성된 비밀번호 선택
    6. 사용자가 다음에 로그인할 때 새 비밀번호 생성 요청 해제

![image](https://user-images.githubusercontent.com/33227831/232283728-498908c4-36e3-4489-ada8-8d79b009db90.png)
![image](https://user-images.githubusercontent.com/33227831/232283777-293bdecd-8324-412e-9887-3562e9456cd4.png)

5. 권한 설정에서 그룹에 사용자 추가 선택
    1. 새로 생성한 그룹을 선택
    2. 사용자 생성 클릭

![image](https://user-images.githubusercontent.com/33227831/232283813-c5209595-9a3d-40c1-8ee2-1e01be59ebb0.png)
![image](https://user-images.githubusercontent.com/33227831/232283830-519ef452-751a-4774-b668-7df225b72d69.png)


## 개발자 아이디 추가

1. 개발자 그룹 생성(codesquad-developer)
    1. PowerUserAccess 정책 선택
2. 사용자 생성 (yonghwan_dev)
    1. codesquad-developer 그룹 선택

![image](https://user-images.githubusercontent.com/33227831/232284075-7fddb558-13b8-4206-be1f-d35933ffd6b1.png)


## 로그인 URL 변경

1. 대시보드 이동
2. AWS 계정 - 계정 별칭 생성 버튼
3. 기본 별칭 수정 및 저장


