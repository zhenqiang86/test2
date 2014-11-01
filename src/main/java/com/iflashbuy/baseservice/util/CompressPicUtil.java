package com.iflashbuy.baseservice.util;

/**
 *  image compressing util class. supported images file types: jpg,bmp,png,gif
 */
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

 public class CompressPicUtil { 
	 private File file = null; 
	 private String inputDir; 
	 private String outputDir; 
	 private String inputFileName; 
	 private String outputFileName; 
	 private int outputWidth = 100; 
	 private int outputHeight = 100; 
	 private boolean proportion = true;
	 public CompressPicUtil() { 
		 inputDir = ""; 
		 outputDir = ""; 
		 inputFileName = ""; 
		 outputFileName = ""; 
		 outputWidth = 100; 
		 outputHeight = 100; 
	 } 
	 public void setInputDir(String inputDir) { 
		 this.inputDir = inputDir; 
	 } 
	 public void setOutputDir(String outputDir) { 
		 this.outputDir = outputDir; 
	 } 
	 public void setInputFileName(String inputFileName) { 
		 this.inputFileName = inputFileName;
	 } 
	 public void setOutputFileName(String outputFileName) { 
		 this.outputFileName = outputFileName; 
	 } 
	 public void setOutputWidth(int outputWidth) {
		 this.outputWidth = outputWidth; 
	 } 
	 public void setOutputHeight(int outputHeight) { 
		 this.outputHeight = outputHeight; 
	 } 
	 public void setWidthAndHeight(int width, int height) { 
		 this.outputWidth = width;
		 this.outputHeight = height; 
	 } 
	 
	 /* 
	  * pic size 
	  */ 
	 public long getPicSize(String path) { 
		 file = new File(path); 
		 return file.length(); 
	 }
	 
	 /* 
	  * pic compress core method 
	  */ 
	 public String compressPic() { 
		 try { 
			 file = new File(inputDir + inputFileName); 
			 if (!file.exists()) { 
				 return ""; 
			 } 
			 Image img = ImageIO.read(file); 
			 
			 if (img.getWidth(null) == -1) {
				 System.out.println(" can't read,retry!" + "<BR>"); 
				 return "no"; 
			 } else { 
				 int newWidth; int newHeight; 

				 if (this.proportion == true) { 
					 double rate1 = ((double) img.getWidth(null)) / (double) outputWidth; 
					 double rate2 = ((double) img.getHeight(null)) / (double) outputHeight; 
					 double rate = rate1 < rate2 ? rate1 : rate2; 
					 newWidth = (int) (((double) img.getWidth(null)) / rate); 
					 newHeight = (int) (((double) img.getHeight(null)) / rate); 
				 } else { 
					 newWidth = outputWidth; 
					 newHeight = outputHeight; 
				 } 
			 	BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB); 
			 	
			 	tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
			 	FileOutputStream out = new FileOutputStream(outputDir + outputFileName);

			 	JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out); 
			 	encoder.encode(tag); 
			 	out.close(); 
			 } 
		 } catch (IOException ex) { 
			 ex.printStackTrace(); 
		 } 
		 return "ok"; 
	} 
 	public String compressPic (String inputDir, String outputDir, String inputFileName, String outputFileName) { 
 		this.inputDir = inputDir; 
 		this.outputDir = outputDir; 
 		this.inputFileName = inputFileName; 
 		this.outputFileName = outputFileName; 
 		return compressPic(); 
 	} 
 	public String compressPic(String inputDir, String outputDir, String inputFileName, String outputFileName, int width, int height, boolean gp) { 
 		this.inputDir = inputDir; 
 		this.outputDir = outputDir; 
 		this.inputFileName = inputFileName; 
 		this.outputFileName = outputFileName; 
 		setWidthAndHeight(width, height); 
 		this.proportion = gp; 
 		return compressPic(); 
 	} 
 	
 	public static void main(String[] arg) { 
 		CompressPicUtil mypic = new CompressPicUtil(); 
 		System.out.println("输入的图片大小：" + mypic.getPicSize("C:\\Documents and Settings\\Administrator\\桌面\\77.jpg")/1024 + "KB"); 
		mypic.compressPic("C:\\Documents and Settings\\Administrator\\桌面\\", "C:\\Documents and Settings\\Administrator\\桌面\\", "77.jpg", "77_120.jpg", 120, 120, true); 

//			
// 		int count = 0; // 记录全部图片压缩所用时间
// 		for (int i = 0; i < 100; i++) { 
// 			int start = (int) System.currentTimeMillis();	// 开始时间 
// 			mypic.compressPic("C:\\Documents and Settings\\Administrator\\桌面\\", "C:\\Documents and Settings\\Administrator\\桌面\\", "77.jpg", "r1"+i+".jpg", 120, 120, true); 
// 			int end = (int) System.currentTimeMillis(); // 结束时间 
// 			int re = end-start; // 但图片生成处理时间 
// 			count += re; System.out.println("第" + (i+1) + "张图片压缩处理使用了: " + re + "毫秒"); 
// 			System.out.println("输出的图片大小：" + mypic.getPicSize("e:\\test\\r1"+i+".jpg")/1024 + "KB"); 
// 		}
// 		System.out.println("总共用了：" + count + "毫秒"); 
 	} 
 }


