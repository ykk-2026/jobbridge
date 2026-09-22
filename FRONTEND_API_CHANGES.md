# 프론트엔드(YKK-Frontend) 변경 안내 — 백엔드 강의 문법 전환

백엔드(jobbridge)를 강의 문법(`@Controller` + `@ResponseBody`, `request.getParameter()`, `MsgDTO`)으로
바꾸면서 **호출 방식**이 달라졌습니다. 수정할 파일은 `YKK-Frontend/src/app/api/*.ts` 9개뿐이며,
화면 컴포넌트는 손댈 필요가 없습니다.

## 공통 규칙 (반드시 읽기)

| 항목 | 이전 | 변경 후 |
|---|---|---|
| HTTP 방식 | GET / POST / PUT / PATCH / DELETE | **GET / POST 만** 사용 |
| URL | `/api/jobs/{id}`, `/api/jobs/{id}/close` | 경로 변수 없음 → `?jobId=1` 쿼리/폼 파라미터 |
| 요청 본문 | `Content-Type: application/json` + `JSON.stringify(...)` | **`application/x-www-form-urlencoded`** + `URLSearchParams` (강의의 `$("#f").serialize()`와 동일) |
| 등록/수정/삭제 응답 | 생성된 객체 또는 204 | **`{ result: 1, msg: "등록되었습니다." }`** (`MsgDTO`). `result`가 1이 아니면 실패 |
| 에러 | HTTP 400/401/403/409 + `{message}` | HTTP는 항상 200. `result: 0` + `msg`로 판단 |
| 로그인 안 한 상태의 조회 | 401 | 빈 배열 `[]` 또는 빈 객체 `{}` |
| 중복 체크 | `{available: true}` | `{ existsYn: "Y" \| "N" }` |
| 로그인 결과 | 회원 객체 | `MsgDTO` → 이어서 `getLoginInfo`로 회원 정보 조회 |
| 날짜/시간 | ISO 문자열 | 문자열 그대로 (`"2026-09-17"`, `"2026-09-17 10:00:00"`) |
| 세션 | `credentials` 기본값 | Vite 프록시 사용 시 그대로. 다른 origin에서 직접 호출하면 `credentials: 'include'` 필요 |

### 공통 헬퍼 (각 api 파일 상단의 `responseError` 대신 사용)

```ts
// src/app/api/http.ts  (새 파일)
export interface MsgDTO { result?: number; msg?: string }

// 폼 전송 : 강의의 $("#f").serialize() 와 같은 형식 (application/x-www-form-urlencoded)
export const form = (data: Record<string, unknown>) => {
  const params = new URLSearchParams();
  Object.entries(data).forEach(([key, value]) => {
    if (value === undefined || value === null) return;
    params.append(key, String(value));
  });
  return params;
};

export async function postForm(url: string, data: Record<string, unknown> = {}): Promise<MsgDTO> {
  const response = await fetch(url, { method: 'POST', body: form(data) });
  if (!response.ok) throw new Error(`요청 실패 (${response.status})`);
  const json = (await response.json()) as MsgDTO;
  if (json.result !== 1) throw new Error(json.msg || '요청이 실패했습니다.');
  return json;
}

export async function getJson<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const query = params ? `?${form(params).toString()}` : '';
  const response = await fetch(url + query, { cache: 'no-store' });
  if (!response.ok) throw new Error(`요청 실패 (${response.status})`);
  return response.json() as Promise<T>;
}
```

---

## 1. memberApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `checkLoginIdAvailability` | `GET /api/members/check-login-id?loginId=` → `{available, message}` | `GET /api/members/getLoginIdExists?loginId=` → `{existsYn}` |
| `registerMember` | `POST /api/members` (JSON) | `POST /api/members/insertMemberInfo` (폼) → `MsgDTO` |
| `loginMember` | `POST /api/members/login` (JSON) → 회원 | `POST /api/members/login` (폼) → `MsgDTO`, 이어서 `GET /api/members/getLoginInfo` |
| `getCurrentMember` | `GET /api/members/me` (204면 null) | `GET /api/members/getLoginInfo` (`loginId` 없으면 null) |
| `logoutMember` | `POST /api/members/logout` | 동일 (응답만 `MsgDTO`) |

