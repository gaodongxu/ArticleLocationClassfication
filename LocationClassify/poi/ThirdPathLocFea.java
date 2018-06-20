package poi;

import java.io.*;
import java.util.ArrayList;

/**
 * 确保all_path中所有地名都在loc_features中
 * 
 * @author Gdx
 *
 */
public class ThirdPathLocFea {
	public static void main(String[] args) {
		String all_path_file = "D:\\data\\GraduationDesign\\all_path";
		String loc_fea_file = "D:\\data\\GraduationDesign\\file\\loc_features.txt";
		String output_file = "D:\\data\\GraduationDesign\\file\\loc_features_补充.txt";
		check(all_path_file, loc_fea_file, output_file);
		System.out.println("OVRE!!!");
	}

	public static void check(String all_path_file, String loc_fea_file, String output_file) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file)));

			ArrayList<String> all_path_list = readAllPath(all_path_file);
			ArrayList<String> loc_fea_list = readLocFea(loc_fea_file);
			for (String all_path : all_path_list) {
				if (!loc_fea_list.contains(all_path)) {
					out.write(all_path + "\n");
					out.flush();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}

		}

	}

	public static ArrayList<String> readAllPath(String all_path_file) {
		ArrayList<String> list = new ArrayList<String>();
		File all_path = new File(all_path_file);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(all_path));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String mainName = tempString.split("\t")[0];
				list.add(mainName);
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
		return list;
	}

	public static ArrayList<String> readLocFea(String loc_fea_path) {
		ArrayList<String> list = new ArrayList<String>();
		File loc_fea = new File(loc_fea_path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(loc_fea));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				list.add(tempString);
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
		return list;
	}

}
