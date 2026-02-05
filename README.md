# SmartThings Kotlin Backend

Samsung SmartThings 기기를 제어하는 Kotlin + Spring Boot 백엔드 서비스입니다.

## 기술 스택

- **Kotlin**: 2.3.0
- **Spring Boot**: 4.0.2
- **Java**: 25
- **Gradle**: Kotlin DSL
- **Spring WebFlux**: 비동기 HTTP 서버 및 WebClient
- **Kotlin Coroutines**: `kotlinx-coroutines-reactor`로 WebClient와 연동
- **Spring Data JPA**: 기기 별명 등 사용자 데이터 저장 (H2)
- **Resilience4j**: Circuit Breaker로 외부 API 장애 격리

## 기술 스택 선택 이유

- **Kotlin**: null 안전성, 데이터 클래스·코루틴 등으로 생산성과 유지보수성 확보.
- **Spring WebFlux + Coroutines**: 논블로킹 스택으로 스레드 효율을 높이고, suspend 함수로 가독성 있는 비동기 흐름 유지.
- **JPA + H2**: 기기 별명 같은 단순 도메인은 JPA로 빠르게 모델링·구현. 테스트·로컬은 H2 인메모리/파일 사용.
- **Resilience4j**: SmartThings API 장애 시 Circuit Breaker로 실패를 격리하고, 503 등 일관된 에러 응답 제공.

## 아키텍처

- **레이어드 + 헥사고날(포트/어댑터)**: 도메인·애플리케이션 로직과 외부 API·DB를 명확히 분리.
  - **포트**: `DeviceSource`(기기 목록 조회) 등 인터페이스로 “필요한 능력”만 정의.
  - **어댑터**: `SmartThingsClient`가 `DeviceSource` 구현체로, 외부 SmartThings API 호출을 담당.
  - **의존성 방향**: Controller → Service → Port(인터페이스), Adapter → Port 구현.
- **도메인**: `UserDeviceAlias` 등 엔티티는 `domain/`에 두고, 외부 DTO와 구분.

## 안정성

- **글로벌 예외 처리**: `@RestControllerAdvice`로 `WebClientResponseException`(4xx/5xx), 타임아웃, 기타 예외를 표준화된 `ErrorResponse`(code, message, path, timestamp)로 매핑해 클라이언트에 일관된 에러 응답 제공.
- **Resilience4j**: SmartThings 호출에 Circuit Breaker 적용. 장애 시 “SmartThings API temporarily unavailable” 등 503 응답으로 외부 장애 전파 완화.

## 주요 기능

- SmartThings 기기 목록 조회
- 사용자별 기기 별명 저장·조회 (DB)

## 프로젝트 구조

```
프로젝트 루트/
├── src/                              # 백엔드 (Kotlin/Spring Boot)
│   ├── main/
│   │   ├── kotlin/com/example/smartthings/
│   │   │   ├── SmartThingsApplication.kt
│   │   │   ├── config/               # 설정 (CORS, SmartThings)
│   │   │   │   ├── CorsConfig.kt
│   │   │   │   └── SmartThingsConfig.kt
│   │   │   ├── port/
│   │   │   │   └── DeviceSource.kt   # 포트 인터페이스
│   │   │   ├── client/
│   │   │   │   └── SmartThingsClient.kt  # DeviceSource 구현
│   │   │   ├── domain/
│   │   │   │   ├── InstalledApp.kt   # Webhook 설치 정보 (authToken 등)
│   │   │   │   └── UserDeviceAlias.kt
│   │   │   ├── repository/
│   │   │   │   ├── InstalledAppRepository.kt
│   │   │   │   └── UserDeviceAliasRepository.kt
│   │   │   ├── service/
│   │   │   │   ├── DeviceAliasService.kt
│   │   │   │   ├── DeviceService.kt
│   │   │   │   └── SmartThingsTokenProvider.kt  # InstalledApp 또는 PAT
│   │   │   └── web/
│   │   │       ├── dto/
│   │   │       │   ├── DeviceAliasDto.kt
│   │   │       │   ├── DeviceDto.kt
│   │   │       │   ├── ErrorResponse.kt
│   │   │       │   ├── SmartAppRequest.kt
│   │   │       │   └── SmartAppResponse.kt
│   │   │       ├── GlobalExceptionHandler.kt
│   │   │       ├── DeviceAliasController.kt
│   │   │       ├── DeviceController.kt
│   │   │       └── SmartAppController.kt   # POST /smartapp (lifecycle)
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── kotlin/com/example/smartthings/
│           ├── client/SmartThingsClientTest.kt
│           ├── service/DeviceServiceTest.kt
│           └── web/DeviceControllerTest.kt
└── frontend/                         # 프론트엔드 (React/Vite)
    ├── src/
    │   ├── api/                      # API 클라이언트
    │   │   └── devices.ts            # 기기 목록 조회
    │   ├── pages/                    # 페이지 컴포넌트
    │   │   └── DevicesPage.tsx       # 기기 목록 페이지
    │   ├── types/
    │   │   └── device.ts             # TypeScript 타입 정의
    │   ├── App.tsx                   # 루트 컴포넌트, 라우팅
    │   └── main.tsx                  # 진입점
    ├── .env.development              # 로컬 환경 변수
    ├── .env.production               # 프로덕션 환경 변수
    ├── vercel.json                   # Vercel 배포 설정
    └── vite.config.ts                # Vite 빌드 설정
```

