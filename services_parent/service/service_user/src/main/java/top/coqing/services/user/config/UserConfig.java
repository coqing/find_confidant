package top.coqing.services.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("top.coqing.services.user.mapper")
public class UserConfig {
}
