package myPoiSpider;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gaode.LatitudeUtils;

public class NumIPPoiSpider {
	static String preUrl = "http://www.poi86.com";
	static String perPath = "/data/gaodx1/POI/POIInfo/";
	static Log log = LogFactory.getLog("poi");
	static String PRO = "";
	static String CITY = "";
	static String DIS = "";
	static int START = 1;
	static int END = 10000;

	public static void main(String[] args) {
		IPHttpRequest.refresh();
		if (args.length == 4) {
			PRO = args[0];
			CITY = args[1];
			START = Integer.parseInt(args[2]);
			END = Integer.parseInt(args[3]);
		}
		if (args.length == 5) {
			PRO = args[0];
			CITY = args[1];
			DIS = args[2];
			START = Integer.parseInt(args[3]);
			END = Integer.parseInt(args[4]);
		}
//		 PRO = "湖北省";
//		 CITY = "武汉市";
//		 DIS = "江岸区";
//		 START=200;
//		 END=202;

		log.info("args:" + PRO + "---" + CITY + "---" + DIS + "-" + START + "-" + END);
		Map<String, String> proUrl = getProvinceUrl();
		log.info(proUrl.size());
		while (proUrl.size() == 0) {
			IPHttpRequest.refresh();
			proUrl = getProvinceUrl();
			log.info(proUrl.size());
		}

		for (Map.Entry<String, String> pro : proUrl.entrySet()) {

			Map<String, String> disUrl = new HashMap<String, String>();
			String proName = pro.getKey();
			log.info(proName);

			if (!proName.equals(PRO)) {
				continue;
			}

			if (isZXS(pro.getKey())) {

				disUrl = getDisUrl(pro.getValue());
				for (Map.Entry<String, String> dis : disUrl.entrySet()) {
					String disName = dis.getKey();
					log.info("\t" + dis.toString());
					if (!disName.equals(CITY)) {
						continue;
					}
					String fileName = proName + "-" + proName + "-" + disName + START + "-" + END;

					List<POIStruct> POIS = getAllPOIS(dis.getValue());

					FileOperation.writeToFile(perPath + proName + "/" + fileName, POIS);
					log.info("【写入文件】" + proName + "/" + fileName);

				}

			} else {
				Map<String, String> cityUrl = getCityUrl(pro.getValue());
				for (Map.Entry<String, String> city : cityUrl.entrySet()) {
					log.info(city.toString());
					String cityName = city.getKey();
					if (!cityName.equals(CITY)) {
						continue;
					}
					disUrl = getDisUrl(city.getValue());

					for (Map.Entry<String, String> dis : disUrl.entrySet()) {
						log.info("\t" + dis.toString());
						String disName = dis.getKey();
						if (!disName.equals(DIS)) {
							continue;
						}

						List<POIStruct> POIS = getAllPOIS(dis.getValue());

						String fileName = proName + "-" + cityName + "-" + disName + START + "-" + END;

						FileOperation.writeToFile(perPath + proName + "/" + cityName + "/" + fileName, POIS);
						log.info("【写入文件】" + proName + "/" + cityName + "/" + fileName);

					}

				}
				break;
			}

		}
	}

	public static List<POIStruct> getAllPOIS(String disUrl) {
		List<POIStruct> POIS = new ArrayList<POIStruct>();

		String res = IPHttpRequest.sendGet(disUrl, null);
		while (res == null || res.equals("")) {
			IPHttpRequest.refresh();
			res = IPHttpRequest.sendGet(disUrl, null);
		}
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

		int a = disUrl.lastIndexOf("/") + 1;
		disUrl = disUrl.substring(0, a);
		for (int i = START; i <= END; i++) {
			System.out.print(" " + i);
			String disurll = disUrl + i + ".html";
			List<POIStruct> pois = getPOIS(disurll);
			while (pois.size() == 0) {
				IPHttpRequest.refresh();
				pois = getPOIS(disurll);
			}
			POIS.addAll(pois);
		}

		System.out.println();

		return POIS;
	}

	public static List<POIStruct> getPOIS(String disurll) {
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
				if (CITY!=null && !allName.contains(CITY)) {
					allName = CITY + allName;
				}
				if (!allName.contains(PRO)) {
					allName = PRO + allName;
				}
				List<String> poi_list = LatitudeUtils.getGeocoderLatitude(allName);

				POIS.add(new POIStruct(name, address, url_, poi_list));
			}
		}
		return POIS;
	}

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

	public static boolean isZXS(String s) {
		return s.equals("北京市") || s.equals("天津市") || s.equals("重庆市") || s.equals("上海市") || s.equals("澳门特别行政区")
				|| s.equals("香港特别行政区");
	}

}
