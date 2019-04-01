public class ConnectionPool{

 private PoolConfig config;//连接池的配置对象

 private int count;//记录连接池的连接数

 private boolean isActive;//连接池是否被激活

 //空闲连接集合

 private Vector<Connection> freeConn = new Vector<Connection>();

 //正在使用的连接集合

 private Vector<Connection> userConn = new Vector<Connection>();

 //同一个线程无论请求多少次都使用同一个连接（使用ThreadLocal确保）

 //每一个线程都私有一个连接

 private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

 /*

 初始化连接池配置

 */

 public ConnectionPool(PoolConfig config){

     this.config = config;

 }

 

 /*

 数据库连接池初始化

 */

 public void init(){

     for(int i=0;i<config.getInitConn();i++){//建立初始连接

         //获取连接对象

         Connection conn;

         try{

             conn = getNewConnection();

             freeConn.add(conn);

             count++;

         }catche(SQLException e){

             e.printStackTrace();

         }

         isActive = true;//连接池激活

     }

 }

 

 /*

 获取新数据库连接

 */

 private synchronized Connection getNewConnection() throws SQLException{

     Connection conn = null;

     conn = DriverManager.getConnection(config.getUrl(),

                                       config.getUserName(),

                                       config.getPassword());

     return conn;

 }

 

 /*

 从连接池获取连接

 */

 public synchronized Conenction getConnection(){

     Connection conn = null;

     //当前连接总数小于配置的最大连接数才去获取

     if(count<config.getMaxActiveConn()){

         //空闲集合中有连接数

         if(freeConn.size()>0){

             conn = freeConn.get(0);//从空闲集合中取出

             freeConn.remove(0);//移除该连接

         }else{

             conn = getNewConnection();//拿到新连接

             count++;

         }

         if(isEnable(conn)){

             useConn.add(conn);//添加到已经使用的连接

         }else{

             count--;

             conn = getConnection();//递归调用到可用的连接

         }

     }else{//当达到最大连接数，只能阻塞等待

         wait(config.getWaitTime());//线程睡眠了一段时间

         conn = getConnection();//递归调用

     }catch(Exception e){

         e.printStackTrace();

     }

     //将获取的conn设置到本地变量ThreadLocal

     threadLocal.set(conn);

     return conn;

 }

 

 /*

 把用完的连接放回连接池集合Vector中

 */

 public synchronized void releaseConnection(Connection conn){

     if(isEnable(conn)){

         if(freeConn.size()<config.getMaxConn()){//空闲连接数没有达到最大

             freeConn.add(conn);//放回集合

         }else{

             conn.close();

         }

     }

     useConn.remove(conn);

     count--;

     threadLocal.remove();

     notifyAll();//放回连接池后说明有连接可用，唤醒阻塞的线程获取连接

 }

 

 /*

 获取当前线程的本地变量连接

 */

 public Connection getCurrentConnection(){

     return threadLocal.get();    

 }

 

 /*

 判断该连接是否可用

 */

 private boolean isEnable(Connection conn){

     if(conn == null){

         return false;

     }

     if(conn.isClosed()){

         return false;

     }

     return true;

 }

}