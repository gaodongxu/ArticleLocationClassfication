package downloadNews;

import com.google.gson.Gson;

import fileUtil.FileOperation;
import ikvOperation.IKVOperationv3;
import net.sf.json.JSONObject;

public class DownloadNews {

	static Gson gson = new Gson();
	static IKVOperationv3 op = new IKVOperationv3("appitemdb");

	public static void main(String[] args) {
		// for (int i = 49961700; i > 99; i--) {
		for (int i = 11489625; i > 99; i--) {
			String key = "cmpp_" + i;
			String json = op.get(key);
			JSONObject jsonObject = JSONObject.fromObject(json);
			if (jsonObject.keySet().contains("originalLoclist")) {
				FileOperation.writeFile("/MyLoc/" + i / 100000 + "/" + i / 1000 + "/" + key, json);
				System.out.println(i);
			}
		}
		System.exit(0);
	}

}
