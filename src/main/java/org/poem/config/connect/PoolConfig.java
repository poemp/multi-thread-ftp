package org.poem.config.connect;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PoolConfig {

/*

数据库jdbc属性

*/

  private long checkPeriod = 1000 * 30;//自检周期
  private String driverName;//数据库的驱动类
  private int initConn = 5;//初始连接数
  private boolean isCheck = false;//数据库连接池是否启用自检机制（间隔一段时间检测连接池状态）

/*

连接池配置

*/
  private int maxActiveConn = 10;//整个连接池（数据库）允许的最大连接数
  private int maxConn = 5;//空闲集合最多的连接数
  private int minConn = 1;//空闲集合中最少连接数
  private String password;//数据库密码
  private String url;//数据库的连接地址
  private String userName;//数据库用户名
  private int waitTime = 1000;//单位毫秒，连接数不够时，线程等待的时间

}
