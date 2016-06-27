package com.fc.recommend.filterWeight;

import java.util.Map.Entry;

import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserInfo;
import com.fc.recommend.dataBean.UserNode;

/**
 * 计算用户相似度的时候 做热门物品损失 sum(x*y*1/(log(1+n)))/n1*n2
 * 
 * *1/(log(1+n)
 * 
 * @author Administrator
 *
 */
public class UserHotItemLoss implements WeightUtil {
	public UserNode userNode = null;

	public UserHotItemLoss() {

	}

	public void init(UserInfo userInfo) {
		userNode = new UserNode();
		for (Entry<Integer, UserNode> user : userInfo.entrySet()) {
			for (Entry<Integer, ItemNode> item : user.getValue().entrySet()) {
				// 统计所有用户的物品信息
				ItemNode item2 = new ItemNode(item.getKey(),
						item.getValue().categoryId, 1, 1);
				userNode.addItemAccumulation(item2);
				//System.out.println("ini:"+item.getKey());
			}
		}
		// 将元数据 数量设置上
		for (Entry<Integer, UserNode> user : userInfo.entrySet()) {
			for (Entry<Integer, ItemNode> item : user.getValue().entrySet()) {
				// System.out.println(item.getKey()+"\t"+userNode.getItem(item.getKey()).getCount());
				item.getValue().setCount(
						userNode.getItem(item.getKey()).getCount());
			}
		}
	}

	@Override
	public void add(UserInfo userInfo) {
		// TODO Auto-generated method stub
		if (userNode == null) {
			userNode = new UserNode();
		}
		for (Entry<Integer, UserNode> user : userInfo.entrySet()) {
			for (Entry<Integer, ItemNode> item : user.getValue().entrySet()) {
				// 统计所有用户的物品信息
				ItemNode item2 = new ItemNode(item.getKey(),
						item.getValue().categoryId, 1, 1);
				userNode.addItemAccumulation(item2);
				//System.out.println("add:"+item.getKey());
			}
		}
		// 将元数据 数量设置上
		for (Entry<Integer, UserNode> user : userInfo.entrySet()) {
			for (Entry<Integer, ItemNode> item : user.getValue().entrySet()) {
				// System.out.println(item.getKey()+"\t"+userNode.getItem(item.getKey()).getCount());
				item.getValue().setCount(
						userNode.getItem(item.getKey()).getCount());
			}
		}
	}

	@Override
	public int getCount(ItemNode item) {
		// TODO Auto-generated method stub
		// 通过userNode1的物品获取userNode2物品
		ItemNode item2 = userNode.getItem(item.getItemId());
		return item2.getCount();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return userNode.size();
	}

	@Override
	public int getCount(int itemId) {
		// TODO Auto-generated method stub
		ItemNode item2 = userNode.getItem(itemId);
		try {
			int zn = item2.getCount();
			System.out.println(zn);
		} catch (Exception e) {
			System.out.println("zn:" + itemId);
			for(Entry<Integer,ItemNode> item:userNode.items.entrySet())
			{
				System.out.println(":"+item.getKey());
			}
		}
		return item2.getCount();
	}

}
