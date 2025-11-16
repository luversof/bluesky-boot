# Bluesky Boot - GitHub Copilot Instructions

## 프로젝트 개요

Bluesky Boot는 Spring Boot를 확장하여 사용하기 위해 만든 프레임워크입니다.

Spring Boot의 AutoConfiguration을 더욱 편리하게 확장하여 실무에서 필요한 기능들을 제공합니다.

## 주요 기능

- 멀티 모듈 지원
- 다국어 메시지 복수 설정 처리
- DataSource 복수 설정 제공 (예정)
- Mongo 복수 설정 제공 (예정)

## 기술 스택

- Java 17
- Spring Boot 3.x
- Maven

## 프로젝트 구조

```
bluesky-boot/
├── bluesky-boot-build/          # 빌드 설정
└── README.adoc                  # 프로젝트 문서
```

## 설계 원칙

### 1. AutoConfiguration 확장

Spring Boot의 기본 AutoConfiguration은 단일 DataSource, 단일 MongoDB 등을 전제로 합니다.
실무에서는 여러 개의 DataSource나 MongoDB를 사용하는 경우가 많으므로, 이를 쉽게 설정할 수 있도록 확장합니다.

### 2. 편의성 중심

개발자가 반복적으로 작성해야 하는 설정 코드를 최소화하고, Convention over Configuration 원칙을 따릅니다.

### 3. 멀티 모듈 지원

대규모 프로젝트에서 여러 모듈을 효율적으로 관리할 수 있도록 지원합니다.

## 사용 프로젝트

이 프레임워크는 다음 프로젝트들에서 사용됩니다:

- **bluesky-cloud**: Spring Cloud 인프라
- **bluesky-project**: 메인 비즈니스 애플리케이션

## 관련 프로젝트

- **bluesky-boot-crypto**: 암호화 기능 제공
- **bluesky-boot-autoconfigure-devcheck**: 개발 확인 도구
- **bluesky-boot-autoconfigure-connectioninfo**: 연결 정보 자동 구성

## Maven Dependency

```xml
<dependency>
    <groupId>io.github.luversof</groupId>
    <artifactId>bluesky-boot</artifactId>
    <version>${version}</version>
</dependency>
```

## 코딩 규칙

### 패키지 구조

```
io.github.luversof.boot/
├── autoconfigure/               # AutoConfiguration 클래스
├── config/                      # 설정 클래스
└── util/                        # 유틸리티 클래스
```

### 명명 규칙

- AutoConfiguration 클래스: `{Feature}AutoConfiguration`
- Properties 클래스: `{Feature}Properties`
- 설정 클래스: `{Feature}Configuration`

### 설정 원칙

- `@ConditionalOnProperty`를 활용하여 기능 활성화/비활성화 지원
- `@ConfigurationProperties`로 외부 설정 바인딩
- `spring.factories` 또는 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`에 AutoConfiguration 등록
