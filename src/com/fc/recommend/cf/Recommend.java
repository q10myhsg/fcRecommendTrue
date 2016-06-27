package com.fc.recommend.cf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import com.db.MongoDb;
import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.fc.recommend.dataBean.EvoleBean;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.StreamBean;
import com.fc.recommend.dataBean.UserInfo;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.dataLoader.DataLoader;
import com.fc.recommend.dataModel.DataModel;
import com.fc.recommend.dataOut.dataBean.ItemRecommendBean;
import com.fc.recommend.dataOut.dataBean.MallRecommendBean;
import com.fc.recommend.dataOut.dataBean.SonSimBean;
import com.fc.recommend.filter.CFParamStatic;
import com.fc.recommend.filter.ItemGetCel;
import com.fc.recommend.filter.ItemGetRurl;
import com.fc.recommend.filter.RecommenderFilterUtil;
import com.fc.recommend.filter.RecommenderHostItemCountFilter;
import com.fc.recommend.filter.RecommenderHostItemRateFilter;
import com.fc.recommend.filterWeight.UserHotItemLoss;
import com.fc.recommend.filterWeight.UserWeightUtil;
import com.fc.recommend.filterWeight.WeightUtil;
import com.fc.recommend.neighborhood.NeighBean;
import com.fc.recommend.neighborhood.NeighborhoodUtil;
import com.fc.recommend.reDataProcession.RecommendDataProcession;
import com.fc.recommend.similary2.CosinSimilary2;
import com.fc.recommend.similary2.LogLikelihoodRatioSimilary;
import com.fc.recommend.similary2.MathchHard;
import com.fc.recommend.similary2.SimilaryUtil;
import com.mongodb.DBCursor;
import com.mysql.jdbc.log.Log;
import com.sort.SortFast;
import com.sort.SortList;
import com.sort.SortUtil;
import com.util.FileUtil2;

/**
 * 推荐基于用户核心方法
 * 
 * @author Administrator
 * 
 */
public class Recommend {

	public static String splitStr = "^@@^";
	public static String splitReg = "\\^@@\\^";

	/**
	 * 是否打印推荐
	 */
	public boolean isPrintRecommand = false;

	/**
	 * 是否使用评估
	 */
	public boolean isEvalue = true;
	/**
	 * 是否打印评估值
	 */
	public boolean isPrintEvalue = true;
	/**
	 * 数据源
	 */
	public DataModel data = null;
	/**
	 * 属性
	 */
	public DataModel contentData = null;
	/**
	 * cf 相似性函数
	 */
	public SimilaryUtil simiFunc = null;
	/**
	 * 用户 和物品计算score的时候 结构化的比率 （1-x）非结构化的比率
	 */
	public float userItemRate = 0.1f;
	/**
	 * 邻域函数
	 */
	public NeighborhoodUtil neighborhoodFunc = new NeighborhoodUtil();
	/**
	 * 相似度加权方法
	 */
	public WeightUtil weight = new UserHotItemLoss();
	/**
	 * 作为基于内容推荐的权重信息
	 */
	public HashMap<Integer, float[]> weightPower = null;

	/**
	 * 最终推荐结果的过滤器方法
	 */
	public LinkedList<RecommenderFilterUtil> recommanderFilter = new LinkedList<RecommenderFilterUtil>();

	/**
	 * 数据清洗的的过滤器方法
	 */
	public LinkedList<RecommenderFilterUtil> dataClearnFilter = new LinkedList<RecommenderFilterUtil>();
	/**
	 * 物品的提取规则
	 */
	public ItemGetRurl itemRurl = new ItemGetCel();
	/**
	 * 输出数量限制
	 */
	public int recommandItemCount = 100;
	/**
	 * 统一相似度为 越大越好
	 */
	public NeighborhoodUtil userNeighFunction = new NeighborhoodUtil();
	/**
	 * 用于存储最终的推荐结果
	 */
	public HashMap<Integer, String> recommandMap = new HashMap<Integer, String>();
	/**
	 * 存储测试集和的评估值
	 */
	public HashMap<String, EvoleBean> recommandMapEvole = new HashMap<String, EvoleBean>();
	/**
	 * 库中全部商品的总数量
	 */
	public int itemCount = 12595;
	/**
	 * 最終輸出的命中率值
	 */
	public ArrayList<Float> outputPredict = new ArrayList<Float>();
	/**
	 * 最終輸出的對映射的用戶的物品總數
	 */
	public ArrayList<Integer> outputCount = new ArrayList<Integer>();

	/**
	 * mall 对应的业态数量
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> mallCategory = null;
	/**
	 * 共现矩阵 第一个为分类 不是用户组的 第二个为u1_u1 value
	 */
	public HashMap<Integer, HashMap<Long, Integer>> conMatrix = new HashMap<Integer, HashMap<Long, Integer>>();
	/**
	 * 分类下的物品
	 */
	public HashMap<Integer, HashSet<Integer>> categoryItem = new HashMap<Integer, HashSet<Integer>>();

