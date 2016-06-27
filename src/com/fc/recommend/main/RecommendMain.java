package com.fc.recommend.main;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.log4j.PropertyConfigurator;

import com.config.ConfigData;
import com.config.ConfigStatic;
import com.db.CursorInter;
import com.db.MongoDb;
import com.db.MysqlConnection;
import com.fc.commend.graphLinked.GeneralSortBean;
import com.fc.commend.graphLinked.GraphLinkedBean;
import com.fc.commend.graphLinked.GraphLinkedMethod;
import com.fc.recommend.cf.Recommend;
import com.fc.recommend.dataBean.FeatherNode;
import com.fc.recommend.dataLoader.DataGeneralLoader;
import com.fc.recommend.dataLoader.DataLoader;
import com.fc.recommend.filter.CFParamStatic;
import com.fc.recommend.filter.RecommenderHostItemCountFilter;
import com.fc.recommend.reDataProcession.FillMethodDataProcession;
import com.fc.recommend.reDataProcession.RecommendDataProcession;
import com.fc.recommend.similary2.CosinSimilary2;

/**
 * 推荐主程序
 * 
 * @author Administrator
 *
 */
public class RecommendMain {

	public static String mallCollection="mall";
	
	public static String brandCollection="brand";
	
	public static String outPutMongoIp="192.168.1.11";
	public static String outPutMongoDb="recommend";
	public static int dbmsFeather=3;
	
	public static String mysqlIp="192.168.1.134";
	public static String mysqlDb="fangcheng_global";
	public static String mysqlUser="fangcheng_admin";
	public static String mysqlPwd="fc1234";
	
	public static SimpleDateFormat df =new SimpleDateFormat("YYYYmmdd");
	static{
		mallCollection+=df.format(new Date());
		brandCollection+=df.format(new Date());
	}
	
