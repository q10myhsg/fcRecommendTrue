package com.fc.recommend.dataLoader;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.dataModel.DataModel;

public abstract class DataLoader {
	Logger loger = LoggerFactory.getLogger(DataLoader.class);
	/**
	 * train set rate
	 */
	public float trainRate = 0.7f;

	/**
	 * data set
	 */
	public DataModel dataModel = new DataModel();
	/**
	 * content data set
	 */
	public DataModel contentModel = new DataModel();

	/**
	 * get data set
	 * 
	 * @return
	 */
	public DataModel getDate() {
		return dataModel;
	}

	/**
	 * get content data set
	 * 
	 * @return
	 */
	public DataModel getContentData() {
		return contentModel;
	}

	/**
	 * set category
	 * 
	 * @param isCategory
	 */
	public void setCategory(boolean isCategory) {
		dataModel.isCategory = isCategory;
	}

	/**
	 * set is mall info
	 * 
	 * @param isMall
	 */
	public void setMall(boolean isMall) {
		dataModel.isMall = isMall;
	}

	/**
	 * add user info to train set
	 * 
	 * @param groupId
	 * @param categoryId
	 * @param user
	 */
	public void addUser(int groupId, int categoryId, UserNode userNode) {
		dataModel.addUser(groupId, categoryId, userNode);
	}

	/**
	 * add user info to test set
	 * 
	 * @param groupId
	 * @param categoryId
	 * @param user
	 */
	public void addUserTest(int groupId, int categoryId, UserNode userNode) {
		dataModel.addUserTest(groupId, categoryId, userNode);
	}

	/**
	 * 读取文件 输入数据格式为 id isUser featherId:value
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public abstract DataLoader readContentFeatherFile(String filePath,
			String code, String regex, int featherLength);

	/**
	 * 读取文件 输入数据格式为id isUser featherId:value
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public abstract DataLoader readContentFeatherMysql(MysqlConnection mysql,
			String sql, int featherLength);

	/**
	 * 读取文件 输入数据格式为 id isUser featherId:value
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public abstract DataLoader readItemContentFeatherFile(String filePath,
			String code, String regex, int featherLength);

	/**
	 * 读取文件 输入数据格式为id isUser featherId:value
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public abstract DataLoader readItemContentFeatherMysql(
			MysqlConnection mysql, String sql, int featherLength);

	/**
	 * 读取文件中 map的 业态数量数据
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 * @param result
	 *            返回 mall 对应的 categoryId 对应的数量
	 */
	public abstract HashMap<Integer, HashMap<Integer, Integer>> readMallCategoryFile(
			String filePath, String code, String regex);

	/**
	 * 读取文件 输入数据格式为id isUser featherId:value
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public abstract HashMap<Integer, HashMap<Integer, Integer>> readMallCategoryMysql(
			MysqlConnection mysql, String sql);

	/**
	 * 打印训练集
	 */
	public void printTrainSet() {
		this.dataModel.printTrainSet(contentModel);
	}

	/**
	 * 打印测试集
	 */
	public void printTestSet() {
		this.dataModel.printTestSet(contentModel);
	}

	/**
	 * 打印特征映射关系
	 */
	public void printFeatherSet() {
		this.contentModel.feather.printFeatherSet();
	}

	/**
	 * 获取用户信息
	 * 
	 * @param userId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getUserString(int userId,int groupId) {
		return this.dataModel.getUserString(userId,groupId, contentModel);
	}

	/**
	 * 获取某个用户的所有物品信息
	 * 
	 * @param userId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemsList(int userId,int groupId) {
		return dataModel.getItemsList(userId,groupId, contentModel);
	}

	/**
	 * 获取某个用户的物品信息
	 * 
	 * @param userId
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemString(int userId, int itemId,int groupId) {
		return dataModel.getItemString(userId, itemId,groupId, contentModel);
	}

	/**
	 * 获取物品对应的全部用户
	 * 
	 * @param itemId
	 * @param feather
	 * @return
	 */
	public LinkedList<String> getItemToUserList(int itemId,int groupId) {
		return dataModel.getItemToUserList(itemId,groupId, contentModel);
	}
	
	/**
	 * 读取映射
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashMap<Integer,String> getHashMap(MysqlConnection mysql, String sql)
	{
		HashMap<Integer,String> map=new HashMap<Integer,String>();
		MysqlSelect select = mysql.sqlSelect(sql);
		if (select == null) {
			return map;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				int id = result.getInt(1);
				String name = result.getString(2);
				map.put(id, name);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