## 설치 및 실행

### 1. SmartThings Personal Access Token 발급

1. https://account.smartthings.com/tokens 접속
2. 새 토큰 생성 (Devices scope 필요)
3. 토큰 복사

### 2. 로컬 설정 파일 생성

`src/main/resources/application-local.yml` 파일에 토큰 설정 (Git 제외):

```yaml
smartthings:
  api:
    token: YOUR_TOKEN_HERE
```

### 3. 애플리케이션 실행

```bash
chmod +x gradlew
./gradlew bootRun --args='--spring.profiles.active=local'
```

또는 환경변수로 토큰 설정:

```bash
export SMARTTHINGS_TOKEN=YOUR_TOKEN_HERE
./gradlew bootRun
```

### 4. Docker로 실행

```bash
export SMARTTHINGS_TOKEN=YOUR_TOKEN_HERE
docker compose up --build
```

- H2 DB 데이터는 `app-data` 볼륨에 저장됩니다.
- 앱만 빌드: `docker build -t smartthings-kotlin .`

### 5. 프론트엔드 (React + MUI)

```bash
cd frontend
cp .env.example .env.local   # 필요 시 API URL 수정
npm install
npm run dev
```

- 개발 서버: http://localhost:5173
- 백엔드 API: `VITE_API_URL` (기본 개발 시 http://localhost:8080)

### 6. OAuth 인증 (SmartThings 로그인)

앱은 SmartThings OAuth로 “나만 사용”하도록 제한할 수 있습니다. 사용자가 SmartThings 계정으로 로그인해야 기기 목록 등 API를 사용할 수 있습니다.

**SmartThings CLI로 OAuth 앱 등록**

1. [SmartThings CLI](https://github.com/SmartThingsCommunity/smartthings-cli) 설치 후 `smartthings apps:create` 실행
2. **Display name**: 앱 표시 이름 (예: SmartThings Kotlin App)
3. **Target URL**: 백엔드 배포 URL (예: `https://xxx.railway.app`)
4. **Redirect URIs**: `https://xxx.railway.app/api/oauth/callback`, 로컬 테스트 시 `http://localhost:8080/api/oauth/callback`
5. **Permissions**: `r:devices:*`, `x:devices:*`
6. 등록 후 발급된 **Client ID**, **Client Secret**을 Railway 환경 변수에 설정 (Git에 올리지 않음)

**로컬 OAuth 테스트**

- 백엔드: `SERVER_BASE_URL=http://localhost:8080`, `FRONTEND_URL=http://localhost:5173`, `SMARTTHINGS_OAUTH_CLIENT_ID`, `SMARTTHINGS_OAUTH_CLIENT_SECRET` 설정 후 실행
- SmartThings 앱 등록 시 Redirect URI에 `http://localhost:8080/api/oauth/callback` 추가

### 7. 배포 (Railway + Vercel)

- **백엔드**: [Railway](https://railway.app)에 배포. 환경 변수에만 비밀/토큰 설정 (Git에 올리지 않음).
- **프론트엔드**: [Vercel](https://vercel.com)에 배포.

**Railway 환경 변수 (OAuth 사용 시)**

| 변수 | 설명 |
|------|------|
| `SMARTTHINGS_OAUTH_CLIENT_ID` | SmartThings CLI에서 발급한 Client ID |
| `SMARTTHINGS_OAUTH_CLIENT_SECRET` | SmartThings CLI에서 발급한 Client Secret |
| `SERVER_BASE_URL` | 백엔드 공개 URL (예: `https://xxx.railway.app`) |
| `FRONTEND_URL` | 프론트엔드 URL (예: `https://xxx.vercel.app`) |

**Vercel 배포 절차**

1. 이 저장소를 GitHub에 푸시한 뒤 Vercel 로그인
2. **Add New Project** → **Import Git Repository**에서 해당 저장소 선택
3. **Root Directory**를 `frontend`로 설정
4. **Environment Variables**에 `VITE_API_URL` 추가 (Railway 백엔드 URL, 예: `https://xxx.railway.app`) — **Git에 올리지 않고 Vercel 대시보드에서만 설정**
5. **Deploy** 실행

**배포 순서 권장**: Railway로 백엔드 먼저 배포 → SmartThings CLI로 Redirect URI에 Railway URL 등록 → Vercel로 프론트 배포 → Railway에 `FRONTEND_URL`에 Vercel URL 설정

### 8. 보안 (비밀/URL 관리)

| 항목 | Git에 올리면 안 됨 | 설정 위치 |
|------|---------------------|-----------|
| 프로덕션 백엔드 URL | 예 | Vercel 환경 변수 `VITE_API_URL` |
| SmartThings 토큰 / OAuth Client Secret | 예 | Railway(백엔드) 환경 변수 |
| DB URL, API 키 등 | 예 | Railway 환경 변수 |

- 로컬용 예시만 `.env.example`, `application-local.yml` 예시 형태로 둘 수 있으며, 실제 값은 각자 로컬·배포 환경 변수에만 둡니다.

### 9. 테스트

```bash
./gradlew test
```

## API 문서

- **인증**: OAuth 사용 시 `/api/devices`, `/api/users/*` 등은 세션 쿠키(`SESSION_ID`) 필요. 미인증 시 `401 Unauthorized`.
- **공개**: `/api/oauth/authorize`, `/api/oauth/callback`, `/api/user/me`는 세션 없이 호출 가능.

### GET /api/user/me

현재 로그인 사용자 ID. 세션 없으면 `id`가 빈 문자열.

**Response:** `200 OK` — `{ "id": "user-id" }` 또는 `{ "id": "" }`

### GET /api/oauth/authorize

SmartThings OAuth 로그인 URL을 반환. 프론트에서 이 URL로 리다이렉트.

**Response:** `200 OK` — `{ "url": "https://api.smartthings.com/oauth/authorize?..." }`

### GET /api/oauth/callback

SmartThings가 인증 후 호출. `code`로 토큰 교환 후 세션 생성, 프론트로 리다이렉트.

### POST /api/oauth/logout

세션 삭제 후 프론트 URL로 리다이렉트. 쿠키 포함 요청.

### GET /api/devices

SmartThings에 등록된 기기 목록. **인증 필요** (OAuth 세션).

**Response:** `200 OK`

```json
[
  {
    "deviceId": "device-id-1",
    "name": "Living Room Light",
    "label": "Living Room Light",
    "manufacturerCode": "samsung",
    "typeName": "Light",
    "type": "LIGHT",
    "roomId": "room-id",
    "locationId": "location-id"
  }
]
```

### GET /api/users/{userId}/devices/{deviceId}/alias

사용자·기기별로 저장한 별명을 조회합니다. 없으면 `404`.

**Response:** `200 OK`

```json
{ "alias": "거실 조명" }
```

### PUT /api/users/{userId}/devices/{deviceId}/alias

사용자·기기별 별명을 저장하거나 수정합니다.

**Request body:**

```json
{ "alias": "거실 조명" }
```

**Response:** `200 OK`

```json
{ "alias": "거실 조명" }
```

## 참고 문서

- [SmartThings API Documentation](https://developer.smartthings.com/docs/api/public)
- [SmartThings Devices API](https://developer.smartthings.com/docs/api/public#tag/devices)
- [Authorization Guide](https://developer.smartthings.com/docs/getting-started/authorization-and-permissions)
