package com.fc.recommend.dataBean;

import java.util.HashMap;

/**
 * 状态值
 * @author Administrator
 *
 */
public class FeatherNode {

	/**
	 * 特征对应值
	 */
	public float[] featherValue=null;
	/**
	 * 特征对应的 str值映射
	 * key 为 映射index value为对应的得分
	 */
	public HashMap<Integer,Integer> featherStrValue=new HashMap<Integer,Integer>();
}
