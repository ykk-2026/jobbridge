# YKK-Frontend 연결

프런트엔드의 단일 원본은 기본적으로 다음 경로를 사용합니다.

`C:\Users\data8320-13\Desktop\YKK\YKK-Frontend`

개발할 때는 YKK-Frontend가 `http://localhost:5173`, Spring Boot가
`http://localhost:8080`에서 실행됩니다. Vite가 `/api` 요청을 백엔드로
프록시합니다.

두 서버를 함께 실행하려면 프로젝트 루트에서 `run-linked-dev.cmd`를 실행합니다.
프런트엔드 위치가 바뀌면 실행 전에 `YKK_FRONTEND_DIR` 환경 변수에 새 경로를
지정합니다.

배포용 정적 파일이 필요하면 YKK-Frontend에서 `npm run build`를 실행한 뒤
`dist` 결과물을 배포 대상으로 사용합니다.
