package it.uniba.dao;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//final class can't be extended
//Access Modifiers = default = only same package, no subpackage
class DbSingleton {
	private static volatile Connection connSna = null;
	private static volatile Connection connSnaSalted = null;
	private static volatile DbSingleton instance = null;

	
	private DbSingleton() {
		
		//Avoid reflection
		if(connSna != null) {
			throw new RuntimeException("Use getConnectionSna() method to create");
		}
		if(connSnaSalted != null) {
			throw new RuntimeException("Use getConnectionSnaSalted() method to create");
		}
		if (instance != null) {
			throw new RuntimeException("Use getInstance() method to create");
		}
	}

	//Singleton instance of ConnectorThreadSafe
	public static DbSingleton getInstance() {
		if (instance == null) {
			synchronized (DbSingleton.class) {
				if (instance == null) {

					instance = new DbSingleton();

				}

			}
		}
		return instance;
	}
	
	//Use singleton instance of ConnectorThreadSafe to get the connection
	public Connection getConnectionSna() {
		if (connSna == null) {
			synchronized (DbSingleton.class) {
				if (connSna == null) {
					try {
						Context initCtx = new InitialContext();
						Context envCtx = (Context) initCtx.lookup("java:comp/env");
						DataSource ds = (javax.sql.DataSource) envCtx.lookup("jdbc/sna");

						connSna = ds.getConnection();
					} catch (SQLException | NamingException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}

		return connSna;
	}
	
	public Connection getConnectionSnaSalted() {
		if (connSnaSalted == null) {
			synchronized (DbSingleton.class) {
				if (connSnaSalted == null) {
					try {
						Context initCtx = new InitialContext();

						Context envCtx = (Context) initCtx.lookup("java:comp/env");
						DataSource ds = (javax.sql.DataSource) envCtx.lookup("jdbc/sna_salted");

						connSnaSalted = ds.getConnection();
					} catch (SQLException | NamingException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}

		return connSnaSalted;
	}



}