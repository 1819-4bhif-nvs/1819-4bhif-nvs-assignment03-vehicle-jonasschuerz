package at.htl.vehicle;

import org.apache.derby.client.am.SqlException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sound.midi.SysexMessage;
import java.sql.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VehicleTest {
    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    public static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db;create=true";
    public static final String USER = "app";
    public static final String PASSWORD = "app";
    public static Connection conn;

    @BeforeClass
    public static void initJdbc(){
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING,USER,PASSWORD);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Verbindung zur Datenbank nicht möglich\n"+ e.getMessage()+"\n");
            System.exit(1);
        }

        //Erstellen der Table Vehicle
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE vehicle(" +
                    "ID INT CONSTRAINT vehicle_pk PRIMARY KEY," +
                    "brand VARCHAR(255) NOT NULL," +
                    "type VARCHAR(255) NOT NULL" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @AfterClass
    public static void teardownJdbc(){
        //Tabelle löschen
        try {
            conn.createStatement().execute("DROP TABLE VEHICLE");
            System.out.println("Tabelle Vehicle gelöscht");
        } catch (SQLException e) {
            System.out.println("Tabelle Vehicle konnte nicht gelöscht werden:\n"
                    + e.getMessage());
        }


        try {
            if (conn != null && !conn.isClosed()){
                conn.close();
                System.out.println("Good bye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void ddl(){
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE vehicle(" +
                    "ID INT CONSTRAINT vehicle_pk PRIMARY KEY," +
                    "brand VARCHAR(255) NOT NULL," +
                    "type VARCHAR(255) NOT NULL" +
                    ")";

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    @Test
    public void dml(){
        //Daten einfügen
        int counterInserts = 0;

        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO vehicle (id, brand, type) VALUES (1,'Opel','Commodore')";
            counterInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (2,'Opel','Kapitän')";
            counterInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (3,'Opel','Kadett')";
            counterInserts += stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        assertThat(counterInserts,is(3));


        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, brand, type FROM vehicle");
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Commodore"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kapitän"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kadett"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
