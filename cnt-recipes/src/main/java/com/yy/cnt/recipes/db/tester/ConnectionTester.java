package com.yy.cnt.recipes.db.tester;

import java.sql.Connection;

public interface ConnectionTester {
    boolean isActivation(Connection conn);
}
