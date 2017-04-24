package bean;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;
/**
 * ��������
 * @author grs
 *
 */
public class CombineData implements Indexable {

	@Field
	private int id;
	@Field
	private String url;//�ɼ�����
	@Field
	private String title;//����
	@Field 
	private String imgUrl;//ͼƬ����
	@Field
	private String author;//����
	@Field
	private String content;//����
	@Field
	private String source;//����Դ
	@Field
	private Date pubdate;//ʱ��
	@Field
	private String md5;
	@Field
	private int similarCount;//��������
	@Field 
	private int commCount;//������
	@Field 
	private int rttCount;//ת����
	@Field
	private int emotion;//�������
	@Field
	private String hotKeys;//�ؼ���
	@Field
	private int categoryCode;//�������
	@Field
	private int category1;//������������
	@Field
	private int category2;//������2������
	@Field
	private int category3;//������3������
	@Field
	private int warnLevel;
	@Field 
	private int hotIndex;//�ȶ�
	@Field
	private Date insertTime;//�ɼ�ʱ��
	@Field
	private long _version_;
	@Field
	private int media;//ý��
	@Field
	private int reliability;//���Ŷ�
	@Field
	private String searchKeyword;//������
	
	@Field
	private int roadType;//�Ƿ����·
	
	@Field
	private int isJunk;//�Ƿ�����

	@Field
	private int relateLevel;

	public int getRelateLevel() {
		return relateLevel;
	}

	public void setRelateLevel(int relateLevel) {
		this.relateLevel = relateLevel;
	}
	
	
	public int getIsJunk() {
		return isJunk;
	}
	public void setIsJunk(int isJunk) {
		this.isJunk = isJunk;
	}
	public int getRoadType() {
		return roadType;
	}
	public void setRoadType(int roadType) {
		this.roadType = roadType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getPubdate() {
		return pubdate;
	}
	public void setPubdate(Date pubdate) {
		this.pubdate = pubdate;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public int getSimilarCount() {
		return similarCount;
	}
	public void setSimilarCount(int similarCount) {
		this.similarCount = similarCount;
	}
	public int getCommCount() {
		return commCount;
	}
	public void setCommCount(int commCount) {
		this.commCount = commCount;
	}
	public int getRttCount() {
		return rttCount;
	}
	public void setRttCount(int rttCount) {
		this.rttCount = rttCount;
	}
	public int getEmotion() {
		return emotion;
	}
	public void setEmotion(int emotion) {
		this.emotion = emotion;
	}
	public String getHotKeys() {
		return hotKeys;
	}
	public void setHotKeys(String hotKeys) {
		this.hotKeys = hotKeys;
	}
	public int getHotIndex() {
		return hotIndex;
	}
	public void setHotIndex(int hotIndex) {
		this.hotIndex = hotIndex;
	}
	public int getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(int categoryCode) {
		this.categoryCode = categoryCode;
	}
	public int getCategory1() {
		return category1;
	}
	public void setCategory1(int category1) {
		this.category1 = category1;
	}
	public int getCategory2() {
		return category2;
	}
	public void setCategory2(int category2) {
		this.category2 = category2;
	}
	public int getCategory3() {
		return category3;
	}
	public void setCategory3(int category3) {
		this.category3 = category3;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public long get_version_() {
		return _version_;
	}
	public void set_version_(long _version_) {
		this._version_ = _version_;
	}
	public int getMedia() {
		return media;
	}
	public void setMedia(int media) {
		this.media = media;
	}
	public int getWarnLevel() {
		return warnLevel;
	}
	public void setWarnLevel(int warnLevel) {
		this.warnLevel = warnLevel;
	}
	public int getReliability() {
		return reliability;
	}
	public void setReliability(int reliability) {
		this.reliability = reliability;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}
	
}