	public static boolean isMall = true;
	/**
	 * 是否使用共现矩阵计算
	 */
	public boolean isConMatrix = false;

	/**
	 * 排序方法
	 */
	public SortUtil sortFunction = new SortFast();

	/**
	 * 推荐输出流
	 */
	public RecommendDataProcession recommandDataProcession = null;

	/**
	 * 是否输出
	 */
	public boolean isOutput = true;

	/**
	 * 
	 * @param limitItem
	 *            最终显示的物品数量
	 * @param simLimitRate
	 *            有效相似度 邻域比率值 默认为 up方向
	 * @param trainRate
	 *            训练比率
	 * @param mall
	 *            mall 对应 id 的名字
	 * @param shop
	 *            shop id对应名字
	 * @param outIntoFile
	 *            输出文件地址
	 * @param isMall
	 *            是mall推荐品牌，还是品牌推荐mall
	 */
	public Recommend(DataLoader dataLoader,
			RecommendDataProcession recommandDataProcession) {
		this.data = dataLoader.dataModel;
		this.contentData = dataLoader.contentModel;
		this.recommandDataProcession = recommandDataProcession;
		this.isMall = recommandDataProcession.outPutStream.isMall;
	}

	/**
	 * 设置物品总数量
	 * 
	 * @param itemCount
	 */
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * 设置最终推荐过滤器方法
	 * 
	 * @param recommanderFilter
	 */
	public void setRecommanderFilter(
			LinkedList<RecommenderFilterUtil> recommanderFilter) {
		this.recommanderFilter = recommanderFilter;
	}

	/**
	 * 设置最终推荐过滤器方法
	 * 
	 * @param recommanderFilter
	 */
	public void addRecommanderFilter(RecommenderFilterUtil recommanderFilter) {
		this.recommanderFilter.add(recommanderFilter);
	}

	/**
	 * 设置初始 数据清洗工具
	 * 
	 * @param recommanderFilter
	 */
	public void setDataClearnFilter(
			LinkedList<RecommenderFilterUtil> recommanderFilter) {
		this.dataClearnFilter = recommanderFilter;
	}

	/**
	 * 设置初始 数据清洗工具
	 * 
	 * @param recommanderFilter
	 */
	public void addDataClearnFilter(RecommenderFilterUtil recommanderFilter) {
		this.dataClearnFilter.add(recommanderFilter);
	}

	public WeightUtil getWeight() {
		return weight;
	}

	public void setWeight(WeightUtil weight) {
		this.weight = weight;
	}

	/**
	 * 设置物品提取规则
	 * 
	 * @param itemRurl
	 */
	public void setItemGetRurl(ItemGetRurl itemRurl) {
		this.itemRurl = itemRurl;
	}

	/**
	 * 使用的业态作为cf方法
	 */
	public int cfCategory = -1;

