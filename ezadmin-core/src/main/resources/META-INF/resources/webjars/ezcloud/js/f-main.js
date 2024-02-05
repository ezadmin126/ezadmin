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

    dragula({
        isContainer: function (el) {
            return $(el).hasClass('dragula-container'); // only elements in drake.containers will be taken into account
        },
        moves: function (el, source, handle, sibling) {
            return true; // elements are always draggable by default
        },
        accepts: function (el, target, source, sibling) {
            //改为正向
            //中间
            var iscenter=$(target).attr("id")=='centerContainer';

            var iscard=
                $(el).find("[ez-type=card]").length>0||//是否是左侧模版
                $(el).attr("ez-type")=='card';//是否是中间自己挪动

            var targetBody=$(target).parent().hasClass("layui-card-body");

            if(iscenter&&iscard){

                return true;
            }
            if(targetBody&&!iscard){

                return true;
            }

            return false;
          //  return true; // elements can be dropped in any of the `containers` by default
        },
        invalid: function (el, handle) {
            if($(el).hasClass("tip")){
                return true;
            }
            return false; // don't prevent any drags from initiating by default
        },
        copy: true,
        revertOnSpill: true,
        copySortSource: true
    }).on('drop', function (el, target, source, sibling) {
        console.log('drop::'+el+target);

        var iscenter=$(target).attr("id")=='centerContainer';

        var iscard=
            $(el).find("[ez-type=card]").length>0||//是否是左侧模版
            $(el).attr("ez-type")=='card';//是否是中间自己挪动

        var targetBody=$(target).parent().hasClass("layui-card-body");

        if(iscenter&&iscard){
            dropcall(el,target);
            return true;
        }
        if(targetBody&&!iscard){
            dropcall(el,target);
            return true;
        }

        return false;
    })
    function dropcall(el,target){


        $(target).find(".tip").remove();

        if( $(el).find(".template").length>0){
            var tmp=$(el).find(".template");
            tmp.find('[name=default]').attr("name",Math.random());

            $(el).replaceWith($(el).find(".template").html());
        }else{
            return false;
        }
        layui.form.render()
    }
    function rowselect(el){
        let edittype=el.attr("ez-type");
        //样式切换
        $(".active").removeClass("active");
        $(".layui-component-tools").remove();
        el.addClass("active");
        if(edittype!='formcore'){//表单不允许复制
            el.append(tools);
        }
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
                currentConfig.find("[name=init_express]").val($("#initcode").html());
                currentConfig.find("[name=submit_express]").val($("#subcode").html());
                currentConfig.find("[name=delete_express]").val($("#delcode").html());
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
            $(this).attr("name",Math.random());
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




    //保存
    $(".layui-btn-save").click(function(){

        let formcode=$("body").find("[ez-type=formcore]").attr("ez-encodeformid");
        let form_name=$("#form_name").text();
        let successurl=$("body").find("[ez-type=formcore]").attr("ez-success_url");
        let formsubmiturl=$("body").find("[ez-type=formcore]").attr("ez-formsubmiturl");
        let datasource=$("body").find("[ez-type=formcore]").attr("ez-datasource");
        let initcode=$("#initcode").html();
        let subcode=$("#subcode").html();
        let delcode=$("#delcode").html();
        let append_head=$("#append_head").html();
        let append_foot=$("#append_foot").html();
        if(formcode==undefined||formcode==''){
            formcode= Math.random();
        }

        var formData={};
        var core={};
        core.formcode=formcode;
        core.form_name=form_name;
        core.success_url=successurl;
        core.formsubmiturl=formsubmiturl;
        core.datasource=datasource;
        core.initcode=initcode;
        core.subcode=subcode;
        core.delcode=delcode;
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
                $.each($(this)[0].attributes,function(i,d){
                    if(d.name.indexOf('ez-')>=0){
                        var cname=d.name.replace('ez-','');
                        try{
                            item[cname]=d.value;
                        }catch(e){
                            console.log(e);
                        }
                    }
                })
                //item.type=$(this).attr("ez-type");
                if(item.item_name==''||item.item_name==undefined){
                    item.item_name=Math.random();
                }
                items.push(item);
            })
            card.items=items;
            cards.push(card);
        })
        formData.cards=cards;
        $.post("/ezform/submitEdit.html",  {data:JSON.stringify(formData),cloudId:$("#cloudId").val()}, function(data) {
            if(data.success){
                layui.layer.confirm('保存成功，重新加载？', {icon: 1, title:'提示'}, function(index){
                    location.href='/ezform/loadEdit.html?cloudId='+$("#cloudId").val();
                });
            }else{
                layui.layer.alert(data.message);
            }

        }, 'json').fail(function() {
            layui.layer.alert('请求失败，网络异常');
        });
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
