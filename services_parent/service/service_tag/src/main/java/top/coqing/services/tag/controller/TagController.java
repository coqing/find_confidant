package top.coqing.services.tag.controller;

import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.coqing.services.common.result.Result;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;
import top.coqing.services.tag.aspect.CallTime;
import top.coqing.services.tag.service.TagService;
import top.coqing.services.user.client.UserFeignClient;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tag")
@Slf4j
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 获取标签热度排行
     * @return
     */
    @GetMapping("/tagRank")
    public Result getTagRank(){
        Map<String, Integer> tagRank = tagService.getTagRank();
        if(tagRank==null){
            return Result.fail();
        }
        return Result.success(tagRank);
    }

    /**
     * 查询所有标签构成的标签树
     * @return
     */
    @GetMapping("/tree")
    public Result getTagTree(){
        List<Tag> tagTree = tagService.getTagTree();
        return Result.success(tagTree);
    }


    /**
     * 测试打印时间切面
     * @return
     */
    @CallTime
    @GetMapping("/test")
    public Result test() throws InterruptedException {
        Thread.sleep(1000);
        return Result.success();
    }

}
