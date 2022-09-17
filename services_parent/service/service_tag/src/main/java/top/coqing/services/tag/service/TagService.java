package top.coqing.services.tag.service;

import top.coqing.services.model.tag.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author coqing
* @description 针对表【tag】的数据库操作Service
* @createDate 2022-09-03 12:10:06
*/
public interface TagService extends IService<Tag> {

    public String redis1(String s);

    public String redis2(String s);

    public String redis3(String s);

    /*
    根据用户id查询tags
     */
    List<Tag> tagsById(Long id);

    /**
     * 查询所有标签构成的标签树
     * @return
     */
    List<Tag> getTagTree();

    /**
     * 更新用户标签列表
     * @param tags
     * @param userId
     * @return
     */
    boolean updateTags(List<Integer> tags, long userId);

    /**
     * 获取标签热度排行
     * @return
     */
    Map<String, Integer> getTagRank();

    /**
     * 验证标签id是否合法
     * @param tags
     * @return
     */
    boolean isLegal(List<Integer> tags);
}
