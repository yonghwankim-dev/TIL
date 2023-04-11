# IAM Role

## IAM Role

- IAM Role은 특정 개체(IAM 사용자, AWS 서비스, 다른 계정, AWS 관리 계정)
  에게 리소스 접근 권한을 부여하기 위해 사용됩니다.
- 임시 자격 증명이라고 합니다.
- 주로 AWS 서비스들이 직접 다른 AWS 서비스를 제어하기 위해 사용됩니다.
- 사용자나 응용 프로그램에서 일시적으로 AWS 리소스에 접근 권한을 얻을때도 사용됩니다.

## IAM Role 주요 구성 요소

- Role ARN(Amazon Resource Name) : AWS 리소스에 대한 권한을 부여하는 데 사용되는 역할
- IAM(Ientity and Access Management, 식별 및 접근 관리) Policy : AWS 리소스에 대한 접근을 규정합니다.
    - JSON 형식으로 명시됩니다.
    - 특정 작업, 리소스 또는 조건에 대해서 접근을 허용/거부합니다.
- 신뢰 관계 : 어떤 개체가 IAM Role을 호출할 수 있는지 명시하는 것
- 유지 시간
- 이름

### IAM Role 예시

- EC2 role : EC2 인스턴스에게 AWS 서비스 접근 권한을 부여함
- Lambda Execution Role : 람다에서 S3로부터 파일을 읽고 싶을때 role에 권한 지정
- 다른 계정의 사용자에게 내 계정의 DynamoDB에 대한 임시 접근 권한 부여
- 안드로이드 앱이 S3로 직접 동영상을 업로드할 때 사용

### ARN

- Amazon Resource Name
- 아마존에서 리소스를 유일하게 식별할 수 있는 구분자
- ARN 형식
    - arn:partition:service:region:account-id:resource-id
    - arn:partition:service:region:account-id:resource-type/resource-id
    - arn:partition:service:region:account-id:resource-type:resource-id
- ARN 예시
    - arn:aws:iam::123456789012:user/yonghwan
        - iam 서비스가 글로벌이라서 region 생략 가능
    - arn:aws:s3:::my-bucket/folder1/file1
        - my-bucket은 글로벌하게 유니크한 이름이어서 region, account-id 생략 가능
