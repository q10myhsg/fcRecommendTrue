package com.fc.recommend.similary2;


/**
 * sum(|xi-yi|) pearson 曼哈顿距离
 * 
 * @author Administrator
 *
 */
public class CityBlockSimilary implements SimilaryUtil2 {

	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		float result = 0f;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				result += Math.abs(userNode1[i] - userNode2[i]);
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				result += Math.abs(userNode1[i] - userNode2[i])*weight[i];
			}
		}
		return result;
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float result = 0f;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				result += Math.abs(userNode1[i] - userNode2[i]);
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				result += Math.abs(userNode1[i] - userNode2[i])*weight[i];
			}
		}
		return result;
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
