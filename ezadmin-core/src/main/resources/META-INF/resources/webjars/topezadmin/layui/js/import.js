
    $(function(){
        $("[type=button-import]").click(function () {
            var url="/topezadmin/form/form-import";
            var json = ['100%', '100%'];
            var obutton=$(this);
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
                btn: ["下一步", '完成'],
                // 按钮1 的回调
                btn1: function (index111, layero, that) {
                    var body = layer.getChildFrame('body', index111);

                    var txt = $(document).find(".layui-layer-btn0").text().trim();
                    if (txt == "下一步") {
                        $(document).find(".layui-layer-btn0").text("开始导入");
                        $(body).find('#next').click();
                    }
                    if (txt == "开始导入") {
                        $(body).find('#import').click();
                        $(document).find(".layui-layer-btn0").text("上一步");
                    }
                    if (txt == "上一步") {
                        $(document).find(".layui-layer-btn0").text("下一步");
                        $(body).find('#pre').click();
                    }

                },
                btn2: function (index222, layero, that) {
                    layer.close(index222);
                    location.reload();
                },
                success: function (layero, indexyyy, that) {
                    var body = layer.getChildFrame('body', indexyyy);
                    $(body).find('#submitButtonContainer').html(
                        "<button class='layui-btn' id='pre' type='button'></button>" +
                        "<button class='layui-btn' id='next' type='button'></button>" +
                        "<button class='layui-btn' id='import' type='button'></button>" +
                        "<button class='layui-btn' id='cancel' type='button'>取消</button>");


                    $(body).find('#importtips').html(obutton.attr("importtips"));
                    $(body).find('#ITEM_ID_IMPORT_ID').attr("name",obutton.attr("importname"));
                    $(body).find('#ITEM_ID_importservice').val( obutton.attr("importservice"));
                    $(body).find('#submitButtonContainer').hide();
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
