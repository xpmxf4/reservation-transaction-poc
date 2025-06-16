#!/bin/bash

# =================================================================
# LocalStack AWS 리소스 초기화 스크립트 (최종 수정본)
# =================================================================
#
# 이 스크립트는 호스트 머신에서 실행되며, 'docker exec'를 통해
# LocalStack 컨테이너 내부의 AWS CLI를 사용하여 리소스를 생성합니다.
#
# 주요 수정 사항:
# 1. 'docker exec' 실행 시 '-e' 옵션을 통해 더미(dummy) AWS 자격 증명을
#    환경 변수로 주입하여 'Unable to locate credentials' 오류 해결.
#

set -e # 스크립트 실행 중 오류가 발생하면 즉시 중단

# --- 설정 변수 ---
REGION="ap-northeast-2"
SNS_TOPIC_NAME="reservation-created-topic"
SQS_NOTI_QUEUE_NAME="reservation-noti-queue"
SQS_STAT_QUEUE_NAME="reservation-stat-queue"
LOCALSTACK_CONTAINER_NAME="poc-localstack" # docker-compose.yml에 정의된 컨테이너 이름

# aws cli 명령어를 실행하는 헬퍼 함수
run_in_localstack() {
    # 컨테이너 내부에서 실행될 때, AWS 자격증명 에러를 피하기 위해 더미 값을 제공합니다.
    # LocalStack은 이 값들을 검증하지 않습니다.
    docker exec \
      -e AWS_ACCESS_KEY_ID=test \
      -e AWS_SECRET_ACCESS_KEY=test \
      -e AWS_DEFAULT_REGION=$REGION \
      "$LOCALSTACK_CONTAINER_NAME" \
      aws --endpoint-url=http://localhost:4566 "$@"
}


echo "================================================="
echo " LocalStack 리소스 초기화를 시작합니다..."
echo "================================================="
echo ""

# 1. SNS 토픽 생성
echo "[1/3] SNS 토픽 ($SNS_TOPIC_NAME) 생성을 시도합니다..."
SNS_TOPIC_ARN=$(run_in_localstack sns create-topic --name $SNS_TOPIC_NAME --output text --query 'TopicArn')
if [ -z "$SNS_TOPIC_ARN" ]; then
    echo "-> [오류] SNS 토픽 생성에 실패했습니다."
    exit 1
fi
echo "-> SNS 토픽 생성 완료: $SNS_TOPIC_ARN"
echo ""

# 2. SQS 큐 생성
echo "[2/3] SQS 큐 ($SQS_NOTI_QUEUE_NAME, $SQS_STAT_QUEUE_NAME) 생성을 시도합니다..."

# 알림 큐 생성
SQS_NOTI_QUEUE_URL=$(run_in_localstack sqs create-queue --queue-name $SQS_NOTI_QUEUE_NAME --output text --query 'QueueUrl')
if [ -z "$SQS_NOTI_QUEUE_URL" ]; then
    echo "-> [오류] SQS 큐 (알림) 생성에 실패했습니다."
    exit 1
fi
echo "-> SQS 큐 (알림) 생성 완료: $SQS_NOTI_QUEUE_URL"

# 통계 큐 생성
SQS_STAT_QUEUE_URL=$(run_in_localstack sqs create-queue --queue-name $SQS_STAT_QUEUE_NAME --output text --query 'QueueUrl')
if [ -z "$SQS_STAT_QUEUE_URL" ]; then
    echo "-> [오류] SQS 큐 (통계) 생성에 실패했습니다."
    exit 1
fi
echo "-> SQS 큐 (통계) 생성 완료: $SQS_STAT_QUEUE_URL"
echo ""

# 3. SNS 토픽에 SQS 큐 구독 설정
echo "[3/3] SNS 토픽에 SQS 큐 구독을 설정합니다..."

# 알림 큐 구독
NOTI_QUEUE_ARN=$(run_in_localstack sqs get-queue-attributes --queue-url $SQS_NOTI_QUEUE_URL --attribute-names QueueArn --output text --query 'Attributes.QueueArn')
run_in_localstack sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $NOTI_QUEUE_ARN > /dev/null
echo "-> 구독 완료: ($SNS_TOPIC_NAME) -> ($SQS_NOTI_QUEUE_NAME)"

# 통계 큐 구독
STAT_QUEUE_ARN=$(run_in_localstack sqs get-queue-attributes --queue-url $SQS_STAT_QUEUE_URL --attribute-names QueueArn --output text --query 'Attributes.QueueArn')
run_in_localstack sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $STAT_QUEUE_ARN > /dev/null
echo "-> 구독 완료: ($SNS_TOPIC_NAME) -> ($SQS_STAT_QUEUE_NAME)"
echo ""

echo "================================================="
echo " ✅ 모든 AWS 리소스 설정이 성공적으로 완료되었습니다."
echo "================================================="