package myPoiSpider;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

public class IPHttpRequest {

	public static void main(String[] args) {
		System.out.println("S");
		System.out.println(ips.toString());
		refresh();
		for (int i = 0; i < 10; i++) {
			System.out.println(sendGet("http://ip.chinaz.com/getip.aspx", null));
		}
	}

	static String ip = "";
	static String port = "";
	static Map<String, String> ips = GetIPs.getAllIPs();

	public static void refresh() {
		long a = new Date().getTime();
		int b = (int) (a % ips.size());

		String[] ip_port=ips.get(String .valueOf(b)).split(":");
		ip=ip_port[0];
		port=ip_port[1];
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;

		try {
			int por = Integer.parseInt(port);
			if (ip != null && port != null) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, por));
				String urlNameString = url + "?" + param;
				URL realUrl = new URL(urlNameString);
				// 打开和URL之间的连接
				HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection(proxy);
				// 设置通用的请求属性
				connection.setRequestProperty("accept", "*/*");
				connection.setRequestProperty("connection", "Keep-Alive");
				connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				connection.setConnectTimeout(2000);
				// 建立实际的连接
				connection.connect();
				// 获取所有响应头字段
				// Map<String, List<String>> map = connection.getHeaderFields();
				// 遍历所有的响应头字段
				// for (String key : map.keySet()) {
				// System.out.println(key + "--->" + map.get(key));
				// }
				// 定义 BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
				System.out.print("\t" + ip + ":" + port + "\t");
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			System.out.println("异常url:" + url + " 参数:" + param + " ip:");
			System.out.println(ip + ":" + port);
			// e.printStackTrace();
			return null;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
