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
|-----|---|
| 기원  |  |
| 최혁  |  |
| 정승원 |  |
| 양재호 |  |
| 김희준 |  |

## 기술 스택
- Java 17, Spring Boot 3.1.x
- JPA + MySQL + Redis
- JWT + Spring Security
- Swagger (springdoc-openapi)

## 코드 스타일
- 네이버 Java 코드 스타일 + `.editorconfig`
- 줄바꿈: LF 통일