```ts
import { getJson, postForm } from './http';

export async function checkLoginIdAvailability(loginId: string) {
  const json = await getJson<{ existsYn?: string }>('/api/members/getLoginIdExists', { loginId });
  const available = json.existsYn !== 'Y';
  return { available, message: available ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.' };
}

export async function loginMember(loginId: string, password: string) {
  await postForm('/api/members/login', { loginId: loginId.trim(), password }); // 실패 시 msg로 throw
  return (await getCurrentMember()) as LoginMember;
}

export async function getCurrentMember() {
  const json = await getJson<Partial<LoginMember>>('/api/members/getLoginInfo');
  return json.loginId ? (json as LoginMember) : null;
}

export async function logoutMember() {
  await fetch('/api/members/logout', { method: 'POST' });
}

export async function registerMember(form: RegisterFormData, passwordConfirm: string) {
  const json = await postForm('/api/members/insertMemberInfo', {
    loginId: form.loginId.trim().toLowerCase(),
    password: form.password,
    passwordConfirm,
    name: form.name.trim(),
    birthDate: form.birthDate.trim(),
    gender: form.gender || 'OTHER',
    email: form.email.trim(),
    phone: form.phone,
    role: 'JOB_SEEKER',
    desiredJob: form.preferredRole.trim(),
  });
  return { loginId: form.loginId.trim().toLowerCase(), name: form.name.trim(), message: json.msg ?? '' };
}
```

## 2. companyApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `registerCompany` | `POST /api/companies` (JSON) | `POST /api/companies/insertCompanyInfo` (폼) → `MsgDTO` |
| `loginCompany` | `POST /api/companies/login` (JSON) → `{member, profile}` | `POST /api/companies/login` (폼) → `MsgDTO`, 이어서 `GET /api/companies/getCompanyInfo` |
| (없었음) | `GET /api/companies/me` | `GET /api/companies/getCompanyInfo` → `CompanyProfileDTO` (`memberId, loginId, name, role, companyName, ...` 평면 구조) |

```ts
export async function registerCompany(form: CorporateRegisterFormData, passwordConfirm: string) {
  return postForm('/api/companies/insertCompanyInfo', {
    ...form, passwordConfirm,
    name: form.memberName?.trim() || form.managerName.trim(),
    employeeCount: form.employeeCount ? Number(form.employeeCount) : '',
    establishedDate: form.establishedDate || '',
  });
}

export async function loginCompany(loginId: string, password: string): Promise<CompanyLoginResult> {
  await postForm('/api/companies/login', { loginId: loginId.trim(), password });
  const c = await getJson<Record<string, any>>('/api/companies/getCompanyInfo');
  return {
    member: { id: c.memberId, loginId: c.loginId, name: c.name, role: c.role },
    profile: c as CompanyLoginResult['profile'],
  };
}
```

## 3. jobPostingApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `getOpenJobPostings` | `GET /api/jobs?limit=100` | `GET /api/jobs/getJobList` |
| `getMyJobPostings` | `GET /api/jobs/my` | `GET /api/jobs/getMyJobList` |
| (상세) | `GET /api/jobs/{id}` | `GET /api/jobs/getJobInfo?jobId=` |
| `createJobPosting` | `POST /api/jobs` (JSON) → 공고 | `POST /api/jobs/insertJobInfo` (폼) → `MsgDTO` (등록된 공고가 필요하면 `getMyJobList` 재조회) |
| `updateJobPosting` | `PUT /api/jobs/{id}` | `POST /api/jobs/updateJobInfo` (폼, `jobId` 포함) → `MsgDTO` |
| `closeJobPosting` | `PATCH /api/jobs/{id}/close` | `POST /api/jobs/updateJobClose` (폼 `jobId`) → `MsgDTO` |
| `deleteJobPosting` | `DELETE /api/jobs/{id}` | `POST /api/jobs/deleteJobInfo` (폼 `jobId`) → `MsgDTO` |

