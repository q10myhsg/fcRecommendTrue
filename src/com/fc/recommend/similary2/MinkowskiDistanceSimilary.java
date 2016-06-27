package com.fc.recommend.similary2;

import com.math.MathBase;


/**
 * 闵可夫斯基距离
 * @author Administrator
 *sum((x-y)^k)^(1/k)
 */
public class MinkowskiDistanceSimilary implements SimilaryUtil2{

	public int kValue=2;
	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
		float val = 0f;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				val += MathBase.pow(userNode1[i] - userNode2[i], kValue);
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				val += MathBase.pow(userNode1[i] - userNode2[i], 2)*weight[i];
			}
		}
		return (float) Math.pow(val,1.0/kValue);
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float val = 0f;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				val += MathBase.pow(userNode1[i] - userNode2[i], kValue);
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				val += MathBase.pow(userNode1[i] - userNode2[i], 2)*weight[i];
			}
		}
		return (float) Math.pow(val,1.0/kValue);
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
