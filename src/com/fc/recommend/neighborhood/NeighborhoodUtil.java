package com.fc.recommend.neighborhood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.fc.recommend.cf.Recommend;
import com.fc.recommend.dataBean.FeatherInfo;
import com.fc.recommend.dataBean.FeatherNode;
import com.fc.recommend.dataBean.UserInfo;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.similary.CosinSimilary;
import com.fc.recommend.similary2.SimilaryUtil;
import com.sort.SortFast;
import com.sort.SortUtil;

/**
 * 邻域计算规则 如果为其他方法需要继承父类方法
 * 
 * @author Administrator
 *
 */
public class NeighborhoodUtil {

	/**
	 * 相似度的下限值 为 cf+itemContent的相似度
	 */
	public float similaryRate = 0.3f;
	/**
	 * 混合用户的user相似度
	 */
	public float comSimilaryRate = 0.1f;
	/**
	 * 对应 content中 结构化的比率 和非结构化的 1-score 的比率
	 */
	public float userItemRate = 0.1f;
	/**
	 * 获取的topN个最相似的用户
	 */
	public int similaryCount = 20;
	public float similaryMin=-0.1f;
	/**
	 * 最近最近邻相思的计算方法
	 */
	public SimilaryUtil simiFunction = new CosinSimilary();
	/**
	 * 用户内容相似性
	 */
	public SimilaryUtil simiUserContentFunction = new CosinSimilary();
	/**
	 * 排序方法
	 */
	public SortUtil sortFunction = new SortFast();

