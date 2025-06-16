# Reservation Transaction PoC - 로컬 개발환경 빠른 시작 가이드

---

## 🖥️ 1. 개요

* 이 문서는 **Reservation Transaction PoC** 프로젝트의 로컬 개발환경(`MySQL`)을 구성하기 위한 실전 자동화 가이드입니다.
* Docker와 docker-compose만 있으면, **명령어 한 줄로 DB 환경 세팅**이 완료됩니다.

---

## ⚙️ 2. 환경 구조/서비스 요약

| 서비스 | 이미지 | 호스트 포트 | 컨테이너 포트 | 계정/비번/DB | 비고 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.0` | `13306` | `3306` | `root` / `password` / `transaction_db` | `utf8mb4`, KST |

---

## 🏗️ 3. 프로젝트 구조

```
/reservation-transaction-poc
 ├── docker-compose.yml
 └── ... (나머지 Spring Boot 프로젝트 파일)
```

* **docker-compose.yml**:
  `MySQL` 데이터베이스 컨테이너의 모든 설정(버전, 포트, 계정, 초기 DB 등)을 정의하고 실행합니다.

---

## 🚀 4. 사용법 (완전 자동화)

1.  **DB 컨테이너 실행**

    ```sh
    docker-compose up -d
    ```

    * 위 명령어 한 줄이면 개발에 필요한 MySQL DB가 자동으로 준비됩니다.

2.  **DB 컨테이너 중지**

    ```sh
    docker-compose down
    ```
    * `-v` 옵션을 함께 사용하면 데이터 볼륨까지 삭제합니다: `docker-compose down -v`

---

## 🧑‍💻 5. 상태 확인 및 접속

* **실행중인 컨테이너 목록 확인:**
    ```sh
    docker ps
    ```

* **MySQL 컨테이너 로그 확인:**
    ```sh
    docker logs transaction-db-mysql
    ```

* **MySQL 컨테이너 내부 접속:**
    ```sh
    docker exec -it transaction-db-mysql bash
    ```

* **로컬에서 MySQL 클라이언트로 직접 접속:**
    ```sh
    mysql -h 127.0.0.1 -P 13306 -u root -p
    # 암호 입력: password
    ```

---

## ⚡ 6. 문제해결 체크리스트

* **포트 충돌 에러 발생 시:**
  `docker ps` 명령어로 `13306` 포트를 이미 사용중인 다른 컨테이너가 있는지 확인하고, 필요시 해당 컨테이너를 중지 (`docker stop [컨테이너명]`) 또는 `docker-compose.yml`의 포트를 변경하세요.

* **DB 연결이 안 될 경우:**
  `application.yml`의 `datasource` 정보(url, username, password)가 위 `2. 환경 구조` 표의 내용과 일치하는지 다시 한번 확인하세요.

* **MySQL 컨테이너가 정상인지 확인:**
  위 `5. 상태 확인 및 접속`의 로그 확인 및 직접 접속 명령어를 통해 DB 서버의 상태를 직접 체크하세요.

---

## 📝 7. 환경 업데이트

* DB 버전 변경, 포트 수정, 비밀번호 변경 등 환경에 대한 모든 수정은 **`docker-compose.yml` 파일 하나만 관리**하면 됩니다.

---

### ✅ 이 프로젝트의 개발 환경 준비는 아래 명령어가 전부입니다.

```sh
# 개발 환경 실행 (매번)
docker-compose up -d
```