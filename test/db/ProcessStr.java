package db;

import java.sql.*;
import java.util.*;

import util.StringUtil;
import bean.BrandDS;

public class ProcessStr {
	//�����ַ�
	private static Set<String> words = new HashSet<String>();
	//��ʼ�ַ�
	private static Set<String> first = new HashSet<String>();
	//��ֹ�ַ�
	private static Set<String> end = new HashSet<String>();
	//Ʒ���б�
	private static final Map<String, List<BrandDS>> brands = new TreeMap<String, List<BrandDS>>();
	//����Ʒ����
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
		
		String s = "���ݳ����������в�ʵ��̥";
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
			//�����ڵ���ֱ�����������ڵ��ּ���Ƿ���ڸô���
			len++;
			if(end.contains(tmp)) {
				List<BrandDS> li = brands.get(s.substring(index-len, index));
				if(li != null) {
					System.out.println("�ҵ�����");
					break outer;
				}
			}
			if(!first.contains(tmp)) 
				len = 0;
		}
	}

	public static void main(String[] args) {
		brands();
		
		String s = "��������";
//		String s = "�����������в�ʵ��̥";
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
			//�����ڵ���ֱ�����������ڵ��ּ���Ƿ���ڸô���
			len++;
			//�账��β�ַ�Ϊ�����������ַ������
			if(end.contains(tmp)) {
				String in = s.substring(index-len, index);
				List<BrandDS> li = brands.get(in);
				if (li ==null) {
					//����������ĸ��ǰ��������ϵ�磺������ �ȴʵ�ʶ��
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
					//�ҵ�����
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
