package com.changgou.user.dao;
import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:User的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface UserMapper extends Mapper<User> {
    @Select("SELECT * FROM tb_user where (password=#{password} and email=#{param1}) " +
            "or (password=#{password} and phone=#{param1}) " +
            "or (password=#{password} and username=#{param1})")
    List<User> registerCheck(String param1, String password);

    /**
     * 增加用户积分
     * @param username
     * @param points
     */
    @Update("update tb_user set points=points+#{points} where username=#{username}")
    int addPoints(@Param(value = "username") String username, @Param(value = "points") Integer points);
}
