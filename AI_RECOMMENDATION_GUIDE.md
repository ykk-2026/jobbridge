# 생성형 AI 하이브리드 추천 설명서

## 한 문장 설명

구직자 프로필과 채용공고의 직무 의미는 생성형 AI가 30점으로 평가하고, 지역·고용형태·경력·급여·근무방식·접근성은 Java 규칙이 70점으로 계산해 총 100점의 추천 점수를 만든다.

## 전체 흐름

```text
GET /api/recommendations/getRecommendationList
        ↓
로그인 구직자의 job_seeker_profile 조회
        ↓
OPEN이고 마감되지 않은 job_posting 조회
        ↓
각 공고의 직무·기술 정보를 OpenAI Chat Completions API에 전달
        ↓
구조화된 JSON으로 직무 점수(0~30)와 근거 수신
        ↓
Java에서 나머지 조건 점수(0~70) 계산
        ↓
ai_job_posting_recommendation에 점수와 분석 출처 저장
        ↓
총점 내림차순으로 화면에 표시
```

## 점수 구성

| 항목 | 최고점 | 담당 |
|---|---:|---|
| 직무·기술 의미 적합도 | 30 | 생성형 AI, 실패 시 키워드 규칙 |
| 지역·출퇴근 | 15 | Java + 카카오 길찾기 |
| 고용형태 | 10 | Java 규칙 |
| 경력 | 10 | Java 규칙 |
| 급여 | 10 | Java 규칙 |
| 근무방식 | 10 | Java 규칙 |
| 접근성 | 15 | Java 규칙 |
| 합계 | 100 | 하이브리드 합산 |

## AI에 전달하는 정보

개인정보 최소화를 위해 이름, 이메일, 전화번호는 전송하지 않는다.

```json
{
  "candidate": {
    "desiredJob": "Java 백엔드 개발자",
    "careerType": "EXPERIENCED",
    "careerYears": 3,
    "introduction": "Spring Boot 기반 API 개발 경험이 있습니다."
  },
  "job": {
    "title": "백엔드 엔지니어",
    "jobCategory": "개발",
    "description": "서비스 API 개발",
    "requirements": "Java, Spring Boot 경험",
    "preferredQualifications": "AWS 경험"
  }
}
```

AI 응답은 JSON Schema로 제한한다.

```json
{
  "score": 27,
  "matchedSkills": ["Java", "Spring Boot"],
  "missingSkills": ["AWS"],
  "reason": "백엔드 기술과 담당업무가 대부분 일치합니다."
}
```

## 장애 시 동작

`OPENAI_API_KEY`가 없거나 API가 타임아웃·오류를 반환하면 추천 전체를 실패시키지 않는다. 공고 제목, 직무 카테고리, 요구사항, 설명을 기존 키워드 방식으로 비교해 동일한 30점 범위의 점수를 만든다.

- `job_match_source = GENERATIVE_AI`: 생성형 AI 결과
- `job_match_source = RULE_FALLBACK`: 키워드 대체 결과

화면에도 어떤 방식으로 계산했는지 표시한다.

## 실행 설정

PowerShell에서 백엔드를 실행하기 전에 환경변수를 설정한다.

```powershell
$env:OPENAI_API_KEY = "발급받은 API 키"
$env:OPENAI_MODEL = "gpt-6-astra"
.\mvnw.cmd spring-boot:run
```

AI 호출 없이 fallback만 확인하려면 다음과 같이 실행한다.

```powershell
$env:OPENAI_ENABLED = "false"
.\mvnw.cmd spring-boot:run
```

API 키는 `application.properties`나 Git 저장소에 직접 작성하지 않는다.

## 핵심 코드

- `OpenAiJobMatchService`: Chat Completions API 요청, JSON Schema 응답 해석, 개인정보 제외
- `KakaoMapDistanceService`: 카카오 주소 검색 + 길찾기로 출퇴근 거리·시간 조회
- `JsonHttpClient`: 위 두 서비스가 함께 쓰는 HTTP/JSON 호출 공통 코드
- `IAiJobMatchService`: AI 직무 평가 인터페이스 (`JobMatchAssessment` 결과 레코드 포함)
- `JobRecommendationCalculator`: 항목별 점수 계산 메서드를 순서대로 호출해 100점 합산
  - 직무·기술(30) · 지역·출퇴근(15) · 고용형태(10) · 경력(10) · 급여(10) · 근무방식(10) · 접근성(15)
  - 근무방식·접근성은 `checklistScore`로 "원하는 조건 중 몇 개를 지원하는가" 비율 계산
  - `MatchNotes`: 각 항목의 근거 문장을 모아 추천 문구·부족 조건 문자열 생성
  - `KoreaRegions`: 광역지역 인접 정보, 서울 자치구 좌표, 행정구역 이름 비교
- `util/TextUtils`, `util/NumberUtils`: 문자열 정리·숫자 범위 제한 등 공통 함수
- `AiJobRecommendationServiceImpl`: 공고별 계산, 점수순 정렬, DB 저장
- `AiJobRecommendationMapper.xml`: 분석 점수·출처·근거 INSERT/UPDATE
- `OpenAiJobMatchServiceTests`: 구조화 응답과 개인정보 미전송 검증

## 발표 예시

> 기존 방식은 공고 제목의 문자열이 정확히 일치해야 높은 점수를 받는 한계가 있었습니다. 이를 개선하기 위해 생성형 AI가 희망 직무와 공고의 제목, 담당업무, 자격요건을 의미적으로 비교해 30점을 계산하도록 했습니다. 급여나 경력, 접근성처럼 값이 명확한 조건은 생성형 AI가 추측하지 않도록 Java 규칙으로 70점을 계산합니다. AI 응답은 정해진 JSON 형식으로만 받고, API 장애 시에는 기존 키워드 계산으로 자동 전환하여 추천 기능이 중단되지 않도록 구현했습니다.

## 운영 시 고려사항

현재는 추천 요청마다 대상 공고별로 AI를 호출한다. 공고 수가 많아지면 비용과 응답시간을 줄이기 위해 키워드 또는 임베딩으로 후보 공고를 먼저 추린 뒤 상위 공고만 생성형 AI로 재평가하는 구조가 적합하다.
