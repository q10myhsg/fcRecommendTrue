package com.fc.recommend.filter;

import java.util.LinkedList;

import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserNode;
/**
 * 用于过滤最终推荐结果
 * @author Administrator
 *
 */
public interface RecommenderFilterUtil {

	/**
	 * 用于数据开始时 的数据清洗
	 * @param userNode
	 */
	public void filter(UserNode userNode);
	/**
	 * 用于过滤 推荐结果
	 * @param userItem
	 * @param filterItem
	 */
	public void filter(LinkedList<ItemNode> userItem,LinkedList<ItemNode> filterItem);
}
