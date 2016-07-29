package system;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.solr.client.solrj.impl.CloudSolrServer;

import bean.DataType;
import service.DBService;
import util.StringUtil;

/**
 * œµÕ≥≈‰÷√
 *
 * @author grs
 */
public class Systemconfig {

    public static DBService commonService;
    public static CloudSolrServer setimentServer;
    //	public static CloudSolrServer ebServer;
//	public static CloudSolrServer commentEbServer;
    public static Map<String, String> siteMap;
    public static long cycle;
    private String URL;
    private int connectTime;
    private int clientTime;

    private String coreName;

    public void init() {
        try {
            setimentServer = new CloudSolrServer(URL);
//			ebServer = new CloudSolrServer(URL);
//			commentEbServer = new CloudSolrServer(URL);
        } catch (MalformedURLException e) {
            System.exit(-1);
        }
        initServer(setimentServer, coreName);
//		initServer(ebServer, "eb");
//		initServer(commentEbServer, "ebcomments");
        setimentServer.connect();
//		ebServer.connect();
//		commentEbServer.connect();


        for (DataType dt : DataType.values()) {
            if (dt.ordinal() == 0) continue;
            File f = new File("record" + File.separator + dt.name().toLowerCase());
            if (!f.exists()) {
                StringUtil.writeFile("record" + File.separator + dt.name().toLowerCase(), "0");
            }
        }

    }

    private void initServer(CloudSolrServer server, String index) {
        server.setDefaultCollection(index);
        server.setZkClientTimeout(clientTime);
        server.setZkConnectTimeout(connectTime);
    }

    public void setClientTime(int clientTime) {
        this.clientTime = clientTime;
    }

    public void setConnectTime(int connectTime) {
        this.connectTime = connectTime;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public void setCommonService(DBService commonService) {
        Systemconfig.commonService = commonService;
    }

    public void setCycle(long cycle) {
        Systemconfig.cycle = cycle;
    }
//	public void setHbaseDBService(HbaseDBService hbaseDBService) {
//		Systemconfig.hbaseDBService = hbaseDBService;
//	}


    public void setCoreName(String coreName) {
        this.coreName = coreName;
    }
}
