package com.fc.recommend.reDataProcession;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.pool.impl.GenericKeyedObjectPool.Config;
import org.apache.log4j.PropertyConfigurator;

import com.config.ConfigData;
import com.config.ConfigStatic;
import com.db.MongoDb;
import com.db.MysqlConnection;
import com.fc.commend.graphLinked.GeneralSortBean;
import com.fc.recommend.dataBean.FeatherNode;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserInfo;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.dataLoader.DataGeneralLoader;
import com.fc.recommend.dataLoader.DataLoader;
import com.fc.recommend.dataModel.DataModel;
import com.fc.recommend.dataOut.dataBean.CategoryBean;
import com.fc.recommend.dataOut.dataBean.RecommendBean;
import com.fc.recommend.dataOut.dataBean.SonBean;
import com.fc.recommend.dataOut.dataBean.SonSimBean;
import com.math.MathBase;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.util.FileUtil2;

/**
 * 
 * @author Administrator
 *
 */
public class FillMethodDataProcession {

	/**
	 * 业态文件
	 */
	public String brand_hot_cat2_file = System.getProperty("user.dir")
			+ "/config/brand_hot_cat2.txt";
	/**
	 * 是否为mall
	 */
	public boolean isMall = true;

	public MysqlConnection mysql = null;

	public Random random = new Random();

	public MongoDb mongo = null;
	/**
	 * key 为 mallid mall 推荐 出 brand key 为 cityId_categoryName
	 */
	public HashMap<String, ArrayList<PropertiesBean>> brandMap = new HashMap<String, ArrayList<PropertiesBean>>();
	
	/**
	 * 平台内部 mall id  brand id 对应的名字
	 */
	HashMap<Integer,HashMap<Integer, String>> mapName = new HashMap<Integer,HashMap<Integer,String>>();
	/**
	 * 品牌 推荐 出 mall brand 使用 地理填补的方法去填补 key对应 cityId_areaId value 为对应填补的mall数据
	 */
	public HashMap<String, ArrayList<PropertiesBean>> mallMap = new HashMap<String, ArrayList<PropertiesBean>>();
	/**
	 * 品牌所在商圈信息 key 对应 cityId_brandId value areas
	 */
	public HashMap<String, HashSet<Integer>> brandArea = new HashMap<String, HashSet<Integer>>();
	/**
	 * 商圈中存在的mall
	 */
	public HashMap<Integer, int[]> areaContainsMall = new HashMap<Integer, int[]>();
	/**
	 * 城市对应的所有商圈
	 */
	public HashMap<Integer, int[]> area = new HashMap<Integer, int[]>();
	/**
	 * 存储的所有业态
	 */
	public HashSet<String> category = new HashSet<String>();
	/**
	 * 城市 点评
	 */
	public HashSet<Integer> cityDpCode = new HashSet<Integer>();
	/**
	 * dp城市id 和平台id映射
	 */
	public HashMap<Integer,Integer> dpCityIdToCityId=new HashMap<Integer,Integer>();
	/**
	 * 一级业态对应二级业态关系
	 */
	public HashMap<Integer, String[]> categoryCat1Cat2 = null;
	/**
	 * mall业态关系 mall 所使用的业态 按照平台1级业态对应的点评二级业态的 热门品牌推荐 key 为 平台1级业态 value
	 * 所存在的全部点评二级业态
	 * key mallId_categoryId
	 */
	public HashMap<String, String[]> mallCategory = null;
	/**
	 * 品牌属性关系 无效
	 */
	public HashMap<Integer, String[]> brandCategory = null;

	/**
	 * 评论计算流
	 */
	public HashMap<Integer,HashMap<Integer, LinkedList<GeneralSortBean>>> commentFillMethod=null;
	
	
	public FillMethodDataProcession(boolean isMall, DataLoader data,HashMap<Integer,HashMap<Integer,String>> mapName,
			MysqlConnection mysql, MongoDb mongo,HashMap<Integer,HashMap<Integer, LinkedList<GeneralSortBean>>> commentFillMethod) {
		this.mapName=mapName;
		this.isMall = isMall;
		readData(data.dataModel);
		this.contentModel = data.contentModel;
		this.mongo = mongo;
		this.mysql = mysql;
		this.commentFillMethod=commentFillMethod;
	//	readOtherData();
	}

