# 실행 방법 및 프로젝트 설명

## 핵심 문제 해결 전략

JPA와 Redis를 활용한 비동기 처리로 대용량 트래픽에 대비

AOP를 활용하여 어플리케이션 내부적으로 특정 서비스에 성능지표 측정 가능

ThreadLocal에 사용자 정보를 입력하여 각 요청별로 독립적인 사용자 정보 보관 및 메서드 파라미터 없이 전역적 접근 가능

통합/단위 테스트 작성으로 보다 견고한 코드 작성에 기여

## 실행 방법

1. 환경 준비 - Java 17 이상, Gradle 8.x 이상 필요, 로컬 DB(H2/MySQL 등) 및 Redis 실행 필요

2. 작업할 디렉토리에 소스를 클론 
   * git clone https://github.com/beaver84/rsupport-notice.git

3. IDE(인텔리제이 등)를 실행하여 해당 디렉토리를 열기 

4. H2 DB를 설치(localhost:8080/h2-console 에 접근, 접속정보는 application.yml 파일 참조)

5. IDE에서 RsupportNoticeApplication의 메인 함수를 실행

6. 과제에서 요구하는 API들을 차례로 Postman 등으로 API 테스트 
   * 예) 학생 추가 - URL에 http://localhost:8080/api/notices 입력, Body에 form-data 형식으로 request 입력
   * Postman 링크 확인 - (https://postman.co/workspace/kuka-Workspace~2d33b039-aec0-4da4-83be-ba660625ac56/collection/10700825-6a5ae6a5-5ae0-49e5-bc27-3bd200369b26?action=share&creator=10700825)

7. DB의 데이터를 확인하는 방법은 localhost:8080/h2-console 에 접속 후 확인, 접속정보는 application.yml 파일 참조)
 
## API 사용 예시 

- 공지사항 등록: POST /api/notices (multipart/form-data)

- 공지사항 목록: GET /api/notices

- 공지사항 상세: GET /api/notices/{id}

- 공지사항 검색: GET /api/notices/search?keyword=...&searchType=...

- 공지사항 수정: PUT /api/notices/{id}

- 공지사항 삭제: DELETE /api/notices/{id}

## 테스트 실행 

./gradlew test
- 단위/통합 테스트가 자동 실행됩니다.

## 추가 참고 
- 컨트롤러, 서비스, 리포지토리, 엔티티 등 계층별 책임 분리

- 단위테스트 예시는 src/test/java 디렉토리 참고

- 이 프로젝트는 RESTful 설계, 대용량 트래픽 대응, 확장성, 신뢰성, 유지보수성을 최우선으로 고려하여 구현되었습니다.
자세한 구현 내용과 예시는 소스코드와 주석, 테스트 코드를 참고해 주세요.