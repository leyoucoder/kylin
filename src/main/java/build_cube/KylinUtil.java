package build_cube;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class KylinUtil {
    //１,获取用户名密码
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


    //2, 日期格式化
    static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String parsetTimeStamp(long timestamp) {
        String formatstr = df.format(new Date(timestamp));
        return formatstr;
    }

    //3,读取配置文件
    static Properties prop=null;
    public static Properties getProperty() throws IOException {

        if (prop ==null){
            prop = new Properties();
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("kylin.properties");
            prop.load(in);
        }
        return prop;
    }

}
