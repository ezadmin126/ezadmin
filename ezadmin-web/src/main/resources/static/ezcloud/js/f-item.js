$(function(){
    $(".config-formcore").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());
        switch (name){
            case 'form_name':
               $('#form_name').html($(this).val());
                break;
            case 'init_express':
                active.find('#initcode').html( $(this).val());
                break;
            case 'submit_express':
                active.find('#subcode').html( $(this).val());
                break;
            case 'delete_express':
                active.find('#delcode').html( $(this).val());
                break;
            case 'append_head':
                $('#append_head').html( $(this).val());
                break;
            case 'append_foot':
                $('#append_foot').html( $(this).val());
                break;
        }

    });
    $(".config-card").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());
        switch (name){
            case 'cardname':
                var h=active.find(".layui-card-header");
                if(h.length>0){
                    active.find(".layui-card-header").text($(this).val());
                }else{
                    active.find('.layui-card').prepend(
                        '<div class="layui-card-header">'+ $(this).val()+'</div>');
                }
                break;
        }

    });


    $(".config-input-text").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());

        switch (name){
            case 'label':
                active.find('.layui-form-label').html($(this).val());
                break;
            case 'item_name':
                active.find('input,textarea').attr('item_name',$(this).val())
                break;
            case 'placeholder':
                active.find('input,textarea').attr("placeholder",$(this).val());
                break;
            case 'style':
                active.attr("style",$(this).val());
                break;
            case 'top_desc':
                active.find(".top_desc").remove();
                if($(this).val()!='') {
                    active.find(".layui-input-block").prepend("<div class=\"layui-form-mid layui-text-em top_desc\">" + $(this).val() + "</div>");
                }
                break;
            case 'item_desc':
                active.find(".item_desc").remove();
                if($(this).val()!=''){
                    active.find(".layui-input-block").append("<div class=\"layui-form-mid layui-text-em item_desc\">"+$(this).val()+"</div>");
                }
                break;
        }
        layui.form.render();
    });

    $(".config-textarea").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());

        switch (name){
            case 'label':
                active.find('.layui-form-label').html($(this).val());
                break;
            case 'item_name':
                active.find('input,textarea').attr('item_name',$(this).val())
                break;
            case 'placeholder':
                active.find('input,textarea').attr("placeholder",$(this).val());
                break;
            case 'style':
                active.find('input,textarea').attr("style",$(this).val());
                break;
            case 'top_desc':
                active.find(".top_desc").remove();
                if($(this).val()!='') {
                    active.find(".layui-input-block").prepend("<div class=\"layui-form-mid layui-text-em top_desc\">" + $(this).val() + "</div>");
                }
                break;
            case 'item_desc':
                active.find(".item_desc").remove();
                if($(this).val()!=''){
                    active.find(".layui-input-block").append("<div class=\"layui-form-mid layui-text-em item_desc\">"+$(this).val()+"</div>");
                }
                break;
        }

    });

    $(".config-select").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());

        switch (name){
            case 'label':
                active.find('.layui-form-label').html($(this).val());
                break;
            case 'item_name':
                active.find('input,textarea').attr('item_name',$(this).val())
                break;
            case 'url':
                active.attr("ez-url",$(this).val());
                break;
            case 'placeholder':
                active.find('input,textarea').attr("placeholder",$(this).val());
                break;
            case 'style':
                active.attr("style",$(this).val());
                break;
            case 'data':
                var datatype=$(".config-select").find('[name=datatype]').val();
                var data=$(this).val();
                if(datatype==''||datatype==undefined){
                    if(data=='yesno'||data=='delete'){
                        active.find('select').html('');
                        active.find('select').append('<option>是</option><option>否</option>')
                    }
                    if(data=='status'){
                        active.find('select').html('');
                        active.find('select').append('<option>启用</option><option>禁用</option>')
                    }
                    if(data.indexOf('\n')>=0){
                        active.find('select').html('');
                        $.each(data.split('\n'),function(i,d){
                            var val=d;
                            if(d.indexOf(':')>0){
                                val=d.split(':')[1];
                            }
                            active.find('select').append('<option>'+val+'</option>');
                        })
                    }

                }else if(datatype=='JSON'){
                    var j=JSON.parse(data);
                    active.find('select').html('');
                    $.each(j,function(i,j){
                        active.find('select').append('<option>'+j.V+'</option>');
                    })
                }
                break;
            case 'top_desc':
                active.find(".top_desc").remove();
                if($(this).val()!='') {
                    active.find(".layui-input-block").prepend("<div class=\"layui-form-mid layui-text-em top_desc\">" + $(this).val() + "</div>");
                }
                break;
            case 'item_desc':
                active.find(".item_desc").remove();
                if($(this).val()!=''){
                    active.find(".layui-input-block").append("<div class=\"layui-form-mid layui-text-em item_desc\">"+$(this).val()+"</div>");
                }
                break;
            case 'data':
                if($(".config-form:visible").find('[name=datatype]').val()==''||
                    $(".config-form:visible").find('[name=datatype]').val()=='TEXT'){
                    //先清空选项
                    active.find('.layui-input-block').html('');
                    if(type=='select-search'||type=='select'){
                        active.find('.layui-input-block').html('<select><option>请选择</option></select>');
                    }
                    console.log('text:'+ JSON.stringify($(this).val().split('\n')));
                    $.each($(this).val().split('\n'),function(i,j){
                        if(type.indexOf('input-radio')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="radio"  value="'+j+'" title="'+j+'">');
                        }
                        if(type.indexOf('input-checkbox')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="checkbox"  value="'+j+'" title="'+j+'">');
                        }
                        if(type.indexOf('input-select')>-1&&j!=''){
                            active.find('select').append('<option   value="'+j+'"  >'+j+'</option>');
                        }
                    })
                    form.render();
                }
                if(
                    $(".config-form:visible").find('[name=datatype]').val()=='JSON'){

                    var json=JSON.parse($(this).val())
                    //先清空选项
                    active.find('.layui-input-block').html('');
                    if(type=='select-search'||type=='select'){
                        active.find('.layui-input-block').html('<select><option>请选择</option></select>');
                    }
                    $.each(json,function(i,j){
                        //补充选项
                        console.log('json:'+j.K+'--'+j.V);
                        if(type.indexOf('input-radio')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="radio"  value="'+j.K+'" title="'+j.V+'">');
                        }
                        if(type.indexOf('input-checkbox')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="checkbox"  value="'+j.K+'" title="'+j.V+'">');
                        }
                        if(type.indexOf('input-select')>-1&&j!=''){
                            active.find('select').append('<option   value="'+j.K+'"  >'+j.V+'</option>');
                        }
                    })
                    form.render();

                }
                break;
        }
        layui.form.render();
    });


    $(".config-cascader").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());

        switch (name){
            case 'label':
                active.find('.layui-form-label').html($(this).val());
                break;
            case 'item_name':
                active.find('input,textarea').attr('item_name',$(this).val())
                break;
            case 'url':
                active.attr("ez-url",$(this).val());
                break;
            case 'placeholder':
                active.find('input,textarea').attr("placeholder",$(this).val());
                break;
            case 'style':
                active.attr("style",$(this).val());
                break;
            case 'data':
                if($(".config-form:visible").find('[name=datatype]').val()==''||
                    $(".config-form:visible").find('[name=datatype]').val()=='TEXT'){
                    //先清空选项
                    active.find('.layui-input-block').html('');
                    if(type=='select-search'||type=='select'){
                        active.find('.layui-input-block').html('<select><option>请选择</option></select>');
                    }
                    console.log('text:'+ JSON.stringify($(this).val().split('\n')));
                    $.each($(this).val().split('\n'),function(i,j){
                        if(type.indexOf('input-radio')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="radio"  value="'+j+'" title="'+j+'">');
                        }
                        if(type.indexOf('input-checkbox')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="checkbox"  value="'+j+'" title="'+j+'">');
                        }
                        if(type.indexOf('input-select')>-1&&j!=''){
                            active.find('select').append('<option   value="'+j+'"  >'+j+'</option>');
                        }
                    })
                    form.render();
                }
                if(
                    $(".config-form:visible").find('[name=datatype]').val()=='JSON'){

                    var json=JSON.parse($(this).val())
                    //先清空选项
                    active.find('.layui-input-block').html('');
                    if(type=='select-search'||type=='select'){
                        active.find('.layui-input-block').html('<select><option>请选择</option></select>');
                    }
                    $.each(json,function(i,j){
                        //补充选项
                        console.log('json:'+j.K+'--'+j.V);
                        if(type.indexOf('input-radio')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="radio"  value="'+j.K+'" title="'+j.V+'">');
                        }
                        if(type.indexOf('input-checkbox')>-1&&j!=''){
                            active.find('.layui-input-block').append('<input type="checkbox"  value="'+j.K+'" title="'+j.V+'">');
                        }
                        if(type.indexOf('input-select')>-1&&j!=''){
                            active.find('select').append('<option   value="'+j.K+'"  >'+j.V+'</option>');
                        }
                    })
                    form.render();

                }
                break;
            case 'top_desc':
                active.find(".top_desc").remove();
                if($(this).val()!='') {
                    active.find(".layui-input-block").prepend("<div class=\"layui-form-mid layui-text-em top_desc\">" + $(this).val() + "</div>");
                }
                break;
            case 'item_desc':
                active.find(".item_desc").remove();
                if($(this).val()!=''){
                    active.find(".layui-input-block").append("<div class=\"layui-form-mid layui-text-em item_desc\">"+$(this).val()+"</div>");
                }
                break;
        }

    });

    $(".config-uploadimage").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());

        switch (name){
            case 'label':
                active.find('.layui-form-label').html($(this).val());
                break;
            case 'item_name':
                active.find('input,textarea').attr('item_name',$(this).val())
                break;
            case 'url':
                active.attr("ez-url",$(this).val());
                break;
            case 'placeholder':
                active.find('input,textarea').attr("placeholder",$(this).val());
                break;
            case 'style':
                active.attr("style",$(this).val());
                break;
            case 'data':
                var datatype=$(".config-select").find('[name=datatype]').val();
                var data=$(this).val();
                if(datatype==''||datatype==undefined){
                    if(data=='yesno'||data=='delete'){
                        active.find('select').html('');
                        active.find('select').append('<option>是</option><option>否</option>')
                    }
                    if(data=='status'){
                        active.find('select').html('');
                        active.find('select').append('<option>启用</option><option>禁用</option>')
                    }
                    if(data.indexOf('\n')>=0){
                        active.find('select').html('');
                        $.each(data.split('\n'),function(i,d){
                            var val=d;
                            if(d.indexOf(':')>0){
                                val=d.split(':')[1];
                            }
                            active.find('select').append('<option>'+val+'</option>');
                        })
                    }

                }else if(datatype=='JSON'){
                    var j=JSON.parse(data);
                    active.find('select').html('');
                    $.each(j,function(i,j){
                        active.find('select').append('<option>'+j.V+'</option>');
                    })
                }
                break;
            case 'top_desc':
                active.find(".top_desc").remove();
                if($(this).val()!='') {
                    active.find(".layui-input-block").prepend("<div class=\"layui-form-mid layui-text-em top_desc\">" + $(this).val() + "</div>");
                }
                break;
            case 'item_desc':
                active.find(".item_desc").remove();
                if($(this).val()!=''){
                    active.find(".layui-input-block").append("<div class=\"layui-form-mid layui-text-em item_desc\">"+$(this).val()+"</div>");
                }
                break;
        }

    });

})