package com.fc.recommend.reDataProcession;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.config.ConfigStatic;
import com.db.CursorInter;
import com.fc.commend.graphLinked.GeneralSortBean;
import com.fc.recommend.dataBean.StreamBean;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.dataOut.dataBean.CategoryBean;
import com.fc.recommend.dataOut.dataBean.CategoryItemBean;
import com.fc.recommend.dataOut.dataBean.CategoryMallBean;
import com.fc.recommend.dataOut.dataBean.ItemRecommendBean;
import com.fc.recommend.dataOut.dataBean.MallRecommendBean;
import com.fc.recommend.dataOut.dataBean.RecommendBean;
import com.fc.recommend.dataOut.dataBean.SonBean;
import com.fc.recommend.dataOut.dataBean.SonSimBean;
import com.fc.recommend.filter.RecommenderFilterUtil;
import com.util.JsonUtil;

/**
 * 推荐过滤填充程序
 * 
 * @author Administrator
 *
 */
public class RecommendDataProcession implements Runnable {

	/**
	 * 队列容器
	 */
	public ConcurrentLinkedQueue<StreamBean> queueData = new ConcurrentLinkedQueue<StreamBean>();

	/**
	 * 输出容器
	 */
	public ConcurrentLinkedQueue<RecommendBean> outputStream = new ConcurrentLinkedQueue<RecommendBean>();
	/**
	 * 是否为mall
	 */
	public boolean isMall = true;
	/**
	 * 
	 */
	public LinkedList<RecommenderFilterUtil> filter = new LinkedList<RecommenderFilterUtil>();

	public CursorInter cursor = null;
	/**
	 * 队列超时时间
	 */
	public int timeOut = 3000;
	/**
	 * 是否为最终的写成json的程序
	 */
	public boolean isOutPutStream = false;
	/**
	 * 推荐程序是否结束
	 */
	public boolean recommandIsStop = false;
	/**
	 * 输出流
	 */
	public RecommendDataProcession outPutStream = null;
	/**
	 * userId_cityCode
	 */
	public HashMap<String, RecommendBean> recommandBeanTemp = null;
	/**
	 * 对应的id映射的名字
	 */
	public HashMap<Integer, HashMap<Integer, String>> map = null;
	/**
	 * 推荐填补程序
	 */
	public FillMethodDataProcession fillMethodDatatProcession = null;
	/**
	 * 是否填补
	 */
	public boolean isFill=true;
	/**
	 * 父节点关联的结束状态 true为执行中
	 */
	public boolean fatherStatus = true;

	public RecommendDataProcession() {
	}

	/**
	 * 输入流
	 * 
	 * @param outPutStream
	 */
	public RecommendDataProcession(RecommendDataProcession outPutStream) {
		this.outPutStream = outPutStream;
	}

	/**
	 * 输出流
	 * 
	 * @param cursor
	 */
	public RecommendDataProcession(CursorInter cursor,
			HashMap<Integer, HashMap<Integer, String>> map, boolean isMall,
			FillMethodDataProcession fillMethodDatatProcession) {
		this.cursor = cursor;
		this.isOutPutStream = true;
		this.map = map;
		this.isMall = isMall;

		this.fillMethodDatatProcession = fillMethodDatatProcession;
	}

