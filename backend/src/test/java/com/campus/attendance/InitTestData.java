package com.campus.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.File;

@SpringBootTest
public class InitTestData {

    @Autowired
    private DataSource dataSource;

    @Test
    public void runScript() throws Exception {
        File script = new File("../sql/test-data.sql");
        if (!script.exists()) {
            System.out.println("Script not found at: " + script.getAbsolutePath());
            return;
        }
        try (var conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new FileSystemResource(script));
            System.out.println("Test data initialized successfully!");
        }
    }
}
