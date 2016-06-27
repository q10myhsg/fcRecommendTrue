package com.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {

	public static DecimalFormat df = new DecimalFormat("0.00");
	public static DateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static DateFormat datef2 = new SimpleDateFormat("yyyy-MM-dd");

	public static String parse(double val) {
		return df.format(val);
	}

	public static String parse(Date val) {
		return datef.format(val);
	}

	public static String parseDate(Date val) {
		return datef2.format(val);
	}
	/**
	 * "lastDate": {
     * "$numberLong": "1382927899000"
     *}
	 * @param str
	 * @return
	 */
	public static String parseMongoLongToDate(String str)
	{
		Matcher matcher = Pattern.compile("[0-9]").matcher(str);
		int index=0;
		if(matcher.find())
		{
			index=matcher.start();
		}
		return parseDate(Long.parseLong(str.substring(index,index+13)));
	}
	
	
	/**
	 * long 转时间
	 * @param val
	 * @return
	 */
	public static String parseDate(long val)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(val);
		return parse(c.getTime());
	}
	
	// TimeZone srcTimeZone = TimeZone.getTimeZone("EST");
	// TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");

	public static String fromTimeZone(String sourceDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.CHINA);
		try {
			Date date = sdf.parse(sourceDate);
			return parseDate(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String dateTransformBetweenTimeZone(Date sourceDate,
			DateFormat formatter, TimeZone sourceTimeZone,
			TimeZone targetTimeZone) {
		Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset()
				+ targetTimeZone.getRawOffset();
		return getTime(new Date(targetTime), formatter);
	}

	public static String getTime(Date date, DateFormat formatter) {
		return formatter.format(date);
	}

	/**
	 * 输入格式为字符串
	 *  { "$date" : "2015-03-31T02:54:26.119Z"}}
	 * @param str
	 * @return
	 */
	public static Date parseMongoDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.US);
		Date da = null;
		Matcher matcher = Pattern.compile("[0-9]").matcher(str);
		int index=0;
		if(matcher.find())
		{
			index=matcher.start();
		}
		try {
			da = sdf.parse(str.substring(index).replaceAll("[TZ]", " "));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(da);
		cal.add(Calendar.HOUR, 8);
	//	System.out.println(cal.getTime());
		return cal.getTime();
	}

	public static void main(String[] args) {
		double d = 1.00999d;
		System.out.println(parse(d));

		System.out.println(parse(new Date()));
		String st = "{ \"$date\" : \"2015-03-31T02:54:26.119Z\"}}";
		System.out.println(Format.parseMongoDate(st));
	}

}
