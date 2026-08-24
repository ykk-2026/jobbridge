<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>${job.title}</title>
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
            margin: 0 0 12px;
            font-size: 28px;
        }

        h2 {
            margin: 34px 0 16px;
            font-size: 22px;
        }

        .meta {
            display: flex;
            flex-wrap: wrap;
            gap: 10px 18px;
            color: #526070;
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
            display: grid;
            gap: 14px;
        }

        label {
            display: grid;
            gap: 6px;
            font-weight: 700;
        }

        input,
        textarea {
            width: 100%;
            box-sizing: border-box;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            padding: 11px 12px;
            font: inherit;
            font-weight: 400;
        }

        textarea {
            min-height: 150px;
            resize: vertical;
        }

        button {
            justify-self: start;
            border: 0;
            border-radius: 6px;
            padding: 12px 18px;
            background: #2563eb;
            color: #fff;
            font-weight: 700;
            cursor: pointer;
        }
    </style>
</head>
<body>
<main class="wrap">
    <a class="back" href="${pageContext.request.contextPath}/jobs">목록으로</a>

    <section class="panel">
        <h1>${job.title}</h1>
        <div class="meta">
            <span>${job.companyName}</span>
            <span>${job.location}</span>
            <span>${job.employmentType}</span>
            <span>${job.salary}</span>
            <span>마감 ${job.deadline}</span>
        </div>
    </section>

    <h2>입사지원</h2>

    <c:if test="${not empty message}">
        <div class="alert success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert error">${error}</div>
    </c:if>

    <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/jobs/${job.id}/applications">
            <label>
                이름
                <input type="text" name="applicantName" required maxlength="100">
            </label>
            <label>
                이메일
                <input type="email" name="email" required maxlength="150">
            </label>
            <label>
                연락처
                <input type="text" name="phone" required maxlength="50">
            </label>
            <label>
                자기소개
                <textarea name="coverLetter"></textarea>
            </label>
            <button type="submit">지원하기</button>
        </form>
    </section>
</main>
</body>
</html>
