#!/bin/bash

# docker exec의 -i 옵션과 heredoc(<<)을 사용하여
# 아래 명령어 블록 전체를 컨테이너 내부의 bash 셸로 전달하여 실행합니다.
docker exec -i poc-localstack /bin/bash <<'EOF'

echo "================================================="
echo "  LocalStack 컨테이너 내부에서 리소스 초기화를 시작합니다... "
echo "================================================="

# AWS CLI 설정
aws configure set aws_access_key_id test
aws configure set aws_secret_key test
aws configure set region ap-northeast-2
aws configure set output json

# LocalStack 내부에서 사용하는 Endpoint URL
# 컨테이너 안에서는 localhost 대신 host.docker.internal 또는 localstack을 사용할 수 있지만,
# LocalStack 에서는 localhost로도 통신이 가능합니다.
ENDPOINT_URL=http://localhost:4566

# 1. SNS 토픽 생성
SNS_TOPIC_NAME="reservation-created-topic"
echo "\n[1/3] SNS 토픽 ($SNS_TOPIC_NAME) 생성을 시도합니다..."
SNS_TOPIC_ARN=$(aws --endpoint-url=$ENDPOINT_URL sns create-topic --name $SNS_TOPIC_NAME --query 'TopicArn' --output text)
echo "-> SNS 토픽 생성 완료: $SNS_TOPIC_ARN"

# 2. SQS 큐 생성
SQS_QUEUE_NAME_NOTI="reservation-noti-queue"
SQS_QUEUE_NAME_STAT="reservation-stat-queue"
echo "\n[2/3] SQS 큐 ($SQS_QUEUE_NAME_NOTI, $SQS_QUEUE_NAME_STAT) 생성을 시도합니다..."

SQS_QUEUE_URL_NOTI=$(aws --endpoint-url=$ENDPOINT_URL sqs create-queue --queue-name $SQS_QUEUE_NAME_NOTI --query 'QueueUrl' --output text)
SQS_QUEUE_ARN_NOTI=$(aws --endpoint-url=$ENDPOINT_URL sqs get-queue-attributes --queue-url $SQS_QUEUE_URL_NOTI --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)
echo "-> SQS 큐 (알림) 생성 완료: $SQS_QUEUE_URL_NOTI"

SQS_QUEUE_URL_STAT=$(aws --endpoint-url=$ENDPOINT_URL sqs create-queue --queue-name $SQS_QUEUE_NAME_STAT --query 'QueueUrl' --output text)
SQS_QUEUE_ARN_STAT=$(aws --endpoint-url=$ENDPOINT_URL sqs get-queue-attributes --queue-url $SQS_QUEUE_URL_STAT --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)
echo "-> SQS 큐 (통계) 생성 완료: $SQS_QUEUE_URL_STAT"

# 3. SNS 토픽에 SQS 큐 구독 (Fan-out 연결)
echo "\n[3/3] SNS 토픽에 SQS 큐 구독을 설정합니다..."
aws --endpoint-url=$ENDPOINT_URL sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $SQS_QUEUE_ARN_NOTI
echo "-> 구독 완료: ($SNS_TOPIC_NAME) -> ($SQS_QUEUE_NAME_NOTI)"

aws --endpoint-url=$ENDPOINT_URL sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $SQS_QUEUE_ARN_STAT
echo "-> 구독 완료: ($SNS_TOPIC_NAME) -> ($SQS_QUEUE_NAME_STAT)"

echo "\n================================================="
echo "  모든 AWS 리소스 설정이 성공적으로 완료되었습니다.  "
echo "================================================="

EOF