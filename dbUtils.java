public class dbUtils<T> {
    private DataSource dataSource;

    public dbUtils(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public dbUtils() {
    }

    //增删改查
    public int update(String sql, Object... para) {
        Connection connection = null;
        PreparedStatement statement = null;
        int i = 0;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            for (int j= 0; j< para.length; j++) {
                statement.setObject(j + 1, para[j]);
            }
            i = statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
    }

    //查询
    public <T> T query(String sql, ClassChange<T> change, Object... para) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet  resultSet =null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < para.length; i++) {
                statement.setObject(i + 1, para[i]);
            }
            resultSet = statement.executeQuery();
            return change.change(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (resultSet!=null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement!=null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
interface ClassChange<T> {
    T change(ResultSet result);
}
