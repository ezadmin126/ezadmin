
    $(function(){
        $("[type=button-import]").click(function () {
            var url="/topezadmin/form/form-import";
            var json = ['100%', '100%'];
            var obutton=$(this);
            if(obutton.attr("importname")==''||obutton.attr("importname")==undefined){
                layer.msg("importname未配置，请检查")
                return false;
            }
            if(obutton.attr("importservice")==''||obutton.attr("importservice")==undefined){
                layer.msg("importservice未配置，请检查")
                return false;
            }
            var param=obutton.attr("url");
            if(param) {
                try {
                    // 创建 URLSearchParams 对象
                    const queryParams = new URLSearchParams(param);
                } catch (e) {
                    layer.msg("url配置错误，请检查，格式为：  k=v&k1=v1")
                    return false;
                }
            }
            var index = layer.open({
                title: obutton.attr("windowname")||'批量导入',
                type: 2,
                shade: 0.2,
                maxmin: false,
                btnAlign: 'c',
                shadeClose: true,
                area: json,
                content:  url ,
                moveOut: true,

                success: function (layero, indexyyy, that) {
                    var body = layer.getChildFrame('body', indexyyy);
                    $(body).find('#submitButtonContainer').html(
                        "<button class='layui-btn   layui-bg-blue layui-btn-disabled'   id='pre' type='button'>上一步</button>" +
                        "<button class='layui-btn layui-bg-blue' id='next' type='button'>下一步</button>" +
                        "<button class='layui-btn layui-bg-blue layui-btn-disabled' id='importBatch'    type='button'>开始导入</button>" +
                        "<button class='layui-btn layui-bg-blue' id='cancel' type='button'>关闭</button>");

                    $(body).on("click","#cancel",function(){
                        layer.close(indexyyy);
                    })



                    $(body).find('#importtips').html(obutton.attr("importtips"));
                    $(body).find('#ITEM_ID_IMPORT_ID').attr("name",obutton.attr("importname"));
                    $(body).find('#ITEM_ID_importservice').val( obutton.attr("importservice"));
                    //参数带过去
                    var param=obutton.attr("url");
                    if(param){
                        try{
                        // 创建 URLSearchParams 对象
                        const queryParams = new URLSearchParams(param);
// 将查询参数转换为 JSON 数组
                        queryParams.forEach((value, key) => {
                                    $(body).find('#inputForm').append("<input type='hidden' name='"+key+"' " +
                                       "value='"+value+"'>");
                        });
                        }catch (e){
                            console.log("url 格式不对， k=v&k1=v1 ,"+queryString)
                        }
                    }

                }
            });
        })
    })
