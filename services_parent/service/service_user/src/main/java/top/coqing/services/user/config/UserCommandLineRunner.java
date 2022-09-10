package top.coqing.services.user.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.coqing.services.user.service.UserService;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserCommandLineRunner implements CommandLineRunner {

    @Resource
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
//        boolean res = userService.initUserAndTagsToRedis();
//        if(res){
//            log.info("成功:初始化用户和标签-------------------------");
//        }else {
//            log.info("失败:初始化用户和标签-------------------------");
//        }

    }
}
