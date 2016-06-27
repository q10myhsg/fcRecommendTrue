package com.fc.commend.graphLinked;

/**
 * 标准排序bean
 * 通常使用降序结构
 * @author Administrator
 *
 */
public class GeneralSortBean implements Comparable<GeneralSortBean>{

	public int id=-1;	
	public String name=null;
	public float value=0f;
	@Override
	public int compareTo(GeneralSortBean o) {
		// TODO Auto-generated method stub
		return Float.compare(o.value,this.value);
	}
	public String toString()
	{
		return "[id:"+id+",name:"+name+",value:"+value+"]";
	}
}
