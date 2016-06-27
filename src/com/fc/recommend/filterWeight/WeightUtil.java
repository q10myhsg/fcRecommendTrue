package com.fc.recommend.filterWeight;

import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserInfo;
import com.fc.recommend.dataBean.UserNode;

/**
 * 权重接口
 * @author Administrator
 *
 */
public interface WeightUtil {

	public UserNode userNode=null;
	/**
	 * 获取全部用户统计后该物品的数量
	 * @param item
	 * @return
	 */
	public int getCount(ItemNode item);
	
	public int getCount(int itemId);
	/**
	 * 权重初始化
	 * @param userInfo
	 */
	public void init(UserInfo userInfo);
	/**
	 * 添加权重
	 * @param userInfo
	 */
	public void add(UserInfo userInfo);
	/**
	 * 物品总数
	 * @return
	 */
	public int size();
}
