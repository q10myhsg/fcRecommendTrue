package com.fc.recommend.dataBean;


public class ItemNode implements Comparable<ItemNode>{

	/**
	 * 物品id
	 */
	public int itemId=0;
	/**
	 * 业态id
	 */
	public int categoryId=0;
	/**
	 * 喜好度 及最终得分
	 */
	public float value=0.0f;
	/**
	 * 对应的数量
	 */
	public int count=1;
	
	
	
	public ItemNode(int itemId,int categoryId,float value,int count)
	{
		this.itemId=itemId;
		this.value=value;
		this.count=count;
		this.categoryId=categoryId;
	}
	
	public int compareTo(ItemNode other)
	{
		return -Float.compare(value, other.value);
	}
	
	public boolean equals(Object in)
	{
		ItemNode st=(ItemNode)in;
		if(this.itemId==st.itemId)
		{
			return true;
		}else{
			return false;
		}
	}
	public int hashCode()
	{
		return (int)itemId & 0x7FFFFFFF;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
