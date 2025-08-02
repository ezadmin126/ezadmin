/*! ezadmin v1.0.0-SNAPSHOT |
 https://gitee.com/ezadmin/index.html
 | MIT /license */
var table;
var laytable;
var json = cacheConfig();
var tableConfig;

// 添加字体大小设置
$(document).ready(function() {
    // 获取表格尺寸配置
    var tableSize = json.tableSize || 'md';
    // 如果是小尺寸,设置较小的字体
    if(tableSize === 'sm') {
        $('body').css('font-size', '12px');
    } else {
        $('body').css('font-size', ''); // 恢复默认字体大小
    }
    
    //获取form表单下所有name属性值
    function getobj() {
        var elms = $("#searchForm [name]"); //formid 包含name属性的所有元素
        var obj = {};
        $.each(elms, function (i, item) {
            var name = $(item).attr("name");
            var type = $(item).attr("type");
            if (name != 'LIST_ID' && type != 'hidden') {
                obj[name] = "";
            }

        });
        return obj;
    }
    initForm();
    $('body').on('click', function (e) {
        if (($(e.target).attr('id') != 'custom-cols-span')
            && !$(e.target).hasClass('custom-cols')) {
            $("#custom-cols-span").hide();
        }
    });


    $("#downBtn").click(function () {
        $(this).hide();
        $("#upBtn").show();
        $("#_SEARCH_ITEM_DISPLAY").val(1);
        calculateSearchItemDisplay();
    })
    $("#upBtn").click(function () {
        $(this).hide();
        $("#downBtn").show();
        $("#_SEARCH_ITEM_DISPLAY").val(0);
        calculateSearchItemDisplay();
    })

    var config = {
        onfocusout: function (element) {
            $(element).valid();
        },
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.parent().append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
        },
        submitHandler: function (form) {
            if (typeof submitHandle === "function") {
                if (submitHandle()) {
                    $("#submitBtn").addClass("layui-btn-disabled");
                    $("#submitBtn").unbind("click")
                    return true;
                } else {
                    return false;
                }
            }
            if ($("#submitBtn").hasClass("layui-btn-disabled")) {
                alert('请勿重复提交');
                return false;
            }
            $("#submitBtn").addClass("layui-btn-disabled");
            $("#submitBtn").unbind("click")
            return true;
        }
    };
    if ($("#validateRules").val() != null && $("#validateRules").val() != '') {
        config.rules = JSON.parse($("#validateRules").val());
    }
    if ($("#validateMessages").val() != null && $("#validateMessages").val() != '') {
        config.messages = JSON.parse($("#validateMessages").val());
    }
    $("#searchForm").validate(config);

    $("#submitBtn").click(function () {
        $("#currentPage").val(1);
        $("#currentPage").val(1);
        $("#searchForm").submit();
    })
    layui.use(['table', 'form', 'layCascader'], function () {
        var form = layui.form;
        var layCascader = layui.layCascader;
        $("#resetBtn").click(function () {

            $("#resetBtn").addClass("layui-btn-disabled");

            var elms = $("#searchForm [name]"); //formid 包含name属性的所有元素
            var obj = {};
            $.each(elms, function (i, item) {
                var type = $(item).attr("type");
                if (type != 'hidden') {
                    $(item).val("");
                }
            });

            // $(".J-lv-value").val('')
            // $(".J-text").text('请选择')
            if ($(".el-tag").length > 0) {
                $(".el-icon-arrow-down").click();
            }

            var xms = xmSelect.get();
            for (let i = 0; i < xms.length; i++) {
                xms[i].setValue([])
            }
            $("#currentPage").val(1)

            form.val("searchForm", getobj());

            $("#searchForm").submit();
        })
    })

    //
    $(document).on("click",".page-button:not(layui-btn-disabled)",function(){
        $(this).addClass(".layui-btn-disabled");
        $("#currentPage").val($(this).attr("page"));
        $("#searchForm").submit();
    })

    //回车搜索
    $("#searchForm").find('input').each(function () {
        $(this).keyup(function (event) {
            if (event.keyCode == 13) {
                $("#submitBtn").click();
            }
        });
    })


    $(document).on('click', '.order-icon', function () {
        //排序
        var name = $(this).attr("ITEM_NAME");
        var value = $(this).attr("ITEM_VALUE");
        if (value == '0') {
            value = 1;
        } else if (value == 1) {
            value = '';
        } else {
            value = 0;
        }
        $("#orderBy").attr("name", name);

        $("#orderBy").val(value);
        $("#searchForm").submit();
    })
    $("#export").click(function () {
        var url=$("#prefixUrl").val()+"/list/export-"+$("#ENCRYPT_LIST_ID").val()+'?_BLANK_PARAM_COLUMN='+getCurrentCol()+"&"+getSearchParams();
       openBlank(url)
    })
    function getCurrentCol(){
        var json = cacheConfig();
        var column = json.column == undefined ? [] : json.column;
        var result=[];
        $("th[ez-fixed=left]").each(function(){
            result.push($(this).attr("item_name"))
        })
        result.push(column);
        return result.join();
    }

    $(".ITEM_CHECK_BOX").each(function () {
        $(this).change(function (e) {
            e.stopPropagation();
            e.preventDefault();
            //
            var checked = $(this).is(':checked');
            var itemId = $(this).attr("item_check_id");
            if (checked) {
                $('[list-item_id="' + itemId + '"]').show();
            } else {
                $('[list-item_id="' + itemId + '"]').hide();
            }
        })
    })

    $(document).on('click', '.list-head-checkbox', function () {
        if (this.checked) {
            $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', true);
        } else {
            $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', false);
        }
    })

    renderTable();

    $("#customColAndSearch").click(function () {
        var index = layer.open({
            title: "显隐与排序设置",
            area: ['90%', '80%'],
            type: 2,
            shade: 0.1,
            shadeClose: true,
            anim: 0,
            content: $("#contextName").val() + $("#prefixUrl").val()+"/list/selectCols-" + $("#ENCRYPT_LIST_ID").val()+"?IS_DEBUG="+$("#IS_DEBUG").val(),
            moveOut: true,
            btnAlign: 'c',
            closeBtn: 1,
            btn: ['保存', '设为默认', '取消'],
            yes: function (index, layero) {
                var body = layer.getChildFrame('body', index);
                body.find("#save").click();
                layer.closeAll()
                location.reload();
                return true;
            },
            btn2: function (index, layero) {
                if (confirm('确定要初始化排序？')) { //只有当点击confirm框的确定时，该层才会关闭
                    //
                    var key = 'EZ_CONFIG_' + $("#ENCRYPT_LIST_ID").val();
                    localStorage.removeItem(key);
                    layer.close(index)
                    location.reload();
                }
                return true;
            }, function(index) {
                layer.close(index)
            }
        });
    })

    $(window).resize(function(){
        calculateSearchItemDisplay();
    })

    $("#customBtn").click(function(e){
        e.preventDefault();
        openModel($("#prefixUrl").val()+"/list/customSearch-"+$("#ENCRYPT_LIST_ID").val(),"高级查询")
    })

    $(".tracesql").click(function(){
        $("#trace").val(1);
        $("#searchForm").submit();
    })
    if($("#trace").val()==1){
        window.scrollTo(0, $(window).height());
    }
});
function renderTable() {
    console.log("开始渲染table");
    if (table) {
        console.log("销毁上一个table");
        table.destroy();
    }
    try {
        if (typeof (selfConfig) == "function") {
            console.log(" 内容渲染之前函数 selfConfig() ");
            selfConfig();
        }
    } catch (e) {
        console.log('selfConfig error', e)
    }
    try {
        console.log(" 自定义渲染之前函数 userConfig() , ");
        if (typeof (userConfig) == "function") {
            userConfig();
        }
    } catch (e) {
        console.log('userConfig error', e)
    }

    layui.use(function () {
        var table2 = layui.table;
        var treeTable = layui.treeTable;
        var form=layui.form;
        //实现checkbox 半选效果
        console.log("开始渲染checkbox");
        form.on('checkbox(list-head-checkbox)', function(data){
            var elem = data.elem; // 获得 checkbox 原始 DOM 对象
            var checked = elem.checked; // 获得 checkbox 选中状态
            if (checked) {
                $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', true);
            } else {
                $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', false);
            }
            form.render();
        });

        form.on('checkbox(list-body-checkbox)', function(data){
            var cl=$(".layui-table-box").find("[name=list-body-checkbox]:checked").length;
            var al=$(".layui-table-box").find("[name=list-body-checkbox]").length;
            if(cl<al&&cl>0){
                $('.list-head-checkbox').prop('indeterminate', true);
            }else{
                $('.list-head-checkbox').prop('indeterminate', false);
            }
            if(al==cl){
                $(".list-head-checkbox").prop('checked', true);
            }else{
                $(".list-head-checkbox").prop('checked', false);

            }
            var _check_id_value=$(this).attr("_check_id_value");
            $("[_check_id_value='"+_check_id_value+"']").prop("checked", data.elem.checked)
            form.render();
        });
        var inited=false;
        var hh=43+5; //分页+padding

        if($(".ez-table-tool:visible").length>0){ //表头
            hh=hh+51;
        }
        if($(".ez-table-tool-height:visible").length>0){ //表头
            hh=hh+42;
        }
        if($(".searchWrap:visible").length>0){ //搜索
            hh=hh+$(".searchWrap").height()+5;
        }
        if($(".appendhead").length>0){ //搜索
            hh=hh+$(".appendhead").height() ;
        }


        console.log("计算table的高度："+hh);
        var json = cacheConfig();
        tableConfig={
            height: 'full-'+hh  //设置高度
            ,escape: false
            ,autoSort: false
            ,cellExpandedMode: $("#expandedMode").val()||'tips'
            ,limit: $("#perPageInt").val() //注意：请务必确保 limit 参数（默认：10）是与你服务端限定的数据条数一致
            //  ,cellExpandedMode:'tips'
            //支持所有基础参数
            ,text: {none: '暂无数据'}
            ,done: function (res, curr, count,origin) {
                //+JSON.stringify(res)
                console.log(" 初始化table完成 data："+ "\tpage:"+ curr+"\tcount:"+count+"\torigin:"+(origin||''))
                try {
                    if(origin=='reloadData'){
                        //     inited=true;
                        if (typeof  afterAllDataLoad  == "function") {
                            afterAllDataLoad();
                        }
                        //
                        console.log("数据获取完成之后，开始初始化dropdown");
                        doDropdown();
                        console.log("数据获取完成之后，开始初始化排序");

                        doOrder();
                        console.log("数据获取完成之后，开始初始化dragula，页头拖拽排序");
                        // 初始化 dragula
                        dragula([$("[lay-table-id=mytable] thead tr").eq(0)[0]], {
                            moves: function (el, container, handle) {
                                // 确保只有 span 元素可以触发拖拽
                                return   handle.tagName === 'SPAN'&&handle.className=='';
                            }
                        }).on('drop', function (el, target, source, sibling) {
                            // 拖拽结束后的回调函数
                            // console.log('Element dropped');
                            var ths= $("[lay-table-id=mytable] thead").eq(0).find("th");
                            var json=cacheConfig();
                            var column =  [];
                            ths.each(function(i,item){
                                console.log(i+"--"+$(item).attr("data-field"));
                                column.push($(item).attr("data-field"))
                            })
                            json.column=column;
                            updateCacheConfig(JSON.stringify(json));
                            location.reload();
                        });

                    }
                    console.log("数据获取完成之后，开始初始化 水印，图片高度，按钮颜色等 ");
                    doSystem();
                } catch (e) {
                    console.log(e)
                }

            }
            ,size: json.tableSize || 'md',  // 默认使用中等尺寸
        }
        var initSort={};
        if($("#orderBy").attr("name")!=null&&$("#orderBy").attr("name")!=''){
            initSort.field=$("#orderBy").attr("name").replace("_ORDER","");
            initSort.type=$("#orderBy").attr("value");
            tableConfig.initSort=initSort;
        }
        if($("#mytable").attr("lineStyle")!=null&&$("#mytable").attr("lineStyle")!=''){
            tableConfig.lineStyle=$("#mytable").attr("lineStyle");
        }
        if($("#mytable").attr("class")!=null&&$("#mytable").attr("class")!=''){
            tableConfig.className=$("#mytable").attr("class");
        }
        if($("#mytable").attr("emptytext")!=null&&$("#mytable").attr("emptytext")!=''){
            tableConfig.text.none=$("#mytable").attr("emptytext");
        }
        if($("#cellMinWidth").val()!=null&&$("#cellMinWidth").val()!=''){
            tableConfig.cellMinWidth=$("#cellMinWidth").val();
        }else{
            tableConfig.cellMinWidth=110;
        }
        //转换静态表格
        laytable = table2.init('mytable',tableConfig );
        console.log("初始化分页组件");
        doPage();

        laytable.on('sort(mytable)', function(obj){
            console.log(obj.field); // 当前排序的字段名
            console.log(obj.type); // 当前排序类型：desc（降序）、asc（升序）、null（空对象，默认排序）
            console.log(this); // 当前排序的 th 对象
            $("#orderBy").attr("name", obj.field+"_ORDER");

            $("#orderBy").val(obj.type);
            $("#submitBtn").click();
        });
        console.log("初始化页头手动设置宽度");
        laytable.on('colResized(mytable)', function(obj){
            var col = obj.col; // 获取当前列属性配置项
            var options = obj.config; // 获取当前表格基础属性配置项

            var field=obj.col.field;
            var width=obj.col.width;

            var json = cacheConfig();
            var colwidth = json.colwidth == undefined ? {} : json.colwidth;
            colwidth[field]=width;
            json.colwidth=colwidth;
            updateCacheConfig(JSON.stringify(json));

            console.log(obj); // 查看对象所有成员
        });
        console.log("初始化行选择");

        laytable.on('row(mytable)', function(obj){
            if(obj.e.target.tagName=='IMG'||obj.e.target.tagName=='INPUT'
                ||obj.e.target.tagName=='BUTTON'){
                return false;
            }
            var data = obj.data; // 获取当前行数据
            var input=obj.tr.eq(0).find("[name=list-body-checkbox]");
            var fixedInput=obj.tr.eq(1).find("[name=list-body-checkbox]");
            if(input!=undefined&&input.length>0  ){
                 var checked = input.prop("checked");
                // var _check_id_value=input.attr("_check_id_value");
                //所有的设置选中
               // $("[_check_id_value='"+_check_id_value+"']").prop("checked", !checked)
                //只有checkbox这样处理
                input.prop("checked", !checked);
                if(fixedInput!=undefined&&fixedInput.length>0) {
                    fixedInput.prop("checked", !checked);
                }
               // $("[_check_id_value='"+_check_id_value+"']").attr("checkedflag", checked? '0' : '1');
                obj.setRowChecked({
                    type: input.attr('type') // radio 单选模式；checkbox 复选模式
                });
            }else{
                obj.setRowChecked({
                    type: 'radio'// radio 单选模式；checkbox 复选模式
                });
            }
            layui.form.render()
        });
    });
}