	public DataModel dataModel = null;

	public DataModel contentModel = null;

	/**
	 * 读取数据
	 */
	public void readData(DataModel dataModel) {
		// HashMap<Integer,HashMap<Integer, UserInfo>>
		this.dataModel = dataModel;
	}

	/**
	 * 读取其他数据源 填充列表的填充
	 */
	public void readOtherData() {
		area = DataGeneralLoader.readIntFeatherMysql(mysql, ConfigData.areaSql);
		cityDpCode = DataGeneralLoader.readIntMysql(mysql,
				ConfigData.cityDpCodeSql);
		dpCityIdToCityId=DataGeneralLoader.readIntMapMysql(mysql,
				ConfigData.cityIdToDpIdSql);
		if (isMall) {
			// 读取平台1及业态 对应的点评二级业态
			categoryCat1Cat2 = DataGeneralLoader.readStringFeatherMysql(mysql,
					ConfigData.cateCat1Cat2Sql);
			// mall 获取mall 唯一
			// 获取mall对应的二级业态属性集
			mallCategory = DataGeneralLoader.readMallCat2StringFeatherMysql(mysql,
					ConfigData.mallPropertiesSql);
			for (Entry<String, String[]> entry : mallCategory.entrySet()) {
				for (String str : entry.getValue()) {
					category.add(str);
				}
			}
			System.out.println("读取业态 热门数据");
			// 读取业态下的排名//写死//读取城市二级业态数据
			// 需要判断文件中是否存在

			File file = new File(brand_hot_cat2_file);
			if (file.exists()) {
				System.out.println("读取配置文件中业态热门品牌");
				FileUtil2 fileUitl = new FileUtil2(brand_hot_cat2_file,
						"utf-8", false);
				LinkedList<String> strList = fileUitl.readAndClose();
				ArrayList<PropertiesBean> list = null;
				String city = null;
				String cate = null;
				while (strList.size() > 0) {
					String str = strList.pollFirst();
					if (str.equals("")) {
						continue;
					}
					String[] input = str.split("\t");
					if (list == null) {
						list = new ArrayList<PropertiesBean>();
						city = input[0];
						cate = input[1];
						PropertiesBean properties = new PropertiesBean();
						properties.name = input[2];
						properties.count = Integer.parseInt(input[3]);
						list.add(properties);
					} else if (city.equals(input[0]) && cate.equals(input[1])) {
						PropertiesBean properties = new PropertiesBean();
						properties.name = input[2];
						properties.count = Integer.parseInt(input[3]);
						list.add(properties);
					}else{
						brandMap.put(dpCityIdToCityId.get(Integer.parseInt(city)) + "_" +cate, list);
						list=new ArrayList<PropertiesBean>();
						city=input[0];
						cate=input[1];
						PropertiesBean properties = new PropertiesBean();
						properties.name = input[2];
						properties.count = Integer.parseInt(input[3]);
						list.add(properties);
					}
				}
				if(list!=null)
				{
					brandMap.put(dpCityIdToCityId.get(Integer.parseInt(city)) + "_" +cate, list);
				}
			} else {
				//需要写入文件
				FileUtil2 fileUitl = new FileUtil2(brand_hot_cat2_file,
						"utf-8","delete");
				LinkedList<String> input=new LinkedList<String>();
				DBCollection collection = mongo.getCollection("brand_hot_cat2");
				for (int city : cityDpCode) {
					for (String cate : category) {
						System.out.println("city:" + city + "\tcat2:" + cate);
						BasicDBObject dbObject = new BasicDBObject();
						dbObject.put("city_code", city);
						dbObject.put("cat2", cate);
						dbObject.put("deal_date", "2015-01-22");
						BasicDBObject reObject = new BasicDBObject();
						reObject.put("value", 1);
						BasicDBObject sort = new BasicDBObject();
						sort.put("value.avghit", -1);
						DBCursor cursor = collection.find(dbObject, reObject)
								.sort(sort).limit(40);
						if (cursor == null) {
							continue;
						}
						ArrayList<PropertiesBean> list = new ArrayList<PropertiesBean>();
						while (cursor.hasNext()) {
							BasicDBObject obj = (BasicDBObject) cursor.next();
							// System.out.print(obj);
							BasicDBObject val = (BasicDBObject) obj
									.get("value");
							String shopName = val.getString("shopName");
							PropertiesBean properties = new PropertiesBean();
							properties.name = shopName;
							properties.count = val.getInt("avghit");
							list.add(properties);
							input.add(city+"\t"+cate+"\t"+shopName+"\t"+properties.count);
						}
						brandMap.put(dpCityIdToCityId.get(city) + "_" + cate, list);
					}
					fileUitl.write(input);
					fileUitl.close();
				}
			}
		} else {
			// 如果为brand则需要按照区位 推荐 mall
			brandArea = DataGeneralLoader.readCityAndIdAreaIdFeatherMysql(
					mysql, ConfigData.brandInvertAreaSql);
			areaContainsMall = DataGeneralLoader.readIntFeatherMysql(mysql,
					ConfigData.areaToMallSql);
		}
	}

