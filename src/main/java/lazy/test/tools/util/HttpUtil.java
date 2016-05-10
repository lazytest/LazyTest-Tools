package lazy.test.tools.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <b>工具说明：</b>提供HTTP请求发送工具，支持GET、POST</br>
 * <b>使用说明：</b>推荐使用导出工具导出到XML，再稍做修改，来构造预期结果文件</br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class HttpUtil {

    private static String DEFAULT_CHARSET="UTF8"  ;

    private static final int TIME_OUT = 5000;
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * <b>方法说明：</b>发送Post请求</br>
     * 
     * @param data  请求数据map</br>
     * @param url  请求的url</br>
     * 
     * @return String  返回的字符串
     */
    public static String postRequest(Map<String, String> data, String url) {
        return makePostRequest(data, url, DEFAULT_CHARSET, null);
    }
    
    /**
     * <b>方法说明：</b>发送Post请求（JSON格式）</br>
     * 
     * @param data  请求数据map(将被转换为JSON发送)</br>
     * @param url  请求的url</br>
     * 
     * @return String  返回的字符串
     */
    public static String postRequestJson(Map<String, String> data, String url) {
    	Map<String, String> headerMap = new HashMap<String, String>();
    	headerMap.put("Content-Type", "application/json");
    	
        return makeRequest(JsonUtil.hashMap2JSONNoFeatures(data), url, DEFAULT_CHARSET, headerMap);
    }

    /**
     * <b>方法说明：</b>发送Post请求</br>
     * 
     * @param data  请求的字符串数据</br>
     * @param url  请求的url</br>
     * 
     * @return String  返回的字符串
     */
    public static String postRequest(String data, String url) {
        return makeRequest(data, url, DEFAULT_CHARSET, null);
    }

    /**
     * <b>方法说明：</b>发送Post请求</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * @param header  请求报文头 </br>
     * 
     * @return String  返回的字符串
     */
    public static String postRequest(Map<String, String> data, String url, Map<String, String> header) {
        return makePostRequest(data, url, DEFAULT_CHARSET, header);
    }

    /**
     * <b>方法说明：</b>发送Post请求(Json)</br>
     * 
     * @param data  请求的数据map(将被转换为JSON发送)</br>
     * @param url  请求的url</br>
     * @param header  请求报文头 </br>
     * 
     * @return String  返回的字符串
     */
    public static String postRequestJson(Map<String, String> data, String url, Map<String, String> header) {
    	if (!header.containsKey("Content-Type")) {
    		header.put("Content-Type", "application/json");
    	}
    	
        return makeRequest(JsonUtil.hashMap2JSONNoFeatures(data), url, DEFAULT_CHARSET, header);
    }
    /**
     * <b>方法说明：</b>发送Post请求</br>
     * 
     * @param data  请求的数据</br>
     * @param url  请求的url</br>
     * @param header  请求报文头 </br>
     * 
     * @return String  返回的字符串
     */
    public static String postRequest(String data, String url, Map<String, String> header) {
        return makeRequest(data, url, DEFAULT_CHARSET, header);
    }
    /**
     * <b>方法说明：</b>发送Get请求</br>
     * 
     * @param url  请求的url</br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequest(String url) {
        return getRequest(null,url,DEFAULT_CHARSET,null);
    }
    /**
     * <b>方法说明：</b>发送Get请求</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequest(Map<String, String> data, String url) {
        return getRequest(data,url,DEFAULT_CHARSET,null);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求（不做URLEncode）</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequestWithoutEncode(Map<String, String> data, String url) {
        return getRequest(data,url,DEFAULT_CHARSET,null,false);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequest(Map<String, String> data, String url, String encoding) {
        return getRequest(data,url,encoding,null);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求（不做URLEncode）</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequestWithoutEncode(Map<String, String> data, String url, String encoding) {
        return getRequest(data,url,encoding,null,false);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * @param header  请求报文头 </br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequest(Map<String, String> data, String url, String encoding, Map<String, String> header) {
    	return getRequest(data, url, encoding, header, true);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求（不做URLEncode）</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * @param header  请求报文头 </br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequestWithoutEncode(Map<String, String> data, String url, String encoding, Map<String, String> header) {
        return getRequest(data, url, encoding, header, false);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求（不做URLEncode）</br>
     * 
     * @param data  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * @param header  请求报文头 </br>
     * @param needEncode 是否要进行URL编码</br>
     * 
     * @return String  返回的字符串
     */
    public static String getRequest(Map<String, String> data, String url, String encoding, Map<String, String> header, boolean needEncode) {
        GetMethod getMethod = getGetMethod(data, url, encoding, needEncode);
        setRequestHeaderIfNeed(getMethod, header);
        return getRequest(getMethod);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求</br>
     * 
     * @param map  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * 
     * @return String  返回的字符串
     */
    public static String genGetUrl(Map<String, String> map,String encoding,String url) {
    	return genGetUrl(map, encoding, url, true);
    }
    
    /**
     * <b>方法说明：</b>发送Get请求</br>
     * 
     * @param map  请求的数据map</br>
     * @param url  请求的url</br>
     * @param encoding  编码</br>
     * @param needEncode 是否要进行URL编码</br>
     * 
     * @return String  返回的字符串
     */
    public static String genGetUrl(Map<String, String> map,String encoding,String url,boolean needEncode) {
        if(map==null||map.size()<=0){
            return url;
        }
        ArrayList<String> mapKeys = new ArrayList<String>(map.keySet());
        StringBuilder link = new StringBuilder();
        boolean first = true;
        for (String key : mapKeys) {
            String value = map.get(key);
            if (value == null || "".equals(value.trim())) continue;
            if (!first) link.append("&");
            link.append(key).append("=").append(value);
            if (first) first = false;
        }
        String paramStr=link.toString();
        
        if(needEncode) {
	        try {
	            paramStr = URLEncoder.encode(link.toString(), encoding);
	        }catch (Exception e){
	            logger.error("get 参数传为url时，urlencode时报错");
	
	        }
        }
        
        if(paramStr!=null&&!paramStr.equals("")){
            return url+"?"+paramStr;
        }
        return url;
    }


    private static String makePostRequest(Map<String, String> data, String url, String encoding, Map<String, String> header) {
        PostMethod postMethod = getPostMethod(data, url, encoding);
        setRequestHeaderIfNeed(postMethod, header);
        return postRequest(postMethod);
    }

    private static String makeRequest(String data, String url, String encoding, Map<String, String> header) {
        PostMethod postMethod = getPostMethod(data, url, encoding);
        setRequestHeaderIfNeed(postMethod, header);
        return postRequest(postMethod);
    }




    private static String postRequest(PostMethod method) {
        try {
            HttpClient httpClient = new HttpClient();
            HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
            params.setConnectionTimeout(TIME_OUT);
            params.setSoTimeout(TIME_OUT);
            httpClient.executeMethod(method);
            return new String(method.getResponseBodyAsString().getBytes());
        } catch (Exception e) {
            logger.error("Exception happened, message:{}", e.getMessage(), e);
            return null;
        } finally {
            method.releaseConnection();
        }
    }

    private static String getRequest(GetMethod method) {
        try {
            HttpClient httpClient = new HttpClient();
            HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
            params.setConnectionTimeout(TIME_OUT);
            params.setSoTimeout(TIME_OUT);
            httpClient.executeMethod(method);
            return new String(method.getResponseBodyAsString().getBytes());
        } catch (Exception e) {
            logger.error("Exception happened, message:{}", e.getMessage(), e);
            return null;
        } finally {
            method.releaseConnection();
        }
    }

    private static PostMethod getPostMethod(Map<String, String> request, String url, String encoding) {
        PostMethod postMethod = new PostMethod(url);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
        NameValuePair[] body;
        if (request == null) {
            body = new NameValuePair[0];
        } else {
            Set<Map.Entry<String, String>> entries = request.entrySet();

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : entries) {
                pairs.add(new NameValuePair(entry.getKey(), entry.getValue()));
            }
            body = pairs.toArray(new NameValuePair[pairs.size()]);
        }
        postMethod.setRequestBody(body);
        return postMethod;
    }

    private static PostMethod getPostMethod(String request, String url, String encoding) {
        PostMethod postMethod = new PostMethod(url);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
        try {
            StringRequestEntity requestEntity =
                    new StringRequestEntity(request, null, encoding);
            postMethod.setRequestEntity(requestEntity);
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception happened, message:{}", e.getMessage(), e);
        }
        return postMethod;
    }

    private static GetMethod getGetMethod(Map<String, String> request, String url, String encoding, boolean needEncode){
        GetMethod getMethod=new GetMethod(genGetUrl(request,encoding,url, needEncode));
        getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
        return getMethod;
    }

    private static void setRequestHeaderIfNeed(HttpMethodBase method, Map<String, String> header) {
        if (header != null) {
            Set<Map.Entry<String, String>> entries = header.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                method.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }
    }



}
