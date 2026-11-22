# Bluesky Boot - GitHub Copilot Instructions

## 프로젝트 개요

Bluesky Boot는 Spring Boot를 확장한 라이브러리입니다.
애플리케이션 하나가 도메인 · 경로별로 여러 모듈(사이트)을 서비스할 때, 모듈마다 다른 설정을 쓰도록 돕습니다.
자세한 설명은 `bluesky-boot-build/bluesky-boot-docs/src/main/asciidoc/bluesky-boot/bluesky-boot.adoc` 에 있습니다.

## 주요 기능

- 모듈 지원: 요청의 도메인 · 경로로 모듈을 고르고(`ModuleNameResolver`), 현재 모듈을 `BlueskyContextHolder` 에 둠
- properties 3단 구조: `XxxProperties`(parent) · `XxxGroupProperties` · `XxxModuleProperties`, parent → group → `ModuleInfo` preset → module 순서로 병합
- 모듈별 locale · cookie · 도메인 설정과 locale resolve handler
- 여러 message basename 을 합친 MessageSource
- 여러 DataSource 와 routing DataSource
- 공통 예외 응답(ProblemDetail), UUID 생성기, htmx 지원, TextEncryptor

## 기술 스택

- Java 25
- Spring Boot 4 (Jackson 3, `tools.jackson`)
- Maven, Spotless (google-java-format)

## 프로젝트 구조

```
bluesky-boot/
├── bluesky-boot-build/
│   ├── bluesky-boot/                       # 핵심 기능 (io.github.luversof.boot)
│   ├── bluesky-boot-autoconfigure/         # AutoConfiguration (io.github.luversof.boot.autoconfigure)
│   ├── bluesky-boot-test-autoconfigure/    # 시험 지원 (REST Docs 등)
│   ├── bluesky-boot-properties-migrator/   # 이름이 바뀐 property 안내
│   ├── bluesky-boot-docs/                  # asciidoc 문서
│   ├── bluesky-boot-dependencies/          # 의존성 관리
│   └── bluesky-boot-parent/                # 사용하는 프로젝트의 parent pom
├── docs/agents/                            # agent 용 이슈 트래커 · 라벨 · 도메인 문서 설정
└── README.adoc
```

## 사용 프로젝트

- **bluesky-cloud**: Spring Cloud 인프라
- **bluesky-project**: 메인 비즈니스 애플리케이션

## 관련 프로젝트

- **bluesky-boot-crypto**: 암호화 기능 제공
- **bluesky-boot-autoconfigure-devcheck**: 개발 확인 도구
- **bluesky-boot-autoconfigure-connectioninfo**: 연결 정보 자동 구성

## 코딩 규칙

### 명명 규칙

- AutoConfiguration 클래스: `{Feature}AutoConfiguration`
- Properties 클래스: `{Feature}Properties`, `{Feature}GroupProperties`, `{Feature}ModuleProperties`
- 설정 클래스: `{Feature}Configuration`

### 설정 원칙

- `@ConditionalOnProperty`를 활용하여 기능 활성화/비활성화 지원
- `@ConfigurationProperties`로 외부 설정 바인딩 (prefix `bluesky-boot.<feature>`)
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`에 AutoConfiguration 등록
- 커밋 전 `mvn spotless:apply` 후 `mvn spotless:check`
