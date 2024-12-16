/*! ezadmin v1.0.0-SNAPSHOT |
 https://gitee.com/ezadmin/index.html
 | MIT /license */
$(function () {
    initForm();
    var canEzFormSubmit=true;
    var config = {
        ignore: "",
        onfocusout: function (element) {
             $(element).valid();
           // this.element(element);
        },
        onkeyup: false,
        // 其他验证选项...
        invalidHandler: function(event, validator) {
            // 找到第一个错误的元素
            var firstError = $(validator.errorList[0].element);

            // 如果元素是隐藏的，先将其滚动到视图中
            if (firstError.is(':hidden')) {
                // 使用jQuery的animate方法滚动到元素位置
                $('html, body').animate({
                    scrollTop: firstError.offset().top
                }, 500);
            }else{
                // 聚焦到第一个错误的元素
                firstError.focus();
            }
        },
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            if(element.parent().prop('tagName')=='XM-SELECT'){
                element.parent().parent().append(error);
            }else{
                element.parent().append(error);
            }
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
            if($(element).parent().prop('tagName')=='XM-SELECT'){
                $(element).parent().parent().find(".error").remove();
            }else{
                $(element).parent().find(".error").remove();
            }
        },
        submitHandler: function (form) {
            if (!canEzFormSubmit){
                return false;
            }
            canEzFormSubmit=false;
           try {
               var fileerror = false;
               $(".layui-upload-list").each(function () {
                   if ($(this).children().length > $(this).attr("item_max_upload_max")
                       || $(this).children().length < $(this).attr("item_max_upload_min")
                   ) {
                       var error = "不符合文件上传个数限制，最多" + $(this).attr("item_max_upload_max") + "个，最少" + $(this).attr("item_max_upload_min") + "个";
                       $(this).parent().append("<span   class=\"error invalid-feedback\" style=\"display: inline;\">" + error + "</span>");
                       layui.layer.msg(error)
                       fileerror = true;
                       canEzFormSubmit = true;
                       return false;
                   } else {
                       $(this).parent().find(".error").remove();
                   }
               })

               $(".tinymcetextarea").each(function () {
                   if ($(this).attr("lay-verify") == 'required' && $(this).val() == '') {
                       var error = "<span   class=\"error invalid-feedback\"  >请维护详细内容</span>"
                       error.insertBefore($(this));
                       fileerror = true;
                       canEzFormSubmit = true;
                       return false;
                   } else {
                       $(this).parent().find(".error").remove();
                   }
               })

               if (fileerror) {
                   canEzFormSubmit = true;
                   return false;
               }


               if (typeof submitHandler == "function") {
                   try {
                       if (!submitHandler()) {
                           canEzFormSubmit = true;
                           return false;
                       }
                   } catch (e) {
                   }
               }

               $(form).ajaxSubmit({
                   url: $("#formSubmitUrl").val(),
                   dataType: 'json',
                   success: function (data) {
                           if (typeof submitSuccess == "function") {
                               try {
                                submitSuccess(data);
                               } catch (e) {
                                   console.log(e);
                               }
                               canEzFormSubmit = true;
                               return;
                           }
                       if (data.code == 0) {
                           console.log("data::" + data.data);
                           layer.alert("保存成功", function (index) {
                               if ('reload' == data.data) {
                                   canEzFormSubmit = true;
                                   window.parent.location.reload();
                               } else if ('reloadlocal' == data.data) {
                                   canEzFormSubmit = true;
                                   window.location.reload();
                               } else {
                                   canEzFormSubmit = true;
                                   location.href = data.data;
                               }
                               return false;
                           })
                       }else if(data.code=='500'){
                           layer.alert(data.message);
                       }  else {
                           layer.alert( "保存失败");
                           console.log(data.message )
                       }
                       canEzFormSubmit = true;
                   },
                   error: function (e) {
                       canEzFormSubmit = true;
                       layer.alert("保存失败,网络异常" + e);
                   }
               });
           }catch (e) {
               console.log(e);
               canEzFormSubmit=true;
           }
            return false;
        }
    };
    if ($("#validateRules").val() != null && $("#validateRules").val() != '') {
        config.rules =validateRules;

    }
    if ($("#validateMessages").val() != null && $("#validateMessages").val() != '') {
        config.messages =validateMessages;
    }




    $.validator.addMethod("isMoney", function (value, element) {
        //允许, 货币格式
        return this.optional(element) || /^([1-9]{1}[0-9]{0,3}(\,[0-9]{3,4})*(\.[0-9]{0,2})?|[1-9]{1}\d*(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/.test(value);
    });
    $.validator.addMethod("url", function() {
        return true; // 始终返回 true，表示通过
    }, "Please enter a valid URL.");
    $("#inputForm").validate(config);



    $("body").on("click","#submitbtnProxy",function(){
        $("#submitbtn").click();
    })
    //end
   // $('.layuimini-loader').fadeOut();
    watermark({"watermark_txt": $("#EZ_SESSION_USER_NAME_KEY").val()+ getNow()});

    //panel 扩大缩小
    $(".ez-form-panel  .layui-layer-min").click(function(){
        $(this).closest(".layui-card").find(".layui-card-body").hide();
        $(this).siblings().removeClass("layui-hide");
        $(this).addClass("layui-hide");
    })
    $(".ez-form-panel  .layui-layer-maxmin").click(function(){
        $(this).closest(".layui-card").find(".layui-card-body").show();
        $(this).addClass("layui-hide");
        $(this).siblings().removeClass("layui-hide");
    })

})


var template = `
 <div  class="file_preview file_preview_{{ d.uniqueName }}" file_id="{{ d.fileId }}" style="margin-top:5px;margin-right:5px;cursor: pointer;position: relative;border:1px solid #dae0e6">
	<div style="display: flex;width: 150px;height: 150px;justify-content: center;align-items:center">
	<img data-original="{{ d.downloadUrl }}{{ d.fileId }}" style=" max-width: 100%; max-height: 100%;" class="viewer-image layui-upload-img"
		 src="{{ d.downloadUrl }}{{ d.fileId }}"></img>
	</div> 
	<span style="top: 0; right: 0;position: absolute; " item_id="{{d.itemId}}" class="layui-badge deleteFiles   ">
<i class="layui-icon layui-icon-close"></i></span>
</div>`;

function upload_reCalId(itemId) {
    var fset = new Set();
    $("#layui-upload-list_" + itemId).find(".file_preview").each(function () {
        fset.add($(this).attr("file_id"))
    })
    let inputHiddenNode = $("#ITEM_ID_" + itemId);
    inputHiddenNode.val([...fset]);
}

function upload_add(config,itemId, fileId) {
    layui.use(['form', 'laytpl'], function () {
        let laytpl = layui.laytpl;
        var ctxName = document.getElementById("contextName").value;
        var uurl = document.getElementById("downloadUrl").value;
        var tmp=template;
        if(config.accept=='video'){
            tmp=template_v;
        }else if(config.accept=='file'){
            tmp=template_file;
        }
        var uniqueName = fileId.replace('./', '').replace(/["&'./:=?[\]%]/gi, '-').replace(/(--)/gi, '');
        var imgShow = laytpl(tmp).render({
            fileId: fileId, contextName: ctxName,uniqueName:uniqueName,
            downloadUrl: uurl,
            itemId: itemId,
        });
        $("#layui-upload-list_" + itemId).append(imgShow);

        upload_reCalId(itemId);
    })

}

function upload_remove(itemId, fileId) {
    var uniqueName = fileId.replace('./', '').replace(/["&'./:=?[\]%]/gi, '-').replace(/(--)/gi, '');
    $("#layui-upload-list_" + itemId).find(".file_preview_" + uniqueName).remove();
    upload_reCalId(itemId);
}

$(document).on('click', '.deleteFiles', function () {
    var _this = $(this);
    layer.confirm('删除图片?', function (index) {
        var itemId = _this.attr("item_id")
        upload_remove(itemId, _this.parent().attr("file_id"));
        layer.close(index);
        if (typeof (eval("deleteFilesCallback")) == "function") {
            deleteFilesCallback(_this);
        }
    });
});

var template_v = `
 <div  class="file_preview file_preview_{{ d.uniqueName }}" file_id="{{ d.fileId }}" style="margin-right:20px;cursor: pointer;position: relative;border:1px solid #dae0e6">
	<div style="display: flex;width: 150px;height: 150px;justify-content: center;align-items:center">
	<video  style=" max-width: 100%; max-height: 100%;" class="viewer-image layui-upload-img"
		 src="` + $("#contextName").val() + $("#downloadUrl").val() + `{{ d.fileId }}" controls="controls"></video>
	</div>
	<span style="top: 0; right: 0;position: absolute; " item_id="{{d.itemId}}" class="layui-badge deleteFiles   ">
<i class="layui-icon layui-icon-close"></i></span>
</div>`;

var template_file = `
 <div  class="file_preview file_preview_{{ d.uniqueName }}" file_id="{{ d.fileId }}" style="margin-right:20px;cursor: pointer;position: relative;border:1px solid #dae0e6">
	<div style="display: flex;width: 150px;height: 150px;justify-content: center;align-items:center">
	<i  onclick="javascript:openBlank('` + $("#contextName").val() + $("#downloadUrl").val() + `{{ d.fileId }}')" style="font-size:100px; max-width: 100%; max-height: 100%;" 
		  class=" layui-upload-img layui-icon-file-b layui-icon"></i>
	</div>
	<span style="top: 0; right: 0;position: absolute; " item_id="{{d.itemId}}" class="layui-badge deleteFiles   ">
<i class="layui-icon layui-icon-close"></i></span>
</div>`;
