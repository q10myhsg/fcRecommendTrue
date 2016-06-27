package com.fc.recommend.dataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.fc.recommend.dataBean.FeatherInfo;
import com.fc.recommend.dataBean.UserInfo;
import com.fc.recommend.dataBean.UserNode;

public class DataModel {

	/**
	 * 存储组 对应的用户
	 */
	public HashMap<Integer,HashMap<Integer, UserInfo>> userGroupAll = new HashMap<Integer,HashMap<Integer, UserInfo>>();
	/**
	 * 测试集 对应的用户
	 */
	public HashMap<Integer,HashMap<Integer, UserInfo>> userGroupAllTest = new HashMap<Integer,HashMap<Integer, UserInfo>>();

	/**
	 * 属性
	 */
	public FeatherInfo feather = new FeatherInfo();
	/**
	 * mall 是否为user
	 */
	public boolean isMall = true;
	/**
	 * 是否按照业态划分
	 */
	public boolean isCategory = true;

	/**
	 * 添加用户到对应的组中
	 * 
	 * @param userNode
	 * @param groupId
	 *            决策分组后的数据
	 * @param 本组对应的数据
	 *            业态
	 * @param is
	 *            Category 是否 按照分类分隔
	 */
	public void addUser(int groupId, int categoryId, UserNode userNode) {

		HashMap<Integer, UserInfo> temp = userGroupAll.get(groupId);
		if(temp==null)
		{
			temp=new HashMap<Integer,UserInfo>();
			userGroupAll.put(groupId,temp);
		}
		if (isCategory) {
			UserInfo userInfo = temp.get(categoryId);
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.addUser(userNode);
				temp.put(categoryId, userInfo);
			} else {
				userInfo.addUser(userNode);
			}
		} else {
			UserInfo userInfo = temp.get(-1);
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.addUser(userNode);
				temp.put(categoryId, userInfo);
			} else {
				userInfo.addUser(userNode);
			}
		}
	}

	/**
	 * 添加用户到对应的组中 测试集
	 * 
	 * @param userNode
	 */
	public void addUserTest(int groupId, int categoryId, UserNode userNode) {
		HashMap<Integer, UserInfo> temp = userGroupAllTest.get(groupId);
		if(temp==null)
		{
			temp=new HashMap<Integer,UserInfo>();
			userGroupAllTest.put(groupId,temp);
		}
		if (isCategory) {
			UserInfo userInfo = temp.get(categoryId);
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.addUser(userNode);
				temp.put(categoryId, userInfo);
			} else {
				userInfo.addUser(userNode);
			}
		} else {
			UserInfo userInfo = temp.get(-1);
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.addUser(userNode);
				temp.put(categoryId, userInfo);
			} else {
				userInfo.addUser(userNode);
			}
		}
	}

	/**
	 * 打印训练集数据
	 */
	public void printTrainSet(DataModel contentModel) {
		int index = 0;
		for (Entry<Integer,HashMap<Integer, UserInfo>> map : userGroupAll.entrySet()) {
			System.out.println("第" + map.getKey() + "组:");
			for (Entry<Integer, UserInfo> m : map.getValue().entrySet()) {
				System.out.println("分类:" + m.getKey());
				m.getValue().print(map.getKey(),m.getKey(),contentModel.feather);
			}
		}
	}

	/**
	 * 打印测试集数据
	 */
	public void printTestSet(DataModel contentModel) {
		int index = 0;
		for (Entry<Integer,HashMap<Integer, UserInfo>> map : userGroupAllTest.entrySet()) {
			index++;
			System.out.println("第" + index + "组:");
			for (Entry<Integer, UserInfo> m : map.getValue().entrySet()) {
				System.out.println("分类:" + m.getKey());
				m.getValue().print(map.getKey(),m.getKey(),contentModel.feather);
			}
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param userId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getUserString(int userId, int groupId, DataModel contentModel) {
		LinkedList<String> flag = new LinkedList<String>();
		for (Entry<Integer,UserInfo> userInfoMap : userGroupAll.get(groupId).entrySet()) {
			String temp = userInfoMap.getValue()
					.getUserString(userId, groupId, contentModel.feather);
			if (flag == null) {
				continue;
			} else {
				flag.add(temp);
				break;
			}
		}
		return flag;
	}

	/**
	 * 获取某个用户的所有物品信息
	 * 
	 * @param userId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemsList(int userId, int groupId,
			DataModel contentModel) {
		LinkedList<String> result = new LinkedList<String>();
		if(userGroupAll.get(groupId)==null)
		{
			return result;
		}
		for (Entry<Integer,UserInfo> userInfoMap : userGroupAll.get(groupId).entrySet()) {
			if(userInfoMap.getKey()==105)
			{
				System.out.println();
			}
			LinkedList<String> flag = userInfoMap.getValue().getItemsList(userId, groupId, contentModel.feather);
			if (flag == null||flag.size()==0) {
				continue;
			} else {
				result.addAll(flag);
			}
		}
		return result;
	}

	/**
	 * 获取某个用户的物品信息
	 * 
	 * @param userId
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemString(int userId, int itemId, int groupId,
			DataModel contentModel) {
		LinkedList<String> flag =new LinkedList<String>();
		if(userGroupAll.get(groupId)==null)
		{
			return flag;
		}
		for (Entry<Integer,UserInfo> userInfo : userGroupAll.get(groupId).entrySet()) {
			String temp = userInfo.getValue().getItemString(userId, itemId, groupId,
					contentModel.feather);
			if (temp == null) {
				continue;
			} else {
				flag.add(temp);
			}
		}
		return flag;
	}

	/**
	 * 获取物品对应的全部用户 包含用户属性 
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemToUserList(int itemId, int groupId,
			DataModel contentModel) {
		LinkedList<String> flag = new LinkedList<String>();
		if(userGroupAll.get(groupId)==null)
		{
			return flag;
		}
		for (Entry<Integer,UserInfo> userInfo : userGroupAll.get(groupId).entrySet()) {
			LinkedList<String> temp = userInfo.getValue().getItemToUserList(itemId,
					groupId, contentModel.feather);
			if (temp == null) {
				continue;
			} else {
				flag.addAll(temp);
			}
		}
		return flag;
	}

	
	/**
	 * 获取物品对应的全部用户 包含用户属性 
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public HashSet<String> getItemToUserSet(int itemId, int groupId,
			DataModel contentModel) {
		LinkedList<String> flag =getItemToUserList(itemId,groupId,contentModel);
		HashSet<String> set=new HashSet<String>();
		for(String str:flag)
		{
			set.add(str);
		}
		return set;
	}
}
