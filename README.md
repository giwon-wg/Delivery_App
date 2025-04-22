## 코드 스타일 적용 안내

### IntelliJ 코드 포맷 자동 적용

1. `.editorconfig`는 자동 적용됩니다. (`Ctrl + Alt + L`로 정렬 가능)
    - 설정 위치: `Settings > Editor > Code Style > Enable EditorConfig support`

<br>

2. 추가로 Checkstyle을 쓰려면:
    - `Settings > Plugins > Checkstyle-IDEA` 설치
    - `src/code-style/naver-checkstyle-rules.xml` 로드

참고: 모든 코드 스타일 파일은 `src/code-style/` 디렉토리에 있습니다.
