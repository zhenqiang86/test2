package com.iflashbuy.baseservice.upload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.iflashbuy.baseservice.util.DataSourceSupport;

/**
 * @author Administrator
 *
 */
@Repository
public class UploadDao {

	private static final Logger logger = LoggerFactory.getLogger(UploadDao.class);
	
	@Autowired
	private DataSourceSupport dataSource;
		
	/**
	 * @description insert resource into ent_upload and returns the primary key
	 * @param Resource resource
	 * @return long primary key
	 */
	public long saveResouce(final Resource r) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = dataSource.getOracleTemplate();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			    	  String sql = "insert into ENT_UPLOAD(ID,NAME,TYPE,UPLOADER,UPTIME,URL,SIZES,DESCS,RESTYPE,VIEW_URL) "
			  				+ "values(ENT_UPLOAD_SEQ.nextval,'"+r.getName()+"','"+r.getType()+"',"+r.getUploader()+",Sysdate,'"+r.getUrl()+"',"+r.getSize()+","+r.getDesc()+",'"+r.getResType()+"','"+r.getViewUrl()+"')";
				    PreparedStatement ps =con.prepareStatement(sql,new String[]{"id"});
				    con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				    return ps;
			    }
			   }, keyHolder);
			  } catch (DataAccessException e) {
				  logger.error(e.getMessage(),e);
				  throw e;
			  }
			  logger.info("id============================" + keyHolder.getKey().intValue());
			  return keyHolder.getKey().intValue();
		}

	public List<Resource> findById(long id) {
		 JdbcTemplate jdbcTemplate = dataSource.getOracleTemplate();
		 List<Resource> result = new ArrayList<Resource>();
			CardCallbackHandler handler = new CardCallbackHandler(result);
			jdbcTemplate.query("select * from ENT_UPLOAD where id ="+id, handler);
		return handler.getResult();
	}
	
	private static class CardCallbackHandler implements RowCallbackHandler {
		private List<Resource> result = new ArrayList<Resource>();
		
		public CardCallbackHandler(List<Resource> result){
			this.result = result;
		}

		public void processRow(ResultSet rs) throws SQLException {
			Resource entity = new Resource();
			entity.setId(Long.valueOf(rs.getString("id")));
			entity.setUrl(rs.getString("URL"));
			//新添加
			entity.setViewUrl(rs.getString("ViEW_URL"));
			result.add(entity);
		}

		public List<Resource> getResult() {
			return result;
		}
		
	}
}
