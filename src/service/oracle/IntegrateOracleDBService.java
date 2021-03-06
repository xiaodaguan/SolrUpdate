package service.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import bean.CombineData;
import bean.CommentData;
import bean.DataType;
import bean.EBData;
import bean.Indexable;
import service.AbstractDBService;
import util.StringUtil;
//.*(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%]).*
public class IntegrateOracleDBService extends AbstractDBService {
	
	//list, type, max, SIZE
	public int getEbCommentDatas(final List<Indexable> list, final DataType type, int start, int len) {
		String sql = "select id, info, label, lv, person, product_code, product_title, "
				+ "pubtime, score, cid, md5 from (select "
				+ "A.id, A.info, A.label, A.lv, A.person, A.product_code, A.product_title, "
				+ "A.pubtime, A.score, A.cid, A.md5, rownum rn from ("
				+ "select id, info, label, lv, person, product_code, product_title, "
				+ "pubtime, score, cid, md5 from eb_comment where id > "
				+ start+" order by id) A where rownum<="+len+") where rn > 0";
		//从id>start开始的len条数据
		final List<Integer> max = new ArrayList<Integer>();
		max.add(0, start);
		
		list.addAll(this.jdbcTemplate.query(sql, new RowMapper<CommentData>(){
			@Override
			public CommentData mapRow(ResultSet rs, int arg1)
					throws SQLException {
				CommentData data = new CommentData();
				data.setDid(rs.getInt(1));
				data.setInfo(rs.getString(2));
				List<String> labelList = new ArrayList<String>();
				String label = rs.getString(3);
				if(label != null) {
					String[] arr = label.split(",");
					for(String a : arr) 
						labelList.add(a);
					data.setLabel(labelList);
				}
				data.setLevel(rs.getString(4));
				data.setPerson(rs.getString(5));
				data.setId(rs.getString(6));
				data.setProduct(rs.getString(7));
				data.setPubdate(rs.getDate(8));
				data.setScore(rs.getString(9));
				data.setCid(rs.getObject(10).toString());
				data.setMd5(rs.getString(11));
				
				if(max.get(0) < data.getDid()) {
					max.set(0, data.getDid());
				}
				return data;
			}
		}));
		return max.get(0);
	}

