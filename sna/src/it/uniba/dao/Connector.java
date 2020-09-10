package it.uniba.dao;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//final class can't be extended
//Access Modifiers = default = only same package, no subpackage
final class Connector {
	private static Connection connSna;
	private static Connection connSnaSalted;

	private Connector() {
	}

	// final method can't be overridden by a subclass
	 static final Connection getConnectionSna() throws SQLException, NamingException{

		if (connSna == null) {

			Context initCtx = new InitialContext();

			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (javax.sql.DataSource) envCtx.lookup("jdbc/sna");

			connSna = ds.getConnection();

		}
		return connSna;
	}

	 static final  Connection getConnectionSnaSalted() throws Exception {

		if (connSnaSalted == null) {

			Context initCtx = new InitialContext();

			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (javax.sql.DataSource) envCtx.lookup("jdbc/sna_salted");

			connSnaSalted = ds.getConnection();

		}
		return connSnaSalted;

	}

}