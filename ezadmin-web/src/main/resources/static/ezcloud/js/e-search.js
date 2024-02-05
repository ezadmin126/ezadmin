$(function(){
    $(".config-searchcore").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());

        switch (name){
            case 'label':
                active.find('.layui-form-label').html($(this).val());
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
            case 'style':
                active.attr("style",$(this).val());
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
            case 'name':
                active.attr("ez-item_name",$(this).val());
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
            case 'name':
                active.attr("ez-item_name",$(this).val());
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
        }

    });

})
