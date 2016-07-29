package service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import bean.CombineData;
import bean.DataType;
import bean.Indexable;
import util.StringUtil;

public abstract class AbstractDBService implements DBService {

	protected JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private static String CODE = "select id, code, nodelevel from category_scheme where id=?";
	public void code(int id, final CombineData data) {
		this.jdbcTemplate.query(CODE, new ResultSetExtractor<Object>() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				rs.next();
				int i = rs.getInt(1);
				int level = rs.getInt(3);
				if(level == 0) {
					data.setCategory1(i);
				} else if(level == 1) {
					data.setCategory2(i);
				} else if(level ==2) {
					data.setCategory3(i);
				}
				if(level > 0) {
					code(rs.getInt(2), data);
				}
				return null;
			}
		}, id);
	}
	
	private static String MAX_ID = "select max(id) from ";
	@Override
	public int getMaxId(DataType type) {
		MAX_ID += type.name().toLowerCase()+"_data";
		return this.jdbcTemplate.queryForInt(MAX_ID);
	}

	@Override
	public void update(int max, DataType type) {
		StringUtil.writeFile("record"+File.separator+type.name().toLowerCase(), max+"");
	}

	@Override
	public int getDatas(List<Indexable> list, DataType type) {
		return getDatas(list, type, 0, Integer.MAX_VALUE);
	}

	@Override
	public abstract int getDatas(List<Indexable> list, DataType type, int start, int len);

	}
