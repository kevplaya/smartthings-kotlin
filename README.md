# SmartThings Kotlin Backend

Samsung SmartThings 기기를 제어하는 Kotlin + Spring Boot 백엔드 서비스입니다.

## 기술 스택

- **Kotlin**: 2.3.0
- **Spring Boot**: 4.0.2
- **Java**: 25
- **Gradle**: Kotlin DSL
- **Spring WebFlux**: WebClient를 통한 HTTP 통신

## 주요 기능

- SmartThings 기기 목록 조회

## 프로젝트 구조

```
src/
├── main/
│   ├── kotlin/com/example/smartthings/
│   │   ├── SmartThingsApplication.kt        # 메인 애플리케이션
│   │   ├── config/
│   │   │   └── SmartThingsConfig.kt         # WebClient 설정
│   │   ├── client/
│   │   │   └── SmartThingsClient.kt         # SmartThings API 클라이언트
│   │   ├── service/
│   │   │   └── DeviceService.kt             # 비즈니스 로직
│   │   └── web/
│   │       ├── dto/DeviceDto.kt             # API 응답 DTO
│   │       └── DeviceController.kt          # REST API 컨트롤러
│   └── resources/
│       ├── application.yml                   # 기본 설정
│       └── application-local.yml             # 로컬 개발 설정 (Git 제외)
└── test/
    └── kotlin/com/example/smartthings/
        ├── client/SmartThingsClientTest.kt
        ├── service/DeviceServiceTest.kt
        └── web/DeviceControllerTest.kt
```

## 설치 및 실행

### 1. SmartThings Personal Access Token 발급

1. https://account.smartthings.com/tokens 접속
2. 새 토큰 생성 (Devices scope 필요)
3. 토큰 복사

### 2. 로컬 설정 파일 생성

`src/main/resources/application-local.yml` 파일에 토큰 설정:

```yaml
smartthings:
  api:
    token: YOUR_TOKEN_HERE
```

### 3. 애플리케이션 실행

```bash
# Gradle wrapper 실행 권한 부여
chmod +x gradlew

# 로컬 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

또는 환경변수로 토큰 설정:

```bash
export SMARTTHINGS_TOKEN=YOUR_TOKEN_HERE
./gradlew bootRun
```

### 4. 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 애플리케이션이 실행 중일 때 API 호출
curl http://localhost:8080/api/devices
```

## API 문서

### GET /api/devices

SmartThings에 등록된 모든 기기 목록을 조회합니다.

**Response:**

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

## 개발 가이드

### 테스트 작성

TDD(Test-Driven Development) 방식을 따릅니다:

1. Red: 실패하는 테스트 작성
2. Green: 테스트를 통과하는 최소한의 코드 작성
3. Refactor: 코드 개선

### 커밋 규칙

- Subject: 72자 이하, 명령형
- 테스트는 해당 코드와 함께 커밋
- 리팩토링은 별도 커밋

## 참고 문서

- [SmartThings API Documentation](https://developer.smartthings.com/docs/api/public)
- [SmartThings Devices API](https://developer.smartthings.com/docs/api/public#tag/devices)
- [Authorization Guide](https://developer.smartthings.com/docs/getting-started/authorization-and-permissions)
