//单一文件上传处理
function getBrowserType() {  
   var browser = {};  
   var userAgent = navigator.userAgent.toLowerCase();  
   var s;  
   (s = userAgent.match(/msie ([\d.]+)/))  
           ? browser.ie = s[1]  
           : (s = userAgent.match(/firefox\/([\d.]+)/))  
                   ? browser.firefox = s[1]  
                   : (s = userAgent.match(/chrome\/([\d.]+)/))  
                            ? browser.chrome = s[1]  
                            : (s = userAgent.match(/opera.([\d.]+)/))  
                                    ? browser.opera = s[1]  
                                    : (s = userAgent  
                                            .match(/version\/([\d.]+).*safari/))  
                                            ? browser.safari = s[1]  
                                            : 0;  
    var oType = "";  
    if (browser.ie) {  
    	oType = "ie";
    } else if (browser.firefox) {  
    	oType = "firefox";
    } else if (browser.chrome) {  
    	oType = "chrome";
    } else if (browser.opera) {  
    	oType = "opera";
    } else if (browser.safari) {  
    	oType = "safari"; 
    } else {  
    	oType = '未知浏览器';  
    }  
    return oType;  
} 

function fileQueueErrorSingle(file, errorCode, message) {
	try {
		var errorName = "";
		if (errorCode === SWFUpload.errorCode_QUEUE_LIMIT_EXCEEDED) {
			errorName = "You have attempted to queue too many files.";
		}
		if (errorName !== "") {
			alert("errorName: "+errorName);
			return;
		}
		
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			alert("文件大小为0");
			return;
			break;
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			alert("超出文件最大限制！ 文件实际大小："+file.size+"byte; 最大允许："+this.customSettings.file_size_limit);
			return;
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
		default:
			//alert("只允许上传单个文件");
			//return ;
			break;
		}

	} catch (ex) {
		this.debug(ex);
	}
}

/**
 * 当文件选择对话框关闭消失时，如果选择的文件成功加入上传队列，
 * 那么针对每个成功加入的文件都会触发一次该事件（N个文件成功加入队列，就触发N次此事件）。
 * @param {} file
 * id : string,			    // SWFUpload控制的文件的id,通过指定该id可启动此文件的上传、退出上传等
 * index : number,			// 文件在选定文件队列（包括出错、退出、排队的文件）中的索引，getFile可使用此索引
 * name : string,			// 文件名，不包括文件的路径。
 * size : number,			// 文件字节数
 * type : string,			// 客户端操作系统设置的文件类型
 * creationdate : Date,		// 文件的创建时间
 * modificationdate : Date,	// 文件的最后修改时间
 * filestatus : number		// 文件的当前状态，对应的状态代码可查看SWFUpload.FILE_STATUS }
 */
var thatis=null;
function fileQueuedSingle(file){
	thatis=this;
	initUploadingProgress(this,file.size,file.id,file.name,"等待上传");
}
function fileDialogCompleteSingle(numFilesSelected, numFilesQueued) {
	try {
		if (numFilesQueued > 0) {
			this.startUpload();
		}
	} catch (ex) {
		this.debug(ex);
	}
}
function uploadProgressSingle(file, bytesLoaded) {
	try {
		var percent = Math.ceil((bytesLoaded / file.size) * 100);

		if (percent >= 100) {
			loadingProgress(file.id,"100%");
		} else {
			loadingProgress(file.id,percent+"%");
		}	
	} catch (ex) {
		alert("exc");
		this.debug(ex);
	}
}

