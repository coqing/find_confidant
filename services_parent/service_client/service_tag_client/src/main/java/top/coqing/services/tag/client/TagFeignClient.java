package top.coqing.services.tag.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.coqing.services.model.tag.Tag;

import java.util.List;

@FeignClient(value = "service-tag")
public interface TagFeignClient {

    @GetMapping("/admin/tag/tagsById/{id}")
    public List<Tag> tagsById(@PathVariable("id") Long id);

}
