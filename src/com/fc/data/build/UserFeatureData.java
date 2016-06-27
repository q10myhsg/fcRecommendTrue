package com.fc.data.build;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.config.ConfigData;
import com.db.MongoDb;
import com.db.MysqlConnection;
import com.fc.recommend.main.RecommendMain;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.util.JsonUtil;

/**
 * 用户特征表
 * 该程序每天会执行一次
 * @author Administrator 使用fcRecommendTrue
 */
public class UserFeatureData {
	/**
	 * 每一列 feather 对应的 index 对应的value
	 */
	public ArrayList<HashMap<String, Integer>> map = new ArrayList<HashMap<String, Integer>>();

	// /**
	// * 转换的值
	// */
	// public ArrayList<ArrayList<String[]>> value=new
	// ArrayList<ArrayList<String[]>>();
	// /**
	// * 具体的user
	// */
	// public ArrayList<UserFeatureBean> user=new ArrayList<UserFeatureBean>();
	/**
	 * 读取 各个feather的具体 index标签
	 * 
	 * @param mysql
	 * @throws Exception
	 */
	public void readConfig(MysqlConnection mysql) throws Exception {
		ResultSet set =null;
			set=	mysql.sqlSelect(ConfigData.cateOneRank).resultSet;
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		map.add(map1);
		while (set.next()) {
			String name = set.getString(1);
			int val = set.getInt(2);
			map1.put(name, val);
		}

		set = mysql.sqlSelect(ConfigData.cateAllRank).resultSet;
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		map.add(map2);
		while (set.next()) {
			String name = set.getString(1);
			int val = set.getInt(2);
			//System.out.println(name+":"+val);
			map2.put(name, val);
		}

		set = mysql.sqlSelect(ConfigData.levelRank).resultSet;
		HashMap<String, Integer> map3 = new HashMap<String, Integer>();
		map.add(map3);
		while (set.next()) {
			String name = set.getString(1);
			int val = set.getInt(2);
			map3.put(name, val);
		}
	}

	/**
	 * 用户的基础属性
	 * 
	 * @param mongo
	 * @param database
	 * @param table
	 *            标准feather
	 * @param tableT
	 *            转化后的feather
	 */
	public void runMall(MysqlConnection mysql, MongoDb mongo, String database,
			String table, String tableT) throws Exception {
		int dbmsFeather = RecommendMain.dbmsFeather;
		ResultSet set = mysql.sqlSelect(ConfigData.mallFeatherSql).resultSet;
		int zn = set.getMetaData().getColumnCount();
		int count;
		while (set.next()) {
			UserFeatureBean bean = new UserFeatureBean();
			int[] dbms = new int[dbmsFeather];
			count = -1;
			long id = 0;
			int city = 0;
			// int cate=0;
			for (int i = 1; i <= zn; i++) {
				if (i == 1) {
					id = ((Integer) set.getInt(i)).longValue();
				} else if (i == 2) {
					city = (set.getInt(i));
				} else if (i == 3) {

				} else {
					if (i <= dbmsFeather + 3) {
						dbms[i - 4] = set.getInt(i);
					} else {
						String temp = set.getString(i);
						if (temp == null || temp.equals("")) {
							continue;
						}
						StringTokenizer token = new StringTokenizer(temp, ",");
						// System.out.println(Arrays.toString(dbms));
						// System.out.println(temp+":"+i);
						// System.out.println(token.countTokens());
						String[] list = new String[token.countTokens()];
						int znp = -1;
						while (token.hasMoreTokens()) {
							znp++;
							count++;
//							if (i - 4 == 3) {
//
//							} else if (i - 4 == 4) {
//
//							} else if (i - 4 == 5) {
//
//							} else if (i - 4 == 6) {
//
//							}
							list[znp] = token.nextToken();
						}
						//System.out.println("新增:"+(i-4)+Arrays.toString(list));
						bean.f.put(i - 4, list);
					}
				}
			}
			bean.dbms = dbms;
			// 入库
			DBObject obj = new BasicDBObject();
			// String _id="re_"+id
			obj.put("i", id);
			obj.put("c", city);
			bean.i = id;
			bean.c = city;
			// System.out.println("str:"+JsonUtil.getJsonStr(bean));
			mongo.update(table, obj, JsonUtil.getJsonStr(bean));
			System.out.println(JsonUtil.getJsonStr(bean));
			// 重新编译
			HashMap<Integer, Integer[]> list = new HashMap<Integer, Integer[]>();
			for (int i = 3; i < 6; i++) {
				String[] list4 = (String[]) bean.f.get(i);
				//System.out.println(Arrays.toString(list4));
				Integer[] temp = new Integer[map.get(i - 3).size()];
				list.put(i, temp);
				for(int j=0;j<temp.length;j++){
					temp[j]=0;
				}
				if (list4 == null) {
					continue;
				}
				for (String str : list4) {
					//System.out.println(str+"\t:"+(i-3));
					Integer index2=map.get(i - 3).get(str);
					if(index2==null)
					{
						continue;
					}
					temp[index2] = 1;
				}
			}
			// 替换
			bean.f = new HashMap<Integer, Object[]>();
			for (Entry<Integer, Integer[]> m : list.entrySet()) {
				bean.f.put(m.getKey(), m.getValue());
			}
			mongo.update(tableT, obj, JsonUtil.getJsonStr(bean));
		}
	}

