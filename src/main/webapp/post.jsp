<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<html>
<head>
<title>上传</title>
</head>
<form method="post" enctype="multipart/form-data" name="form1" action="img/upload.do">    
请选择上传的图片
<input type="hidden" name="action" value="upload" />
<input type="hidden" name="compress" value="true" />
<input type="hidden" name="saveorign" value="true" />
 <input type="file" name="uploadFile">    
  <input type="submit" name="Submit" value="上传">    
</form>
</html>