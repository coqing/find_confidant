package top.coqing.services.tag.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;
import top.coqing.services.tag.service.TagService;
import top.coqing.services.user.client.UserFeignClient;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/tag")
@Slf4j
public class TagAdminController {


    @Resource
    private TagService tagService;

    /*
    被远程调用
    根据用户id查询tags
     */
    @GetMapping("/tagsById/{id}")
    public List<Tag> tagsById(@PathVariable("id") Long id){
        List<Tag> tagList = tagService.tagsById(id);
        return tagList;
    }
}
