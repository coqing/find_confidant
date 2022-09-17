package top.coqing.services.tag.controller;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import top.coqing.services.common.exception.GlobalException;
import top.coqing.services.common.result.Result;
import top.coqing.services.common.result.StateCode;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.tag.service.TagService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tag/auth")
@Slf4j
public class TagAuthController {

    @Resource
    private TagService tagService;

    /**
     * 更新用户标签列表
     * @param tags
     * @param userId
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody List<Integer> tags, @RequestHeader("userId") long userId){
        if(tags==null){
            tags = new ArrayList<>();
        }

        // 验证标签id是否合法
        boolean flag = tagService.isLegal(tags);
        if(!flag){
            throw new GlobalException(StateCode.PARAMS_ERROR,"标签id只能为子标签id");
        }

        boolean res = tagService.updateTags(tags,userId);

        if(res){
            return Result.success();
        }
        return Result.fail();
    }

}

