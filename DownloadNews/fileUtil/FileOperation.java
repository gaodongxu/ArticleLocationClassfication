package fileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileOperation {

	public static void main(String[] args) {
		String filePath = "D:/a_file/test/test.txt";
		String content = "第一行\n第二行\n";
		String content2 = "第三行\n第四行\n";
		createFile(filePath);
		writeFile(filePath, content);
		writeFile(filePath, content2);
		readFile(filePath);
	}

	/**
	 * 按行读文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		File file = new File(filePath);
		BufferedReader reader = null;
		String result = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				// System.out.println(tempString);
				result = result + tempString;
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
		return result;
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 * @param conent
	 */
	public static void writeFile(String filePath, String content) {
		BufferedWriter out = null;
		File file = new File(filePath);
		if (!file.exists()) {
			createFile(filePath);
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false)));
			out.write(content);
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

	/**
	 * 创建文件
	 * 
	 * @param filePath
	 */
	public static void createFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			// System.out.println("文件已存在");
		} else {
			try {
				File fileParent = file.getParentFile();
				if (fileParent != null) {
					if (!fileParent.exists()) {
						fileParent.mkdirs();
					}
				}
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 以追加方式写文件，效率低
	 * 
	 * @param filePath
	 * @param content
	 */
	public static void writeFileByFileWriter(String filePath, String content) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(filePath), true);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}