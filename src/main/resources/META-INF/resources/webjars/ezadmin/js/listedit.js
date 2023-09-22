
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
    form.on('select(lay_filter_jdbctype)',function(data){
        let active=$(".active");
        let col=active.attr("ez-jdbctype");
        active.attr("ez-jdbctype",data.value);
    });
    form.on('select(lay_filter_datatype)',function(data){
        let active=$(".active");
        let col=active.attr("ez-datatype");
        active.attr("ez-datatype",data.value);
    });
    form.on('select(lay_filter_select)',function(data){
        let active=$(".active");
        let col=active.attr("ez-select");
        active.attr("ez-select",data.value);
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


    form.on('select(lay_filter_opentype)',function(data){
        let active=$(".active");
        active.attr("ez-opentype",data.value);
        var button=$(".active").find("button");
        button.attr("opentype",data.value);
    });

    $(".config-form").find("input,textarea").not("[type=radio]").not("[type=checkbox]").on("change",function(){
        let name=$(this).attr("name");
        let active=$(".active");
        active.attr("ez-"+name,$(this).val());
        let type=active.attr("type");

        if(name=='append_head'){
            $('#append_head').html( $(this).val());
        }
        if(name=='append_foot'){
            $('#append_foot').html( $(this).val());
        }
        if(type=='tabcore'){
            switch (name){
                case 'label':
                active.find(".tablink").text($(this).val());
                break;
                case 'name':
                break;
                case 'url':
                active.find(".tablink").attr("url",$(this).val());
                break;
            }
            return;
        }
        if(type=='buttoncore'){
            active.find("button").html($(this).val());
        }

        if(type=='cascader'||type=='cascadersql'
        ||type=='daterange'
        ||type=='datetimerange'
        ||type=='hidden'
        ||type=='input-text'
        ||type=='select'
        ||type=='select-search'
        ||type=='union'
        ||type=='uniondate'
        ||type=='unionor'){
            switch (name){
            case 'label':
            active.find('.layui-form-label').text($(this).val());
            break;
            case 'name':
            active.attr("ez-item_name",$(this).val());
            break;
            case 'url':
            active.attr("ez-url",$(this).val());
            break;
            case 'placeholder':
            active.find('input,textarea').attr("placeholder",$(this).val());
            break;
        }
        return;
    }


    if(name=='select_express'){
        active.find('#select_express').html( $(this).val());
    }
    if(name=='count_express'){
        active.find('#count_express').html( $(this).val());
    }
    if(name=='orderby_express'){
        active.find('#orderby_express').html( $(this).val());
    }
    if(name=='groupby_express'){
        active.find('#groupby_express').html( $(this).val());
    }
})
});
function rowselect(el){
    let edittype=el.attr("type");
    let eztype=el.attr("ez-type");
    //样式切换
    $(".active").removeClass("active");
    $(".layui-component-tools").remove();
    el.addClass("active");
    if(!$(el).hasClass("systemfloat")){//表单不允许复制
        el.append(tools);
    }

    $(".config-form").hide();//所有配置隐藏

    var currentConfig=typeform(edittype);
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
        currentConfig.find("[name=select_express]").val($("#select_express").html());
        currentConfig.find("[name=count_express]").val($("#count_express").html());
        currentConfig.find("[name=orderby_express]").val($("#orderby_express").html());
        currentConfig.find("[name=groupby_express]").val($("#groupby_express").html());
        currentConfig.find("[name=append_head]").val($("#append_head").html());
        currentConfig.find("[name=append_foot]").val($("#append_foot").html());
    }
    // if(eztype=='daterange'||eztype=='datetimerange'){
    //     currentConfig.find("[name=label]").val($(".active").find("layui-form-label").text());
    // }
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
        if (
            $(target).attr("id") == 'left-colitem' ||
            $(target).attr("id") == 'left-search'
        ) {
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
            $.each($("[type=listcore]")[0].attributes,function(i,d){
                if(d.name.indexOf('ez-')>=0){
                    var cname=d.name.replace('ez-','');
                    try{
                        core[cname]=d.value;
                    }catch(e){
                        console.log(e);
                    }
                }
            })
            core.select_express=$("#select_express").html();
            core.count_express=$("#count_express").html();
            core.orderby_express=$("#orderby_express").html();
            core.groupby_express=$("#groupby_express").html();


            $("[type=tabcore]").find(".selector").each(function(){
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
                    searchitem.item_name=Math.random();
                }
                tab.push(searchitem)
            })
            $("[type=searchcore]").find(".selector").each(function(){
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
                    searchitem.item_name=Math.random();
                }
                search.push(searchitem)
            })
            $("[type=tablebtn]").find(".selector").each(function(){
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
                    searchitem.item_name=Math.random();
                }
                tablebtn.push(searchitem)
            })
            $("[type=rowbtn]").find(".selector").each(function(){
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
                searchitem.type=$(this).attr("type");
                if(searchitem.item_name==''||searchitem.item_name==undefined){
                    searchitem.item_name=Math.random();
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
                    searchitem.item_name=Math.random();
                }
                col.push(searchitem)
            })

            if($("[type=firstcol]").children().eq(0).attr("type")!=undefined){
                core.firstcol=$("[type=firstcol]").children().eq(0).attr("type");
            }
            listData.core=core;
            listData.search=search;
            listData.tab=tab;
            listData.tablebtn=tablebtn;
            listData.rowbtn=rowbtn;
            listData.col=col;

            $.post("/ezadmin/list/submitEdit-"+core.listcode,  {data:JSON.stringify(listData)}, function(data) {
                if(data.success){
                    layui.layer.confirm('保存成功，重新加载？', {icon: 1, title:'提示'}, function(index){
                        location.href='/ezadmin/list/loadEdit-'+core.listcode;
                    });
                }else{
                    layui.layer.alert(data.message);
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
            $(this).attr("name",Math.random());
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
        closeBtn:1,maxmin: true,resize:true,
        title:'插件',
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
        closeBtn:1,
        title:'配置',
        maxmin: true,
        resize:true,
        content:$(".righttab"),
        success: function(){
        },end: function(){
        }
    });


    $(".layui-btn-import").click(function(){
        $("#importForm").show();
        layer.open({
            type: 1, // page 层类型
            area: ['80%', '80%'],
            title: '请输入SQL、JSON、HTML配置',
            shade: 0.6, // 遮罩透明度
            // 点击遮罩区域，关闭弹层
            maxmin: true, // 允许全屏最小化
            anim: 0, // 0-6 的动画形式，-1 不开启
            content: $("#importForm")
             //   '<textarea placeholder="select user_id 用户名,clazz 班级 from T_USER A   WHERE 1=1" class="layui-textarea" id=importcode style="height:100%"></textarea>'
            ,btn: ['确定', '取消' ],
            // 按钮1 的回调
            yes: function(index, layero, that){
               // alert(layui.$("#importcode").val())
                $("#importFormSub").submit();
            },
            cancel: function(index, layero, that){
                // 按钮2 的回调
                return true; // 点击该按钮后不关闭弹层
            },
            end:function(){
                $("#importForm").hide();
            }
        });
    })

    $(".layui-btn-preview").click(function(){
        if($("#listCoreContainer").attr("ez-listcode")==''||$("#listCoreContainer").attr("ez-listcode")==undefined){
                layui.layer.alert("请先保存列表");
        }else{
            openBlank("/ezadmin/list/list-"+$("#listCoreContainer").attr("ez-listcode"));
        }
    })

})
function typeform(edittype){
    if( edittype.indexOf('input-checkbox')>-1||edittype.indexOf('input-radio')>-1
        ||edittype.indexOf('select')>-1){
        edittype='select';
    }else if(edittype=='cascader'||edittype=='cascadersql' ){
        edittype='cascader';
    }else if(edittype=='tab'  ){
        edittype='tabcore';
    }else if(edittype=='listcore'  ){
        edittype='listcore';
    }else if(edittype=='buttoncore'){
        edittype='buttoncore';
    }else if(edittype=='colcore'){
        edittype='colcore';
    }else if(edittype=='daterange'||edittype=='datetimerange'){
        edittype='searchcore';
    }
    else{
        edittype='searchcore';
    }
    return $(".config-"+edittype);
}

