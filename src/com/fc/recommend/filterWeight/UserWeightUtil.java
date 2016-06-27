package com.fc.recommend.filterWeight;

import java.util.HashMap;

/**
 * 用户相似度加权函数
 * @author Administrator
 *
 */
public class UserWeightUtil {

	public static HashMap<Integer,Float> map=new HashMap<Integer,Float>();
	
	public UserWeightUtil(){
	add(3671260,1.2f);//侨福芳草地购物中心
	add(4740075,1.2f);//嘉里中心
	add(1766266,1.2f);//国贸商城
	add(4717983,1.2f);//颐堤港
	add(2564407,1.2f);//三里屯太古里
	add(2088220,1.2f);//新光天地
	add(20912003,1.2f);//荟聚西红门购物中心 
	}
	
	/**
	 * 添加数据
	 * @param id
	 * @param value
	 */
	public void add(int id,Float value)
	{
		map.put(id,value);	
	}
	/**
	 * 通过id获取对应的加权值
	 * @param id
	 * @return
	 */
	public float get(int id)
	{
		Float fval=map.get(id);
		if(fval==null)
		{
			return 1.0f;
		}
		return fval;
	}

}
