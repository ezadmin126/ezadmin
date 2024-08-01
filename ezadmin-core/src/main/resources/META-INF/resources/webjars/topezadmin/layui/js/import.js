
        $(function(){
            var url="/topezadmin/form/form-import?importname=" + $(this).attr("importname")
                + "&importtips=" + $(this).attr("importtips")
                + "&importservice=" + $(this).attr("importservice")
                + "&";
            if (typeof getSearchParams == "function") {
                url=url+ getSearchParams();
            }

        $("[item_name=批量导入]").click(function () {
            var json = ['100%', '100%'];
            var index = layer.open({
                title: $(this).attr("windowname"),
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
                    $(body).find('#submitButtonContainer').hide();
                }
            });
        })
    })
