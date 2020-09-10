module sna {
	exports it.uniba.utils;
	exports it.uniba.servlets;
	exports it.uniba.dao;

	requires java.naming;
	requires java.sql;
	requires java.xml;
	requires org.apache.tika.core;
	requires org.apache.commons.codec;
	requires transitive javax.servlet.api;
}