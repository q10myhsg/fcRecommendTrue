package com.fc.recommend.similary2;

import com.math.MathBase;


/**
 * fenzi = sum(X .* Y) - (sum(X) * sum(Y)) / length(X);  
 * fenmu = sqrt((sum(X .^2) - sum(X)^2 / length(X)) * (sum(Y .^2) - sum(Y)^2 / length(X)));  
 * coeff = fenzi / fenmu;  
 * pearson 相关系数
 * @author Administrator
 *
 */
public class PearsonSimilary implements SimilaryUtil2{
	/**
	 * 不定长处理方法
	 * @param xy_sum
	 * @param x_sum
	 * @param y_sum
	 * @param size1
	 * @param size2
	 * @param same_len
	 * @param x_2_sum
	 * @param y_2_sum
	 * @return
	 */
	public float compute(float xy_sum,float x_sum,float y_sum,int size1,int size2,int same_len,float x_2_sum,float y_2_sum)
	{
		float nzi=xy_sum-(x_sum+y_sum)/(size1+size2-same_len);
		//System.out.println(x_2_sum+"\t"+Math.pow(x_sum,2f)/item1.size());
		//System.out.println(y_2_sum+"\t"+Math.pow(y_sum,2f)/item2.size());
		float fenmu=(float) Math.sqrt((x_2_sum-Math.pow(x_sum,2f)/size1)*(y_2_sum-Math.pow(y_sum,2f)/size2));
		return nzi/fenmu;
	}

	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
		float xy_sum=0f;
		float x_sum=0f;
		float x_2_sum=0f;
		float y_sum=0f;
		float y_2_sum=0f;
		int same_len=0;
		for(int i=0;i<userNode1.length;i++)
		{
			xy_sum+=userNode1[i]*userNode2[i];
			x_sum+=userNode1[i];
			x_2_sum+=MathBase.pow(userNode1[i],2);
			y_sum+=userNode2[i];
			y_2_sum+=MathBase.pow(userNode2[i],2);
		}
		return compute(xy_sum,x_sum,y_sum,userNode1.length,userNode2.length,same_len,x_2_sum,y_2_sum);
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
