package poi;

import java.io.*;
import java.util.*;

/**
 * 提取all_path中二级及以上路径
 * 
 * @author Gdx
 *
 */

public class SecondPath {
	public static void main(String[] args) {
		String all_path_file = "D:\\data\\GraduationDesign\\poi_file\\all_path";
		String first_path_file = "D:\\data\\GraduationDesign\\poi_file\\first_path";
		String output_file = "D:\\data\\GraduationDesign\\poi_file\\second_path";
		check(all_path_file, first_path_file, output_file);
		System.out.println("OVRE!!!");
	}

	public static void check(String all_path_file, String first_path_file, String output_file) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file, false)));

			ArrayList<String> first_path_list = readList(first_path_file);
			HashMap<String, List<String>> all_path_map = readAllPathMap(all_path_file, first_path_list);
			for (String first_path : first_path_list) {
				out.write(first_path + "\n");
				for (String path : all_path_map.get(first_path)) {
					out.write(path + "\n");
				}
				out.flush();
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

	public static HashMap<String, List<String>> readAllPathMap(String all_path_file, List<String> up_path_list) {
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		for (String up_path : up_path_list) {
			ArrayList<String> tmpList = new ArrayList<String>();
			map.put(up_path, tmpList);
		}
		File all_path = new File(all_path_file);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(all_path));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {

				String[] tmpList = tempString.split("\t");
				String mainName = tmpList[0];
				String[] paths = tmpList[1].split(", ");
				for (String path : paths) {
					String[] locs = path.split("->");
					String up_path = locs[0];
					if (locs.length == 2 && tmpList[3].equals("main")) {
						System.out.println(mainName + "\t" + path.split("->").length + "\t" + path);
						map.get(up_path).add(mainName);
						break;
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
		return map;
	}

	public static ArrayList<String> readList(String file_path) {
		ArrayList<String> list = new ArrayList<String>();
		File loc_fea = new File(file_path);
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
