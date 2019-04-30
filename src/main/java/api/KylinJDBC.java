package api;

import org.apache.kylin.jdbc.Driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class KylinJDBC {
    public static void main(String[] args) throws Exception {
        Driver driver = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
        Properties info = new Properties();
        info.put("user", "ADMIN");
        info.put("password", "KYLIN");

        Connection conn = driver.connect("jdbc:kylin://218.245.1.135:7070/kylin_demo", info);
        Statement state = conn.createStatement();
//        String sql="select * from KYLIN_ACCOUNT";
        String sql="SELECT KYLIN_ACCOUNT.ACCOUNT_BUYER_LEVEL ,count(*) as data FROM KYLIN_ACCOUNT  group by KYLIN_ACCOUNT.ACCOUNT_BUYER_LEVEL  order by data desc";
        ResultSet resultSet = state.executeQuery(sql);

        int count=0;
        while (resultSet.next()) {
          System.out.println(resultSet.getString(1)+","+
                  resultSet.getString(2));
          count++;
        }
        System.out.println("count == "+ count);
    }
}
