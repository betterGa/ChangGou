package com.changgou.goods.dao;
import com.changgou.goods.pojo.Template;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:Template的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface TemplateMapper extends Mapper<Template> {

    @Select("select tem.name from tb_template tem,tb_category cat where tem.id=cat.template_id and cat.id=#{cateId}")
    String findByCategory(Integer cateId);
}