	/**
	 * 读取数据源
	 * 
	 * @param userId
	 */
	public HashSet<String> readSet(int userId,HashMap<Integer,String> brandNameMapping) {
		HashSet<String> set = new HashSet<String>();
		for (Entry<Integer, HashMap<Integer, UserInfo>> group : dataModel.userGroupAll
				.entrySet()) {
			for (Entry<Integer, UserInfo> category : group.getValue()
					.entrySet()) {
				UserNode userNode = category.getValue().userGroup.get(userId);
				if (userNode == null) {
					continue;
				}
				for (Entry<Integer, ItemNode> item : userNode.items.entrySet()) {
					ItemNode itemNode = item.getValue();
					set.add(brandNameMapping.get(itemNode.itemId));
				}
			}
		}
		for (Entry<Integer, HashMap<Integer, UserInfo>> group : dataModel.userGroupAllTest
				.entrySet()) {
			for (Entry<Integer, UserInfo> category : group.getValue()
					.entrySet()) {
				UserNode userNode = category.getValue().userGroup.get(userId);
				if (userNode == null) {
					continue;
				}
				for (Entry<Integer, ItemNode> item : userNode.items.entrySet()) {
					ItemNode itemNode = item.getValue();
					set.add(brandNameMapping.get(itemNode.itemId));
				}
			}
		}
		return set;
	}

	/**
	 * 执行推荐
	 * 
	 * @param recommandBean
	 */
	public void run(int itemCount, int simCount, RecommendBean recommandBean) {
		// 对于分类下不全的补充
		fillCategory(itemCount, recommandBean);
		// 对于相似不全的补充
		fillSimilary(simCount, recommandBean);
	}

