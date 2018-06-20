package poi;

import java.io.*;

import redis.clients.jedis.Jedis;

/**
 * POI信息保存到Redis数据库中
 * 
 * @author Gdx
 *
 */
public class POI2Redis {
	Jedis jedis = null;

	public POI2Redis(String ip, int port) {
		jedis = new Jedis(ip, port);
		System.out.println("Redis Test Over!");
	}

	public void listPushTest() {
		jedis.select(2);
		String key = "aaa";
		String[] values = { "bbb", "ccc", "ddd" };
		for (String value : values) {
			jedis.lpush(key, value);
		}
	}

	public void POIToRedis() {
		jedis.select(12);
		jedis.flushDB();
		String filePath = "D:\\data\\GraduationDesign\\poi_file\\all_path";

		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				System.out.println(tempString);
				String[] strs = tempString.split("\t");

				if (strs.length == 4) {
					for (int i = 3; i > 0; i--) {
						jedis.lpush(strs[0], strs[i]);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

	}

	public static void main(String[] args) {
		POI2Redis redisTest = new POI2Redis("127.00.0.1", 6379);
		// redisTest.listPushTest();
		redisTest.POIToRedis();

	}

}
