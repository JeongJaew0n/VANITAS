# 헥사고날 패키지 구조 결정: layer-first vs subdomain-first

> **Status**: 채택 — subdomain-first (Option 2)
> **Scope**: `example` 모듈 내부 패키지 구조. 추후 모듈 분리 시에도 동일 원칙 적용.

## 배경

VANITAS는 헥사고날 아키텍처 학습/실험을 위해 Gradle 멀티 모듈로 시작했고, 현재는 `example` 모듈 안에서만 구현이 진행된다. 헥사고날 3계층(`adapter` / `application` / `domain`)과 서브도메인(`user`, `order`, `payment`)을 어떤 축으로 나눌지를 결정해야 했다.

## 비교 대상

### Option 1 — Layer-first (계층 우선)

```
example/
├── adapter/
│   ├── user/
│   ├── order/
│   └── payment/
├── application/
│   ├── user/
│   ├── order/
│   └── payment/
└── domain/
    ├── user/
    ├── order/
    └── payment/
```

루트에서 헥사고날 3계층이 1차 축, 서브도메인은 각 계층 내부의 2차 축.

### Option 2 — Subdomain-first (서브도메인 우선) ★ 채택

```
example/
├── user/
│   ├── adapter/
│   ├── application/
│   └── domain/
├── order/
│   ├── adapter/
│   ├── application/
│   └── domain/
├── payment/
│   ├── adapter/
│   ├── application/
│   └── domain/
└── shared/             # 서브도메인 공통 ID/VO/이벤트 (선택)
```

루트에서 서브도메인이 1차 축, 헥사고날 3계층은 각 서브도메인 내부의 2차 축.

## 비교

| 관점 | Layer-first | Subdomain-first |
| --- | --- | --- |
| 루트에서 보이는 것 | "헥사고날 시스템이다" | "유저·주문·결제 시스템이다" |
| DDD bounded context 우위 | 어긋남 (layer가 1차) | 일치 (context가 1차) |
| Screaming Architecture | 아키텍처 패턴이 비명 | 비즈니스가 비명 |
| 서브도메인 간 결합 누수 위험 | 높음 (같은 폴더 안이라 import 쉬움) | 낮음 (패키지 경계가 시각적 장벽) |
| ArchUnit/Konsist 강제 비용 | 높음 | 낮음 (`order는 user를 참조 못함` 한 줄) |
| 모듈 승격 비용 (서브도메인 → 별도 모듈) | 세 곳에서 동시 들어내고 재배선 | 폴더 하나 통째로 이동 |
| 소유권/CODEOWNERS 분할 | 세 줄 필요 | 한 줄로 끝 |
| 한 기능 작업 시 인지 부하 | 항상 세 폴더 왕복 | 한 폴더에서 완결 |
| 한 layer 횡단 변경 (예: 모든 controller에 헤더 추가) | 한 폴더에서 완결 | 서브도메인 수만큼 순회 |

## 철학적 근거 (왜 Subdomain-first인가)

- **헥사고날 자체는 침묵한다.** 헥사고날은 "내부 vs 외부, 의존은 안쪽으로만"만 규정한다. 안쪽을 layer로 자를지 subdomain으로 자를지엔 의견이 없으므로, 헥사고날만으로는 두 구조 다 합법.
- **DDD가 1차 축을 정해준다.** Eric Evans, Vaughn Vernon은 "bounded context가 1차 분할 축, layer는 2차"라고 명시한다. Layer-first는 이 우선순위를 뒤집는다.
- **Screaming Architecture (Uncle Bob)**: 루트를 열었을 때 "이 시스템이 무엇을 하는지"가 먼저 보여야 한다. Subdomain-first가 이 원칙을 직접 만족.
- **Tom Hombergs, *Get Your Hands Dirty on Clean Architecture***: 헥사고날 실무서 중 가장 자주 인용되는 책에서 layer-first를 "package by layer" 안티패턴이라고 명시적으로 비판하고 subdomain-first를 권장한다.

## 실무적 근거

- **결합 누수 방지**: Layer-first에선 `application/order`가 `application/user.UserService`를 import하는 게 IDE 자동완성 한 번으로 끝난다. Subdomain-first에선 패키지 경계를 넘는 게 시각적·심리적 장벽이 되고, ArchUnit 룰로도 강제하기 쉽다.
- **모듈 승격이 무통**: VANITAS의 명시적 의도가 "user 같은 큰 도메인은 찢는다"이다. Subdomain-first는 이 동작을 폴더 이동 한 번으로 끝낸다. Layer-first는 세 폴더에서 동시에 들어내고 import 경로를 재배선해야 한다.
- **팀/소유권 분할**: 나중에 user 팀과 order 팀이 분리되면 Subdomain-first는 CODEOWNERS 한 줄로 끝난다.
- **인지 부하**: 한 기능 작업할 때 한 폴더 안에서 완결되는 쪽이 작업 효율이 높다.

## Layer-first가 정당한 경우

- bounded context가 영원히 1개임이 확실한 단순한 마이크로서비스
- 헥사고날을 가르치기 위한 데모/튜토리얼 (구조 자체가 교재)

VANITAS는 둘 다 해당하지 않으므로 채택하지 않는다.

## 채택한 구조의 운영 규칙

1. **서브도메인 간 호출은 port를 통해서만.** `order/application`이 `user/application`의 service를 직접 import 금지. 필요하면 `order/application/port/out/UserLookupPort`를 정의하고, `order/adapter/out/`에서 `user`의 inport를 호출하는 어댑터를 둔다. 또는 도메인 이벤트로 비동기 통신.
2. **`shared/`는 최소화.** 공통 ID, VO, 도메인 이벤트만 허용. 공용 service나 usecase는 두지 않는다. `shared/`가 비대해지는 건 bounded context를 잘못 나눴다는 신호.
3. **각 서브도메인 내부의 `adapter/`는 in/out으로 한 번 더 분리.** `<sub>/adapter/in/web/`(controller), `<sub>/adapter/out/persistence/`(JPA repo), `<sub>/adapter/out/client/`(외부 API).
4. **`<sub>/domain/`엔 순수 객체만.** Spring/JPA 어노테이션 금지. JPA 엔티티는 `<sub>/adapter/out/persistence/`에 따로 두고 매핑한다.
5. **승격 시그널**: 한 서브도메인이 자기만의 service 5개·outport 3개를 갖고, 다른 서브도메인이 자기 내부 객체를 직접 참조하기 시작하면 별도 Gradle 모듈(`user-domain`, `user-application`, `user-adapter`)로 승격한다. 이 시점이 `buildSrc`에 `domain.gradle.kts`(Spring 의존성 없는 순수 Kotlin 컨벤션) 같은 모듈 타입별 컨벤션 플러그인을 추가할 타이밍.

## 참고

- Tom Hombergs, *Get Your Hands Dirty on Clean Architecture* (2nd ed.)
- Vaughn Vernon, *Implementing Domain-Driven Design*
- Robert C. Martin, "Screaming Architecture" (Clean Architecture 블로그/책)
- Alistair Cockburn, "Hexagonal Architecture" 원문
