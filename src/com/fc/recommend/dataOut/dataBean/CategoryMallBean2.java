package com.fc.recommend.dataOut.dataBean;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * 推荐结果的 sort中间过程实体
 * @author Administrator
 *
 */
public class CategoryMallBean2{
	/**
	 * 业态id
	 */
	private int  category=0;
	/**
	 * 分类名
	 */
	private String categoryName="";
	
	/**
	 * 子类
	 */
	private HashMap<Integer,SonBean> recommandMall=new HashMap<Integer,SonBean>();
	
	public long getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public HashMap<Integer, SonBean> getRecommandMall() {
		return recommandMall;
	}
	public void setRecommandMall(HashMap<Integer, SonBean> recommandMall) {
		this.recommandMall = recommandMall;
	}
	public void addRecommandMall(SonBean recommandMall,float similary) {
		SonBean bean=this.recommandMall.get(recommandMall.getId());
		if(bean==null)
		{
			recommandMall.setValue(similary);
			this.recommandMall.put(recommandMall.getId(),recommandMall);
			
		}else{
			bean.setValue(bean.getValue()+similary);
		}
	}
	
	/**
	 * 获取推荐信息
	 * @return
	 */
	public LinkedList<CategoryMallBean> sortRecommandAndGet(int count)
	{
		LinkedList<CategoryMallBean> re=new LinkedList<CategoryMallBean>();
		LinkedList<SonBean> bean=new LinkedList<SonBean>();
		for(Entry<Integer,SonBean> son:recommandMall.entrySet())
		{
			bean.add(son.getValue());
		}
		Collections.sort(bean);
		while(bean.size()>count)
		{
			bean.pollLast();
		}
		CategoryMallBean result=new CategoryMallBean();
		result.setCategory(category);
		result.setCategoryName(categoryName);
		result.setRecommandMall(bean);
		re.add(result);
		return re;
	}
	
	
}
