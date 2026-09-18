package org.ykk.jobbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * 스프링 부트 메인 클래스
 * @SpringBootApplication 은 아래 3개 어노테이션을 포함함
 *  - @Configuration : 자바 기반 설정 클래스
 *  - @EnableAutoConfiguration : 자동 설정 활성화
 *  - @ComponentScan : 해당 패키지 및 하위 패키지를 스캔하여 빈 등록
 *
 * Mapper 인터페이스는 각 인터페이스에 @Mapper 어노테이션을 선언하여 등록함
 * */
@SpringBootApplication
public class JobbridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobbridgeApplication.class, args);
    }

}
