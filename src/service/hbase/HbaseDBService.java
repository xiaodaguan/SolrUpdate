package service.hbase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.jdbc.core.RowMapper;


import bean.BrandDS;
import bean.CombineData;
import bean.CommentData;
import bean.DataType;
import bean.EBData;
import bean.Indexable;
import service.AbstractDBService;
import system.Systemconfig;
import util.StringUtil;

public class HbaseDBService extends AbstractDBService {
	private static Configuration conf = null;
	private static HTable ht;
	static HBaseAdmin admin;
	static HConnection connection = null;
	private static Map<Integer, String> map = new HashMap<Integer, String>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	//字集合
	private static Set<String> words = new HashSet<String>();
	//起始字符
	private static Set<String> first = new HashSet<String>();
	//终止字符
	private static Set<String> end = new HashSet<String>();
	private static final Map<String, List<BrandDS>> brands = new TreeMap<String, List<BrandDS>>();
	static {
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.10.94");
		HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
		conf = HBaseConfiguration.create(HBASE_CONFIG);
		try {
			connection = HConnectionManager.createConnection(conf);
			admin = new HBaseAdmin(connection);
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		}
		fileBrands();
	}
	
	public static HConnection getConnection() {
		return connection;
	}
	
	private static String sql = "select name, alias from eb_brand where type=0";
	/**
	 * 得到品牌名及其别名列表
	 */
	public void brands() {
		jdbcTemplate.query(sql , new RowMapper<Object>(){
			String name = null;
			String alias = null;
			@Override
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				List<BrandDS> list = new ArrayList<BrandDS>();
				name = rs.getString(1);
				list.add(new BrandDS(name, true));
				brands.put(name.toUpperCase(), list);
				
				alias = rs.getString(2);
				if(alias != null) {
					String arr[] = alias.split(",");
					for(String a : arr) {
						list.add(new BrandDS(a));
						brands.put(a.toUpperCase(), list);
						
						for(int j = 0;j < a.length();j++) {
							char c =a.charAt(j);
							if(j ==0)
								first.add(String.valueOf(c));
							else if(j == a.length() - 1)
								end.add(String.valueOf(c));
							words.add(String.valueOf(c));
						}
					}
				}
				
				for(int j = 0;j < name.length();j++) {
					char c =name.charAt(j);
					if(j ==0)
						first.add(String.valueOf(c));
					else if(j == name.length() - 1)
						end.add(String.valueOf(c));
					words.add(String.valueOf(c));
				}
				
				return null;
			}
		});
	}
	/**
	 * 通过文件处理品牌名
	 */
	private static void fileBrands() {
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
	
	private static final String product = "product";
	@Override
	public int getDatas(List<Indexable> list, DataType type, int start, int len) {
		Scan scan = new Scan("0".getBytes(), "1".getBytes());
		//对于全局搜索汇总的电商，需要过滤
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("indexFlag"), Bytes.toBytes("global"),  
                CompareOp.EQUAL, Bytes.toBytes("false"));
		scan.setFilter(filter);
		try {
			HTableInterface table = connection.getTable(product);
			ResultScanner rs = table.getScanner(scan);
			eb(rs, list, len);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
//	private void combine(ResultScanner rs, List<Indexable> list, int len) {
//		int count = 0;
//		for(Result r : rs) {
//			if(++count > len) break;
//			attrSet(r, list);
//		}
//	}
	private void eb(ResultScanner rs, List<Indexable> list, int len) throws IOException {
		int count = 0;
		int filter = 0;
		for(Result r : rs) {
			if(++count > len) break;
			if(!ebAttrSet(r, list)) {
				filter++;
				System.out.println(new String(r.getValue(Bytes.toBytes("title"), Bytes.toBytes("title")), "utf-8"));
			}
		}
		System.out.println(count+":"+filter);
		try {
			TimeUnit.SECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取全局搜索电商数据
	 * @param rs
	 * @param list
	 */
	private void attrSet(Result r, List<Indexable> list) {
		CombineData e = new CombineData();
		try {
			byte[] b = r.getValue(Bytes.toBytes("categoryCode"), Bytes.toBytes("categoryCode"));
			if(b != null) {
				e.setCategoryCode(Bytes.toInt(b));
			}
			
			b = r.getValue(Bytes.toBytes("content"), Bytes.toBytes("content"));
			if(b != null)
				e.setContent(new String(b, "utf-8"));
			
			b = r.getValue(Bytes.toBytes("imgs"), Bytes.toBytes("product"));
			if(b != null)
				e.setImgUrl(new String(b));

			b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_name"));
			if(b != null)
				e.setAuthor(new String(b));
			
			b = r.getValue(Bytes.toBytes("inserttime"), Bytes.toBytes("inserttime"));
			if(b != null)
				e.setInsertTime(new Timestamp(Bytes.toLong(b)));
			
			b = r.getValue(Bytes.toBytes("md5"), Bytes.toBytes("md5"));
			if(b != null)
				e.setMd5(new String(b));
			
			b = r.getValue(Bytes.toBytes("title"), Bytes.toBytes("title"));
			if(b != null)
				e.setTitle(new String(b, "utf-8"));

			NavigableMap<byte[], byte[]> m = r.getFamilyMap(Bytes.toBytes("transation"));
			for(Entry<byte[], byte[]> price : m.entrySet()) {
				e.setCommCount(Integer.parseInt(new String(price.getValue())));
				break;
			}
			
			b = r.getValue(Bytes.toBytes("searchKey"), Bytes.toBytes("searchKey"));
			if(b != null)
				e.setSearchKeyword(new String(b, "utf-8"));
			
//			b = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("pubtime"));
//			if(b != null) {
//				String s = new String(b, "utf-8").trim();
//				if(!s.equals(""))
//					e.setPubdate(sdf.parse(s.replace("上架时间：", "")));
//			}
//			
			b = r.getValue(Bytes.toBytes("url"), Bytes.toBytes("url"));
			e.setUrl(new String(b));
			if(e.getUrl().contains("taobao.com"))
				e.setSource("淘宝");
			else if(e.getUrl().contains("jd.com"))
				e.setSource("京东");
			e.setMedia(7);
			
			if(map.containsKey(e.getCategoryCode())) {
				String[] s = map.get(e.getCategoryCode()).split(",");
				e.setCategory1(Integer.parseInt(s[0]));
				e.setCategory2(Integer.parseInt(s[1]));
				e.setCategory3(Integer.parseInt(s[2]));
			} else {
				Systemconfig.commonService.code(e.getCategoryCode(), e);
				map.put(e.getCategoryCode(), e.getCategory1()+","+e.getCategory2()+","+e.getCategory3());
			}
			list.add(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	private static final String[] FILTER = {"米其林", "马牌", "固特异", "邓禄普", "普利司通","赛轮", "三角", "回力", "双钱", "金宇", "玲珑", "中策", "其他/Other"};
	public static Set<String> set = new HashSet<String>();
	private boolean ebAttrSet(Result r, List<Indexable> list) {
		EBData e = new EBData();
		try {
			set.add(Bytes.toString(r.getRow()));
			byte[] b = r.getValue(Bytes.toBytes("title"), Bytes.toBytes("title"));
			if(b != null)
				e.setTitle(new String(b, "utf-8"));
			
			if(!e.getTitle().contains("轮胎")) return false;
			
			b = r.getValue(Bytes.toBytes("content"), Bytes.toBytes("content"));
			if(b != null)
				e.setContent(new String(b, "utf-8"));
			
//			boolean has = false;
//			for(String s : FILTER) {
//				if(e.getTitle().contains(s) || e.getContent().contains(s)) {
//					has = true;
//					break;
//				}
//			}
//			if(!has) return false;
			
			b = r.getValue(Bytes.toBytes("brand"), Bytes.toBytes("brand"));
			if(b != null)
				e.setBrand(new String(b, "utf-8"));
			else
				e.setBrand("其他");
			//处理品牌名,首先去除所有空格和数字,没有brand的从标题抽取
			e.setBrand(e.getBrand().replace(" ", "").replaceAll("\\d", "").toUpperCase());
			if(e.getBrand().trim().equals("")) {
				int index = e.getTitle().indexOf("轮胎");
				if(index > -1) {
					String s = "其他";
					if(index > 4) {
						//向前平移4个字符，可覆盖中文情况，后面可判断字符中英文决定移动多少字符
						s = e.getTitle().substring(index-4, index);
					}
					e.setBrand(s);
				} else {
					String s = StringUtil.regMatcher(e.getTitle(), "（", "）"); 
					if(s == null) {
						s = "其他";
					}
					e.setBrand(s);
				}
			} 
			
			String s = e.getBrand();
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
				len++;
//				//需特殊处理的字符匹配
				if(end.contains(tmp)) {
					String in = s.substring(index-len, index);
					List<BrandDS> li = brands.get(in);
					if (li ==null) {
						for(int i = 1;i < in.length();i++) {
							li = brands.get(in.substring(i));
							len--;
							if(li != null) {
								break;
							}
						}
					}
					if(li != null) {
						for(BrandDS bds : li) {
							if(bds.master) {
								e.setBrand(bds.name);
								break outer;
							}
						}
					} 
				}
				if(!first.contains(tmp)) 
					len = 0;
			}
			if(e.getBrand() == null || e.getBrand().contains("其他")) e.setBrand("其他");
//			boolean flag = false;
//			for(String ss : FILTER) {
//				if(e.getBrand().contains(ss)) {
//					flag = true;
//					break;
//				}
//			}
//			if(!flag) return false;
			
			b = r.getValue(Bytes.toBytes("categoryCode"), Bytes.toBytes("categoryCode"));
			if(b != null)
				e.setCategoryCode(Bytes.toInt(b));
			
			b = r.getValue(Bytes.toBytes("imgs"), Bytes.toBytes("product"));
			if(b != null) {
				String[] arr = new String(b).split(";");
				List<String> l = new ArrayList<String>();
				for(String str : arr) {
					l.add(str);
				}
				e.setImgUrl(l);
			}
			
			b = r.getValue(Bytes.toBytes("imgs"), Bytes.toBytes("info"));
			if(b != null) {
				String[] arr = new String(b).split(";");
				List<String> l = new ArrayList<String>();
				for(String str : arr) {
					l.add(str);
				}
				e.setInfoImgUrl(l);
			}
			
			b = r.getValue(Bytes.toBytes("inserttime"), Bytes.toBytes("inserttime"));
			if(b != null)
				e.setInsertTime(new Timestamp(Bytes.toLong(b)));
			e.setUpdateTime(e.getInsertTime());
			
			b = r.getValue(Bytes.toBytes("md5"), Bytes.toBytes("md5"));
			if(b != null)
				e.setMd5(new String(b));
			
			b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("diameter"));
			if(b != null) {
				String str = StringUtil.extrator(new String(b), "\\d");
				if(!str.trim().equals("")) {
					e.setDiameter(Double.valueOf(str));
				}
			}
			
			b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("width"));
			if(b != null) {
				String str = StringUtil.extrator(new String(b), "\\d");
				if(!str.trim().equals(""))
					e.setWidth(Double.valueOf(str));
//				else
//					e.setWidth(0);
			}
			if(e.getWidth() < 155 || e.getWidth() > 275)
				return false;
			
			b = r.getValue(Bytes.toBytes("searchKey"), Bytes.toBytes("searchKey"));
			if(b != null)
				e.setSearchKeyword(new String(b, "utf-8"));
			
			List<Double> l = new ArrayList<Double>();
			NavigableMap<byte[], byte[]> m = r.getFamilyMap(Bytes.toBytes("price"));
			for(Entry<byte[], byte[]> price : m.entrySet()) {
				double d = Double.parseDouble(new String(price.getValue()));
				if(d < 100) return false;
				l.add(d);
			}
			e.setPrice(l);
			if(l.size() > 0)
				e.setPriceSingle(l.get(l.size() - 1));
			
			List<Integer> li = new ArrayList<Integer>();
			m = r.getFamilyMap(Bytes.toBytes("transation"));
			for(Entry<byte[], byte[]> sale : m.entrySet()) {
				li.add(Integer.parseInt(new String(sale.getValue())));
			}
			e.setSaleNum(li);
			if(li.size() > 0)
				e.setSaleNumSingle(li.get(li.size() - 1));
			
//			b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("model"));
//			if(b != null) {
//				e.setModel(new String(b,"utf-8"));
//				e.getModel().replace("颜色分类", "").replace("规格", "").replace("轮胎", "");
//			}
			
			
			b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("params"));
			if(b != null) {
				String model = StringUtil.regMatcher(new String(b,"utf-8"), "型号:", "[轮颜规]");
				if(model == null)
					e.setModel(new String(b,"utf-8"));
				else
					e.setModel(model.trim());
			}
			
			
			b = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("code"));
			if(b != null)
				e.setId(new String(b,"utf-8"));
//			b = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("pubtime"));
//			if(b != null) {
//				String s = new String(b, "utf-8").trim();
//				if(!s.equals(""))
//					e.setPubdate(sdf.parse(s.replace("上架时间：", "")));
//			}
			
			b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_name"));
			if(b != null)
				e.setOwner(new String(b,"utf-8"));
			
			b = r.getValue(Bytes.toBytes("url"), Bytes.toBytes("url"));
			e.setUrl(new String(b));
			if(e.getUrl().contains("taobao.com"))
				e.setSite("淘宝");
			else if(e.getUrl().contains("jd.com"))
				e.setSite("京东");
			list.add(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return true;
	}

	public void updateRecords() throws IOException {
		for(String s : set) 
			updateRecord(s, "true");
		set.clear();
	}
	public void resetRecords() throws IOException {
		Scan scan = new Scan();
		HTable table = null;
		try {
			table = new HTable(product.getBytes(), connection);
			ResultScanner rs = table.getScanner(scan);
			for(Result r : rs) {
				updateRecord(Bytes.toString(r.getRow()), "false");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			table.close();
		}
	}
	
	private void updateRecord(String id, String value) throws IOException {
		ht = new HTable(product.getBytes(), connection);
		Put put = new Put(Bytes.toBytes(id));
		put.add(Bytes.toBytes("indexFlag"), Bytes.toBytes("global"), Bytes.toBytes(value));
		ht.put(put);
		ht.close();
	}
	
	public void getComments(List<Indexable> list,  int len) throws IOException {
		Scan scan = new Scan("1_".getBytes());
		//对于全局搜索汇总的电商，需要过滤
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("indexFlag"), Bytes.toBytes("globle"),  
                CompareOp.EQUAL, Bytes.toBytes("false"));
		scan.setFilter(filter);
		ResultScanner rs = null;
		try {
			HTableInterface table = connection.getTable(product);
			rs = table.getScanner(scan);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Result r : rs) {
			CommentData e = new CommentData();
			
			String s = Bytes.toString(r.getRow());
			if(s == null || s.equals("null")) continue;
			set.add(s);
			try {
				byte[] b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_info"));
				if(b != null) {
					e.setInfo(Bytes.toString(b));
				}
				
				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_id"));
				if(b != null)
					e.setCid(Bytes.toString(b));
				if(e.getCid() == null) continue;
				
				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_person"));
				if(b != null)
					e.setPerson(Bytes.toString(b));

				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_level"));
				if(b != null)
					e.setLevel(Bytes.toString(b));
				
				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_product"));
				if(b != null)
					e.setProduct(Bytes.toString(b));
				
				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_pubtime"));
				if(b != null) {
					String str = Bytes.toString(b);
					try {
						e.setPubdate(sdf1.parse(str));
					} catch(Exception e1) {
						e.setPubdate(null);
					}
				}
				
				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_score"));
				if(b != null)
					e.setScore(Bytes.toString(b));

				b = r.getValue(Bytes.toBytes("comments"), Bytes.toBytes("comment_label"));
				if(b != null) {
					List<String> l = new ArrayList<String>();
					l.add(Bytes.toString(b));
					e.setLabel(l);
				}
				e.setId(s);
				list.add(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void updateCommentRecords() throws IOException {
		for(String s : set) 
			updateCommentRecord(s, "true");
		set.clear();
	}
	public void resetCommentRecords() throws IOException {
		Scan scan = new Scan("1".getBytes());
		HTable table = null;
		try {
			table = new HTable(product.getBytes(), connection);
			ResultScanner rs = table.getScanner(scan);
			for(Result r : rs) {
				updateCommentRecord(Bytes.toString(r.getRow()), "false");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			table.close();
		}
	}
	
	private void updateCommentRecord(String id, String value) throws IOException {
		ht = new HTable(product.getBytes(), connection);
		Put put = new Put(Bytes.toBytes(id));
		put.add(Bytes.toBytes("indexFlag"), Bytes.toBytes("globle"), Bytes.toBytes(value));
		ht.put(put);
		ht.close();
	}
	@Override
	public int getEbDatas(List<Indexable> list, DataType type, int start,
			int len) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getEbCommentDatas(List<Indexable> list, DataType type, int max, int size) {
		// TODO Auto-generated method stub
		return 0;
	}
}
//boolean find = false;
//StringBuffer sb = new StringBuffer();
//for(int i = 0;i < e.getBrand().length();i++) {
//	char c = e.getBrand().charAt(i);
//	//添加字符到字符串直到不存在相关字符
//	if(words.contains(String.valueOf(c))) {
//		sb.append(c);
//	} else {
//		List<BrandDS> li = brands.get(sb.toString());
//		if(li == null) {
//			sb.delete(0, sb.toString().length());
//			continue;
//		}
//		//找到后设置品牌
//		for(BrandDS bds : li) {
//			if(bds.master) {
//				e.setBrand(bds.name);
//				find = true;
//				all.append("字符匹配到的品牌：").append(e.getBrand()).append(",").append(sb.toString()).append("\n");
//				break;
//			}
//		}
//	}
//}
//if(!find) {
//	List<BrandDS> bl = brands.get(sb.toString());
//	if(bl != null) {
//		for(BrandDS bds : bl) {
//			if(bds.master) {
//				e.setBrand(bds.name);
//				all.append("字符匹配到的品牌：").append(e.getBrand()).append(",").append(sb.toString()).append("\n");
//				break;
//			}
//		}
//	} else {
//		e.setBrand("其他");
//		all.append("没找到的品牌：").append(sb.toString()).append("\n");
//	}
//}