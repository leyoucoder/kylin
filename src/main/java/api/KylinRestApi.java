package api;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class KylinRestApi {
    static String encoding = "UTF-8";
    static String username = "ADMIN";
    static String passwd = "KYLIN";
    static String url = "http://218.245.1.135:7070/kylin/api/query";

    public static void main(String[] args) throws IOException {
        requestByPostMethod();
    }

    /**
     * 使用httpcline 进行post访问
     *
     * @throws IOException
     */
    public static void requestByPostMethod() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            //创建post方式请求对象
            // 接收参数json列表 (kylin 只接受json格式数据)
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
//          String sql="select * from KYLIN_ACCOUNT ";
            String sql = "SELECT KYLIN_ACCOUNT.ACCOUNT_BUYER_LEVEL  ,count(*) as data FROM KYLIN_ACCOUNT  group by  KYLIN_ACCOUNT.ACCOUNT_BUYER_LEVEL  order by data desc";
            jsonParam.put("sql", sql);
//            jsonParam.put("limit", "20");
            jsonParam.put("project", "kylin_demo");

            StringEntity sentity = new StringEntity(jsonParam.toString(), encoding);//解决中文乱码问题
            sentity.setContentEncoding(encoding);
            sentity.setContentType("application/json");
            httpPost.setEntity(sentity);

            //设置header信息
            //指定报文头【Content-type】,【User-Agent】
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            httpPost.setHeader("Authorization", getPWD(username,passwd));//Basic QURNSU46S1lMSU4=

            //执行请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (null != entity) {
                    //按指定编码转换结果实体为String类型
                    String body = EntityUtils.toString(entity, encoding);
                    JSONObject obj = JSONObject.parseObject(body);
                    System.out.println(body);
                    System.out.println(obj.get("results"));
                }
            } finally {
                httpResponse.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
    }

    public static String getPWD(String username, String passwd) {
        String parm = username+":"+passwd;
        String result="";
        try{
            byte[] encodeBase64 = Base64.encodeBase64(parm.getBytes("UTF-8"));
            result = new String(encodeBase64);
            return "Basic "+result;
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
    }
}
