package ua.com.zmike.userservice.testUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class TestDbManager {

    private final Connection connection;

    public TestDbManager(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public void initDataBase() {
        executeScript("init-db.sql");
    }

    public void fillTables() {
        executeScript("fill-db.sql");
    }

    public void cleanTables() {
        executeScript("clean-db.sql");
    }

    private void executeScript(String location) {
        ScriptUtils.executeSqlScript(connection, new DefaultResourceLoader().getResource(location));
    }
}
