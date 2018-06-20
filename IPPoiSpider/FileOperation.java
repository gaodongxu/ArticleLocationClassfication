package myPoiSpider;

import java.io.*;
import java.util.List;

import com.google.gson.Gson;

public class FileOperation {
	
	public static void writeToFile(String filePath, List<POIStruct> POIS) {
		Gson gson = new Gson();
		File file = new File(filePath);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false)));
			for (POIStruct e : POIS) {
				String jsonStr = gson.toJson(e);
				out.write(jsonStr + "\n");
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
