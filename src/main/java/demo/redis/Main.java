package demo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 常用命令参考：
 * 
 * http://redis.io/commands
 * 
 * http://www.redis.cn/commands.html （中文）
 * 
 * @author hbm
 *
 */
public class Main {
	static Logger logger = LoggerFactory.getLogger(SubscribeThread.class);

	final static int REDIS_DB = 8; // 数据库

	public static void main(String[] args) {
		/** 基本设置 **/
		String host = "127.0.0.1";
		int port = 6379;
//		String password = "密码";
		String channels = "test-channel";

		/** 连接池，获取一个实例 **/
		JedisPool pool = new JedisPool(host, port);
		Jedis jedis = pool.getResource();
//		jedis.auth(password);

		/** 起一个新线程处理订阅逻辑 **/
		SubscribeThread sub1 = new SubscribeThread(pool, channels);
		sub1.start();
		logger.info("Subscribe Finish");

		/** 连接池，获取第二个实例 **/
		Jedis jedis2 = pool.getResource();
//		jedis2.auth(password);
		jedis2.select(REDIS_DB);
		String key = "test.hello";

		logger.info(jedis2.get(key));
		jedis2.incrBy(key, 100);
		logger.info(jedis2.get(key));
		jedis2.incrBy(key, -23);
		logger.info(jedis2.get(key));
		jedis2.getSet(key, "88");
		logger.info(jedis2.get(key));

		jedis2.publish(channels, "{json}");// 发送一个消息进行测试
		jedis2.close();
		logger.info(jedis2.toString() + " 关闭");
	}

}
