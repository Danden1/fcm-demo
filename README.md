# Firebase Cloud Messaging(FCM)

해당 프로젝트는 FCM에 관한 프로젝트이다.

AiForPet(TTcare) 회사에서 인턴을 하면서 구현하고 있다.
(회사에 대한 정보는 코드 내에 없다.)

클래스 설계는 domain driven design(DDD)를 기반으로 한다.


### 요구사항

(필수)

1. push가 사용자에게 보내져야 한다.
2. push에 관한 에러 처리가 필요하다.
3. 발송 가능한 시간에만 해야한다. 만약에 당일에 처리를 못했을 경우, 다음날에 어떻게 처리할 것인지에 대한 전략도 필요하다.
4. 유효하지 않은 토큰은 db에서 삭제해야 한다(FCM 공식 사이트에서는 2달을 추천한다.).
5. 서버가 다운되거나 fcm 자체에서 문제가 생길 경우, 보내지 못한 push에 대한 처리가 필요하다.
6. 비동기로 작동해야 한다.

(옵션)
1. push 이력을 남겨야 한다.
2. push를 한 번에 보내지 않고, delay를 주고 전송해야 한다.
3. push를 예약한 시간대에 보낼 수 있어야 한다.

### 구현 기술

master branch 에서는 DB를 이용하여 MessageBox를 구현한다.

kafka branch 에서는 kafka를 이용하여 MessageBox를 구현한다.




