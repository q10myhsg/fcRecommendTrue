package com.fc.recommend.similary2;


/**
 * 汉明距离
 * 
 * 如果在字符串中则为编辑距离
 * 0 1 
 * 0 0
 * 不同分量得占比 0.5
 * @author Administrator
 *
 */
public class HammingDistanceSimilary implements SimilaryUtil2{

	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float count=0;
		float length=userNode1.length;
		if(weight==null)
		{
			for(int i=0;i<userNode1.length;i++)
			{
				if(userNode1[i]!=userNode2[i])
				{
					count++;
				}
			}
		}else{
			length=0f;
			for(int i=0;i<userNode1.length;i++)
			{
				length+=weight[i];
				if(userNode1[i]!=userNode2[i])
				{
					count+=weight[i];
				}
			}
			if(length<1E-10)
			{
				return 0f;
			}
		}
		return count/length;
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