```ts
export const getOpenJobPostings = () => getJson<JobPosting[]>('/api/jobs/getJobList');
export const getMyJobPostings = () => getJson<JobPosting[]>('/api/jobs/getMyJobList');
export const createJobPosting = (form: JobPostingForm) => postForm('/api/jobs/insertJobInfo', form);
export const updateJobPosting = (jobId: number, form: JobPostingForm) =>
  postForm('/api/jobs/updateJobInfo', { ...form, jobId });
export const closeJobPosting = (jobId: number) => postForm('/api/jobs/updateJobClose', { jobId });
export const deleteJobPosting = (jobId: number) => postForm('/api/jobs/deleteJobInfo', { jobId });
```
`salaryMin`이 `null`이면 `form()`이 자동으로 빼므로 백엔드에서 NULL 저장됩니다.
`createJobPosting`이 반환하던 `JobPosting` 객체를 쓰던 화면이 있으면 `getMyJobPostings()`를 다시 호출해 주세요.

## 4. jobApplicationApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `getJobApplications` | `GET /api/job-applications` | `GET /api/job-applications/getApplicationList` |
| `getCompanyJobApplications` | `GET /api/job-applications/company` | `GET /api/job-applications/getCompanyApplicationList` |
| `saveJobApplication` | `POST /api/job-applications` (JSON) → 지원서 | `POST /api/job-applications/insertApplicationInfo` (폼) → `MsgDTO` |

```ts
export const getJobApplications = () => getJson<JobApplicationRecord[]>('/api/job-applications/getApplicationList');
export const getCompanyJobApplications = () => getJson<JobApplicationRecord[]>('/api/job-applications/getCompanyApplicationList');
export const saveJobApplication = (job: Job, form: JobApplicationForm) =>
  postForm('/api/job-applications/insertApplicationInfo', { jobId: job.id, ...form });
```

## 5. interestJobApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `getInterestJobs` | `GET /api/interest-jobs` | `GET /api/interest-jobs/getInterestJobList` |
| `saveInterestJob` | `POST /api/interest-jobs` (JSON) | `POST /api/interest-jobs/insertInterestJobInfo` (폼 `jobId`) → `MsgDTO` |
| `deleteInterestJob` | `DELETE /api/interest-jobs?companyName=&title=` | `POST /api/interest-jobs/deleteInterestJobInfo` (폼 `jobId`) → `MsgDTO` |

관심 공고 저장과 삭제에는 공고 전체 정보 대신 `jobId`만 전달합니다. 목록 응답의 공고 정보는 `job_posting`과 조인한 최신 값입니다.

## 6. communityApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `getCommunityPosts` | `GET /api/community/posts` | `GET /api/community/getPostList` |
| `createCommunityPost` | `POST /api/community/posts` (JSON) → 게시글 | `POST /api/community/insertPostInfo` (폼 `category,title,content`) → `MsgDTO`, 목록 재조회 |
| `incrementCommunityPostViews` | `POST /api/community/posts/{id}/views` → 게시글 | `GET /api/community/getPostInfo?postId=` → 게시글 (조회수 증가 포함) |
| `deleteCommunityPost` | `DELETE /api/community/posts/{id}` | `POST /api/community/deletePostInfo` (폼 `postId`) |
| `createCommunityComment` | `POST .../{postId}/comments` (JSON) → 댓글 | `POST /api/community/insertCommentInfo` (폼 `postId, content`) → `MsgDTO`, `getPostInfo`로 재조회 |
| `deleteCommunityComment` | `DELETE .../{postId}/comments/{commentId}` | `POST /api/community/deleteCommentInfo` (폼 `commentId`) |
| `reportCommunityPost` | `POST .../{postId}/reports` → 신고 | `POST /api/community/insertReportInfo` (폼 `postId`) → `MsgDTO` |

