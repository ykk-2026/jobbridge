<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>회원가입</title>
</head>

<body>

<h2>회원가입</h2>


<form action="/member/join" method="post">

    아이디 :
    <input type="text" name="loginId">
    <br>


    비밀번호 :
    <input type="password" name="password">
    <br>


    이름 :
    <input type="text" name="name">
    <br>


    이메일 :
    <input type="email" name="email">
    <br>


    전화번호 :
    <input type="text" name="phone">
    <br>


    <button type="submit">
        회원가입
    </button>

</form>


<a href="/member/login">
    로그인
</a>


</body>
</html>