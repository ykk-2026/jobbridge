<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>내 프로필 | JobBridgeAI</title>
</head>

<body>

<h1>내 프로필</h1>

<form action="${pageContext.request.contextPath}/profile/save"
      method="post">

    <section>
        <h2>기본 정보</h2>

        <div>
            <label>이름</label>
            <input type="text"
                   value="${profile.name}"
                   readonly>
        </div>

        <div>
            <label>생년월일</label>
            <input type="date"
                   value="${profile.birthDate}"
                   readonly>
        </div>

        <div>
            <label>성별</label>
            <input type="text"
                   value="${profile.gender}"
                   readonly>
        </div>

        <div>
            <label>이메일</label>
            <input type="email"
                   value="${profile.email}"
                   readonly>
        </div>

        <div>
            <label>전화번호</label>
            <input type="text"
                   value="${profile.phone}"
                   readonly>
        </div>

        <div>
            <label>현재 거주지역</label>
            <input type="text"
                   name="residenceRegion"
                   value="${profile.residenceRegion}">
        </div>
    </section>


    <section>
        <h2>희망 근무 조건</h2>

        <div>
            <label>희망 직무</label>
            <input type="text"
                   name="desiredJob"
                   value="${profile.desiredJob}">
        </div>

        <div>
            <label>희망 근무지역</label>
            <input type="text"
                   name="desiredRegion"
                   value="${profile.desiredRegion}">
        </div>

        <div>
            <label>희망 고용형태</label>

            <select name="employmentType">
                <option value="ANY"
                ${profile.employmentType == 'ANY' ? 'selected' : ''}>
                    상관없음
                </option>

                <option value="FULL_TIME"
                ${profile.employmentType == 'FULL_TIME' ? 'selected' : ''}>
                    정규직
                </option>

                <option value="PART_TIME"
                ${profile.employmentType == 'PART_TIME' ? 'selected' : ''}>
                    시간제
                </option>

                <option value="CONTRACT"
                ${profile.employmentType == 'CONTRACT' ? 'selected' : ''}>
                    계약직
                </option>

                <option value="INTERNSHIP"
                ${profile.employmentType == 'INTERNSHIP' ? 'selected' : ''}>
                    인턴
                </option>

                <option value="FREELANCER"
                ${profile.employmentType == 'FREELANCER' ? 'selected' : ''}>
                    프리랜서
                </option>
            </select>
        </div>

        <div>
            <label>경력 구분</label>

            <select name="careerType">
                <option value="ANY"
                ${profile.careerType == 'ANY' ? 'selected' : ''}>
                    상관없음
                </option>

                <option value="ENTRY"
                ${profile.careerType == 'ENTRY' ? 'selected' : ''}>
                    신입
                </option>

                <option value="EXPERIENCED"
                ${profile.careerType == 'EXPERIENCED' ? 'selected' : ''}>
                    경력
                </option>
            </select>
        </div>

        <div>
            <label>경력 연수</label>

            <input type="number"
                   name="careerYears"
                   min="0"
                   value="${profile.careerYears}">
        </div>

        <div>
            <label>희망 최소 연봉</label>

            <input type="number"
                   name="minSalary"
                   min="0"
                   value="${profile.minSalary}">

            <span>만원</span>
        </div>
    </section>


    <section>
        <h2>근무 형태 선호</h2>

        <label>
            <input type="checkbox"
                   name="remotePreferred"
                   value="true"
            ${profile.remotePreferred ? 'checked' : ''}>
            재택근무
        </label>

        <label>
            <input type="checkbox"
                   name="flexiblePreferred"
                   value="true"
            ${profile.flexiblePreferred ? 'checked' : ''}>
            유연근무
        </label>

        <label>
            <input type="checkbox"
                   name="hybridPreferred"
                   value="true"
            ${profile.hybridPreferred ? 'checked' : ''}>
            하이브리드 근무
        </label>

        <label>
            <input type="checkbox"
                   name="onsitePreferred"
                   value="true"
            ${profile.onsitePreferred ? 'checked' : ''}>
            출퇴근 근무
        </label>
    </section>


    <section>
        <h2>연락 가능 설정</h2>

        <div>
            <label>연락 가능 시작 시간</label>

            <input type="time"
                   name="contactTimeStart"
                   value="${profile.contactTimeStart}">
        </div>

        <div>
            <label>연락 가능 종료 시간</label>

            <input type="time"
                   name="contactTimeEnd"
                   value="${profile.contactTimeEnd}">
        </div>

        <div>
            <label>선호 연락 방식</label>

            <select name="contactMethod">
                <option value="PHONE"
                ${profile.contactMethod == 'PHONE' ? 'selected' : ''}>
                    전화
                </option>

                <option value="EMAIL"
                ${profile.contactMethod == 'EMAIL' ? 'selected' : ''}>
                    이메일
                </option>

                <option value="SMS"
                ${profile.contactMethod == 'SMS' ? 'selected' : ''}>
                    문자
                </option>

                <option value="KAKAO"
                ${profile.contactMethod == 'KAKAO' ? 'selected' : ''}>
                    카카오톡
                </option>
            </select>
        </div>
    </section>


    <section>
        <h2>자기소개</h2>

        <textarea name="introduction"
                  maxlength="500"
                  rows="7">${profile.introduction}</textarea>
    </section>


    <section>
        <h2>프로필 공개 설정</h2>

        <label>
            <input type="radio"
                   name="profilePublic"
                   value="true"
            ${profile.profilePublic ? 'checked' : ''}>
            공개
        </label>

        <label>
            <input type="radio"
                   name="profilePublic"
                   value="false"
            ${!profile.profilePublic ? 'checked' : ''}>
            비공개
        </label>
    </section>

    <button type="submit">저장하기</button>

</form>

</body>
</html>