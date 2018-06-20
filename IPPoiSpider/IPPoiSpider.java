package myPoiSpider;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gaode.LatitudeUtils;

/**
 * POI信息爬取类
 */
public class IPPoiSpider {
	static String preUrl = "http://www.poi86.com";
	static String perPath = "/data/gaodx1/POI/POIInfo/";
	static Log log = LogFactory.getLog("poi");
	static String PRO = "";
	static String CITY = "";
	static String DIS = "";

	public static void main(String[] args) {
		IPHttpRequest.refresh();
		if (args.length == 2) {
			PRO = args[0];
			CITY = args[1];
		}
		if (args.length == 3) {
			PRO = args[0];
			CITY = args[1];
			DIS = args[2];
		}
		// PRO = "四川省";
		// CITY = "成都市";
		// DIS = "郫县";

		log.info("args:" + PRO + "---" + CITY + "---" + DIS);
		Map<String, String> proUrl = getProvinceUrl();
		log.info(proUrl.size());
		while (proUrl.size() == 0) {
			IPHttpRequest.refresh();
			proUrl = getProvinceUrl();
			log.info(proUrl.size());
		}

		// 遍历省一级的Map
		for (Map.Entry<String, String> pro : proUrl.entrySet()) {
			Map<String, String> disUrl = new HashMap<String, String>();
			String proName = pro.getKey();
			log.info(proName);
			if (!proName.equals(PRO)) {
				continue;
			}

			// 根据是否直辖市分类处理
			if (isZXS(pro.getKey())) {

				// 从省一级链接获取区一级链接
				disUrl = getDisUrl(pro.getValue());

				List<String> disrtucts = new ArrayList<String>();
				for (Map.Entry<String, String> dis : disUrl.entrySet()) {
					disrtucts.add(dis.getKey());
				}

				// 遍历区一级链接
				for (Map.Entry<String, String> dis : disUrl.entrySet()) {
					String disName = dis.getKey();
					log.info("\t" + dis.toString());
					if (!disName.equals(CITY)) {
						continue;
					}
					String fileName = proName + "-" + proName + "-" + disName;

					// 此List是为了保证在本区的POI信息下不会出现其他区的地址信息
					List<String> delDiss = new ArrayList<String>(disrtucts);
					Iterator<String> iter = delDiss.iterator();
					while (iter.hasNext()) {
						String item = iter.next();
						if (item.equals(disName)) {
							iter.remove();
						}
					}
					log.info("\tdelDiss:" + delDiss.toString());
					List<POIStruct> POIS = getAllPOIS(dis.getValue(), delDiss, proName, null, disName);

					FileOperation.writeToFile(perPath + proName + "/" + fileName, POIS);
					log.info("【写入文件】" + proName + "/" + fileName);

				}

			} else {
				// 如果不是直辖市,获取市一级链接Map
				Map<String, String> cityUrl = getCityUrl(pro.getValue());

				// 遍历市一级链接
				for (Map.Entry<String, String> city : cityUrl.entrySet()) {
					log.info(city.toString());
					String cityName = city.getKey();
					if (!cityName.equals(CITY)) {
						continue;
					}
					// 获取区一级链接Map
					disUrl = getDisUrl(city.getValue());

					// 遍历区一级链接
					List<String> disrtucts = new ArrayList<String>();
					for (Map.Entry<String, String> dis : disUrl.entrySet()) {
						disrtucts.add(dis.getKey());
					}

					for (Map.Entry<String, String> dis : disUrl.entrySet()) {
						log.info("\t" + dis.toString());
						String disName = dis.getKey();
						if (!disName.equals(DIS)) {
							continue;
						}

						// 此List是为了保证在本区的POI信息下不会出现其他区的地址信息
						List<String> delDiss = new ArrayList<String>(disrtucts);
						Iterator<String> iter = delDiss.iterator();
						while (iter.hasNext()) {
							String item = iter.next();
							if (item.equals(disName)) {
								iter.remove();
							}
						}
						// 获取所有POI信息并写入文件
						List<POIStruct> POIS = getAllPOIS(dis.getValue(), delDiss, proName, cityName, disName);

						String fileName = proName + "-" + cityName + "-" + disName;

						FileOperation.writeToFile(perPath + proName + "/" + cityName + "/" + fileName, POIS);
						log.info("【写入文件】" + proName + "/" + cityName + "/" + fileName);

					}

				}
				break;
			}

		}
	}

	// 根据区一级链接获取所有POI信息
	public static List<POIStruct> getAllPOIS(String disUrl, List<String> delDiss, String proName, String cityName,
			String disName) {
		List<POIStruct> POIS = new ArrayList<POIStruct>();

		String res = IPHttpRequest.sendGet(disUrl, null);
		while (res == null || res.equals("")) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(disUrl, null);
		}
		// 利用正则表达式获取页数
		String pat = "<li class=\"disabled\"><a href=\"javascript:;\">(.*)/(.*)</a></li></ul>";
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(res);

