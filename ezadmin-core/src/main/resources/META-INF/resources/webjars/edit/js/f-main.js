let tools=`<div class="layui-component-tools">
<i class="layui-icon layui-icon-templeate-1" title="复制"></i>
<i class="layui-icon layui-icon-delete" title="删除"></i>
</div>`;

layui.use(function(){
    let form=layui.form;
    let $=layui.jquery;
    var slider = layui.slider;

    form.on('radio(radio-filter)', function(data){
        var elem = data.elem; // 获得 radio 原始 DOM 对象
        var checked = elem.checked; // 获得 radio 选中状态
        var value = elem.value; // 获得 radio 值
        var othis = data.othis; // 获得 radio 元素被替换后的 jQuery 对象
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


$(function (){
    var  itemdrag=itemdragInit();
    const cardcontainer = document.getElementById('cardcontainer');
    const centerContainer = document.getElementById('centerContainer');
    //card拖入情况
    var carddrag= dragula([cardcontainer, centerContainer], {
        copy: function (el, source) {
            // 只允许从左边复制到右边
            return source === cardcontainer;
        },
        moves: function (el, source, handle, sibling) {

            var iscardcol=(
                (handle.classList.contains('cardcol')
                )
                &&
                handle.classList.contains("active"))
            ||
                handle.classList.contains('layui-card-header');
            console.log(iscardcol)
            var ileftcard=handle.classList.contains("leftcard")||$(handle).parent().hasClass("leftcard");
            console.log(ileftcard)

            return iscardcol||ileftcard  ;
        },
        accepts: function (el, target, source, sibling) {
            // 只允许拖放到右侧容器
            console.log("carddrag accepts ")
            return target !== cardcontainer;
        }
    }).on('drop', function (el, target, source, sibling) {
        if(source !== cardcontainer){
            return false;
        }
        var tmp=$(el).find(".template");
        tmp.find('[name=default]').attr("name",new Date().getTime());
        $(el).replaceWith($(el).find(".template").html());
        layui.form.render()
        itemdragInit();
        return true;
    })


    //元素拖动初始化
    const  rightContainers = document.querySelectorAll('.dragula-container');

    rightContainers.forEach(level2 => {
        dragula([level2], {
            moves: function (el, source, handle, sibling) {
                return handle.classList.contains('layui-col-space10'); // 确保只有 level3 元素可以被拖动
            }
        }).on('drop', function (el, target, source, sibling) {
            console.log("rightContainers drop ")
            return true;
        }) ;
    });

    function itemdragInit(){
        const leftContainer = document.getElementById('leftContainer');
        const  rightContainers = document.querySelectorAll('.form-item-container');
        try{
            itemdrag.destroy();
        }catch (e) {
            console.log(e);
        }
        //新增元素的情况
        itemdrag= dragula([leftContainer,...rightContainers ], {
            copy: function (el, source) {
                // 只允许从左边复制到右边
                console.log("itemdragInit copy"+(source === leftContainer))
                return source === leftContainer;
            },
            moves: function (el, source, handle, sibling) {
                console.log("itemdragInit moves")
                return true;
            },
            accepts: function (el, target, source, sibling) {
                // 只允许拖放到右侧容器
                console.log("itemdragInit accepts"+(target !== leftContainer))
                return target !== leftContainer;
            }
        }).on('drop', function (el, target, source, sibling) {
            // 只允许从左边拖到右边
            console.log("itemdragInit drop"+(source !== leftContainer))
            if(source !== leftContainer){
                return false;
            }
            var tmp=$(el).find(".template");
            tmp.find('[name=default]').attr("name",new Date().getTime());

            $(el).replaceWith($(el).find(".template").html());

            layui.form.render()
            return true;
        })
        return itemdrag;
    }


    function rowselect(el){
        let edittype=el.attr("ez-type");
        //样式切换
        $(".active").removeClass("active");
        $(".layui-component-tools").remove();
        el.addClass("active");
        $(".config-form").hide();//所有配置隐藏
        var currentConfig=typeform(edittype);
        //清空当前表单
        var elms = currentConfig.find("[name]"); //formid 包含name属性的所有元素
        $.each(elms, function (i, item) {
            var type = $(item).attr("type");
            if (type != 'hidden' && type != 'radio' &&type != 'checkbox') {
                $(item).val("");
            }
        });


        if(edittype!='formcore'){//表单不允许复制
            el.append(tools);
            //赋值

        }else{
            $.each($("body")[0].attributes,function(i,d){
                var k=d.name;
                var v=d.value;
                el.attr("ez-"+k,v);
            })
        }


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

        let col=el.attr("ez-col")||12;
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
                let col=active.attr("ez-col")||12;
                active.removeClass("layui-col-md"+col)
                active.addClass("layui-col-md"+value);
                active.attr("ez-col",value);
            }
        });

        //其他自定义操作
        switch (edittype){
            case 'input-text':
                console.log('input-text');
                break;
            case 'card':
                console.log('card');
                break;
            case 'formcore':
                currentConfig.find("[name=init_express]").val($("#init_express").html());
                currentConfig.find("[name=submit_express]").val($("#submit_express").html());
                currentConfig.find("[name=delete_express]").val($("#delete_express").html());
                currentConfig.find("[name=status_express]").val($("#status_express").html());
                currentConfig.find("[name=form_name]").val($("#form_name").html());
                currentConfig.find("[name=append_head]").val($("#append_head").html());
                currentConfig.find("[name=append_foot]").val($("#append_foot").html());
                break;
            default:
                break;
        }
        layui.form.render();
        currentConfig.show();
    }

    $(document).on("click",".selector",function(e){
        if(!$(this).hasClass("active")){
            rowselect($(this));
        }
        console.log('stopPropagation')
        e.preventDefault();
        e.stopPropagation();
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
        $(this).parents(".active").after(copy);
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
        closeBtn:0,
        title:'配置',
        maxmin: false,
        resize:false,
        content:$(".righttab"),
        success: function(){
        },end: function(){
        }
    });


    //保存
    $(".layui-btn-save").click(function(){

        let formcode=$("body").find("[ez-type=formcore]").attr("ez-encodeformid");
        let form_name=$("#form_name").text();
        let successurl=$("body").find("[ez-type=formcore]").attr("ez-success_url");
        let formsubmiturl=$("body").find("[ez-type=formcore]").attr("ez-formsubmiturl");
        let datasource=$("body").find("[ez-type=formcore]").attr("ez-datasource");
        let init_express=$("#init_express").val();
        let submit_express=$("#submit_express").val();
        let delete_express=$("#delete_express").val();
        let status_express=$("#status_express").val();
        let append_head=$("#append_head_code").val();
        let append_foot=$("#append_foot_code").val();
        if(formcode==undefined||formcode==''){
            formcode=new Date().getTime();
        }
        var formData={};
        var core={};
        core.formcode=formcode;
        core.form_name=form_name;
        core.success_url=successurl;
        core.formsubmiturl=formsubmiturl;
        core.datasource=datasource;

        var ezconfig={};
        $.each($("#formCoreContainer")[0].attributes,function(i,d){
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
        core.init_express=init_express;
        core.submit_express=submit_express;
        core.delete_express=delete_express;
        core.status_express=status_express;
        core.append_head=append_head;
        core.append_foot=append_foot;
        formData.core=core;
        var cards=[];
        $("#centerContainer").find("[ez-type=card]").each(function(){
            var card={};
            let cardname=$(this).find(".layui-card-header").text();
            let col=$(this).attr("ez-col");
            if(cardname==''){
                console.log('卡片名称为空，页面名称将不展示')
            }
            card.cardname=cardname;
            card.col=col;
            var items=[];
            $(this).find(".selector").each(function(){
                var item={};
                var attrMap={};
                var attrmapStr= $(this).attr("ez-attrmap");
                //先获取到attrmap的值，再根据实际情况更新
                if(attrmapStr!=undefined&&attrmapStr!=''){
                    attrMap=JSON.parse(attrmapStr);
                }
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name=='ez-attrmap'){
                        return;
                    }
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            item[cname]=d.value;
                            attrMap[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                item["attrMap"]=attrMap;
                if(item.item_name==''||item.item_name==undefined){
                    item.item_name=new Date().getTime();
                }
                items.push(item);
            })
            card.items=items;
            cards.push(card);
        })
        formData.cards=cards;
        var cloudId=$("#cloudId").val();
        $.post("/topezadmin/formEdit/submitEdit-"+formcode,  {data:JSON.stringify(formData)}, function(data) {
            if(data.success){
                layui.layer.confirm('保存成功，重新加载？', {icon: 1, title:'提示'}, function(index){
                    location.href='/topezadmin/formEdit/loadEdit-'+data.data.EZ_CODE;
                });
            }else{
                layui.layer.alert(data.message);
            }
        }, 'json').fail(function() {
            layui.layer.alert('请求失败，网络异常');
        });
    })


    $("#leftContainer").find(".ez-plugin").each(function(){
        let html=$($(this).find(".template").html());
        var value=html.attr("ez-type");
        var name=$(this).find("span").eq(0).text();
        $(".formitem_type_select").each(function(){
            $(this).append("<option value="+value+">"+name+"</option>");
        })
    })

    $(".layui-btn-core").click(function(){
        $("#formCoreContainer").click();
    })

    initExpress("init_express1","layui-btn-initex","请编辑初始化表单表达式","ace/mode/java");
    initExpress("submit_express1","layui-btn-subex","请编辑提交表单表达式","ace/mode/java");
    initExpress("delete_express1","layui-btn-delex","请编辑删除数据表达式","ace/mode/java");
    initExpress("status_express1","layui-btn-staex","请编辑更改状态表达式","ace/mode/java");
    initExpress("append_head_code1","layui-btn-head","请编辑页头html片段","ace/mode/html");
    initExpress("append_foot_code1","layui-btn-foot","请编辑页脚html片段","ace/mode/html");
    $(document).on("click",".layui-icon-help",function(e){
        window.open($(this).attr("href"))
    })
})


function typeform(edittype){

    if(edittype=='table'
        ||edittype.indexOf('input-checkbox')>-1||edittype.indexOf('input-radio')>-1
        ||edittype.indexOf('select')>-1){
        edittype='select';
    }else if(edittype=='cascader'||edittype=='cascadersql' ){
        edittype='cascader';
    }else if(edittype=='card'||edittype=='formcore'){
    } else{
        edittype='input-text';
    }

    return $(".config-"+edittype);


}

function initExpress(id,btn,title,model){
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

            , success:function(){
                var span=`<span href='`+btn.attr("help")+`' class="layui-icon layui-icon-help"></span>`;
                $(".layui-layer-setwin").prepend(span)
            }   ,btn: [ '确定', '取消' ],
            //  ,btn: ['格式化','确定', '取消' ],
            // 按钮1 的回调
            // btn1: function (){
            //     if(model.indexOf("java")>=0){
            //         var v=select_expresseditor.getValue();
            //         $.post("/ezlist/format.html",  {source:v}, function(data) {
            //             if(data.success){
            //                 select_expresseditor.setValue(data.data.code);
            //             }
            //         }, 'json').fail(function() {
            //             layui.layer.alert('请求失败，网络异常');
            //         });
            //     }else{
            //         layui.layer.msg('保存后自动格式化');
            //     }
            // },
            yes: function(index, layero, that){
                var v=select_expresseditor.getValue();
                $("#"+id.replace("1","")).val(v);
                layer.close(index);

                $(".layui-btn-save").click();
            },
            cancel: function(index, layero, that){
                return true; // 点击该按钮后不关闭弹层
            },
            end:function(){
                pre.hide();
                $(".layui-icon-help").remove();
            }
        });
    })
}
