package ikvOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
//import org.junit.Test;

import com.ifeng.ikvlite.IkvLiteClient;

import commenUtils.JsonUtils;
import dataStructure.appBill;
import dataStructure.itemf;

public class IKVOperationv3 {
	static Logger LOG = Logger.getLogger(IKVOperationv3.class);
	private final String HOST0 = "10.90.9.61";
	private final String HOST1 = "10.90.9.62";
	private final String HOST2 = "10.90.9.63";
	private final String HOST3 = "10.90.9.64";
	private final String HOST4 = "10.90.9.65";
	private final String KEYSPACE = "ikv";
	public static String[] defaultTables = { "appitemdb", "appbilldb" };
	private String TABLE = null;
	IkvLiteClient client;

	public IKVOperationv3(String tablename) {
		this.TABLE = tablename;
		boolean tnameCorr = checkTbaleName();
		if (!tnameCorr) {
			LOG.error("Tablename initial failed. Create IKV operation interface failed, return.");
			return;
		}
		try {
			client = new IkvLiteClient(KEYSPACE, tablename, true);
			client.connect(HOST0, HOST1, HOST2, HOST3, HOST4);
			LOG.info("IKV client create success. table name is " + this.TABLE);
		} catch (Exception e) {
			LOG.error("IKV client create failed.", e);
			return;
		}

	}

	public String get(String key) {
		if (key == null)
			return null;
		String value = "";
		try {
			value = client.get(key);
		} catch (Exception e) {
			LOG.error("ERROR get: " + key);
			LOG.error("ERROR get", e);
			return null;
		}
		return value;
	}

	private Map<String, String> gets(String... key) {
		if (key == null)
			return null;
		Map<String, String> retmap = new HashMap<String, String>();
		try {
			retmap = client.gets(key);
		} catch (Exception e) {
			LOG.error("ERROR get: " + key);
			LOG.error("ERROR get", e);
			return null;
		}
		return retmap;
	}

	public void put(String key, String value) {
		if (key == null)
			return;
		if (value == null)
			return;
		try {
			client.put(key, value);
		} catch (Exception e) {
			LOG.error("ERROR put key: " + key);
			LOG.error("ERROR put toJson", e);
			return;
		}
	}

	/**
	 * 在IKV中删除tag type 注意：
	 * 
	 * @param key
	 *            ，
	 * @return
	 */
	public void del(String key) {
		if (key != null) {
			try {
				client.delete(key);
			} catch (Exception e) {
				LOG.error("Delete " + key + " failed.");
			}
		}

		LOG.info("Delete key " + key + " success.");
	}

	public void close() {
		client.close();
		LOG.info("Client to " + TABLE + " has closed.");
	}

	private boolean checkTbaleName() {
		ArrayList<String> tableList = new ArrayList<String>();
		for (int i = 0; i < defaultTables.length; i++) {
			tableList.add(defaultTables[i]);
		}
		if (null == this.TABLE) {
			LOG.warn("[WARNING]Table name setting failed, tablename is null.");
			return false;
		} else if (!tableList.contains(this.TABLE)) {
			LOG.warn("[WARNING]Table name you are using is not in the default list.");
		}
		return true;
	}

	/**
	 * * 查询示例： 1.自有cmpp数据：cmpp_4773047 
	 * 2.imcp数据： imcp_112320266
	 * 3.新的cmpp数据：cmpp_033760049820820 
	 * 4.subid数据： sub_xxxxxxx 
	 * 5.标题：
	 * 态度！体重不够120？谈什么性感！
	 * 6.url(包含videoid):http://mini.eastday.com/a/160823095712489.html
	 * 
	 * @param key
	 * @return
	 */
	public itemf queryItemF(String key) {
		if (key == null || key.isEmpty())
			return null;
		itemf item = null;
		String json = null;
		json = get(key);
		if (json == null)
			return null;
		if (json.startsWith("cmpp_") || json.startsWith("imcp_"))
			json = get(json);
		if (json == null)
			return null;
		try {
			item = JsonUtils.fromJson(json, itemf.class);
		} catch (Exception e) {
			LOG.error("[ERROR] Convert ikv json to item.");
		}
		return item;
	}

	/**
	 * 批量查询 仅支持id查询
	 * 
	 * @param key
	 * @param type
	 * @return Map<String//id,itemf>
	 */
	public Map<String, itemf> queryItems(String... keys) {

		if (keys == null || keys.length <= 0)
			return null;
		String[] queryKeys = keys;
		Map<String, itemf> itemMap = new HashMap<String, itemf>();
		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap = gets(queryKeys);
		itemMap = getItemMap(jsonMap);
		return itemMap;
	}

	public appBill queryAppBill(String key) {
		if (key == null || key.isEmpty())
			return null;
		appBill bill = null;
		String json = null;
		json = get(key);
		if (json == null)
			return null;
		if (json.startsWith("cmpp_") || json.startsWith("imcp_"))
			json = get(json);
		if (json == null)
			return null;
		try {
			bill = JsonUtils.fromJson(json, appBill.class);
		} catch (Exception e) {
			LOG.error("[ERROR] Convert ikv json to item.");
		}
		return bill;
	}

	/**
	 * 循环获取itemf并导出到map中
	 * 
	 * @param jsonMap
	 * @return
	 */
	private static Map<String, itemf> getItemMap(Map<String, String> jsonMap) {
		if (jsonMap == null)
			return null;
		Map<String, itemf> resultMap = new HashMap<String, itemf>();
		for (Entry<String, String> entry : jsonMap.entrySet()) {
			try {
				itemf item = JsonUtils.fromJson(entry.getValue(), itemf.class);
				resultMap.put(entry.getKey(), item);
			} catch (Exception e) {
				LOG.error("ERROR in change to item.");
			}
		}
		return resultMap;
	}
}
