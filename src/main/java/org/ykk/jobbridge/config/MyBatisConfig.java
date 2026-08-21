package org.ykk.jobbridge.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.ykk.jobbridge.mapper")
public class MyBatisConfig {

}
