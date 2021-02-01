package org.springboot.Controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springboot.Util.RedisUtil;
import org.springboot.entity.Goods;
import org.springboot.mapper.KillMapper;
import org.springboot.redis.RedissLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RestController
public class GoodsController {
    //redisson分布式锁
    //RLock lock = redisson.getLock(lockKey);
//    private static RedissonClient redissonClient;
    ReentrantLock lock = new ReentrantLock();
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private KillMapper killMapper;
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1,
            10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
    @RequestMapping("/QueryAllGoods")
    public List<Goods> Query()
    {
        long start = System.currentTimeMillis();
        List<Goods> goods = killMapper.queryallgoods();
//        System.out.println(d);
//        System.out.println("mysql执行时间为");
        System.out.println("mysql执行时间为");
        System.out.println(( System.currentTimeMillis()-start));
        return goods;
    }
    @RequestMapping("/QueryAllGoodsByRedis")
    public List<Goods> QueryK()
    {
        List<Goods> goods = null;
        long start = System.currentTimeMillis();
        for(int i = 200;i<1000;i++)
        {
            System.out.println(redisUtil.get(String.valueOf(i)));
//           goods.add((Goods)redisUtil.get(String.valueOf(i)));
        }
//        System.out.println("mysql执行时间为");
        System.out.println("redis执行时间为");
        System.out.println(( System.currentTimeMillis()-start));
        return goods;
    }
    public Goods query(int id) {
        Goods goods;
//        goods=(redisUtil.get(String.valueOf(id))==null)? (Goods) redisUtil.get(String.valueOf(id)) :killMapper.querygoods(id);
        if(redisUtil.get(String.valueOf(id)) == null) {
            goods = killMapper.querygoods(id);
            if(goods==null)
                return null;
            redisUtil.set(String.valueOf(goods.getId()), goods,1000);
        }
        else {
            goods = (Goods) redisUtil.get(String.valueOf(id));
        }
        return goods;
    }
    @RequestMapping("/querygoods")
    public Goods query1(@RequestParam("id")int id) {
       return query(301);
    }
    @RequestMapping("/insertgoods")
    public int insert()
    {
        int a=1;
        for(;a<1000;a++)
        {
            killMapper.insertgoods(100+a,"book"+a);
        }
        return 1;
    }
    @RequestMapping("/order")
    public int order()
    {
        for(int i = 1;i<7;i++)
        {
            executor.submit(new Runnable()
            {
                @Override
                public void run() {
                    //redisson
//                    RedissonClient redissonClient = ne;
                    Redisson = RedissLockUtil.tryLock(+"", TimeUnit.SECONDS, 3, 20);
                    lock.lock();
                    //先向redis中查询商品剩余的数量
                    Goods g = query(302);
                    int number = g.getNumber();
                    System.out.println(number);
                    //先更新数据库,再删除redis中缓存的数据 保证数据的一致性
                    if(number>0)
                    {
                        killMapper.Order(302);
                        redisUtil.del(String.valueOf(302));
                    }
                    lock.unlock();
                }
            });
        }
        return 1;
    }
}

