<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>로그인</title>
</head>


<body>


<h2>로그인</h2>


<form action="/member/login" method="post">


    아이디 :
    <input type="text" name="loginId">

    <br>


    비밀번호 :
    <input type="password" name="password">

    <br>


    <button type="submit">
        로그인
    </button>


</form>


<a href="/member/join">
    회원가입
</a>


</body>
</html>