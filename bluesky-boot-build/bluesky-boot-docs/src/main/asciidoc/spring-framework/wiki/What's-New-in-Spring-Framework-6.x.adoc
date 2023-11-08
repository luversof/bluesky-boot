## What's New in Version 6.1

106 revisions

### Core Container

* [virtual threads](https://github.com/spring-projects/spring-framework/issues/23443) 및 JDK 21와 전반적으로 호환됨.
* virtual thread에 대한 Configuration option: 전용 [VirtualThreadTaskExecutor](https://docs.spring.io/spring-framework/docs/6.1.0-SNAPSHOT/javadoc-api/org/springframework/core/task/VirtualThreadTaskExecutor.html) 와 [SimpleAsyncTaskExecutor의 virtual threads mode](https://docs.spring.io/spring-framework/docs/6.1.0-SNAPSHOT/javadoc-api/org/springframework/core/task/SimpleAsyncTaskExecutor.html#setVirtualThreads(boolean)), 그리고 new-thread-per-task strategy 와 virtual threads mode가 있는 유사한 [SimpleAsyncTaskScheduler](https://docs.spring.io/spring-framework/docs/6.1.0-SNAPSHOT/javadoc-api/org/springframework/scheduling/concurrent/SimpleAsyncTaskScheduler.html)가 있음.
* JVM checkpoint restore을 위한 Project CRaC와의 Lifecycle 통합([관련 문서](https://docs.spring.io/spring-framework/reference/6.1/integration/checkpoint-restore.html) 참조).
* Lifecycle 통합 [pause/resume 기능](https://github.com/spring-projects/spring-framework/issues/30831) 및 `ThreadPoolTaskExecutor` 와 `ThreadPoolTaskScheduler` , `SimpleAsyncTaskScheduler` 에 대한 [parallel graceful shutdown](https://github.com/spring-projects/spring-framework/issues/27090) for .
* Async/reactive destroy method (예: R2DBC에서 `ConnectionFactory`); [26691](https://github.com/spring-projects/spring-framework/issues/26991) 참조.
* Async/reactive cacheable method, `Cache` interface 및 `CaffeineCacheManager` 의 해당 지원 포함; [17559](https://github.com/spring-projects/spring-framework/issues/17559) and [17920](https://github.com/spring-projects/spring-framework/issues/17920) 참조.
* Reactive `@Scheduled` methods (Kotlin coroutines 포함); [22924](https://github.com/spring-projects/spring-framework/pull/29924) 참조.
* 각 `@Scheduled` method에 대한 특정 대상 scheduler 선택; [20818](https://github.com/spring-projects/spring-framework/issues/20818) 참조.
* 일회성 task에 대한 `@Scheduled` method (초기 지연만 포함); [31211](https://github.com/spring-projects/spring-framework/issues/31211) 참조.
* `@Scheduled` method의 Observation instrumentation; [29883](https://github.com/spring-projects/spring-framework/issues/29883) 참조.
* Spring Framework는 `@Async` 또는 `@EventListener` annotated method에 대한 observation을 즉시 생성하지는 않지만 해당 method의 실행에 대한 context (예: 현재 trace id가 있는 MDC logging)를 전파하는데 도움이 됨. 새로운 `ContextPropagatingTaskDecorator` , [관련 참조 문서 section](https://docs.spring.io/spring-framework/reference/6.1/integration/observability.html#observability.application-events) 및 [issue 31130](https://github.com/spring-projects/spring-framework/issues/31130) 참조.
* programming 방식 validator 구현을 위한 `Validator` factory method; [29890](https://github.com/spring-projects/spring-framework/pull/29890) 참조.
* flexible programming 방식으로 사용할 수 있도록 `Errors` 및 `Errors.failOnError` method 를 반환하는 `Validator.validateObject(Object)`; [19877](https://github.com/spring-projects/spring-framework/issues/19877) 참조.
* `MethodValidationInterceptor` 는 `MessageSource` resolvable code와 cascade violation이 있는 `@Valid` argument에 대한 `Errors` instances에 맞게 조정된 violation이 있는 `ConstraintViolationException` 의 subclass인 `MethodValidationException` 를 throw 함. [29825](https://github.com/spring-projects/spring-framework/issues/29825), 및 umbrella issue [30645](https://github.com/spring-projects/spring-framework/issues/30645) 참조.
* `@PropertySource` 에서 resource patterns 지원; [21325](https://github.com/spring-projects/spring-framework/issues/21325) 참조.
* `BeanWrapper` 및 `DirectFieldAccessor` 에서 `Iterable` 및 `MultiValueMap` binding 지원; [907](https://github.com/spring-projects/spring-framework/pull/907) 및 [26297](https://github.com/spring-projects/spring-framework/issues/26297) 참조.
* `Instant` 및 `Duration` parsing 개선(Spring Boot와 일치); [22013](https://github.com/spring-projects/spring-framework/issues/22013) 참조.
* SpEL 표현식에서 property/field/variable name에 A-Z 이외ㅡ이 문자를 지원; [30580](https://github.com/spring-projects/spring-framework/issues/30580) 참조. 
* `MethodHandle` 을 SpEL function으로 등록하는 기능 지원 ([관련 문서](https://docs.spring.io/spring-framework/reference/6.1/core/expressions/language-ref/functions.html) 참조).
* 이제 Spring AOP 가 Coroutine을 지원함; [22462](https://github.com/spring-projects/spring-framework/issues/22462) 참조.

### Data Access and Transactions

* transaction manager에 의해 trigger 되는 before/afterBegin, before/afterCommit 및 before/afterRollback callback이 있는 일반적인 `TransactionExecutionListener` contract (thread-bound 및 reactive transaction 용); [27479](https://github.com/spring-projects/spring-framework/issues/27479) 참조.
* `@TransactionalEventListener` 와 `TransactionalApplicationListener` 는 async multicaster 설정과 무관하게 항상 original thread에서 실행됨; [30244](https://github.com/spring-projects/spring-framework/issues/30244) 참조.
* `@TransactionalEventListener` 와 `TransactionalApplicationListener` 는 `ApplicationEvent` 가 transaction context를 event source로 하여 publish 될 때 reactive transaction에 참여할 수 있음; [27515](https://github.com/spring-projects/spring-framework/issues/27515) 참조.
* 실패한 `CompletableFuture` 는 async transactional method애 대한 rollback을 trigger 함; [30018](https://github.com/spring-projects/spring-framework/issues/30018) 참조.
* `DataAccessUtils` 는 `java.util.Optional` return type으로 다양한 `optionalResult` methods 를 제공함; [27735](https://github.com/spring-projects/spring-framework/pull/27735) 참조.
* 새로운 `JdbcClient` 는 flexible parameter option과 flexible result retrieval option을 사용하여 `JdbcTemplate` 및 `NamedParameterJdbcTemplate` 위에 query/update 믄을 위한 통합된 facade를 제공; [30931](https://github.com/spring-projects/spring-framework/issues/30931) 참조. 
* `JdbcTemplate` / `NamedParameterJdbcTemplate` 및 `JdbcClient` 와 함께 사용하기 위한 `SimplePropertyRowMapper` 및 `SimplePropertySqlParameterSource` strategy는 result object 및 named parameter holder에 대한 flexible constructor/property/field mapping을 제공함; [26594](https://github.com/spring-projects/spring-framework/issues/26594#issuecomment-1678725276) 참조.
* `SQLExceptionSubclassTranslator` 는 overriding `customTranslator` 로 구성할 수 있음; [24634](https://github.com/spring-projects/spring-framework/issues/24634) 참조.
* The R2DBC `DatabaseClient` 는 parameter value의 사전 구성된 map을 위한 `bindValues(Map)` 과 record component를 기반으로 하는 parameter object를 위한 `bindProperties(Object)` 를 제공함, [27282](https://github.com/spring-projects/spring-framework/issues/27282) 참조.
* The R2DBC `DatabaseClient` 는 plain database column value에 대한 `mapValue(Class)` 와 bean properties 또는 record component에 기반한 result object에 대한 `mapProperties(Class)` 를 제공함; [26021](https://github.com/spring-projects/spring-framework/issues/26021) 참조.
* R2DBC에서도 `BeanPropertyRowMapper` 및 `DataClassRowMapper` 를 사용할 수 있음; [30530](https://github.com/spring-projects/spring-framework/pull/30530) 참조.
* `HibernateJpaDialect` 를 사용하는 `JpaTransactionManager` 는 가능한 경우 Hibernate commit/rollback exception을 repository operation에 대한 persistence exception translation에서 던져진 exception hierarchy에 맞춰 가능한 경우 `DataAccessException` subclass (예: `CannotAcquireLockException` ) 로 변환함. 주요 동기는 [31274](https://github.com/spring-projects/spring-framework/issues/31274) 참조: PostgreSQL serialization failures.

### Web Applications

* 이제 Spring MVC 와 WebFlux 는 `@Constraint` annotations이 있는 controller method parameter에 대한 method validation을 기본으로 지원함.
즉, AOP proxy를 통해 method validation을 활성화 하기 위해 controller class level에서 `@Validated` 가 더 이상 필요하지 않음.
기본 제공 method validation은 model attribute 및 request body argument에 대한 기존 argument validation 위에 계층화 됨.
double validation으로 사례 방지와 같이 두 가지가 더욱 긴밀하게 통합되고 조정됨.
 migration에 대한 자세한 내용은 [Upgrading to 6.1](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x#web-applications) 및 모든 관련 task 및 feedback은 umbrella issue [30645](https://github.com/spring-projects/spring-framework/issues/30645) 를 참조.
* 이제 object의 collections, arrays, 또는 maps 에서 method validation이 지원됨.
* 새로운 기본 제공 method validation에 의해 발생하는 `HandlerMethodValidationException` 은 controller method parameter type (예: `@RequestParameter`, `@PathVariable` 등)에 따라 validation error를 처리하기 위해 `Visitor` API를 노출함. 
* `MethodValidationInterceptor` 는 `Mono` 및 `Flux` method parameters의 validation을 지원함, [20781](https://github.com/spring-projects/spring-framework/issues/20781) 참조.
* Spring MVC는 matching handler가 없는 경우 기본적으로 `NoHandlerFoundException` 을 발생 시키고 matching static resource가 없는 경우 `ResponseStatusException(NOT_FOUND)` 을 발생시키며 또한 RFC 7807 response를 포함하여 404 error를 기본적으로 일관되게 처리하는 것을 목표로 처리함. [29491](https://github.com/spring-projects/spring-framework/issues/29491) 참조.
* [ErrorResponse](https://docs.spring.io/spring-framework/docs/6.1.0-SNAPSHOT/javadoc-api/org/springframework/web/ErrorResponse.html) 를 사용하면 `MessageSource` 를 통해 `ProblemDetail` 을 [customization](https://docs.spring.io/spring-framework/reference/6.1/web/webmvc/mvc-ann-rest-exceptions.html#mvc-ann-rest-exceptions-i18n) 하고 builder를 통해 custom `ProblemDetail` 을 사용할 수 있음.
* Spring MVC는 error를 처리하고 error response를 rendering 하기 전에 Servlet response buffer 를 재설정 함. 
* 이제 `DataBinder` 는 argument value가 `NameResolver` 를 통해 조회되는 [constructor binding](https://docs.spring.io/spring-framework/reference/6.1-SNAPSHOT/core/validation/beans-beans.html#beans-constructor-binding) 을 지원하며 (예: HTTP request parameters map), 이러한 조회는 `@BindParam` annotation을 통해 custom 할 수 있다.
또한 constructor parameter를 초기화하는데 필요한 constructor 호출을 통해 중첩된 object structure도 지원한다.
이 기능은 Spring MVC 및 WebFlux의 data binding에 통합되어 있으며, 예샹되는 parameter만 data binding 할 수 있는 더 안전한 옵션을 제공한다. (자세한 내용은 [Model Design](https://docs.spring.io/spring-framework/reference/6.1-SNAPSHOT/web/webflux/controller/ann-initbinder.html) 참조.
이제 Spring MVC 및 WebFlux 는 nested objects constructor를 포함하여 constructor를 통한 data binding을 지원한다.
* WebFlux는 `VirtualThreadTaskExecutor` 와 같은 다른 `Executor` 에서 synchronous signature가 있는 controller method의 실행을 차단하는 옵션을 제공한다, 참조 문서의 [Blocking Execution](https://docs.spring.io/spring-framework/reference/6.1-SNAPSHOT/web/webflux/config.html#webflux-config-blocking-execution) 을 참조.
* 이제 `SseEmitter` 는 SSE format에 따라 newline 으로 format data를 지정.
* `WebClient` 와 유사한 API를 제공하지만 `RestTemplate` 과 infrastructure를 공유하는 새로운 synchronous HTTP client인 `RestClient` 가 추가됨. [29552](https://github.com/spring-projects/spring-framework/issues/29552) 참조.
* `RestTemplate` 및 `RestClient` 와 함께 사용하기 위한 Jetty 기반 `ClientHttpRequestFactory` ; [30564](https://github.com/spring-projects/spring-framework/issues/30564) 참조.
* `RestTemplate` 및 `RestClient` 와 함께 사용하기 위한 JDK HttpClient 기반 `ClientHttpRequestFactory` ; [30478](https://github.com/spring-projects/spring-framework/pull/30478) 참조.
* `RestTemplate` 및 `RestClient` 와 함께 사용하기 위한 Reactor Netty 기반 `ClientHttpRequestFactory` ; [30835](https://github.com/spring-projects/spring-framework/issues/30835) 참조.
* 다양한 `ClientHttpRequestFactory` 구현에서 buffering 개선; [30557](https://github.com/spring-projects/spring-framework/issues/30557) 참조.
* reactive `WebClient` 를 위한 기존 adapter에 더해 새로운 `RestClient` 및 `RestTemplate` 용 [HTTP Interface client](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface) 내장 adapter 제공. 
* HTTP Interface client 는 input method parameter로 `MultipartFile` 을 지원함.
* HTTP Interface client 는 기본 구성 대신 사용할 수 있는 input method parameter (예: baseURL을 동적으로 변경해야 하는 경우)로 `UriBuilderFactory` 를 지원.
* HTTP interface method에 사용되는 `@HttpExchange` annotation은 이제 `@RequestMapping` 의 대안으로 Spring MVC 및 WebFlux에서 server side handling을 위해 지원됨, 자세한 내용과 지침은 [@HttpExchange](https://docs.spring.io/spring-framework/reference/6.1-SNAPSHOT/web/webmvc/mvc-controller/ann-requestmapping.html#mvc-ann-httpexchange-annotation) 를 참조.
* `RestTemplate` 및 `RestClient` 에서 함께 사용하기 위한 Reactor Netty 기반 `ClientHttpRequestFactory` 와 `WebClient` 와 함께 사용하기 위한 `ClientHttpConnector` 에 JVM checkpoint 복원 지원이 추가됨; [31280](https://github.com/spring-projects/spring-framework/issues/31280), [31281](https://github.com/spring-projects/spring-framework/issues/31281) 및 [31180](https://github.com/spring-projects/spring-framework/issues/31180) 참조.
* General Coroutine은 WebFlux의 revision을 지원하며, 여기에는 [`CoWebFilter` 의 `CoroutineContext` 전파](https://github.com/spring-projects/spring-framework/issues/27522), [`filter` 가 있는 `coRouter` DSL의 `CoroutineContext` 전파](https://github.com/spring-projects/spring-framework/issues/26977), [`coRouter` DSL의 새로운 `context` function](https://github.com/spring-projects/spring-framework/issues/27010), [WebFlux의 suspending function이 있는 `@ModelAttribute` 지원](https://github.com/spring-projects/spring-framework/issues/30894) 및 [ `awaitSingle()` 의 `Mono` variant를 일관되게 사용하는 것](https://github.com/spring-projects/spring-framework/issues/31127) 이 포함됨.

### Messaging Applications

* STOMP messaging은 특정 client로 부터 받은 message를 순서대로 처리하기 위한 새로운 `preserveReceiveOrder` config option 을 지원함.
이 option은 clients에 publish 된 message에 대한 기존의 `preservePublishOrder` flag 에 추가됨.
참조 문서의 [Order of Messages](https://docs.spring.io/spring-framework/reference/6.1/web/websocket/stomp/ordered-messages.html) section 참조.
* RSocket interface method에 사용되는 `@RSocketExchange` annotation은 이제 응답자 측 처리를 위해 `@MessageMapping` 대신 지원됨, 자세한 내용과 지침은 [@RSocketExchange](https://docs.spring.io/spring-framework/reference/6.1-SNAPSHOT/rsocket.html#rsocket-annot-rsocketexchange) 참조.
* messaging handler method에 대해서도 interface parameter annotations 이 감지됨 (web handler method와 유사).
* WebSocket messaging의 The SpEL 기반 `selector` header 지원은 이제 default로 비활성화되어 있으며 명시적으로 사용하도록 설정해야 함. migration에 대한 자세한 내용은 [30550](https://github.com/spring-projects/spring-framework/issues/30550) 및 [Upgrading to 6.1](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x#messaging-applications) 참조.
* JMS에 대한 Observability 지원. 이제 `JmsTemplate` 으로 publish 할 때와 `MessageListener` 또는 `@JmsListener` 으로 message를 publish 할 때 observation을 생성함. [참조 문서 section](ttps://docs.spring.io/spring-framework/reference/6.1/integration/observability.html#observability.jms) 과 issue [30335](https://github.com/spring-projects/spring-framework/issues/30335) 참조.

### Testing

* `ApplicationContext` failure threshold 지원: 기본 값은 1 이지만 system property를 통해 구성할 수 있는 failure threshold를 기반으로 TestContext frameworke에서 실패한 `ApplicationContext`  를 로드하려는 시도가 반복되지 않도록 방지함 ([관련 문서](https://docs.spring.io/spring-framework/reference/6.1/testing/testcontext-framework/ctx-management/failure-threshold.html) 참조).
* `@RecordApplicationEvents` 로 asynchronous event 기록 지원. [30020](https://github.com/spring-projects/spring-framework/pull/30020) 참조.
  * main test thread 이외의 thread 에서 event를 기록함.
  * 별도의 thread에서 event를 assert 함 – 예: awaitility 사용.
* MockMvc 는 init parameter를 사용하여 filter를 초기화하고 특정 dispatch type에 mapping 할 수 있도록 지원함.
* `MockMvcWebTestClient` 는 이제 test 간에 다양한 사용자 ID를 허용할 수 있는 `RequestPostProcessor` hook 을 지원함. issue [31298](https://github.com/spring-projects/spring-framework/issues/31298) 참조.
* `MockRestServiceServer` 는 `RestTemplate` 외에 새로운 `RestClient` 를 지원함.
* `MockHttpServletResponse.setCharacterEncoding()` 에서 `null` 을 지원함. [30341](https://github.com/spring-projects/spring-framework/issues/30341) 참조.


## What's New in Version 6.0

### JDK 17+ and Jakarta EE 9+ Baseline

* Entire framework codebase based on Java 17 source code level now.
* Migration from `javax` to `jakarta` namespace for Servlet, JPA, etc.
* Runtime compatibility with Jakarta EE 9 as well as Jakarta EE 10 APIs.
* Compatible with latest web servers: [Tomcat 10.1](https://tomcat.apache.org/whichversion.html), [Jetty 11](https://www.eclipse.org/jetty/download.php), [Undertow 2.3](https://github.com/undertow-io/undertow).
* Early compatibility with [virtual threads](https://spring.io/blog/2022/10/11/embracing-virtual-threads) (in preview as of JDK 19).

### General Core Revision

* Upgrade to ASM 9.4 and Kotlin 1.7.
* Complete CGLIB fork with support for capturing CGLIB-generated classes.
* Comprehensive foundation for [Ahead-Of-Time transformations](https://spring.io/blog/2022/03/22/initial-aot-support-in-spring-framework-6-0-0-m3).
* First-class support for [GraalVM](https://www.graalvm.org/) native images (see [related Spring Boot 3 blog post](https://spring.io/blog/2022/09/26/native-support-in-spring-boot-3-0-0-m5)).

### Core Container

* Basic bean property determination without `java.beans.Introspector` by default.
* AOT processing support in `GenericApplicationContext` (`refreshForAotProcessing`).
* Bean definition transformation based on pre-resolved constructors and factory methods.
* Support for early proxy class determination for AOP proxies and configuration classes.
* `PathMatchingResourcePatternResolver` uses NIO and module path APIs for scanning, enabling support for classpath scanning within a GraalVM native image and within the Java module path, respectively.
* `DefaultFormattingConversionService` supports ISO-based default `java.time` type parsing.

### Data Access and Transactions

* Support for predetermining JPA managed types (for inclusion in AOT processing).
* JPA support for [Hibernate ORM 6.1](https://hibernate.org/orm/releases/6.1/) (retaining compatibility with Hibernate ORM 5.6).
* Upgrade to [R2DBC 1.0](https://r2dbc.io/) (including R2DBC transaction definitions).
* Aligned data access exception translation between JDBC, R2DBC, JPA and Hibernate.
* Removal of JCA CCI support.

### Spring Messaging

* [RSocket interface client](https://docs.spring.io/spring-framework/docs/6.0.0-RC1/reference/html/web-reactive.html#rsocket-interface) based on `@RSocketExchange` service interfaces.
* Early support for Reactor Netty 2 based on [Netty 5](https://netty.io/wiki/new-and-noteworthy-in-5.0.html) alpha.
* Support for Jakarta WebSocket 2.1 and its standard WebSocket protocol upgrade mechanism.

### General Web Revision

* [HTTP interface client](https://docs.spring.io/spring-framework/docs/6.0.0-RC1/reference/html/integration.html#rest-http-interface) based on `@HttpExchange` service interfaces.
* Support for [RFC 7807 problem details](https://docs.spring.io/spring-framework/docs/6.0.0-RC1/reference/html/web.html#mvc-ann-rest-exceptions).
* Unified HTTP status code handling.
* Support for Jackson 2.14.
* Alignment with Servlet 6.0 (while retaining runtime compatibility with Servlet 5.0).

### Spring MVC

* `PathPatternParser` used by default (with the ability to opt into `PathMatcher`).
* Removal of outdated Tiles and FreeMarker JSP support.
 
### Spring WebFlux

* New `PartEvent` API to stream multipart form uploads (both on [client](https://docs.spring.io/spring-framework/docs/6.0.0-RC1/reference/html/web-reactive.html#partevent-2) and [server](https://docs.spring.io/spring-framework/docs/6.0.0-RC1/reference/html/web-reactive.html#partevent)).
* New `ResponseEntityExceptionHandler` to customize WebFlux exceptions and render RFC 7807 [error responses](https://docs.spring.io/spring-framework/docs/6.0.0-RC1/reference/html/web-reactive.html#webflux-ann-rest-exceptions).
* `Flux` return values for non-streaming media types (no longer collected to `List` before written).
* Early support for Reactor Netty 2 based on [Netty 5](https://netty.io/wiki/new-and-noteworthy-in-5.0.html) alpha.
* JDK `HttpClient` integrated with `WebClient`.

### Observability

Direct Observability instrumentation with [Micrometer Observation](https://micrometer.io/docs/observation) in several parts of the Spring Framework. The `spring-web` module now requires `io.micrometer:micrometer-observation:1.10+` as a compile dependency.

* `RestTemplate` and `WebClient` are instrumented to produce HTTP client request observations.
* Spring MVC can be instrumented for HTTP server observations using the new `org.springframework.web.filter.ServerHttpObservationFilter`.
* Spring WebFlux can be instrumented for HTTP server observations using the new `org.springframework.web.filter.reactive.ServerHttpObservationFilter`.
* Integration with Micrometer [Context Propagation](https://github.com/micrometer-metrics/context-propagation#context-propagation-library) for `Flux` and `Mono` return values from controller methods. 

### Testing

* Support for testing AOT-processed application contexts on the JVM or within a GraalVM native image.
* Integration with HtmlUnit 2.64+ request parameter handling.
* Servlet mocks (`MockHttpServletRequest`, `MockHttpSession`) are based on Servlet API 6.0 now.
* New `MockHttpServletRequestBuilder.setRemoteAddress()` method.
* The four abstract base test classes for JUnit 4 and TestNG no longer declare listeners via `@TestExecutionListeners` and
instead now rely on registration of default listeners.
