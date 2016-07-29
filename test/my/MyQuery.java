package my;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.common.params.SolrParams;

public class MyQuery extends BooleanQuery {
	private SolrParams params;
	public MyQuery() {
		super();
	}
	public MyQuery(SolrParams params, boolean disablecoord) {
		super(disablecoord);
		this.params = params;
	}
	
	public class MultiWeight extends BooleanWeight {

		private SolrParams params;
		private String[] factor;
		public MultiWeight(SolrParams params, IndexSearcher search, boolean disablecoord) throws IOException {
			super(search, disablecoord);
			this.params = params;
		}
		
		@Override
		public Scorer scorer(AtomicReaderContext reader, boolean order,
				boolean score, Bits bits) throws IOException {
					return null;
//			String[] temp = FieldCache.DEFAULT.g;
//			return super.scorer(arg0, arg1, arg2, arg3);
		}
		
		public String[] getFactor() {
			return factor;
		}
		public SolrParams getParams() {
			return params;
		}
	}
	
	@Override
	public Weight createWeight(IndexSearcher searcher) throws IOException {
		return new MultiWeight(params, searcher, isCoordDisabled());
	}
}
