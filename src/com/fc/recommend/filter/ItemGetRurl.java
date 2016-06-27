package com.fc.recommend.filter;

import java.util.HashMap;

import com.fc.recommend.dataBean.FeatherInfo;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.filterWeight.WeightUtil;

/**
 * 物品的提取规则
 * @author Administrator
 *
 */
public interface ItemGetRurl {
	
	/**
	 * 损失比率值
	 */
	public float lossValue=3f;
	/**
	 * 
	 * @param user1
	 * @param user2
	 * @param sim 相似度
	 * @param simRate 为用户和品牌的 结构化数据比率 和(1-simRate)非结构化比率
	 */
	public void getRecommendItems(int groupId,UserNode user1,UserNode user2,float sim,FeatherInfo feather,float simRate,WeightUtil weight);
}