	/**
	 * @param userNode1
	 *            使用的用户
	 * @param userInfo
	 *            同组的其他用户相似度 返回的如果为0 则表示无效
	 * @param FeatherInfo
	 *            属性值
	 * @param categoryRel
	 *            是否存在对应的分类属性
	 */
	public NeighBean getNearUser(int groupId,int groupId2, UserNode userNode1,
			UserInfo userInfo, int category, FeatherInfo feather,
			HashMap<Integer, HashMap<Integer, Integer>> categoryRel) {
		if (categoryRel == null) {
			return getNearUser(groupId,groupId2, userNode1, userInfo, feather);
		}
		int index = -1;
		float simiTemp = similaryMin;
		float[] queue = new float[similaryCount + 1];
		int[] queueId = new int[similaryCount + 1];
		for (Entry<Integer, UserNode> userNode2 : userInfo.entrySet()) {
			if (userNode1.getUserId() == userNode2.getKey()) {
				continue;
			}
			HashMap<Integer, Integer> map = categoryRel.get(userNode2.getKey());
			if (map == null) {
				continue;
			}
			Integer count = map.get(category);
			if (count == null) {
				continue;
			}
			float simiValue = simiFunction.getSimilary(userNode1,
					userNode2.getValue(), null, null);
			// System.out.println("sim:"+simiValue);
			
				// 判断用户的content相似度
				float simiUserSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								true);
				// System.out.println("sim:"+simiValue+"\tsim2:"+simiUserSimiValue);
				simiValue += this.similaryRate * simiUserSimiValue;
				if (simiValue < comSimilaryRate) {
					continue;
				}
				// 两个用户的所有物品的属性相似度
				float simiItemSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								false);
				// System.out.println("sim:"+simiValue+"\tsim2:"+simiUserSimiValue+"\tsim3:"+simiItemSimiValue);
				// 最终的整合值
				simiValue = simiValue + this.similaryRate * simiItemSimiValue;
				// System.out.println("sim:"+simiValue);
				if (simiValue > simiTemp) {
				index++;
				if (index > similaryCount) {
					index = similaryCount;
				}
				sortFunction.quickSort(simiValue, userNode2.getKey(), index,
						queue, queueId, true);
				float val = queue[similaryCount - 1];
				if (val > simiTemp) {
					simiTemp = val;
				}
			}
		}
		if (index == -1) {
			return new NeighBean(queueId, queue);
		}
		queueId[similaryCount] = 0;
		return new NeighBean(queueId, queue);
	}

	/**
	 * @param userNode1
	 *            使用的用户
	 * @param userInfo
	 *            同组的其他用户相似度 返回的如果为0 则表示无效
	 * @param FeatherInfo
	 *            属性值
	 * @param categoryRel
	 *            是否存在对应的分类属性
	 */
	public NeighBean getNearUserAll(int groupId,int groupId2, UserNode userNode1,
			UserInfo userInfo, int category, FeatherInfo feather,
			HashMap<Integer, HashMap<Integer, Integer>> categoryRel,
			float simiLit, float comSimiLimit) {
		if (categoryRel == null) {
			return getNearUser(groupId,groupId2, userNode1, userInfo, feather);
		}
		int index = -1;
		float simiTemp = simiLit;
		float[] queue = new float[userInfo.size() + 1];
		int[] queueId = new int[userInfo.size() + 1];
		for (Entry<Integer, UserNode> userNode2 : userInfo.entrySet()) {
			if (userNode1.getUserId() == userNode2.getKey()) {
				continue;
			}
			HashMap<Integer, Integer> map = categoryRel.get(userNode2.getKey());
			if (map == null) {
				continue;
			}
			Integer count = map.get(category);
			if (count == null) {
				continue;
			}
			float simiValue = simiFunction.getSimilary(userNode1,
					userNode2.getValue(), null, null);
				// 判断用户的content相似度
				float simiUserSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								true);
				// System.out.println("sim:"+simiValue+"\tsim2:"+simiUserSimiValue);
				simiValue += this.similaryRate * simiUserSimiValue;
				if (simiValue < comSimiLimit) {
					continue;
				}
				// 两个用户的所有物品的属性相似度
				float simiItemSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								false);
				// System.out.println("sim:"+simiValue+"\tsim2:"+simiUserSimiValue+"\tsim3:"+simiItemSimiValue);
				// 最终的整合值
				simiValue = simiValue + this.similaryRate * simiItemSimiValue;
				// System.out.println("sim:"+simiValue);
				if (simiValue > simiTemp) {
				index++;
				if (index > similaryCount) {
					index = similaryCount;
				}
				sortFunction.quickSort(simiValue, userNode2.getKey(), index,
						queue, queueId, true);
			}
		}
		if (index == -1) {
			index = notItemsMethod(groupId,groupId2, userNode1, userInfo, comSimiLimit,
					queueId, queue, feather);
		}
		int[] queue2 = new int[index + 1];
		float[] queueF = new float[index + 1];
		for (int i = 0; i <= index; i++) {
			queue2[i] = queueId[i];
			queueF[i] = queue[i];
		}
		return new NeighBean(queue2, queueF);
	}

	/**
	 * @param userNode1
	 *            使用的用户
	 * @param userInfo
	 *            同组的其他用户相似度 返回的如果为0 则表示无效
	 * @param categoryRel
	 *            是否存在对应的分类属性
	 */
	public NeighBean getNearUser(int groupId,int groupId2, UserNode userNode1,
			UserInfo userInfo, FeatherInfo feather) {
		int index = -1;
		float simiTemp = 0;
		float[] queue = new float[similaryCount + 1];
		int[] queueId = new int[similaryCount + 1];
		for (Entry<Integer, UserNode> userNode2 : userInfo.entrySet()) {
			if (userNode1.getUserId() == userNode2.getKey()) {
				continue;
			}
			//System.out.println("g:"+groupId2+"\tc:"+userNode2.getValue().categoryId+"\tu:"+userNode2.getValue().userId);
			float simiValue = simiFunction.getSimilary(userNode1,
					userNode2.getValue(), null, null);
				// 判断用户的content相似度
				float simiUserSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								true);
				// System.out.println("进入:"+groupId+"\t"+userNode1.userId+"\t"+simiValue+"\t"+simiUserSimiValue);
				simiValue += this.similaryRate * simiUserSimiValue;
				if (simiValue < comSimilaryRate) {
					continue;
				}
				// 两个用户的所有物品的属性相似度
				float simiItemSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								false);
				// 最终的整合值
				simiValue = simiValue + this.similaryRate * simiItemSimiValue;
				//System.out.println(simiValue);
				if (simiValue >= simiTemp) {
				index++;
				if (index > similaryCount) {
					index = similaryCount;
				}
				sortFunction.quickSort(simiValue, userNode2.getKey(), index,
						queue, queueId, true);
				float val = queue[similaryCount - 1];
				if (val > simiTemp) {
					simiTemp = val;
				}
			}
		}
		if (index == -1) {
			index = notItemsMethod(groupId,groupId2, userNode1, userInfo,
					comSimilaryRate, queueId, queue, feather);
		}
		queueId[similaryCount] = 0;
		return new NeighBean(queueId, queue);
	}

	/**
	 * @param userNode1
	 *            使用的用户
	 * @param userInfo
	 *            同组的其他用户相似度 返回的如果为0 则表示无效
	 * @param categoryRel
	 *            是否存在对应的分类属性
	 * @param simLimit
	 *            最小相似度
	 * @param 返回所有相似的用户不考虑
	 */
	public NeighBean getNearUserAll(int groupId,int groupId2, UserNode userNode1,
			UserInfo userInfo, FeatherInfo feather, float simLimit,
			float comSimiLimit) {
		int index = -1;
		float simiTemp = simLimit;
		float[] queue = new float[userInfo.size() - 1];
		int[] queueId = new int[userInfo.size() - 1];
		for (Entry<Integer, UserNode> userNode2 : userInfo.entrySet()) {
			if (userNode1.getUserId() == userNode2.getKey()) {
				continue;
			}
			float simiValue = simiFunction.getSimilary(userNode1,
					userNode2.getValue(), null, null);
			if (simiValue > simiTemp) {
				// 判断用户的content相似度
				float simiUserSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								true);
				if (simiUserSimiValue < comSimiLimit) {
					continue;
				}
				// 两个用户的所有物品的属性相似度
				float simiItemSimiValue = simiUserContentFunction
						.getContentSimilary(groupId,groupId2, userNode1,
								userNode2.getValue(), feather, userItemRate,
								false);
				// 最终的整合值
				simiValue = simiValue + this.similaryRate * simiItemSimiValue;
				index++;
				if (index > similaryCount) {
					index = similaryCount;
				}
				sortFunction.quickSort(simiValue, userNode2.getKey(), index,
						queue, queueId, true);
			}
		}
		if (index == -1) {
			// 如果不存在相似的则需要跑第二套 通过属性集获取相似用户的方法
			// 按照属性做匹配
			index = notItemsMethod(groupId,groupId2, userNode1, userInfo, comSimiLimit,
					queueId, queue, feather);
		}
		int[] queue2 = new int[index + 1];
		float[] queueF = new float[index + 1];
		for (int i = 0; i <= index; i++) {
			queue2[i] = queueId[i];
			queueF[i] = queue[i];
		}
		return new NeighBean(queue2, queueF);
	}

	/**
	 * 处理没有物品的推荐
	 * 
	 * @param groupId
	 *            组
	 * @param index
	 *            值
	 * @param userNode1
	 * @param userInfo
	 * @param comSimlaryRate
	 * @param queueId
	 * @param queue
	 * @param feather
	 * @return
	 */
	public int notItemsMethod(int groupId,int groupId2, UserNode userNode1,
			UserInfo userInfo, float comSimlaryRate, int[] queueId,
			float[] queue, FeatherInfo feather) {
		int index = -1;
		for (Entry<Integer, UserNode> userNode2 : userInfo.entrySet()) {
			if (userNode1.getUserId() == userNode2.getKey()) {
				continue;
			}
			float simiUserSimiValue = simiUserContentFunction
					.getContentSimilary(groupId,groupId2, userNode1,
							userNode2.getValue(), feather, userItemRate, Recommend.isMall);
			if (simiUserSimiValue < comSimilaryRate) {
				continue;
			}
			index++;
			if (index > similaryCount) {
				index = similaryCount;
			}
			sortFunction.quickSort(simiUserSimiValue, userNode2.getKey(),
					index, queue, queueId, true);
		}
		return index;
	}

}