	/**
	 * 
	 * @param itemCount
	 *            推荐的总物品书
	 * @param recommandBean
	 *            推荐
	 */
	public void fillCategory(int itemCount, RecommendBean recommandBean) {
	
		if (isMall) {
			if(mallCategory==null){
				return ;
			}
			HashSet<String> set = readSet(recommandBean.id,this.mapName.get(ConfigStatic.BRAND));
			for (CategoryBean categoryBean : recommandBean.category) {
				int count = categoryBean.item.size();
				if (count < itemCount) {
					//System.out.println("填充:"+recommandBean.id+"\t"+categoryBean.category);
					// 填补
					// 获取排序后的list
					String[] cate2List=mallCategory.get(recommandBean.id+"_"+categoryBean.category);
					if(cate2List==null)
					{
						continue;
					}
					ArrayList<PropertiesBean> list=new ArrayList<PropertiesBean>();
					for(String cate:cate2List)
					{
						ArrayList<PropertiesBean> cateCat2=brandMap.get(recommandBean.cityId+"_"+cate);
						if(cateCat2!=null)
						{
							list.addAll(cateCat2);
						}
					}
					//此处可以根据mall的具体业态属性提供不同的权重值//如果这样需要考虑不同二级业态属于一个业态的排名关系，需要修正
					if(list.size()>0)
					{
						Collections.sort(list);
						//以及业态含有的二级业态
						//System.out.println("userId:"+recommandBean.id);
						fillCategory(itemCount - count, categoryBean, set,list);
					}
				}
			}
		} else {
			HashSet<Integer> set = new HashSet<Integer>();
			for (CategoryBean categoryBean : recommandBean.category) {
				int count = categoryBean.item.size();
				if (count < itemCount) {
					// 填补
					fillBrand(itemCount - count, recommandBean.cityId,
							recommandBean.id, categoryBean, set);
				}
			}
		}
	}

	/**
	 * 
	 * @param count
	 *            需要填补的数量
	 * @param categoryBean
	 *            分类
	 * @param map
	 *            user 的已存在的 item;
	 * @param mapList
	 *            填补的数据集
	 */
	public void fillCategory(int count, CategoryBean categoryBean,
			HashSet<String> map, ArrayList<PropertiesBean> mapList) {
		//System.out.println("填充");
		if (mapList == null) {
			//System.out.println("跳出");
			return;
		}
		//System.out.println("填充2");
		int i = 0;
		for (SonBean bean : categoryBean.item) {
			map.add(bean.name);
		}
		for (PropertiesBean pro : mapList) {
			if (i >= count) {
				//System.out.println("跳出:"+i);
				break;
			}
			if (map.contains(pro.name)) {
				//System.out.println("剔除item："+pro.name);
				continue;
			}
			map.add(pro.name);
			// 填补
			i++;
			//System.out.println("新增:" + pro.name);
			SonBean bean = new SonBean();
			bean.id = pro.id;
			bean.name = pro.name;
			bean.count = -2;
			categoryBean.item.add(bean);
		}
	}

	/**
	 * 
	 * 填补brand 推荐mall
	 * 
	 * @param count
	 *            需要填补的数量
	 * @param categoryBean
	 *            分类
	 * @param map
	 *            user 的已存在的 item;
	 * @param mapList
	 *            填补的数据集
	 */
	public void fillBrand(int count, int cityId, int brandId,
			CategoryBean categoryBean, HashSet<Integer> map) {
		if(brandArea==null){
			return;
		}
		int i = 0;
		for (SonBean bean : categoryBean.item) {
			// 应该在这个地方判断一下商圈信息
			// 如果能判断brand的商圈分布属性则可以大幅度优化推荐结果
			// 目前略 主要使用 svm测试
			map.add(bean.id);
		}
		// 获取品牌所属商圈
		HashSet<Integer> area = this.brandArea.get(cityId + "_" + brandId);
		if(area==null)
		{
			if(area==null)
			{
				area=new HashSet<Integer>();
			}
		}
		// 获取该城市的全部商圈
		int[] areaAll = this.area.get(cityId);
		ArrayList<Integer> areaUse = new ArrayList<Integer>();
		for (int j = 0; j < areaAll.length; j++) {
			// 判断品牌是否存在于商圈
			if (area.contains(areaAll[j])) {
			} else {
				// 添加不存在的商圈
				areaUse.add(areaAll[j]);
			}
		}
		if (areaUse.size() > 0) {
			int nextCount = 0;// 标记的次数
			// 目前使用随机化填补方法
			// 骰子
			while (true) {
				if (i == count || nextCount > count * 2) {
					break;
				}
				int val = MathBase.abs(random.nextInt() % areaUse.size());
				// 获取商圈 对应的所有mall
				int[] mallList = this.areaContainsMall.get(areaUse.get(val));
				if (mallList == null) {
					nextCount++;
					continue;
				} else {
					while (true) {
						// 从其中获取一个mall
						int mallId = mallList[MathBase.abs(random.nextInt()
								% mallList.length)];
						nextCount++;
						if (map.contains(mallId)) {
							break;
						} else {
							map.add(mallId);
							i++;
							//System.out.println("添加:"+mallId);
							SonBean bean = new SonBean();
							bean.id = mallId;
							bean.count = -3;
							categoryBean.item.add(bean);
							break;
						}
					}
				}
			}
		}
		// 填补

	}

