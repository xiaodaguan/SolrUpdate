package bean;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
/**
 * ��������
 * @author grs
 *
 */
public class EBData implements Indexable {

	private int did;
	@Field
	private String id;
	@Field
	private String owner;
	@Field
	private String url;//�ɼ�����
	@Field
	private String title;//����
	@Field
	private String model;//�ͺ�
	@Field 
	private List<String> imgUrl;//��ƷͼƬ����
	@Field 
	private List<String> infoImgUrl;//����ͼƬ����
	@Field
	private String content;//����	
	@Field
	private String info;//����	
	@Field
	private String source;//����Դ
	@Field
	private Date pubdate;//ʱ��
	@Field
	private String md5;
	@Field
	private List<Double> price;//�۸�
	@Field 
	private List<Integer> saleNum;//������
	@Field 
	private int saleNumSingle;
	@Field 
	private double priceSingle;
	@Field 
	private String brand;//Ʒ��
	@Field
	private double width;//̥����
	@Field
	private double diameter;//ֱ��
	@Field
	private double format;//���
	@Field
	private Date updateTime;//��������
	@Field
	private Date insertTime;//�ɼ�ʱ��
	@Field
	private long _version_;
	@Field
	private String site;//ƽ̨
	@Field
	private int categoryCode;//�������
	@Field
	private String searchKeyword;//������
	@Field
	private int year;
	@Field
	private int month;
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
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
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public List<String> getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(List<String> imgUrl) {
		this.imgUrl = imgUrl;
	}
	public List<String> getInfoImgUrl() {
		return infoImgUrl;
	}
	public void setInfoImgUrl(List<String> infoImgUrl) {
		this.infoImgUrl = infoImgUrl;
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
	public List<Double> getPrice() {
		return price;
	}
	public void setPrice(List<Double> price) {
		this.price = price;
	}
	public List<Integer> getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(List<Integer> saleNum) {
		this.saleNum = saleNum;
	}
	public int getSaleNumSingle() {
		return saleNumSingle;
	}
	public void setSaleNumSingle(int saleNumSingle) {
		this.saleNumSingle = saleNumSingle;
	}
	public double getPriceSingle() {
		return priceSingle;
	}
	public void setPriceSingle(double priceSingle) {
		this.priceSingle = priceSingle;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getDiameter() {
		return diameter;
	}
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}
	public double getFormat() {
		return format;
	}
	public void setFormat(double format) {
		this.format = format;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateDate) {
		this.updateTime = updateDate;
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
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public int getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(int categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	
}
