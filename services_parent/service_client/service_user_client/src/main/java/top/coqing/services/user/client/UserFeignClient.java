package top.coqing.services.user.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.coqing.services.common.result.Result;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;

import java.util.List;

@FeignClient(value = "service-user")
//@Repository
public interface UserFeignClient {

    @PostMapping("/admin/user/updateUserTags/{id}")
    public boolean updateUserTags(@RequestBody List<Tag> tags,@PathVariable("id") Long id);
}
