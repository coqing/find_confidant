package top.coqing.services.chat.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.coqing.services.model.chat.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.coqing.services.model.tag.Tag;

import java.util.List;

/**
* @author coqing
* @description 针对表【chat】的数据库操作Mapper
* @createDate 2022-09-17 12:13:53
* @Entity top.coqing.services.model.chat.Chat
*/
public interface ChatMapper extends BaseMapper<Chat> {

    /**
     * 根据userId查询该用户的所有标签
     * @param userId
     * @return List<Chat>
     */
    @Select(
            "select * from chat where id in " +
            "(select MAX(id) from chat where from_id = #{userId} or to_id=#{userId} GROUP BY from_id,to_id) " +
            "ORDER BY create_time desc " +
            "limit #{limit}"
    )
    List<Chat> userList(@Param("userId") Long userId,@Param("limit") Integer limit);

}




