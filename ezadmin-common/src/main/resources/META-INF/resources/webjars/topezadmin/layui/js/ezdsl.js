layui.use(function(){
    try {

        var form = layui.form;
        var laytpl = layui.laytpl;

        laytpl.config({
            // open: '<%', // 自定义起始界定符
            // close: '%>', // 自定义起始界定符
            tagStyle: 'modern' // 采用新版本的标签风格
        });
        document.querySelectorAll(".ez-date").forEach(function (el) {
            var config = {
                elem: el,
                type: 'date',
                weekStart: 1
            };
            var resultConfig={};
            if(el.getAttribute('data-propsJson') != null){
                var c = JSON.parse(el.getAttribute('data-propsJson'));
                resultConfig= Global.deepUnion(c,config);
            }else{
                resultConfig=config;
            }
            if (resultConfig.format && resultConfig.format == 'yyyy-MM-dd') {
                resultConfig.shortcuts = rangeShortCut
            }
            layui.laydate.render(resultConfig);
        })
        document.querySelectorAll(".ez-xmselect").forEach(function (el) {
            renderXmselect(el);
        })
        document.querySelectorAll(".ez-laycascader").forEach(function (el) {
            renderCascader(el);
        })

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
});
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
    // 遍历 URL 参数，设置到表单
    for (var key in data) {
        var input = document.querySelector('[name="' + key + '"]');
        var type = input.getAttribute("type");


        if (input) {
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
                $(this).parent().find(".layext-textool-count").text(length);
            }
            else {
                if (type == 'hidden') {
                    continue;
                }
                input.value = data[key];
            }
        }
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
            var c = JSON.parse(currentDom.getAttribute('data-propsJson'));
            resultConfig= Global.deepUnion(c,config);
        }else{
            resultConfig=config;
        }
        if(currentDom.getAttribute('data-dataJson') != null){
            var dataArray=JSON.parse(currentDom.getAttribute('data-dataJson'));
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
            config.data = JSON.parse(currentDom.getAttribute('data-dataJson'));
        }
        var resultConfig={};
        if(currentDom.getAttribute('data-propsJson') != null){
            var c = JSON.parse(currentDom.getAttribute('data-propsJson'));
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

function ezopen(openType, title, appendUrl, area) {
    var contextNameElement = document.getElementById('contextName');
    appendUrl = (contextNameElement ? contextNameElement.value : '') + appendUrl;
    if (openType && openType.indexOf('parent.') >= 0) {
        openType = openType.replace('parent.', '');
        parent.window.ezopen(openType, title, appendUrl, area);
        return;
    }
    switch (openType) {
        case 'APPEND_PARAM':
            if (appendUrl != null && appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + getSearchParams() + '&' + getCheckIdsUrl();
            } else {
                appendUrl += '&' + getSearchParams() + '&' + getCheckIdsUrl();
            }
            openModel(appendUrl, title, area);
            break;
        case 'MODAL':
            openModel(appendUrl, title, area);
            break;
        case 'FORM':
            openForm(appendUrl, title, area);
            break;
        case 'FULL':
            openFull(appendUrl, title, '确定', function (index111, layero, that) {
                    var body = layer.getChildFrame('body', index111);

                    var submitBtn = body.querySelector('#submitbtn');
                    if (submitBtn) {
                        submitBtn.click();
                    }
                }, '取消',
                function (index222, layero, that) {
                    layer.close(index222);
                });
            break;
        case 'CONFIRM_MODEL':
            layer.confirm('确认操作?', {icon: 3, title: '提示'}, function (index) {
                layer.close(index);
                openModel(appendUrl, title, area);
            })
            break;
        case '_BLANK':
            openBlank(appendUrl);
            break;
        case '_BLANK_PARAM':
            if (appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + getSearchParams() + '&' + getCheckIdsUrl();
            } else {
                appendUrl += '&' + getSearchParams() + '&' + getCheckIdsUrl();
            }
            openBlank(appendUrl);
            break;
        case '_BLANK_PARAM_COLUMN':
            if (appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + getSearchParams() + '&' + getCheckIdsUrl();
            } else {
                appendUrl += '&' + getSearchParams() + '&' + getCheckIdsUrl();
            }
            var encryptListIdElement = document.getElementById('ENCRYPT_LIST_ID');
            var key = 'EZ_CONFIG_' + (encryptListIdElement ? encryptListIdElement.value : '');
            var jsonconfig = localStorage.getItem(key);
            if (jsonconfig != undefined) {
                var json = JSON.parse(jsonconfig);
                var search = json.search;
                var column = json.column;
                if (jsonconfig != column && column.length > 0) {
                    var columnurl = "";
                    for (let index = 0; index < column.length; index++) {
                        columnurl += column[index] + ","
                    }
                    appendUrl += "_BLANK_PARAM_COLUMN=" + columnurl;
                }
            }
            openBlank(appendUrl);
            break;
        case 'LOCATION':
            location.href = appendUrl;
            break;
        case 'PARENT':
            openTab(title, appendUrl)

            break;
        case 'AJAX':
            fetch(appendUrl)
            .then(response => response.json())
            .then(function (result) {
                if (result.success) {
                    layer.msg("操作成功",{time:500}, function (index) {
                        location.reload();
                    })
                } else {
                    layer.msg("操作失败:" + result.message)
                }
            })
            .catch(function(error) {
                console.error('Error:', error);
            });
            // openTab(title,appendUrl)
            break;
        case 'CONFIRM_AJAX':
            var title = title;
            layer.confirm(title, {icon: 3, title: '提示'}, function (index) {
                var loaderElements = document.querySelectorAll('.layuimini-loader');
                for (var i = 0; i < loaderElements.length; i++) {
                    loaderElements[i].style.display = 'block';
                }
                fetch(appendUrl)
                .then(response => response.json())
                .then(function (result) {
                    var loaderElements = document.querySelectorAll('.layuimini-loader');
                    for (var i = 0; i < loaderElements.length; i++) {
                        loaderElements[i].style.display = 'none';
                    }
                    if (result.success) {
                        layer.alert("操作成功",  function (index) {
                            location.reload();
                        })
                    } else {
                        layer.alert("操作失败:" + result.message)
                    }
                })
                .catch(function(error) {
                    console.error('Error:', error);
                    var loaderElements = document.querySelectorAll('.layuimini-loader');
                    for (var i = 0; i < loaderElements.length; i++) {
                        loaderElements[i].style.display = 'none';
                    }
                });
                layer.close(index);
            });

            // openTab(title,appendUrl)
            break;
        default:
            openModel(appendUrl, title, area);
            console.log('无opentype默认model')
    }
}
