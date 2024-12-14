package ca.usask.cs.srlab.rack.server.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MiscUtility {

	public static String list2Str(ArrayList<String> list) {
		String temp = "";
		for (String item : list) {
			temp += item + " ";
		}
		return temp.trim();
	}
	
	public static String list2Str(String[] list) {
		String temp = "";
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
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] filteredTokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(filteredTokens));
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

	public static HashMap<String, Integer> wordCount(String content) {
		// performing simple word count
		String[] words = content.split("\\s+");
		HashMap<String, Integer> countMap = new HashMap<>();
		for (String word : words) {
			if (countMap.containsKey(word)) {
				int count = countMap.get(word) + 1;
				countMap.put(word, count);
			} else {
				countMap.put(word, 1);
			}
		}
		return countMap;
	}

	public static HashMap<String, Integer> wordCount(ArrayList<String> words) {
		HashMap<String, Integer> countMap = new HashMap<>();
		for (String word : words) {
			if (countMap.containsKey(word)) {
				int count = countMap.get(word) + 1;
				countMap.put(word, count);
			} else {
				countMap.put(word, 1);
			}
		}
		return countMap;
	}

	public static double[] list2Arr(ArrayList<Double> list) {
		double[] temp = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			temp[i] = list.get(i);
		}
		return temp;
	}
}
