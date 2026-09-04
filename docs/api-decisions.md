# API 설계 결정 내역 (API Decisions)

## 1. JWT 로그아웃 방식 (MVP)

### 결정 내용
- **Stateless JWT 로그아웃 방식 채택**:
  - 서버에서 Access Token을 DB나 인메모리에 저장하지 않으며, 서버 측에서 강제로 토큰을 폐기하지 않습니다.
  - 로그아웃 요청(`POST /api/v1/auth/logout`) 시 서버는 성공 상태(`200 OK`)만 응답합니다.
  - 로그아웃 시 클라이언트(브라우저/앱)에서 로컬 스토리지나 메모리에 저장하고 있던 `accessToken`을 삭제하는 방식으로 로그아웃을 처리합니다.

### MVP 범위 제외 항목
- **JWT Blacklist**: 토큰 블랙리스트 테이블 및 저장 로직 제외
- **Redis / In-Memory Cache**: 분산 캐시 인프라 구축 제외
- **Refresh Token / Refresh Token Rotation (RTR)**: 토큰 갱신 및 DB 저장 제외
- **서버 세션 생성**: `SessionCreationPolicy.STATELESS` 유지

### 의도된 기술적 제한사항
- 서버에서 토큰을 블랙리스트 처리하지 않으므로, 이미 발급된 JWT는 유효 기간(만료 시각)이 지나기 전까지 기술적으로 서명이 유효한 상태로 남아있습니다.
- 클라이언트가 토큰을 삭제하여 요청 헤더(`Authorization: Bearer <token>`)를 보내지 않음으로써 이후 API 호출 시 `401 Unauthorized`가 발생하게 됩니다.

### 향후 개선 방안
- 보안 요구 수준이 높아질 경우 다음 전략을 단계적으로 검토합니다:
  1. **Short-lived Access Token + Refresh Token**: Access Token 유효시간을 수 분 단위로 단축하고 Refresh Token을 도입하여 탈취 위험 최소화
  2. **Redis 기반 Blacklist**: 로그아웃 시 남은 만료 시간 동안 Redis에 블랙리스트 토큰을 등록하여 즉시 접근 차단