	public void runBrand(MysqlConnection mysql, MongoDb mongo, String database,
			String table, String tableT) throws Exception {
		int dbmsFeather = RecommendMain.dbmsFeather;
		ResultSet set = mysql.sqlSelect(ConfigData.brandBrandFeatherSql
				.replaceAll("c.category_name",
						"b.category_name")).resultSet;
		int zn = set.getMetaData().getColumnCount();
		int count;
		while (set.next()) {
			UserFeatureBean bean = new UserFeatureBean();
			int[] dbms = new int[dbmsFeather];
			count = -1;
			long id = 0;
			int city = 0;
			int cate = 0;
			for (int i = 1; i <= zn; i++) {
				if (i == 1) {
					id = ((Integer) set.getInt(i)).longValue();
				} else if (i == 2) {
					city = (set.getInt(i));
				} else if (i == 3) {
					cate = (set.getInt(i));
				} else {
					if (i <= dbmsFeather + 3) {
						dbms[i - 4] = set.getInt(i);
					} else {
						String temp = set.getString(i);
						if (temp == null || temp.equals("")) {
							continue;
						}
						StringTokenizer token = new StringTokenizer(temp, ",");
						// System.out.println(Arrays.toString(dbms));
						// System.out.println(temp+":"+i);
						// System.out.println(token.countTokens());
						String[] list = new String[token.countTokens()];
						int znp = -1;
						while (token.hasMoreTokens()) {
							znp++;
							count++;
							list[znp] = token.nextToken();
						}
						System.out.println(Arrays.toString(list));
						bean.f.put(i - 4, list);
					}
				}
			}
			bean.dbms = dbms;
			// 入库
			DBObject obj = new BasicDBObject();
			// String _id="re_"+id
			obj.put("i", id);
			obj.put("c", city);
			obj.put("t", cate);
			bean.i = id;
			bean.c = city;
			bean.t = cate;
			mongo.update(table, obj, JsonUtil.getJsonStr(bean));

			// 重新编译
			HashMap<Integer, Integer[]> list = new HashMap<Integer, Integer[]>();
			for (int i = 3; i < 6; i++) {
				String[] list4 = (String[]) bean.f.get(i);
				Integer[] temp=null;
				if(i==4)
				{
					temp= new Integer[map.get(i - 3).size()];
				}else{
					temp= new Integer[map.get(i - 3).size()];					
				}
				for(int j=0;j<temp.length;j++){
					temp[j]=0;
				}
				list.put(i, temp);
				if (list4 == null) {
					continue;
				}
				if(i==4)
				{
					for (String str : list4) {
						Integer index2=map.get(i - 3).get(str);
						if(index2==null){
							continue;
						}else{
							temp[index2] = 1;
						}
					}
				}else{
				for (String str : list4) {
					Integer index2=map.get(i - 3).get(str);
					if(index2==null)
					{
						continue;
					}else{
						temp[index2] = 1;
					}
				}
				}
			}
			// 替换
			bean.f = new HashMap<Integer, Object[]>();
			for (Entry<Integer, Integer[]> m : list.entrySet()) {
				bean.f.put(m.getKey(), m.getValue());
			}
			mongo.update(tableT, obj, JsonUtil.getJsonStr(bean));
		}
	}

	public static void main(String[] args) {
		String mongoIp = "";
		mongoIp="192.168.1.11";
		String db = "recommend";
		//boolean isMall = true;
		String table = "userFeature";
		String tableT = "userFeatureT";
		String mysqlIp="192.168.1.134";
		String mysqlDb="fangcheng_global";
		String user="fangcheng_admin";
		String pwd="fc1234";
		if(args.length==6){
			mongoIp=args[0];
			db=args[1];
			table=args[2];
			tableT=args[3];
			mysqlIp=args[4];
			mysqlDb=args[5];
			user=args[6];
			pwd=args[7];
		}else if(args.length!=0){
			System.out.println("参数错误");
			System.out.println("mongoIp mongoDb userFeature userFeatureT mysqlIp mysqlDb");
			System.exit(1);
		}
		UserFeatureData data = new UserFeatureData();
		MysqlConnection mysql = new MysqlConnection(mysqlIp, 3306,
				mysqlDb,user,pwd);
		try {
			data.readConfig(mysql);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MongoDb mongo = new MongoDb(mongoIp, 27017, "recommend");
		try {
			//data.runMall(mysql, mongo, db, table,tableT);
			data.runBrand(mysql, mongo, db, table,tableT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