		while (!m.find()) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(disUrl, null);
			while (res == null || res.equals("")) {
				IPHttpRequest.refresh();
				res = IPHttpRequest.sendGet(disUrl, null);
			}
			m = p.matcher(res);
		}

		log.info("\t页数:" + m.group(2));
		int num = Integer.parseInt(m.group(2));

		int a = disUrl.lastIndexOf("/") + 1;
		disUrl = disUrl.substring(0, a);
		for (int i = 1; i <= num; i++) {
			System.out.print(" " + i);
			// 加上页数获取每一页的链接
			String disurll = disUrl + i + ".html";
			List<POIStruct> pois = getPOIS(disurll, delDiss, proName, cityName, disName);
			while (pois.size() == 0) {
				IPHttpRequest.refresh();
				pois = getPOIS(disurll, delDiss, proName, cityName, disName);
			}
			POIS.addAll(pois);
		}

		System.out.println();

		return POIS;
	}

	// 获取某一页上的POI信息
	public static List<POIStruct> getPOIS(String disurll, List<String> delDiss, String proName, String cityName,
			String disName) {
		List<POIStruct> POIS = new ArrayList<POIStruct>();
		String res = IPHttpRequest.sendGet(disurll, null);
		while (res == null || res.equals("")) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(disurll, null);
		}
		int aa = res.indexOf("<tr>");
		int bb = res.lastIndexOf("</tr>");
		while (aa < 0 || bb < 0) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(disurll, null);
			while (res == null || res.equals("")) {
				IPHttpRequest.refresh();
				res = IPHttpRequest.sendGet(disurll, null);
			}
			aa = res.indexOf("<tr>");
			bb = res.lastIndexOf("</tr>");
		}
		System.out.println("正常 ");
		res = res.substring(aa, bb + 6);
		String[] trs = res.split("</tr>");
		for (String tr : trs) {

			String poiPattern = "<tr>" + ".*?<td><a href=\"(.*?)\" title=\"\">(.*?)</a></td>" + ".*?<td>(.*?)</td>";
			Pattern p = Pattern.compile(poiPattern);
			Matcher m = p.matcher(tr);

			while (m.find()) {
				String name = m.group(2).trim();
				String address = m.group(3).trim();
				String url_ = preUrl + m.group(1).trim();

				String allName = address + name;
				if (shouldDel(allName, delDiss)) {
					continue;
				}
				if (!allName.contains(disName)) {
					allName = disName + allName;
				}
				if (cityName != null && !allName.contains(cityName)) {
					allName = cityName + allName;
				}
				if (!allName.contains(proName)) {
					allName = proName + allName;
				}
				List<String> poi_list = LatitudeUtils.getGeocoderLatitude(allName);

				POIS.add(new POIStruct(name, address, url_, poi_list));
			}
		}
		return POIS;
	}

	// 根据市一级的链接获取区一级的链接并存入Map
	public static Map<String, String> getDisUrl(String cityUrl) {
		Map<String, String> disUrl = new HashMap<String, String>();
		String res = IPHttpRequest.sendGet(cityUrl, null);
		while (res == null || res.equals("")) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(cityUrl, null);
		}

		String disPattern = "<a href=\"(/poi/district/.*?)\" title=\".*?\">(.*?)</a>";
		Pattern p = Pattern.compile(disPattern);
		Matcher m = p.matcher(res);

		while (m.find()) {
			disUrl.put(m.group(2).trim(), preUrl + m.group(1).trim());
		}
		return disUrl;
	}

	// 根据省一级的链接获取市一级的链接并存入Map
	public static Map<String, String> getCityUrl(String proUrl) {
		Map<String, String> cityUrl = new HashMap<String, String>();
		String res = IPHttpRequest.sendGet(proUrl, null);
		while (res == null || res.equals("")) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(proUrl, null);
		}

		String cityPattern = "<a href=\"(/poi/city/.*?)\" title=\".*?\">(.*?)</a>";
		Pattern p = Pattern.compile(cityPattern);
		Matcher m = p.matcher(res);

		while (m.find()) {
			cityUrl.put(m.group(2).trim(), preUrl + m.group(1).trim());
		}
		return cityUrl;
	}

	// 根据首页获取省一级的链接并存入Map
	public static Map<String, String> getProvinceUrl() {
		Map<String, String> proUrl = new HashMap<String, String>();
		String url = "http://www.poi86.com/poi/amap.html";
		String res = IPHttpRequest.sendGet(url, null);
		while (res == null || res.equals("")) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(url, null);
		}

		String proPattern = "<a href=\"(/poi/province/.*?)\" title=\".*?\">(.*?) \\(<small";
		Pattern p = Pattern.compile(proPattern);
		Matcher m = p.matcher(res);

		while (m.find()) {
			proUrl.put(m.group(2).trim(), preUrl + m.group(1).trim());
		}
		return proUrl;
	}

	// 从列表del中删除name
	public static boolean shouldDel(String name, List<String> del) {
		for (String str : del) {
			if (name.contains(str)) {
				return true;
			}
		}
		return false;
	}

	// 判断是否直辖市和特别行政区
	public static boolean isZXS(String s) {
		return s.equals("北京市") || s.equals("天津市") || s.equals("重庆市") || s.equals("上海市") || s.equals("澳门特别行政区")
				|| s.equals("香港特别行政区");
	}

}
