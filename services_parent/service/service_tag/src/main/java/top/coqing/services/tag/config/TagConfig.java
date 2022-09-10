package top.coqing.services.tag.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("top.coqing.services.tag.mapper")
public class TagConfig {
}
