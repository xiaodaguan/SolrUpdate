package bean;

public class BrandDS {

	public String name;//����
	public boolean master;//�Ƿ�Ϊ��׼��ʾ��
	
	public BrandDS(String name) {
		this(name, false);
	}
	public BrandDS(String name, boolean master) {
		this.name = name;
		this.master = master;
	}
}
