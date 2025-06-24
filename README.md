# Reservation Transaction PoC
분산 시스템의 트랜잭션 분리 및 데이터 정합성 보장 아키텍처 PoC.
## 🚀 프로젝트 목표
#### **🎯 Goal 1: 트랜잭션 분리 및 신뢰성 있는 이벤트 발행**
핵심 로직과 부가 로직의 트랜잭션을 분리하여 시스템 안정성 확보.
* **탐색 완료:** `@Transactional` 전파 옵션, `@TransactionalEventListener` 등 대안 탐색 및 한계점(스레드 블로킹, 이벤트 유실) 식별 완료.
* **핵심 패턴:** 트랜잭셔널 아웃박스 패턴(TOP) 도입. 메인 DB 작업과 이벤트 발행의 원자성 보장.
* **구현:**
* **1단계:** Polling Publisher MVP 구축.
* **2단계:** Log-based CDC(Debezium) 도입 검토.
#### **🎯 Goal 2: EDA 확장성 및 데이터 파이프라인 고도화**
다양한 시스템에서 이벤트를 안정적으로 소비할 수 있는 확장 가능한 아키텍처 구축.
* **실시간 처리:** Polling에서 Log-based CDC(Debezium)로 전환. DB 부하 최소화 및 지연 시간 단축.
* **순서 보장:** Kinesis/Kafka 도입. Partition Key 기반 이벤트 순서 보장 및 데이터 무결성 확보.
* **느슨한 결합:** 도메인 이벤트를 통한 서비스 간 결합도 최소화 및 유연한 서비스 확장 구조 지향.
-----
## ⚙ 로컬 개발 환경 실행
Docker Compose를 통해 개발 인프라 관리.
```sh
# 모든 서비스 실행 (백그라운드)
docker compose up -d
```
-----
## 📚 참고 자료
프로젝트 아키텍처 설계 시 참고한 국내외 기술 자료.
#### **해외 사례**
* **[How Disney+ scales globally on Amazon DynamoDB](https://www.google.com/search?q=https://aws.amazon.com/blogs/database/how-disney-scales-globally-on-amazon-dynamodb/)**
> DynamoDB 글로벌 테이블을 활용한 대규모 확장성 및 저지연 아키텍처.
* **[Amazon DynamoDB Customers - Zoom](https://aws.amazon.com/dynamodb/customers/)**
> DynamoDB 온디맨드 모드를 통한 트래픽 급증 대응 사례.
* **[How Airbnb simplified its data infrastructure by moving to Amazon DynamoDB](https://www.google.com/search?q=https://www.youtube.com/watch%3Fv%3Djw_h_h5Lp6A)**
> DynamoDB 도입을 통한 데이터 인프라 단순화 및 생산성 향상 경험.
#### **국내 사례**
* **[배달의민족 최전방 시스템\! '가게노출 시스템'을 소개합니다.](https://techblog.woowahan.com/2667/)**
> DynamoDB의 주 저장소/캐시 활용 및 실제 운영 경험.
* **[[배민스토어] 배민스토어에 이벤트 기반 아키텍처를 곁들인…](https://techblog.woowahan.com/13101/)**
> Kafka, DynamoDB, Redis 조합의 이벤트 기반 아키텍처 사례.
* **[당근마켓, AWS 기반 온라인 플랫폼을 통한 지역 커뮤니티 서비스 사례](https://aws.amazon.com/ko/blogs/korea/danggeun-market-online-platform-community-building/)**
> AWS 관리형 서비스를 활용한 대규모 서비스 구축 및 운영 철학.
* **[삼성 클라우드의 Amazon DynamoDB 비용 최적화 여정](https://aws.amazon.com/ko/blogs/tech/sec-cloud-amazon-dynamodb-cost-optimization-journey/)**
> DynamoDB TTL 기능을 활용한 대용량 임시 데이터 처리 및 비용 최적화.