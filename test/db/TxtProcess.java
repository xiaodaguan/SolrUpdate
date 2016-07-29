package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.solr.client.solrj.beans.Field;

import util.StringUtil;

public class TxtProcess {

	class DB {
		 String id;
		
		 String person;//评论人
		
		 String level;//评论人级别
		
		 String info;//评论内容
		
		 Date pubdate;//评论时间
		
		 String label;//标签
		
		 String product;//
		
		 String score;//评分
		
		 String cid;//评论id
		 int did;
	}
	public static void main(String[] args) throws SQLException {
//		processMd5();
//		deleteReduplicationUrls();
		String sql = "select count(md5) from eb_comment";
		PreparedStatement ps  = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.next();
		System.out.println(rs.getObject(1));
		
//		Map<String, DB> set = new HashMap<String, DB>();
//		String sql = "select id, info, label, lv, person, product_code, product_title, "
//				+ "pubtime, score, inserttime, cid, searchkey, product_url, md5 from eb_comment";
//		PreparedStatement ps  = con.prepareStatement(sql);
//		ResultSet rs = ps.executeQuery();
//		while(rs.next()) {
//			DB db = new DB();
//			db.did=id
//			set.put(rs.getString(1), db);
//		}
//		for(String )
	}
	public static void deleteReduplicationUrls() throws SQLException {
		Set<String> set = new HashSet<String>();
		List<String> list = new ArrayList<String>();
		String sql = "select md5 from eb_comment";
		PreparedStatement ps  = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			if(set.contains(rs.getString(1)))
				list.add(rs.getString(1));
			set.add(rs.getString(1));
		}
		set.clear();
		
		String md5 = "select id from eb_comment where md5=?";
		String DELETE_SQL = "delete from eb_comment where id=?";
		Iterator<String> urlIter = list.iterator();
		while (urlIter.hasNext()) {
			String url = urlIter.next();
			synchronized (list) {
				urlIter.remove();
			}
			PreparedStatement pps = con.prepareStatement(md5);
			pps.setString(1, url);
			ResultSet rrs = pps.executeQuery();
			
			PreparedStatement delps = con.prepareStatement(DELETE_SQL);
			rs.next();
			while(rrs.next()) {
				delps.setInt(1, rrs.getInt(1));
				delps.executeUpdate();
			}
		}
	}
	public static void processMd5() throws SQLException {
		String sql = "select product_code, person, pubtime, id from eb_comment";
		PreparedStatement ps  = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		String up = "update eb_comment set md5=? where id=?";
		PreparedStatement pps = con.prepareStatement(up );
		while(rs.next()) {
			pps.setString(1, MD5Util.MD5(rs.getString(1)+rs.getString(2)+rs.getString(3)));
			pps.setInt(2, rs.getInt(4));
			pps.executeUpdate();
		}
	}
	
	public static void processBrand() throws SQLException {
		List<String> cont = StringUtil.contentList("brand");
//		Map<String, String> map = new TreeMap<String, String>();
//		Set<String> set = new HashSet<String>();
		String sql = "insert into eb_brand(name, en_name, alias) values(?,?,?)";
		PreparedStatement ps  = con.prepareStatement(sql);
		for(String s : cont) {
			String arr[] = s.split("/");
			String en_name = null;
			String name = null;
			String alias = null;
			if(arr.length > 1) {
				en_name = arr[0];
				name = arr[1];
			} else {
				name = arr[0];
			}
			if(en_name != null && en_name.indexOf(",") > -1) {
				alias = "";
				if(en_name.indexOf(",") > -1) {
					alias += en_name.substring(en_name.indexOf(",")+1);
					en_name = en_name.substring(0, en_name.indexOf(","));
				}
			}
			if(alias != null)
				alias += ",";
			else
				alias = "";
			if(name.indexOf(",") > -1) {
				alias += name.substring(name.indexOf(",")+1);
				name = name.substring(0, name.indexOf(","));
			}
			
			ps.setString(1, name);
			ps.setString(2, en_name);
			ps.setString(3, alias);
			ps.executeUpdate();
		}
	}
	
	static Connection con;
	static {
		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
		try {
			con = DriverManager.getConnection(url, "tire", "tire2014");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
