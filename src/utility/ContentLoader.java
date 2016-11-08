package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class ContentLoader {

	public static String loadFileContent(String fileName) {
		// code for loading the file name
		String fileContent = new String();
		try {
			File f = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(f));
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				fileContent += line + "\n";
			}
			bufferedReader.close();
		} catch (Exception ex) {
			// handle the exception
		}
		return fileContent;
	}

	public static String loadFileContentSC(String fileName) {
		// loading content from a file
		String content = new String();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line + "\n";
			}
			scanner.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return content;
	}

	public static String[] getAllLines(String fileName) {
		// loading all lines from a file
		String content = loadFileContentSC(fileName);
		String[] lines = content.split("\n");
		return lines;
	}
}
