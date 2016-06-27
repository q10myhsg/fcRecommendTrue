package com.fc.recommend.filter;

import java.util.HashMap;

import com.fc.recommend.dataBean.FeatherInfo;
import com.fc.recommend.dataBean.FeatherNode;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.filterWeight.WeightUtil;
import com.fc.recommend.similary.CosinSimilary;
import com.fc.recommend.similary2.CosinSimilary2;

/**
 * 获取补集
 * 
 * @author Administrator
 * 
 */
public class ItemGetCel implements ItemGetRurl {

	@Override
	public void getRecommendItems(int groupId, UserNode user1, UserNode user2,
			float sim, FeatherInfo feather, float simRate, WeightUtil weight) {
		if (sim < 1E-10) {
			return;
		}
		// 用户1的属性
		FeatherNode user1Feather = null;
		if (feather != null) {
			HashMap<Integer, FeatherNode> map = feather.userCGroupAll.get(
					groupId).get(user1.categoryId);
			if (map != null) {
				user1Feather = map.get(user1.getUserId());
			}
		}
		for (ItemNode item2 : user2.getItems()) {
			// 如果不在 用户 1中
			ItemNode temp = user1.getItem(item2.getItemId());
			if (temp == null) {
				// 如果为正常协同过滤则为null,如果为content则添加
				if (weight == null) {
					// ***************需要添加match方法实现硬匹配方法
					if (user1Feather != null) {
						FeatherNode itemFeather = null;
						if (feather != null) {
							HashMap<Integer, HashMap<Integer, FeatherNode>> groupNode = feather.itemCGroupAll
									.get(groupId);
							if (groupNode == null) {

							} else {
								HashMap<Integer, FeatherNode> map = groupNode
										.get(item2.categoryId);
								if (map != null) {
									itemFeather = map.get(item2.getItemId());
								}
							}
						}
						float sim2 = getMathRate(user1Feather, itemFeather,
								simRate);
						sim2 = Float.compare(sim2,0f)==0 ? simRate/10:sim2;
//						System.out.println("用户:" + user1.getUserId()
//								+ "\t:item:" + item2.getItemId() + "\t:sim:"
//								+ sim2);
						user1.addSortItem(new ItemNode(item2.getItemId(),
								item2.categoryId, item2.getValue()
										* ((1-simRate)*sim +simRate* sim2), item2.getCount()));
					} else {
						user1.addSortItem(new ItemNode(item2.getItemId(),
								item2.categoryId, item2.getValue() * (1-simRate)*sim, item2
										.getCount()));
					}
				} else {
					// 获取总量
					if (user1Feather != null) {
						FeatherNode itemFeather = null;
						if (feather != null) {
							HashMap<Integer, HashMap<Integer, FeatherNode>> groupNode = feather.itemCGroupAll
									.get(groupId);
							if (groupNode == null) {
							} else {
								HashMap<Integer, FeatherNode> map = groupNode
										.get(item2.categoryId);
								if (map != null) {
									itemFeather = map.get(item2.getItemId());
								}
							}
						}
						float sim2 = getMathRate(user1Feather, itemFeather,
								simRate);
						//System.out.println((Float.compare(sim2,0f)==0));
						sim2 = Float.compare(sim2,0f)==0 ? simRate/10:sim2;
//						System.out.println("用户:" + user1.getUserId()
//								+ "\t:item:" + item2.getItemId() + "\t:sim2:"
//								+ sim2);
						float it2 = weight.getCount(item2);
//						System.out.println("end:"+((1-simRate)*sim +simRate* sim2)+"\t:"+Math.log(1 + it2));
//						System.out.println("endValue:"+ (float) (item2.getValue()
//								* ((1-simRate)*sim +simRate* sim2) / Math.log(1 + it2)));
						user1.addSortItem(new ItemNode(item2.getItemId(),
								item2.categoryId, (float) (item2.getValue()
										* ((1-simRate)*sim +simRate* sim2) / Math.log(1 + it2)),
								item2.getCount()));
					} else {
						float it2 = weight.getCount(item2);
						user1.addSortItem(new ItemNode(item2.getItemId(),
								item2.categoryId, (float) (item2.getValue()
										* (sim*(1-simRate)) / Math.log(1 + it2)), item2
										.getCount()));
					}
				}
			} else {
				// System.out.println(user1.getUserId()+":存在物品:"+item2.getItemId()+":用户:"+user2.getUserId()+":sim:"+sim+":size:"+user2.size());
			}
		}
	}

	/**
	 * 计算匹配度
	 * 
	 * @param userNode1Map
	 * @param userNode2Map
	 * @return
	 */
	public float getMathRate(FeatherNode userFeather, FeatherNode itemFeather,
			float simR) {
		if (userFeather == null || itemFeather == null) {
			return 0f;
		}
		CosinSimilary2 cosin = new CosinSimilary2();
		float sim1 = 0f;
		if (userFeather.featherStrValue == null
				|| itemFeather.featherStrValue == null) {
		} else {
			//使用match方法获取 用户和物品的匹配数
			sim1 = cosin.getSimilaryMatch(userFeather.featherStrValue,
					itemFeather.featherStrValue,lossValue);
		}
		float sim2 = 0f;
		if (userFeather.featherValue == null
				|| itemFeather.featherValue == null) {
		} else {
			sim2 = cosin.getSimilary(userFeather.featherValue,
					itemFeather.featherValue, null);
		}
//		System.out.println("simR:"+simR+"\tsim1:"+sim1+"\tsim2:"+sim2);
//		System.out.println("simValue="+(simR * sim2 + (1 - simR) * sim1));
		return simR * sim2 + (1 - simR) * sim1;
	}

}