```ts
export const getCommunityPosts = () => getJson<ApiCommunityPost[]>('/api/community/getPostList');
export const createCommunityPost = (v: Pick<ApiCommunityPost, 'category' | 'title' | 'content'>) =>
  postForm('/api/community/insertPostInfo', v);
export const incrementCommunityPostViews = (id: string) =>
  getJson<ApiCommunityPost>('/api/community/getPostInfo', { postId: id });
export const deleteCommunityPost = (id: string) => postForm('/api/community/deletePostInfo', { postId: id });
export const createCommunityComment = (postId: string, content: string) =>
  postForm('/api/community/insertCommentInfo', { postId, content });
export const deleteCommunityComment = (_postId: string, commentId: string) =>
  postForm('/api/community/deleteCommentInfo', { commentId });
export const reportCommunityPost = (postId: string) => postForm('/api/community/insertReportInfo', { postId });
```

## 7. profileApi.ts

| 함수 | 이전 | 변경 후 |
|---|---|---|
| `getProfile` | `GET /api/profiles/{memberId}` | `GET /api/profiles/getProfileInfo?memberId=` |
| `saveProfile` | `PUT /api/profiles/{memberId}` (JSON) | `POST /api/profiles/saveProfileInfo` (폼, `memberId` 포함) → `MsgDTO` |

```ts
export const getProfile = (memberId: string | number) =>
  getJson<ApiJobSeekerProfile>('/api/profiles/getProfileInfo', { memberId });
export const saveProfile = (memberId: string | number, profile: ApiJobSeekerProfile) =>
  postForm('/api/profiles/saveProfileInfo', { ...profile, memberId });
```
`birthDate`, `contactTimeStart/End`는 `"1999-01-02"`, `"09:00"` 문자열로 보내면 됩니다. 한글 라벨(`남성`, `정규직`, `신입`, `이메일`)은 백엔드에서 코드값으로 변환합니다.

## 8. jobRecommendationApi.ts

`recommendationUrls`를 아래처럼 바꾸면 끝입니다 (응답 구조는 동일한 배열).
```ts
const recommendationUrls = (_memberId: number) => ['/api/recommendations/getRecommendationList'];
```
프로필이 없거나 구직자가 아니면 404 대신 **빈 배열**이 옵니다. 404를 보고 "프로필을 먼저 등록하세요"를 띄우던 화면이 있으면 `length === 0`으로 바꿔 주세요.

## 9. 관리자 / 상태

- `GET /api/admin/overview` → `GET /api/admin/getOverview` (관리자가 아니면 `{}`; 숫자 필드는 0)
- `GET /api/status` → 동일 URL, 응답은 `{ result: 1, msg: "UP" }`

---

## 참고 : 백엔드에서 바뀐 것

- 비밀번호 저장 방식이 BCrypt → **SHA-256 해시(EncryptUtil)** 로 바뀌었습니다. **기존 DB의 회원은 로그인이 되지 않으므로** 다시 가입하거나 `member.password`를 갱신해야 합니다.
  (예: 비밀번호 `1234` → `5a8702bdcc88f00df8538b14b492dd7fe2501d40a3b1e4aa94f5470c48ddbb71`)
- 필요한 테이블과 컬럼은 현재 `jobbridge` DB에 반영되어 있습니다.
- `application-mariadb.properties`는 `application.properties`로 합쳐졌습니다. DB 주소/계정은 `application.properties`에서 직접 수정합니다. 카카오/OpenAI 키만 환경변수(`KAKAO_REST_API_KEY`, `OPENAI_API_KEY`)로 넣습니다.
