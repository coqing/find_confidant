package top.coqing.services.tag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "top.coqing")
@EnableFeignClients(basePackages = "top.coqing") // feign指定包路径
public class ServiceTagApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTagApplication.class,args);
    }
}