//计算搜索栏是否隐藏
function calculateSearchItemDisplay() {

    let search = json.search == undefined ? [] : json.search;
    //搜索项的排序
    if (search.length > 0) {
        //显示隐藏
        $(".searchcontent > .selector").not(".list-item-hidden").each(function () {
            var _this=$(this);
            //配置包含
            var contains=false;
            for (var i = search.length; i > 0; i--) {
                if(_this.attr("item_name")==search[i - 1].k && search[i - 1].c==true){
                    contains=true;
                    break;
                }
            }
            if(!contains){
                _this.remove();
            }
        })
        //排序
        for (var i = search.length; i > 0; i--) {
            var item = $(".searchcontent").find('.selector[item_name="' + search[i - 1].k + '"]').detach();
            if (!item.hasClass("list-item-hidden")) {
                $(".searchcontent").prepend(item);
            } else {
                $(".searchcontent").append(item);
            }
        }
    }
    $("#upBtn").hide();
    $("#downBtn").hide();
    //如果是全部展示
    if ($("#_SEARCH_ITEM_DISPLAY").val() == 1) {
        $(".searchcontent > .selector").not(".list-item-hidden").show();
        $("#upBtn").show(); //展示 展开 按钮就行了
        $("#downBtn").hide();
    }else{
        var hasHidden=false;
        $(".searchcontent > .selector").not(".list-item-hidden").each(function () {
            if($("#searchForm").offset()){
                if($(this).offset().top>$("#searchForm").offset().top+76){  //大于第二行就不展示
                    $("#upBtn").hide();
                    $("#downBtn").show();
                    $(this).hide();
                    hasHidden=true;
                }else if($(this).offset().top==0){
                    $("#downBtn").hide(); $("#upBtn").show();
                    $(this).hide();
                }else{
                    $(this).show();
                }
            }
        })
        if(hasHidden){
            $("#upBtn").hide(); //展示 展开 按钮就行了
            $("#downBtn").show();
        }
    }
}

