# 웹 공격 기술
## 학습 키워드
- 크로스 사이트 스크립팅(XSS, cross-site scripting)
- 크로스 사이트 리퀘스트 포저리(CSRF, Cross Site Request Forgery)

## 크로스 사이트 스크립팅(XSS, cross-site scripting)
- 공격자가 웹 사이트에 코드를 첨부하여 피해자가 웹 사이트를 로드할때 코드가 실행되도록 하는 공격입니다.

### 크로스 사이트 스크립팅 영향
- 가짜 입력 폼 등에 의해서 유저의 개인정보가 도둑맞습니다.
- 스크립트에 의해서 쿠키 값이 도둑맞거나 피해자가 의도하지 않은 리퀘스트를 송신됩니다.
- 가짜 문장이나 이미지 등이 표시됩니다.

![image](https://user-images.githubusercontent.com/33227831/230704506-acac5602-f193-44b3-8a8c-1a2f9f2fb6f7.png)

1. 공격자가 피해자에게 스크립트가 담긴 링크를 전송합니다.
2. 피해자는 링크를 클릭하고 웹 사이트에 리퀘스트합니다.
3. 피해자의 브라우저는 웹 사이트를 로드합니다. 그런데 악성 스크립트또한 실행됩니다.
4. 악성 스크립트가 피해자의 개인 정보를 공격자에게 전송합니다.

## References
- https://www.cloudflare.com/ko-kr/learning/security/threats/cross-site-scripting/
