package com.fc.recommend.dataOut.dataBean;

import java.io.Serializable;
import java.util.LinkedList;

public class CategoryItemBean  implements Serializable,Comparable<CategoryItemBean>{

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;

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
	public LinkedList<SonBean> recommandShop=new LinkedList<SonBean>();


	public String getCategoryName() {
		return categoryName;
	}



	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}



	public long getCategory() {
		return category;
	}



	public void setCategory(int category) {
		this.category = category;
	}



	public LinkedList<SonBean> getRecommandShop() {
		return recommandShop;
	}

	public void setRecommandShop(LinkedList<SonBean> recommandShop) {
		this.recommandShop = recommandShop;
	}

	public void addRecommandShop(SonBean recommandShop) {
		this.recommandShop.add(recommandShop);
	}
	
	public void addRecommandShopLimit(SonBean recommandShop,int count) {
		if(this.recommandShop.size()<=count)
		this.recommandShop.add(recommandShop);
	}
//	public LinkedList<SonSimBean> getSimilaryMall() {
//		return similaryMall;
//	}
//
//	public void setSimilaryMall(LinkedList<SonSimBean> similaryMall) {
//		this.similaryMall = similaryMall;
//	}
//	public void addSimilaryMall(SonSimBean similaryMall) {
//		this.similaryMall.add(similaryMall);
//	}

	@Override
	public int compareTo(CategoryItemBean o) {
		// TODO Auto-generated method stub
		return Long.compare(category,o.category);
	}
	
	
	
}
