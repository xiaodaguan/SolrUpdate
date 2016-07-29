package service;

import java.util.List;

import bean.CombineData;
import bean.DataType;
import bean.Indexable;

public interface DBService {

	/**
	 * ���ĳ�����������ݱ�����ID
	 * @return
	 */
	public int getMaxId(DataType type);
	
	int getDatas(List<Indexable> list, DataType type);
	/**
	 * ���������
	 * @param max
	 * @param num
	 */
	public void update(int max, DataType type);

	int getDatas(List<Indexable> list, DataType type, int start, int len);
	int getEbDatas(List<Indexable> list, DataType type, int start, int len);
	
	public void code(int id, CombineData data);

	public int getEbCommentDatas(List<Indexable> list, DataType type, int max,
			int size);
}