	/**
	 * 协同过滤执行程序
	 * 
	 * @param category
	 *            使用作为 相似度的分类 如果<=0则表示用自己
	 */
	public void run(int category) {
		System.out.println("启动使用分类:" + category);
		// 执行数据清洗
		this.cfCategory = category;
		UserInfo categoryUserInfo = null;
		UserInfo categoryUserInfoOther = null;
		if (isConMatrix) {
			System.out.println("计算共现矩阵");
			for (Entry<Integer, HashMap<Integer, UserInfo>> userList : data.userGroupAll
					.entrySet()) {
				// 每个组
				for (Entry<Integer, UserInfo> userInfo : userList.getValue()
						.entrySet()) {
					int cate = userInfo.getKey();
					HashMap<Long, Integer> cateConMatrix = this.conMatrix
							.get(cate);
					HashSet<Integer> cateItem = this.categoryItem.get(cate);
					if (cateConMatrix == null) {
						cateConMatrix = new HashMap<Long, Integer>();
						this.conMatrix.put(cate, cateConMatrix);
					}
					if (cateItem == null) {
						cateItem = new HashSet<Integer>();
						this.categoryItem.put(cate, cateItem);
					}
					// 计算实际的共现
					for (Entry<Integer, UserNode> userNode : userInfo
							.getValue().entrySet()) {
						int item1Index = 0;
						for (Entry<Integer, ItemNode> item1 : userNode
								.getValue().items.entrySet()) {
							item1Index++;
							cateItem.add(item1.getKey());
							int item2Index = 0;
							for (Entry<Integer, ItemNode> item2 : userNode
									.getValue().items.entrySet()) {
								item2Index++;
								if (item2Index <= item1Index) {
									continue;
								}
								long index = UserInfo.getU_U(item1.getKey(),
										item2.getKey());
								Integer val = cateConMatrix.get(index);
								if (val == null) {
									cateConMatrix.put(index, 1);
								} else {
									cateConMatrix.put(index, val + 1);
								}
							}
						}
					}
				}
			}
		}
		for (Entry<Integer, HashMap<Integer, UserInfo>> userGroupList : data.userGroupAll
				.entrySet()) {
			//第一个城市
			// 唯一性用户
			if (category > 0) {
				// 使用同一分类做相似度计算
				for (Entry<Integer, UserInfo> userCategoryInfo : userGroupList
						.getValue().entrySet()) {
					if (userCategoryInfo.getKey() == category) {
						categoryUserInfo = userCategoryInfo.getValue();
						break;
					}
				}
			}
			for (Entry<Integer, UserInfo> userCategory : userGroupList
					.getValue().entrySet()) {
				if (isMall) {
					// 组的业态
					for (Entry<Integer, HashMap<Integer, UserInfo>> userCategoryOther : data.userGroupAll
							.entrySet()) {
						//交互的第二个城市
						// 计算组与组之间的数据
						if (category > 0) {
							// 使用同一分类做相似度计算
							for (Entry<Integer, UserInfo> cateUserInfo : userCategoryOther
									.getValue().entrySet()) {
								if (cateUserInfo.getKey() == category) {
									categoryUserInfoOther = cateUserInfo
											.getValue();
									break;
								}
							}
						}
						// 一个用户的所有分类全计算完
						for (Entry<Integer, UserInfo> userInfoOther : userCategoryOther
								.getValue().entrySet()) {
							if (Integer.compare(userCategory.getKey(),userInfoOther.getKey())!=0) {// 不同分类不进行计算
								continue;
							}
							userCategory.getValue().dataClearnFilter(
									dataClearnFilter);
							// 执行相似度计算及推荐
							// 第一个为设定相似度的使用
							// 第二个为自己的
							System.out.println("执行组:" + userGroupList.getKey()
									+ "\t和组:" + userCategoryOther.getKey()
									+ "\t交互"+"\t分类组:"+userInfoOther.getKey()+"\t分类:"+userCategory.getKey());
							run(userGroupList.getKey(),
									userCategoryOther.getKey(),
									categoryUserInfo, categoryUserInfoOther,
									userCategory.getValue(),
									userInfoOther.getValue(),
									userCategory.getKey());
						}
					}
				} else {
					System.out.println("执行组:" + userGroupList.getKey()
							+ "\t和组:" + userGroupList.getKey()
							+ "\t交互\tsize:"+userCategory.getValue().size()+"\t分类:"+userCategory.getKey());
					run(userGroupList.getKey(),userGroupList.getKey(),
							categoryUserInfo, categoryUserInfoOther,
							userCategory.getValue(),userCategory.getValue(),
							userCategory.getKey());
				}
			}
		}
		if(this.recommandDataProcession!=null)
		{
			this.recommandDataProcession.fatherStatus=false;
		}
	}

	/**
	 * 执行 groupId 对应城市
	 * 
	 * @param groupId
	 *            使用数据集1 对应的组
	 * @param groupId2
	 *            计算相似度用户集2 对应的组
	 * @param 当前用户集合
	 *            使用的分类
	 * @param categoryUserInfo
	 *            使用的分类作为相似度计算基准
	 * @param categoryUserInfoOther
	 *            第二用户集的使用的分类数据计算相似度
	 * @param userInfo
	 *            实际的用户集
	 * @param userInfoOther 第二用户集
	 * @param 使用的分类
	 */
	public void run(int groupId, int groupId2, UserInfo categoryUserInfo,
			UserInfo categoryUserInfoOther, UserInfo userInfo,
			UserInfo userInfoOther, int category) {
		// 初始化权重值
		weight.init(userInfo);
		weight.add(userInfoOther);
		ArrayList<UserNode> users = userInfo.getUsers();
		// 计算邻域 用户
		HashSet<Integer> preSet = new HashSet<Integer>();
		HashSet<Integer> allSet = new HashSet<Integer>();
		for (UserNode userNode : users) {
			// 执行某个用户推荐
			// System.out.println("用户:"+userNode.userId);
			run(groupId, groupId2, categoryUserInfo, categoryUserInfoOther,
					userNode, userInfoOther, category, preSet, allSet);
		}
	}

