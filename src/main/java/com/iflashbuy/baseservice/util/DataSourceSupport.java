package com.iflashbuy.baseservice.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class DataSourceSupport {
	/**
	 * MYSQL jdbc
	 */
	private JdbcTemplate mysqlTemplate;
	
	/**
	 * ORACLE jdbc
	 */
//	private JdbcTemplate oracleTemplate;
	
	private JdbcTemplate oracleTemplate;

	/**
	 * GET MYSQL JDBC
	 * @return
	 */
	public JdbcTemplate getMysqlTemplate() {
		return mysqlTemplate;
	}
	
	/**
	 * SET MYSQL JDBC
	 * @param mysqlTemplate
	 */
	public void setMysqlTemplate(JdbcTemplate mysqlTemplate) {
		this.mysqlTemplate = mysqlTemplate;
	}

	/**
	 * GET ORACLE JDBC
	 * @return
	 */
	public JdbcTemplate getOracleTemplate() {
		return oracleTemplate;
	}

	/**
	 * SET ORACLE JDBC
	 * @param oracleTemplate
	 */
	public void setOracleTemplate(JdbcTemplate oracleTemplate) {
		this.oracleTemplate = oracleTemplate;
	}
}
