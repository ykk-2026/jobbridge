# YKK-Frontend 자동 연결

`C:\Users\data8320-13\Desktop\YKK\YKK-Frontend`가 프론트엔드 원본입니다.
원본 파일을 저장하면 Vite가 `src/main/resources/static`을 자동 빌드하고,
열려 있는 `http://localhost:8080` 화면도 자동으로 새로고침됩니다.

백엔드와 프론트 감시기를 함께 실행하려면 프로젝트 루트의
`run-linked-dev.cmd`를 실행합니다.

원본 위치가 바뀌면 실행 전에 `YKK_FRONTEND_DIR` 환경 변수에 새 경로를 지정합니다.
백엔드를 IntelliJ에서 따로 실행할 때는 `frontend/run-linked-watch.cmd`도 함께 실행합니다.
