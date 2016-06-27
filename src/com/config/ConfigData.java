package com.config;

import com.util.FileUtil2;

/**
 * 读取数据信息
 * 
 * @author Administrator
 *
 */
public class ConfigData {

	/**
	 * mall groupid mall的catgory brandid pre mall对应的品牌数据 剔除掉<=2的数据
	 */
	public static String mallSql = null;
	/**
	 * mall的feather数据
	 */
	public static String mallFeatherSql = null;
	
	public static String mallFeatherOneSql=null;
	/**
	 * 品牌的feather数据
	 */
	public static String brandFeatherSql = null;

	public static String brandFeatherOneSql=null;
	/**
	 * mall对应的业态数量数据
	 */
	public static String mallCategorySql = null;

	/**
	 * brand groupid brand的catgory mallid pre mall对应的品牌数据 剔除掉<=2的数据
	 */
	public static String brandSql = null;
	/**
	 * 品牌推荐 mall的feather数据
	 */
	public static String brandMallFeatherSql = null;
	public static String brandMallFeatherOneSql=null;
	/**
	 * 品牌推荐 品牌的feather数据
	 */
	public static String brandBrandFeatherSql = null;
	
	public static String brandBrandFeatherOneSql=null;

	/**
	 * mall 的名字
	 */
	public static String mallIdToNameSql = null;
	/**
	 * brand 的名字
	 */
	public static String brandIdToNameSql = null;
	/**
	 * 分类对应的名字
	 */
	public static String categoryIdToNameSql = null;
	/**
	 * 城市对应的名字
	 */
	public static String cityIdToNameSql = null;
	
	/**
	 * 一级对应二级业态的关系
	 */
	public static String cateCat1Cat2Sql=null;
	/**
	 * mall的业态属性
	 */
	public static String mallPropertiesSql=null;
	
	/**
	 * 品牌所属商圈
	 */
	public static String brandInvertAreaSql=null;
	
	/**
	 * 城市对应商圈数
	 */
	public static String areaSql=null;
	
	/**
	 * 商圈内部的mall
	 */
	public static String areaToMallSql=null;
	/**
	 * 城市名
	 */
	public static String cityNameSql=null;
	/**
	 * 点评的cityCode
	 */
	public static String cityDpCodeSql=null;
	
	/**
	 * 平台的城市id和点评id 映射
	 */
	public static String cityIdToDpIdSql=null;
	
	/**
	 * mall内部的品牌，用户信息
	 */
	public static String commentFeatherSql=null;
	
	/**
	 * 所有的分类排序
	 */
	public static String cateAllRank=null;
	/**
	 * 一级分类排序
	 */
	public static String cateOneRank=null;
	/**
	 * 高级 奢侈等的排序 无序
	 */
	public static String levelRank=null;
	static {
		// mall recommand 数据
		FileUtil2 fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mall/mallData.sql", "utf-8", false);
		mallSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mall/mallFeather.sql", "utf-8", false);
		mallFeatherSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mall/mallFeatherOne.sql", "utf-8", false);
		mallFeatherOneSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mall/brandFeather.sql", "utf-8", false);
		brandFeatherSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mall/brandFeatherOne.sql", "utf-8", false);
		brandFeatherOneSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mall/mallCategory.sql", "utf-8", false);
		mallCategorySql = fileUtil.readAllAndClose2(" ","#");

		// brand recommand 数据
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brand/brandData.sql", "utf-8", false);
		brandSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brand/mallFeather.sql", "utf-8", false);
		brandMallFeatherSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brand/mallFeatherOne.sql", "utf-8", false);
		brandMallFeatherOneSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brand/brandFeather.sql", "utf-8", false);
		brandBrandFeatherSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brand/brandFeatherOne.sql", "utf-8", false);
		brandBrandFeatherOneSql = fileUtil.readAllAndClose2(" ","#");

		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mallIdToName.sql", "utf-8", false);
		mallIdToNameSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brandIdToName.sql", "utf-8", false);
		brandIdToNameSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/categoryIdToName.sql", "utf-8", false);
		categoryIdToNameSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cityIdToName.sql", "utf-8", false);
		cityIdToNameSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/mallProperties.sql", "utf-8", false);
		mallPropertiesSql = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cateCat1Cat2.sql", "utf-8", false);
		cateCat1Cat2Sql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/brandInvertArea.sql", "utf-8", false);
		brandInvertAreaSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/area.sql", "utf-8", false);
		areaSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/areaToMall.sql", "utf-8", false);
		areaToMallSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cityName.sql", "utf-8", false);
		cityNameSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cityDpCode.sql", "utf-8", false);
		cityDpCodeSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cityIdToDpId.sql", "utf-8", false);
		cityIdToDpIdSql = fileUtil.readAllAndClose2(" ","#");
		
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/commentFeather.sql", "utf-8", false);
		commentFeatherSql = fileUtil.readAllAndClose2(" ","#");
		
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cateAllRank.sql", "utf-8", false);
		cateAllRank = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/cateOneRank.sql", "utf-8", false);
		cateOneRank = fileUtil.readAllAndClose2(" ","#");
		fileUtil = new FileUtil2(System.getProperty("user.dir")
				+ "/config/levelRank.sql", "utf-8", false);
		levelRank = fileUtil.readAllAndClose2(" ","#");
		
	}
}
