package com.fc.recommend.similary2;


/**
 * 马氏距离
 * sqrt((x-y)s-1(x-y))
 * @author Administrator
 *
 */
public class MahalanobisDistanceSimilary {

//	@Override
//	public float getSimilary(float[] userNode1, float[] userNode2,
//			float[] weight) {
//		// TODO Auto-generated method stub
//		float[][] fval=new float[userNode1.length][2];
//		for(int i=0;i<userNode1.length;i++)
//		{
//			fval[i][0]=userNode1[i];
//			fval[i][1]=userNode2[i];
//		}
//		MatrixBase matrix=new MatrixBase(fval);
//		//获取协方差得逆
//		float[] mean=matrix.getMeans(false);
//		MatrixBase cov=matrix.getCovMatrix(mean).getInversionLU();
//		float covVal=cov.data[0][1];
//		float result=0f;
//		//计算
//		for(int i=0;i<userNode1.length;i++)
//		{
//			result+=(userNode1[i]-userNode2[0])*covVal*(userNode1[i]-userNode2[1]);
//		}
//		return MathBase.sqrt(result);
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

}
