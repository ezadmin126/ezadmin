
let tools=`
    <div class="layui-component-tools">
        <i class="layui-icon layui-icon-templeate-1" title="复制"></i>
        <i class="layui-icon layui-icon-delete" title="删除"></i>
    </div>
    `;

    //表单数据同步
layui.use(function(){
    let form=layui.form;
    let $=layui.jquery;
    var slider = layui.slider;

    form.on('radio(radio-filter)', function(data){
        var elem = data.elem; // 获得 radio 原始 DOM 对象
        let active=$(".active");
        active.attr("ez-"+elem.name,data.value);
        form.render();
    });

    form.on('select(disabled)',function(data){
        let active=$(".active");
        if(data.value=='disabled'){
            active.attr("ez-disabled","disabled");
            active.find('[item_name]').attr("disabled","disabled");

        }else{
            active.removeAttr("ez-disabled");
            active.find('[item_name]').removeAttr("disabled");

        }
    });
    form.on('select(readonly)',function(data){
        let active=$(".active");
        if(data.value=='readonly'){
            active.attr("ez-readonly","readonly");
            active.find('[item_name]').attr("readonly","readonly");

        }else{
            active.removeAttr("ez-readonly");
            active.find('[item_name]').removeAttr("readonly");
        }

    });

    form.on('select(lay_filter_all)',function(data){
        let active=$(".active");
        active.attr("ez-"+data.elem.name,data.value);
    });

});
function rowselect(el){
    let edittype=el.attr("config-form-type");
    let eztype=el.attr("ez-type");
    //样式切换
    $(".active").removeClass("active");
    $(".layui-component-tools").remove();
    el.addClass("active");
    if(!$(el).hasClass("systemfloat")){//表单不允许复制
        el.append(tools);
    }

    $(".config-form").hide();//所有配置隐藏

    var currentConfig=$(".config-"+edittype)  ;
    //  $(".config-"+edittype);

    //清空当前表单
    var elms = currentConfig.find("[name]"); //formid 包含name属性的所有元素
    $.each(elms, function (i, item) {
        var type = $(item).attr("type");
        if (type != 'hidden' && type != 'radio' &&type != 'checkbox') {
            $(item).val("");
        }
    });

    //赋值
    $.each(el[0].attributes,function(i,d){
        if(d.name.indexOf('ez-')>=0){
            var cname=d.name.replace('ez-','');
            try{
                var item= currentConfig.find("[name="+cname+"]");
                //配置表单如果是 radio或者checkbox需要单独处理
                var type = $(item).attr("type");
                if (type != 'hidden' && type != 'radio' &&type != 'checkbox') {
                    $(item).val(d.value);
                }
                if(type == 'radio' ||type == 'checkbox'){
                    item.each(function(i,it){
                        if(it.value==d.value){
                            $(this).prop("checked","checked")
                        }
                    })
                }
            }catch(e){
                console.log(e);
            }
            layui.form.render();
        }
    })
    let col=el.attr("ez-col")||3;
    //列设置
    layui.slider.render({
        elem: currentConfig.find('.col'),
        min: 1, // 最小值
        max: 12, // 最大值
        step: 1, // 步长
        showstep: true, // 开启间隔点
        input: true,
        value:col,
        change: function(value){
            console.log(value) // 滑块当前值
            // do something
            let active=$(".active");
            let col=active.attr("ez-col")||3;
            active.removeClass("layui-col-md"+col)
            active.addClass("layui-col-md"+value);
            active.attr("ez-col",value);
        }
    });
    //按钮
    if(edittype=='buttoncore'){//按钮
          var button=$(".active").find("button");
          currentConfig.find("[name=label]").val(button.html());
    }//主体
    else if(edittype=='listcore'){
    }

    layui.form.render();
    currentConfig.show();
}
$(function (){


    dragula({
        isContainer: function (el) {
            return $(el).hasClass('dragula-container'); // only elements in drake.containers will be taken into account
        },
        moves: function (el, source, handle, sibling) {
            return true; // elements are always draggable by default
        },
        accepts: function (el, target, source, sibling) {
            return true; // elements can be dropped in any of the `containers` by default
        },
        invalid: function (el, handle) {
            if ($(el).hasClass("tip")) {
                return true;
            }
            return false; // don't prevent any drags from initiating by default
        },
        copy: true,
        revertOnSpill: true,
        copySortSource: true
    }).on('drop', function (el, target, source, sibling) {
        if (  $(target).attr("id") == 'left-colitem' ||  $(target).attr("id") == 'left-search'
            || $(target).attr("id") == 'left-button'
        ) {
            dropdownint();
            return false;
        }
        $(target).attr("title", $(target).find(".tip").text());
        $(target).find(".tip").remove();
        if ($(el).find(".template").length > 0) {

            if ($(target).attr('type') == 'firstcol') {
                $(target).html($(el).find(".template").html());
            } else {
                $(el).replaceWith($(el).find(".template").html());
            }
        } else {
            return false;
        }
        layui.element.render('nav', 'demo-filter-nav');
        layui.form.render()
    })

    $(document).on("click",".selector",function(e){
        if(!$(this).hasClass("active")){
            rowselect($(this));
        }
        e.preventDefault();
        e.stopPropagation();
    })



    $(".layui-btn-save").click(function(){
        var listData={};
        var core={};
        var search=[];
        var tab=[];
        var tablebtn=[];
        var rowbtn=[];
        var col=[];
        layer.confirm('确定保存？', {
            btn: ['确定', '关闭'] //按钮
        }, function(){
            var ezconfig={};
            $.each($("#listCoreContainer")[0].attributes,function(i,d){
                if(d.name.indexOf('ez-')>=0){
                    var cname=d.name.replace('ez-','');
                    try{
                        core[cname]=d.value;
                        ezconfig[cname]=d.value;
                    }catch(e){
                        console.log(e);
                    }
                }
            })
            core.ezconfig=JSON.stringify(ezconfig);

            core.select_express=$("#select_express").val();
            core.count_express=$("#count_express").val();
            core.orderby_express=$("#orderby_express").val();
            core.groupby_express=$("#groupby_express").val();
            core.displayorder_express=$("#displayorder_express").val();
            core.append_head=$("#append_head_code").val();
            core.append_foot=$("#append_foot_code").val();


            $("#tabCoreContainer").find(".selector").each(function(){
                var searchitem={};
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            searchitem[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                // searchitem.type=$(this).attr("type");
                if(searchitem.item_name==''||searchitem.item_name==undefined){
                    searchitem.item_name=new Date().getTime();
                }
                tab.push(searchitem)
            })
            $("#searchCoreContainer").find(".selector").each(function(){
                var searchitem={};
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            searchitem[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                // searchitem.type=$(this).attr("ez-type");
                if(searchitem.item_name==''||searchitem.item_name==undefined){
                    searchitem.item_name=new Date().getTime();
                }
                search.push(searchitem)
            })
            $("#tableBtnContainer").find(".selector").each(function(){
                var searchitem={};
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            searchitem[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                if(searchitem.item_name==''||searchitem.item_name==undefined){
                    searchitem.item_name=new Date().getTime();
                }
                tablebtn.push(searchitem)
            })
            $("#rowbtnContainer").find(".selector").each(function(){
                var searchitem={};
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            searchitem[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                //searchitem.type=$(this).attr("type");
                if(searchitem.item_name==''||searchitem.item_name==undefined){
                    searchitem.item_name=new Date().getTime();
                }
                rowbtn.push(searchitem)
            })
            $("#centerContainer").find(".selector").each(function(){
                var searchitem={};
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            searchitem[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                if(searchitem.item_name==''||searchitem.item_name==undefined){
                    searchitem.item_name=new Date().getTime();
                }
                col.push(searchitem)
            })

            listData.core=core;
            listData.search=search;
            listData.tab=tab;
            listData.tablebtn=tablebtn;
            listData.rowbtn=rowbtn;
            listData.col=col;
            $.post("/topezadmin/listEdit/submitEdit.html",  {data:JSON.stringify(listData)}, function(data) {
                if(data.success){
                    //保存到数据库
                    layui.layer.confirm('保存成功，重新加载？', {icon: 1, title:'提示'}, function(index){
                        location.href='/topezadmin/listEdit/loadEdit-'+data.data.EZ_CODE;
                    });
                }else{
                    layui.layer.alert("保存失败："+data.message);
                }
            }, 'json').fail(function() {
                layui.layer.alert('请求失败，网络异常');
            });
        }, function(){

        });


    })

    $(document).on("click",".layui-icon-delete",function(e){
        $(this).parents(".active").remove();
    })
    $(document).on("click",".layui-icon-templeate-1",function(e){
        e.preventDefault();
        e.stopPropagation();
        var copy=$(this).parents(".active").clone();

        $.each(copy.find("[name]"),function(i,j){
            $(this).attr("name",new Date().getTime());
        });

        $(this).parents(".active").parent().append(copy);
        $(".active").removeClass("active");
        $(".layui-component-tools").remove();
    })
    //插件配置区
    layer.open({
        type: 1,
        offset: 'l',
        anim: 5, // 从左往右
        area: ['17.666667%', '100%'],
        shade: 0,
        closeBtn:0,maxmin: false,resize:false,
        title:'',
        content:$(".lefttab"),
        success: function(){
        },end: function(){
        }
    });
    layer.open({
        type: 1,
        offset: 'r',
        anim: 5, // 从左往右
        area: ['17.666667%', '100%'],
        shade: 0,
        closeBtn:0,
        title:'配置',
        maxmin: false,
        resize:false,
        content:$(".righttab"),
        success: function(){
        },end: function(){
        }
    });



    function dropdownint(){
        layui.use(function(){
            var dropdown = layui.dropdown;
            dropdown.render({
                elem: '.lefttab .ez-plugin', // 绑定元素选择器，此处指向 class 可同时绑定多个元素
                data: [{
                    title: '添加',
                    id: 100
                }],
                click: function(obj){
                    let template=this.elem.find(".template");
                    var eztype=$(template.html()).attr("config-form-type");
                    var eztype2=$(template.html()).attr("ez-type");
                    var html=template.html();
                    switch (eztype){
                        case 'searchcore':
                        case 'select':
                        case 'cascader':
                            $("#searchCoreContainer").append(html);
                            break;
                        case 'colcore':
                            $("#centerContainer").append(html);
                            break;
                        case 'firstcol':
                            $("#firstcolContainer").append(html);
                            break;
                        case 'tabcore':
                            $("#tabCoreContainer").append(html);
                            break;
                        case 'buttoncore':
                            if( 'button-table,button-span,button-tableselectcheckbox,button-tableselectradio'.indexOf(eztype2)>=0 ){
                                $("#tableBtnContainer").append(html);
                            }else if(eztype2=='button-single'){
                                $("#rowbtnContainer").append(html);
                            }
                            break;
                    };
                    layui.element.render('nav', 'demo-filter-nav');
                    layui.form.render()
                }
            });
        })
    }
    dropdownint();

    initSettingsAll();
})

function  initSettingsAll(){

    $(".layui-btn-preview").click(function(){
        if($("#listCoreContainer").attr("ez-id")==''||$("#listCoreContainer").attr("ez-id")==undefined){
            layui.layer.alert("请先保存列表");
        }else{
            openBlank("/topezadmin/listEdit/list-"+$("#listCoreContainer").attr("ez-id"));
        }
    })

    $(".layui-btn-sourceEdit").click(function(){
        if($("#listCoreContainer").attr("ez-id")==''||$("#listCoreContainer").attr("ez-id")==undefined){
            layui.layer.alert("请先保存列表");
        }else{
            openBlank("/topezadmin/listEdit/sourceEdit-"+$("#listCoreContainer").attr("ez-id"));
        }
    })

    $(".layui-btn-publish").click(function(){
        if($("#listCoreContainer").attr("ez-id")==''||$("#listCoreContainer").attr("ez-id")==undefined){
            layui.layer.alert("请先保存列表");
        }else{
            var title = "确认发布？";
            var appendUrl="/topezadmin/listEdit/publish-"+$("#listCoreContainer").attr("ez-id");
            layer.confirm(title, {icon: 3, title: '提示'}, function (index) {
                $(".layuimini-loader").show();
                $.getJSON(appendUrl, function (result) {
                    $(".layuimini-loader").hide();
                    if (result.success) {
                        layer.alert("操作成功", function (index) {
                            location.reload();
                        })
                    } else {
                        layer.alert("操作失败:" + result.message)
                    }
                })
                layer.close(index);
            });
        }
    })




    $(".layui-btn-core").click(function(){
        $("#listCoreContainer").click();
    })

    initExpress("select_express1","layui-btn-select","请编辑查询表达式","ace/mode/java");
    initExpress("count_express1","layui-btn-count","请编辑总数表达式","ace/mode/java");
    initExpress("orderby_express1","layui-btn-orderby","请编辑排序表达式","ace/mode/java");
    initExpress("groupby_express1","layui-btn-groupby","请编辑分组表达式","ace/mode/java");
    initExpress("displayorder_express1","layui-btn-displayorder","请编辑排序数字表达式","ace/mode/java");

    initExpress("append_head_code1","layui-btn-head","请编辑页头脚本","ace/mode/html");
    initExpress("append_foot_code1","layui-btn-foot","请编辑页脚脚本","ace/mode/html");

    $(document).on("click",".layui-icon-help",function(e){
         window.open($(this).attr("href"))
    })
}
function initExpress(id,btn,title,model) {
    let select_expresseditor = ace.edit(id);
    select_expresseditor.setTheme("ace/theme/eclipse");
    select_expresseditor.session.setMode(model);

        $("."+btn).click(function(){
        var pre= $("#"+id);
        var btn=$(this);
        pre.show();
        layer.open({
            type: 1, // page 层类型
            area: ['80%', '80%'],
            title: title,
            shade: 0.6, // 遮罩透明度
            // 点击遮罩区域，关闭弹层
            maxmin: true, // 允许全屏最小化
            anim: 0, // 0-6 的动画形式，-1 不开启
            content: pre
            ,
            success:function(){
                var span=`<span href='`+btn.attr("help")+`' class="layui-icon layui-icon-help"></span>`;
                $(".layui-layer-setwin").prepend(span)
            }   ,
            btn: ['确定', '取消'],
            yes: function (index, layero, that) {
                var v = select_expresseditor.getValue();
                $("#" + id.replace("1", "")).val(v);
                layer.close(index);
                $(".layui-btn-save").click();
            },
            cancel: function (index, layero, that) {
                return true; // 点击该按钮后不关闭弹层
            },
            end: function () {
                pre.hide();
                $(".layui-icon-help").remove();
            }
        });
        })
}

