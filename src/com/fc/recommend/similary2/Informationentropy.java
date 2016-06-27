package com.fc.recommend.similary2;


/**
 * 信息熵
 * sum(-p*log2(p))
 * @author Administrator
 *
 */
public class Informationentropy implements SimilaryUtil2{

	/**
	 * 输入为userNode1 为信息上得概率值
	 */
	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
		float entry=0f;
		for(int i=0;i<userNode1.length;i++)
		{
			entry+=userNode1[i]*Math.log(userNode1[i])/Math.log(2);
		}
		return -entry;
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
