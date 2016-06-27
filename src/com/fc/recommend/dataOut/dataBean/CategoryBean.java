package com.fc.recommend.dataOut.dataBean;

import java.io.Serializable;
import java.util.LinkedList;

public class CategoryBean  implements Serializable,Comparable<CategoryBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 业态
	 */
	public String  categoryName="";
	/**
	 * 业态id
	 */
	public int category=0;
	
	/**
	 * 子类
	 */
	public LinkedList<SonBean> item=new LinkedList<SonBean>();
	
	public void addRecommandShopLimit(SonBean recommandShop,int count) {
		if(this.item.size()<=count)
		this.item.add(recommandShop);
	}
	@Override
	public int compareTo(CategoryBean o) {
		// TODO Auto-generated method stub
		return Integer.compare(category,o.category);
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public LinkedList<SonBean> getItem() {
		return item;
	}
	public void setItem(LinkedList<SonBean> item) {
		this.item = item;
	}
	
	
	
}