	public static void runMallRecommand(float trainRate) {
		boolean isMall = true;
		MysqlConnection mysql = new MysqlConnection(mysqlIp, 3306,
				mysqlDb,mysqlUser,mysqlPwd);
		DataGeneralLoader dataLoader = new DataGeneralLoader();
		// 使用分类
		dataLoader.dataModel.isCategory = true;
		dataLoader.trainRate =trainRate;
		// groupId对应城市
		DataLoader data = dataLoader.readMysql(mysql, ConfigData.mallSql);
		// new
		// DataGeneralLoader().readFile("e:\\work\\recommand\\data.dat","utf-8","\t");
		// 如果dataLoader.dataModel.isCategory=true; 为不适用分类请 设置category为-1
		data = data.readContentFeatherMysql(mysql, ConfigData.mallFeatherSql,
				dbmsFeather);
		// 如果dataLoader.dataModel.isCategory=true; 为不适用分类请 设置category为-1
		// groupId 从0开始
		data = data.readItemContentFeatherMysql(mysql,
				ConfigData.brandFeatherSql, dbmsFeather);
		// System.out.println(data.getItemToUserList( 19408,86999031));
		// FeatherNode
		// feat=data.contentModel.feather.itemCGroupAll.get(86999030).get(105).get(19408);
		// System.out.println(data.getItemsList(166,86999031));
		// System.out.println(data.getUserString(4,86999030));
		// data.printTrainSet();
		// if (true) {
		// System.exit(1);
		// }
		// data.printFeatherSet();
		// System.out.println(data.getItemsList(1940548));
		// System.out.println(data.getUserString(1,86999030));
		// System.out.println(data.getItemToUserList(24787));
		System.out.println("基础数据加载完成");
		HashMap<Integer, HashMap<Integer, String>> map = new HashMap<Integer, HashMap<Integer, String>>();
		map.put(ConfigStatic.MALL,
				DataLoader.getHashMap(mysql, ConfigData.mallIdToNameSql));
		map.put(ConfigStatic.BRAND,
				DataLoader.getHashMap(mysql, ConfigData.brandIdToNameSql));
		map.put(ConfigStatic.CATE,
				DataLoader.getHashMap(mysql, ConfigData.categoryIdToNameSql));
		map.put(ConfigStatic.CITY,
				DataLoader.getHashMap(mysql, ConfigData.cityIdToNameSql));
		System.out.println("读取配置完成");
		// 获取业态下热门品牌
		MongoDb mongoHotBrand = new MongoDb("123.57.4.152", 27017, "dealdata");
		// 启动入库程序
		MongoDb mongo = new MongoDb(outPutMongoIp, 27017, outPutMongoDb);
		String collection = null;
		if (isMall) {
			collection = RecommendMain.mallCollection;
		} else {
			collection = RecommendMain.brandCollection;
		}
		boolean exist = mongo.existCollection(collection);
		if (exist) {
			mongo.removeCollection(collection);
		}
		CursorInter cursor = mongo.findCursor(collection, "", "");
		System.out.println("启动comment解析程序");
		HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> comment = runMallComment();
		System.out.println("启动填充程序");
		/**
		 * 推荐填充
		 */
		FillMethodDataProcession fileMethodDataProcession = new FillMethodDataProcession(
				isMall, data, map, mysql, mongoHotBrand, comment);
		System.out.println("输出cursor建立完成");
		RecommendDataProcession recommandOutPutProcession = new RecommendDataProcession(
				cursor, map, isMall, fileMethodDataProcession);
		Thread recommandOutPutProcessionThread = new Thread(
				recommandOutPutProcession);
		System.out.println("启动输出流程序");
		recommandOutPutProcessionThread.start();

		System.out.println("启动过滤程序");
		// 启动过滤程序
		RecommendDataProcession recommandDataProcession = new RecommendDataProcession(
				recommandOutPutProcession);
		recommandDataProcession.filter.add(new RecommenderHostItemCountFilter(
				CFParamStatic.recommanderHotGteFilter, 20));
		recommandDataProcession.filter.add(new RecommenderHostItemCountFilter(
				CFParamStatic.recommanderHotlteFilter, 1));
		Thread recommandFilterThread = new Thread(recommandDataProcession);
		System.out.println("启动处理流程序");
		recommandFilterThread.start();
		System.out.println("启动推荐主程序");
		long start = System.currentTimeMillis();
		// 初始化recommand
		Recommend re = new Recommend(data, recommandDataProcession);
		// 设置mall对应的业态数量
		re.mallCategory = data.readMallCategoryMysql(mysql,
				ConfigData.mallCategorySql);
		// re.isPrintRecommand=true;
		re.isEvalue = true;// 是否执行估计值
		re.isPrintEvalue = false;// 是否打印估计值
		re.neighborhoodFunc.similaryRate = 0.012f;// cf的过滤比率
		re.neighborhoodFunc.similaryCount = 20;// 相似用户数
		re.neighborhoodFunc.comSimilaryRate = 0.01f;// 混合content的过滤比率
		re.neighborhoodFunc.userItemRate = 0.1f;// 计算邻域时的两个用户相似度 的的全部物品的结构性比率
		re.userItemRate = 0.1f;// 用户 和物品做相似度计算的结构性比率
		re.recommandItemCount = 20;// 推荐的物品数
		re.isConMatrix = false;// 使用同现填补
		re.isOutput = true;
		//使用购物
		re.run(20000);
		// re.run(-1);
		System.out.print((System.currentTimeMillis() - start) + "ms");
		// System.out.println(data.getUserString(2782357,86999030));
		// System.out.println(data.getItemsList(2782357,86999030));
		//
		// System.out.println(data.getItemToUserList(24787,86999030));
		// System.out.println("打印用户下物品对应的其他用户");
		// re.printUserItemToUser(4534661, 86999030, 102);
		// re.printUserToUserSim(4534661,86999030, 102, 0.05f,0.1f);
	}

