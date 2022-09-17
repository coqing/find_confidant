package top.coqing.services.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "top.coqing")
@EnableFeignClients(basePackages = "top.coqing")
public class ServiceChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceChatApplication.class,args);
    }
}
