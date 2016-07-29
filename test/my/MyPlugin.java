package my;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class MyPlugin extends QParserPlugin {

	@Override
	public void init(NamedList arg0) {
		
	}

	@Override
	public QParser createParser(String qstr, SolrParams lparams, SolrParams params,
			SolrQueryRequest req) {
		return new MyParser(qstr, lparams, params, req);
	}

}
