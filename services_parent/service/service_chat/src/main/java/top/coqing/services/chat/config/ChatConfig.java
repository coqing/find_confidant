package top.coqing.services.chat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("top.coqing.services.chat.mapper")
public class ChatConfig {
}
