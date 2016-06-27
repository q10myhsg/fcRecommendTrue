package com.fc.recommend.similary2;



public class MathchGoodRate implements SimilaryUtil2{
	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * good/(bad)*good/all
	 */
	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float fval=0f;
		float fweight=1f;
		if(weight==null)
		{
			for(int i=0;i<userNode1.length;i++)
			{
				if(userNode1[i]==userNode2[i])
					fval++;
			}
		}else{
			fweight=0f;
			for(int i=0;i<userNode1.length;i++)
			{
				if(userNode1[i]==userNode2[i])
					fval++;
				fweight+=weight[i];
			}
		}
		if(fval==fweight)
		{
			return fweight;
		}
		return fval/(fweight-fval)*fval/(fweight);
	}
	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
