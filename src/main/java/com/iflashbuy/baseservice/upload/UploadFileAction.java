package com.iflashbuy.baseservice.upload;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Decoder;

import com.iflashbuy.baseservice.util.FastdfsUtil;
import com.iflashbuy.baseservice.util.LoadProperties;

@Controller
@RequestMapping("/img")
public class UploadFileAction implements ServletContextAware {

	private static final Logger logger = LoggerFactory.getLogger(UploadFileAction.class);

	private ServletContext servletContext;

	@Autowired
	private UploadService uploadService;

	/**
	 * @param file
	 *            -- 上传的文件
	 * @param compress
	 *            -- 压缩标志 true means compress
	 * @param cwidth
	 *            -- 压缩的宽度基准
	 * @param cheight
	 *            -- 压缩的高度基准
	 * @param saveorign
	 *            -- 保存原图
	 * @return json对象
	 * @throws Exception
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public Object uploadImg(@RequestParam(value = "uploadFile", required = true) MultipartFile file, @RequestParam(value = "compress", required = false, defaultValue = "false") boolean compress,
			@RequestParam(value = "width", required = false, defaultValue = "0") int cwidth, @RequestParam(value = "height", required = false, defaultValue = "0") int cheight,
			@RequestParam(value = "saveorign", required = false, defaultValue = "false") boolean saveorign, @RequestParam(value = "resType", required = false, defaultValue = "rtImage") String resType) {
		if (file == null || file.isEmpty()) {
			logger.warn("upload img error: the http server get an empty file or the file is null");
			return "{success:1, message:\"upload failure because of empty file\"}";
		}
		String _name1 = file.getOriginalFilename();
		String _name1c = file.getOriginalFilename();
		// 生成10000以内的随机数，加到_name1上，避免性能测试时上传同一张图时命名冲突失败
		long sid = Thread.currentThread().getId();
		Random rand = new Random();
		int ri = rand.nextInt(100);
		String prefix = new Long(sid).toString() + "_" + new Integer(ri).toString();
		_name1 = prefix + _name1;
		String _extName = _name1.substring(_name1.lastIndexOf(".") + 1);
		int ri1 = rand.nextInt(100);
		String prefix1 = new Long(sid).toString() + "_" + new Integer(ri1).toString();
		_name1c = prefix1 + _name1c;//_name1.substring(0, _name1.lastIndexOf(".")) + "_200_200." + _extName;
		String path = LoadProperties.getPropt("tmpdir");
		String viewImagePath = LoadProperties.getPropt("viewImagePath");
		String res = null;

		Resource resource = new Resource();
		resource.setName(_name1);
		resource.setType(_extName);
		resource.setResType(resType);
		resource.setUptime(new Date());
		FileInputStream in = null;
		FileOutputStream out = null;
		FileChannel fin = null;
		FileChannel fout = null;
		try {
			String fp = path + _name1; // means full path
			File f = new File(fp);
			if (!f.exists()) {
				f.createNewFile();
			}
			file.transferTo(f);
			if (compress == false) {
				// byte[] in_b = file.getBytes();
				// String result = FdfsUtil.upload(_name1, in_b);
				String result = FastdfsUtil.upload(f, _name1);
				resource.setSize(f.length());
				resource.setViewUrl(viewImagePath + result);
				resource.setUrl(result);
				long iid = uploadService.saveResouce(resource);
				res = "{success:0, message:\"upload success!\", id:\"" + iid + "\", path:\"" + (viewImagePath + result) + "\",viewPath:\"" + (viewImagePath + result) + "\",\"fileName\":\"" + _name1
						+ "\"}";
			} else {
				String fp1 = path + _name1c; // means full path
				File f1 = new File(fp1);
				if (!f1.exists()) {
					f1.createNewFile();
				}
				in = new FileInputStream(f);
				out = new FileOutputStream(f1);
				fin = in.getChannel();
				fout = out.getChannel();
				fin.transferTo(0, f.length(), fout);
				/*CompressPicUtil cp = new CompressPicUtil();
				cp.compressPic(path, path, _name1, _name1c);*/
				if (saveorign == false) {
					// String result = FdfsUtil.upload(path + _name1);
					String result = FastdfsUtil.upload(f1,_name1c);
					File tmp = new File(path + _name1c);
					resource.setSize(tmp.length());
					resource.setViewUrl(viewImagePath + result);
					resource.setUrl(result);
					long iid = uploadService.saveResouce(resource);
					res = "{success:0, message:\"upload success!\", id:\"" + iid + "\", path:\"" + (viewImagePath + result) + "\", viewPath:\"" + (viewImagePath + result) + "\",\"fileName\":\""
							+ _name1 + "\"}";
				} else {
					String result0 = FastdfsUtil.upload(path + _name1c, _name1c); // 压缩后的资源
					String result = FastdfsUtil.upload(path + _name1, _name1); // 原有的资源

					File tmp0 = new File(path + _name1c);
					resource.setSize(tmp0.length());
					resource.setUrl(result0);
					resource.setViewUrl(viewImagePath + result0);
					resource.setResType("rtSImage");
					Resource resource1 = (Resource) resource.clone();
					File tmp = new File(path + _name1);
					resource1.setSize(tmp.length());
					resource1.setUrl(result);
					resource1.setViewUrl(viewImagePath + result);
					resource1.setResType(resType);
					List<Resource> resLst = new ArrayList<Resource>();
					resLst.add(resource);
					resLst.add(resource1);
					long[] ids = uploadService.saveResouceList(resLst);
					if (ids.length != 2)
						throw new Exception();
					res = "{success:0, message:\"upload success!\", id:\"" + ids[1] + "\", path: \"" + result + "\", viewPath:\"" + (viewImagePath + result) + "\", " + "compressed_id:\"" + ids[0]
							+ "\", compressed_path:\"" + result0 + "\", compressed_viewPath:\"" + (viewImagePath + result0) + "\",\"fileName\":\"" + _name1 + "\"}";
				}
			}
			if(in != null){
				in.close();
			}
			if(out != null){
				out.close();
			}
			if(fin != null){
				fin.close();
			}
			if(fout != null){
				fout.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			res = "{success:1, message:\"upload failure because of IOException\"}";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			res = "{success:1, message:\"upload failure\"}";
		} finally {
			File f0 = new File(path + _name1);
			File f1 = new File(path + _name1c);
			if (f0 != null && f0.exists())
				f0.getAbsoluteFile().delete();
			if (f1 != null && f1.exists())
				f1.getAbsoluteFile().delete();
		}
		return res;
	}

	/**
	 * @param file
	 *            -- 上传的文件
	 * @param compress
	 *            -- 压缩标志 true means compress
	 * @param cwidth
	 *            -- 压缩的宽度基准
	 * @param cheight
	 *            -- 压缩的高度基准
	 * @param saveorign
	 *            -- 保存原图
	 * @return json对象
	 * @throws Exception
	 */
	@RequestMapping("/upload_K")
	@ResponseBody
	public String uploadImg_K(HttpServletRequest req, HttpServletResponse resp, @RequestParam(value = "compress", required = false, defaultValue = "false") boolean compress,
			@RequestParam(value = "width", required = false, defaultValue = "0") int cwidth, @RequestParam(value = "height", required = false, defaultValue = "0") int cheight,
			@RequestParam(value = "saveorign", required = false, defaultValue = "false") boolean saveorign,
			@RequestParam(value = "resType", required = false, defaultValue = "rtImage") String resType, @RequestParam(value = "referer", required = false, defaultValue = "") String referer,
			@RequestParam(value = "newFileName", required = false, defaultValue = "") String newFileName, @RequestParam(value = "fileExt", required = false, defaultValue = "") String fileExt,
			@RequestParam(value = "fileSize", required = false, defaultValue = "") String fileSize) {
		String _name1 = "";
		try {
			newFileName = java.net.URLDecoder.decode(newFileName, "UTF-8");
			_name1 = newFileName;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String _extName = _name1.substring(_name1.lastIndexOf(".") + 1);
		String _name1c = _name1 + "_200_200." + _extName;
		String path = LoadProperties.getPropt("tmpdir");
		String viewImagePath = LoadProperties.getPropt("viewImagePath");
		String res = "";
		Resource resource = new Resource();
		resource.setName(_name1);
		resource.setType(_extName);
		resource.setResType(resType);
		resource.setUptime(new Date());
		try {

			if (compress == false) {
				InputStream in = req.getInputStream();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] data = new byte[Integer.parseInt(fileSize)];
				int count = -1;
				while ((count = in.read(data, 0, Integer.parseInt(fileSize))) != -1)
					outStream.write(data, 0, count);

				data = null;
				byte[] in_b = outStream.toByteArray();
				OutputStream o = new FileOutputStream(path + _name1);
				o.write(in_b);
				o.flush();
				o.close();
				// String result = FdfsUtil.upload(_name1, in_b);
				String result = FastdfsUtil.upload(path + _name1, _name1);
				resource.setSize(in_b.length);
				resource.setViewUrl(viewImagePath + result);
				resource.setUrl(result);
				uploadService.saveResouce(resource);
				res = (viewImagePath + result);
			} else {
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			res = "{success:1, message:\"upload failure because of IOException\"}";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			res = "{success:1, message:\"upload failure\"}";
		} finally {
			File f0 = new File(path + _name1);
			File f1 = new File(path + _name1c);
			if (f0 != null && f0.exists())
				System.out.println(f0.getAbsoluteFile());
				f0.getAbsoluteFile().delete();
			if (f1 != null && f1.exists())
				f1.getAbsoluteFile().delete();
		}
		return res;
	}

	/**
	 * <b>function:</b> 预览图片内容
	 * 
	 * @author hoojo
	 * @createDate 2012-3-30 下午03:25:57
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewImage")
	@ResponseBody
	public ModelAndView viewImage(HttpServletResponse resp, @RequestParam(value = "contentType", required = false, defaultValue = "") String contentType,
			@RequestParam(value = "id", required = false, defaultValue = "") String id) throws Exception {
		if (contentType == null || contentType.length() == 0) {
			contentType = "text/html";
		}
		// File resFile = null;
		OutputStream out = null;
		Resource re = null;
		try {
			re = uploadService.findById(Long.valueOf(id));
			out = new BufferedOutputStream(resp.getOutputStream());
			// FdfsUtil.down(re.getUrl(), out);
			out.write(FastdfsUtil.returnBitMap(re.getViewUrl()));
			resp.setContentType("application/octet-stream");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
		return null;
	}

	/**
	 * @param file
	 *            -- 上传的文件
	 * @param compress
	 *            -- 压缩标志 true means compress
	 * @param cwidth
	 *            -- 压缩的宽度基准
	 * @param cheight
	 *            -- 压缩的高度基准
	 * @param saveorign
	 *            -- 保存原图
	 * @return json对象
	 * @throws Exception
	 */
	@RequestMapping("/uploadImg_phone")
	@ResponseBody
	public String uploadImg_phone(HttpServletRequest req, HttpServletResponse resp, @RequestParam(value = "compress", required = false, defaultValue = "false") boolean compress,
			@RequestParam(value = "width", required = false, defaultValue = "0") int cwidth, @RequestParam(value = "height", required = false, defaultValue = "0") int cheight,
			@RequestParam(value = "saveorign", required = false, defaultValue = "false") boolean saveorign,
			@RequestParam(value = "resType", required = false, defaultValue = "rtImage") String resType, @RequestParam(value = "referer", required = false, defaultValue = "") String referer,
			@RequestParam(value = "newFileName", required = false, defaultValue = "") String newFileName, @RequestParam(value = "fileExt", required = false, defaultValue = "") String fileExt,
			@RequestParam(value = "fileSize", required = false, defaultValue = "") String fileSize, @RequestParam(value = "data", required = false, defaultValue = "") String data) {
		String _name1 = "";
		_name1 = newFileName;
		String _extName = _name1.substring(_name1.lastIndexOf(".") + 1);
		String _name1c = _name1 + "_200_200." + _extName;
		String path = LoadProperties.getPropt("tmpdir");
		String viewImagePath = LoadProperties.getPropt("viewImagePath");
		String res = "";
		Resource resource = new Resource();
		resource.setName(_name1);
		resource.setType(_extName);
		resource.setResType(resType);
		resource.setUptime(new Date());
		try {
			if (compress == false) {
				data = UploadFileAction.imageToString(data);
				byte[] in_b = UploadFileAction.GenerateImage(data);
				OutputStream o = new FileOutputStream(path + _name1);
				o.write(in_b);
				o.flush();
				o.close();
				// String result = FdfsUtil.upload(_name1, in_b);
				String result = FastdfsUtil.upload(path + _name1, _name1);
				resource.setSize(in_b.length);
				resource.setViewUrl(viewImagePath + result);
				resource.setUrl(result);
				uploadService.saveResouce(resource);
				res = (viewImagePath + result);
			} else {
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			res = "{success:1, message:\"upload failure because of IOException\"}";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			res = "{success:1, message:\"upload failure\"}";
		} finally {
			File f0 = new File(path + _name1);
			File f1 = new File(path + _name1c);
			if (f0 != null && f0.exists())
				f0.getAbsoluteFile().delete();
			if (f1 != null && f1.exists())
				f1.getAbsoluteFile().delete();
		}
		return res;
	}

	@SuppressWarnings("restriction")
	public static byte[] GenerateImage(String imgStr) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null)
			// 图像数据为空
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] bytes = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 转换接受手机端 或发送手机端的格式
	 * 
	 * @param image
	 * @return
	 */
	public static String imageToString(String image) {
		if (image != null && image.length() > 0) {
			image = image.replaceAll(" ", "+");
		}
		return image;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}
}
