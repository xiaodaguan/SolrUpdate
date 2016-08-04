package index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import system.Systemconfig;
import util.StringUtil;
import bean.CombineData;
import bean.DataType;
import bean.EBData;
import bean.Indexable;

/**
 * solr����
 * 
 * @author grs
 *
 */
public class IndexSolr {
	private static final int SIZE = 10000;
	private static final ExecutorService exec = Executors.newFixedThreadPool(5);
	private static final HashMap<DataType, Future<?>> map = new HashMap<DataType, Future<?>>();

	private class IndexData implements Runnable {
		private final DataType type;

		public IndexData(DataType type) {
			this.type = type;
		}

		@Override
		public void run() {
			List<Indexable> list = new ArrayList<Indexable>();
			String con = StringUtil.getContent("record" + File.separator + type.name().toLowerCase());
			int num = Integer.parseInt(con.trim());
			int max = num;
			long start = System.currentTimeMillis();
			// if(type.equals(DataType.EB_COMMENT)) {
			// max = Systemconfig.commonService.getEbCommentDatas(list, type,
			// max, SIZE);
			// System.out.println(type.name()+"��ȡ"+list.size()+"������");
			// if(list.size()>0) {
			// System.out.println(type.name()+"��ʼ�����������");
			// try {
			// Systemconfig.commentEbServer.addBeans(list);
			// } catch (SolrServerException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// list.clear();
			// max = Systemconfig.commonService.getEbCommentDatas(list, type,
			// max, SIZE);
			// }
			// System.out.println(type.name()+"��ʼ�ύ��������");
			// try {
			// Systemconfig.commentEbServer.commit();
			// } catch (SolrServerException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// } else if(type.equals(DataType.EB)) {
			// max = Systemconfig.commonService.getEbDatas(list, type, max,
			// SIZE);
			// System.out.println(type.name()+"��ȡ"+list.size()+"������");
			// if(list.size()>0) {
			// System.out.println(type.name()+"��ʼ�����������");
			// try {
			// Systemconfig.ebServer.addBeans(list);
			//
			// // ת��
			// List<CombineData> newList = covert(list);
			// Systemconfig.setimentServer.addBeans(newList);
			// newList.clear();
			// } catch (SolrServerException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// list.clear();
			// max = Systemconfig.commonService.getEbDatas(list, type, max,
			// SIZE);
			// }
			// System.out.println(type.name()+"��ʼ�ύ��������");
			// try {
			// Systemconfig.ebServer.commit();
			// Systemconfig.setimentServer.commit();
			// } catch (SolrServerException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// } else {
			// if(type.name().equals("WEIXIN"))
			// System.out.println("debug...");
			max = Systemconfig.commonService.getDatas(list, type, max, SIZE);

			System.out.println(type.name() + "read: " + list.size() + " records.");
			if (list.size() > 0) {
				System.out.println(type.name() + " adding ...");
				try {
					Systemconfig.setimentServer.addBeans(list);
				} catch (SolrServerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				list.clear();
//				max = Systemconfig.commonService.getDatas(list, type, max, SIZE);
			}
			System.out.println(type.name() + " committing...");
			try {
				UpdateResponse res = Systemconfig.setimentServer.commit();
				System.out.println(res);
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// }
			if (num < max)
				Systemconfig.commonService.update(max, type);
			System.out.println(type.name() + "cost: " + (System.currentTimeMillis() - start) + " milliseconds.");
			list.clear();
		}

		private List<CombineData> covert(List<Indexable> list) {
			List<CombineData> result = new ArrayList<CombineData>();
			for (Indexable i : list) {
				EBData d = (EBData) i;
				CombineData cd = new CombineData();
				cd.setCategoryCode(d.getCategoryCode());
				cd.setContent(d.getContent());
				String s = d.getImgUrl().toString();
				cd.setImgUrl(s.substring(1, s.length() - 1));
				cd.setInsertTime(d.getInsertTime());
				cd.setSearchKeyword(d.getSearchKeyword());
				cd.setAuthor(d.getOwner());
				cd.setMd5(d.getMd5());
				cd.setTitle(d.getTitle());
				cd.setCommCount(d.getSaleNumSingle());
				cd.setMedia(7);
				cd.setPubdate(d.getPubdate());
				cd.setSource(d.getSite());

				result.add(cd);
			}
			return result;
		}

	}

	/**
	 * ��������
	 */
	public void indexData() {
		for (DataType dt : DataType.values()) {
			if (dt.ordinal() == 0)
				continue;
			indexData(dt);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * ��������
	 * 
	 */
	public void indexData(DataType type) {
		Future<?> fu = exec.submit(new IndexData(type));
		map.put(type, fu);
		// new IndexData(num).run();
	}

	public HashMap<DataType, Future<?>> getMap() {
		return map;
	}

}
