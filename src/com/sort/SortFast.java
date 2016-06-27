package com.sort;

import java.util.Arrays;

public class SortFast implements SortUtil{

	@Override
	public void quickSort(float value,int id,int index,float[] arr,int[] arrId,boolean flag) {
		arr[index]=value;
		arrId[index]=id;
		quickSort(arr,arrId,0,index,flag);
	}
	
	public void quickSort(float[] arr,int[] arrId, int left, int right, boolean flag) {
		if (flag) {
			// 拆成多组
			int i = left, j = right,iiTemp;
			float middle, iTemp;
			middle = arr[(left + right) / 2];
			do {
				while ((arr[i] > middle) && (i < right))
					// 从左扫描小于中值的树
					i++;
				while ((arr[j] < middle) && (j > left))
					// 从右扫描大于中值的数
					j--;
				if (i <= j) {
					iTemp = arr[i];
					arr[i] = arr[j];
					arr[j] = iTemp;
					
					iiTemp=arrId[i];
					arrId[i]=arrId[j];
					arrId[j]=iiTemp;
					i++;
					j--;
				}
			} while (i <= j);
			// 当左边部分有值(left<j)，递归左半边
			if (left < j)
				quickSort(arr,arrId, left, j, flag);
			// 当右边部分有值(right>i)，递归右半边
			if (right > i)
				quickSort(arr,arrId, i, right, flag);
		} else {

			// 拆成多组
			int i = left, j = right,iiTemp;
			float middle, iTemp;
			middle = arr[(left + right) / 2];
			do {
				while ((arr[i] < middle) && (i < right))
					// 从左扫描大于中值的数
					i++;
				while ((arr[j] > middle) && (j > left))
					// 从右扫描小于中值的数
					j--;
				if (i <= j) {
					iTemp = arr[i];
					arr[i] = arr[j];
					arr[j] = iTemp;
					
					iiTemp=arrId[i];
					arrId[i]=arrId[j];
					arrId[j]=iiTemp;
					i++;
					j--;
				}
			} while (i <= j);
			// 当左边部分有值(left<j)，递归左半边
			if (left < j)
				quickSort(arr,arrId, left, j, flag);
			// 当右边部分有值(right>i)，递归右半边
			if (right > i)
				quickSort(arr,arrId, i, right, flag);
		}
	}
	
	public static void main(String[] args) {
		float[] arr=new float[21];
		int[] arrId=new int[21];
		int index=-1;
		SortFast sort=new SortFast();
		long start=System.currentTimeMillis();
		for(int i=0;i<3000000;i++)
		{
			index++;
			if(index>20)
			{
				index=20;
			}
			sort.quickSort((float)Math.random(),i,index,arr,arrId,true);
		}
		System.out.println((System.currentTimeMillis()-start)/1000.0+"ms");
		System.out.println(index+":"+Arrays.toString(arr));
		System.out.println(Arrays.toString(arrId));
	}
}
