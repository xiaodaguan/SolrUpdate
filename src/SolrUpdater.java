import index.IndexSolr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bean.DataType;
import system.Systemconfig;
import util.StringUtil;

/*
 * 
 */
public class SolrUpdater {

    public static ApplicationContext appCtx;

    public static void main(String[] args) throws IOException, SolrServerException {
        initAppCtx("");

//		Systemconfig.setimentServer.deleteByQuery("media:4");
//		Systemconfig.setimentServer.commit();

        if(args.length==0){
            System.out.println("命令：\n-c(clear)\n-u(update)\n-t={typename}(type)");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if ("-c".equalsIgnoreCase(args[i]) || "--clearall".equalsIgnoreCase(args[i])) {
                clearAll();
            }

            if ("-u".equalsIgnoreCase(args[i]) || "--update".equalsIgnoreCase(args[i])) {
                processAll();
            }


            if (args[i].startsWith("-t=")||args[i].startsWith("--type=")) {
                try {
                    String param = args[i].split("=")[1];
                    DataType type = DataType.findType(param);
                    if (type != null) processByType(type);
                } catch (Exception e) {
                    System.err.println("类别参数错误");
                }
            }
        }
//		clearAll();
//		clearEB();
//		clearEBComment();
//		processAll();
//		processByType(DataType.BBS);
//		clearByQuery("media:4", 1);
    }

    /*
     * 建立索引
     */
    public static void processAll() {
        IndexSolr si = new IndexSolr();
        while (true) {
            si.indexData();
            for (DataType dt : DataType.values()) {

                if (dt.ordinal() == 0) continue;
                if (!si.getMap().get(dt).isDone() || !si.getMap().get(dt).isCancelled()) {
                    try {
                        si.getMap().get(dt).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(new java.util.Date() + "索引完毕！！");
            try {
                TimeUnit.MINUTES.sleep(Systemconfig.cycle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /*
     * 根据类型索引
     */
    public static void processByType(DataType type) {
        IndexSolr si = new IndexSolr();
        si.indexData(type);
    }

    /**
     * @param query
     * @param type  core
     * @throws SolrServerException
     * @throws IOException
     */
    public static void clearByQuery(String query, int type) throws SolrServerException, IOException {
        if (type == 1) {
            Systemconfig.setimentServer.deleteByQuery(query);
            Systemconfig.setimentServer.commit();
        } else if (type == 2) {
//			Systemconfig.ebServer.deleteByQuery(query);
//			Systemconfig.ebServer.commit();
        } else if (type == 3) {
//			Systemconfig.commentEbServer.deleteByQuery(query);
//			Systemconfig.commentEbServer.commit();
        }
//		"(-text:\"赛轮\" AND -text:\"三角\")"
    }

    public static void clearAll() throws IOException {
        try {
            clearCombine();
            clearEB();
            clearEBComment();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (DataType dt : DataType.values()) {
            if (dt.ordinal() == 0) continue;
            File f = new File("record" + File.separator + dt.name().toLowerCase());
            if (f.exists()) {
                if (f.delete()) StringUtil.writeFile("record" + File.separator + dt.name().toLowerCase(), "0");
                ;
            } else {
                StringUtil.writeFile("record" + File.separator + dt.name().toLowerCase(), "0");
            }
        }
    }


    private static void clearCombine() throws SolrServerException, IOException {
        Systemconfig.setimentServer.deleteByQuery("*:*");
        Systemconfig.setimentServer.commit();
    }

    private static void clearEB() throws SolrServerException, IOException {
//		Systemconfig.ebServer.deleteByQuery("*:*");
//		Systemconfig.ebServer.commit();
    }

    private static void clearEBComment() throws SolrServerException, IOException {
//		Systemconfig.commentEbServer.deleteByQuery("*:*");
//		Systemconfig.commentEbServer.commit();
//		System.out.println("ebcomment 索引已清除");
    }

    /**
     * 加载配置文件
     *
     * @param path
     */
    public static void initAppCtx(String path) {
        PropertyConfigurator.configure(path + "config/log4j.properties");
        File[] files = new File(path + "config").listFiles();
        List<String> list = new ArrayList<String>();
        for (File file : files) {
            if (file.getName().startsWith("app")) {
                list.add(path + "config" + File.separator + file.getName());
            }
        }
        String[] arry = new String[list.size()];
        list.toArray(arry);
        appCtx = new FileSystemXmlApplicationContext(arry);
    }
}
