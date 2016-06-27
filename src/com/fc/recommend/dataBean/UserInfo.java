package com.fc.recommend.dataBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import com.fc.recommend.dataModel.DataModel;
import com.fc.recommend.filter.RecommenderFilterUtil;

/**
 * 数据主程序
 * @author Administrator
 *
 */
public class UserInfo {
	
	/**
	 * u1 和u2分割符
	 */
	public static final int U_U_LENGTH=100000000;
	
	/**
	 * 基础数据源
	 * categoryCode 具体值
	 */
	public HashMap<Integer,UserNode> userGroup=new HashMap<Integer,UserNode>();
	
	/**
	 * 用户相似度
	 */
	private HashMap<Long,Float> userSim=new HashMap<Long,Float>();
	public UserInfo()
	{
		
	}
	/**
	 * 两个用户之间的相似度 存储
	 * @param user1
	 * @param user2
	 * @param sim
	 */
	public void addUserSim(long user1,long user2,Float sim)
	{
		userSim.put(getU_U(user1,user2),sim);
	}
	
	/**
	 * 获取u1 u2 值
	 * @param user1
	 * @param user2
	 * @return
	 */
	public static long getU_U(long user1,long user2)
	{
		if(user1<user2)
		{
			return user1*U_U_LENGTH+user2;
		}else{
			return user2*U_U_LENGTH+user1;
		}
	}
	/**
	 * 获取小数
	 * @param value
	 * @return
	 */
	public static int getU_U1(long value)
	{
		return (int)(value/U_U_LENGTH);
	}
	/**
	 * 获取大数
	 * @param value
	 * @return
	 */
	public static int getU_U2(long value)
	{
		return (int)value%U_U_LENGTH;
	}
	
	/**
	 * 获取两个用户的相似度
	 * @param user1
	 * @param user2
	 * @return
	 */
	public float getUserSim(long user1,long user2)
	{
		StringBuffer strBuffer=new StringBuffer();
		return userSim.get(getU_U(user1,user2));
	}
	
	/**
	 * 数据清洗
	 * @param recommanderFilterUtil
	 */
	public void dataClearnFilter(LinkedList<RecommenderFilterUtil> recommanderFilterUtil)
	{
		//添加过滤器方法 
		if(recommanderFilterUtil==null)
		{
			return;
		}
		for(Entry<Integer,UserNode> users:entrySet())
		{
			UserNode userNode=users.getValue();
			for(RecommenderFilterUtil recommanderFilter:recommanderFilterUtil)
			{
				recommanderFilter.filter(userNode);
			}
		}
	}
	
	
	public HashMap<Integer, UserNode> getUserInfo() {
		return userGroup;
	}



	public void setUserInfo(HashMap<Integer, UserNode> userGroup) {
		this.userGroup = userGroup;
	}
	public int size()
	{
		return userGroup.size();
	}
	
	/**
	 * 对应的分类
	 * 添加一条数据
	 * 是否添加成功
	 * @param user
	 */
	public boolean addUser(UserNode user)
	{	
		UserNode userInfo=userGroup.get(user.getUserId());
		if(userInfo==null)
		{
			userGroup.put(user.getUserId(), user);
		}else {
			userInfo.addItem(user.getItems().get(0));
		}
		return true;
	}
	/**
	 * 获取用户
	 * @param userId
	 * @return
	 */
	public UserNode getUser(int userId)
	{
		return userGroup.get(userId);
	}
	
	/**
	 * 获取全部用户
	 * @return
	 */
	public ArrayList<UserNode> getUsers()
	{
		ArrayList<UserNode> result=new ArrayList<UserNode>();
		for(Entry<Integer, UserNode> item:userGroup.entrySet())
		{
			result.add(item.getValue());
		}
		return result;
	}
	
	public Set<Entry<Integer,UserNode>> entrySet()
	{
		return userGroup.entrySet();
	}
	
	public Set<Entry<Long,Float>> entrySetSimilary()
	{
		return this.userSim.entrySet();
	}
	/**
	 * 打印方法
	 */
	public void print(int groupId,int category,FeatherInfo feather)
	{
		System.out.println("业态:"+category);
		for(Entry<Integer,UserNode> userInfo:this.userGroup.entrySet())
		{
			UserNode userNode=userInfo.getValue();
			userNode.print(groupId,feather);
		}
	}
	
	/**
	 * 获取用户信息
	 * @param userId
	 * @param feather
	 * @return
	 */
	public String getUserString(int userId,int groupId,FeatherInfo feather)
	{
		UserNode userNo=userGroup.get(userId);
		if(userNo==null)
		{
			return null;
		}else{
			return userNo.getUserString(groupId,feather);
		}
	}
	/**
	 * 获取某个用户的所有物品信息
	 * @param userId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemsList(int userId,int groupId,FeatherInfo feather)
	{
		UserNode userNo=userGroup.get(userId);
		if(userNo==null)
		{
			return null;
		}else{
			return userNo.getItemsList(groupId,feather);
		}
	}
	/**
	 * 获取某个用户的物品信息
	 * @param userId
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public String getItemString(int userId,int itemId,int groupId,FeatherInfo feather)
	{
		UserNode userNo=userGroup.get(userId);
		if(userNo==null)
		{
			return null;
		}else{
			return userNo.getItemString(itemId,groupId,feather);
		}
	}
	/**
	 * 获取物品对应的全部用户
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemToUserList(int itemId,int groupId,FeatherInfo feather)
	{
		LinkedList<String> list=new LinkedList<String>();
		for(Entry<Integer,UserNode> m:userGroup.entrySet())
		{
			String temp=m.getValue().getItemToUserString(itemId,groupId,feather);
			if(temp==null)
			{
				continue;
			}else{
				list.add(temp);
			}
		}
		return list;
	}
	
	/**
	 * 获取用户下所有物品的关联的用户
	 * @return
	 */
	public HashMap<Integer,HashSet<String>> getUserItemToUser(int userId,int groupId,DataModel dataModel,DataModel contentModel)
	{
		HashMap<Integer,HashSet<String>> map=new HashMap<Integer,HashSet<String>>();
		UserNode userNode=userGroup.get(userId);
		if(userNode==null)
		{
			return map;
		}
		map=userNode.getUserItemToUser(groupId, dataModel, contentModel);
		return map;
	}
}