function cacheConfig() {
    var key = 'EZ_CONFIG_NEW' + $("#ENCRYPT_LIST_ID").val();
    var jsonconfig = localStorage.getItem(key);
    if (jsonconfig != undefined) {
        var json = JSON.parse(jsonconfig);
        return json;
    }
    return {};
}

function updateCacheConfig(value) {
    var key = 'EZ_CONFIG_NEW' + $("#ENCRYPT_LIST_ID").val();
    localStorage.setItem(key,value);
}

function selfConfig() {
    //根据本地缓存，去除部分字段
    try {
         var json = cacheConfig();
        var column = json.column == undefined ? [] : json.column;
        var colwidth = json.colwidth == undefined ? {} : json.colwidth;

        for (let key in colwidth) {
            if (colwidth.hasOwnProperty(key)) {
                var value=colwidth[key];
                var thoption=JSON.parse($('#mytable th[item_name="'+key+'"]').attr("lay-options"));
                thoption.width=value;
                $('#mytable th[item_name="'+key+'"]').attr("lay-options",JSON.stringify(thoption));
            }
        }

        //第二行开始初始隐藏
        calculateSearchItemDisplay();


        if (column.length > 0) {
            //先排序
            for (var i = 0; i < column.length; i++) {
                $("#mytable thead tr").each(function () {
                    $(this).find('th[ITEM_NAME="' + column[i].k + '"]').detach().appendTo($(this));
                })
                $("#mytable tbody tr").each(function () {
                    $(this).find('td[ITEM_NAME="' + column[i].k + '"]').detach().appendTo($(this));
                })
            }
            //将按钮放到最后一列
            $("#mytable thead tr").each(function () {
                $(this).find('.rowButtons').detach().appendTo($(this));
            })

            $("#mytable tbody tr").each(function () {
                $(this).find('.rowButtons').detach().appendTo($(this));
            })
            //头的显影
            $("#mytable th").each(function () {
                if ( $(this).attr("ez-fixed")!==undefined
                    || $(this).attr("specialcol") ==1 //比如综合字段
                    || $(this).hasClass("rowButtons")
                ) {
                    $(this).show();
                    return;
                }
                for (var i = 0; i < column.length; i++) {
                    if($(this).attr("item_name") != column[i].k){
                        continue;
                    }
                    if ( column[i].c==true) {
                        $(this).show();
                    }else{
                        var json=JSON.parse($(this).attr("lay-options"));
                        json.hide=true;
                        $(this).attr("lay-options",JSON.stringify(json));
                    }
                }
            })
            //body的显影 todo
            $("#mytable td").each(function () {
                // if ( $(this).hasClass("rowButtons")
                //     || $(this).attr("ez-fixed")!==undefined
                //     || $(this).attr("specialcol") ==1
                //     || $(this).hasClass("fixedCol")
                // ) {
                //     $(this).show();return;
                // }
                //
                //
                //
                //
                //
                // if ($("#mytable thead tr").find('th[ITEM_NAME="' + $(this).attr("item_name") + '"]').size()>0
                //
                // ) {
                //     $(this).show();
                // } else {
                //     var json=JSON.parse($(this).attr("lay-options"));
                //     json.hide=true;
                //     $(this).attr("lay-options",JSON.stringify(json));
                // }
            })
        }

    } catch (E) {
        console.log(E);
    }
}