	/**
	 * 
	 * @param simCount
	 *            相似的数量
	 * @param recommandBean
	 *            推荐
	 */
	public void fillSimilary(int simCount, RecommendBean recommandBean) {
		
		if(recommandBean.similary.size()>=simCount)
		{
			return;
		}
		if(commentFillMethod!=null)
		{//当存在评论处理集合则执行
			HashMap<Integer,LinkedList<GeneralSortBean>> list=this.commentFillMethod.get(recommandBean.cityId);
			if(list==null)
			{
				return;
			}
			//如果存在城市
			LinkedList<GeneralSortBean> userList=list.get(recommandBean.id);
			if(userList==null||userList.size()==0||recommandBean.similary.size()>=simCount)
			{
				return;
			}
			//如果存在推荐列表
			HashSet<Integer> similarySet=new HashSet<Integer>();
			for(SonSimBean simBean:recommandBean.similary)
			{
				similarySet.add(simBean.simId);
			}
			if(isMall)
			{
				fillSimilaryMall(simCount-similarySet.size(),recommandBean,similarySet,userList);
			}else{
				fillSimilaryBrand(simCount-similarySet.size(),recommandBean,similarySet,userList);
			}
		}
	}
	/**
	 * 对mall填充给予用户的相似度信息
	 * @param simCount
	 * @param fillBean
	 */
	public void fillSimilaryMall(int simCount,RecommendBean recommandBean,HashSet<Integer> set,LinkedList<GeneralSortBean> fillBean)
	{
		int i=0;
		for(GeneralSortBean bean:fillBean)
		{
			if(set.contains(bean.id))
			{
				continue;
			}
			i++;
			set.add(bean.id);
			SonSimBean simBean=new SonSimBean();
			simBean.simId=bean.id;
			simBean.value=bean.value;
			System.out.println(recommandBean.id+"\t添加:"+simBean.simId);
			recommandBean.similary.add(simBean);
			if(i>=simCount)
			{
				break;
			}
		}
	}
	
	/**
	 * 对brand填充给予用户的相似度信息
	 * @param simCount
	 * @param fillBean
	 */
	public void fillSimilaryBrand(int simCount,RecommendBean recommandBean,HashSet<Integer> set,LinkedList<GeneralSortBean> fillBean)
	{
		int i=0;
		for(GeneralSortBean bean:fillBean)
		{
			if(set.contains(bean.id))
			{
				continue;
			}
			i++;
			set.add(bean.id);
			SonSimBean simBean=new SonSimBean();
			simBean.simId=bean.id;
			simBean.value=bean.value;
			recommandBean.similary.add(simBean);
			System.out.println(recommandBean.id+"\t添加:"+simBean.simId);
			if(i>=simCount)
			{
				break;
			}
		}
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("./log4j.properties");
		int dbmsFeather = 3;
		boolean isMall = true;
		MysqlConnection mysql = new MysqlConnection("192.168.1.4", 3306,
				"fcMysql", "root", "zjroot");
		// 获取业态下热门品牌
		MongoDb mongoHotBrand = new MongoDb("123.57.4.152", 27017, "dealdata");
		DataGeneralLoader dataLoader = new DataGeneralLoader();
		// 使用分类
		dataLoader.dataModel.isCategory = true;
		dataLoader.trainRate = 0.7f;
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
		/**
		 * 推荐填充
		 */
//		FillMethodDataProcession fileMethodDataProcession = new FillMethodDataProcession(
//				isMall, data, mysql, mongoHotBrand);
	}
}
