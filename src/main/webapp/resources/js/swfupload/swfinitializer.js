function SWFInitializer(params){
	this.imageboxId=params.imageboxId;
	this.fileTypes=params.fileTypes;
	this.sizeLimit=params.sizeLimit;
	this.progressId=params.progressId;
	this.urlShowId=params.urlShowId;
	this.urlValueId=params.urlValueId;
	this.targetURL="";
	this.swfu;
	this.swfu1;
	this.rtImage=(params.rtImage=="undefined"||params.rtImage==null||params.rtImage=="")?"rtImage":params.rtImage;

	//生成界面
	that=this;
	this.init=function(){
		$("#"+that.imageboxId).html("<span class=\"hook\"><span id=\"spanButtonPlaceholder\"></span></span>"
							+"<div class=\"uploading\" style=\"display: none;\">"
							+"</div>"
							+"<img class=\"showimg\" style=\"display: none;\">");
		
		swfu = new SWFUpload({
//			upload_url: ctxPath+"/upload.action;jsessionid="+jsessionid+"?timestamp="+Math.round(new Date().getTime()/1000),
			upload_url: "http://172.27.35.2:8080/FileUpload/upload.action;jsessionid="+jsessionid+"?timestamp="+Math.round(new Date().getTime()/1000),
			file_size_limit : that.sizeLimit,	
			file_types : that.fileTypes,
			file_upload_limit : "1",
			file_post_name: "uploadFile",
			post_params: {"resType" : that.rtImage, "jsessionid": jsessionid },
			file_queue_error_handler : fileQueueErrorSingle,
			file_dialog_complete_handler : fileDialogCompleteSingle,
			file_queued_handler : fileQueuedSingle,
			upload_progress_handler : uploadProgressSingle,
			upload_error_handler : uploadErrorSingle,
			upload_success_handler : uploadSuccessSingle,
			upload_complete_handler : uploadCompleteSingle,

			button_image_url : ctxPath+"/resource/js/swfupload/SmallSpyGlassWithTransperancy_17x18.png",
			button_placeholder_id : "spanButtonPlaceholder",
			button_width: 80,
			button_height: 18,
			button_text : '<span class="button">点击上传</span>',
			button_text_style : '.button { font-family: Helvetica, Arial, sans-serif; font-size: 12pt; color: #999999; width: 50pt; } .buttonSmall { font-size: 10pt; }',
			button_text_top_padding: 0,
			button_text_left_padding: 20,
			button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			button_cursor: SWFUpload.CURSOR.HAND,
			
			flash_url : ctxPath+"/resource/js/swfupload/swfupload.swf", 
			custom_settings : {
				holder_box_id: that.imageboxId,
				file_types: that.fileTypes,
				file_size_limit: that.sizeLimit,
				rtImage: that.rtImage,
				urlShowId: that.urlShowId,
				urlValueId: that.urlValueId
			},
			debug: false  
		});
	};
	
	this.initMultiple2one=function(){
		$("#"+that.imageboxId).find(".hooks").html("<span id=\"spanButtonPlaceholder\"></span>");
		swfu1 = new SWFUpload({
			upload_url: ctxPath+"/upload.action;jsessionid="+jsessionid+"?timestamp="+Math.round(new Date().getTime()/1000),
			file_size_limit : that.sizeLimit,	
			file_types : that.fileTypes,
			file_upload_limit : "1",
			file_post_name: "uploadFile",
			post_params: {"resType" : that.rtImage },
			file_queue_error_handler : fileQueueErrorM2one,
			file_dialog_complete_handler : fileDialogCompleteM2one,
			file_queued_handler : fileQueuedM2one,
			upload_progress_handler : uploadProgressM2one,
			upload_error_handler : uploadErrorM2one,
			upload_success_handler : uploadSuccessM2one,
			upload_complete_handler : uploadCompleteM2one,

			button_image_url : ctxPath+"/resource/js/swfupload/SmallSpyGlassWithTransperancy_17x18.png",
			button_placeholder_id : "spanButtonPlaceholder",
			button_width: 80,
			button_height: 18,
			button_text : '<span class="button">开始上传</span>',
			button_text_style : '.button { font-family: Helvetica, Arial, sans-serif; font-size: 12pt; color: #999999; width: 50pt; } .buttonSmall { font-size: 10pt; }',
			button_text_top_padding: 0,
			button_text_left_padding: 20,
			button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			button_cursor: SWFUpload.CURSOR.HAND,
			
			flash_url : ctxPath+"/resource/js/swfupload/swfupload.swf", 
			custom_settings : {
				progress_box_id: that.progressId,  //显示上传进度的DIV的id
				target_box_id: that.imageboxId, //用于向ID为此的DIV中添中图片
				file_types: that.fileTypes,
				file_size_limit: that.sizeLimit,
				rtImage: that.rtImage
			},
			debug: false  
		});
	};
}
	