package com.fc.recommend.similary2;



/**
 * jarcard 相关系数
 * 股本相似度
 * @author Administrator
 *
 */
public class JarcardSimilary implements SimilaryUtil2{

	/**
	 * 不支持
	 */
	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
			
		return 0;
	}
	
	public float getSimilary(int[] userNode1, int[] userNode2,
			float[] weight) {
		float up=0f;
		float down=0f;
		if(userNode1.length==0)
		{
			return 0;
		}
		if(weight==null)
		{
			down=userNode1.length;
			for(int i=0;i<userNode2.length;i++)
			{
				if(userNode1[i]==userNode2[i])
				{
					up++;
				}
			}
		}else{
			down=0f;
			for(int i=0;i<userNode2.length;i++)
			{
				if(userNode1[i]==userNode2[i])
				{
					up+=weight[i];
				}
				down+=weight[i];
			}
		}
		return up/down;
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
