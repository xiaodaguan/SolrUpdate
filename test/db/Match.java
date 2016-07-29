package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Match {
	
	public static void process(String s) {
		int index = 0;
		int len = 0;
		outer:
		while(index < s.length()) {
			char c = s.charAt(index);
			String tmp = String.valueOf(c);
			index++;
			if(!words.contains(tmp)) {
				len = 0;
				continue;
			}
			//不存在的字直接跳过，存在的字检测是否存在该词项
			len++;
			//简单的字符匹配
//			if(end.contains(tmp)) {
//				String in = s.substring(index-len, index);
//				if(brands.containsKey(in)) break;
//				//处理多个首字母在前，包含关系如：朝建大 等词的识别
//				for(int i = 1;i < in.length();i++) {
//					len--;//优化，右移已经确定不存在的词中的字的偏移
//					if (brands.containsKey(in.substring(i))) {
//						System.out.println(in.substring(i));
//						break outer;
//					}
//				}
//			}
//			//需特殊处理的字符匹配
			if(end.contains(tmp)) {
				String in = s.substring(index-len, index);
				List<BrandDS> li = brands.get(in);
				if (li ==null) {
					//处理多个首字母在前，包含关系如：朝建大 等词的识别
					for(int i = 1;i < in.length();i++) {
						li = brands.get(in.substring(i));
						len--;//优化，右移已经确定不存在的词中的字的偏移
						if(li != null) {
							break;
						}
					}
				}
				if(li != null) {
					//找到词项后处理为标准名称
					for(BrandDS bds : li) {
						if(bds.master) {
							System.out.println(bds.name);
							break outer;
						}
					}
				}
			}
			if(!first.contains(tmp)) 
				len = 0;
		}
	}
	public static void main(String[] args) {
		brands();
		String s = "朝建大阳建中策实心胎";
		process(s);
	}
	
	//所有字符
	private static Set<String> words = new HashSet<String>();
	//起始字符
	private static Set<String> first = new HashSet<String>();
	//终止字符
	private static Set<String> end = new HashSet<String>();
	//品牌列表
	private static final Map<String, List<BrandDS>> brands = new HashMap<String, List<BrandDS>>();
	//处理品牌名
	public static void brands() {
		List<String> con = contentList("brand", "utf-8");
		for(String str : con) {
			String arr[] = str.split(",");
			List<BrandDS> list = new ArrayList<BrandDS>();
			for(int i = 0;i < arr.length;i++) {
				for(int j = 0;j < arr[i].length();j++) {
					char c = arr[i].charAt(j);
					if(j ==0)
						first.add(String.valueOf(c));
					else if(j == arr[i].length() - 1)
						end.add(String.valueOf(c));
					words.add(String.valueOf(c));
				}
				BrandDS b = new Match().new BrandDS(arr[i], i == 0 ? true : false);
				list.add(b);
				brands.put(arr[i], list);
			}
		}
	}
	
	private static List<String> contentList(String filename, String encode) {
		String s = "";
		List<String> sb = new ArrayList<String>();
		java.io.BufferedReader br = null;
		try {
			InputStream in = new FileInputStream(filename);
			br = new java.io.BufferedReader(new java.io.InputStreamReader(in , encode));
			while((s=br.readLine())!=null) 
				sb.add(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb;
	}
	
	public class BrandDS {

		public String name;//名称
		public boolean master;//是否为标准名称
		
		public BrandDS(String name) {
			this(name, false);
		}
		public BrandDS(String name, boolean master) {
			this.name = name;
			this.master = master;
		}
	}
	
}
