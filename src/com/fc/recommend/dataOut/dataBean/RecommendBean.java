package com.fc.recommend.dataOut.dataBean;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 给 mall 推荐 brand
 * 
 * @author Administrator
 *
 */
public class RecommendBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * mallName
	 */
	public String name = "";
	/**
	 * mall 对应的id
	 */
	public int id = 0;

	public int cityId = 0;
	/**
	 * 推荐的业态对应的物品
	 */
	public LinkedList<CategoryBean> category = new LinkedList<CategoryBean>();

	/**
	 * 品牌相似度信息
	 */
	public LinkedList<SonSimBean> similary = new LinkedList<SonSimBean>();

	/**
	 * 添加业态
	 * @param categoryBean
	 */
	public void addCategory(CategoryBean categoryBean)
	{
		boolean flag =false;
		for(CategoryBean bean:category)
		{
			if(bean.category==categoryBean.category)
			{
				flag=true;
				break;
			}
		}
		if(!flag)
		{
			this.category.add(categoryBean);
		}
	}
	
	public void addSimilaryMall(SonSimBean similary) {
		boolean flag = false;
		for (SonSimBean son : this.similary) {
			if (son.getName().equals(similary.getName())) {
				flag = true;
				son.setValue((son.getValue() + similary.getValue()) / 2);
			}
		}
		if (!flag) {
			this.similary.add(similary);
		}
	}

	/**
	 * 重新排序 相似信息
	 */
	public void sortSim() {
		// 重新排序
		Collections.sort(similary);
		// 组合
	}

	/**
	 * 排序item数据
	 */
	public void sortItem() {
		Collections.sort(this.category);
	}

//	/**
//	 * 排序解释信息
//	 */
//	public void sortExplain() {
//		for (SonSimBean bean : similary) {
//			bean.sortExplain();
//		}
//		for (CategoryBean bean : category) {
//			for (SonBean beanson : bean.item) {
//				beanson.sortExplain();
//			}
//		}
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public LinkedList<CategoryBean> getCategory() {
		return category;
	}

	public void setCategory(LinkedList<CategoryBean> category) {
		this.category = category;
	}

	public LinkedList<SonSimBean> getSimilary() {
		return similary;
	}

	public void setSimilary(LinkedList<SonSimBean> similary) {
		this.similary = similary;
	}
	
}