	/**
	 * 执行子类
	 * 
	 * @param groupId
	 *            用户的组
	 * @param groupId2
	 *            用户2的组
	 * @param categoryUserInfo
	 *            用户1 使用的分类
	 * @param categoryUserInfoOther
	 *            用户2 使用的分类
	 * @param userNode
	 *            使用的用户
	 * @param userInfoOther
	 *            用户2 使用的用户
	 * @param category
	 *            用户1对应的分类
	 * @param preSet
	 *            正确率值
	 * @param allSet
	 *            全部数值
	 */
	public void run(int groupId, int groupId2, UserInfo categoryUserInfo,
			UserInfo categoryUserInfoOther, UserNode userNode,
			UserInfo userInfoOther, int category, HashSet<Integer> preSet,
			HashSet<Integer> allSet) {
		// if (userNode.getUserId() == 4534661) {
		// System.out.println();
		// }
		// 初始化数据
		userNode.sortItem = new LinkedList<ItemNode>();
		// 获取邻域用户
		NeighBean userList = null;
		if (categoryUserInfo == null) {
			// 如果给出的分类为空则表示使用自己的数据
			userList = this.neighborhoodFunc.getNearUser(groupId,groupId2, userNode,
					userInfoOther, this.contentData.feather);
		} else {
			UserNode tempUserNode = categoryUserInfo.getUser(userNode
					.getUserId());
			if (tempUserNode == null) {// 如果为空则表示 该用户不存在与分类中则使用自己的业态
				userList = this.neighborhoodFunc.getNearUser(groupId,groupId2, userNode,
						userInfoOther, this.contentData.feather);
			} else {
				userList = this.neighborhoodFunc.getNearUser(groupId,groupId2,
						tempUserNode, categoryUserInfoOther, category,
						this.contentData.feather, mallCategory);
			}
		}
		int[] userIdList = userList.usersId;
		// 获取有效的邻域用户
		LinkedList<SonSimBean> simiBean = new LinkedList<SonSimBean>();
		for (int i = 0; i < userIdList.length - 1; i++) {
			if (userIdList[i] == 0) {// 如果为0 则表示没有相似id
				break;
			}
			// 添加相似的user
			SonSimBean simBean = new SonSimBean();
			simBean.setSimId(userIdList[i]);
			simBean.setValue(userList.scores[i]);
			simiBean.add(simBean);
			// 相似用户的物品添加规则
			UserNode simiUserNode = userInfoOther.getUserInfo().get(
					userIdList[i]);
			if (simiUserNode == null) {
				// System.out.println("该业态不存在用户:" + userIdList[i]);
				continue;
			} else {
				// userInfo.addUserSim(userNode.getUserId(),
				// userNode2.getUserId(), fll);
				// System.out.println("i:"+i);
				itemRurl.getRecommendItems(groupId, userNode, simiUserNode,
						userList.scores[i], this.contentData.feather,
						this.userItemRate, weight);
			}
		}
		// 重新排序并执行过滤器方法
		userNode.sortItem(true, recommanderFilter);
		if (userNode.sortItem.size() < this.recommandItemCount) {
			// 如果cf推荐结果无 则使用共现矩阵 填充推荐
			useConMatrixFunction(userNode, category);
		}
		if (this.isPrintRecommand) {
			System.out.println("用户相关信息:" + userNode.getUserId());
			for (SonSimBean simB : simiBean) {
				simB.print();
			}
			userNode.printRecommand(groupId, category, this.contentData.feather);
			userNode.print(groupId, this.contentData.feather);
		}
		// 评估方法
		if (this.isEvalue) {
			// 评估 最终的结果放在了 recommandMapEvole 中 key 对应 getEvoleKey 值
			evalue(groupId, category, userNode.getUserId(), preSet, allSet);
			if (isPrintEvalue) {
				String evalueKey = EvoleBean.getEvoleKey(groupId, category,
						userNode.getUserId(), evalueSplitRegex);
				EvoleBean evoleBean = recommandMapEvole.get(evalueKey);
				String statisticString = EvoleBean.getEvalueString(evalueKey,
						evoleBean, evalueSplitRegex);
				if (statisticString == null) {
				} else {
					System.out.println("相似组:" + groupId2 + "\t推荐"
							+ statisticString);
				}
			}
		}
		// 输出流
		if (isOutput) {
			StreamBean streamBean = new StreamBean(userNode, simiBean, groupId,
					groupId2, category);
			this.recommandDataProcession.add(this.recommandItemCount,
					this.neighborhoodFunc.similaryCount, streamBean);
		}
	}

	/**
	 * 使用同现矩阵
	 * 
	 * @param userNode
	 *            用户
	 */
	public void useConMatrixFunction(UserNode userNode, int category) {
		if (isConMatrix) {
			// System.out.println("计算同现:" + userNode.userId);
			HashMap<Long, Integer> conTemMatix = this.conMatrix.get(category);
			// 获取用户的train item 以及推荐物品
			HashSet<Integer> userItems = new HashSet<Integer>();
			for (Entry<Integer, ItemNode> itemNode : userNode.items.entrySet()) {
				userItems.add(itemNode.getKey());
			}
			for (ItemNode itemNode : userNode.sortItem) {
				userItems.add(itemNode.itemId);
			}
			// 获取补集
			int index = -1;
			int[] queueId = new int[this.recommandItemCount + 1];
			float[] queue = new float[this.recommandItemCount + 1];
			for (Integer itemC : this.categoryItem.get(category)) {
				float conValue = 0f;
				if (userItems.contains(itemC)) {
					continue;
				}
				for (Entry<Integer, ItemNode> itemNode : userNode.items
						.entrySet()) {

					Integer val = conTemMatix.get(UserInfo.getU_U(itemC,
							itemNode.getValue().itemId));
					if (val == null) {
						continue;
					} else {
						conValue += val * itemNode.getValue().value;
					}
				}
				index++;
				if (index > this.recommandItemCount) {
					index = recommandItemCount;
				}
				sortFunction.quickSort(conValue, itemC, index, queue, queueId,
						true);
			}
			// 获取同现后的排序物品
			for (int i = 0; i < queue.length; i++) {
				if (queueId[i] == 0) {
					break;
				}
				userNode.addSortItem(new ItemNode(queueId[i], category,
						queue[i], -1));// weight.getCount(queueId[i])
			}
		}
	}

