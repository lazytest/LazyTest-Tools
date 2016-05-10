package lazy.test.tools.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>工具说明：提供Json和对象互转的常用功能</b></br>
 * <b>使用说明：</b></br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class JsonUtil {

    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
    }

    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };

    /**
     * <b>方法说明：</b>将指定的对象导出为json串，有特殊转换要求的</br>
     * <b>使用说明：</b>list字段如果为null，输出为[]，而不是null;</br>
     * &emsp;&emsp;&emsp;&emsp;&emsp;数值字段如果为null，输出为0，而不是null
     * &emsp;&emsp;&emsp;&emsp;&emsp;Boolean字段如果为null，输出为false，而不是null
     * &emsp;&emsp;&emsp;&emsp;&emsp;字符类型字段如果为null，输出为""，而不是null
     * 
     * @param object 需要导出的对象 </br>
     * 
     * @return String
     */
    public static String object2JSONString(Object object) {
        return JSON.toJSONString(object, config, features);
    }
    /**
     * <b>方法说明：</b>将指定的对象导出为json串,无特殊转换要求的</br>
     * @param object 需要导出的对象 </br>
     * @return String
     */
    public static String object2JSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config);
    }
    /**
     * <b>方法说明：</b>将指定的ArrayList对象导出为json串,无特殊转换要求的</br>
     * 
     * @param list 需要导出的ArrayList对象 </br>
     * 
     * @return String
     */
    public static String arrayList2JSONNoFeatures(List list) {
        return JSON.toJSONString(list, config);
    }
    /**
     * <b>方法说明：</b>将指定的HashMap对象导出为json串,无特殊转换要求的</br>
     * 
     * @param map 需要导出的HashMap对象 </br>
     * 
     * @return String
     */
    public static String hashMap2JSONNoFeatures(Map map) {
        return JSON.toJSONString(map, config);
    }

    /**
     * <b>方法说明：</b>将指json串导入为对象</br>
     * 
     * @param json 需要导入的jason，最好为此工具导出的json </br>
     * 
     * @return String
     */
    public static Object toBean(String json) {
        return JSON.parse(json);
    }
    /**
     * <b>方法说明：</b>将指json串导入为指定对象</br>
     * 
     * @param json 需要导入的jason，最好为此工具导出的json </br>
     * @param clazz 目标对象
     * 
     * @return T
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * <b>方法说明：</b>将指json串导入为ArrayList</br>
     * 
     * @param json 需要导入的jason，最好为此工具导出的json </br>
     * 
     * @return ArrayList
     */
    public static  List json2ArrayList(String json) {
        return (ArrayList)JSON.parseArray(json, ArrayList.class);
    }
    
    /**
     * <b>方法说明：</b>将指json串导入为HashMap" </br>
     * 
     * @param json 需要导入的jason，最好为此工具导出的json </br>
     * 
     * @return HashMap
     */
    public static <T> Map<String, T> json2HashMap(String json) {
        return JSON.parseObject(json, HashMap.class);
    }

}
