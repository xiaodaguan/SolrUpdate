package db;

import java.sql.*;
import java.util.*;

import util.StringUtil;
import bean.BrandDS;

public class ProcessStr {
	//所有字符
	private static Set<String> words = new HashSet<String>();
	//起始字符
	private static Set<String> first = new HashSet<String>();
	//终止字符
	private static Set<String> end = new HashSet<String>();
	//品牌列表
	private static final Map<String, List<BrandDS>> brands = new TreeMap<String, List<BrandDS>>();
	//处理品牌名
	public static void brands() {
		List<String> con = StringUtil.contentList("brand", "utf-8");
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
				BrandDS b = new BrandDS(arr[i], i == 0 ? true : false);
				list.add(b);
				brands.put(arr[i], list);
			}
		}
	}
	
	public static void main() {
		brands();
		
		String s = "杭州朝建大阳建中策实心胎";
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
			if(end.contains(tmp)) {
				List<BrandDS> li = brands.get(s.substring(index-len, index));
				if(li != null) {
					System.out.println("找到词项");
					break outer;
				}
			}
			if(!first.contains(tmp)) 
				len = 0;
		}
	}

	public static void main(String[] args) {
		brands();
		
		String s = "朝建阳大";
//		String s = "朝建大阳建中策实心胎";
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
			//需处理尾字符为其他词语首字符的情况
			if(end.contains(tmp)) {
				String in = s.substring(index-len, index);
				List<BrandDS> li = brands.get(in);
				if (li ==null) {
					//处理多个首字母在前，包含关系如：朝建大 等词的识别
					for(int i = 1;i < in.length();i++) {
						li = brands.get(in.substring(i));
						if(li != null) {
							break;
						} else {
							len--;
						}
					}
				}
				if(li != null) {
					//找到词项
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
	
//	static Connection con;
//	static {
//		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
//		try {
//			con = DriverManager.getConnection(url, "tire", "tire2014");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		words.addAll(StringUtil.contentList("brandword", "utf-8"));
//	}
//	public static void brands() throws SQLException {
//	String sql = "select name, alias from eb_brand where type=0";
//	PreparedStatement ps = con.prepareStatement(sql);
//	ResultSet rs = ps.executeQuery();
//	while(rs.next()) {
//		String name = null;
//		String alias = null;
//		List<BrandDS> list = new ArrayList<BrandDS>();
//		name = rs.getString(1);
//		list.add(new BrandDS(name, true));
//		brands.put(name.toUpperCase(), list);
//		
//		alias = rs.getString(2);
//		if(alias != null) {
//			String arr[] = alias.split(",");
//			for(String a : arr) {
//				list.add(new BrandDS(a));
//				brands.put(a.toUpperCase(), list);
//			}
//		}
//	}
//}
}
