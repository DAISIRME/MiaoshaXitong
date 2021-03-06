package org.springboot.Controller;

import org.springboot.Util.RedisUtil;
import org.springboot.entity.Goods;
import org.springboot.entity.User;
import org.springboot.mapper.KillMapper;
import org.springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    RedisTemplate redisTemplate = new RedisTemplate();
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private Lock lock = new ReentrantLock(true);//互斥锁 参数默认false，不公平锁
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1,
            10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private KillMapper killMapper;
    @GetMapping({"/","/index.html"})
    public String index()
    {
        return "index";
    }
    @GetMapping("/queryUserlist")
    public List<User> queryUserList()
    {
        List<User> users = userMapper.queryUserList();
        for(User user : users)
        {
            System.out.println(user);
        }
        return users;
    }

    /**
     * 初始化redis内存 将所有的数据读到redis缓存中
     * @return
     */
    @GetMapping("/redisinit")
    public List<Goods> redisinit()
    {
//        bloomFilter
        List<Goods> goodses = killMapper.queryallgoods();
        for(Goods g:goodses)
        {
            redisUtil.set(String.valueOf(g.getId()),g);
        }
        System.out.println(redisUtil.get(String.valueOf(301)));
        return goodses;
    }
    @GetMapping("/start")
    public int start()
    {
        List<Goods> goodses = killMapper.queryallgoods();
        for(Goods g:goodses)
        {
            System.out.println(g);
//            redisUtil.set();
        }
        for(int i = 0;i<10;i++ )
        {
            executor.submit(new Runnable() {
                @Override
                public void run()
                {
                    lock.lock();
                    killMapper.Order(301);
                    lock.unlock();
                }
            });
        }
    return 1;
    }
}
