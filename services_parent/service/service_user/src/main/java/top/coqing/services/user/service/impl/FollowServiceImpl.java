package top.coqing.services.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.coqing.services.model.user.Follow;
import top.coqing.services.user.service.FollowService;
import top.coqing.services.user.mapper.FollowMapper;
import org.springframework.stereotype.Service;

/**
* @author coqing
* @description 针对表【follow】的数据库操作Service实现
* @createDate 2022-09-17 18:45:20
*/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{

}




