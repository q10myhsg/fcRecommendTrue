package com.fc.recommend.dataBean;

import java.util.HashMap;

public class EvoleBean {
	/**
	 * 命中率
	 */
	public Float preValue=null;
	/**
	 * 召回率
	 */
	public Float recallValue=null;
	/**
	 * f值
	 */
	public Float fValue=null;
	/**
	 * 推荐的数量
	 */
	public int reCount=0;	
	/**
	 * 测试集的数量
	 */
	public int testCount=0;
	/**
	 * 自己的item数量
	 */
	public int count=0;

	/**
	 * 获取 统计信息的key值
	 * 
	 * @param groupId
	 * @param category
	 * @param userId
	 * @return
	 */
	public static String getEvoleKey(int groupId, int category, int userId,String evalueSplitRegex) {
		return groupId + evalueSplitRegex + category + evalueSplitRegex
				+ userId;
	}

	/**
	 * 获取组id
	 * 
	 * @param str
	 * @return
	 */
	public static int getEvalueGroupId(String str,String evalueSplitRegex) {
		String[] split = str.split(evalueSplitRegex);
		return Integer.parseInt(split[0]);
	}

	/**
	 * 获取 分类id
	 * 
	 * @param str
	 * @return
	 */
	public static int getEvalueCategoryId(String str,String evalueSplitRegex) {
		String[] split = str.split(evalueSplitRegex);
		return Integer.parseInt(split[1]);
	}

	/**
	 * 获取evalueid
	 * 
	 * @param str
	 * @return
	 */
	public static int getEvalueUserId(String str,String evalueSplitRegex) {
		String[] split = str.split(evalueSplitRegex);
		return Integer.parseInt(split[2]);
	}

	/**
	 * 获取组string
	 * 
	 * @param str
	 * @return
	 */
	public static String getEvalueGroupId(String str,String evalueSplitRegex,
			HashMap<Integer, String> groupMap) {
		String[] split = str.split(evalueSplitRegex);
		return groupMap.get(Integer.parseInt(split[0]));
	}

	/**
	 * 获取 分类id 对应的String
	 * 
	 * @param str
	 * @return
	 */
	public static String getEvalueCategoryId(String str,String evalueSplitRegex,
			HashMap<Integer, String> categoryMap) {
		String[] split = str.split(evalueSplitRegex);
		return categoryMap.get(Integer.parseInt(split[1]));
	}

	/**
	 * 获取evalue 对应的string
	 * 
	 * @param str
	 * @return
	 */
	public static String getEvalueUserId(String str,String evalueSplitRegex,
			HashMap<Integer, String> userMap) {
		String[] split = str.split(evalueSplitRegex);
		return userMap.get(Integer.parseInt(split[2]));
	}

	/**
	 * 打印统计内容
	 * @param evalueKey
	 * @param evaleBean
	 * @param evalueSplitRegex
	 * @return
	 */
	public static String getEvalueString(String evalueKey, EvoleBean evaleBean,String evalueSplitRegex) {
		if(evaleBean==null)
		{
			return null;
		}
		int groupId=getEvalueGroupId(evalueKey,evalueSplitRegex);
		int category=getEvalueCategoryId(evalueKey,evalueSplitRegex);
		int userId=getEvalueUserId(evalueKey,evalueSplitRegex);
		StringBuffer sb=new StringBuffer();
		sb.append("组:").append(groupId).append("\t分类:").append(category).append("用户:"+userId).append("[");
		sb.append("preValue:").append(evaleBean.preValue).append(",reValue:").append(evaleBean.recallValue).append(",fValue:"+evaleBean.fValue).append("]\t[");
		sb.append("reCount:").append(evaleBean.reCount).append(",testCount:").append(evaleBean.testCount).append(",count:").append(evaleBean.count).append("]");
		return sb.toString();
		
	}
}
