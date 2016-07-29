package bean;

public class BrandDS {

	public String name;//名称
	public boolean master;//是否为标准显示名
	
	public BrandDS(String name) {
		this(name, false);
	}
	public BrandDS(String name, boolean master) {
		this.name = name;
		this.master = master;
	}
}
