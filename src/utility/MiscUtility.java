package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MiscUtility {

	public static String list2Str(ArrayList<String> list) {
		String temp = new String();
		for (String item : list) {
			temp += item + " ";
		}
		return temp.trim();
	}
	
	public static String list2Str(String[] list) {
		String temp = new String();
		for (String item : list) {
			temp += item + " ";
		}
		return temp.trim();
	}
	
	public static ArrayList<String> extract(ArrayList<String> all, int TOPK) {
		ArrayList<String> temp = new ArrayList<>();
		for (String item : all) {
			temp.add(item);
			if (temp.size() == TOPK) {
				break;
			}
		}
		return temp;
	}
	
	public static ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(ftokens));
		return refined;
	}

	public static ArrayList<String> str2List(String str) {
		return new ArrayList<>(Arrays.asList(str.split("\\s+")));
	}

	public static double[] list2Array(ArrayList<Integer> list) {
		double[] array = new double[list.size()];
		for (int index = 0; index < list.size(); index++) {
			array[index] = list.get(index);
		}
		return array;
	}

	public static HashMap<String, Integer> wordcount(String content) {
		// performing simple word count
		String[] words = content.split("\\s+");
		HashMap<String, Integer> countmap = new HashMap<>();
		for (String word : words) {
			if (countmap.containsKey(word)) {
				int count = countmap.get(word) + 1;
				countmap.put(word, count);
			} else {
				countmap.put(word, 1);
			}
		}
		return countmap;
	}

	public static HashMap<String, Integer> wordcount(ArrayList<String> words) {
		// performing simple word count
		HashMap<String, Integer> countmap = new HashMap<>();
		for (String word : words) {
			if (countmap.containsKey(word)) {
				int count = countmap.get(word) + 1;
				countmap.put(word, count);
			} else {
				countmap.put(word, 1);
			}
		}
		return countmap;
	}

	public static double[] list2Arr(ArrayList<Double> list) {
		double[] temp = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			temp[i] = list.get(i);
		}
		return temp;
	}

}
