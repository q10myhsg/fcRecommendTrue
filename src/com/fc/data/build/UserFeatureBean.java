package com.fc.data.build;

import java.io.Serializable;
import java.util.HashMap;

public class UserFeatureBean implements Serializable{
	
	public long i=0;

	public int c=0;
	public int t=0;
	/**
	 * 结构化
	 */
	public int[] dbms=null;
	/**
	 * 非结构化
	 */
	public HashMap<Integer,Object[]> f=new HashMap<Integer,Object[]>();
	
	public int[] getDbms() {
		return dbms;
	}
	public void setDbms(int[] dbms) {
		this.dbms = dbms;
	}
	public HashMap<Integer, Object[]> getF() {
		return f;
	}
	public void setF(HashMap<Integer, Object[]> f) {
		this.f = f;
	}
	public long getI() {
		return i;
	}
	public void setI(long i) {
		this.i = i;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	public int getT() {
		return t;
	}
	public void setT(int t) {
		this.t = t;
	}
	
	
}
