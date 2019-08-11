# loan-mower-man

## Requirements
1. JDK 1.8+
1. 웹브라우져 : Swagger으로 작성한 API 문서를 확인하기 위해 필요.

## Build and Packaging
* Build: `./mvnw compile`
* Packaging: `./mvnw package`

## Run tests
1. `./mvnw test`
1. To check test coverage: open `./target/jacoco-ut/index.html` file in web browser.

## Run standalone
1. `./mvnw spring-boot:run`

## API definitions
1. Run `./mvnw spring-boot:run`
1. Visit <http://localhost:8080/swagger-ui.html>

----

## 사용한 프레임웍, 라이브러리, 툴

### Spring Boot
* 스프링 프레임웍에서 전부 WebMVC등을 가져다쓰고,
* 최대한 Spring스럽게, 너무 세밀한 Spring Configuration을 하지 않고, Spring Boot 컨벤션-디폴트를 따르려고 했습니다.
* <https://spring.io/projects/spring-boot>

### Spring Security, jjwt
* <https://spring.io/projects/spring-security> + <https://github.com/jwtk/jjwt>
* 어떻게 JWT 기반으로 세팅해야할지 잘 모르겠어서 참고했습니다: <https://sdqali.in/blog/2016/07/06/jwt-authentication-with-spring-web-part-3/>
* 기존에 Spring Security을 이용한것과 조금 달라서 많이 좋은 경험: <https://github.com/ageldama/shortening-fat>

### Swagger UI
* <https://springfox.github.io/springfox/docs/current/>
* [Swagger 2](https://swagger.io/docs/specification/2-0/basic-structure/) 으로 모든 API을 문서화
  * 다른 별도 API문서 대신 자기자신으로 설명이 가능하도록 노력.
* 특히 API문서에서 잘정리하기 어려운 요청과 응답 payload의 정의가 예제를 포함해 만족스럽게 표현될 수 있어서 좋아함.

### Commons-CSV
* [CSV](https://en.wikipedia.org/wiki/Comma-separated_values) 파일 파싱을 직접 구현하지 않았다
* <https://commons.apache.org/proper/commons-csv/>

### Guava, Commons-lang3, Commons-IO
* Java 코드를 작성할때 공통으로 계속 반복해서 나오는것들이나 자료구조(`ImmutableMap/List`)을 더 깔끔하고 확실하게 제공해서

### FlywayDB
* 테스트케이스 실행시, 서버 시작시 자동으로 Database Migration Script을 실행하도록 사용했다.
* Hibernate의 `hbm2ddl=auto` 기능으로는 조금씩 변경하는 DB구조를 제대로 안전하게 마이그레이션하기 어려워지니까. (유지보수시)

### Lombok
* <https://projectlombok.org/>
* [@Log](https://projectlombok.org/features/log)은 반복적인 `LoggerFactory`을 안 불러도 되니까 사용
* Getter/Setter, ToString/Equals/HashCode, Builder 등등 그냥 IDE에서 Generate하거나 Commons-Lang등에서 제공하는 헬퍼들을 써도 너무 clutter들이 많이 생겨서 사용했다.
* 만족스럽다. 원래 compile-time preprocessing/generation/transform 같은게 자바스럽지 않다고 생각해서 싫어하는데, 그런거 신경 안써도 Maven이랑 연동 잘되서 잘 동작해서.

### Spring Boot Test: JUnit 4, AssertJ, Mockito, Jayway
* JUnit의 [Parameterized](https://github.com/junit-team/junit4/wiki/parameterized-tests) 을 입출력이 투명한 기능의 단위테스트 작성시 활용했다
* Spring Boot, WebMVC 등등에 연동하는 JUnit Runner들도 활용해 테스트를 작성.
* [Mockito](https://site.mockito.org/) 으로 한 test subject와 이에 연관된 부분이 잘 되었는지를 주로 검사하는데 사용
* [Jayway JSONPath](https://github.com/json-path/JsonPath) 사용해서 JSON결과등을 Assert

### HSQLDB
* 외부 DB을 사용하지 않고, 테스트케이스 실행시나 서버 실행시 in-jvm-memory에서 실행하는 DB으로 간단히 구현.

### Spring Boot Data JPA + Hibernate
* JPA Entity으로 기본 데이터 엔티티들 정의하고
* Spring Data JPA으로 CRUD 메서드/쿼리 일일이 작성을 하지 않아도 되도록.

----

## 문제해결 전략

### TDD, 골격만들기 
* 꼭 "테스트를 먼저 작성" 하지 않더라도, 작성한 코드를 실행해보고, 확인하는데 계속해서 테스트케이스를 작성하고 검토
* 작성한 코드를 변경할때 테스트가 실패함에 따라서 계속 수정한 내용을 테스트에 반영하며 리팩토링

### 중간 이음새를 체크하기, 가벼운 테스트케이스
* 전체 테스트들의 실행시간이 너무 길어지지 않도록 조절.
* 꼭 전체 integration이 필요하지 않고, 단순히 test subject와 그에 연결된 부분만 검증하려면, mock을 이용해서 가볍게 작성

### try-and-error, ..and repeat
* 테스트를 짜고, 실행하고, 틀리고, 고치고. 반복.

----

## 구현한 API의 특성/특징

### 데이터 업로드
1. CSV파일을 업로드하도록 만들었다.

### "특정 은행의 특정 달에 대해서 2018 년도 해당 달에 금융지원 금액을 예측하는 API"
1. `strategy` query parameter을 이용해서 다른 예측 알고리즘으로 쉽게 바꿔서 실행하도록 구조를 만들어놓았다.
  1. 지정하지 않으면, 기본값은 "매년 같은 달(month)의 평균값"을 제시하는 `average`.
1. 추가적인 알고리즘을 개발해도 쉽게 추가가 가능한 코드 구조를 만들어놓았다.
