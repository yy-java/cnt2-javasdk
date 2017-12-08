package com.yy.cnt.recipes.db.tester.mysql;

import com.yy.cnt.recipes.db.tester.ConnectionTester;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



public class MySQLConnectionTester implements ConnectionTester {

    @Override
    public boolean isActivation(Connection conn) {
        boolean status=true;
        Statement pingStatement = null;
        try {
            pingStatement = conn.createStatement();
            pingStatement.executeQuery("SELECT 1").close();
        }catch(Exception e){
            status =false;
        } finally {
            if (pingStatement != null) {
                try {
                    pingStatement.close();
                } catch (SQLException e) {
                }
            }
        }
        return status;
    }

}
