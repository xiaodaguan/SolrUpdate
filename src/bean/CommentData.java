package bean;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class CommentData implements Indexable {

	@Field
	private String id;
	@Field
	private String person;//评论人
	@Field
	private String level;//评论人级别
	@Field
	private String info;//评论内容
	@Field
	private Date pubdate;//评论时间
	@Field
	private List<String> label;//标签
	@Field
	private String product;//
	@Field
	private String score;//评分
	@Field
	private String cid;//评论id
	@Field
	private String md5;
	private int did;
	
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
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Date getPubtime() {
		return pubdate;
	}
	public void setPubdate(Date pubtime) {
		this.pubdate = pubtime;
	}
	public List<String> getLabel() {
		return label;
	}
	public void setLabel(List<String> label) {
		this.label = label;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	@Override
	public String toString() {
		return cid +", "+ info +", " + pubdate+", "+person;
	}
	
}
//oot下执行命令：
//sudo -u hdfs hadoop fsck -delete
//再次执行下面检查命令就没问题了
//sudo -u hdfs hadoop fsck /