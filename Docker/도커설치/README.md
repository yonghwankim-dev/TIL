# 도커설치
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
