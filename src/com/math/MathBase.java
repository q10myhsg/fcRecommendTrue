package com.math;
/**
 * 基础方法区
 * @author Administrator
 * ftp://ftp.idsoftware.com/idstuff/source/quake3-1.32b-source.zip
 *
 */
public class MathBase {

	/**
	 * 计算固定的pow方法
	 * @param x
	 * @param count
	 * @return
	 */
	public static float pow(float x,int count)
	{
		float fval=x;
		while(count>1)
		{
			fval*=x;
			count--;
		}
		return fval;
	}
	
	
	/**
	 * 计算 long 固定的pow方法
	 * @param x
	 * @param count
	 * @return
	 */
	public static long pow(long x,int count)
	{
		long fval=x;
		while(count>1)
		{
			fval*=x;
			count--;
		}
		return fval;
	}
	
	/**
	 * 平方根 快速方法
	 * @param x
	 * @return
	 */
	public static float sqrt(float x)
	{
	    float xhalf = 0.5f* x ;
	    int i = Float.floatToIntBits(x) ; // get bits for floating VALUE
	    i = 0x5f375a86- (i>>1); // gives initial guess y0
	    x = Float.intBitsToFloat(i); // convert bits BACK to float
	    x = x*(1.5f-xhalf* x *x ); // Newton step, repeating increases accuracy
	    //x = x *(1.5f-xhalf* x* x); // Newton step, repeating increases accuracy
	    x = x*(1.5f-xhalf*x*x); // Newton step, repeating increases accuracy
	    return 1/ x ;
	}
	/**
	 * 平方根 的 倒数 快速方法
	 * @param x
	 * @return
	 */
	public static float invSqrt(float x)
	{
	    float xhalf = 0.5f* x ;
	    int i = Float.floatToIntBits(x) ; // get bits for floating VALUE
	    i = 0x5f375a86- (i>>1); // gives initial guess y0
	    x = Float.intBitsToFloat(i); // convert bits BACK to float
	    x = x*(1.5f-xhalf* x *x ); // Newton step, repeating increases accuracy
	    //x = x *(1.5f-xhalf* x* x); // Newton step, repeating increases accuracy
	    x = x*(1.5f-xhalf*x*x); // Newton step, repeating increases accuracy
	    return x ;
	}
	
	public static int abs(int a)
	{
	int temp = (a >> 31);
	return (a + temp) ^ temp;
	} 
	
}
