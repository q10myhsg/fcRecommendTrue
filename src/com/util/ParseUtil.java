package com.util;


public class ParseUtil {

	
	public static String standerValue(String str) {
		try {
			Double.parseDouble(str);
		} catch (Exception e) {
			return null;
		}
		return str;
	}
	public static String starndarMoney(String str, int pricie) {
		if (str.length() == 0) {
			return null;
		}
		Double val = Double.parseDouble(str);
		return Format.parse(val);
	}

	public static String removeZero(String month) {
		if (month.charAt(0) == '0') {
			return Character.toString(month.charAt(1));
		}
		return month;
	}
}
