package myPoiSpider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetIPs {

	public static void main(String[] args) {

		Map<String, String> ips = getAllIPs();

		System.out.println(ips.size());

	}

	public static Map<String, String> getAllIPs() {
		String url = null;
		int i = 0;
		Map<String, String> ip = null;
		Map<String, String> ips = new LinkedHashMap<String, String>();

		url = "http://www.xicidaili.com/nn/1";
		ip = getByUrl(url);
		for (Map.Entry<String, String> e : ip.entrySet()) {
			ips.put(String.valueOf(i), e.getKey() + ":" + e.getValue());
			i++;
		}

		url = "http://www.xicidaili.com/nn/2";
		ip = getByUrl(url);
		for (Map.Entry<String, String> e : ip.entrySet()) {
			ips.put(String.valueOf(i), e.getKey() + ":" + e.getValue());
			i++;
		}

		ip = getFromFile();
		for (Map.Entry<String, String> e : ip.entrySet()) {
			ips.put(String.valueOf(i), e.getKey() + ":" + e.getValue());
			i++;
		}

		url = "http://www.kuaidaili.com/free/inha/1/";
		ip = getByUrl(url);
		for (Map.Entry<String, String> e : ip.entrySet()) {
			ips.put(String.valueOf(i), e.getKey() + ":" + e.getValue());
			i++;
		}

		// ips.putAll(get66ip());
		//
		// ips.putAll(getip3366());

		System.out.println("获取的ip个数:" + ips.size());
		return ips;
	}

	public static Map<String, String> getFromFile() {
		String filePath = "/data/gaodx1/POI/ips.txt";
		Map<String, String> ips = new LinkedHashMap<String, String>();
		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] ip_port = tempString.split(":");
				if (ip_port.length == 2) {
					ips.put(ip_port[0], ip_port[1]);
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

		return ips;
	}

	public static Map<String, String> getByUrl(String url) {
		Map<String, String> ips = new LinkedHashMap<String, String>();
		String res = HttpRequest.sendGet(url, null);
		String pat = "<td.*?>([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)</td>.*?<td.*?>([0-9]+)</td>";
		Pattern p = Pattern.compile(pat);
		if (res != null) {
			Matcher m = p.matcher(res);
			while (m.find()) {
				ips.put(m.group(1), m.group(2));
			}
		}
		return ips;
	}

	public static Map<String, String> getip3366() {
		Map<String, String> ips = new LinkedHashMap<String, String>();
		String url = "http://www.ip3366.net/free/";
		String param = "stype=1&page=1";
		String res = HttpRequest.sendGet(url, param);
		String pat = "<td.*?>([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)</td>.*?<td.*?>([0-9]+)</td>";
		Pattern p = Pattern.compile(pat);
		if (res != null) {
			Matcher m = p.matcher(res);
			while (m.find()) {
				ips.put(m.group(1), m.group(2));
			}
		}
		return ips;
	}

	public static Map<String, String> get66ip() {
		Map<String, String> ips = new LinkedHashMap<String, String>();
		String url = "http://www.66ip.cn/nmtq.php?getnum=50&isp=0&anonymoustype=0&start=&ports=&export=&ipaddress=&area=1&proxytype=2&api=66ip";
		String res = HttpRequest.sendGet(url, null);
		String pat = "([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+):([0-9]+)<br />";
		Pattern p = Pattern.compile(pat);
		if (res != null) {
			Matcher m = p.matcher(res);
			while (m.find()) {
				ips.put(m.group(1), m.group(2));
			}
		}
		return ips;
	}

	public static Map<String, String> get89ip() {
		Map<String, String> ips = new LinkedHashMap<String, String>();
		String url = "http://www.89ip.cn/apijk/?&tqsl=50&sxa=&sxb=&tta=&ports=&ktip=&cf=1";
		String res = HttpRequest.sendGet(url, null);
		String pat = "([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+):([0-9]+)<br />";
		Pattern p = Pattern.compile(pat);
		if (res != null) {
			Matcher m = p.matcher(res);
			while (m.find()) {
				ips.put(m.group(1), m.group(2));
			}
		}
		return ips;
	}

}
