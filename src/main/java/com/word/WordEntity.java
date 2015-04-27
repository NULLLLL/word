package com.word;

import java.util.HashMap;
import java.util.Map;

public class WordEntity {

	private int count = 0;

	private Map<String, Integer> map = new HashMap<String, Integer>();

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public WordEntity(byte[] lines, int start, int end, int flag) {
		CharTreeNode root = createTree(lines, start, end, flag);
		Map<String, Integer> map = new HashMap<String, Integer>();
		getWordCountFromCharTree(map, root);
		this.map = map;
	}

	private CharTreeNode createTree(byte[] result, int begin, int end, int flag) {
		int length = result.length;
		CharTreeNode root = new CharTreeNode();
		CharTreeNode p = root;
		byte c = 0;
		byte c2 = 0;
		if (flag == 1) {
			c = result[begin - 1];
			c2 = result[begin];
			if (((c2 >= 'a' && c2 <= 'z') || (c2 >= 'A' && c2 <= 'Z')) && (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))) {
				for (int i = begin + 1; i < begin + 15; i++) {
					c = result[i];
					if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == 39)
						continue;
					else {
						c2 = result[i + 1];
						if ((c2 >= 'a' && c2 <= 'z') || (c2 >= 'A' && c2 <= 'Z')) {
							begin = i + 1;
							break;
						}
					}
				}
			}
		}
		for (int i = begin; i < end; i++) {
			c = result[i];
			if (c == 39)
				continue;
			if (c >= 'A' && c <= 'Z')
				c = (byte) (c + 'a' - 'A');
			if (c >= 'a' && c <= 'z') {
				if (p.children[c - 'a'] == null)
					p.children[c - 'a'] = new CharTreeNode();
				p = p.children[c - 'a'];
			} else {
				if (i == end - 1 || (i < end - 1 && ((result[i + 1] >= 97 && result[i + 1] <= 122) || (result[i + 1] >= 65 && result[i + 1] <= 90))))
					this.count++;
				p.cnt++;
				p = root;
			}
		}
		flag = 0;
		if (flag == 0 && end != length) {
			c2 = result[end];
			if (p != root && (((c2 >= 'a' && c2 <= 'z') || (c2 >= 'A' && c2 <= 'Z')))) {
				for (int i = end; i < end + 15; i++) {
					c = result[i];
					if (c == 39)
						continue;
					if (c >= 'A' && c <= 'Z')
						c = (byte) (c + 'a' - 'A');
					if (c >= 'a' && c <= 'z') {
						if (p.children[c - 'a'] == null)
							p.children[c - 'a'] = new CharTreeNode();
						p = p.children[c - 'a'];
					} else {
						this.count++;
						p.cnt++;
						p = root;
						break;
					}
				}
			}
		}
		if (c >= 'a' && c <= 'z')
			p.cnt++;
		return root;
	}

	private void getWordCountFromCharTree(Map<String, Integer> result, CharTreeNode p) {
		getWordCountFromCharTree(result, p, new char[100], 0);
	}

	private void getWordCountFromCharTree(Map<String, Integer> result, CharTreeNode p, char[] buffer, int length) {
		for (int i = 0; i < 26; ++i) {
			if (p.children[i] != null) {
				buffer[length] = (char) (i + 'a');
				if (p.children[i].cnt > 0)
					result.put(String.valueOf(buffer, 0, length + 1), p.children[i].cnt);
				getWordCountFromCharTree(result, p.children[i], buffer, length + 1);
			}
		}
	}

	public Map<String, Integer> getMap() {
		return map;
	}

	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}

}
