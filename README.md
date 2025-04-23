## 코드 스타일 적용 안내

### IntelliJ 코드 포맷 자동 적용

1. `.editorconfig`는 자동 적용됩니다. (`Ctrl + Alt + L`로 정렬 가능)
    - 설정 위치: `Settings > Editor > Code Style > Enable EditorConfig support`

<br>

2. 추가로 Checkstyle을 쓰려면:
    - `Settings > Plugins > Checkstyle-IDEA` 설치
    - `src/code-style/naver-checkstyle-rules.xml` 로드

참고: 모든 코드 스타일 파일은 `src/code-style/` 디렉토리에 있습니다.

---

# 배달 어플 - Spring Boot 팀 프로젝트

## 프로젝트 구조

- `controller/`: API 진입 지점
- `service/`: 비즈니스 로직
- `domain/`: Entity + Repository
- `dto/`: 요청/응답 DTO
- `config/`: Security, Swagger 등 설정
- `exception/`: 전역 예외 처리

## API 명세

작성 예정. (Postman 또는 Swagger 자동 문서화)

## ERD

작성 예정. ERDCloud 사용

## 팀원별 역할

| 이름  | 담당 |
|-----|----|
| 기원  |    |
| 최혁  |    |
| 정승원 |    |
| 양재호 |    |
| 김희준 |    |

## 기술 스택

- Java 17, Spring Boot 3.1.x
- JPA + MySQL + Redis
- JWT + Spring Security
- Swagger (springdoc-openapi)

## 코드 스타일

- 네이버 Java 코드 스타일 + `.editorconfig`
- 줄바꿈: LF 통일

## 공통 응답 및 예외처리

### CommonResponseDto (공통 응답 DTO)

- 예외/성공 공통 응답 DTO 입니다.
- `code` 필드는 커스텀코드가 아니므로, 추후 삭제 가능성이 있습니다.
- 정적 팩토리 메서드 패턴을 적용하여 가독성 있게 생성할 수 있게 했습니다.

### ResponseCode (공통 코드 클래스)

- 예외/성공 코드의 부모 클래스입니다.
  `(ex. OrderErrorCode, OrderSuccessCode..)`

### CustomException (커스텀 예외 클래스)

- `RuntimeException`을 상속받은 커스텀 예외 클래스
- 도메인 별, 예외코드 필드(`ResponseCode`)를 갖고 있습니다.

### GlobalExceptionHandler (전역 예외 핸들러 클래스)

- 기본 응답형식은 `ResponseEntity<CommonResponseDto<Void>>` 형태로 합니다.
- `Void`인 이유는 예외의 경우, 예외발생시 전달 데이터가 없기 때문입니다.
- `Service Layer`에서 `CustomException` 생성자를 통해 `도메인 ErrorCode`를 넘겨 반환합니다.
- 