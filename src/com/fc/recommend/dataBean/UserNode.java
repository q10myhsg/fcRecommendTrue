package com.fc.recommend.dataBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import com.fc.recommend.dataModel.DataModel;
import com.fc.recommend.dataOut.dataBean.SonBean;
import com.fc.recommend.filter.RecommenderFilterUtil;

public class UserNode implements Comparable<UserNode> {

	public float alpha = 1f;
	/**
	 * 用户id
	 */
	public int userId = 0;
	/**
	 * 业态
	 */
	public int categoryId = 0;
	/**
	 * 物品id
	 */
	public HashMap<Integer, ItemNode> items = new HashMap<Integer, ItemNode>();

	/**
	 * 推荐的物品 有排序的
	 */
	public LinkedList<ItemNode> sortItem = new LinkedList<ItemNode>();
	/**
	 * 被过滤的部分
	 */
	public LinkedList<ItemNode> filterItem = new LinkedList<ItemNode>();
	/**
	 * 该用户的所有物品的属性汇总
	 */
	public FeatherNode itemFeatherAvg=null;
	/**
	 * 初始化
	 */
	public UserNode() {

	}
	/**
	 * 获取最终的推荐列表
	 * @param itemCount
	 * @return
	 */
	public LinkedList<SonBean> getSortItemsEnd(int itemCount)
	{
		LinkedList<SonBean> result=new LinkedList<SonBean>();
		int i=0;
		for(ItemNode item:sortItem)
		{
			i++;
			SonBean sonBean=new SonBean();
			sonBean.id=item.itemId;
			sonBean.value=item.getValue();
			sonBean.count=item.getCount();
			result.add(sonBean);
			if(i==itemCount)
			{
				break;
			}
		}
		return result;
	}

	/**
	 * 打印最终的结果
	 */
	public void printRecommand(int groupId,int category, FeatherInfo feather) {
		System.out.println(""+"分类:" + category + ":" + getUserInfo(groupId,feather)
				+ "推荐结果");
		LinkedList<String> reItems = this.getItemsListRecommand(groupId,feather);
		for (String re : reItems) {
			System.out.println(re);
		}
	}

	/**
	 * 对最终的sortItem排序 是否为降序 过滤器方法
	 */
	public void sortItem(boolean isDown,
			LinkedList<RecommenderFilterUtil> recommanderFilterUtil) {
		Collections.sort(sortItem);
		if (isDown) {
		} else {
			LinkedList<ItemNode> sortItem2 = new LinkedList<ItemNode>();
			for (ItemNode sortTemp : sortItem) {
				sortItem2.addFirst(sortTemp);
			}
			sortItem = sortItem2;
		}
		// 添加过滤器方法
		if (recommanderFilterUtil == null || recommanderFilterUtil.size() == 0) {
		} else {
			for (RecommenderFilterUtil recommanderFilter : recommanderFilterUtil) {
				recommanderFilter.filter(sortItem, filterItem);
			}
		}
	}

	/**
	 * 添加到排序中
	 * 
	 * @param itemNode
	 */
	public void addSortItem(ItemNode itemNode) {
		int i = 0;
		boolean flag = true;
		for (ItemNode itemno : sortItem) {
			if (itemno.equals(itemNode)) {
				if (itemno.getValue() > itemNode.getValue()) {
					itemno.setValue(itemno.getValue() + alpha
							* itemNode.getValue());
				} else {
					itemno.setValue(itemNode.getValue() + alpha
							* itemno.getValue());
				}
				flag = false;
			}
			i++;
		}
		if (i == 0 || flag) {
			sortItem.add(itemNode);
		}
	}

	/**
	 * 从排序完的推荐列表中踢除对应的物品
	 * 
	 * @param itemId
	 */
	public void removeSortItem(int itemId) {
		Iterator<ItemNode> iter = sortItem.iterator();
		while (iter.hasNext()) {
			ItemNode itemnode = iter.next();
			if (itemnode.getItemId() == itemId) {
				iter.remove();
				return;
			}
		}
		return;
	}

