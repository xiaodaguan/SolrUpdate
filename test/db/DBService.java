package db;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Map.Entry;

import oracle.jdbc.OracleTypes;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bean.CombineData;
import system.Systemconfig;
import util.StringUtil;

public class DBService {
	private static Configuration conf = null;
	private static HTable ht;
	static HBaseAdmin admin;
	static HConnection connection = null;
	public static void main(String[] args) throws IOException, SQLException {
//		initAppCtx("");
		Scan scan = new Scan("0".getBytes(), "1".getBytes());
		//对于全局搜索汇总的电商，需要过滤
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("indexFlag"), Bytes.toBytes("global"),  
                CompareOp.EQUAL, Bytes.toBytes("false"));
		scan.setFilter(filter);
		ResultScanner rs = null;
		try {
			HTableInterface table = connection.getTable("product");
			rs = table.getScanner(scan);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int count = 0;
		for(Result r : rs) {
			Owner o = new Owner();
			Bean e = new Bean();
			
			try {
				byte[] b = r.getValue(Bytes.toBytes("title"), Bytes.toBytes("title"));
				if(b != null)
					e.title = (new String(b, "utf-8"));
				
				b = r.getValue(Bytes.toBytes("content"), Bytes.toBytes("content"));
				if(b != null)
					e.content = (new String(b, "utf-8"));
				
				b = r.getValue(Bytes.toBytes("brand"), Bytes.toBytes("brand"));
				if(b != null)
					e.brand = (new String(b, "utf-8"));

				b = r.getValue(Bytes.toBytes("categoryCode"), Bytes.toBytes("categoryCode"));
				if(b != null)
					e.category_code = (Bytes.toInt(b));
				
				b = r.getValue(Bytes.toBytes("imgs"), Bytes.toBytes("product"));
				if(b != null) {
					String[] arr = new String(b).split(";");
					List<String> l = new ArrayList<String>();
					for(String str : arr) {
						l.add(str);
					}
					e.product_img = (l.toString());
					e.product_img = e.product_img.substring(1, e.product_img.length()-1);
				}
				
				b = r.getValue(Bytes.toBytes("imgs"), Bytes.toBytes("info"));
				if(b != null) {
					String[] arr = new String(b).split(";");
					List<String> l = new ArrayList<String>();
					for(String str : arr) {
						l.add(str);
					}
					e.info_img = (l.toString());
					e.info_img = e.info_img.substring(1, e.info_img.length()-1);
				}
				
				b = r.getValue(Bytes.toBytes("inserttime"), Bytes.toBytes("inserttime"));
				if(b != null)
					e.insert_time = (new Timestamp(Bytes.toLong(b)));
				
				b = r.getValue(Bytes.toBytes("updateDate"), Bytes.toBytes("updateDate"));
				if(b != null)
				e.update_time = (new String(b, "utf-8"));
				
				b = r.getValue(Bytes.toBytes("md5"), Bytes.toBytes("md5"));
				if(b != null)
					e.md5 = (new String(b));
				
				b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("diameter"));
				if(b != null) {
					String str = StringUtil.extrator(new String(b), "\\d");
					if(!str.trim().equals("")) {
						e.diameter = str;
					}
				}
				
				b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("width"));
				if(b != null) {
					String str = StringUtil.extrator(new String(b), "\\d");
					if(!str.trim().equals(""))
						e.width = str;
				}
				b = r.getValue(Bytes.toBytes("searchKey"), Bytes.toBytes("searchKey"));
				if(b != null)
					e.search_keyword = (new String(b, "utf-8"));
				
				List<Double> l = new ArrayList<Double>();
				NavigableMap<byte[], byte[]> m = r.getFamilyMap(Bytes.toBytes("price"));
				for(Entry<byte[], byte[]> price : m.entrySet()) {
					double d = Double.parseDouble(new String(price.getValue()));
					l.add(d);
				}
				e.price = (l.toString());
				e.price = e.price.substring(1, e.price.length()-1);
				
				List<Integer> li = new ArrayList<Integer>();
				m = r.getFamilyMap(Bytes.toBytes("transation"));
				for(Entry<byte[], byte[]> sale : m.entrySet()) {
					li.add(Integer.parseInt(new String(sale.getValue())));
				}
				e.sale_num = (li.toString());
				e.sale_num = e.sale_num.substring(1, e.sale_num.length()-1);
				
				b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("model"));
				if(b != null) {
					e.model = (new String(b,"utf-8"));
				}
				
				
				b = r.getValue(Bytes.toBytes("params"), Bytes.toBytes("params"));
				if(b != null) {
					e.info = (new String(b,"utf-8"));
				}
				
