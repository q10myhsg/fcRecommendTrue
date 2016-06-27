package com.fc.recommend.reDataProcession;

public class PropertiesBean implements Comparable<PropertiesBean>{

	/**
	 * id
	 */
	public int id=0;
	/**
	 * 名字
	 */
	public String name="";
	/**
	 * 值
	 */
	public int count=0;
	@Override
	public int compareTo(PropertiesBean o) {
		// TODO Auto-generated method stub
		return Integer.compare(o.count,this.count);
	}
	
}