	public LinkedList<ItemNode> getSortItem() {
		return sortItem;
	}

	public LinkedList<ItemNode> getFilterItem() {
		return filterItem;
	}

	public void setFilterItem(LinkedList<ItemNode> filterItem) {
		this.filterItem = filterItem;
	}

	public void addFilterItem(ItemNode filterItem) {
		this.filterItem.add(filterItem);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setItems(HashMap<Integer, ItemNode> items) {
		this.items = items;
	}

	/**
	 * 设置用户
	 * 
	 * @param userId
	 */
	public void setUser(int userId) {
		this.userId = userId;
	}

	/**
	 * 设置物品
	 */
	public void addItem(ItemNode item) {
		this.items.put(item.getItemId(), item);
	}

	/**
	 * 添加物品 如果物品不存在则直接添加 否则 item值累加
	 * 
	 * @param item
	 */
	public void addItemAccumulation(ItemNode item) {
		ItemNode itemnode = this.items.get(item.getItemId());
		if (itemnode == null) {
			addItem(item);
		} else {
			itemnode.setCount(itemnode.getCount() + item.getCount());
		}
	}

	/**
	 * 移出一个物品
	 * 
	 * @param item
	 */
	public void remove(long itemId) {
		items.remove(itemId);
	}

	/**
	 * 物品数量
	 * 
	 * @return
	 */
	public int size() {
		return this.items.size();
	}

	/**
	 * 获取全部物品
	 * 
	 * @return
	 */
	public ArrayList<ItemNode> getItems() {
		ArrayList<ItemNode> result = new ArrayList<ItemNode>();
		for (Entry<Integer, ItemNode> item : items.entrySet()) {
			result.add(item.getValue());
		}
		return result;
	}

	/**
	 * 获取全部物品
	 * 
	 * @return
	 */
	public LinkedList<ItemNode> getItemsLinked() {
		LinkedList<ItemNode> result = new LinkedList<ItemNode>();
		for (Entry<Integer, ItemNode> item : items.entrySet()) {
			result.add(item.getValue());
		}
		return result;
	}

	public Set<Entry<Integer, ItemNode>> entrySet() {
		return items.entrySet();
	}

	public void removeItem(ItemNode itemNode) {
		items.remove(itemNode.getItemId());
	}

	/**
	 * 通过商品编号获取商品信息
	 * 
	 * @param itemId
	 * @return
	 */
	public ItemNode getItem(int itemId) {
		return items.get(itemId);
	}

	public int compareTo(UserNode other) {
		return Integer.compare(userId, other.userId);
	}

	public boolean equals(Object in) {
		UserNode st = (UserNode) in;
		return this.userId == st.userId;
	}

	public int hashCode() {
		return (int) userId & 0x7FFFFFFF;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param userFeatherNode
	 * @param feather
	 * @return
	 */
	public StringBuffer getUserInfo(int groupId,FeatherInfo feather) {
		StringBuffer sbU = new StringBuffer("[用户:").append(userId)
				.append(":分类:").append(this.categoryId).append("]:");
		HashMap<Integer,HashMap<Integer,FeatherNode>> groupNode=feather.userCGroupAll.get(groupId);
		if(groupNode==null)
		{
			return sbU;
		}
		HashMap<Integer, FeatherNode> featherMap = groupNode
				.get(this.categoryId);

		if (featherMap == null) {
			return sbU;
		}
		FeatherNode userFeatherNode = featherMap.get(userId);
		if (userFeatherNode == null) {
			System.out.println(userId + "\t属性为空");
		} else {
			if (userFeatherNode.featherValue == null) {
				sbU.append("[]:[");
			} else {
				sbU.append(Arrays.toString(userFeatherNode.featherValue))
						.append(":[");
			}
			int index = 0;
			if (userFeatherNode.featherStrValue == null) {

			} else {
				for (Entry<Integer, Integer> mp : userFeatherNode.featherStrValue
						.entrySet()) {
					index++;
					if (index != 1) {
						sbU.append(",");
					}
					sbU.append(feather.getIndex(mp.getKey())).append(":")
							.append(mp.getValue());
				}
			}
			sbU.append("]");
		}
		return sbU;
	}

	/**
	 * 获取物品的信息
	 * 
	 * @param it
	 * @param feather
	 * @return
	 */
	public StringBuffer getItemInfo(ItemNode it,int groupId, FeatherInfo feather) {
		if(it.itemId==19408)
		{
			System.out.println();
		}
		StringBuffer sb = new StringBuffer("[");
		sb.append(it.getItemId()).append(":").append(it.categoryId).append(":")
				.append(it.getValue()).append(":").append(it.getCount())
				.append("]");
		HashMap<Integer,HashMap<Integer,FeatherNode>> groupNode=feather.itemCGroupAll.get(groupId);
		if(groupNode==null)
		{
			return sb;
		}
		HashMap<Integer, FeatherNode> featherMap = groupNode
				.get(it.categoryId);
		if (featherMap == null) {
			return sb;
		}
		FeatherNode featherNode = featherMap.get(it.getItemId());
		if (featherNode != null) {
			sb.append(":");

			if (featherNode.featherValue == null) {
				sb.append("[]:[");
			} else {
				sb.append(Arrays.toString(featherNode.featherValue)).append(
						":[");
			}
			int index = 0;
			if (featherNode.featherStrValue == null) {

			} else {
				for (Entry<Integer, Integer> mp : featherNode.featherStrValue
						.entrySet()) {
					index++;
					if (index != 1) {
						sb.append(",");
					}
					sb.append(feather.getIndex(mp.getKey())).append(":")
							.append(mp.getValue());
				}
			}
			sb.append("]");
		}
		return sb;
	}

	/**
	 * 获取推荐物品的信息
	 * 
	 * @param it
	 * @param feather
	 * @return
	 */
	public StringBuffer getItemInfoRecommand(ItemNode it,int groupId, FeatherInfo feather) {
		return getItemInfo(it, groupId,feather);
	}

	/**
	 * 打印
	 */
	public void print(int groupId,FeatherInfo feather) {
		StringBuffer sbU = getUserInfo(groupId,feather);
		System.out.println(sbU.toString());
		for (Entry<Integer, ItemNode> item : items.entrySet()) {
			ItemNode it = item.getValue();
			StringBuffer sb = getItemInfo(it,groupId, feather);
			System.out.println(sb.toString());
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param feather
	 * @return
	 */
	public String getUserString(int groupId,FeatherInfo feather) {
		StringBuffer sbU = getUserInfo(groupId,feather);
		return sbU.toString();
	}

	/**
	 * 获取该用户下的所有物品
	 * 
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemsList(int groupId,FeatherInfo feather) {
		LinkedList<String> list = new LinkedList<String>();
		for (Entry<Integer, ItemNode> item : items.entrySet()) {
			ItemNode it = item.getValue();
			StringBuffer sb = getItemInfo(it,groupId, feather);
			list.add(sb.toString());
		}
		return list;
	}

	/**
	 * 获取该用户下推荐的所有物品
	 * 
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemsListRecommand(int groupId,FeatherInfo feather) {
		LinkedList<String> list = new LinkedList<String>();
		for (ItemNode item : sortItem) {
			StringBuffer sb = getItemInfo(item, groupId,feather);
			list.add(sb.toString());
		}
		return list;
	}

	/**
	 * 获取某个物品信息
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public String getItemString(int itemId,int groupId,FeatherInfo feather) {
		ItemNode item = items.get(itemId);
		if (item == null) {
			return null;
		}
		StringBuffer sb = getItemInfo(item,groupId, feather);
		return sb.toString();
	}

	/**
	 * 获取该用户下的 某个推荐物品
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public String getItemStringRecommand(int itemId,int groupId, FeatherInfo feather) {
		for (ItemNode item : sortItem) {
			if (item.getItemId() == itemId) {
				StringBuffer sb = getItemInfo(item,groupId, feather);
				return sb.toString();
			}
		}
		return null;
	}

	/**
	 * 如果该用户存在物品则返回用户信息
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public String getItemToUserString(int itemId,int groupId, FeatherInfo feather) {
		ItemNode item = items.get(itemId);
		if (item == null) {
			return null;
		}
		return getUserString(groupId,feather);
	}

	/**
	 * 如果该用户存推荐的物品则返回用户信息
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public String getItemToUserStringRecommand(int itemId,int groupId, FeatherInfo feather) {
		for (ItemNode item : sortItem) {
			if (item.getItemId() == itemId) {
				return getUserString(groupId,feather);
			}
		}
		return null;
	}

	/**
	 * 获取物品的所有属性的平均值
	 * 
	 * @param items
	 * @param feather
	 * @return
	 */
	public FeatherNode getItemsFeather(int groupId,FeatherInfo feather) {
		if(itemFeatherAvg!=null)
		{
			return itemFeatherAvg;
		}
		ArrayList<ItemNode> items = this.getItems();
		itemFeatherAvg = new FeatherNode();
		int count = 0;
		float[] score = null;
		HashMap<Integer, Integer> itemsMap = new HashMap<Integer, Integer>();
		for (ItemNode item : items) {
//			if(feather==null||feather.itemCGroupAll==null)
//			{
//				System.out.println(groupId+":"+feather+"\t"+feather.itemCGroupAll);
//			}
			HashMap<Integer,HashMap<Integer,FeatherNode>> groupFeather=feather.itemCGroupAll.get(groupId);
			if(groupFeather==null)
			{
				continue;
			}
			HashMap<Integer, FeatherNode> featherNode1 = groupFeather.get(item.categoryId);
			if (featherNode1 == null) {
				continue;
			}
			FeatherNode node1 = featherNode1.get(item.itemId);
			if (node1 == null) {
				continue;
			}
			// 计算结构化数据的平均值
			if (score == null) {
				if (node1.featherValue == null) {
				} else {
					score = new float[node1.featherValue.length];
					for(int i=0;i<score.length;i++)
					{
						if(Float.compare(0f,node1.featherValue[i])==0)
						{
							continue;
						}
						score[i]+=node1.featherValue[i];
						count++;
					}
				}
			}else{
				for(int i=0;i<score.length;i++)
				{
					if(Float.compare(0f,node1.featherValue[i])==0)
					{
						continue;
					}
					score[i]+=node1.featherValue[i];
					count++;
				}
			}
			//计算非机构化数据的统计值
			for(Entry<Integer,Integer> m:node1.featherStrValue.entrySet())
			{
				Integer val=itemsMap.get(m.getKey());
				if(val==null)
				{
					itemsMap.put(m.getKey(),m.getValue());
				}else{
					itemsMap.put(m.getKey(),val+m.getValue());
				}
			}
		}
		if(count>0)
		{
			for(int i=0;i<score.length;i++)
			{//获取均值
				score[i]/=count;
			}
		}
		itemFeatherAvg.featherValue=score;
		itemFeatherAvg.featherStrValue=itemsMap;
		return itemFeatherAvg;
	}
	
	/**
	 * 获取用户下所有物品的关联的用户
	 * @return
	 */
	public HashMap<Integer,HashSet<String>> getUserItemToUser(int groupId,DataModel dataModel,DataModel contentModel)
	{
		HashMap<Integer,HashSet<String>> map=new HashMap<Integer,HashSet<String>>();
		for(Entry<Integer,ItemNode> item:this.items.entrySet())
		{
			map.put(item.getKey(),dataModel.getItemToUserSet(item.getKey(), groupId,contentModel));
		}
		return map;
	}
}
