package my;

import java.util.Map;

import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;

public class MyQueryParser extends SolrQueryParser {
	private Map<String, Float> queryFields;
	public MyQueryParser(QParser parser, String defaultField) {
		super(parser, defaultField);
	}
	public MyQueryParser(QParser parser, String defaultField,
			Map<String, Float> queryFields) {
		super(parser, defaultField);
		this.queryFields = queryFields;
	}
	
}
