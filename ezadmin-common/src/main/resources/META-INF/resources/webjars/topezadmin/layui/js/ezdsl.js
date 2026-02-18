

layui.use(function(){

    try {

        var form = layui.form;
        var laytpl = layui.laytpl;

        laytpl.config({
            // open: '<%', // 自定义起始界定符
            // close: '%>', // 自定义起始界定符
            tagStyle: 'modern' // 采用新版本的标签风格
        });
        initDate();
        initXmselect();
        initLaycascader();
        initUpload();
        initWater();


        $(".ez-form-panel  .layui-layer-min").click(function () {
            $(this).closest(".layui-card").find(".layui-card-body").hide();
            $(this).siblings().removeClass("layui-hide");
            $(this).addClass("layui-hide");
            $(".layui-layer-dir").addClass('show');
        })
        $(".ez-form-panel  .layui-layer-maxmin").click(function () {
            $(this).closest(".layui-card").find(".layui-card-body").show();
            $(this).addClass("layui-hide");
            $(this).siblings().removeClass("layui-hide");
        })
        $("textarea").each(function(){
            if($(this).attr("maxlength")){
                $(this).on("keyup",function(){
                    var length = $(this).val().length;
                    $(this).parent().find(".layext-textool-count").text(length);
                })
            }
        })



        form.render();
    } catch (e) {
        console.error(e);
    }

    var canEzFormSubmit = true;
    var config = {
        ignore: "",
        onfocusout: function (element) {
            $(element).valid();
            // this.element(element);
        },
        onkeyup: false,
        // 其他验证选项...
        invalidHandler: function (event, validator) {

            // 找到第一个错误的元素
            var firstError = $(validator.errorList[0].element);
            // 如果元素是隐藏的，先将其滚动到视图中
            if (firstError.is(':hidden')) {
                // 使用jQuery的animate方法滚动到元素位置
                $('html, body').animate({
                    scrollTop: firstError.offset().top
                }, 500);
            } else {
                // 聚焦到第一个错误的元素
                firstError.focus();
            }
        },
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            if (element.hasClass('invalidate-tips')) {
                try{
                    layui.layer.msg(error.html(), {icon: 5});
                }catch(e){}
            }
            if (element.parent().prop('tagName') == 'XM-SELECT') {
                element.parent().parent().append(error);
            } else if (element.hasClass('ez-upload-input')) {
                // 对于 upload 组件,将错误信息添加到上传列表容器之后
                var itemName = element.attr('name');
                var uploadList = $('#layui-upload-list_' + itemName);
                if (uploadList.length > 0) {
                    uploadList.after(error);
                } else {
                    element.parent().append(error);
                }
            }
            else {
                element.parent().append(error);
            }
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
            if ($(element).parent().prop('tagName') == 'XM-SELECT') {
                $(element).parent().parent().find(".error").remove();
            }else if($(element).hasClass("layui-upload-file")){
                return;
            }
            else if ($(element).hasClass('ez-upload-input')) {
                // 对于 upload 组件,从上传列表容器之后移除错误信息
                var itemName = $(element).attr('name');
                var uploadList = $('#layui-upload-list_' + itemName);
                if (uploadList.length > 0) {
                    uploadList.next('.error').remove();
                }
                $(element).parent().find(".error").remove();
            } else {
                $(element).parent().find(".error").remove();
            }
        },
        submitHandler: function (form) {
            if (!canEzFormSubmit) {
                return false;
            }
            var loadIndex = layer.msg('加载中', {
                icon: 16,
                shade: 0.01,
                time: 0 // 不自动关闭
            });
            canEzFormSubmit = false;
            try {

                // 原有的 upload 文件数量验证已移至 jQuery Validate 的 uploadMin 和 uploadMax 方法中
                // 这样可以和其他表单验证保持一致，并正确显示验证错误信息

                if (typeof submitHandler == "function") {
                    try {
                        if (!submitHandler()) {
                            canEzFormSubmit = true;
                            layer.close(loadIndex)
                            return false;
                        }
                    } catch (e) {
                    }
                }

                $(form).ajaxSubmit({
                    url: $("#formSubmitUrl").val(),
                    dataType: 'json',
                    success: function (data) {

                        layer.close(loadIndex)
                        if (data.code == 0) {
                            console.log("data::" + data.data);
                            if (typeof submitSuccess == "function") {
                                try {
                                    submitSuccess(data);
                                } catch (e) {
                                    console.log(e);
                                }
                                canEzFormSubmit = true;
                                layer.close(loadIndex)
                                return;
                            }
                            layer.alert("保存成功", function (index) {
                                if ('reload' == data.data || data.data == null) {
                                    canEzFormSubmit = true;
                                    window.parent.location.reload();
                                } else if ('reloadlocal' == data.data) {
                                    canEzFormSubmit = true;
                                    window.location.reload();
                                } else if (data.data.toLowerCase().indexOf('refreshcard') >= 0) {
                                    canEzFormSubmit = true;
                                    refreshCard(data.data);
                                    return;
                                } else {
                                    canEzFormSubmit = true;
                                    location.href = data.data;
                                }
                                return false;
                            })
                        } else if (data.code == '200') {
                            layer.alert(data.message);
                        } else {
                            layer.alert("保存失败,错误码500");
                            console.log(data.message)
                        }
                        canEzFormSubmit = true;
                    },
                    error: function (e) {
                        canEzFormSubmit = true;
                        layer.close(loadIndex)
                        layer.alert("保存失败,网络异常");
                        console.log(e)
                    }
                });
            } catch (e) {
                console.log(e);
                layer.close(loadIndex)
                canEzFormSubmit = true;
            }
            return false;
        }
    };

    //获取每个 name元素的  data-propJson  属性 ，转为json获取里面的 validate节点。里面的rule和message 合并到config.rules和config.messages
    var validateRules = {};
    var validateMessages = {};
    $("[name]").each(function () {
        var propJson = $(this).attr("data-propsJson");
        if (propJson != null && propJson != '') {
            try {
                var propObj = JSON.parse(propJson);

                // 特殊处理 ez-upload-input 组件的 min 和 max 验证
                if ($(this).hasClass('ez-upload-input')) {
                    var uploadRules = {};
                    var uploadMessages = {};

                    if (propObj.min != null && propObj.min > 0) {
                        uploadRules.uploadMin = propObj.min;
                        uploadMessages.uploadMin = "至少需要上传 " + propObj.min + " 个文件";
                    }

                    if (propObj.number != null && propObj.number > 0) {
                        uploadRules.uploadMax = propObj.number;
                        uploadMessages.uploadMax = "最多只能上传 " + propObj.number + " 个文件";
                    }

                    // 如果有 validate 配置，合并进去
                    if (propObj && propObj.validate != null) {
                        if (propObj.validate.rule != null) {
                            uploadRules = Object.assign(uploadRules, propObj.validate.rule);
                        }
                        if (propObj.validate.message != null) {
                            uploadMessages = Object.assign(uploadMessages, propObj.validate.message);
                        }
                    }

                    if (Object.keys(uploadRules).length > 0) {
                        validateRules[$(this).attr("name")] = uploadRules;
                        validateMessages[$(this).attr("name")] = uploadMessages;
                    }
                } else {
                    // 其他组件的验证规则处理
                    if (propObj && propObj.validate != null) {
                        if (propObj.validate.rule != null) {
                            validateRules[$(this).attr("name")] = propObj.validate.rule;
                        }
                        if (propObj.validate.message != null) {
                            validateMessages[$(this).attr("name")] = propObj.validate.message;
                        }
                    }
                }
            } catch (e) {
                console.log(e);
            }
        }
    });
    config.rules = validateRules;
    config.messages = validateMessages;


    if ($.validator) {
        $.validator.addMethod("isMoney", function (value, element) {
            //允许, 货币格式
            return this.optional(element) || /^([1-9]{1}[0-9]{0,3}(\,[0-9]{3,4})*(\.[0-9]{0,2})?|[1-9]{1}\d*(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/.test(value);
        });
        $.validator.addMethod("url", function () {
            return true; // 始终返回 true，表示通过
        }, "Please enter a valid URL.");

        // 添加 upload 组件的 min 验证方法
        $.validator.addMethod("uploadMin", function (value, element, param) {
            if ($(element).hasClass('ez-upload-input')) {
                var itemName = $(element).attr('name');
                var fileCount = $("#layui-upload-list_" + itemName).children().length;
                return fileCount >= param;
            }
            return true;
        }, function(param, element) {
            return "至少需要上传 " + param + " 个文件";
        });

        // 添加 upload 组件的 max 验证方法
        $.validator.addMethod("uploadMax", function (value, element, param) {
            if ($(element).hasClass('ez-upload-input')) {
                var itemName = $(element).attr('name');
                var fileCount = $("#layui-upload-list_" + itemName).children().length;
                return fileCount <= param;
            }
            return true;
        }, function(param, element) {
            return "最多只能上传 " + param + " 个文件";
        });

        $("#inputForm").validate(config);
    }


    $("body").on("click", "#submitbtnProxy", function () {
        $("#submitbtn").click();
    })

    $(document).on('click', '.deleteFiles', function () {
        var _this = $(this);
        var itemId = _this.attr("item_id")
        upload_remove(itemId, _this.parent().attr("file_id"));
        if (typeof deleteFilesCallback === "function") {
            deleteFilesCallback(_this);
        }
    });

});

function initDate(){
    document.querySelectorAll(".ez-date").forEach(function (el) {
        var config = {
            elem: el,
            type: 'date',
            weekStart: 1
        };
        //去除type属性

        var resultConfig={};
        if(el.getAttribute('data-propsJson') != null){
            var c = Global.safeParseJSON(el.getAttribute('data-propsJson'), {});
            resultConfig= Global.deepUnion(c,config);
        }else{
            resultConfig=config;
        }
        if (resultConfig.format && resultConfig.format == 'yyyy-MM-dd') {
            resultConfig.shortcuts = rangeShortCut
        }
        el.removeAttribute("type");
        layui.laydate.render(resultConfig);
    })
}
function initXmselect() {
    document.querySelectorAll(".ez-xmselect").forEach(function (el) {
        renderXmselect(el);
    })

}

function initLaycascader() {
    document.querySelectorAll(".ez-laycascader").forEach(function (el) {
        renderCascader(el);
    })

}

function initUpload() {
    $('button.ez-upload').each(function(){
        var _this=$(this);
        var item_name=_this.attr("upload_item_name");
        var config={
            elem: _this,
            "accept":"image",
            "url":  document.getElementById("uploadUrl").value
        };
        var resultConfig={};
        if(_this.attr('data-propsJson') != null){
            var c = Global.safeParseJSON(_this.attr('data-propsJson'), {});
            resultConfig= Global.deepUnion(c,config);
        }else{
            resultConfig=config;
        }
        resultConfig.done=function(res){
            var respon = res;
            try {
                respon = JSON.parse(res);
            } catch (E) {
            }
            //如果上传失败
            if (!respon.success||respon.data==null) {
                return layer.msg('上传失败，原因：' + respon.message);
            }
            //上传成功的一些操作
            if(respon.data.length>0){
                for (var i = 0; i < respon.data.length; i++) {
                        upload_add(resultConfig, item_name, respon.data[i].fileId);
                }
            }else{
                upload_add(resultConfig, item_name, respon.data.fileId);
            }

        }
        resultConfig.before=function(obj){
           var count= Object.keys(obj.getChooseFiles()).length
            var number= resultConfig.number==null?1:resultConfig.number;
           var exist= $("#layui-upload-list_"+item_name).children().size();
            if(count>number){
                return false;
            }
            if(count>0&& (count +exist) <=number){
                return true;
            }
            layui.layer.msg('同时最多只能上传:' + number + '个文件当前已经选择了:' + (count+exist) + ' 个文件');
            return false;
        }

        var inst=layui.upload.render(resultConfig);

        Global.registry("Upload").register(item_name,inst);

        dragula([document.getElementById("layui-upload-list_"+item_name)])
            .on('drop', function (el) {
                upload_reCalId(item_name)
            });
    })

}

function initWater() {
    try{
        var d = new Date();
        var year = d.getFullYear();
        var month = change(d.getMonth() + 1);
        var day = change(d.getDate());
        var hour = change(d.getHours());
        var minute = change(d.getMinutes());
        var second = change(d.getSeconds());
        function change(t) {
            if (t < 10) {
                return "0" + t;
            } else {
                return t;
            }
        }
        var time = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second + '';
        var name=document.getElementById("EZ_SESSION_USER_NAME_KEY").value;
        watermark.init({ watermark_txt: name + ' ' + time ,watermark_fontsize:'14px'})
    }catch(e){}
}

// 获取 URL 参数
function getUrlParams() {
    var params = {};
    var queryString = window.location.search.substring(1);
    if (queryString) {
        var pairs = queryString.split('&');
        for (var i = 0; i < pairs.length; i++) {
            var pair = pairs[i].split('=');
            params[decodeURIComponent(pair[0])] = decodeURIComponent(pair[1] || '');
        }
    }
    return params;
}
//初始化表单值
function initFormValue(data){
    if (!data||Object.keys(data).length == 0) {
        return;
    }
    var needFormRender = false;
    // 遍历 URL 参数，设置到表单
    for (var key in data) {
        var input = document.querySelector(':is(input, textarea, select)[name="' + key + '"]');
        if(!input){
            continue;
        }
        var type = input.getAttribute("type");
            if (input.classList.contains("ez-laycascader")) {
                var val = data[key];
                //转为数组
                var c= Global.registry("Cascader").get(key);
                if(c){
                    c.setValue(Global.toNumberArray(val));
                }
            } else if (input.classList.contains("ez-xmselect")) {
                var val = data[key];
                //转为数组
                xmSelect.get("#" + input.getAttribute("id")).setValue(Global.toNumberArray(val));
            }else if (input.classList.contains("layui-textarea")) {
                input.value = data[key];
                var length =input.value.length;
                var parentElement = input.parentElement;
                var countElement = parentElement.querySelector(".layext-textool-count");
                if (countElement) {
                    countElement.textContent = length;
                }
            }else if (input.classList.contains("ez-upload-input")) {
                var val = data[key];
                input.value = val;
                //转为数组

                var config={};
                if(input.getAttribute('data-propsJson') != null) {
                    config= Global.safeParseJSON(input.getAttribute('data-propsJson'), {});
                }
                if (val && val.length > 0) {
                    var arr=val.split(",");
                    for (let i = 0; i < arr.length; i++) {
                        if (arr[i] != '') {
                            upload_add(config, input.getAttribute("name"), arr[i]);
                        }
                    }
                }
            }
            else {
                if (type == 'hidden') {
                    var span = document.querySelector(':is(span)[name="' + key + '"]');
                    if(span&&span.getAttribute("data-datajson")){
                        var json=Global.safeParseJSON(span.getAttribute("data-datajson"), []);
                        //获取json里面value= data[key]的label
                        for (var i = 0; i < json.length; i++) {
                            if (json[i].value == data[key]) {
                                span.innerHTML = json[i].label;
                                break;
                            }
                        }
                    }
                    input.value = data[key];
                } else if (type == 'radio') {
                    // Radio: 找到对应 value 的 radio 按钮并设置 checked
                    var radios = document.querySelectorAll('input[type="radio"][name="' + key + '"]');
                    radios.forEach(function(radio) {
                        if (radio.value == data[key]) {
                            radio.checked = true;
                            needFormRender = true;
                        }
                    });
                } else if (type == 'checkbox') {
                    // Checkbox: 可能是单个或多个
                    var checkboxes = document.querySelectorAll('input[type="checkbox"][name="' + key + '"]');
                    if (checkboxes.length > 1) {
                        // 多个 checkbox: 值可能是逗号分隔的字符串或数组
                        var values = Array.isArray(data[key]) ? data[key] : (data[key] ? data[key].toString().split(',') : []);
                        checkboxes.forEach(function(checkbox) {
                            if (values.includes(checkbox.value) || values.includes(checkbox.value.toString())) {
                                checkbox.checked = true;
                                needFormRender = true;
                            }
                        });
                    } else if (checkboxes.length == 1) {
                        // 单个 checkbox: 根据值设置 checked 状态
                        var checkbox = checkboxes[0];
                        var checkboxValue = data[key];
                        // 处理布尔值、字符串 "true"/"false"、数字 1/0 等情况
                        if (checkboxValue === true || checkboxValue === 'true' || checkboxValue === '1' || checkboxValue === 1 ||
                            checkboxValue == checkbox.value) {
                            checkbox.checked = true;
                            needFormRender = true;
                        }
                    }
                } else {
                    input.value = data[key];
                }
            }

    }
    // 如果设置了 radio 或 checkbox，需要重新渲染表单
    if (needFormRender && layui && layui.form) {
        layui.form.render();
    }
}
//渲染级联选择器
function renderCascader(cas) {

    try {
        var currentDom=cas;
        var name=cas.getAttribute("name");
        var config = {
            "elem":currentDom,
            "name":name,
            "filterable": true,
            "clearable": true,
            "collapseTags": true,
            "placeholder": currentDom.getAttribute('placeholder'),
        };

        var resultConfig={};
        if(currentDom.getAttribute('data-propsJson') != null){
            var c = Global.safeParseJSON(currentDom.getAttribute('data-propsJson'), {});
            resultConfig= Global.deepUnion(c,config);
        }else{
            resultConfig=config;
        }
        if(currentDom.getAttribute('data-dataJson') != null){
            var dataArray=Global.safeParseJSON(currentDom.getAttribute('data-dataJson'), []);
            var id=resultConfig.props&&resultConfig.props.id||'id';
            var parent_id=resultConfig.props&&resultConfig.props.parent_id||'parent_id';
            var label=resultConfig.props&&resultConfig.props.label||'label';
            var children=resultConfig.props&&resultConfig.props.children||'children';
            var tree=Global.listToTree(dataArray,id,parent_id,children);
            resultConfig.options =tree;
        }
        var value= currentDom.value;
        if (value != undefined && value != '') {
            resultConfig.value = value.split(",");
        }

        try {
            var layCascader = layui.layCascader;
            var ins= layCascader(resultConfig);//这里获取不到实例
            Global.registry("Cascader").register(resultConfig.name,ins);
        } catch (e) {
            console.log(e)
        }

    } catch (e) {
        console.log(e)
    }
}
//渲染多选选择器
function renderXmselect(xm) {
    try {
        var currentDom=xm;
        var config = {
            el: "#"+currentDom.getAttribute("id"),
            name: currentDom.getAttribute("name"),
            language: 'zn',
            filterable: true,
            tips: currentDom.getAttribute('placeholder'),
            style: {
                height: '26px',
            }, theme: {
                color: '#1e9fff',
            },
            prop: {
                name: 'label',
                value: 'value'
            },
            model: {
                label: {
                    type: 'block',
                    block: {
                        //最大显示数量, 0:不限制
                        showCount: 1,
                        //是否显示删除图标
                        showIcon: true,
                    }
                }
            },
            on:function(data){
                // lay_cascader 关闭兼容
                var elPopperElements = document.querySelectorAll('.el-popper');
                for (var i = 0; i < elPopperElements.length; i++) {
                    elPopperElements[i].style.display = 'none';
                }
                // document.querySelectorAll('.layui-cascader-panel').forEach(panel => {
                //     panel.style.display = 'none';
                // });
            }
        };
        if(currentDom.getAttribute('data-dataJson') != null){
            config.data = Global.safeParseJSON(currentDom.getAttribute('data-dataJson'), []);
        }
        var resultConfig={};
        if(currentDom.getAttribute('data-propsJson') != null){
            var c = Global.safeParseJSON(currentDom.getAttribute('data-propsJson'), {});
            resultConfig= Global.deepUnion(c,config);
        }else{
            resultConfig=config;
        }
        var value= currentDom.value;
        if (value != undefined && value != '') {
            resultConfig.initValue = value.split(",");
        }
        xmSelect.render(resultConfig)
    } catch (e) {
        console.log(e)
    }
}

Global.onClick('reset', function (e) {
    var dataset = this.dataset;
    if (this.__lock) return;
    this.__lock = true;
    setTimeout(() => this.__lock = false, 500);

    // 添加禁用样式
    this.classList.add("layui-btn-disabled");

    // 获取表单中所有包含name属性的元素

    var elms = document.querySelectorAll('[name]')  ;
    var obj = {};

    // 遍历元素并清空非隐藏域的值
    elms.forEach(function (item) {
        var type = item.getAttribute("type");
        if (type !== 'hidden') {
            item.value = "";
        }
    });

    // 如果存在.el-tag元素，触发点击事件
    var tagElements = document.querySelectorAll(".el-tag");
    if (tagElements.length > 0) {
        var arrowElements = document.querySelectorAll(".el-icon-arrow-down");
        arrowElements.forEach(function (element) {
            element.click();
        });
    }

    // 处理xmSelect组件
    var xms = xmSelect.get();
    for (let i = 0; i < xms.length; i++) {
        xms[i].setValue([]);
    }
    // 触发表单提交
    var searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.click();
    }
    this.classList.remove("layui-btn-disabled");
    e.preventDefault();
    e.stopPropagation();
    return false;
});


function upload_add(config, itemId, fileId) {
    layui.use(['form', 'laytpl'], function () {
        let laytpl = layui.laytpl;
        var ctxName = document.getElementById("contextName") == null ? '' : document.getElementById("contextName").value;
        var uurl = document.getElementById("downloadUrl") == null ? '' : document.getElementById("downloadUrl").value;
        var tmp = template;
        if (config.accept == 'video') {
            tmp = template_v;
        } else if (config.accept == 'file') {
            tmp = template_file;
        }
        var uniqueName = fileId.replace('./', '').replace(/["&'./:=?[\]%]/gi, '-').replace(/(--)/gi, '');
        var imgShow = laytpl(tmp).render({
            fileId: fileId, contextName: ctxName, uniqueName: uniqueName,
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
function upload_reCalId(itemId) {
    var fset = new Set();
    $("#layui-upload-list_" + itemId).find(".file_preview").each(function () {
        fset.add($(this).attr("file_id"))
    })
    let inputHiddenNode = $("#ITEM_ID_" + itemId);
    inputHiddenNode.val([...fset]);
    // 手动触发验证,确保 required 规则正确生效
    if ($.validator && $("#inputForm").data("validator")) {
        inputHiddenNode.valid();
    }
}

