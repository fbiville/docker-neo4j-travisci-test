package io.github.fbiville.neo4j_jdbc_travisci_docker_test;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuickAndDirtyTest {

    @BeforeClass
    public static void prepareAll() throws ClassNotFoundException {
        Class.forName("org.neo4j.jdbc.Driver");
    }

    @Test
    public void interacts_with_remote_instance() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:neo4j://localhost:7474", "neo4j", "j4oen")) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery("MATCH n RETURN COUNT(n) AS count")) {

                assertThat(results.next(), is(true));
                assertThat(results.getInt("count"), equalTo(0));
                assertThat(results.next(), is(false));
            }

            try (Statement statement = connection.createStatement()) {

                assertThat(statement.execute("CREATE (n:Test {contents:'Hello world!'}) RETURN n"), is(true));
            }

            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery("MATCH n RETURN COUNT(n) AS count")) {

                assertThat(results.next(), is(true));
                assertThat(results.getInt("count"), equalTo(1));
                assertThat(results.next(), is(false));
            }
        }

    }
}