	@Override
	public void run() {
		// 是否启动
		boolean isStart = false;
		// 父节点是否结束
		boolean flag = false;
		// 存在数据
		boolean fl = false;
		while (true) {
			if (isOutPutStream) {
//				 System.out.println(Thread.currentThread().getName() + ":输出流："
//				 + this.outputStream.size());
				RecommendBean recommandBean = null;
				fl = false;
				flag = false;
				if (!fatherStatus) {// 如果父节点停止了
					recommandBean = this.outputStream.poll();
					if (recommandBean == null) {
						// 表示父节点结束
						fl = false;
						flag = true;
						isStart = true;
					} else {
						fl = true;
						flag = false;
						isStart = true;
						processDB(recommandBean);
						continue;
					}
				} else {
					recommandBean = this.outputStream.poll();
					if (recommandBean == null) {
						fl = false;
					} else {
						fl = true;
						flag = false;
						isStart = true;
						processDB(recommandBean);
						continue;
					}
				}
			} else {
//				 System.out.println(Thread.currentThread().getName() + ":处理流："
//				 + this.queueData.size());
				 StreamBean streamBean=null;
				 if (!fatherStatus) {// 如果父节点停止了
					 streamBean = queueData.poll();
						if (streamBean == null) {
							// 表示父节点结束
							fl = false;
							flag = true;
							isStart = true;
						} else {
							fl = true;
							flag = false;
							isStart = true;
							process(streamBean);
							continue;
						}
					} else {
						 streamBean = queueData.poll();
						if (streamBean == null) {
							fl = false;
						} else {
							fl = true;
							flag = false;
							isStart = true;
							process(streamBean);
							continue;
						}
					}
			}
			// System.out.println(Thread.currentThread().getName() + "\t" + fl
			// + "\t" + flag + "\t" + isStart);
			if (fl == false && flag == true && isStart == true) {
				if (isOutPutStream) {
					if (recommandBeanTemp == null) {
						break;
					} else {
						for (Entry<String, RecommendBean> m : recommandBeanTemp
								.entrySet()) {
							this.outputStream.add(m.getValue());
						}
						recommandBeanTemp = null;
						flag = true;
						continue;
					}
				} else {
					StreamBean streamBean = queueData.poll();
					if (streamBean == null) {
					} else {
						process(streamBean);
					}
				}
				break;
			}
			if (!fl) {
				try {
					Thread.sleep(timeOut);
				} catch (Exception e) {
				}
			}
		}
		if (isOutPutStream) {
			System.out.println("输出流结束");
			System.exit(1);
		} else {
			outPutStream.fatherStatus = false;
			System.out.println("处理流结束");
		}
	}

	/**
	 * 执行过程
	 * 
	 * @return 返回为是否执行成功
	 */
	public void process(StreamBean streamBean) {
		for (RecommenderFilterUtil filterNode : filter) {
			filterNode.filter(streamBean.userNode);
		}
		processOther(streamBean);
		if (this.outPutStream == null) {
			// System.out.println("无输出流");
		} else {
			this.outPutStream.addRecommand(streamBean);
		}
	}

	public void processOther(StreamBean streamBean) {
		// 其他填充程序

	}

	public void processDB(RecommendBean recommandBean) {
		if(isFill)
		{
		fillMethodDatatProcession.run(this.itemLimit, this.simiLimit,
				recommandBean);
		}
		String str = null;
		// System.out.println(recommandBean.similary.size());
		if (isMall) {
			MallRecommendBean mall = new MallRecommendBean();
			HashMap<Integer, String> mallMap = map.get(ConfigStatic.MALL);
			HashMap<Integer, String> brandMap = map.get(ConfigStatic.BRAND);
			mall.mallId = recommandBean.id;
			mall.cityId = recommandBean.cityId;
			mall.cityName = map.get(ConfigStatic.CITY).get(mall.cityId);
			mall.mallName = mallMap.get(recommandBean.id);
			mall.similaryMall = recommandBean.similary;
			for (CategoryBean category : recommandBean.category) {
				LinkedList<SonBean> itemList = category.item;
				CategoryItemBean cate = new CategoryItemBean();
				mall.addCategoryItem(cate);
				cate.category = category.category;
				cate.categoryName = map.get(ConfigStatic.CATE).get(
						category.category);
				cate.recommandShop = itemList;
				for (SonBean item : itemList) {
					if (item.name.length() > 0) {

					} else {
						item.name = brandMap.get(item.id);
					}
				}
			}
			for (SonSimBean simBean : mall.similaryMall) {
				simBean.name = mallMap.get(simBean.getSimId());
			}
			if (mall.similaryMall.size() <= 0 && mall.categoryItem.size() <= 0) {
				return;
			}
			str = JsonUtil.getJsonStr(mall);
		} else {
			ItemRecommendBean brand = new ItemRecommendBean();
			HashMap<Integer, String> mallMap = map.get(ConfigStatic.MALL);
			HashMap<Integer, String> brandMap = map.get(ConfigStatic.BRAND);
			brand.brandId = recommandBean.id;
			brand.cityId = recommandBean.cityId;
			brand.cityName = map.get(ConfigStatic.CITY).get(brand.cityId);
			brand.brandName = brandMap.get(recommandBean.id);
			brand.similaryBrand = recommandBean.similary;
			for (CategoryBean category : recommandBean.category) {
				LinkedList<SonBean> itemList = category.item;
				CategoryMallBean cate = new CategoryMallBean();
				brand.addCategoryMall(cate);
				cate.category = category.category;
				cate.categoryName = map.get(ConfigStatic.CATE).get(
						category.category);
				cate.recommandMall = itemList;
				for (SonBean item : itemList) {
					item.name = mallMap.get(item.id);
				}
			}
			for (SonSimBean simBean : brand.similaryBrand) {
				simBean.name = brandMap.get(simBean.getSimId());
			}
			if (brand.similaryBrand.size() <= 0
					&& brand.categoryMall.size() <= 0) {
				return;
			}
			str = JsonUtil.getJsonStr(brand);
		}
		cursor.insertString(str);
	}

