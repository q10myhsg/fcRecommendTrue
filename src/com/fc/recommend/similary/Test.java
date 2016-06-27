package com.fc.recommend.similary;

import java.util.Arrays;

public class Test {
public static void main(String[] args) {
	String str="﻿20	赛特奥莱	1	4251	1916	81	944	219	0";
	String[] temp=str.split("\t");
	System.out.println(Arrays.toString(temp));
	System.out.println(temp[4].length());
	System.out.println(temp[0].substring(1,2));
	System.out.println(Long.valueOf("20".substring(0,1)));
	System.out.println(Long.valueOf(temp[0].substring(0,3)));
	System.out.println(Long.parseLong(temp[0]));
}
}
