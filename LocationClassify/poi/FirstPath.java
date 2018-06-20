package poi;

import java.io.*;
import java.util.*;

/**
 * 提取all_path中一级路径
 * 
 * @author Gdx
 *
 */

public class FirstPath {
	public static void main(String[] args) {
		String all_path_file = "D:\\data\\GraduationDesign\\all_path";
		String output_file = "D:\\data\\GraduationDesign\\file\\first_path.txt";
		check(all_path_file, output_file);
		System.out.println("OVRE!!!");
	}

	public static void check(String all_path_file, String output_file) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file, false)));

			HashSet<String> all_path_list = readAllPath(all_path_file);
			for (String all_path : all_path_list) {
				out.write(all_path + "\n");
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

	public static HashSet<String> readAllPath(String all_path_file) {
		HashSet<String> set = new HashSet<String>();
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
					if (path.split("->").length < 2 && tmpList[3].equals("main")) {
						System.out.println(mainName + "\t" + path.split("->").length + "\t" + path);
						set.add(mainName);
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
		return set;
	}

}
