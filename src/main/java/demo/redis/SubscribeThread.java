package demo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class SubscribeThread extends Thread {
	static Logger logger = LoggerFactory.getLogger(SubscribeThread.class);

	private JedisPool pool;
	private String channels;
	private String password;

	public SubscribeThread(JedisPool pool, String channels) {
		this.pool = pool;
		this.channels = channels;
		this.password = null;
	}

	public SubscribeThread(JedisPool pool, String channels, String password) {
		this.pool = pool;
		this.channels = channels;
		this.password = password;
	}

	@Override
	public void run() {
		JedisPubSub jedisPubSub = new JedisPubSub() {
			int i = 1;

			@Override
			public void onMessage(String channel, String message) {
				if (i >= 3) {
					unsubscribe();
				}
				logger.info("接收到消息:{}", message);
				// System.out.println("接收到消息:" + message);
				i++;
			}

			@Override
			public void onPMessage(String pattern, String channel, String message) {
				logger.info("onPMessage");
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				logger.info("onSubscribe");
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				logger.info("onUnsubscribe");
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
				logger.info("onPUnsubscribe");
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
				logger.info("onPSubscribe");
			}

		};

		Jedis jedis = pool.getResource();
		try {
			if (password != null) {
				jedis.auth(password);
			}
			jedis.subscribe(jedisPubSub, channels);
		} catch (Exception e) {
			logger.error("发生异常 ", e);
			try {
				this.sleep(1000);
			} catch (InterruptedException e1) {
			}
			jedis = pool.getResource();
			if (password != null) {
				jedis.auth(password);
			}
			jedis.subscribe(jedisPubSub, channels);
		} finally {
			jedis.close();
		}
		logger.info(jedis.toString() + " 关闭");
	}
}
