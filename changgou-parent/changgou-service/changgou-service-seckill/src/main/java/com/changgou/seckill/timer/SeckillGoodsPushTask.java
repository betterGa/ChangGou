package com.changgou.seckill.timer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时任务，将秒杀商品读入 Redis 缓存
 */
@Component
public class SeckillGoodsPushTask {


    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每 5 秒执行一次
     */
    @Scheduled(cron = "0/60 * * * * ?")
    public void loadGoodsPushRedis() {
        //System.out.println("task demo");

        // 查询符合当前参与秒杀活动的时间清单
        List<Date> dateMenus = DateUtil.getDateMenus();

        // 遍历时间清单
        for (Date datemenus : dateMenus) {
            // 求时间的字符串格式
            String timeSpace = DateUtil.data2str(datemenus, "yyyyMMddHH");
            System.out.println(timeSpace);

            /** 满足以下条件，将商品读入 Redis 缓存
             1、秒杀商品库存stock_count >0
             2、审核状态为通过 status
             3、开始时间start_time <= 当前时间 && 当前时间+2 < 结束时间 end_time
             */

            // 构建条件
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andGreaterThan("stockCount", 0);
            criteria.andEqualTo("status", "1");
            criteria.andGreaterThanOrEqualTo("startTime", datemenus);
            criteria.andLessThan("endTime", DateUtil.addDateHour(datemenus, 2));

            // 排除已经存到 Redis 中的 seckillGoods
            // 先获取命名空间下所有的 key
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + timeSpace).keys();
            // 排除
            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }

            // 查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

            // 存入 Redis
            for (SeckillGoods seckillgood : seckillGoods) {
                // namespace是当前时间，key是商品id，value是商品信息
                redisTemplate.boundHashOps("SeckillGoods_" + timeSpace).put(seckillgood.getId().toString(), JSON.toJSONString(seckillgood));
            }
        }
    }
}
