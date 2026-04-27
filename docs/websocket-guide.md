# WebSocket 추가 및 테스트 가이드

`example` 모듈(Spring Boot 3.5.4 / Kotlin 2.0.21 / JVM 21)에 WebSocket을 도입하고 로컬에서 테스트하는 절차입니다. 기존 멀티 모듈 구조(`buildSrc` 공통 컨벤션 + `gradle/libs.versions.toml` 카탈로그)를 그대로 따릅니다.

## 0. 사전 점검
- 현재 `example`에는 REST 컨트롤러 3종(`EchoController`, `ChaosController`, `HealthController`)만 존재. WebSocket 관련 의존성/설정은 아직 없음.
- 진입점: `example/src/main/kotlin/com/nhn/inje/ccp/Main.kt`
- WebSocket은 `spring-boot-starter-websocket`로 추가하고, 메시지 브로커 추상화(STOMP)를 쓸지 raw WebSocket(`TextWebSocketHandler`)을 쓸지 먼저 결정. 본 문서는 **두 방식 모두 다루되, 단순 echo 검증은 raw WebSocket으로 시작**합니다.

## 1. 의존성 카탈로그 등록
`gradle/libs.versions.toml`의 `[libraries]` 섹션에 starter 추가.

```toml
spring-boot-starter-websocket = { module = "org.springframework.boot:spring-boot-starter-websocket" }
```

버전은 루트의 `spring-boot` BOM이 관리하므로 별도 version 지정 불필요.

## 2. 모듈 의존성 추가
`example/build.gradle.kts`의 `dependencies` 블록.

```kotlin
implementation(libs.spring.boot.starter.websocket)
```

> 카탈로그의 하이픈(`-`)은 Kotlin DSL에서 dot(`.`)으로 접근됩니다.

## 3. WebSocket 핸들러 작성 (raw 방식)
`example/src/main/kotlin/com/nhn/inje/ccp/ws/EchoWebSocketHandler.kt`

```kotlin
package com.ws

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class EchoWebSocketHandler : TextWebSocketHandler() {
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        session.sendMessage(TextMessage("echo: ${message.payload}"))
    }
}
```

## 4. WebSocket 설정 등록
`example/src/main/kotlin/com/nhn/inje/ccp/ws/WebSocketConfig.kt`

```kotlin
package com.ws

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val echoHandler: EchoWebSocketHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(echoHandler, "/ws/echo")
            .setAllowedOriginPatterns("*") // 로컬 검증용. 운영에서는 특정 origin만 허용.
    }
}
```

## 5. 빌드·기동
```bash
./gradlew :example:build           # 컴파일·테스트
./gradlew :example:bootRun         # 로컬 기동 (기본 8080)
```

## 6. 수동 테스트
세 가지 중 편한 방법 선택.

### 6-1. websocat (CLI)
```bash
brew install websocat
websocat ws://localhost:8080/ws/echo
> hello
< echo: hello
```

### 6-2. 브라우저 콘솔
```js
const ws = new WebSocket("ws://localhost:8080/ws/echo");
ws.onmessage = (e) => console.log("recv:", e.data);
ws.onopen = () => ws.send("hello");
```

### 6-3. IntelliJ HTTP Client (`example/src/test/resources/ws.http` 등)
```
WEBSOCKET ws://localhost:8080/ws/echo

hello
```

## 7. 자동화 테스트
`example/src/test/kotlin/com/nhn/inje/ccp/ws/EchoWebSocketHandlerTest.kt`

```kotlin
package com.ws

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URI
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EchoWebSocketHandlerTest {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun shouldEchoTextMessage() {
        val received = ArrayBlockingQueue<String>(1)
        val client = StandardWebSocketClient()
        val session: WebSocketSession = client.execute(
            object : TextWebSocketHandler() {
                override fun handleTextMessage(s: WebSocketSession, m: TextMessage) {
                    received.offer(m.payload)
                }
            },
            URI.create("ws://localhost:$port/ws/echo")
        ).get(2, TimeUnit.SECONDS)

        session.sendMessage(TextMessage("ping"))

        assertEquals("echo: ping", received.poll(2, TimeUnit.SECONDS))
        session.close()
    }
}
```

실행:
```bash
./gradlew :example:test --tests "*EchoWebSocketHandlerTest"
```

## 8. (선택) STOMP / SockJS 확장
브라우저 호환성·구독 모델·브로드캐스트가 필요하면 STOMP로 확장.

`WebSocketConfig`를 `WebSocketMessageBrokerConfigurer` 구현으로 교체:

```kotlin
@Configuration
@EnableWebSocketMessageBroker
class StompConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }
}
```

컨트롤러에서는 `@MessageMapping("/chat")` + `@SendTo("/topic/chat")` 패턴 사용. 클라이언트는 `@stomp/stompjs` 또는 SockJS.

## 9. 마무리 체크리스트
- [ ] `./gradlew ktlintCheck` 통과
- [ ] `./gradlew :example:build` 통과
- [ ] `websocat`(또는 브라우저)로 수동 echo 확인
- [ ] `EchoWebSocketHandlerTest` 통과
- [ ] 운영 배포 시 `setAllowedOriginPatterns("*")` 제거하고 실제 origin 화이트리스트 적용

## 10. 트러블슈팅
- **404 on `/ws/echo`**: `@EnableWebSocket` 또는 `WebSocketConfigurer` 빈 등록 누락.
- **CORS 거부**: `setAllowedOriginPatterns(...)` 누락 — 브라우저 테스트 시에만 의미 있음(websocat은 영향 없음).
- **메시지 미수신 / 즉시 close**: 핸들러가 예외를 던졌을 가능성. 서버 로그(`logging.level.org.springframework.web.socket=DEBUG`)로 확인.
- **Kotlin 클래스 open 이슈**: Spring 어노테이션이 붙은 클래스는 `kotlin("plugin.spring")`이 자동 open 처리 — 추가 설정 불필요.
