public class JdbcUtil {
    //私有化工具类
    private JdbcUtil(){}
    //私有化数据源
    private  static DataSource dataSource;
    //将Connection对象与本地线程绑定
    private static ThreadLocal<Connection> connection=new ThreadLocal<>();
    //获取DataSource数据源
    static{
        //创建properties集合，用于存储配置信息
        Properties properties=new Properties();
        //获取InputStream流
        InputStream in=JdbcUtil.class.getClassLoader().getResourceAsStream("druid.properties");
        //加载配置信息
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataSource= DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //放回DataSource对象
    public static DataSource getDataSource(){
        return dataSource;
    }
    //获取Connection对象
    public static Connection getConnection() throws SQLException {
        //获取本地线程中的connection
        Connection con=connection.get();
        //如果con！=null则说明已经开启了事务连接
        if (con!=null){
            return con;
        }
        //否则创建新的连接
        return con=dataSource.getConnection();
    }
    //开启事务
    public static void beginTrasaction() throws SQLException {
        Connection con=connection.get();
        if (con!=null){
            throw new RuntimeException("事务连接重复开启");
        }
        con=getConnection();
        //开启事务
        con.setAutoCommit(false);
        connection.set(con);
    }
    //事务回滚
    public static void rollback() throws SQLException {
        Connection con=connection.get();
        if (con==null){
            throw new RuntimeException("事务未开启");
        }
        //事务回滚
        con.rollback();
        con.close();
        //将连接从本地线程中移出
        connection.remove();
    }
    //事务提交
    public static void commit() throws SQLException {
        Connection con=connection.get();
        if (con==null){
            throw new RuntimeException("事务未开启");
        }
        con.commit();
        con.close();
        connection.remove();
    }
    //释放连接
    private static void releaseConnection(Connection c) throws SQLException {
        Connection con=connection.get();
        //若果为true则一定不是事务连接
        if (con==null){
            c.close();
        }
        if (con!=c){
            c.close();
        }
    }
}
