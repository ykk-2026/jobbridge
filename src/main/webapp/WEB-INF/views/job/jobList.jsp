<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>채용공고</title>
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

        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 24px;
        }

        h1 {
            margin: 0;
            font-size: 28px;
        }

        .manage-link {
            color: #2563eb;
            font-weight: 700;
            text-decoration: none;
        }

        .job-list {
            display: grid;
            gap: 14px;
        }

        .job-card {
            display: block;
            padding: 20px;
            border: 1px solid #dbe3ef;
            border-radius: 8px;
            background: #fff;
            color: inherit;
            text-decoration: none;
        }

        .job-card:hover {
            border-color: #2563eb;
        }

        .title {
            margin: 0 0 8px;
            font-size: 20px;
            font-weight: 700;
        }

        .meta {
            display: flex;
            flex-wrap: wrap;
            gap: 10px 16px;
            color: #526070;
            font-size: 14px;
        }

        .empty {
            padding: 40px;
            border: 1px solid #dbe3ef;
            border-radius: 8px;
            background: #fff;
            text-align: center;
            color: #526070;
        }
    </style>
</head>
<body>
<main class="wrap">
    <div class="header">
        <h1>채용공고</h1>
        <a class="manage-link" href="${pageContext.request.contextPath}/applications">입사지원 관리</a>
    </div>

    <c:choose>
        <c:when test="${empty jobs}">
            <div class="empty">등록된 채용공고가 없습니다.</div>
        </c:when>
        <c:otherwise>
            <div class="job-list">
                <c:forEach var="job" items="${jobs}">
                    <a class="job-card" href="${pageContext.request.contextPath}/jobs/${job.id}">
                        <p class="title">${job.title}</p>
                        <div class="meta">
                            <span>${job.companyName}</span>
                            <span>${job.location}</span>
                            <span>${job.employmentType}</span>
                            <span>${job.salary}</span>
                            <span>마감 ${job.deadline}</span>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
