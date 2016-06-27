package com.fc.recommend.filter;

import java.util.Iterator;
import java.util.LinkedList;

import com.fc.recommend.cf.Recommend;
import com.fc.recommend.dataBean.ItemNode;
import com.fc.recommend.dataBean.UserNode;

/**
 * 将热门商品过滤
 * 以数量过滤
 * @author Administrator
 *
 */
public class RecommenderHostItemCountFilter implements RecommenderFilterUtil {

	public int recommanderFilterParam=1;
	public int count=0;
	/**
	 * 
	 * @param weightUitl 物品统计表
	 * @param 推荐类型 ParamStatic中> < >= <=
	 * @param count 为 数量
	 * @param count
	 */
	public RecommenderHostItemCountFilter(Recommend recommand,int recommanderFilterParam,int count)
	{
		this.recommanderFilterParam=recommanderFilterParam;
		this.count=count;
	}
	
	/**
	 * 
	 * @param weightUitl 物品统计表
	 * @param 推荐类型 ParamStatic中> < >= <=
	 * @param count 为 数量
	 * @param count
	 */
	public RecommenderHostItemCountFilter(int recommanderFilterParam,int count)
	{
		this.recommanderFilterParam=recommanderFilterParam;
		this.count=count;
	}


	@Override
	public void filter(UserNode userNode) {
		// TODO Auto-generated method stub
		//如果>
		if(recommanderFilterParam==CFParamStatic.recommanderHotGtFilter)
		{
			Iterator<ItemNode> iterator = userNode.sortItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode item= iterator.next();
				if((int)item.getCount()>this.count&&(int)item.getCount()>0)
				{
					iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}else if(recommanderFilterParam==CFParamStatic.recommanderHotltFilter)
		{//如果<
			Iterator<ItemNode> iterator = userNode.sortItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode item= iterator.next();
				if((int)item.getCount()<this.count&&(int)item.getCount()>0)
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
		    	 ItemNode item= iterator.next();
				if((int)item.getCount()>=this.count&&(int)item.getCount()>0)
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
		    	 ItemNode item= iterator.next();
				if((int)item.getCount()<=this.count&&(int)item.getCount()>0)
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
				if((int)itemNode.getCount()>this.count)
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
				if((int)itemNode.getCount()<this.count)
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
				if((int)itemNode.getCount()>=this.count)
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
				if((int)itemNode.getCount()<=this.count)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		
	}


}
