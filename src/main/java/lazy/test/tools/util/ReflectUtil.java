/**
* @Title: ReflectUit.java
* @Package util
* <b>工具说明：</b>提供通过反射来对对象的属性进行操作的功能 </br>
* @author wyyangyang1
* @date 2014-12-8 下午12:52:13
* @version V1.0
*/  
	
package lazy.test.tools.util;

import net.vidageek.mirror.dsl.Mirror;

import java.lang.reflect.Method;

/**
 * @Title: ReflectUit.java
 * @Package util
 * @Description: 反射操作工具类
 * @date 2014-12-8 下午12:54:43
 * @version V1.0
 */
public class ReflectUtil {

    /**
     * 根据变量名字，获取对象中变量
     * 
     * @param object 要获取变量的对象
     * @param filedName 要获取变量的变量名
     * @return 获取到的变量
     */
    public static Object getFiledByFieldName(Object object,String filedName) {
		return new Mirror().on(object).get().field(filedName);
	}
    
	/**
	* 根据变量名字，设置对象中变量的值
	* 
	* @param object 要设置变量的对象
	* @param filedName 要设置变量的变量名
	* @param value 要设置的值
	* 
	* @return void    
	*/
	public static void setFiledByFieldName(Object object,String filedName,Object value) {
		 new Mirror().on(object).set().field(filedName).withValue(value);
	}
	/**
	* 根据传方法名及参数，执行传入对象的方法
	* 
	* @param object 要执行方法的对象
	* @param methodName 要执行方法的方法名
	* @param invokeValues 要执行方法的参数数组
	* 
	* @return 执行返回值
	*/
	public static Object invokeMethodByMethodName(Object object,String methodName,Object[] invokeValues) {
		 return new Mirror().on(object).invoke().method(methodName).withArgs(invokeValues);
	}

    /**
     * 根据属性名获取属性值
     * */
    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