	/**
	 * 获取某组的用户 的所有物品对应的其他用户
	 * 
	 * @param userId
	 * @param groupId
	 * @param categoryId
	 *            使用的分类
	 * @return
	 */
	public HashMap<Integer, HashSet<String>> getUserItemToUser(int userId,
			int groupId, int categoryId) {
		System.out.println("userId:" + userId + "\tgroupId:" + groupId
				+ "\tcategoryId:" + categoryId);
		return this.data.userGroupAll
				.get(groupId)
				.get(categoryId)
				.getUserItemToUser(userId, groupId, this.data, this.contentData);
	}

	/**
	 * 获取某组的用户 的所有物品对应的其他用户
	 * 
	 * @param userId
	 * @param groupId
	 * @param dataModel
	 * @param contentModel
	 * @return
	 */
	public void printUserItemToUser(int userId, int groupId, int categoryId) {
		HashMap<Integer, HashSet<String>> list = getUserItemToUser(userId,
				groupId, categoryId);
		// System.out.println("用户："+userId+"\tgroupId:"+groupId+"\tcategory:"+categoryId);
		for (Entry<Integer, HashSet<String>> li : list.entrySet()) {
			System.out.println("物品:" + li.getKey() + ":");
			for (String str : li.getValue()) {
				System.out.println("\t\t" + str);
			}
		}
	}

	/**
	 * 打印相似用户
	 * 
	 * @param userId
	 * @param groupId
	 */
	public void printUserToUserSim(int userId, int groupId,int groupId2, int categoryUser,
			float simiLimit, float comSimiLimit) {
		printUserToUserSim(userId, groupId,groupId2, categoryUser, this.cfCategory,
				simiLimit, comSimiLimit);
	}

	/**
	 * 打印用户和用户相似属性
	 * 
	 * @param userId
	 *            用户名
	 * @param groupId
	 *            组名
	 * @param categoryUser
	 *            用户使用的组
	 * @param categoryId
	 *            使用作为相似度的组
	 */
	public void printUserToUserSim(int userId, int groupId,int groupId2, int categoryUser,
			int category, float simiLimit, float comSimiLimit) {
		UserInfo categoryUserInfo = null;
		if (category > 0) {
			for (Entry<Integer, UserInfo> userInfo : data.userGroupAll.get(
					groupId).entrySet()) {
				if (userInfo.getKey() == category) {
					categoryUserInfo = userInfo.getValue();
					break;
				}
			}
		}
		UserInfo userInfo = this.data.userGroupAll.get(groupId).get(
				categoryUser);
		UserNode userNode = data.userGroupAll.get(groupId).get(categoryUser)
				.getUser(userId);
		NeighBean userList = null;
		if (categoryUserInfo == null) {
			// 如果给出的分类为空则表示使用自己的数据
			userList = this.neighborhoodFunc
					.getNearUserAll(groupId,groupId2, userNode, userInfo,
							this.contentData.feather, simiLimit, comSimiLimit);
		} else {
			UserNode tempUserNode = categoryUserInfo.getUser(userNode
					.getUserId());
			if (tempUserNode == null) {// 如果为空则表示 该用户不存在与分类中则使用自己的业态
				userList = this.neighborhoodFunc.getNearUserAll(groupId,groupId2,
						userNode, userInfo, this.contentData.feather,
						simiLimit, comSimiLimit);
			} else {
				userList = this.neighborhoodFunc.getNearUserAll(groupId,groupId2,
						tempUserNode, categoryUserInfo, category,
						this.contentData.feather, mallCategory, simiLimit,
						comSimiLimit);
			}
		}
		System.out.println("用户相似计算");
		System.out.println("用户:" + userId + "\t组:" + groupId + "\t用户分类:"
				+ categoryUser + "\t使用分类:" + category + "\t最小邻域值:" + simiLimit);
		System.out.println("使用属性:");
		if (categoryUserInfo == null) {
			System.out.println(userInfo.getUserString(userId, groupId,
					this.contentData.feather));
		} else {
			System.out.println(categoryUserInfo.getUserString(userId, groupId,
					this.contentData.feather));
		}
		if (userList == null) {
			System.out.println("无相似用户");
		}
		boolean flag = false;
		for (int i = 0; i < userList.usersId.length; i++) {
			if (userList.usersId[i] == 0) {
				break;
			}
			flag = true;
			if (categoryUserInfo == null) {
				System.out.println("simiValue:"
						+ userList.scores[i]
						+ "\t"
						+ userInfo.getUserString(userList.usersId[i], groupId,
								this.contentData.feather));
			} else {
				System.out.println("simiValue:"
						+ userList.scores[i]
						+ "\t"
						+ categoryUserInfo.getUserString(userList.usersId[i],
								groupId, this.contentData.feather));
			}
		}
		if (!flag) {
			System.out.println("无相似用户");
		}
	}

