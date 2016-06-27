package com.fc.recommend.dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserNode;

/**
 * data loader gernalral method
 * 
 * @author Administrator
 *
 */
public class DataGeneralLoader extends DataLoader {

	/**
	 * 读取文件 输入数据格式为 userId groupId categoryId itemId itemPre itemCount
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public DataLoader readFile(String filePath, String code, String regex) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在:" + filePath);
			System.exit(1);
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		// 只使用1个group
		try {
			while ((tempString = reader.readLine()) != null) {
				// 用户，物品，喜好
				String[] split = tempString.split(regex);
				if (split.length >= 5) {
					UserNode user = new UserNode();
					user.setUser(Integer.parseInt(split[0]));
					user.categoryId = Integer.parseInt(split[2]);
					ItemNode item = new ItemNode(Integer.parseInt(split[3]),
							Integer.parseInt(split[4]),
							Float.parseFloat(split[5]), 1);
					user.addItem(item);
					// 应该 做成分层抽样算法
					if (Math.random() <= trainRate) {
						addUser(Integer.parseInt(split[1]),
								Integer.parseInt(split[3]), user);
					} else {
						addUserTest(Integer.parseInt(split[1]),
								Integer.parseInt(split[3]), user);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * 
	 * @param mysql
	 * @param sql
	 * @param isConMatrix
	 *            是否使用共现矩阵
	 * @return
	 */
	public DataLoader readMysql(MysqlConnection mysql, String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		if (select == null) {
			System.out.println("返回");
			return this;
		}
		ResultSet result = select.resultSet;
		// System.out.println("进入");
		try {
			while (result.next()) {
				int user = result.getInt(1);
				int groupId = result.getInt(2);
				int categoryUser = result.getInt(3);
				int category = result.getInt(4);
				int brand = -1;
				int score = -1;
				try {
//					category = result.getInt(4);
					brand = result.getInt(5);
					score = result.getInt(6);
				} catch (Exception e) {

				}
				// System.out.println(user+"\t"+groupId);
				UserNode userNode = new UserNode();
				userNode.categoryId = categoryUser;
				userNode.setUser(user);
				if (brand != -1) {
					ItemNode item = new ItemNode(brand, category, score, 1);
					userNode.addItem(item);
				}
				// 应该 做成分层抽样算法
				// userNode.print();
				if (brand == -1) {
					addUser(groupId, category, userNode);
				} else {
					if (Math.random() <= trainRate) {
						addUser(groupId, category, userNode);
					} else {
						addUserTest(groupId, category, userNode);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 读取文件 输入数据格式为 id isUser featherId:value
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public DataLoader readContentFeatherFile(String filePath, String code,
			String regex, int featherLength) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在:" + filePath);
			System.exit(1);
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		// 只使用1个group
		try {
			while ((tempString = reader.readLine()) != null) {
				// 用户，物品，喜好
				String[] split = tempString.split(regex);
				this.contentModel.feather.addContent(split, featherLength);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * 读取文件 输入数据格式为id isUser featherId:value
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public DataLoader readContentFeatherMysql(MysqlConnection mysql,
			String sql, int featherLength) {
		MysqlSelect select = mysql.sqlSelect(sql);
		// 判断有几列
		if (select == null) {
			return this;
		}
		ResultSet result = select.resultSet;
		try {
			int indexAll = 0;
			while (result.next()) {
				int index = 0;
				StringBuffer sb = new StringBuffer();
				while (true) {
					index++;
					if (indexAll != 0 && indexAll == index) {
						index = 0;
						this.contentModel.feather.addContent(sb.toString()
								.split("\t"), featherLength);
						break;
					}
					String temp = null;
					try {
						temp = result.getString(index);
					} catch (Exception e) {
						indexAll = index;
						index = 0;
						this.contentModel.feather.addContent(sb.toString()
								.split("\t"), featherLength);
						break;
					}
					if (index == 1) {
						sb.append(temp);
					} else {
						sb.append("\t").append(temp);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 获取用户的业态属性 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashMap<Integer, String[]> readStringFeatherMysql(
			MysqlConnection mysql, String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashMap<Integer, String[]> map = new HashMap<Integer, String[]>();
		// 判断有几列
		if (select == null) {
			return map;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				String str = result.getString(2);
				if (str == null) {
					map.put(result.getInt(1), new String[0]);
				} else {
					map.put(result.getInt(1), result.getString(2).split(","));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取用户的业态属性 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashMap<String, String[]> readMallCat2StringFeatherMysql(
			MysqlConnection mysql, String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		// 判断有几列
		if (select == null) {
			return map;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				String str = result.getString(3);
				if (str == null) {
					map.put(result.getInt(1) + "_" + result.getInt(2),
							new String[0]);
				} else {
					map.put(result.getInt(1) + "_" + result.getInt(2), result
							.getString(3).split(","));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取用户的业态属性 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashSet<String> readStringMysql(MysqlConnection mysql,
			String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashSet<String> set = new HashSet<String>();
		// 判断有几列
		if (select == null) {
			return set;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				set.add(result.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * 获取用户的业态属性 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashSet<Integer> readIntMysql(MysqlConnection mysql,
			String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashSet<Integer> set = new HashSet<Integer>();
		// 判断有几列
		if (select == null) {
			return set;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				set.add(Integer.parseInt(result.getString(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * 获取用户的业态属性 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashMap<Integer, Integer> readIntMapMysql(
			MysqlConnection mysql, String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashMap<Integer, Integer> set = new HashMap<Integer, Integer>();
		// 判断有几列
		if (select == null) {
			return set;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				set.put(result.getInt(1), result.getInt(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * 获取用户的业态属性 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashMap<Integer, int[]> readIntFeatherMysql(
			MysqlConnection mysql, String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashMap<Integer, int[]> map = new HashMap<Integer, int[]>();
		// 判断有几列
		if (select == null) {
			return map;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				String[] strList = result.getString(2).split(",");
				int[] intList = new int[strList.length];
				for (int i = 0; i < strList.length; i++) {
					intList[i] = Integer.parseInt(strList[i]);
				}
				map.put(result.getInt(1), intList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取品牌所属商圈 输入区格式为id对应 业态,
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public static HashMap<String, HashSet<Integer>> readCityAndIdAreaIdFeatherMysql(
			MysqlConnection mysql, String sql) {
		MysqlSelect select = mysql.sqlSelect(sql);
		HashMap<String, HashSet<Integer>> map = new HashMap<String, HashSet<Integer>>();
		// 判断有几列
		if (select == null) {
			return map;
		}
		ResultSet result = select.resultSet;
		try {
			while (result.next()) {
				HashSet<Integer> set = new HashSet<Integer>();
				String temp = result.getString(3);
				String[] strList = null;
				if (temp == null) {
					strList = new String[0];
				} else {
					strList = temp.split(",");
				}
				for (String str : strList) {
					set.add(Integer.parseInt(str));
				}
				map.put(result.getInt(1) + "_" + result.getInt(2), set);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 读取文件 输入数据格式为 id isUser featherId:value
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public DataLoader readItemContentFeatherFile(String filePath, String code,
			String regex, int featherLength) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在:" + filePath);
			System.exit(1);
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		// 只使用1个group
		try {
			while ((tempString = reader.readLine()) != null) {
				// 用户，物品，喜好
				String[] split = tempString.split(regex);
				this.contentModel.feather.addContentItem(split, featherLength);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * 读取文件 输入数据格式为id isUser featherId:value
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public DataLoader readItemContentFeatherMysql(MysqlConnection mysql,
			String sql, int featherLength) {
		MysqlSelect select = mysql.sqlSelect(sql);
		// 判断有几列
		if (select == null) {
			return this;
		}
		ResultSet result = select.resultSet;
		try {
			int indexAll = 0;
			while (result.next()) {
				int index = 0;
				StringBuffer sb = new StringBuffer();
				while (true) {
					index++;
					if (indexAll != 0 && indexAll == index) {
						index = 0;
						this.contentModel.feather.addContentItem(sb.toString()
								.split("\t"), featherLength);
						break;
					}
					String temp = null;
					try {
						temp = result.getString(index);
					} catch (Exception e) {
						indexAll = index;
						index = 0;
						this.contentModel.feather.addContentItem(sb.toString()
								.split("\t"), featherLength);
						break;
					}
					if (index == 1) {
						sb.append(temp);
					} else {
						sb.append("\t").append(temp);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 读取文件中 map的 业态数量数据
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 * @param result
	 *            返回 mall 对应的 categoryId 对应的数量
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> readMallCategoryFile(
			String filePath, String code, String regex) {
		File file = new File(filePath);

		HashMap<Integer, HashMap<Integer, Integer>> map = new HashMap<Integer, HashMap<Integer, Integer>>();
		if (!file.exists()) {
			System.out.println("文件不存在:" + filePath);
			System.exit(1);
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		// 只使用1个group
		HashMap<Integer, Integer> m = null;
		int mallIdOld = -1;
		try {
			while ((tempString = reader.readLine()) != null) {
				// 用户，物品，喜好
				String[] split = tempString.split(regex);
				int mallId = Integer.parseInt(split[0]);
				int categoryId = Integer.parseInt(split[1]);
				int count = Integer.parseInt(split[2]);
				if (mallIdOld == -1) {
					m = new HashMap<Integer, Integer>();
					mallIdOld = mallId;
				} else if (mallIdOld == mallId) {
				} else {
					map.put(mallIdOld, m);
					m = new HashMap<Integer, Integer>();
					mallIdOld = mallId;
				}
				m.put(categoryId, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (m != null) {
			map.put(mallIdOld, m);
		}
		return map;
	}

	/**
	 * 读取文件 输入数据格式为id isUser featherId:value
	 * 
	 * @param mysql
	 * @param sql
	 * @return
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> readMallCategoryMysql(
			MysqlConnection mysql, String sql) {

		MysqlSelect select = mysql.sqlSelect(sql);
		HashMap<Integer, HashMap<Integer, Integer>> map = new HashMap<Integer, HashMap<Integer, Integer>>();
		// 判断有几列
		if (select == null) {
			return map;
		}
		ResultSet result = select.resultSet;
		HashMap<Integer, Integer> m = null;
		int mallIdOld = -1;
		try {
			while (result.next()) {
				int mallId = result.getInt(1);
				int categoryId = result.getInt(2);
				int count = result.getInt(3);
				if (mallIdOld == -1) {
					m = new HashMap<Integer, Integer>();
					mallIdOld = mallId;
				} else if (mallIdOld == mallId) {
				} else {
					map.put(mallIdOld, m);
					m = new HashMap<Integer, Integer>();
					mallIdOld = mallId;
				}
				m.put(categoryId, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (m != null) {
			map.put(mallIdOld, m);
		}
		return map;
	}
}