function doDropdown(){
    var dropdown = layui.dropdown;
    if ($(".dropdown_button").length > 0) {
        $(".dropdown_button").each(function () {
            var p = $(this).attr("itemsJson");

            var j = JSON.parse(p);
            console.log($(this).parents("td").attr("class"));
            for (let i = 0; i < j.length; i++) {
                j[i]['title'] = j[i]['label'];
                j[i]['id'] = j[i]['item_name'];
            }
            //初演示
            dropdown.render({
                elem: $(this)
                , data: j
                ,trigger: 'hover'
                , click: function (obj) {
                    //debugger
                    if('script'==obj.opentype){
                        doClickDropItem(obj);
                        return;
                    }
                    if (obj.area == undefined) {
                        ezopen(obj.opentype, obj.windowname, obj.url);
                    } else {
                        ezopen(obj.opentype, obj.windowname, obj.url, obj.area);
                    }
                }
            });
        })
    }
}

function doOrder(){
    var listid=$("#ENCRYPT_LIST_ID").val();
    $("[name=DISPLAY_ORDER]").each(function(){
        $(this).attr("oldValue",$(this).val());
        $(this).blur(function(){
            var othis=$(this);
            var id=othis.attr("data-id");
            var order=othis.val();
            var old=othis.attr("oldValue");
            if(order!=undefined&&order!=''&&old!=order){
                refreshOrder(id,order,old);
            }
        })
    })
}

