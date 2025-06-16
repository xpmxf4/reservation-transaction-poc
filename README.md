# Reservation Transaction PoC - 로컬 통합 개발환경 가이드
## 🖥 1. 개요
* 이 문서는 **Reservation Transaction PoC** 프로젝트의 로컬 개발환경(`MySQL`, `LocalStack`)을 Docker 기반으로 구축하기 위한 실전 통합 가이드입니다.
* Docker와 `docker-compose`, 그리고 초기화 스크립트만 있으면, **명령어 몇 줄로 Spring Boot 개발에 필요한 모든 인프라(DB, Message Queue)가 자동으로 구성**됩니다.
## 🏗 2. 프로젝트 아키텍처 및 목표
본 PoC(기술 검증) 프로젝트는 단순한 예약 기능 구현을 넘어, MSA(마이크로서비스 아키텍처) 환경에서 발생할 수 있는 트랜잭션과 이벤트 처리 문제를 해결하는 것을 목표로 합니다.
* **SNS Fan-out 패턴 도입**: 예약 이벤트가 발생하면 `SNS` 토픽으로 메시지를 발행합니다. 이 토픽을 여러 `SQS` 큐가 구독하여, **알림 전송**, **통계 집계** 등 다양한 후속 작업을 비동기적으로 독립 처리합니다. 이를 통해 서비스 간 결합도를 낮추고 확장성을 확보합니다.
* **다양한 트랜잭션 처리 전략 실험**: `Transactional Event Listener`, `Outbox Pattern` 등 분산 환경에서의 데이터 정합성을 보장하기 위한 여러 전략을 코드로 구현하고 테스트합니다.
## ⚙ 3. 환경 명세 (포트, 버전, 계정)
| 서비스 | 이미지 | 호스트 포트 | 컨테이너 포트 | 계정/비번/DB | 비고 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.0` | `13306` | `3306` | `root` / `password` / `transaction_db` | `utf8mb4`, KST |
| **LocalStack** | `localstack/localstack:latest`| `14566` | `4566` | `test` / `test` | SNS, SQS 서비스 활성화 |
## 📁 4. 프로젝트 구조
```
/reservation-transaction-poc
├── docker-compose.yml # 모든 서비스(DB, LocalStack) 정의
├── init-aws.sh # AWS 리소스(SNS, SQS) 자동 생성 스크립트
└── ... (나머지 Spring Boot 프로젝트 파일)
```
* **`docker-compose.yml`**: `MySQL`과 `LocalStack` 컨테이너의 모든 설정(버전, 포트, 계정 등)을 정의하고 실행합니다.
* **`init-aws.sh`**: SNS 토픽, SQS 큐 생성 및 두 리소스 간의 구독(Fan-out) 설정을 자동으로 처리합니다.
-----
## 🚀 5. 로컬 환경 자동 구성 및 실행
### 5-1. 구성 파일
**`docker-compose.yml`**
```yaml
services:
  db:
    image: mysql:8.0
    container_name: poc-mysql
    restart: always
    ports:
      - "13306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: transaction_db
    volumes:
      - db-data:/var/lib/mysql
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci

  localstack:
    image: localstack/localstack:latest
    container_name: poc-localstack
    restart: always
    ports:
      - "14566:4566"
    environment:
      - SERVICES=sns,sqs
      - DEFAULT_REGION=ap-northeast-2
      - HOSTNAME_EXTERNAL=localhost
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock"

volumes:
  db-data:

```
**`init-aws.sh`**
```bash
#!/bin/bash

echo "================================================="
echo " LocalStack AWS 리소스 초기화를 시작합니다... "
echo "================================================="

# AWS CLI 설정
aws configure set aws_access_key_id test
aws configure set aws_secret_key test
aws configure set region ap-northeast-2
aws configure set output json

ENDPOINT_URL=http://localhost:14566
SNS_TOPIC_NAME="reservation-created-topic"

# SNS 토픽 생성
echo -e "\n[1/3] SNS 토픽 ($SNS_TOPIC_NAME) 생성을 시도합니다..."
SNS_TOPIC_ARN=$(aws --endpoint-url=$ENDPOINT_URL sns create-topic --name $SNS_TOPIC_NAME --query 'TopicArn' --output text)
echo "-> SNS 토픽 생성 완료: $SNS_TOPIC_ARN"

# SQS 큐 생성
SQS_QUEUE_NAME_NOTI="reservation-noti-queue"
SQS_QUEUE_NAME_STAT="reservation-stat-queue"

echo -e "\n[2/3] SQS 큐 ($SQS_QUEUE_NAME_NOTI, $SQS_QUEUE_NAME_STAT) 생성을 시도합니다..."

