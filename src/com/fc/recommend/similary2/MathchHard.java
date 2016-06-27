package com.fc.recommend.similary2;


public class MathchHard implements SimilaryUtil2{
	/**
	 * 返回的为match的概率比
	 * a/all
	 */
	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		float fval=0f;
		if(weight==null)
		{
			for(int i=0;i<userNode1.length;i++)
			{
				fval+=1.0/(Math.abs(userNode1[i]-userNode2[i])+1);
			}
		}else{
			float fWeight=0f;
			for(int i=0;i<userNode1.length;i++)
			{
				fval+=1.0/(Math.abs(userNode1[i]-userNode2[i])+1)*weight[i];
				fWeight+=weight[i];
			}
			return fval/fWeight;
		}
		return fval/userNode1.length;
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float fval=0f;
		if(weight==null)
		{
			for(int i=0;i<userNode1.length;i++)
			{
				fval+=1.0/(Math.abs(userNode1[i]-userNode2[i])+1);
			}
		}else{
			float fWeight=0f;
			for(int i=0;i<userNode1.length;i++)
			{
				fval+=1.0/(Math.abs(userNode1[i]-userNode2[i])+1)*weight[i];
				fWeight+=weight[i];
			}
			return fval/fWeight;
		}
		return fval/userNode1.length;
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
