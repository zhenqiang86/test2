package com.iflashbuy.baseservice.upload;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zq
 * @description mvc service layer 
 */
@Service
public class UploadService {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadFileAction.class);
	
	@Autowired
	private UploadDao uploadDao;
	
	/**
	 * @Description 保存上传的图片并返回主键
	 * @param resource 待保存的图片信息
	 * @return long 主键
	 */
	public long saveResouce(Resource resource) throws Exception{
		return uploadDao.saveResouce(resource);
	}
	
	/**
	 * @Description 保存上传的图片和压缩后的图片构成的一个列表，在此方法进行事务管理，要么同时成功，要么同时失败。失败抛出异常，并记录日志。
	 * @param lst 
	 * @return 主键数组
	 */
	public long [] saveResouceList(List<Resource> lst) throws Exception {
		long []iids=new long[2];
		int cnt=0;
		for(Resource r : lst){
			iids[cnt++]=uploadDao.saveResouce(r);
		}
		return iids;
	}

	public Resource findById(long id) {
		return uploadDao.findById(id).get(0);
	}
}
