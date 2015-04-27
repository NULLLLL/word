package com.word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Word {

	@SuppressWarnings("resource")
	public static byte[] re() throws FileNotFoundException {
		try {
			FileChannel channel = new FileInputStream(TestMain.NAME).getChannel();
			MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			byte[] result = new byte[(int) channel.size()];
			if (byteBuffer.remaining() > 0)
				byteBuffer.get(result, 0, byteBuffer.remaining());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("resource")
	public static List<String> readFileByLine() throws Exception {
		long start = System.currentTimeMillis();
		String enterStr = "\n";
		try {
			int bufSize = 1024 * 1024 * 5;
			FileChannel fcin = new RandomAccessFile(TestMain.NAME, "r").getChannel();
			ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
			List<String> readAllLines = new ArrayList<String>();
			byte[] bs = new byte[bufSize];
			StringBuilder strBuf = new StringBuilder();
			String tempString = null;
			while (fcin.read(rBuffer) != -1) {
				int rSize = rBuffer.position();
				rBuffer.rewind();
				rBuffer.get(bs);
				rBuffer.clear();
				tempString = new String(bs, 0, rSize);
				int fromIndex = 0;
				int endIndex = 0;
				while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
					String line = tempString.substring(fromIndex, endIndex);
					line = strBuf.toString() + line;
					readAllLines.add(line);
					strBuf.delete(0, strBuf.length());
					fromIndex = endIndex + 1;
				}
				if (rSize > tempString.length())
					strBuf.append(tempString.substring(fromIndex, tempString.length()));
				else
					strBuf.append(tempString.substring(fromIndex, rSize));
			}
			System.err.print("read:" + (System.currentTimeMillis() - start));
			return readAllLines;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getAllLines() throws Exception {
		try {
			long start = System.currentTimeMillis();
			FileReader reader = new FileReader(new File(TestMain.NAME));
			BufferedReader br = new BufferedReader(reader);
			List<String> readAllLines = new ArrayList<String>();
			String line = null;
			while ((line = br.readLine()) != null)
				readAllLines.add(line);
			br.close();
			reader.close();
			System.out.println("read_time:" + (System.currentTimeMillis() - start) + "ms");
			return readAllLines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void checkWordCount() {
		try {
			long start = System.currentTimeMillis();
			String word = null;
			int count = 0;
			TreeMap<String, Integer> map = new TreeMap<String, Integer>();
			int totalCount = 0;
			String match = " |,|\\.|\\:|\\?|\\!|\"|'|\t";
			FileReader reader = new FileReader(new File(TestMain.NAME));
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			StringTokenizer token = null;
			while ((line = br.readLine()) != null) {
				token = new StringTokenizer(line, match);
				while (token.hasMoreTokens()) {
					word = token.nextToken().toLowerCase();
					if (word.matches("[0-9]*"))
						continue;
					totalCount++;
					if (map.get(word) == null)
						count = 1;
					else
						count = map.get(word) + 1;
					map.put(word, count);
				}
			}
			br.close();
			reader.close();
			List<Map<String, Integer>> sortByValue = sortByValue(map);

			long time = System.currentTimeMillis() - start;
			StringBuilder stringBuilder = new StringBuilder();
			String a = "\n";
			stringBuilder.append("程序执行：" + time + "ms").append(a).append("文章总单词数：" + totalCount).append(a).append("词频统计：").append(a);
			for (Map<String, Integer> map1 : sortByValue) {
				Set<String> keySet = map1.keySet();
				Iterator<String> iterator = keySet.iterator();
				while (iterator.hasNext()) {
					word = (String) iterator.next();
					count = map1.get(word);
					double percentTemp = (double) count / (double) totalCount * 100;
					double percent = sub(percentTemp, 1);
					stringBuilder.append(word + ":" + count + ", " + percent + "%").append(a);

				}
			}
			writeFile(TestMain.RESULT, stringBuilder.toString().getBytes());
			System.out.println("success-time:" + time + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double sub(double temp, int count) {
		BigDecimal bigDecimal = new BigDecimal(temp);
		return bigDecimal.setScale(count, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static void writeFile(String path, byte[] fileArray) throws Exception {
		try {
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();
			FileOutputStream foStream = new FileOutputStream(path);
			foStream.write(fileArray);
			foStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Map<String, Integer>> sortByValue(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
		Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				if (o2.getValue().intValue() == o1.getValue().intValue())
					return o1.getKey().compareTo(o2.getKey());
				return o2.getValue() - o1.getValue();
			}
		};
		Collections.sort(list, comparator);
		List<Map<String, Integer>> result = new LinkedList<Map<String, Integer>>();
		Map<String, Integer> map2 = null;
		for (Map.Entry<String, Integer> entry : list) {
			map2 = new HashMap<String, Integer>();
			map2.put(entry.getKey(), entry.getValue());
			result.add(map2);
		}
		return result;
	}

	public static Map<String, Integer> addMap(Map<String, Integer> map1, Map<String, Integer> map2) {
		Set<Entry<String, Integer>> entrySet = map2.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			Integer value1 = map1.get(key);
			if (value1 != null)
				map1.put(key, value + value1);
			else
				map1.put(key, value);
		}
		return map1;
	}

	public static void tongji(Map<String, Integer> map, int totalCount, long startTime) {
		int count = 0;
		String word = null;
		try {
			List<Map<String, Integer>> sortByValue = sortByValue(map);
			Set<Entry<String, Integer>> entrySet = null;
			long time = System.currentTimeMillis() - startTime;
			StringBuilder stringBuilder = new StringBuilder();
			String a = "\n";
			stringBuilder.append("程序执行：" + time + "ms").append(a).append("文章总单词数：" + totalCount).append(a).append("词频统计：").append(a);
			for (Map<String, Integer> map2 : sortByValue) {
				entrySet = map2.entrySet();
				for (Entry<String, Integer> entry : entrySet) {
					word = entry.getKey();
					count = entry.getValue();
					double percentTemp = (double) count / (double) totalCount * 100;
					double percent = sub(percentTemp, 1);
					stringBuilder.append(word + ":" + count + ", " + percent + "%").append(a);
				}
			}
			writeFile(TestMain.RESULT, stringBuilder.toString().getBytes());
			System.out.println("success-time:" + time + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void last(int count) {
		int totalCount = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (WordEntity entity : MainActor.list) {
			totalCount += entity.getCount();
			map = addMap(map, entity.getMap());
		}
		tongji(map, totalCount, MainActor.startTime);
	}

}