	public class UserVal implements Comparable<UserVal> {
		public int user = -1;
		public Float val = 0f;

		public UserVal(int user, Float val) {
			this.user = user;
			this.val = val;
		}

		public int compareTo(UserVal other) {
			return -Float.compare(val, other.val);
		}
	}

	/**
	 * 对groupId 对应的user做评估
	 * 
	 * @param groupId
	 *            组类别
	 * @param category
	 *            分类
	 * @param userId
	 *            使用的用户
	 * @param preSet
	 *            推荐的物品 不重复
	 * @param allSet
	 *            全部物品 用户的物品+测试的物品+推荐的物品
	 * @return
	 */
	public void evalue(int groupId, int category, int userId,
			HashSet<Integer> preSet, HashSet<Integer> allSet) {
		// System.out.println("groupId:" + groupId + "\tcategory:" + category
		// + "\tid:" + userId);
		// 一定有用户
		HashMap<Integer, UserInfo> userTrainGroup = data.userGroupAll
				.get(groupId);
		UserInfo userInfo = userTrainGroup.get(category);
		if (userInfo == null) {
			return;
		}
		UserNode userTrain = userInfo.getUser(userId);
		HashSet<Integer> cover = new HashSet<Integer>();
		int l = 0;
		// 获取推荐用户的所有推荐物品
		for (ItemNode item : userTrain.getSortItem()) {
			cover.add(item.getItemId());
			preSet.add(item.getItemId());
			allSet.add(item.getItemId());
			l++;
			if (l >= this.recommandItemCount) {
				break;
			}
		}
		for (ItemNode item : userTrain.getItems()) {
			allSet.add(item.getItemId());
			l++;
			if (l >= this.recommandItemCount) {
				break;
			}
		}
		HashMap<Integer, HashMap<Integer, UserInfo>> groupInfoTest = data.userGroupAllTest;
		if (groupInfoTest == null) {
			return;
		} else if (groupInfoTest.size() <= groupId) {
			return;
		}
		HashMap<Integer, UserInfo> testUserGroupInfo = groupInfoTest
				.get(groupId);
		if (testUserGroupInfo == null) {
			return;
			// 返回估计值
		}
		UserInfo testUserCategoryInfo = testUserGroupInfo.get(category);
		if (testUserCategoryInfo == null) {
			return;
		}
		UserNode userTest = testUserCategoryInfo.getUser(userId);
		if (userTest == null) {
			return;
		}
		for (ItemNode item : userTest.getItems()) {
			allSet.add(item.getItemId());
		}
		// 获取用户分类对应的数值
		EvoleBean bean = new EvoleBean();
		float pricision = getPrecision(userTest, userTrain, bean);
		float recall = getRecall(userTest, userTrain);
		float fValue = getFValue(pricision, recall);
		// 预测完成后需要踢除测试集的数据
		// 如果使用测试方式则最终的推荐结果需要移除推荐结果
		removeItem(userTrain, userTest);
		bean.fValue = fValue;
		bean.recallValue = recall;
		bean.count = userTrain.size();
		// 将对应结果放入最终的评估map中
		recommandMapEvole.put(EvoleBean.getEvoleKey(groupId, category, userId,
				evalueSplitRegex), bean);
	}

	/**
	 * evalue的分隔符
	 */
	public static String evalueSplitRegex = ":";

