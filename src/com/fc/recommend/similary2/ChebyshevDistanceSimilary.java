package com.fc.recommend.similary2;


/**
 * 切比雪夫距离
 * max(|xi-yi|)
 * @author Administrator
 *
 */
public class ChebyshevDistanceSimilary implements SimilaryUtil2{

	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
		float val = Float.MIN_VALUE;
		float valNew=Float.MAX_VALUE;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				valNew += Math.abs(userNode1[i] - userNode2[i]);
				if(val<valNew)
				{
					val=valNew;
				}
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				valNew += Math.abs(userNode1[i] - userNode2[i])*weight[i];;
				if(val<valNew)
				{
					val=valNew;
				}
			}
		}
		return val;
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float val = Float.MIN_VALUE;
		float valNew=Float.MAX_VALUE;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				valNew += Math.abs(userNode1[i] - userNode2[i]);
				if(val<valNew)
				{
					val=valNew;
				}
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				valNew += Math.abs(userNode1[i] - userNode2[i])*weight[i];;
				if(val<valNew)
				{
					val=valNew;
				}
			}
		}
		return val;
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