	public static void runBrandRecommand(float trainRate) {
		boolean isMall = false;
		MysqlConnection mysql = new MysqlConnection(mysqlIp, 3306,
				mysqlDb,mysqlUser,mysqlPwd);

		DataGeneralLoader dataLoader = new DataGeneralLoader();
		// 使用分类
		dataLoader.dataModel.isCategory = true;
		dataLoader.trainRate = trainRate;
		// groupId对应城市
		DataLoader data = dataLoader.readMysql(mysql, ConfigData.brandSql);
		// new
		// DataGeneralLoader().readFile("e:\\work\\recommand\\data.dat","utf-8","\t");
		// 如果dataLoader.dataModel.isCategory=true; 为不适用分类请 设置category为-1
		data = data.readContentFeatherMysql(mysql,
				ConfigData.brandBrandFeatherSql, dbmsFeather);
		// 如果dataLoader.dataModel.isCategory=true; 为不适用分类请 设置category为-1
		System.out.println(Arrays.toString(data.contentModel.feather.userCGroupAll.get(86999030).get(90000).get(25333).featherValue));
		System.out.println(data.contentModel.feather.userCGroupAll.get(86999030).get(90000).get(25333).featherStrValue);
		//System.exit(1);
		// groupId 从0开始
		data = data.readItemContentFeatherMysql(mysql,
				ConfigData.brandMallFeatherSql, dbmsFeather);
		//data.printTrainSet();
		
	//	 data.printFeatherSet();
	//	 System.out.println(data.getItemsList(10050,86999030));
	//	 System.exit(1);
//		 System.out.println(data.getUserString(1940548));
//		 System.out.println(data.getItemToUserList(24787));
		System.out.println("基础数据加载完成");
		HashMap<Integer, HashMap<Integer, String>> map = new HashMap<Integer, HashMap<Integer, String>>();
		map.put(ConfigStatic.MALL,
				DataLoader.getHashMap(mysql, ConfigData.mallIdToNameSql));
		map.put(ConfigStatic.BRAND,
				DataLoader.getHashMap(mysql, ConfigData.brandIdToNameSql));
		map.put(ConfigStatic.CATE,
				DataLoader.getHashMap(mysql, ConfigData.categoryIdToNameSql));
		map.put(ConfigStatic.CITY,
				DataLoader.getHashMap(mysql, ConfigData.cityIdToNameSql));
		System.out.println("读取配置完成");
		// 启动入库程序
		MongoDb mongo = new MongoDb(outPutMongoIp, 27017, outPutMongoDb);
		String collection = null;
		if (isMall) {
			collection = RecommendMain.mallCollection;
		} else {
			collection = RecommendMain.brandCollection;
		}
		boolean exist = mongo.existCollection(collection);
		if (exist) {
			mongo.removeCollection(collection);
		}
		CursorInter cursor = mongo.findCursor(collection, "", "");
		System.out.println("启动comment解析程序");
		HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> comment = runBrandComment();
		System.out.println("启动填充程序");
		// 推荐填充
		FillMethodDataProcession fileMethodDataProcession = new FillMethodDataProcession(
				isMall, data, map, mysql, null, comment);
		System.out.println("输出cursor建立完成");
		RecommendDataProcession recommandOutPutProcession = new RecommendDataProcession(
				cursor, map, isMall, fileMethodDataProcession);
		//关闭填补程序
		recommandOutPutProcession.isFill=false;
		Thread recommandOutPutProcessionThread = new Thread(
				recommandOutPutProcession);
		System.out.println("启动输出流程序");
		recommandOutPutProcessionThread.start();

		// 启动过滤程序
		System.out.println("启动过滤程序");
		RecommendDataProcession recommandDataProcession = new RecommendDataProcession(
				recommandOutPutProcession);
		recommandDataProcession.filter.add(new RecommenderHostItemCountFilter(
				CFParamStatic.recommanderHotGteFilter, 30));
		recommandDataProcession.filter.add(new RecommenderHostItemCountFilter(
				CFParamStatic.recommanderHotlteFilter, 1));
		Thread recommandFilterThread = new Thread(recommandDataProcession);
		System.out.println("启动处理流程序");
		recommandFilterThread.start();
		System.out.println("启动推荐主程序");
		long start = System.currentTimeMillis();
		// 初始化recommand
		Recommend re = new Recommend(data, recommandDataProcession);
		// 设置mall对应的业态数量
		// re.isPrintRecommand=true;
		re.isEvalue = true;// 是否执行估计值
		re.isPrintEvalue = true;// 是否打印估计值
		re.neighborhoodFunc.similaryRate = 0.2f;// cf的过滤比率
		re.neighborhoodFunc.similaryCount = 20;// 相似用户数
		re.neighborhoodFunc.comSimilaryRate = 0.01f;// 混合content的过滤比率
		re.neighborhoodFunc.userItemRate = 0.2f;// 计算邻域时的两个用户相似度 的的全部物品的结构性比率
		re.userItemRate = 0.1f;// 用户 和物品做相似度计算的结构性比率
		re.recommandItemCount = 20;// 推荐的物品数
		
		//re.isConMatrix = true;// 使用同现填补 有问题
		re.isOutput = true;
		re.run(-1);
		// re.run(-1);
		System.out.print((System.currentTimeMillis() - start) + "ms");
		// System.out.println(data.getUserString(2782357,0));
		// System.out.println(data.getItemsList(2782357,0));
		//
		// System.out.println(data.getItemToUserList(24787,0));
		// System.out.println("打印用户下物品对应的其他用户");
		// re.printUserItemToUser(4534661, 0, 102);
		// re.printUserToUserSim(4534661,0, 102, 0.05f,0.1f);
		// System.out.println(ConfigData.mallSql);
		// System.exit(1);
	}

