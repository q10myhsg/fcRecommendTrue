package com.fc.recommend.similary2;

import java.util.HashMap;
import java.util.Map.Entry;

import com.math.MathBase;

/**
 * 余弦相似度
 * 
 * @author Administrator
 *
 */
public class CosinSimilary2 implements SimilaryUtil2 {

	/**
	 * 计算 goods/all 切比雪夫
	 * 
	 * @param userNode1
	 * @param userNode2
	 * @return
	 */
	public float getSimilaryMax(HashMap<Integer, Integer> userNode1,
			HashMap<Integer, Integer> userNode2) {
		float up = 0f;
		float down1 = 0f;
		// 计算其他非结构化属性
		for (Entry<Integer, Integer> m : userNode1.entrySet()) {
			Integer index2 = userNode2.get(m.getKey());
			// System.out.println("id:"+m.getKey()+"\tuser1:"+m.getValue()+"\tuser2:"+index2);
			if (index2 == null) {
				down1 += m.getValue();
				continue;
			}
			up += Math.abs(m.getValue() - index2);
			down1 += Math.max(m.getValue(), index2);
			// System.out.println("up:"+up);
		}
		if (Float.compare(up, 0f) == 0) {
			return 0f;
		}
		for (Entry<Integer, Integer> m : userNode2.entrySet()) {
			Integer index2 = userNode1.get(m.getKey());
			if (index2 == null) {
				down1 += m.getValue();
				continue;
			}
		}
		// System.out.println("up:"+up+"\tdown1:"+down1+"\tdown2:"+down2);
		float sim1 = up / down1;
		return 1 - sim1;
	}

	/**
	 * 计算 user feather to item feather mathches 为物品的属性是否存在于用户中
	 * 
	 * @param userNode1
	 * @param userNode2
	 * @return
	 */
	public float getSimilaryMatch(HashMap<Integer, Integer> userNode1,
			HashMap<Integer, Integer> itemNode1,float lossValue) {
		int matchCount = 0;
		// 计算其他非结构化属性
		if (userNode1 == null || userNode1.size() == 0 || itemNode1 == null
				|| itemNode1.size() == 0) {
			return 0f;
		}
		for (Entry<Integer, Integer> m : itemNode1.entrySet()) {
			Integer index2 = userNode1.get(m.getKey());
			if (index2 == null) {
				continue;
			}
			matchCount++;
		}
		if (matchCount == 0) {
			return -1f;
		}
		if(matchCount!=itemNode1.size())
		{
			//做负增益
			matchCount-=(itemNode1.size()-matchCount)*lossValue;
		}
		return matchCount*1f/ itemNode1.size();
	}

	public float getSimilary(HashMap<Integer, Integer> userNode1,
			HashMap<Integer, Integer> userNode2) {
		float up = 0f;
		float down1 = 0f;
		float down2 = 0f;
		// 计算其他非结构化属性
		for (Entry<Integer, Integer> m : userNode1.entrySet()) {
			down1 += m.getValue() * m.getValue();
			Integer index2 = userNode2.get(m.getKey());
			System.out.println("id:" + m.getKey() + "\tuser1:" + m.getValue()
					+ "\tuser2:" + index2);
			if (index2 == null) {
				continue;
			}
			up += m.getValue() * index2;
			// System.out.println("up:"+up);
		}
		if (Float.compare(up, 0f) == 0) {
			return 0f;
		}
		for (Entry<Integer, Integer> m : userNode2.entrySet()) {
			down2 += m.getValue() * m.getValue();
		}
		// System.out.println("up:"+up+"\tdown1:"+down1+"\tdown2:"+down2);
		float sim1 = up / MathBase.sqrt(down1) / MathBase.sqrt(down2);
		return sim1;
	}
	
	
	public float getSimilaryF(HashMap<Integer, Float> userNode1,
			HashMap<Integer, Float> userNode2) {
		float up = 0f;
		float down1 = 0f;
		float down2 = 0f;
		// 计算其他非结构化属性
		for (Entry<Integer, Float> m : userNode1.entrySet()) {
			down1 += m.getValue() * m.getValue();
			Float index2 = userNode2.get(m.getKey());
//			System.out.println("id:" + m.getKey() + "\tuser1:" + m.getValue()
//					+ "\tuser2:" + index2);
			if (index2 == null) {
				continue;
			}
			up += m.getValue() * index2;
			// System.out.println("up:"+up);
		}
		if (Float.compare(up, 0f) == 0) {
			return 0f;
		}
		for (Entry<Integer, Float> m : userNode2.entrySet()) {
			down2 += m.getValue() * m.getValue();
		}
		// System.out.println("up:"+up+"\tdown1:"+down1+"\tdown2:"+down2);
		float sim1 = up / MathBase.sqrt(down1) / MathBase.sqrt(down2);
		return sim1;
	}
	


	@Override
	public float getSimilary(float[] userNode1, float[] userNode2,
			float[] weight) {
		float upValue = 0f;
		float down1 = 0f;
		float down2 = 0f;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				upValue += userNode1[i] * userNode2[i];
				down1 += MathBase.pow(userNode1[i], 2);
				down2 += MathBase.pow(userNode2[i], 2);
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				upValue += userNode1[i] * userNode2[i] * weight[i];
				down1 += MathBase.pow(userNode1[i], 2) * weight[i];
				down2 += MathBase.pow(userNode2[i], 2) * weight[i];
			}
		}
		float down = MathBase.sqrt(down1) * MathBase.sqrt(down2);
		if (down == 0f) {
			return 0f;
		}
		return (float) (upValue / (down));
	}

	@Override
	public float getSimilary(int[] userNode1, int[] userNode2, float[] weight) {
		// TODO Auto-generated method stub
		float upValue = 0f;
		float down1 = 0f;
		float down2 = 0f;
		if (weight == null) {
			for (int i = 0; i < userNode1.length; i++) {
				upValue += userNode1[i] * userNode2[i];
				down1 += MathBase.pow(userNode1[i], 2);
				down2 += MathBase.pow(userNode2[i], 2);
			}
		} else {
			for (int i = 0; i < userNode1.length; i++) {
				upValue += userNode1[i] * userNode2[i] * weight[i];
				down1 += MathBase.pow(userNode1[i], 2) * weight[i];
				down2 += MathBase.pow(userNode2[i], 2) * weight[i];
			}
		}
		float down = MathBase.sqrt(down1) * MathBase.sqrt(down2);
		if (down == 0f) {
			return 0f;
		}
		return (float) (upValue / (down));
	}

	@Override
	public float getSimilary(String s1, String s2) {
		// TODO Auto-generated method stub
		return 0;
	}

}