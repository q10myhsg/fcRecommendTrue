package com.fc.recommend.dataBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import com.math.MathBase;

/**
 * 特征的基础数据集
 * 
 * @author Administrator
 *
 */
public class FeatherInfo {

	/**
	 * 属性映射值
	 */
	public HashMap<String, Integer> index = new HashMap<String, Integer>();
	/**
	 * 反向映射
	 */
	public HashMap<Integer, String> indexRel = new HashMap<Integer, String>();
	/**
	 * 大小
	 */
	public int size = 0;
	/**
	 * 存储id对应的属性值 训练集内容
	 */
	public HashMap<Integer, HashMap<Integer, HashMap<Integer, FeatherNode>>> userCGroupAll = new HashMap<Integer, HashMap<Integer, HashMap<Integer, FeatherNode>>>();
	/**
	 * 训练集 物品内容 categoryCode*(groupId+1) itemId
	 */
	public HashMap<Integer, HashMap<Integer, HashMap<Integer, FeatherNode>>> itemCGroupAll = new HashMap<Integer, HashMap<Integer, HashMap<Integer, FeatherNode>>>();

	/**
	 * 获取可重复使用的userId属性
	 * @param groupId
	 * @param category
	 * @param userId
	 * @return
	 */
	public FeatherNode getUserCGroupAllFeatherNode(int groupId,int category,int userId){
		HashMap<Integer, HashMap<Integer, FeatherNode>> cateMap=userCGroupAll.get(groupId);
		if(cateMap==null){
			return node(userCGroupAll,groupId,category,userId);
		}
		HashMap<Integer,FeatherNode> valMap=cateMap.get(category);
		if(valMap==null){
			return node(userCGroupAll,groupId,category,userId);
		}
		FeatherNode val=valMap.get(userId);
		if(val==null){
			return node(userCGroupAll,groupId,category,userId);
		}
		return val;
	}
	/**
	 * 获取可重复使用的userId属性
	 * @param groupId
	 * @param category
	 * @param userId
	 * @return
	 */
	public FeatherNode getItemCGroupAllFeatherNode(int groupId,int category,int itemId){
		HashMap<Integer, HashMap<Integer, FeatherNode>> cateMap=itemCGroupAll.get(groupId);
		if(cateMap==null){
			return node(itemCGroupAll,groupId,category,itemId);
		}
		HashMap<Integer,FeatherNode> valMap=cateMap.get(category);
		if(valMap==null){
			return node(itemCGroupAll,groupId,category,itemId);
		}
		FeatherNode val=valMap.get(itemId);
		if(val==null){
			return node(itemCGroupAll,groupId,category,itemId);
		}
		return val;
	}
	
	public FeatherNode node(HashMap<Integer, HashMap<Integer, HashMap<Integer, FeatherNode>>> all,int groupId,int category,int userId){
		for(Entry<Integer,HashMap<Integer,HashMap<Integer,FeatherNode>>> map:all.entrySet()){
			if(map.getKey()==groupId){
				continue;
			}
			HashMap<Integer,FeatherNode> catMap=map.getValue().get(category);
			if(catMap==null){
				continue;
			}
			FeatherNode featherNode=catMap.get(userId);
			if(featherNode==null){
				continue;
			}
			return featherNode;
		}
		return null;
	}
	
	
	
	/**
	 * 新增index
	 * 
	 * @param str
	 * @return
	 */
	public synchronized int addIndex(String str) {
		Integer i = index.get(str);
		if (i == null) {
			this.size++;
			index.put(str, this.size);
			indexRel.put(this.size, str);
			return this.size;
		} else {
			return i;
		}
	}

	/**
	 * 获取属性
	 * 
	 * @param index
	 * @return
	 */
	public synchronized String getIndex(int index) {
		return indexRel.get(index);
	}

	/**
	 * 获取属性
	 * 
	 * @param index
	 * @return
	 */
	public synchronized Integer getIndex(String index) {
		return this.index.get(index);
	}

