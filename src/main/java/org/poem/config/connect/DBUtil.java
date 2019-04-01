package org.poem.config.connect;

import org.apache.commons.net.ftp.FTPClient;

public class DBUtil {

  /* 静态数据库配置实体对象，程序运行时加载进内存 */

  private static FTPClient config = new FTPClient();
  private static ConnectionPool connPool = new ConnectionPool(config);
  static {//初始化加载配置文件
    Properties prop = new Properties();
    try {
      prop.load(DBUtil.class.getClassLoader().
          getResourceAsStream
              ("com/mypath/db/db.properties"));

      //获取配置文件信息传入config连接池配置对象
      config.setDriverName(prop.getProperty("jdbc.driverName"));
      config.setUrl(prop.getProperty("jdbc.url"));
      config.setUserName(prop.getProperty("jdbc.userName"));
      config.setPassword(prop.getProperty("jdbc.password"));
      //反射加载这个驱动（使用的是JDBC的驱动加载方式）
      Class.forName(config.getDriverName());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void closeConnection(Connection conn) {
    connPool.releaseConnection(conn);
  }

  public static connection gerConnection() {
    return connPool.getConnection();
  }

  public static connection gerCurrentConnection() {
    return connPool.getCurrentConnection();
  }

}