	/**
	 * 执行mall相关的评论数据
	 */
	public static HashMap<Integer, GraphLinkedMethod> runCommentFeatherMall() {
		HashMap<Integer, GraphLinkedMethod> re = new HashMap<Integer, GraphLinkedMethod>();
		MysqlConnection mysql = new MysqlConnection("192.168.1.4", 3306,
				"fcMysql", "root", "zjroot");
		ResultSet result = mysql.sqlSelect(ConfigData.commentFeatherSql).resultSet;
		try {
			while (result.next()) {
				int mallId = result.getInt(1);
				int brandId = result.getInt(2);
				String brandName = result.getString(3);
				int cate = result.getInt(4);
				int shopId = result.getInt(5);
				int userId = result.getInt(6);
				String comment = result.getString(7);
				int cityId = result.getInt(8);
				// System.out.println("cityId:" + cityId + "mall:" + mallId
				// + "\tuserId:" + userId);
				GraphLinkedBean bean = new GraphLinkedBean(mallId, true);
				GraphLinkedBean bean2 = new GraphLinkedBean(userId, false);
				GraphLinkedMethod method = re.get(cityId);
				if (method == null) {
					method = new GraphLinkedMethod();
					re.put(cityId, method);
					method.addEdge(bean, bean2, 1);
				} else {
					method.addEdge(bean, bean2, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	/**
	 * 执行mall相关的评论数据
	 */
	public static HashMap<Integer, GraphLinkedMethod> runCommentFeatherBrand() {
		HashMap<Integer, GraphLinkedMethod> re = new HashMap<Integer, GraphLinkedMethod>();
		MysqlConnection mysql = new MysqlConnection("192.168.1.4", 3306,
				"fcMysql", "root", "zjroot");
		ResultSet result = mysql.sqlSelect(ConfigData.commentFeatherSql).resultSet;
		try {
			while (result.next()) {
				int mallId = result.getInt(1);
				int brandId = result.getInt(2);
				String brandName = result.getString(3);
				int cate = result.getInt(4);
				int shopId = result.getInt(5);
				int userId = result.getInt(6);
				String comment = result.getString(7);
				int cityId = result.getInt(8);
				// System.out.println("cityId:" + cityId + "mall:" + mallId
				// + "\tuserId:" + userId);
				GraphLinkedBean bean = new GraphLinkedBean(brandId, true);
				GraphLinkedBean bean2 = new GraphLinkedBean(userId, false);
				GraphLinkedMethod method = re.get(cityId);
				if (method == null) {
					method = new GraphLinkedMethod();
					re.put(cityId, method);
					method.addEdge(bean, bean2, 1);
				} else {
					method.addEdge(bean, bean2, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	/**
	 * 执行评论 返回城市 对应的用户推荐信息
	 */
	public static HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> runMallComment() {
		HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> result = new HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>>();
		if(true){
			return result;
		}
		CosinSimilary2 simFunc = new CosinSimilary2();
		HashMap<Integer, GraphLinkedMethod> re = RecommendMain
				.runCommentFeatherMall();
		// ArrayList<GraphLinkedBean>
		// itemBean=reulst.getItemToUser("5666",0.01f);
		// Collections.sort(itemBean);
		// System.out.println(itemBean.size());
		// for(GraphLinkedBean bean:itemBean)
		// System.out.println(bean.toString());
		int i = 0;
		// 如何添加跨城市的相似mall
		for (Entry<Integer, GraphLinkedMethod> cityMap : re.entrySet()) {
			HashMap<Integer, LinkedList<GeneralSortBean>> userRe = new HashMap<Integer, LinkedList<GeneralSortBean>>();
			for (Entry<Integer, HashMap<Integer, Float>> map : cityMap
					.getValue().matrix.entrySet()) {
				// 遍历每一个用户
				i++;
				int j = 0;
				HashMap<Integer, Float> value1 = map.getValue();
				if (value1 == null) {
					continue;
				}
				LinkedList<GeneralSortBean> sort = new LinkedList<GeneralSortBean>();
				for (Entry<Integer, HashMap<Integer, Float>> map2 : cityMap
						.getValue().matrix.entrySet()) {
					j++;
					if (i == j) {
						continue;
					}
					HashMap<Integer, Float> value2 = map2.getValue();
					if (value2 == null) {
						continue;
					}
					Float va = simFunc.getSimilaryF(value1, value2);
					// System.out.println("i:"+i+"\tj:"+j+"\t"+va);
					if (Float.compare(va, 0f) == 0) {
						continue;
					}
					GeneralSortBean bean = new GeneralSortBean();
					bean.id = map2.getKey();
					bean.value = va;
					sort.add(bean);
				}
				if (sort.size() <= 0) {
					continue;
				}
				Collections.sort(sort);
				// for(GeneralSortBean sortBean:sort)
				// {
				// System.out.println();
				// }
				userRe.put(map.getKey(), sort);
			}
			result.put(cityMap.getKey(), userRe);
		}
		return result;
	}

	/**
	 * 执行评论 返回城市 对应的用户推荐信息
	 */
	public static HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> runBrandComment() {
		HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> result = new HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>>();
		if(true){
			return result;
		}
		CosinSimilary2 simFunc = new CosinSimilary2();
		HashMap<Integer, GraphLinkedMethod> re = RecommendMain
				.runCommentFeatherBrand();
		// ArrayList<GraphLinkedBean>
		// itemBean=reulst.getItemToUser("5666",0.01f);
		// Collections.sort(itemBean);
		// System.out.println(itemBean.size());
		// for(GraphLinkedBean bean:itemBean)
		// System.out.println(bean.toString());
		int i = 0;
		// 如果为跨城市 需要剔除
		for (Entry<Integer, GraphLinkedMethod> cityMap : re.entrySet()) {
			HashMap<Integer, LinkedList<GeneralSortBean>> userRe = new HashMap<Integer, LinkedList<GeneralSortBean>>();
			for (Entry<Integer, HashMap<Integer, Float>> map : cityMap
					.getValue().matrix.entrySet()) {
				i++;
				int j = 0;
				HashMap<Integer, Float> value1 = map.getValue();
				if (value1 == null) {
					continue;
				}
				LinkedList<GeneralSortBean> sort = new LinkedList<GeneralSortBean>();
				for (Entry<Integer, HashMap<Integer, Float>> map2 : cityMap
						.getValue().matrix.entrySet()) {
					j++;
					if (i == j) {
						continue;
					}
					HashMap<Integer, Float> value2 = map2.getValue();
					if (value2 == null) {
						continue;
					}
					Float va = simFunc.getSimilaryF(value1, value2);
					if (Float.compare(va, 0f) == 0) {
						continue;
					}
					GeneralSortBean bean = new GeneralSortBean();
					bean.id = map2.getKey();
					bean.value = va;
					sort.add(bean);
				}
				if (sort.size() <= 0) {
					continue;
				}
				Collections.sort(sort);
//				System.out.println("城市:"+cityMap.getKey()+"用户:"+map.getKey());
//				for(GeneralSortBean be:sort)
//				{
//					System.out.println("推:"+be.toString());
//				}
				//将城市拓展至多个城市
				for (Entry<Integer, HashMap<Integer, Float>> map3 : cityMap
						.getValue().matrix.entrySet()) {
					userRe.put(map3.getKey(), sort);
				}
				
			}
			result.put(cityMap.getKey(), userRe);
		}
		return result;
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("./log4j.properties");
		// RecommandMain.runMallComment();
//		HashMap<Integer, HashMap<Integer, LinkedList<GeneralSortBean>>> result=RecommandMain.runBrandComment();
//		for(GeneralSortBean bean:result.get(86999031).get(24687))
//		{
//			System.out.println(bean.toString());
//		}
		
		 RecommendMain.runMallRecommand(1f);
//		 RecommendMain.runBrandRecommand(1f);
	}
}