function uploadSuccessSingle(file, serverData) {
	try {
		var data = eval("(" + serverData + ")");
		data = eval("(" + data.result + ")");
		var rid=data.id;
		var fullpath=data.viewPath;
		var fileid=file.id;
		var imgboxid=this.customSettings.holder_box_id;
		var rtImage=this.customSettings.rtImage;
		var urlShowId=this.customSettings.urlShowId;
		var urlValueId=this.customSettings.urlValueId;
		//重新生成swf对象
		reinstaninate(this);		
		if (data.success == 0 || data.success == "0" ) {		
			uploadSuccessRecall(fileid, fullpath, imgboxid, rid, urlShowId, urlValueId, rtImage);
		} else {
			alert("上传文件失败！ Error:"+data.message);
		}
	} catch (ex) {
		this.debug(ex);
	}
}
function reinstaninate(swfObject){
	var imgboxid=swfObject.customSettings.holder_box_id;
	var sizelimit=swfObject.customSettings.file_size_limit;
	var filetype=swfObject.customSettings.file_types;
	var rtImage=swfObject.customSettings.rtImage;
	var urlShowId=swfObject.customSettings.urlShowId;
	var urlValueId=swfObject.customSettings.urlValueId;
	swfObject.destroy( );
	new SWFInitializer({
		imageboxId: imgboxid,
		fileTypes: filetype,
		sizeLimit: sizelimit,
		rtImage: rtImage,
		urlShowId: urlShowId,
		urlValueId: urlValueId
	}).init();
}
function uploadSuccessRecall(fileId, fullpath, imgboxid, id, urlShowId, urlValueId, rtImage){
	if(rtImage=="rtImage") {
		$("#"+imgboxid).find(".uploading").html("");
		$("#"+imgboxid).find("img.showimg").css("display","block").attr("src",fullpath);
		$("#"+imgboxid).find("img.showimg").css("display","block").attr("id",id);
		var html="<img class=\"deleteImg\" src=\""+ctxPath+"/resource/image/validate-err2.png\" style=\"position: absolute; right: 6px; top: 6px; cursor: pointer;\"/>";
		$("#"+imgboxid).append(html);
		$("#"+imgboxid).find("img.deleteImg").mouseover(function(){
			$(this).attr("src",ctxPath+"/resource/image/validate-err.png");	
		}).mouseout(function(){
			$(this).attr("src",ctxPath+"/resource/image/validate-err2.png");	
		}).click(function(){
			$("#"+imgboxid).find("img.showimg").css("display","none").attr("src","");
			$("#"+imgboxid).find("img.showimg").css("display","none").attr("id","");
			$(this).remove();
		});	
	} 
	if(rtImage=="rtVideo"){
		$("#"+imgboxid).find(".uploading").html("");
		if(urlShowId!=null && urlShowId!="" && urlShowId!="undefined"){
			$("#"+urlShowId).val(fullpath);
			$("#"+urlValueId).val(id+"|"+fullpath);
		}
	}
}
function loadingProgress(fileId,message){
	$("#"+thatis.customSettings.holder_box_id).find(".uploading span.progress").html(message);
}

//初如化上传的展示界面
function initUploadingProgress(swfObject){
	try {
		$("#"+swfObject.customSettings.holder_box_id).find("img.showimg").css("display","none");
		$("#"+swfObject.customSettings.holder_box_id).find(".uploading").css("display","block");
		var html="<img src=\""+ctxPath+"/resource/js/swfupload/loading23.gif\" />"
					+"<p>上传中<span class=\"progress\">0%</span></p>";
		$("#"+swfObject.customSettings.holder_box_id).find(".uploading").html(html);
	} catch (ex) {
		alert("initUploadingProgress 出错");
	}
}
function uploadCompleteSingle(file) {
	try {
		/*  I want the next upload to continue automatically so I'll call startUpload here */
		if (this.getStats().files_queued > 0) {
			this.startUpload();
		} else {
			
		}
	} catch (ex) {
		this.debug(ex);
	}
}
function uploadErrorSingle(file, errorCode, message) {
	try {
		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			alert("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			reinstaninate(this);	
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			alert("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);	
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			alert("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			alert("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			alert("Error Code: FILE_CANCELLED, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.SPECIFIED_FILE_ID_NOT_FOUND:
			alert("Error Code: SPECIFIED_FILE_ID_NOT_FOUND, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.MISSING_UPLOAD_URL:
			alert("Error Code: MISSING_UPLOAD_URL, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			alert("Error Code: UPLOAD_STOPPED, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			alert("Error Code: UPLOAD_LIMIT_EXCEEDED, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
			alert("Error Code: File Validation Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);
			break;
		default:
			alert("Error Code: " + 未知的异常 + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninate(this);	
//			var imgboxid=this.customSettings.holder_box_id;
//			var sizelimit=this.customSettings.file_size_limit;
//			var filetype=this.customSettings.file_types;
//			var rtImage=this.customSettings.rtImage;
//			$("#"+imgboxid).find(".uploading").html("");
//			this.destroy( );
//			new SWFInitializer({
//				imageboxId: imgboxid,
//				fileTypes: filetype,
//				sizeLimit: sizelimit,
//				rtImage: rtImage
//			}).init();
			break;
		}
	} catch (ex3) {
		this.debug(ex3);
	}

}