function doPage(){
    layui.use(['laypage', 'layer', 'form'], function () {
        var laypage = layui.laypage
            , layer = layui.layer;
        if ($(".dataTables_empty").length == 0 && $("#PAGE_LAYUI").length > 0) {
            var params={};
            $("#searchForm").find('input,select').each(function () {
                if ($(this).attr('name')) {
                    params[$(this).attr('name')]=$(this).val();
                }
            })
            if($("#customSearch_count").val()!=''){
                params.customSearch=$("#customSearch_count").val();
            }
            $.post($("#contextName").val() + $("#prefixUrl").val()+"/list/count-" + $("#ENCRYPT_LIST_ID").val()
                , params, function(data) {
                    if(!data.success||data.code==500){
                        return;
                    }
                    if(data.data.page.currentPage>=data.data.page.totalPage){
                        $(".nextpage").removeClass("page-button");
                        $(".nextpage").addClass("layui-btn-disabled");
                    }else{
                        $(".nextpage").addClass("page-button");
                        $(".nextpage").removeClass("layui-btn-disabled");
                    }

                    laypage.render({
                        elem: 'PAGE_LAYUI'
                        , count: data.data.page.totalRecord
                        , curr: data.data.page.currentPage
                        , limit: data.data.page.perPageInt
                        , limits: [10,30, 50, 100, 500, 1000]
                        , layout: ['refresh','count', 'prev', 'page', 'next', 'limit', 'skip']
                        , jump: function (obj, first) {
                            if (!first) {
                                $("#perPageInt").val(obj.limit)
                                $("#currentPage").val(obj.curr)
                                $("#searchForm").submit();
                            }
                        }
                    });

            }, 'json').fail(function () {
                console.log("error");
            });

        } else {
            laypage.render({
                elem: 'PAGE_LAYUI'

                , count: 0
                , curr: 1
                , limit: 10
                , limits: [10, 20, 30, 40, 50, 100]
                , layout: ['count', 'prev', 'page', 'next', 'limit', 'skip']
                , jump: function (obj, first) {
                    if (!first) {
                        $("#perPageInt").val(obj.limit)
                        $("#currentPage").val(obj.curr)
                        $("#searchForm").submit();
                    }
                }
            });
        }
    });
    $(".jumpPage").keyup(function (event) {
        if (event.keyCode == 13) {
            $("#currentPage").val($("#jumpPage").val())
            $("#searchForm").submit();
        }
    });
    $(".perPageIntSelect").change(function (event) {
        $("#currentPage").val(1)
        $("#perPageInt").val($(this).val())
        $("#submitBtn").click();
    });
}

function doSystem() {
    $('.layuimini-loader').fadeOut();
    $("button[item_name='删除']").addClass("layui-border-red");
    $("button[item_name='修改']").addClass("layui-border-blue");
    $("button[item_name='编辑']").addClass("layui-border-blue");
    if($("#removewatermark").val()!=1){
        watermark({"watermark_txt": $("#EZ_SESSION_USER_NAME_KEY").val() +' '+ getNow()});
    }
    //懒加载
    // $('.viewer-image').each(function () {
    //
    //     $(this).on('load', function () {
    //
    //     });
    // })
    lazyImage();
   // document.addEventListener('DOMContentLoaded', function() {

   // });
}

function refreshOrder(item_id,order,oldOrder) {
    $.get($("#prefixUrl").val()+"/list/doOrder-" + $("#ENCRYPT_LIST_ID").val() + "?orderId=" + item_id + "&displayOrder="+order+"&oldOrder="+oldOrder, function (data) {
        if (data.success) {
            location.reload();
        } else {
            alert('重新计算排序失败')
        }
    })
}
