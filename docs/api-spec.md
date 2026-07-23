# 머니로그(MoneyLog) REST API 명세서

## 1. 공통 응답 규약

머니로그의 모든 API 응답은 아래의 공통 봉투(Envelope) 포맷을 따르도록 함

### 1.1. 성공 응답
```json
{
  "success": true,
  "message": "요청 처리에 성공했습니다.",
  "data": { "..." } //실제 데이터가 없으면 null
}
```

**목록 조회 시 페이징 정보 (meta)**
```json
{
  "success": true,
  "message": "목록을 조회했습니다.",
  "data": { "transactions": [] },
  "meta": {
    "pagination": {
      "page": 0,
      "size": 20,
      "totalItems": 42,
      "totalPages": 3,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

### 1.2. 실패 (에러) 응답
```json
{
  "success": false,
  "code": "TRANSACTION_NOT_FOUND",
  "message": "사용자에게 보여줄 한글 에러 메시지",
  "data": null
}
```

### 1.3. 표준 에러 코드
| HTTP 상태 | code | 설명 |
|:---:|---|---|
| **400** | `VALIDATION_ERROR` | 입력 검증 실패 (금액 ≤ 0, 날짜 누락 등) |
| **401** | `INVALID_CREDENTIALS` | 로그인 시 이메일/비밀번호 불일치 |
| **401** | `UNAUTHORIZED` | 인증 토큰 없음/만료됨 |
| **403** | `FORBIDDEN` | 본인 데이터가 아님 (접근 권한 없음) |
| **404** | `CATEGORY_NOT_FOUND` | 존재하지 않는 카테고리 |
| **404** | `TRANSACTION_NOT_FOUND` | 존재하지 않는 거래내역 |
| **409** | `DUPLICATE_EMAIL` | 이미 가입된 이메일 |

---

## 2. API 명세

*(인증 필요 여부가 ✅인 API는 요청 헤더에 `Authorization: Bearer {accessToken}`을 포함해야 합니다.)*

### 2.1. 인증 (Auth)

| 메서드 | URI 경로 | 설명 | 인증 |
|:---:|---|---|:---:|
| **POST** | `/api/auth/signup` | 회원가입 | ❌ |
| **POST** | `/api/auth/login` | 로그인 (JWT 발급) | ❌ |

**[예시] 로그인 요청 및 응답**
- **Request:**
```json
{
  "email": "hong@moneylog.com",
  "password": "pass1234!"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 2.2. 카테고리 (Category)

| 메서드 | URI 경로 | 설명 | 인증 |
|:---:|---|---|:---:|
| **GET** | `/api/categories` | 내 카테고리 목록 조회 | ✅ |
| **POST** | `/api/categories` | 카테고리 추가 | ✅ |
| **PUT** | `/api/categories/{id}` | 카테고리 수정 | ✅ |
| **DELETE**| `/api/categories/{id}` | 카테고리 삭제 | ✅ |

*(type 필드는 `INCOME` 또는 `EXPENSE`로 구성됩니다.)*

### 2.3. 거래내역 (Transaction)

| 메서드 | URI 경로 | 설명 | 인증 |
|:---:|---|---|:---:|
| **GET** | `/api/transactions` | 거래 목록 조회 (필터 및 페이징) | ✅ |
| **POST** | `/api/transactions` | 거래 등록 | ✅ |
| **GET** | `/api/transactions/{id}` | 거래 상세 조회 | ✅ |
| **PUT** | `/api/transactions/{id}` | 거래 수정 (전체 덮어쓰기) | ✅ |
| **DELETE**| `/api/transactions/{id}` | 거래 삭제 | ✅ |

- **Query Parameters (GET 목록 조회 시):** `?yearMonth=2026-07&type=EXPENSE&categoryId=3&page=0&size=20` (모두 선택적)

**[예시] 거래 등록 요청 및 응답**
- **Request:**
```json
{
  "type": "EXPENSE",
  "amount": 12000,
  "categoryId": 3,
  "description": "점심 - 김치찌개",
  "transactionDate": "2026-07-08"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "거래내역이 등록되었습니다.",
  "data": {
    "id": 42,
    "type": "EXPENSE",
    "amount": 12000,
    "categoryId": 3,
    "categoryName": "식비",
    "description": "점심 - 김치찌개",
    "transactionDate": "2026-07-08",
    "createdAt": "2026-07-08T12:31:05"
  }
}
```

### 2.4. 통계 (Statistics)

| 메서드 | URI 경로 | 설명 | 인증 |
|:---:|---|---|:---:|
| **GET** | `/api/statistics/monthly` | 월별 통계 조회 | ✅ |

- **Query Parameters:** `?yearMonth=2026-07`

**[예시] 월별 통계 요청 및 응답**
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "월별 통계를 조회했습니다.",
  "data": {
    "income": 2500000,
    "expense": 830000,
    "balance": 1670000,
    "byCategory": [
      { "categoryName": "식비", "total": 420000 },
      { "categoryName": "교통", "total": 180000 },
      { "categoryName": "문화", "total": 230000 }
    ]
  }
}
```

---

## 3. (도전) 추가 API 명세

| 메서드 | URI 경로 | 설명 | 인증 |
|:---:|---|---|:---:|
| **GET** | `/api/budgets` | 월 예산 조회 | ✅ |
| **POST** | `/api/budgets` | 월 예산 설정 | ✅ |
| **GET** | `/api/transactions/search` | 거래내역 상세 검색 (키워드/금액/기간) | ✅ |
| **GET** | `/api/transactions/export` | 조회된 거래내역 CSV 다운로드 | ✅ |