package com.fc.recommend.dataBean;

import java.util.LinkedList;

import com.fc.recommend.dataOut.dataBean.SonSimBean;

public class StreamBean {

	public UserNode userNode = null;
	public int groupId = -1;
	public int groupId2=-1;
	public LinkedList<SonSimBean> simiBean=null;

	public StreamBean(UserNode userNode,LinkedList<SonSimBean> simiBean, int groupId,int groupId2,int categoryId) {
		this.userNode =new UserNode();
		this.userNode.categoryId=categoryId;
		//只需要更新clonesortItem即可
		this.userNode.sortItem=(LinkedList<ItemNode>) userNode.sortItem.clone();
		this.userNode.userId=userNode.userId;
		this.userNode.alpha=userNode.alpha;
		this.groupId=groupId;
		this.groupId2=groupId2;
		this.simiBean=simiBean;
	}

	public UserNode getUserNode() {
		return userNode;
	}

	public void setUserNode(UserNode userNode) {
		this.userNode = userNode;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public LinkedList<SonSimBean> getSimiBean() {
		return simiBean;
	}

	public void setSimiBean(LinkedList<SonSimBean> simiBean) {
		this.simiBean = simiBean;
	}
	
	
}
