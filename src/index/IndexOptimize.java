package index;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServerException;

import system.Systemconfig;

public class IndexOptimize {

	private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
	public void start() {
		exec.scheduleWithFixedDelay(new IndexThread(), 1, 24, TimeUnit.HOURS);
	}
	private class IndexThread implements Runnable {
		@Override
		public void run() {
			try {
				Systemconfig.setimentServer.optimize();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
