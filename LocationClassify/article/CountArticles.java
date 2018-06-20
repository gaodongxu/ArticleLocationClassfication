package article;

import java.io.*;

/**
 * 整理文章标号到文件article_id中
 * 
 * @author Gdx
 *
 */
public class CountArticles {

	public static void main(String[] args) {
		String filePath = "D:\\data\\GraduationDesign\\Articles";
		String outputFile = "D:\\data\\GraduationDesign\\article_id";
		work(filePath, outputFile);
	}

	public static void work(String filePath, String outputFilePath) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath, false)));
			File file = new File(filePath);
			File[] firstFiles = file.listFiles();
			for (File firstFile : firstFiles) {
				System.out.println(firstFile.getName());
				File[] secondtFiles = firstFile.listFiles();
				for (File secondFile : secondtFiles) {
					// System.out.println(secondFile.getName());
					File[] thirdtFiles = secondFile.listFiles();
					for (File thirdtFile : thirdtFiles) {
						String str = thirdtFile.getName();
						out.write(str + "\n");
					}
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
