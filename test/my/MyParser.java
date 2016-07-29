package my;

import java.util.Map;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.parser.CharStream;
import org.apache.solr.parser.QueryParser;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.util.SolrPluginUtils;

public class MyParser extends QParser {

	private SolrQueryParser lparser;
	private SolrParams params;
	private Map<String, Float> queryFields;
	public MyParser(String qstr, SolrParams lparams, SolrParams params,
			SolrQueryRequest req) {
		super(qstr, lparams, params, req);
	}
	@Override
	public Query parse() throws SyntaxError {
		SolrParams param = getLocalParams();
		SolrParams pa = getParams();
		params = SolrParams.wrapDefaults(param, pa);
		queryFields = SolrPluginUtils.parseFieldBoosts(params.getParams(DisMaxParams.QF));
		if(queryFields.size() == 0)
			queryFields.put(req.getSchema().getDefaultSearchFieldName(), 1.0f);
		
		String qstr = getString();
		String defaultField = getParam("df");
		if(defaultField == null)
			defaultField = req.getSchema().getDefaultSearchFieldName();
		this.lparser = new MyQueryParser(this, defaultField, queryFields);
		
		String opParam = getParam("q.op");
		if(opParam != null) 
			this.lparser.setDefaultOperator("AND".equals(opParam)?QueryParser.Operator.AND : QueryParser.Operator.OR);
		else {
			String op = getReq().getSchema().getQueryParserDefaultOperator();
			this.lparser.setDefaultOperator(op == null ? QueryParser.Operator.OR : "AND".equals(opParam)?QueryParser.Operator.AND : QueryParser.Operator.OR);
		}
		return this.lparser.parse(qstr);
	}
	
	@Override
	public String[] getDefaultHighlightFields() {
		return new String[]{lparser.getDefaultField()};
	}
	
	
	
	

}
