package com.fc.recommend.similary2;

import java.util.HashMap;

/**
 * 通用接口
 * @author Administrator
 *
 */
public interface SimilaryUtil2 {

	/**
	 * 相似度公用方法数据集
	 */
	 HashMap<Long,Float> simiMatrix=new HashMap<Long,Float>();
	/**
	 * 是否使用内存相似度
	 */
	 boolean useSimiMatrix=true;
	
	/**
	 * 获取相似性
	 * @param userNode1
	 * @param userNode2
	 * @param 对应的权重类型
	 * @return
	 */
	public float getSimilary(float[] userNode1,float[] userNode2,float[] weight);
	/**
	 * 获取相似性
	 * @param userNode1
	 * @param userNode2
	 * @param 对应的权重类型
	 * @return
	 */
	public float getSimilary(int[] userNode1,int[] userNode2,float[] weight);
	/**
	 * 获取文本间得距离
	 * @param s1
	 * @param s2
	 * @return
	 */
	public float getSimilary(String s1,String s2);
	
}
