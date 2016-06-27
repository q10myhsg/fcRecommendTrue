package com.fc.recommend.neighborhood;

/**
 * 返回的数据
 * @author Administrator
 *
 */
public class NeighBean {
	public int[] usersId=null;
	public float[] scores=null;
	
	public NeighBean(int[] usersId,float[] scores)
	{
		this.usersId=usersId;
		this.scores=scores;
	}
}