				b = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("code"));
				if(b != null)
					e.code_num = (new String(b,"utf-8"));

				b = r.getValue(Bytes.toBytes("url"), Bytes.toBytes("url"));
				e.url = (new String(b));
				if(e.url.contains("taobao.com"))
					e.site_id = 50;
				else if(e.url.contains("jd.com"))
					e.site_id = 51;
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_name"));
				if(b != null)
					o.name = (new String(b,"utf-8"));
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_address"));
				if(b != null)
					o.address = (new String(b,"utf-8"));
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_company"));
				if(b != null)
					o.company = (new String(b,"utf-8"));
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_code"));
				if(b != null)
					o.code_num = (new String(b,"utf-8"));
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_pScore"));
				if(b != null)
					o.pscore = (new String(b,"utf-8"));
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_sScore"));
				if(b != null)
					o.sscore = (new String(b,"utf-8"));
				
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_score"));
				if(b != null)
					o.ascore = (new String(b,"utf-8"));
				b = r.getValue(Bytes.toBytes("owner"), Bytes.toBytes("owner_url"));
				if(b != null)
					o.url = (new String(b,"utf-8"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if(++count <= 15471) continue;
			e.owner = saveOwner(o);
			saveData(e);
		}
	}
	
	private static final String owner = "insert into eb_owner("
			+ "code_num,"
			+ "company,"
			+ "address,"
			+ "name,"
			+ "url,"
			+ "pscore,"
			+ "sscore,"
			+ "ascore,"
			+ "id) values(?,?,?,?,?,?,?,?,?)";
	private static final String SQL = "begin insert into eb_owner("
			+ "code_num,"
			+ "company,"
			+ "address,"
			+ "name,"
			+ "url,"
			+ "pscore,"
			+ "sscore,"
			+ "ascore) values(?,?,?,?,?,?,?,?) returning id into ?; END;";
	public static int saveOwner(final Owner b) throws SQLException {
//		PreparedStatement ps = conn.prepareStatement(owner, Statement.RETURN_GENERATED_KEYS);
//		ps.setString(1, b.code_num);
//		ps.setString(2, b.company);
//		ps.setString(3, b.address);
//		ps.setString(4, b.name);
//		ps.setString(5, b.url);
//		ps.setObject(6, b.pscore);
//		ps.setString(7, b.sscore);
//		ps.setString(8, b.ascore);
//		ps.executeUpdate();
		//mysql
//		ResultSet results = ps.getGeneratedKeys();
//        int num = -1;
//        if(results.next()) {
//            num = (int) results.getLong(9);
//        }
		
		CallableStatement ps = conn.prepareCall(SQL);
		ps.setString(1, b.code_num);
		ps.setString(2, b.company);
		ps.setString(3, b.address);
		ps.setString(4, b.name);
		ps.setString(5, b.url);
		ps.setObject(6, b.pscore);
		ps.setString(7, b.sscore);
		ps.setString(8, b.ascore);
		ps.registerOutParameter(9, OracleTypes.NUMBER);
//		ps.executeUpdate();
		ps.execute();
		int i = ps.getInt(9);
		ps.close();
		return i;
	}
	private static final String jasql = "insert into eb_data(" +
			"title, " +
			"brand," +
			"content," +
			"product_img," +
			"info_img," +
			"insert_time," +
			"diameter," +
			"width," +
			"price," +
			"sale_num," +
			"name," +
			"url," + 
			"info, " +
			"category_code,"
			+ "md5,"
			+ "search_keyword,"
			+ "site_id,"
			+ "update_time,"
			+ "owner,"
			+ "model,"
			+ "code_num) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static void saveData(final Bean b) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(jasql);
		ps.setString(1, b.title);
		ps.setString(2, b.brand);
		ps.setString(3, b.content);
		ps.setString(4, b.product_img);
		ps.setString(5, b.info_img);
		ps.setObject(6, b.insert_time);
		ps.setString(7, b.diameter);
		ps.setString(8, b.width);
		ps.setString(9, b.price);
		ps.setString(10, b.sale_num);
		ps.setString(11, b.name);
		ps.setString(12, b.url);
		ps.setString(13, b.info);		
		ps.setInt(14, b.category_code);
		ps.setString(15, b.md5);
		ps.setObject(16, b.search_keyword);
		ps.setInt(17, b.site_id);
		ps.setObject(18, b.update_time);
		ps.setInt(19, b.owner);
		ps.setString(20, b.model);
		ps.setString(21, b.code_num);
		ps.executeUpdate();
		ps.close();
	}
	
	public static void testCate() {
		CombineData data = new CombineData();
		data.setCategoryCode(23);
		Systemconfig.commonService.code(23, data);
		System.out.println(data.getCategory1());
		System.out.println(data.getCategory2());
	}
	
	public static ApplicationContext appCtx;
	public static void initAppCtx(String path) {
		PropertyConfigurator.configure(path + "config/log4j.properties"); 
		File[] files = new File(path+"config").listFiles();
		List<String> list = new ArrayList<String>();
		for (File file : files) {
			if (file.getName().startsWith("app")) {
				list.add(path + "config" + File.separator + file.getName());
			}
		}
		String[] arry = new String[list.size()];
		list.toArray(arry);
		appCtx = new FileSystemXmlApplicationContext(arry);
	}
	static Connection conn;
	static {
		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
		try {
			conn = DriverManager.getConnection(url, "tire", "tire2014");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
	}
}