# 알림 큐
SQS_QUEUE_URL_NOTI=$(aws --endpoint-url=$ENDPOINT_URL sqs create-queue --queue-name $SQS_QUEUE_NAME_NOTI --query 'QueueUrl' --output text)
SQS_QUEUE_ARN_NOTI=$(aws --endpoint-url=$ENDPOINT_URL sqs get-queue-attributes --queue-url $SQS_QUEUE_URL_NOTI --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)
echo "-> SQS 큐 (알림) 생성 완료: $SQS_QUEUE_URL_NOTI"

# 통계 큐
SQS_QUEUE_URL_STAT=$(aws --endpoint-url=$ENDPOINT_URL sqs create-queue --queue-name $SQS_QUEUE_NAME_STAT --query 'QueueUrl' --output text)
SQS_QUEUE_ARN_STAT=$(aws --endpoint-url=$ENDPOINT_URL sqs get-queue-attributes --queue-url $SQS_QUEUE_URL_STAT --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)
echo "-> SQS 큐 (통계) 생성 완료: $SQS_QUEUE_URL_STAT"

# SNS -> SQS 구독 연결
echo -e "\n[3/3] SNS 토픽에 SQS 큐 구독을 설정합니다..."

aws --endpoint-url=$ENDPOINT_URL sns subscribe \
  --topic-arn $SNS_TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $SQS_QUEUE_ARN_NOTI
echo "-> 구독 완료: ($SNS_TOPIC_NAME) -> ($SQS_QUEUE_NAME_NOTI)"

aws --endpoint-url=$ENDPOINT_URL sns subscribe \
  --topic-arn $SNS_TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $SQS_QUEUE_ARN_STAT
echo "-> 구독 완료: ($SNS_TOPIC_NAME) -> ($SQS_QUEUE_NAME_STAT)"

echo -e "\n================================================="
echo " 모든 AWS 리소스 설정이 성공적으로 완료되었습니다. "
echo "================================================="

```
### 5-2. 실행 순서
1. **Docker 컨테이너 실행**
```sh
docker compose up -d
```
2. **초기화 스크립트에 실행 권한 부여** (최초 1회만)
```sh
chmod +x init-aws.sh
```
3. **LocalStack 리소스 생성 스크립트 실행**
```sh
./init-aws.sh
```
> 위 3단계를 거치면 개발에 필요한 모든 인프라가 준비됩니다.
4. **컨테이너 중지**
```sh
docker compose down
```
> `-v` 옵션을 함께 사용하면 데이터 볼륨까지 완전히 삭제합니다: `docker-compose down -v`
-----
## 🧑💻 6. 상태 확인 및 접속
* **실행중인 컨테이너 목록 확인:**
  `docker ps`
* **컨테이너 로그 확인:**
  `docker logs poc-mysql`
  `docker logs poc-localstack`
* **MySQL 컨테이너 내부 접속:**
  `docker exec -it poc-mysql bash`
* **로컬에서 MySQL 클라이언트로 직접 접속:**
  `mysql -h 127.0.0.1 -P 13306 -u root -p` (비밀번호: `password`)
* **LocalStack 상태 확인 (Health Check):**
  `curl http://localhost:14566/health`
-----
## ⚡ 7. 문제해결 체크리스트
* **포트 충돌 에러 발생 시:**
  `docker ps` 명령어로 `13306` 또는 `14566` 포트를 이미 사용중인 다른 컨테이너가 있는지 확인하고, 필요시 해당 컨테이너를 중지 (`docker stop [컨테이너명]`) 하거나 `docker-compose.yml`의 포트를 변경하세요.
* **DB 연결이 안 될 경우:**
  `application.yml`의 `datasource` 정보(url, username, password)가 위 `3. 환경 명세` 표의 내용과 일치하는지 다시 한번 확인하세요.
* **AWS 리소스가 생성되지 않을 경우:**
  `init-aws.sh` 스크립트가 정상적으로 실행되었는지, `docker logs poc-localstack` 로그에 에러는 없는지 확인하세요.
-----
## 🤔 8. 추후 해볼 것들
* **`@Testcontainers` 도입 검토**
* **이유**: 현재는 테스트 코드를 실행하기 위해 `docker-compose up` 명령어로 DB와 LocalStack 컨테이너를 미리 실행해야 하는 번거로움이 있습니다.
* **개선 아이디어**: `@Testcontainers` 라이브러리를 도입하면, 테스트 라이프사이클에 맞춰 자동으로 DB/LocalStack 컨테이너를 실행하고 테스트 종료 후 제거해주는 환경을 구축할 수 있습니다. 이를 통해 각 테스트가 격리된 환경을 사용하게 되어 테스트의 독립성과 안정성을 크게 향상시킬 수 있습니다.