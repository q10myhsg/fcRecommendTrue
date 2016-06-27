package com.fc.recommend.similary2;

/**
 * 斯皮尔曼相关系数
 * @author Administrator
 *
 */
public class SpearmanSimilary{
	
//	public void getRank(float[] item,int[] rank)
//	{
//		int i=0;
//		for(float item1:item)
//		{
//			int count1=1;//记录大于特定元素的元素个数
//			int count2=-1;//记录与特定元素相同的元素个数  
//			for(float item2:item)
//			{
//				if(item1<item2)
//				{
//					count1++;
//				}else if(item1==item2)
//				{
//					count2++;
//				}
//			}
//			rank[i]=count1+getMean(count2);
//			i++;	
//		}
//	}
//
//	public int getMean(int count2)
//	{
//		int val=0;
//		for(int i=0;i<=count2;i++)
//		{
//			val+=i;
//		}
//		return val/(count2+1);
//	}
//
//	@Override
//	public float getSimilary(float[] userNode1, float[] userNode2,
//			float[] weight) {
//		// TODO Auto-generated method stub
//		float[] item1=userNode1.clone();
//		float[] item2=userNode1.clone();
//		AllSort.mergerSort(item1, 0,item1.length-1,item1.length,true);
//		AllSort.mergerSort(item2, 0,item2.length-1,item2.length,true);
//		int[] xRank=new int[item1.length];
//		getRank(item1,xRank);
//		int[] yRank=new int[item2.length];
//		getRank(item2,yRank);
//		//利用差分等级(或排行)序列计算斯皮尔曼等级相关系数
//		float fenzi=0f;
//		for(int j=0;j<xRank.length;j++)
//		{
//			fenzi+=Math.pow(xRank[j]-yRank[j],2f);
//		}
//		fenzi*=6;
//		float fenmu=xRank.length*(xRank.length*xRank.length-1);
//		return 1-fenzi/fenmu;
//	}
//
//	@Override
//	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public float getSimilary(String s1, String s2) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	
	
}