	/**
	 * 估计当前数据集的评估效果
	 * 
	 * @return
	 */
	public void evalue() {
		// int groupId = -1;
		// for (ArrayList<UserInfo> userList : data.userGroupAll) {
		// groupId++;
		// // 计算覆盖率
		// HashSet<Integer> cover = new HashSet<Integer>();
		// for (int i = 0; i < userList.size(); i++) {
		// // System.out.println("eval11:"+userGroup.get(i).size());
		// for (UserNode user : userList.get(i).getUsers()) {
		// int l = 0;
		// for (ItemNode item : user.getSortItem()) {
		// cover.add(item.getItemId());
		// l++;
		// if (l >= this.recommandItemCount) {
		// break;
		// }
		// }
		// }
		// }
		// double coverValue = cover.size() * 1d / itemCount;
		//
		// for (int i = 0; i < this.data.userGroupAllTest.get(groupId).size();
		// i++) {
		// UserInfo userInfo = data.userGroupAllTest.get(groupId).get(i);
		// int ii = -1;
		// for (int j = 0; j < data.userGroupAll.get(groupId).size(); j++) {
		// if (data.userGroupAllTest.get(groupId).get(i).category ==
		// data.userGroupAll
		// .get(groupId).get(j).category) {
		// ii = j;
		// }
		// }
		// if (ii == -1) {
		// continue;
		// }
		// HashSet<Integer> coverTemp = new HashSet<Integer>();
		// // 当前分类下所有商品数量
		// int itemAllCount = 0;
		// for (UserNode userTest : userInfo.getUsers()) {
		// for (ItemNode it : userTest.getItems()) {
		// coverTemp.add(it.getItemId());
		// }
		// }
		// itemAllCount = coverTemp.size();
		// for (UserNode userTrain : data.userGroupAll.get(groupId)
		// .get(ii).getUsers()) {
		// for (ItemNode it : userTrain.getItems()) {
		// coverTemp.add(it.getItemId());
		// }
		// }
		// HashSet<Integer> coverTemp2 = new HashSet<Integer>();
		// for (UserNode userTrain : data.userGroupAll.get(groupId)
		// .get(ii).getUsers()) {
		// int ll = 0;
		// for (ItemNode it : userTrain.getSortItem()) {
		// ll++;
		// coverTemp2.add(it.getItemId());
		// if (ll >= this.recommandItemCount) {
		// break;
		// }
		// }
		// }
		// double pricesionSum = 0d;
		// double recallSum = 0d;
		// for (UserNode userTest : userInfo.getUsers()) {
		// UserNode userTrain = data.userGroupAll.get(groupId).get(ii)
		// .getUserInfo().get(userTest.getUserId());
		// // 获取用户分类对应的数值
		// EvoleBean bean = new EvoleBean();
		// double pricision = getPrecision(userTest, userTrain, bean);
		// double recall = getRecall(userTest, userTrain);
		// double fValue = getFValue(pricision, recall);
		// if (pricision < 0 || recall < 0) {
		// continue;
		// }
		// // 预测完成后需要踢除测试集的数据
		// removeItem(userTrain, userTest);
		// pricesionSum += pricision;
		// recallSum += recall;
		// // 将对应结果放入最终的评估map中
		// recommandMapEvole.put(userTrain.getUserId(), bean);
		// // System.out.println("分类:"+userInfo.getCatgory()+" 查准率:"+
		// //
		// s(pricision,6,true)+" 查全率:"+s(recall,6,true)+" fValue:"+s(recall,4,true)+s(this.limitItem,4,true)+"\t"+" testCount:"+s(userTest.size(),4,true));
		// //
		// System.out.println("分类:"+userInfo.getCatgory()+" "+s(category.get(userInfo.getCatgory()),8,true)+":用户:"+s(userTest.getUserId(),5,true)+s(mall.get(userTest.getUserId()),20,true)+" 查准率:"+
		// //
		// s(pricision,6,true)+" 查全率:"+s(recall,6,true)+" fValue:"+s(recall,4,true)+" testCount:"+s(userTest.size(),4,true)+"trainCount:"+s(userTrain.size(),4,true));
		// }
		// pricesionSum /= userInfo.size() == 0 ? 1 : userInfo.size();
		// recallSum /= userInfo.size() == 0 ? 1 : userInfo.size();
		// double coverValueTemp = coverTemp2.size() * 1d
		// / coverTemp.size();
		// //
		// System.out.println("分类:"+userInfo.getCatgory()+" "+s(category.get(userInfo.getCatgory()),8,true)+" 分类覆盖率:"+s(coverValueTemp,5,true)+
		// // "平均查准率:"+s(pricesionSum,5,true)+"平均查全率:"+s(recallSum,5,true));
		// System.out.println(s(coverValueTemp, 5, true)
		// + s(pricesionSum, 5, true) + s(recallSum, 5, true)
		// + s(userInfo.size(), 5, true));
		// outputAddData(pricesionSum, userInfo.size());// itemAllCount);//为所有物品
		// }
		// System.out.println("覆盖率：" + coverValue);
		// System.out.println("正確率：" + outputGetValue());
		// System.out.println("推荐结果写入文件");
		// }
	}

	/**
	 * 从训练集和的最终结果中踢除掉训练集和的数据
	 * 
	 * @param train
	 * @param test
	 */
	public void removeItem(UserNode train, UserNode test) {
		for (ItemNode item : test.getItemsLinked()) {
			train.removeSortItem(item.getItemId());
		}
	}

	/**
	 * 將最終的存儲到數據中
	 * 
	 * @param value
	 * @param count
	 */
	public void outputAddData(double value, int count) {
		outputPredict.add((float) value);
		outputCount.add(count);
	}

	/**
	 * 獲取全劇正確率
	 * 
	 * @return
	 */
	public float outputGetValue() {
		if (outputPredict.size() == 0) {
			System.out.println("數據輸入異常");
			return 0f;
		} else {
			float value = 0f;
			float count = 0;
			for (int i = 0; i < outputPredict.size(); i++) {
				count += outputCount.get(i);
			}
			for (int i = 0; i < outputPredict.size(); i++) {
				// System.out.println(outputPredict.get(i)+"\t"+outputCount.get(i));
				value += outputPredict.get(i) * outputCount.get(i) / count;
			}
			return (float) value;
		}
	}

	public String stLN = "                                  ";

