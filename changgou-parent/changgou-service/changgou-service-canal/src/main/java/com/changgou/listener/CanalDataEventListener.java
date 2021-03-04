package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;


/**
 * 实现对表增删改查操作的监听
 * <p>
 * 增加操作监听
 *
 * @param eventType 当前操作的类型
 * @param rowData   发生变更的数据
 * <p>
 * 更新操作监听
 * @param eventType
 * @param rowData
 * <p>
 * 删除操作监听
 * @param eventType
 * @param rowData
 * <p>
 * 增加操作监听
 * @param eventType 当前操作的类型
 * @param rowData   发生变更的数据
 * <p>
 * 更新操作监听
 * @param eventType
 * @param rowData
 * <p>
 * 删除操作监听
 * @param eventType
 * @param rowData
 *//*

@CanalEventListener
public class CanalDataEventListener {


    */
/**
 * 增加操作监听
 *
 * @param eventType 当前操作的类型
 * @param rowData   发生变更的数据
 *//*

    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("列名:" + column.getName() + "-----变更的数据：" + column.getValue());
        }
    }

    */
/**
 * 更新操作监听
 * @param eventType
 * @param rowData
 *//*

    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("修改前列名:" + column.getName() + "-----修改前数据：" + column.getValue());
        }
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("修改后列名:" + column.getName() + "-----修改后数据：" + column.getValue());
        }
    }

    */
/**
 * 删除操作监听
 * @param eventType
 * @param rowData
 *//*

    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("删除前列名:" + column.getName() + "-----变更的数据：" + column.getValue());
        }
    }

    */

/**
 * 自定义操作监听
 * @param eventType
 * @param rowData
 *//*

    @ListenPoint(
            // 监听的操作类型
            eventType = {CanalEntry.EventType.DELETE, CanalEntry.EventType.UPDATE},

            // 指定监听的数据库
            schema = {"changgou_content"},

            // 指定监控的表
            table = {"tb_content"},

            // 指定实例的地址
            destination = "example"
    )
    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for(CanalEntry.Column column:rowData.getBeforeColumnsList()){
            System.out.println("自定义操作前列名:" + column.getName() + "-----变更的数据：" + column.getValue());
        }
        for(CanalEntry.Column column:rowData.getAfterColumnsList()){
            System.out.println("自定义操作后列名:" + column.getName() + "-----变更的数据：" + column.getValue());
        }
    }

}
*/

@CanalEventListener
public class CanalDataEventListener {

    @Autowired
    private ContentFeign contentFeign;

    //字符串
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //自定义数据库的 操作来监听
    @ListenPoint(destination = "example",
            schema = "changgou_content",
            table = {"tb_content", "tb_content_category"},
            eventType = {
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE,
                    CanalEntry.EventType.INSERT})

    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        // 1.获取列 category_id 对应的值
        String categoryId = getColumnValue(eventType, rowData);

        // 2.调用 feign 获取该分类下的所有的广告集合
        Result<List<Content>> categoryresut = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> data = categoryresut.getData();

        // 3.使用 redisTemplate 存储到 redis 中
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";

        // 如果是删除操作， 则获取 beforlist
        if (eventType == CanalEntry.EventType.DELETE) {
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if (column.getName().equalsIgnoreCase("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        } else {
            //如果是添加 或者 更新 操作 获取 afterlist
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if (column.getName().equalsIgnoreCase("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        return categoryId;
    }
}