	public int itemLimit = 10;

	public int simiLimit = 10;

	/**
	 * 添加数据
	 * 
	 * @param userNode
	 */
	public void add(int itemLimit, int simiLimit, StreamBean simBean) {
		// System.out.println("输入流:" + simBean.userNode.userId + "\t"
		// + this.queueData.size());
		this.itemLimit = itemLimit;
		this.simiLimit = simiLimit;
		// System.out.println("simiSize:"+simBean.getSimiBean().size());
		this.queueData.add(simBean);

	}

	/**
	 * 城市是否切换
	 */
	public int groupId = -1;

	/**
	 * 添加推荐
	 * 
	 * @param cityId
	 *            使用的cityid
	 * @param 推荐的物品数量
	 * @param recommandBean
	 */
	public void addRecommand(StreamBean userStream) {
		// System.out.println("添加输出流:" + userStream.groupId + "\t:"
		// + userStream.userNode.userId + "\t"
		// + (recommandBeanTemp == null ? 0 : recommandBeanTemp.size()));
		CategoryBean categoryBean = new CategoryBean();
		categoryBean.category = userStream.userNode.categoryId;
		categoryBean.item = userStream.userNode.getSortItemsEnd(itemLimit);
		// System.out.println(userStream.simiBean.size());
		// System.out.println("groupId:"+userStream.groupId);
		if (recommandBeanTemp == null) {
			this.groupId = userStream.groupId;
			recommandBeanTemp = new HashMap<String, RecommendBean>();
			RecommendBean temp = new RecommendBean();
			temp.cityId = userStream.groupId2;
			temp.id = userStream.userNode.userId;
			temp.addCategory(categoryBean);
			temp.similary = userStream.simiBean;
			recommandBeanTemp.put(userStream.userNode.userId + "_"
					+ userStream.groupId2, temp);
		} else if (this.groupId == userStream.groupId) {
			// 如果城市没有切换
			RecommendBean temp = recommandBeanTemp
					.get(userStream.userNode.userId + "_" + userStream.groupId2);
			if (temp == null) {
				temp = new RecommendBean();
				temp.cityId = userStream.groupId2;
				temp.id = userStream.userNode.userId;
				temp.addCategory(categoryBean);
				if (temp.similary == null || temp.similary.size() == 0) {
					temp.similary = userStream.simiBean;
				}
				recommandBeanTemp.put(userStream.userNode.userId + "_"
						+ userStream.groupId2, temp);
			} else {
				temp.addCategory(categoryBean);
			}
		} else {
			// 为新的
			for (Entry<String, RecommendBean> m : recommandBeanTemp.entrySet()) {
				this.outputStream.add(m.getValue());
			}
			recommandBeanTemp = new HashMap<String, RecommendBean>();
			this.groupId = userStream.groupId;
			RecommendBean temp = new RecommendBean();
			temp.cityId = userStream.groupId2;
			temp.id = userStream.userNode.userId;
			temp.addCategory(categoryBean);
			if (temp.similary == null || temp.similary.size() == 0) {
				temp.similary = userStream.simiBean;
			}
			recommandBeanTemp.put(userStream.userNode.userId + "_"
					+ userStream.groupId2, temp);
		}
	}
}