	public String s(String str, int len, boolean flag) {
		if (flag) {
			String temp = str;
			if (temp.length() > len) {
				return temp.substring(0, len) + "\t";
			} else {
				return temp + stLN.substring(0, len - temp.length() + 1) + "\t";
			}
		} else {
			String temp = str;
			if (temp.length() > len) {
				return temp.substring(0, len);
			} else {
				return stLN.substring(0, len - temp.length()) + temp;
			}
		}
	}

	public String s(long s, int len, boolean flag) {
		return s(Long.toString(s), len, flag);
	}

	public String s(float s, int len, boolean flag) {
		return s(Float.toString(s), len, flag);
	}

	public String s(int s, int len, boolean flag) {
		return s(Integer.toString(s), len, flag);
	}

	public String s(double s, int len, boolean flag) {
		return s(Double.toString(s), len, flag);
	}

	/**
	 * 查准率
	 * 
	 * @param userTest
	 *            测试集
	 * @param userTrain
	 *            训练集
	 * @param evoleBean
	 *            评估值
	 * @return 如果 <0 则表示 数据集中存在一个不存在的信息
	 * 
	 */
	public float getPrecision(UserNode userTest, UserNode userTrain,
			EvoleBean evoleBean) {
		if (userTest.size() == 0 || userTrain == null
				|| userTrain.getSortItem().size() == 0) {
			return -1;
		}
		int i = 0;
		int okCount = 0;
		for (ItemNode train : userTrain.getSortItem()) {
			i++;
			for (ItemNode test : userTest.getItems()) {
				if (train.getItemId() == test.getItemId()) {
					okCount++;
				}
			}
			if (i == this.recommandItemCount) {
				break;
			}
			// if (i == userTest.size()) {
			// break;
			// }
		}
		// 标准统计
		// return okCount*1.0/(limitItem);
		// 统计方法2
		// int size = (i > userTest.size() ? userTest.size() : i);
		int size = this.recommandItemCount;
		// System.out.println("size:"+size);
		float preValue = (float) (okCount * 1.0 / size);
		evoleBean.preValue = preValue;
		evoleBean.reCount = userTrain.sortItem.size();
		evoleBean.testCount = userTest.size();
		return preValue;

	}

	/**
	 * 查全率
	 * 
	 * @param userTest
	 *            测试集
	 * @param userTrain
	 *            训练集
	 * @return 如果 <0 则表示 数据集中存在一个不存在的信息
	 */
	public float getRecall(UserNode userTest, UserNode userTrain) {
		if (userTest.size() == 0 || userTrain == null
				|| userTrain.getSortItem().size() == 0) {
			return -1;
		}
		int i = 0;
		int okCount = 0;
		// 打印 mall 对应的推荐正确的 shop
		// System.out.println("mall:"+mall.get(userTest.getUserId())+"\t正确shop");
		for (ItemNode train : userTrain.getSortItem()) {
			i++;
			for (ItemNode test : userTest.getItems()) {
				if (train.getItemId() == test.getItemId()) {
					// System.out.print(shop.get(train.getItemId())+"\t");
					okCount++;
				}
			}
			// if (i == this.recommandItemCount) {
			// break;
			// }
			if (i == userTest.size()) {
				break;
			}
		}
		// if(i>0)
		// System.out.println();
		// System.out.println(okCount+"\t"+i+"\t"+userTest.size());
		i = i > userTrain.size() ? userTrain.size() : i;
		return okCount * 1f / i;
	}

	/**
	 * 计算F值
	 * 
	 * @param precision
	 * @param recall
	 * @return
	 */
	public float getFValue(float precision, float recall) {
		return precision * recall * 2 / (precision + recall);
	}

	/**
	 * F得分 和计算F值类似 当a=1f值时为 f值
	 * 
	 * @return
	 */
	public double getFMeasure(double precision, double recall, float a) {
		// float a=1f;
		return (Math.pow(a, 2f) + 1) * precision * recall
				/ (precision * recall);
	}

	/**
	 * 计算查准率P和查全率R的加权平均值 当其中一个为0时，E值为1 b越大，表示查准率的权重越大
	 * 
	 * @param pricision
	 * @param recall
	 * @param b
	 * @return
	 */
	public double getE(double pricision, double recall, float b) {
		return 1 - (1 + Math.pow(b, 2f))
				/ (Math.pow(b, 2f) / pricision + 1 / recall);
	}

	/**
	 * 读取权重信息表
	 * 
	 * @param file
	 * @param code
	 * @return
	 */
	public static HashMap<Long, float[]> readFileWeight(String file, String code) {
		HashMap<Long, float[]> weight = new HashMap<Long, float[]>();
		FileUtil2 fileUtil = new FileUtil2(file, code, false);
		LinkedList<String> strs = fileUtil.readAndClose();
		for (String str : strs) {
			String[] temp = str.split("\t");
			float[] fTemp = new float[temp.length - 1];
			for (int i = 1; i < temp.length; i++) {
				fTemp[i] = Float.parseFloat(temp[i]);
			}
			weight.put(Long.parseLong(temp[0]), fTemp);
		}
		return weight;
	}

	public static void main(String[] args) {

	}

}
