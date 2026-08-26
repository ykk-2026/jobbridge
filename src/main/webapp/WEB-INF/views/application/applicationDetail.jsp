<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>지원서 상세</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f6f8fb;
            color: #1f2937;
        }

        .wrap {
            max-width: 960px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .back {
            display: inline-block;
            margin-bottom: 18px;
            color: #2563eb;
            text-decoration: none;
        }

        .panel {
            padding: 24px;
            border: 1px solid #dbe3ef;
            border-radius: 8px;
            background: #fff;
        }

        h1 {
            margin: 0 0 18px;
            font-size: 28px;
        }

        h2 {
            margin: 28px 0 14px;
            font-size: 20px;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 16px;
        }

        .item {
            display: grid;
            gap: 5px;
        }

        .label {
            color: #64748b;
            font-size: 13px;
            font-weight: 700;
        }

        .value {
            line-height: 1.5;
        }

        .cover {
            white-space: pre-wrap;
            line-height: 1.6;
        }

        .alert {
            margin: 0 0 16px;
            padding: 12px 14px;
            border-radius: 8px;
        }

        .success {
            border: 1px solid #86efac;
            background: #f0fdf4;
            color: #166534;
        }

        .error {
            border: 1px solid #fecaca;
            background: #fef2f2;
            color: #991b1b;
        }

        form {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
        }

        select {
            min-width: 160px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            padding: 10px 12px;
            font: inherit;
        }

        button {
            border: 0;
            border-radius: 6px;
            padding: 11px 16px;
            background: #2563eb;
            color: #fff;
            font-weight: 700;
            cursor: pointer;
        }

        @media (max-width: 680px) {
            .grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<main class="wrap">
    <a class="back" href="${pageContext.request.contextPath}/applications">목록으로</a>

    <c:if test="${not empty message}">
        <div class="alert success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert error">${error}</div>
    </c:if>

    <section class="panel">
        <h1>${application.applicantName} 지원서</h1>

        <div class="grid">
            <div class="item">
                <span class="label">지원 공고</span>
                <span class="value">${application.jobTitle}</span>
            </div>
            <div class="item">
                <span class="label">회사</span>
                <span class="value">${application.companyName}</span>
            </div>
            <div class="item">
                <span class="label">이메일</span>
                <span class="value">${application.email}</span>
            </div>
            <div class="item">
                <span class="label">연락처</span>
                <span class="value">${application.phone}</span>
            </div>
            <div class="item">
                <span class="label">지원일</span>
                <span class="value">${application.createdAt}</span>
            </div>
            <div class="item">
                <span class="label">최근 변경일</span>
                <span class="value">${application.updatedAt}</span>
            </div>
        </div>

        <h2>상태 변경</h2>
        <form method="post" action="${pageContext.request.contextPath}/applications/${application.id}/status">
            <select name="status">
                <option value="RECEIVED" ${application.status == 'RECEIVED' ? 'selected' : ''}>접수</option>
                <option value="REVIEWING" ${application.status == 'REVIEWING' ? 'selected' : ''}>검토중</option>
                <option value="ACCEPTED" ${application.status == 'ACCEPTED' ? 'selected' : ''}>합격</option>
                <option value="REJECTED" ${application.status == 'REJECTED' ? 'selected' : ''}>불합격</option>
            </select>
            <button type="submit">변경</button>
        </form>

        <h2>자기소개</h2>
        <div class="cover"><c:out value="${application.coverLetter}"/></div>
    </section>
</main>
</body>
</html>
