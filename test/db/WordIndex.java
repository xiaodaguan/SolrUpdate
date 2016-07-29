package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import util.StringUtil;

public class WordIndex {

	public static void main(String[] args) throws Exception {
		getData();
//		proBrand();
//		String s  ="品牌: GOOD YEAR/固特异 型号: 安节轮 235/50R18 97V 轮胎规格: 235/50R18 汽车轮胎速度级别: V 负荷指数: 97 轮辋直径: 18英寸 服务内容: 支持安装 胎面宽度: 235mm";
//		System.out.println( StringUtil.regMatcher(s, "型号:", ":").replace("轮胎", "").replace("规格", "").replace("颜色分类", ""));
	}
	
	private static void proBrand() throws SQLException {
		String sql = "select brand, id, info from eb_data where brand is not null";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		String up = "update eb_data set brand_full=?, brand=? where id=?";
		while(rs.next()) {
			String brand = rs.getString(1);
			String[] arr = brand.split("/");
			String real = arr[0];
			if(arr.length > 1) {
				real = arr[1];
			}
			PreparedStatement pps = conn.prepareStatement(up);
			pps.setString(1, brand);
			pps.setString(2, real);
			pps.setInt(3, rs.getInt(2));
			pps.executeUpdate();
			pps.close();
		}
	}
	
	private static void getData() throws SQLException {
		String sql = "select info, brand,model, id from eb_data where info is not null";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		String upd = "update eb_data set diameter=?, width=?,brand=?,model=? where id=?";
				
		while(rs.next()) {
			String info = rs.getString(1);
			if(info == null) {
				System.out.println(rs.getInt(4));
				continue;
			}
			String brand = rs.getString(2);
			if(brand == null) {
				brand = StringUtil.regMatcher(info, "品牌:", "(型号)?:");
				if(brand==null) continue;
			}
			if(brand.contains("型号"))
				brand = brand.split("型号")[0];
			String dia = StringUtil.regMatcher(info, "直径:", "(英)?寸");
			if(dia != null) dia = dia.trim();
			String wid = StringUtil.regMatcher(info, "宽度:", "(m)?", false);
			if(wid != null) wid = wid.replace("mm", "").trim();
			
			String model = rs.getString(3);
			String extractor =  StringUtil.regMatcher(info, "型号:", ":");
			if(extractor!=null) extractor = extractor.replace("轮胎", "").replace("规格", "").replace("颜色分类", "").trim();
			if(model != null && !model.equals(extractor))
				model = extractor;
			
			if(model != null && model.length()>40) {
				System.out.println(rs.getInt(4)+":"+model);
			}
			PreparedStatement pps = conn.prepareStatement(upd);
			pps.setString(1, dia);
			pps.setString(2, wid);
			pps.setString(3, brand);
			pps.setString(4, model);
			pps.setInt(5, rs.getInt(4));
			pps.executeUpdate();
			pps.close();
		}
	}
	static Connection conn;
	static {
		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
		try {
			conn = DriverManager.getConnection(url, "tire", "tire2014");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
