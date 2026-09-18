package org.ykk.jobbridge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 설정 클래스
 * 화면(React)은 http://localhost:5173 에서 실행되고, 스프링부트는 http://localhost:8080 에서 실행되기 때문에
 * 서로 다른 주소 간 Ajax 호출을 허용하는 CORS 설정이 필요함
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api 로 시작되는 URL만 허용
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "OPTIONS") // 강의와 동일하게 GET, POST 방식만 사용
                .allowedHeaders("*")
                .allowCredentials(true); // 로그인 세션(JSESSIONID 쿠키)을 전달하기 위해 필요
    }
}
