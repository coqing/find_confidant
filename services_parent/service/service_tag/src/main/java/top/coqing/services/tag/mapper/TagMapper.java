package top.coqing.services.tag.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.coqing.services.model.tag.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author coqing
* @description 针对表【tag】的数据库操作Mapper
* @createDate 2022-09-03 12:10:06
* @Entity top.coqing.services.model.tag.Tag
*/
public interface TagMapper extends BaseMapper<Tag> {

    /*
    添加单个联系
     */
    @Insert("insert into user_tag(user_id,tag_id) values(#{userId},#{tagId})")
    int insertUserTagOne(@Param("userId") Long userId, @Param("tagId") Long tagId);


    /**
     * 删除单个联系
     * @param userId
     * @param tagId
     * @return 影响的记录数
     */
    @Delete("delete from user_tag where user_id = #{userId}  and tag_id = #{tagId}")
    Integer deleteUserTagOne(@Param("userId") Long userId, @Param("tagId") Long tagId);



    /**
     * 根据userId查询该用户的所有标签
     * @param userId
     * @return List<Tag>
     */
    @Select("select t.* from  tag as t " +
            "left join  user_tag as v " +
            "on v.tag_id = t.id " +
            "where v.user_id = #{userId}")
    List<Tag> selectTagList(@Param("userId") Long userId);




    /**
     * 查询是否存在这个关系
     * @param userId
     * @param tagId
     * @return
     */
    @Select("select count(*) from  user_tag where user_id = #{userId}  and tag_id = #{tagId}")
    Integer selectUserTag(@Param("userId") Long userId, @Param("tagId") Long tagId);
}




