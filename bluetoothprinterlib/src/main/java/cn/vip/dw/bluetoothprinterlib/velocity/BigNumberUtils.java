package cn.vip.dw.bluetoothprinterlib.velocity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * 类名: ArithUtils <br/>
 * 功能: 数学计算工具类 <br/>
 * 日期: 2015年10月30日 下午2:56:37 <br/> *
 * @author liuyu
 */
public final class BigNumberUtils {

	private static final int  DEF_DIV_SCALE  = 10;
   /**
    * 提供精确的加法运算。
    * @param v1 被加数
    * @param v2 加数
    * @return 两个参数的和
    */
   public static double add(double v1,double v2){
       BigDecimal b1 = new BigDecimal(Double.toString(v1));
       BigDecimal b2 = new BigDecimal(Double.toString(v2));
       return b1.add(b2).doubleValue();
   }

   /**
    * 提供精确的减法运算。
    * @param v1 被减数
    * @param v2 减数
    * @return 两个参数的差
    */
   public static double sub(double v1,double v2){
       BigDecimal b1 = new BigDecimal(Double.toString(v1));
       BigDecimal b2 = new BigDecimal(Double.toString(v2));
       return b1.subtract(b2).doubleValue();
   }


   /**
    * 提供精确的乘法运算
    * @param v1 被减数
    * @param v2 减数
    * @return 两个参数的乘积
    */
   public static double mutiply(double v1,double v2){
       BigDecimal b1 = new BigDecimal(Double.toString(v1));
       BigDecimal b2 = new BigDecimal(Double.toString(v2));
       return  b1.multiply(b2).doubleValue();
   }


   /**提供相对精确的除法运算,当除不净时，保留10小数，小数点10位后的数字四舍五入
    * @param v1 被除数
    * @param v2 除数
    * @return 返回v1/v2的商，最多保留10小数,小数点10位后的数字四舍五入
    */
   public static double div(double v1,double v2) {
	   return div(v1,v2,DEF_DIV_SCALE);
   }


   /**提供相对精确的除法运算,当除不净时，保留scale位小数，小数点scale位后的数字四舍五入
    * @param v1 被除数
    * @param v2 除数
    * @param scale 表示要精确到小数点后几位
    * @return 返回v1/v2的商，最多保留scale小数,小数点scale位后的数字四舍五入
    */
   public static double div(double v1,double v2,int scale) {
	   if(scale<0){
           throw new IllegalArgumentException(
               "The scale must be a positive integer or zero");
       }
	   BigDecimal d1 = new BigDecimal(Double.toString(v1));
	   BigDecimal d2 = new BigDecimal(Double.toString(v2));
	  return d1.divide(d2,scale, BigDecimal.ROUND_HALF_UP).doubleValue();
   }



   /**
    * 提供精确的小数位四舍五入处理。
    * @param v 需要四舍五入的数字
    * @param scale 小数点后保留几位
    * @return 四舍五入后的结果
    */
   public static double round(double v,int scale){
       if(scale<0){
           throw new IllegalArgumentException(
               "The scale must be a positive integer or zero");
       }
       BigDecimal b = new BigDecimal(Double.toString(v));
       BigDecimal one = new BigDecimal("1");
       return b.divide(one,scale, BigDecimal.ROUND_HALF_UP).doubleValue();
   }


   /**
    *向下取整数 12.9 =>12
    */
   public static int roundDown(double v) {
	   BigDecimal b = new BigDecimal(Double.toString(v));
	   BigDecimal one = new BigDecimal("1");
	   return b.divide(one, 0, BigDecimal.ROUND_DOWN).intValue();
   }


   /**
    * 向上取整数  12.1 =>13
   */
   public static int roundUp(double v) {
	   BigDecimal b = new BigDecimal(Double.toString(v));
	   BigDecimal one = new BigDecimal("1");
	   return b.divide(one, 0, BigDecimal.ROUND_UP).intValue();
   }

   /**
    * 四舍五入取整
    */
   public static int roundHalfUp(double v){
	  return  new BigDecimal(v).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
   }


   /**返回现金表示形式保留两位小数
    * 如果money是100.53600,返回￥100.54
    * 100 = >￥100.00
    */
   public  static String getCurrency(double money){
       NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.CHINA);
       return formatter.format(money);
   }

   /***
    * <br>描 述: 向上取 BigDecimal,保留两位小数
    * <br>作 者: zhaoc
    * <br>历 史: (版本) 作者 时间 注释
    */
   public static BigDecimal roundUpBegDecimal(BigDecimal a) {
	   return a.divide(new BigDecimal("1"), 2, BigDecimal.ROUND_UP);
   }
   /**
    * <br>描 述: 保留0位小数 向上取
    * <br>作 者: zhaoc
    * <br>历 史: (版本) 作者 时间 注释
    */
   public static BigDecimal roundUpRetain0BegDecimal(BigDecimal a) {
	   return a.divide(new BigDecimal("1"), 0, BigDecimal.ROUND_UP);
   }

   /**
	 * <br>描 述: 取负数
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static BigDecimal inverse(BigDecimal a) {
		return a.multiply(new BigDecimal("-1"));
	}

   /**
	 * <br>描 述： 比较两个 BigDecimal 数值 并返回 大数值{代表折扣优惠力度小的折扣}
	 * <br>作 者：zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 * @param a
	 * @param b
	 * @return 较大的 BigDecimal
	 */
	public static BigDecimal getMaxBigDecimal(BigDecimal a, BigDecimal b) {
		//a等于b 返回a
		if (a.compareTo(b) == 0) {
			return a;
		}
		//a > b 返回  a 否则 返回 b
		return a.compareTo(b) == 1 ? a: b;
	}

	/**
	 * <br>描 述: a > b 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean gt(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) > 0 ? true : false;
	}

	/**
	 * <br>描 述: a >= b 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean gte(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) >= 0 ? true : false;
	}

	/**
	 * <br>描 述: a < b 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean lt(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) < 0 ? true : false;
	}

	/**
	 * <br>描 述: a <= b 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean lte(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) <= 0 ? true : false;
	}

	/**
	 * <br>描 述: a = b 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean eq(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) == 0 ? true : false;
	}

	/**
	 * <br>描 述: a != b 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean neq(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) != 0 ? true : false;
	}

	/**
	 * <br>描 述: a是否 在 b 和 c 范围 内 返回 a>=b && a<=c 返回 true
	 * <br>作 者: zhaoc
	 * <br>历 史: (版本) 作者 时间 注释
	 */
	public static Boolean btw(BigDecimal a, BigDecimal b, BigDecimal c) {
		return (gte(a, b) && lte(a, c));
	}

}