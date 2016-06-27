package com.fc.recommend.dataOut.dataBean;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 给 mall 推荐 brand
 * @author Administrator
 *
 */
public class MallRecommendBean implements Serializable{

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * mallName
	 */
	public String mallName="";
	/**
	 * mall 对应的id
	 */
	public int mallId=0;
	/**
	 * 推荐城市Id
	 */
	public int cityId=0;
	/**
	 * 推荐城市名
	 */
	public String cityName="";
	/**
	 * 推荐的业态对应的物品
	 */
	public LinkedList<CategoryItemBean> categoryItem=new LinkedList<CategoryItemBean>();
	
	/**
	 * 品牌相似度信息
	 */
	public LinkedList<SonSimBean> similaryMall=new LinkedList<SonSimBean>();

	public long getMallId() {
		return mallId;
	}

	public void setMallId(int mallId) {
		this.mallId = mallId;
	}

	public String getMallName() {
		return mallName;
	}

	public void setMallName(String mallName) {
		this.mallName = mallName;
	}

	
	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public LinkedList<CategoryItemBean> getCategoryItem() {
		return categoryItem;
	}

	public void setCategoryItem(LinkedList<CategoryItemBean> categoryItem) {
		this.categoryItem = categoryItem;
	}
	
	public void setCategoryItem(int index,CategoryItemBean categoryItem)
	{
		this.categoryItem.set(index, categoryItem);
	}
	
	public void addCategoryItem(CategoryItemBean categoryItem) {
		this.categoryItem.add(categoryItem);
	}

	public LinkedList<SonSimBean> getSimilaryMall() {
		return similaryMall;
	}

	public void setSimilaryMall(LinkedList<SonSimBean> similaryMall) {
		this.similaryMall = similaryMall;
	}

	public void addSimilaryMall(SonSimBean similary) {
		boolean flag=false;
		for(SonSimBean son:this.similaryMall)
		{
			if(son.getName().equals(similary.getName()))
			{
				flag=true;
				son.setValue((son.getValue()+similary.getValue())/2);
			}
		}
		if(!flag)
		{
			this.similaryMall.add(similary);
		}
	}
	/**
	 * 重新排序
	 * 相似信息
	 */
	public void sortSim()
	{
		//重新排序
		Collections.sort(similaryMall);
		//组合
	}
	/**
	 * 排序item数据
	 */
	public void sortItem()
	{
		Collections.sort(this.categoryItem);
	}
//	/**
//	 * 排序解释信息
//	 */
//	public void sortExplain()
//	{
//		for(SonSimBean bean:similaryMall)
//		{
//			bean.sortExplain();
//		}
//		for(CategoryItemBean bean:categoryItem)
//		{
//			for(SonBean beanson:bean.getRecommandShop())
//			{
//				beanson.sortExplain();
//			}
//		}
//	}
}
