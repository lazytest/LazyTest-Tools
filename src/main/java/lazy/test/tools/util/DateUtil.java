package lazy.test.tools.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * <b>工具说明：</b>提供日期&时间的转换、加减、格式化等工具 </br>
 * <b>使用说明：</b></br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class DateUtil {
	public enum DateField{
		Year, Month, Day;
	}


	public final static int YEAR = 1;
	public final static int MONTH = 2;
	public final static int DATE = 3;
	/**
	 *
	 * <b>方法说明：</b>在date基础上，增加或减少年、月、日
	 * 
	 * @Title: add
	 * @author:yangyang
	 * @date:2012-2-21
	 * 
	 * @param Date date
	 *            基础日期
	 * @param int field
	 *            DateUtil.YEAR=年，DateUtil.MONTH=月，DateUtil.DATE=日
	 * @param int amount
	 *            正负数
	 *            
	 * @return Date
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (field == DateUtil.YEAR) {
			calendar.add(Calendar.YEAR, amount);
			return calendar.getTime();
		} else if (field == DateUtil.MONTH) {
			calendar.add(Calendar.MONTH, amount);
			return calendar.getTime();
		} else if (field == DateUtil.DATE) {
			calendar.add(Calendar.DATE, amount);
			return calendar.getTime();
		}else if(field==Calendar.MINUTE){
			calendar.add(Calendar.MINUTE, amount);
			return calendar.getTime();
		}

		return null;
	}

	/**
	 * <b>方法说明：</b>获取指定日期中的两位“年”
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String 指定日期中的两位“年”
	 */
	public static String getYear(Date date){
		SimpleDateFormat format = new SimpleDateFormat("YY");
		return format.format(date);
	}
	/**
	 * <b>方法说明：</b>获取指定日期中的“月”
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String 指定日期中的“月”
	 */
	public static String getMoth(Date date){
		SimpleDateFormat format = new SimpleDateFormat("MM");
		return format.format(date);
	}
	/**
	 * <b>方法说明：</b>获取指定日期中的“日”
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String 指定日期中的“日”
	 */
	public static String getDay(Date date){
		SimpleDateFormat format = new SimpleDateFormat("dd");
		return format.format(date);
	}
	/**
	 * <b>方法说明：</b>将HH:mm:ss转化为Date
	 * 
	 * @param String str HH:mm:ss格式的日期字符串
	 * 
	 * @return Date
	 */
	public static Date strHmsToDate(String str){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * <b>方法说明：</b>将String yyyy-MM-dd HH:mm:ss转化为Date
	 * 
	 * @param String str yyyy-MM-dd HH:mm:ss格式的日期字符串
	 * 
	 * @return Date
	 */
	public static Date strYMdHmsToDate(String str){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * <b>方法说明：</b>将Date转化为String HH:mm:ss
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String HH:mm:ss格式的日期字符串
	 */
	public static String dateHmsToStr(Date date){
		String str = null;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		str = format.format(date);
		return str;
	}
	/**
	 * <b>方法说明：</b>将Date转化为String yyyy-MM-dd HH:mm:ss
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String yyyy-MM-dd HH:mm:ss格式的日期字符串
	 */
	public static String dateYMdHmsToString(Date date){
		String str = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		str = format.format(date);
		return str;
	}
	/**
	 * <b>方法说明：</b>将String 型的yyyy-MM-dd转化为Date
	 * 
	 * @param String str yyyy-MM-dd格式的日期字符串
	 * 
	 * @return Date
	 */
	public static Date strYMdToDate(String str){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * <b>方法说明：</b>将String 型的yyyy/MM/dd转化为Date
	 * 
	 * @param String str yyyy/MM/dd格式的日期字符串
	 * 
	 * @return Date
	 */
	public static Date strYMdToDate1(String str){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * <b>方法说明：</b>将Date转化为String yyyy-MM-dd
	 * 
	 * @param Date date指定日期
	 * 
	 * @return String yyyy-MM-dd格式的日期字符串
	 */
	public static String dateYMdToStr(Date date){
		String str = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		str = format.format(date);
		return str;
	}

	/**
	 * <b>方法说明：</b>将Date 转化为String yyyyMMdd
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String yyyyMMdd格式的日期字符串
	 */
	public static String dateYMdToStrNoSplit(Date date){
		String str = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		str = format.format(date);
		return str;
	}

	/**
	 * <b>方法说明：</b>将Date转化为年月 yyyyMM
	 * 
	 * @param Date date 指定日期
	 * 
	 * @return String yyyyMM格式的日期字符串
	 */
	public static String dateYMdToMonth(Date date){
		String str  = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		str = format.format(date);
		return str;
	}

	/**
	 *
	 * <b>方法说明：</b>获取当前日期，计算并返回指定格式字符串
	 *
	 * @param String strFormat 日期格式
	 * @param int dayDelta 日期天数差
	 * 
	 * @return String 计算后的指定格式的日期字符串
	 */
	public static String getCurrentDateString(String strFormat , int dayDelta){
		Date calendar = add(Calendar.getInstance().getTime(), DATE, dayDelta);
		SimpleDateFormat format = new SimpleDateFormat(strFormat);
		return format.format(calendar);
	}

	/**
	 * <b>方法说明：</b>获取当前日期，返回yyyyMM格式字符串
	 * 
	 * @return String yyyyMM格式的当前日期字符串
	 */
	public static String getCurrentYearMonthString(){
		Calendar calendar = Calendar.getInstance();
		return dateYMdToMonth(calendar.getTime());
	}

	/**
	 * <b>方法说明：</b>获取前一天的日期，格式为yyyyMMdd或者yyyy-MM-dd
	 *
	 * @param boolean noSpliter - true, 返回yyyyMMdd格式；false,返回yyyy-MM-dd格式
	 * 
	 * @return String 指定格式的前一天日期字符串
	 */
	public static String getYestodayDateString(boolean noSpliter){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);

		if(noSpliter){
			return dateYMdToStrNoSplit(calendar.getTime());
		}

		return dateYMdToStr(calendar.getTime());
	}

	/**
	 * <b>方法说明：</b>获取前一天的yyyyMM格式日期
	 *
	 * @return String 当前日期前一天，yyyyMM格式的字符串
	 */
	public static String getYestodayYearMonthString(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);

		return dateYMdToMonth(calendar.getTime());
	}

	/**
	 * <b>方法说明：</b>获取当前日期的年份
	 *
	 * @return String 当前日期的年份
	 */
	public static String getCurrentYear(){
		Calendar calendar = Calendar.getInstance();
		return String.valueOf(calendar.get(Calendar.YEAR));
	}

	/**
	 * <b>方法说明：</b>获取当前日期的月份
	 * 
	 * @return String 当前日期的月份
	 */
	public static String getCurrentMonth(){
		Calendar calendar = Calendar.getInstance();
		return String.valueOf(calendar.get(Calendar.MONTH)+1);
	}

	/**
	 * <b>方法说明：</b>：获取当前日期的天数
	 * 
	 * @return String 当前日期，月的第几天
	 */
	public static String getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
		return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	}


	/**
	 *
	 * <b>方法说明：</b>获取年、月、日日期字段的值
	 *
	 * @param DateField field 指定日期字段
	 * @param String strDate 字符串表示的日期
	 * 
	 * @return String 指定日期字段的值
	 */
	public static String getSpecifiedField(DateField field, String strDate){
		if(strDate == null || strDate.length() == 0){
			strDate = getCurrentDateString("yyyy-MM-dd",0);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(strYMdToDate(strDate));
		String value = "";
		switch(field){
			case Year:
				value = String.valueOf(calendar.get(Calendar.YEAR));
				break;
			case Month:
				value = String.valueOf(calendar.get(Calendar.MONTH) + 1);
				break;
			case Day:
				value = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
				break;
			default:
				value = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
				break;
		}

		return value;
	}

	/**
	 *
	 * <b>方法说明：</b>根据日期，返回日期计算后的field数值
	 *
	 * @param DateField 指定日期字段
	 * @param String strDate - 指定的日期值，默认为当前日期
	 * @param int diff
	 * 
	 * @return String 日期计算后指定日期字段的数值
	 */
	public static String getSpecifiedField(DateField field, String strDate, int diff){
		if(strDate == null || strDate.length() == 0){
			strDate = getCurrentDateString("yyyy-MM-dd",0);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(strYMdToDate(strDate));

		calendar.add(Calendar.DATE, diff);
		strDate = dateYMdToStr(calendar.getTime());

		return getSpecifiedField(field, strDate);
	}

	/**
	 * <b>方法说明：</b>获取同步的，唯一的时间戳，精确到毫秒
	 * 
	 * @return String 时间戳
	 */
	public synchronized static String getTimeSnapshot(){
		String  timeSnapshot;
		timeSnapshot = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return timeSnapshot;
	}

	/**
	 * <b>方法说明：</b>时间转换成毫秒
	 * 
	 * @param String time
	 * 
	 * @return String 以毫秒表示的时间
	 */
	public String time2MillionSeconds(String time) throws ParseException{
		//定义时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		Date date = dateFormat.parse(time);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return String.valueOf(c.getTimeInMillis());
	}

    /**
     * <b>方法说明：</b>将Date转化为需要的格式
     * 
     * @param Date date
     * @param String dateFormat 例如:"yyyy-MM-dd hh:mm:ss.SSS"
     * 
     * @return String 指定格式时间字符串
     */
    public static String dateToStrByFormat(Date date,String dateFormat){
        String str = null;
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        str = format.format(date);
        return str;
    }

    /**
     * <b>方法说明：</b>将String转化为Date
     * 
     * @param Date date
     * @param String dateFormat 例如:"yyyy-MM-dd hh:mm:ss.SSS"
     * 
     * @return Date
     */
    public static Date stringToDateByFormat(String date,String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
