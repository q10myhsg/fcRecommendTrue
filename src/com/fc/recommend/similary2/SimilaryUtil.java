package com.fc.recommend.similary2;

import java.util.HashMap;

import com.fc.recommend.dataBean.FeatherInfo;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.filterWeight.UserWeightUtil;
import com.fc.recommend.filterWeight.WeightUtil;

public interface SimilaryUtil {

/**
 * 获取函数	
 * @return
 */
	public SimilaryUtil getSimilaryFunc();
	/**
	 * 获取相似性
	 * @param userNode1
	 * @param userNode2
	 * @param 对应的权重类型
	 * @return
	 */
	public float getSimilary(UserNode userNode1,UserNode userNode2,WeightUtil weigth,UserWeightUtil userWeight);
	/**
	 * 用于计算基于内容的相似度
	 * @param userNode1
	 * @param userNOde2
	 * @param map
	 * @return
	 */
	public float getSimilary(UserNode userNode1,UserNode userNode2,HashMap<Long,long[]> map,WeightUtil weight,UserWeightUtil userWeight,float[] weightPower);
	/**
	 * 获取用户的内容数据
	 * @param userNode1
	 * @param userNode2
	 * @param FeatherInfo feather 属性存储地址
	 * @param 是否为用户属性
	 * @return
	 */
	public float getContentSimilary(int groupId,int groupId2,UserNode userNode1,UserNode userNode2,FeatherInfo feather,float simRate,boolean isUser);
}
