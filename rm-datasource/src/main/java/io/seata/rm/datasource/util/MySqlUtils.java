/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.rm.datasource.util;

import javax.sql.XAConnection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * @author wang.liang
 */
public class MySqlUtils {
    static Class<?>       utilClass;
    static boolean        utilClassError = false;
    static boolean        utilClass_isJdbc4 = false;

    static Class<?>       class_5_connection = null;
    static Method         method_5_getPinGlobalTxToPhysicalConnection = null;
    static Class<?>       class_5_suspendableXAConnection = null;
    static Constructor<?> constructor_5_suspendableXAConnection = null;
    static Class<?>       class_5_JDBC4SuspendableXAConnection = null;
    static Constructor<?> constructor_5_JDBC4SuspendableXAConnection = null;
    static Class<?>       class_5_MysqlXAConnection = null;
    static Constructor<?> constructor_5_MysqlXAConnection = null;

    volatile static Class<?>       class_6_connection = null;
    volatile static Method         method_6_getPropertySet = null;
    volatile static Method         method_6_getBooleanProperty = null;
    volatile static Method         method_6_getValue = null;
    volatile static boolean        method_6_getValue_error = false;

    volatile static Class<?>       class_6_suspendableXAConnection = null;
    volatile static Method         method_6_getInstance = null;
    volatile static boolean        method_6_getInstance_error = false;
    volatile static Method         method_6_getInstanceXA = null;
    volatile static boolean        method_6_getInstanceXA_error = false;
    volatile static Class<?>       class_6_JDBC4SuspendableXAConnection = null;


    public static XAConnection createXAConnection(Driver driver, Connection physicalConn) throws SQLException {
        final int major = driver.getMajorVersion();
        if (major == 5) {
            if (utilClass == null && !utilClassError) {
                try {
                    utilClass = Class.forName("com.mysql.jdbc.Util");

                    Method method = utilClass.getMethod("isJdbc4");
                    utilClass_isJdbc4 = (Boolean) method.invoke(null);

                    class_5_connection = Class.forName("com.mysql.jdbc.Connection");
                    method_5_getPinGlobalTxToPhysicalConnection = class_5_connection.getMethod("getPinGlobalTxToPhysicalConnection");

                    class_5_suspendableXAConnection = Class.forName("com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection");
                    constructor_5_suspendableXAConnection = class_5_suspendableXAConnection.getConstructor(class_5_connection);

                    class_5_JDBC4SuspendableXAConnection = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection");
                    constructor_5_JDBC4SuspendableXAConnection = class_5_JDBC4SuspendableXAConnection.getConstructor(class_5_connection);

                    class_5_MysqlXAConnection = Class.forName("com.mysql.jdbc.jdbc2.optional.MysqlXAConnection");
                    constructor_5_MysqlXAConnection = class_5_MysqlXAConnection.getConstructor(class_5_connection, boolean.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    utilClassError = true;
                }
            }

            try {
                boolean pinGlobTx = (Boolean) method_5_getPinGlobalTxToPhysicalConnection.invoke(physicalConn);
                if (pinGlobTx) {
                    if (!utilClass_isJdbc4) {
                        return (XAConnection) constructor_5_suspendableXAConnection.newInstance(physicalConn);
                    }

                    return (XAConnection) constructor_5_JDBC4SuspendableXAConnection.newInstance(physicalConn);
                }

                return (XAConnection) constructor_5_MysqlXAConnection.newInstance(physicalConn, Boolean.FALSE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (major == 6 || major == 8) {
            if (method_6_getValue == null && !method_6_getValue_error) {
                try {
                    class_6_connection = Class.forName("com.mysql.cj.api.jdbc.JdbcConnection");
                } catch (Throwable t) {
                }

                try {
                    // maybe 8.0.11 or higher version, try again with com.mysql.cj.jdbc.JdbcConnection
                    if (class_6_connection == null) {
                        class_6_connection = Class.forName("com.mysql.cj.jdbc.JdbcConnection");
                        method_6_getPropertySet = class_6_connection.getMethod("getPropertySet");
                        method_6_getBooleanProperty = Class.forName("com.mysql.cj.conf.PropertySet").getMethod("getBooleanProperty", String.class);
                        method_6_getValue = Class.forName("com.mysql.cj.conf.RuntimeProperty").getMethod("getValue");
                    }
                    else {
                        method_6_getPropertySet = class_6_connection.getMethod("getPropertySet");
                        method_6_getBooleanProperty = Class.forName("com.mysql.cj.conf.PropertySet").getMethod("getBooleanProperty", String.class);
                        method_6_getValue = Class.forName("com.mysql.cj.conf.RuntimeProperty").getMethod("getValue");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    method_6_getValue_error = true;
                }
            }

            try {
                boolean pinGlobTx;
                try {
                    pinGlobTx = (Boolean) method_6_getValue.invoke(
                            method_6_getBooleanProperty.invoke(
                                    method_6_getPropertySet.invoke(physicalConn)
                                    , "pinGlobalTxToPhysicalConnection"
                            )
                    );
                } catch (Exception e) {
                    pinGlobTx = false;
                }

                if (pinGlobTx) {
                    try {
                        if (method_6_getInstance == null && !method_6_getInstance_error) {
                            class_6_suspendableXAConnection = Class.forName("com.mysql.cj.jdbc.SuspendableXAConnection");
                            method_6_getInstance = class_6_suspendableXAConnection.getDeclaredMethod("getInstance", class_6_connection);
                            method_6_getInstance.setAccessible(true);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        method_6_getInstance_error = true;
                    }
                    return (XAConnection) method_6_getInstance.invoke(null, physicalConn);
                } else {
                    try {
                        if (method_6_getInstanceXA == null && !method_6_getInstanceXA_error) {
                            class_6_JDBC4SuspendableXAConnection = Class.forName("com.mysql.cj.jdbc.MysqlXAConnection");
                            method_6_getInstanceXA = class_6_JDBC4SuspendableXAConnection.getDeclaredMethod("getInstance", class_6_connection, boolean.class);
                            method_6_getInstanceXA.setAccessible(true);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        method_6_getInstanceXA_error = true;
                    }
                    return (XAConnection) method_6_getInstanceXA.invoke(null, physicalConn, Boolean.FALSE);
                }
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            } catch (Exception e) {
                e.printStackTrace();
                method_6_getInstance_error = true;
            }
        }

        throw new SQLFeatureNotSupportedException();
    }
}
