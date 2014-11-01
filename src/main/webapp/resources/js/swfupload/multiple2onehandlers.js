function fileQueueErrorM2one(file, errorCode, message) {
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
function fileQueuedM2one(file){
	initUploadingProgressM2one(this,file.size,file.id,file.name,"等待上传");
}
function fileDialogCompleteM2one(numFilesSelected, numFilesQueued) {
	try {
		if (numFilesQueued > 0) {
			this.startUpload();
		}
	} catch (ex) {
		this.debug(ex);
	}
}

function uploadProgressM2one(file, bytesLoaded) {
	try {
		var percent = Math.ceil((bytesLoaded / file.size) * 100);

		if (percent >= 100) {
			loadingProgressM2one(file.id,"100%", this);
		} else {
			loadingProgressM2one(file.id,percent+"%", this);
		}	
	} catch (ex) {
		alert("exc");
		this.debug(ex);
	}
}

function uploadSuccessM2one(file, serverData) {
	try {
		var data = eval("(" + serverData + ")");
		data = eval("(" + data.result + ")");
		//var path=data.path;
		var rid=data.id;
		var fullpath=data.viewPath;
		var fileid=file.id;
		var target_box_id=this.customSettings.target_box_id;
		var progress_box_id=this.customSettings.progress_box_id;
		//重新生成swf对象
		reinstaninateM2one(this);
		if (data.success == 0 || data.success == "0") {	
			uploadSuccessRecallM2one(fileid, fullpath, target_box_id, progress_box_id, rid);
		} else {
			alert("上传文件失败！");
			$("#"+progress_box_id).css("display","none");
		}
	} catch (ex) {
		this.debug(ex);
	}
}
function reinstaninateM2one(swfObject){
	var target_box_id=swfObject.customSettings.target_box_id;
	var progress_box_id=swfObject.customSettings.progress_box_id;
	var sizelimit=swfObject.customSettings.file_size_limit;
	var filetype=swfObject.customSettings.file_types;
	var rtImage=swfObject.customSettings.rtImage;
	swfObject.destroy( );
	new SWFInitializer({
		imageboxId: target_box_id,
		progressId: progress_box_id,
		fileTypes: filetype,
		sizeLimit: sizelimit,
		rtImage: rtImage
	}).initMultiple2one();
}
function uploadSuccessRecallM2one(fileid, fullpath, target_box_id, progress_box_id, rid){
	var htmlimg="<img class=\"viewImg\" id=\""+rid+"\" src=\""+fullpath+"\" style=\"width: 48px; height: 48px;\" />";
	$("#"+target_box_id).find("ul li div.on").html("");
	$("#"+target_box_id).find("ul li div.on").append(htmlimg);
	$("#"+progress_box_id).html("");
	var htmldel="<img class=\"deleteImg\" src=\""+ctxPath+"/resource/image/validate-err2.png\" style=\"position: absolute; right: 4px; top: 4px; cursor: pointer;\"/>";
	
	$("#"+target_box_id).find("ul li div.on").append(htmldel);
	
	$("#"+target_box_id).find("ul li div").find("img.deleteImg").mouseover(function(){
		$(this).attr("src",ctxPath+"/resource/image/validate-err.png");	
	}).mouseout(function(){
		$(this).attr("src",ctxPath+"/resource/image/validate-err2.png");	
	}).click(function(){
		$(this).parent().html("&nbsp;");
	});	
}
function loadingProgressM2one(fileId,message,swfObject){
	$("#"+swfObject.customSettings.progress_box_id).find("span.progress").html(message);
}

//初如化上传的展示界面
function initUploadingProgressM2one(swfObject){
	try {
		$("#"+swfObject.customSettings.progress_box_id).css("display","block");
		var html="<img src=\""+ctxPath+"/resource/js/swfupload/loading23.gif\" />"
					+"<span>上传中<span class=\"progress\">0%</span></span>";
		$("#"+swfObject.customSettings.progress_box_id).html(html);
	} catch (ex) {
		alert("initUploadingProgress 出错");
	}
}
function uploadCompleteM2one(file) {
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
function uploadErrorM2one(file, errorCode, message) {
	try {
		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			alert("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			reinstaninateM2one(this);	
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			alert("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninateM2one(this);	
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			alert("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			reinstaninateM2one(this);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			alert("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			reinstaninateM2one(this);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			alert("Error Code: FILE_CANCELLED, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninateM2one(this);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			alert("Error Code: UPLOAD_STOPPED, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninateM2one(this);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			alert("Error Code: UPLOAD_LIMIT_EXCEEDED, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninateM2one(this);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
			alert("Error Code: File Validation Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninateM2one(this);
			break;
		default:
			alert("Error Code: " + 未知的异常 + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			reinstaninateM2one(this);	
			break;
		}
	} catch (ex3) {
		this.debug(ex3);
	}

}