package build_cube;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Get_cubs {
    static String encoding = "UTF-8";
    static String username = "ADMIN";
    static String passwd = "KYLIN";
    static String url = "http://218.245.1.135:7070/kylin/api/cubes";  //models/api/tables?project=cdr

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
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Content-type", "application/json;charset=utf-8");
            httpget.setHeader("Authorization", KylinUtil.getPWD(username,passwd));//Basic QURNSU46S1lMSU4=

            //执行请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpget);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (null != entity) {
                    //按指定编码转换结果实体为String类型
                    String body = EntityUtils.toString(entity, encoding);

                    //返回json数组

                    JSONArray arr= JSONObject.parseArray(body);
                    int size = arr.size();
                    for (int i=0;i<size;i++){
                        JSONObject obj = (JSONObject) arr.get(i);
                        String name = obj.getString("name");
                        String uuid = obj.getString("uuid");
                        String last_modified = obj.getString("last_modified");
                        System.out.println(name+","+uuid+","+last_modified+"==>"+KylinUtil.parsetTimeStamp(Long.valueOf(last_modified)));
                    }
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


}