	/**
	 * 获取具体的feathernode值 string feather string:val
	 * 
	 * @param userId
	 * @param str
	 * @return
	 */
	public FeatherNode getFeatherNode(String[] str, int featherLength) {
		FeatherNode value = new FeatherNode();
		float[] fValue = new float[featherLength];
		// int id=Integer.parseInt(str[0]);
		// System.out.println(Arrays.toString(str));
		for (int i = 3; i < featherLength + 3; i++) {
			// System.out.println(str[i]);
			try{
				fValue[i - 3] = Float.parseFloat(str[i]);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(i);
				System.out.println(Arrays.toString(str));
				System.exit(1);
			}
		}
		value.featherValue = fValue;
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = featherLength + 3; i < str.length; i++) {
			String[] sLN = str[i].split(",");
			for (String szn : sLN) {
				String[] sL = szn.split(":");
				if (sL[0].equals("null")) {
					continue;
				}
				Integer indexL = addIndex(sL[0]);
				//System.out.print(sL[0]);
				if (sL.length > 1) {
					map.put(indexL, Integer.parseInt(sL[1]));
				} else {
					map.put(indexL,1);
				}
			}
		}
	//	System.out.println();
		if (map.size() != 0) {
			value.featherStrValue = map;
		}
		return value;
	}

	/**
	 * 添加到content中
	 * 
	 * @param userId
	 * @param str
	 * @param featherLength
	 */
	public void addContent(String[] str, int featherLength) {
		Integer groupId = Integer.parseInt(str[1]);
		Integer category = Integer.parseInt(str[2]);
		//System.out.println("addUser:"+str);
		HashMap<Integer, HashMap<Integer, FeatherNode>> groupNode = userCGroupAll
				.get(groupId);
		if (groupNode == null) {
			groupNode = new HashMap<Integer, HashMap<Integer, FeatherNode>>();
			userCGroupAll.put(groupId, groupNode);
			HashMap<Integer, FeatherNode> featherNode = new HashMap<Integer, FeatherNode>();
			featherNode.put(Integer.parseInt(str[0]),
					getFeatherNode(str, featherLength));
			groupNode.put(category, featherNode);
			return;
		}
		HashMap<Integer, FeatherNode> featherNode = groupNode.get(category);
		if (featherNode == null) {
			// 如果不存在业态则新增
			featherNode = new HashMap<Integer, FeatherNode>();
			featherNode.put(Integer.parseInt(str[0]),
					getFeatherNode(str, featherLength));
			groupNode.put(category, featherNode);
		} else {
			featherNode.put(Integer.parseInt(str[0]),
					getFeatherNode(str, featherLength));
		}
	}

	/**
	 * 添加item到content中
	 * 
	 * @param itemId
	 * @param str
	 * @param featherLength
	 *            第0个为 itemid 第一个为 业态
	 */
	public void addContentItem(String[] str, int featherLength) {
		//System.out.println("addItem:"+Arrays.toString(str));
		Integer groupId = Integer.parseInt(str[1]);
		Integer category = Integer.parseInt(str[2]);
		
//		System.out.println(Arrays.toString(str));
//		if(str[0].equals("19408"))
//		{
//			System.out.println();
//		}
		HashMap<Integer, HashMap<Integer, FeatherNode>> groupNode = itemCGroupAll
				.get(groupId);
		if (groupNode == null) {
			groupNode = new HashMap<Integer, HashMap<Integer, FeatherNode>>();
			itemCGroupAll.put(groupId, groupNode);
			HashMap<Integer, FeatherNode> featherNode = new HashMap<Integer, FeatherNode>();
			featherNode.put(Integer.parseInt(str[0]),
					getFeatherNode(str, featherLength));
			groupNode.put(category, featherNode);
			return;
		}
		HashMap<Integer, FeatherNode> featherNode = groupNode.get(category);
		if (featherNode == null) {
			// 如果不存在业态则新增
			featherNode = new HashMap<Integer, FeatherNode>();
			featherNode.put(Integer.parseInt(str[0]),
					getFeatherNode(str, featherLength));
			groupNode.put(category, featherNode);
		} else {
			featherNode.put(Integer.parseInt(str[0]),
					getFeatherNode(str, featherLength));
		}
//		if(str[0].equals("19408"))
//		{
//			FeatherNode fea=itemCGroupAll.get(groupId).get(category).get(19408);
//			System.out.println(fea);
//		}
		
	}

	/**
	 * 打印
	 */
	public void print() {

	}

	/**
	 * 打印所有的string的inter的映射方法
	 */
	public void printFeatherSet() {
		System.out.println("string特征");
		for (Entry<String, Integer> m : index.entrySet()) {
			System.out.println(m.getKey() + ":" + m.getValue());
		}
	}

}
