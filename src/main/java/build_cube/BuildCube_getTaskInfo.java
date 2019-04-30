package build_cube;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class BuildCube_getTaskInfo {


    public static void main(String[] args) throws IOException {
        if (args.length==0) {
            System.out.println("参数错误：　请输入cube_name");
            System.exit(-1);
        }
        Properties prop = KylinUtil.getProperty();
        String url = prop.getProperty("url");
        String cubeName = args[0];
        String buildType = prop.getProperty("buildType");

        String userName = prop.getProperty("userName");
        String password = prop.getProperty("password");
        //方法调用
        buildCube(url, cubeName, buildType,
                userName, password);

    }

    //1,构建cube, 提交任务
    public static void buildCube(String url, String cubeName, String buildType,
                                 String username, String passwd) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String cuburl = url +"/cubes" + "/"+ cubeName + "/" + buildType;

        Properties prop = KylinUtil.getProperty();
        String encoding = prop.getProperty("encoding");
        try {
            //创建post方式请求对象
            // 接收参数json列表 (kylin 只接受json格式数据)
            HttpPut httpPut = new HttpPut(cuburl);
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("project", "cdr");
            jsonParam.put("startTime", 0);

            Date date = new Date();
            long time = date.getTime();
            System.out.println("修改时间　 " + date);
            jsonParam.put("endTime", time);
            jsonParam.put("buildType", "BUILD");

            StringEntity sentity = new StringEntity(jsonParam.toString(), encoding);//解决中文乱码问题
            sentity.setContentEncoding(encoding);
            sentity.setContentType("application/json");
            httpPut.setEntity(sentity);

            //设置header信息
            //指定报文头【Content-type】,【User-Agent】
            httpPut.setHeader("Content-type", "application/json;charset=utf-8");
            httpPut.setHeader("Authorization", KylinUtil.getPWD(username, passwd));//Basic QURNSU46S1lMSU4=

            //执行请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("ok...");
            } else {
                System.out.println("err..."+statusCode);
            }
            HttpEntity entity = httpResponse.getEntity();
            String response = EntityUtils.toString(entity, encoding);
            System.out.println("response=====》　　" + response);

            //根据uuid查看任务状态
            // http://localhost:7070/kylin/api/jobs/2124e42f-8788-4c2d-901a-6210594d7186
            //	"job_status": "RUNNING",
            //"progress": 50.0
            JSONObject parse = (JSONObject) JSON.parse(response);
            String uuid = parse.getString("uuid");
            String joburl = url +"/jobs" + "/"+ uuid ;
            get_task_metrics(joburl, username,passwd, encoding);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
    }

    //2, 获取任务的执行状态: 每隔1s ,查询一次任务的状态
    public static void get_task_metrics(String url,
                                        String username, String passwd,
                                        String encoding) throws IOException, InterruptedException {
        String job_status ="";
        while (! job_status.equalsIgnoreCase("finished")){
            //根据uuid查看任务状态
            // http://localhost:7070/kylin/api/jobs/2124e42f-8788-4c2d-901a-6210594d7186
            //	"job_status": "RUNNING",
            //"progress": 50.0

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Content-type", "application/json;charset=utf-8");
            httpget.setHeader("Authorization", KylinUtil.getPWD(username,passwd));//Basic QURNSU46S1lMSU4=

            //发送请求
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(httpget);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (null != entity) {
                    //按指定编码转换结果实体为String类型
                    String body = EntityUtils.toString(entity, encoding);
                    JSONObject parse = (JSONObject) JSON.parse(body);
//                String job_status = parse.getString("job_status");
                    job_status = parse.getString("job_status");
                    String progress = parse.getString("progress");

                    //返回json数组
                    System.out.println("job_status: "+job_status+", progress"+progress);
                }
            } finally {
                httpResponse.close();
            }
            Thread.sleep(1000);
        }//while
    }

}
