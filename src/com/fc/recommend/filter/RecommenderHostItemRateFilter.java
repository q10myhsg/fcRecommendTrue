package com.fc.recommend.filter;

import java.util.Iterator;
import java.util.LinkedList;

import com.fc.recommend.cf.Recommend;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserNode;
import com.fc.recommend.filterWeight.WeightUtil;

/**
 * 将热门商品过滤
 * 以比率过滤
 * @author Administrator
 *
 */
public class RecommenderHostItemRateFilter implements RecommenderFilterUtil {

	public WeightUtil weightUtil=null;
	public int recommanderFilterParam=1;
	public float rate=0;
	/**
	 * 
	 * @param weightUitl 物品统计表
	 * @param 推荐类型 ParamStatic中> < >= <=
	 * @param count 为 数量
	 * @param count
	 */
	public RecommenderHostItemRateFilter(Recommend recommand,int recommanderFilterParam,float rate)
	{
		this.weightUtil=recommand.weight;
		this.recommanderFilterParam=recommanderFilterParam;
		this.rate=rate;
	}
	@Override
	public void filter(UserNode userNode) {
		// TODO Auto-generated method stub
		//如果>
		if(recommanderFilterParam==CFParamStatic.recommanderHotGtFilter)
		{
			Iterator<ItemNode> iterator = userNode.sortItem.iterator();
		     while(iterator.hasNext()) {  
		    	 ItemNode item = iterator.next();
		    	 if((int)item.getCount()/weightUtil.size()>this.rate)
				{
		    		 iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}else if(recommanderFilterParam==CFParamStatic.recommanderHotltFilter)
		{//如果<
			Iterator<ItemNode> iterator = userNode.sortItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode item = iterator.next();
		    	 if((int)item.getCount()/weightUtil.size()<this.rate)
				{
		    		 iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotGteFilter)
		{//如果>=
			Iterator<ItemNode> iterator = userNode.sortItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode item = iterator.next();
		    	 if((int)item.getCount()/weightUtil.size()>=this.rate)
				{
		    		 iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotlteFilter)
		{//如果<=
			Iterator<ItemNode> iterator = userNode.sortItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode item = iterator.next();
		    	 if((int)item.getCount()/weightUtil.size()<=this.rate)
				{
		    		 iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}
	}
	
	@Override
	public void filter(LinkedList<ItemNode> userItem,LinkedList<ItemNode> filterItem) {
		//如果>
		if(recommanderFilterParam==CFParamStatic.recommanderHotGtFilter)
		{
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();  
				if((int)itemNode.getCount()/weightUtil.size()>this.rate)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}else if(recommanderFilterParam==CFParamStatic.recommanderHotltFilter)
		{//如果<
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();  
				if((int)itemNode.getCount()/weightUtil.size()<this.rate)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotGteFilter)
		{//如果>=
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();
		    	// System.out.println(itemNode.getValue()+"\t"+itemNode.getCount()+"\t"+weightUitl.size()+"\t"+this.rate);
				if((int)itemNode.getCount()/weightUtil.size()>=this.rate)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotlteFilter)
		{//如果<=
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();  
				if((int)itemNode.getCount()/weightUtil.size()<=this.rate)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		
	}

}