	public int getEbDatas(final List<Indexable> list, final DataType type, int start, int len) {
		String sql = "select id, title, content, product_img, info_img, diameter, width, "
				+ "price, sale_num, brand, url, info, category_code, md5, search_keyword, "
				+ "site_id, shop_code, model, code_num, pubdate, year, month from (select "
				+ "A.id, A.title, A.content, A.product_img, A.info_img, A.diameter, A.width, A.price, "
				+ "A.sale_num, A.brand, A.url, A.info, A.category_code, A.md5, A.search_keyword, A.site_id, "
				+ "A.shop_code, A.model, A.code_num, A.pubdate, A.year, A.month, rownum rn from ("
				+ "select id, title, content, product_img, info_img, diameter, width, price, "
				+ "sale_num, brand, url, info, category_code, md5, search_keyword, site_id, "
				+ "shop_code, model, code_num, pubdate, year, month from eb_data_processed where id > "
				+ start+" order by id) A where rownum<="+len+") where rn > 0";
		final List<Integer> max = new ArrayList<Integer>();
		max.add(0, start);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		list.addAll(this.jdbcTemplate.query(sql, new RowMapper<EBData>(){
			@Override
			public EBData mapRow(ResultSet rs, int arg1)
					throws SQLException {
				EBData data = new EBData();
				data.setDid(rs.getInt(1));
				data.setTitle(rs.getString(2));
				data.setContent(rs.getString(12));
				String img = rs.getString(4);
				if(img != null) {
					List<String> l = new ArrayList<String>();
					String[] a = img.split(",");
					for(String s : a) 
						l.add(s);
					data.setImgUrl(l);
				}
				img = rs.getString(5);
				if(img != null ){
					List<String> l = new ArrayList<String>();
					String[] a = img.split(",");
					for(String s : a) 
						l.add(s);
					data.setInfoImgUrl(l);
				}
				String dia = rs.getString(6);
				if(dia == null)
					data.setDiameter(0);
				else {
					String d = StringUtil.extrator(rs.getString(6), "\\d+");
					if(d.equals("")) d = "0";
					data.setDiameter(Double.parseDouble(d));
				}
					
				
				String width = rs.getString(7);
				if(width == null)
					data.setWidth(0);
				else if(width.contains("其他"))
					data.setWidth(-1);
				else {
					width = StringUtil.extrator(width, "\\d+");
					if(width.equals("")) width = "0";
					data.setWidth(Double.parseDouble(width));
				}
				
				String price = rs.getString(8);
				String[] ps = price.split(",");
				List<Double> priceList = new ArrayList<Double>();
				for(String s : ps) {
					priceList.add(Double.parseDouble(s.trim()));
				}
				data.setPriceSingle(priceList.get(priceList.size()-1));
				
				String sale = rs.getString(9);
				String[] ss = sale.split(",");
				List<Integer> saleList = new ArrayList<Integer>();
				for(String s : ss) {
					saleList.add(Integer.parseInt(s.trim()));
				}
				data.setSaleNumSingle(saleList.get(saleList.size()-1));
				
				data.setBrand(rs.getString(10));
				data.setUrl(rs.getString(11));
				data.setContent(data.getContent()==null?"":data.getContent()+"\n"+rs.getString(12));
				data.setInfo(rs.getString(12));
				data.setCategoryCode(rs.getInt(13));
				data.setMd5(rs.getString(14).trim());
				data.setSearchKeyword(rs.getString(15));
				if(rs.getInt(16)==48 || rs.getInt(16)==50)
					data.setSite("淘宝");
				else
					data.setSite("京东");
//				data.setSite(rs.getInt(16)+"");
				data.setOwner(rs.getString(17));
				data.setModel(rs.getString(18));
				data.setId(rs.getString(19));
				try {
					data.setUpdateTime(sdf.parse(rs.getString(20)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				data.setYear(rs.getInt(21));
				data.setMonth(rs.getInt(22));
				
				if(max.get(0) < data.getDid()) {
					max.set(0, data.getDid());
				}
				return data;
			}
		}));
		return max.get(0);
	}
	@Override
	public int getDatas(final List<Indexable> list, final DataType type, int start, int len) {
		String sql = COMMON;
		switch(type) {
		case NEWS : {
			sql += "title, author, img_url, same_num from ("+COMMON_PREFIX+
					"A.title, A.author, A.img_url, A.same_num, rownum rn from ("+COMMON+"title, author, img_url, same_num from "
					+type.name().toLowerCase()+"_data_processed where id > "+start+" order by id) A where rownum <="+len+") where rn >0";
			break;
		}
		case BBS :
		case BLOG : {
			sql += "title, author, img_url from ("+COMMON_PREFIX+
					"A.title, A.author, A.img_url, rownum rn from ("+COMMON+"title, author, img_url from "
					+type.name().toLowerCase()+"_data_processed where id > "+start+" order by id) A where rownum <="+len+") where rn >0";
//			System.out.println("weixin sql :"+sql);
			break;
		}
		case WEIXIN :{

			sql += "title, author, img_url, read_num, like_num from ("+COMMON_PREFIX+
					"A.title, A.author, A.img_url,A.read_num, A.like_num, rownum rn from ("+COMMON+"title, author, img_url, read_num, like_num from "
					+type.name().toLowerCase()+"_data_processed where id > "+start+" order by id) A where rownum <="+len+") where rn >0";
//			System.out.println("weixin sql :"+sql);
			break;
		
		}
		case WEIBO : {
			sql += "author, img_url, comment_num, rtt_num from ( "+COMMON_PREFIX+
					"A.author, A.img_url, A.comment_num, A.rtt_num, rownum rn from ("+COMMON+
					"author, img_url, comment_num, rtt_num from "+type.name().toLowerCase()+
					"_data_processed where id > "+start+" order by id) A where rownum <="+len+") where rn >0";
			break; 
		}
		
		case REPORT : {
			sql = "select id, title, pubtime, search_key, url, md5, insert_time, category_id from ("
					+ "select A.id, A.title, A.pubtime, A.search_key,A.url, A.md5, A.insert_time, A.category_id, rownum rn from ("
					+ "select id, title, pubtime, search_key, url, md5, insert_time, category_id from company_report where id > "+start+" order by id) A where rownum <="+len+")  where rn > 0";
			break;
		}
		default : return 0;
		}
		final List<Integer> max = new ArrayList<Integer>();
		max.add(0, start);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("reading >>>" + type + "<<< data...");
		if(type.equals(DataType.REPORT)) {
			try {
				list.addAll(this.jdbcTemplate.query(sql, new RowMapper<CombineData>(){
					@Override
					public CombineData mapRow(ResultSet rs, int arg1)
							throws SQLException {
						CombineData index = new CombineData();
						index.setId(rs.getInt(1));//
						index.setTitle(rs.getString(2));
						try {
							index.setPubdate(sdf.parse(rs.getString(3)));
						} catch (Exception e) {
							index.setPubdate(rs.getDate(3));
						}
						index.setSearchKeyword(rs.getString(4));
						index.setUrl(rs.getString(5));
						index.setMd5(rs.getString(6));
						index.setInsertTime(rs.getDate(7));
						index.setCategoryCode(rs.getInt(8));
						index.setMedia(type.ordinal());
						
						code(rs.getInt(8), index);
						if(max.get(0) < index.getId()) {
							max.set(0, index.getId());
						}
						return index;
					}
				}));
			} catch (Exception e) {
				e.printStackTrace();	
			}
		} else {
			try {
				list.addAll(this.jdbcTemplate.query(sql, new RowMapper<CombineData>(){
					@Override
					public CombineData mapRow(ResultSet rs, int arg1)
							throws SQLException {
						CombineData index = new CombineData();
						index.setUrl(rs.getString(1));
						index.setContent(rs.getString(2));
						try {
							index.setPubdate(sdf.parse(rs.getString(3)));
						} catch (Exception e) {
							index.setPubdate(rs.getDate(3));
						}
						index.setMd5(rs.getString(4));
						index.setInsertTime(rs.getDate(5));
						index.setSource(rs.getString(6));
						index.setCategoryCode(rs.getInt(7));
						index.setCategory1(rs.getInt(8));
						index.setCategory2(rs.getInt(9));
						index.setCategory3(rs.getInt(10));
						index.setWarnLevel(rs.getInt(11));
						index.setHotIndex(rs.getInt(12));
						index.setEmotion(rs.getInt(13));
						index.setReliability(rs.getInt(14));
						index.setId(rs.getInt(15));//
						index.setHotKeys(rs.getString(16));
						index.setSearchKeyword(rs.getString(17));
						index.setRoadType(rs.getInt(18));
						index.setIsJunk(rs.getInt(19));
						switch(type) {
						case NEWS : {
							index.setTitle(rs.getString(20));
							index.setAuthor(rs.getString(21));
							index.setImgUrl(rs.getString(22));
							index.setSimilarCount(rs.getInt(23));
							break;
						} 
						case WEIBO : {
							index.setAuthor(rs.getString(20));
							index.setImgUrl(rs.getString(21));
							index.setCommCount(rs.getInt(22));
							index.setRttCount(rs.getInt(23));
							break;
						}
						case BBS : 
						case BLOG : 
						case WEIXIN :{
							index.setTitle(rs.getString(20));
							index.setAuthor(rs.getString(21));
							index.setImgUrl(rs.getString(22));
							break;
						}
						default:
							break;
						}
						index.setMedia(type.ordinal());
						
						if(max.get(0) < index.getId()) {
							max.set(0, index.getId());
						}
						return index;
					}
				}));
			} catch (Exception e) {
				e.printStackTrace();
 			}
		}
		return max.get(0);
	}
	
	private static String COMMON = "select "	
			+ "url, "
			+ "content, "
			+ "pubtime, "
			+ "md5, "
			+ "insert_time, "
			+ "source, "
			+ "category_code, "
			+ "category1, "
			+ "category2, "
			+ "category3, "
			+ "warn_level, "
			+ "hot_index, "
			+ "emotion_score, " 
			+ "reliability, "
			+ "id, "
			+ "keywords, "
			+ "search_keyword,"
			+ "road_type, "
			+ "is_junk, ";
	
	private static String COMMON_PREFIX = "select "	
			+ "A.url, "
			+ "A.content, "
			+ "A.pubtime, "
			+ "A.md5, "
			+ "A.insert_time, "
			+ "A.source, "
			+ "A.category_code, "
			+ "A.category1, "
			+ "A.category2, "
			+ "A.category3, "
			+ "A.warn_level, "
			+ "A.hot_index, "
			+ "A.emotion_score, "
			+ "A.reliability, "
			+ "A.id, "
			+ "A.keywords, "
			+ "A.search_keyword, "
			+ "A.road_type, "
			+ "A.is_junk, ";
}
