package com.fc.recommend.dataOut.dataBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class SonSimBean  implements Serializable,Comparable<SonSimBean>{

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public int simId=0;
	/**
	 * 名
	 */
	public String name="";
	/**
	 * 值
	 */
	public float value=0F;
	
	public void print()
	{
		System.out.println("["+simId+":"+name+":"+value+"]");
	}
	
	public int getSimId() {
		return simId;
	}
	public void setSimId(int simId) {
		this.simId = simId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public int compareTo(SonSimBean other)
	{
		return -Float.compare(value,other.value);
	}
	
//	public void sortExplain()
//	{
//		Collections.sort(explain);
//	}
}
