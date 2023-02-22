# 도커설치
## 목차
- [ubuntu 18.04에 패키지를 이용한 도커 컴퓨니티 에디션 설치](#ubuntu-18.04에-패키지를-이용한-도커-컴퓨니티-에디션-설치)
- [Repository를 사용한 도커 커뮤니티 에디션 설치](#Repository를-사용한-도커-커뮤니티-에디션-설치)
- [docker 제거](#docker-제거)

## ubuntu 18.04에 패키지를 이용한 도커 컴퓨니티 에디션 설치
우분투 운영체제에 도커를 설치합니다.

```shell
# 현재 Ubuntu 버전 확인
$ cat /etc/lsb-release
DISTRIB_ID=Ubuntu
DISTRIB_RELEASE=22.04
DISTRIB_CODENAME=jammy
DISTRIB_DESCRIPTION="Ubuntu 22.04.1 LTS"

# apt(Advanced Packaing Tool)는 우분투를 비롯한 데비안 계열에서 작동하는 패키지 관리 도구임
## 최신 패키지로 업데이트 수행
$ sudo apt-get update

# 도커 설치를 위해 도커와 의존성이 있는 패키지들을 미리 설치
$ sudo apt-get install -y \
apt-transport-https \ # https를 통해 데이터 및 패키지에 접근 가능하도록 함
ca-certificates \     # 일종의 디지털 서명, SSL 기반의 연결 확인
curl \                # 웹에서 데이터 다운로드 시 사용
software-properties-common # PPA(Personal Package Archive, 개발자, 사용자 소프트웨어 저장소를 의미함) 추가, 제거에 사용

# 도커에서 제공하는 공식 GPG(GNU Privacy Guard) key 추가. apt가 패키지를 인증할 때 사용하는 키 리스트 관리.
# apt-key를 통해 지정 사이트에서 새로운 키 추가
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# 추가된 GPG key 확인
$ sudo apt-key fingerprint
/etc/apt/trusted.gpg
--------------------
pub   rsa4096 2017-02-22 [SCEA]
      9DC8 5822 9FC7 DD38 854A  E2D8 8D81 803C 0EBF CD88
uid           [ unknown] Docker Release (CE deb) <docker@docker.com>
sub   rsa4096 2017-02-22 [S]

/etc/apt/trusted.gpg.d/ubuntu-keyring-2012-cdimage.gpg
------------------------------------------------------
pub   rsa4096 2012-05-11 [SC]
      8439 38DF 228D 22F7 B374  2BC0 D94A A3F0 EFE2 1092
uid           [ unknown] Ubuntu CD Image Automatic Signing Key (2012) <cdimage@ubuntu.com>

/etc/apt/trusted.gpg.d/ubuntu-keyring-2018-archive.gpg
------------------------------------------------------
pub   rsa4096 2018-09-17 [SC]
      F6EC B376 2474 EDA9 D21B  7022 8719 20D1 991B C93C
uid           [ unknown] Ubuntu Archive Automatic Signing Key (2018) <ftpmaster@ubuntu.com>

# 추가된 키중에서 첫번째 키를 선택해서 조회
$ sudo apt-key fingerprint 0EBFCD88

# 데비안 계열의 도커 repository PPA 추가
# 에지 버전 설치시 다음 구문의 마지막에 stable edge 추가.
# 매달 기능이 업데이트 되는 에지 버전은 버그 발생 가능성이 높아 안정화(stable) 버전 설치 권장.
$ sudo add-apt-repository \
"deb [arch=amd64] https://download.docker.com/linux/ubuntu \
$(lsb_release -cs) \
stable"

# 새로운 저장소가 추가되었으므로 패키지 업데이트 수행
$ sudo apt-get update

# 설치한 저장소들을 보여준다.
$ apt-cache policy docker-ce

# 도커 커뮤니티 에디션(ce) 설치
$ sudo apt-get -y install docker-ce

# 또는 설치할 버전을 지정해서 설치
# 위 작업을 이미 실행했다면 다음은 실행하지 않아도 됨
$ sudo apt-get -y install docker-ce=20.10.10 ce-0 ubuntu

# 도커 버전 정보와 도커 데몬 상태 확인.
$ sudo docker version
$ sudo systemctl status docker

# 도커는 권한이 있는 바이너리이기 때문에 실행 시 sudo를 명시하고 사용해야함
# 다음 명령은 도커 그룹에 현재 사용자를 그룹에 추가함
$ sudo usermod -aG docker $(whoami)
$ sudo systemctl restart docker
$ sudo systemctl status docker
$ sudo systemctl status containerd.service

# 현재 게정을 로그아웃하고 다시 로그인한 후부터 sudo 없이도 docker 명령을 사용이 가능
$ docker version
```

## Repository를 사용한 도커 커뮤니티 에디션 설치
**1. 이전 버전 삭제**
```shell
$ sudo apt-get remove docker docker-engine docker.io containerd runc
```

**2. Repository 준비**
HTTPS를 통해 Repository를 사용할 수 있도록 package index를 업데이트하고 도커 설치를 위한 패키지를 설치합니다.

**2.1 HTTPS를 통해 Repository를 사용하기 위해 apt package index를 업데이트하고 패키지들을 설치합니다.**
```shell
$ sudo apt-get update # pakcage index 업데이트
$ sudo apt-get install \
  ca-certificates \
  curl \
  gnupg \
  lsb-release
```
- ca-certificates
    - certiciate authority에서 발행되는 디지털 서명, SSL 인증서의 PEM 파일이 포함되어 있어 SSL 기반 앱이 SSL 연결이 되어 있는지 확인할 수 있음, curl은 기본적으로 https 사이트의 SSL 인증서를 검증함
- curl
    - URL을 사용하여 데이터를 전송하기 위한 명령줄 도구 및 라이브러리, 특정 웹사이트에서 데이터 다운로드 할 때 사용
- gnupg
    - 공개/비공캐 키 암호화를 규정하는 OpenPGP(Pretty Good Privacy) 표준을 구현하는 자유 소프트웨어
    - GPG도 공개키와 비공개 키를 이용해 문서, 메시지, 메일 본문, 파일 등을 암호화하고 복원할 수 있음
- lsb-release
    - 리눅스 배포판 버전 확인 명령어

**2.2 도커의 공식적인 GPG 키를 추가합니다.**
```shell
$ sudo mkdir -m 0755 -p /etc/apt/keyrings
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

**2.3 Repository를 준비하기 위해 다음과 같은 명령어를 사용합니다.**
```shell
$ echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

**3. 도커 엔진 설치**
**3.1 apt 패키지 인덱스 업데이트**
```shell
$ sudo apt-get update
```

**3.2 Docker Engine, containerd, Docker Compose 설치**
```shell
$ sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

**4. 도커 설치 확인**
```
$ sudo docker run hello-world
```

![image](https://user-images.githubusercontent.com/33227831/220000716-2b047ddf-4971-4037-841c-50872419990d.png)

## docker 설치 도중 문제해결
### `sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin` 명령 실행중 문제

![image](https://user-images.githubusercontent.com/33227831/219998135-1d436258-4e6a-4c6e-9072-17c352ee256e.png)

위와 같이 설치중 실패한 이유는 다른 프로세스가 /var/lib/dpkg/lock-frontend 파일을 점유하고 있기 때문에 실패하였습니다.

```shell
$ sudo killall apt apt-get
$ sudo rm /var/lib/apt/lists/lock
$ sudo rm /var/cache/apt/archives/lock
$ sudo rm /var/lib/dpkg/lock*

$ sudo dpkg --configure -a
$ sudo apt-get update
```
터미널을 열고 모든 프로세스를 종료시킵니다. 그리고 위 경로의 파일들을 제거합니다.

### `sudo dpkg --configure -a` 실행중 문제
![image](https://user-images.githubusercontent.com/33227831/219999400-ee619d40-6b9c-469a-a75a-a4eedeceadc8.png)

apt 패키지 관리자를 사용하여 패키지를 업그레이드 하려고 할때 명령어로 패키지 설치 시 충돌한 내용을 자동으로 해결하고자 할때 에러 메시지가 나올 수 있습니다.

/var/lib/dpkg/updates 디렉토리 내의 0001 파일을 0001.X로 변경하고 다시 시도합니다.

```shell
$ sudo mv /var/lib/dpkg/updates/0001 /var/lib/dpkg/updates/0001.X
$ sudo dpkg --configure -a
$ sudo apt-get install -f
```

## docker 제거
**1. Docker Engine, CLI, containerd, Docker Compose packages를 삭제합니다.**
```shell
$ sudo apt-get purge docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin docker-ce-rootless-extras
```
- apt-get purge : 패키지 삭제 및 설정 파일도 삭제함
- apt-get remove : 패키지 삭제

**2. Images, containers, volumes, 사용자 정의 파일을 삭제하여 줍니다.**
```shell
$ sudo rm -rf /var/lib/docker
$ sudo rm -rf /var/lib/containerd
```