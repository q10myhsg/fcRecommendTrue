package com.fc.recommend.similary;

import java.util.ArrayList;
import java.util.HashMap;

import com.fc.recommend.dataBean.FeatherInfo;
import com.fc.recommend.dataBean.FeatherNode;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.filterWeight.UserWeightUtil;
import com.fc.recommend.filterWeight.WeightUtil;
import com.fc.recommend.similary2.CosinSimilary2;
import com.fc.recommend.similary2.SimilaryUtil;
import com.math.MathBase;

public class CosinSimilary implements SimilaryUtil {

	public CosinSimilary() {

	}

	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			WeightUtil weight, UserWeightUtil userWeight) {
		// TODO Auto-generated method stub
		// 获取物品
		ArrayList<ItemNode> item1 = userNode1.getItems();
		ArrayList<ItemNode> item2 = userNode2.getItems();
		float down = 0f;
		float down2 = 0f;
		for (ItemNode item : item1) {
			down += MathBase.pow(item.getValue(), 2);
		}
		if (down < 1E-10) {
			return 0f;
		}
		for (ItemNode item : item2) {
			down2 += Math.pow(item.getValue(), 2.0);
		}
		down = (float) (MathBase.sqrt(down) * MathBase.sqrt(down2));
		if (down < 1E-10) {
			return 0f;
		}
		float up = 0f;
		// System.out.println(userNode1.getUserId());
		if (weight == null) {
			for (ItemNode item : item1) {
				// System.out.print(item.getItemId()+"\t");
				ItemNode it = userNode2.getItem(item.getItemId());
				if (it == null) {
					continue;
				}
				up += item.getValue() * it.getValue();
			}
		} else {
			for (ItemNode item : item1) {
				// System.out.print(item.getItemId()+"\t");
				ItemNode it = userNode2.getItem(item.getItemId());
				if (it == null) {
					continue;
				}
				float it2 = weight.getCount(item);
				up += item.getValue() * it.getValue() / Math.log(1 + it2);
			}
		}
		// down=(float) Math.sqrt(down);
		if (userWeight == null) {
			return up / down;
		} else {
			float val = userWeight.get(userNode2.getUserId());
			return up / down * val;
		}
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map, WeightUtil weight,
			UserWeightUtil userWeight, float[] weightPower) {
		// TODO Auto-generated method stub
		// 获取对应的纬度数据
		long[] userNode1Map = map.get(userNode1.getUserId());
		long[] userNode2Map = map.get(userNode2.getUserId());
		float up = 0f;
		float down1 = 0f;
		float down2 = 0f;
		float endVal = 0f;
		if (weight == null) {
			if (weightPower == null) {
				for (int i = 0; i < userNode1Map.length; i++) {
					up += userNode1Map[i] * userNode2Map[i];
					down1 += MathBase.pow(userNode1Map[i], 2);
					down2 += MathBase.pow(userNode2Map[i], 2);
				}

				endVal = (float) (up / Math.sqrt(down1 * down2));
			} else {
				for (int i = 0; i < userNode1Map.length; i++) {
					up += userNode1Map[i] * userNode2Map[i] * weightPower[i];
					down1 += MathBase.pow(userNode1Map[i], 2) * weightPower[i];
					down2 += MathBase.pow(userNode2Map[i], 2) * weightPower[i];
				}
				endVal = (float) (up / (Math.sqrt(down1) * Math.sqrt(down2)));
			}
		} else {
			if (weightPower == null) {
				for (int i = 0; i < userNode1Map.length; i++) {
					up += userNode1Map[i] * userNode2Map[i];
					down1 += MathBase.pow(userNode1Map[i], 2);
					down2 += MathBase.pow(userNode2Map[i], 2);
				}
				// float it2=weight.getCount(item);
				endVal = (float) (up / Math.sqrt(down1 * down2));// /Math.log(1+it2);
			} else {
				for (int i = 0; i < userNode1Map.length; i++) {
					up += userNode1Map[i] * userNode2Map[i] * weightPower[i];
					down1 += MathBase.pow(userNode1Map[i], 2) * weightPower[i];
					down2 += MathBase.pow(userNode2Map[i], 2) * weightPower[i];
				}
				// float it2=weight.getCount(item);
				endVal = (float) (up / MathBase.sqrt(down1 * down2));// /Math.log(1+it2);
			}
		}
		float val = userWeight.get(userNode2.getUserId());
		return endVal * val;
	}

	@Override
	public float getContentSimilary(int groupId,int groupId2,UserNode userNode1, UserNode userNode2,
			FeatherInfo feather, float simRate, boolean isMall) {
		// TODO Auto-generated method stub
		CosinSimilary2 cosin = new CosinSimilary2();
		float sim1 = 0f;
		float sim2 = 0f;
		if (isMall) {
			FeatherNode node1=feather.getUserCGroupAllFeatherNode(groupId,userNode1.categoryId,userNode1.userId);
			if(node1==null){
				
			}else{
				FeatherNode node2=feather.getUserCGroupAllFeatherNode(groupId2,userNode2.categoryId,userNode2.userId);
				if (node2 == null) {
				} else {
					if (node1.featherValue == null
							|| node2.featherValue == null) {

					}else {
						sim1 = cosin.getSimilaryMax(node1.featherStrValue,
								node2.featherStrValue);
					}
					// 计算score的相似度
					if (node1.featherValue == null || node2.featherValue == null) {

					} else {
						sim2 = cosin.getSimilary(node1.featherValue,
								node2.featherValue, null);
					}
				}
			}	
		} else {
			// 如果为两个用户的所有物品的content方法
			// 统计所有物品的基数
			FeatherNode node1 = userNode1.getItemsFeather(groupId,feather);
			FeatherNode node2 = userNode2.getItemsFeather(groupId,feather);
			if(node1.featherStrValue==null|| node2.featherStrValue==null)
			{
			}else{
				sim1 = cosin.getSimilaryMax(node1.featherStrValue,
						node2.featherStrValue);
			}
			if(node1.featherValue==null|| node2.featherValue==null)
			{
			}else{
				sim2 = cosin.getSimilary(node1.featherValue,
						node2.featherValue, null);
			}
		}
		//System.out.println("content:simRate:"+simRate+"\t"+sim1+"\t"+sim2);
		return simRate *sim2  + (1 - simRate) * sim1;
	}

}