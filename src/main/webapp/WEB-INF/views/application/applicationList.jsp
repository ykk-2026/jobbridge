<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>입사지원 관리</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f6f8fb;
            color: #1f2937;
        }

        .wrap {
            max-width: 1100px;
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

        .link {
            color: #2563eb;
            font-weight: 700;
            text-decoration: none;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            overflow: hidden;
            border: 1px solid #dbe3ef;
            border-radius: 8px;
            background: #fff;
        }

        th,
        td {
            padding: 14px 16px;
            border-bottom: 1px solid #e5eaf1;
            text-align: left;
            vertical-align: top;
        }

        th {
            background: #f8fafc;
            color: #475569;
            font-size: 14px;
        }

        tr:last-child td {
            border-bottom: 0;
        }

        .applicant {
            font-weight: 700;
        }

        .muted {
            color: #64748b;
            font-size: 13px;
        }

        .status {
            display: inline-block;
            min-width: 64px;
            border-radius: 999px;
            padding: 5px 10px;
            background: #eef2ff;
            color: #3730a3;
            font-size: 13px;
            font-weight: 700;
            text-align: center;
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
        <h1>입사지원 관리</h1>
        <a class="link" href="${pageContext.request.contextPath}/jobs">채용공고로 이동</a>
    </div>

    <c:choose>
        <c:when test="${empty applications}">
            <div class="empty">접수된 입사지원이 없습니다.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>지원자</th>
                    <th>공고</th>
                    <th>상태</th>
                    <th>지원일</th>
                    <th>관리</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="application" items="${applications}">
                    <tr>
                        <td>
                            <div class="applicant">${application.applicantName}</div>
                            <div class="muted">${application.email}</div>
                            <div class="muted">${application.phone}</div>
                        </td>
                        <td>
                            <div>${application.jobTitle}</div>
                            <div class="muted">${application.companyName}</div>
                        </td>
                        <td>
                            <span class="status">
                                <c:choose>
                                    <c:when test="${application.status == 'REVIEWING'}">검토중</c:when>
                                    <c:when test="${application.status == 'ACCEPTED'}">합격</c:when>
                                    <c:when test="${application.status == 'REJECTED'}">불합격</c:when>
                                    <c:otherwise>접수</c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                        <td>${application.createdAt}</td>
                        <td>
                            <a class="link" href="${pageContext.request.contextPath}/applications/${application.id}">상세</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
