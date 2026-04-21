//0 待提交 1待审核 2审核不通过 3审核通过 4 锁定 自动化审核中 5.待申请审核
function appendCheckButton(dekey, checkStatus, callback) {
    var param = {};
    //param.definitionKey=dekey;
    param.id = $("#ID").val();
    if ($("#ID").val() != '') {
        $.post("/mycamunda/check/exist/" + dekey, param, function (response) {

            if (response.success) {
                //存在这个审核流
                if (checkStatus == 1) {
                    $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn approve   layui-btn-primary  layui-btn layui-border-green ">  审核通过  </button>
                         <button  type="button"   class="  layui-btn  layui-btn-primary  layui-border-red reject ">  驳回  </button>
                        `);
                    $("#submitbtnProxy").hide();
                }
            } else {
                if (checkStatus == 2 || checkStatus == 5 || checkStatus == 0) {
                    $("#submitButtonContainer").append(`<button  type="button"   class="    start  layui-btn-primary  layui-btn  layui-border-blue ">
                            申请审核
                            </button>`);
                }
            }

            //  $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn    layui-bg-blue history ">  审核记录  </button>`);
            //判断有没有callbackcamunda函数，有的话，就执行
            if (typeof callbackcamunda === "function") {
                callbackcamunda();
            }
        }, 'json').fail(function () {
            console.log("error");
        });

        $(document).on("click", ".approve", function () {
            if ($(this).hasClass("layui-btn-disabled")) {
                return false;
            }
            $(this).addClass("layui-btn-disabled");
            $(".reject").addClass("layui-btn-disabled");
            var _thisbtn = $(this);
            var param = {};
            param.id = $("#ID").val();
            param.pass = true;
            var loadIndex = layer.msg('加载中', {
                icon: 16,
                shade: 0.01,
                time: 0 // 不自动关闭
            });
            $.post("/mycamunda/check/complete/" + dekey, param, function (response) {
                layui.layer.close(loadIndex)
                if (response.success) {
                    layer.msg('操作成功', {
                        icon: 1,
                        time: 500
                    }, function () {
                        location.reload()
                    });
                } else {
                    layui.layer.alert("操作失败：" + response.message, function () {
                        location.reload()
                    });
                }

            }, 'json').fail(function () {
                console.log("error");
                _thisbtn.removeClass("layui-btn-disabled");
                $(".reject").removeClass("layui-btn-disabled");
                layui.layer.close(loadIndex)
            });

        })
        $(document).on("click", ".reject", function () {
            var param = {};
            param.id = $("#ID").val();
            param.pass = false;
            if ($(this).hasClass("layui-btn-disabled")) {
                return false;
            }
            var _thisbtn = $(this);
            $(this).addClass("layui-btn-disabled");
            $(".approve").addClass("layui-btn-disabled");
            var loadIndex = layer.msg('加载中', {
                icon: 16,
                shade: 0.01,
                time: 0 // 不自动关闭
            });
            layer.prompt({title: '请输入审核意见', formType: 2}, function (value, index, elem) {
                if (value === '') return elem.focus();
                param.comment = layui.util.escape(value);
                $.post("/mycamunda/check/complete/" + dekey, param, function (response) {
                    if (response.success) {
                        layer.msg('操作成功', {
                            icon: 1,
                            time: 500
                        }, function () {
                            location.reload()
                        });
                    } else {
                        layui.layer.alert("操作失败：" + response.message, function () {
                            location.reload()
                        });
                    }

                }, 'json').fail(function (e) {
                    console.log("error");
                    _thisbtn.removeClass("layui-btn-disabled");
                    $(".approve").removeClass("layui-btn-disabled");
                    layer.close(loadIndex);
                    layer.alert("操作失败")
                });
                // 关闭 prompt
                layer.close(index);
            });
        })
        $(document).on("click", ".history", function () {
            var param = {};
            param.id = $("#ID").val();
            param.pass = false;
            openModel("/mycamunda/check/history/" + dekey + "?id=" + param.id)
        })
        $(document).on("click", ".start", function () {
            var param = {};
            param.id = $("#ID").val();
            if ($(this).hasClass("layui-btn-disabled")) {
                return false;
            }
            var loadIndex = layer.msg('加载中', {
                icon: 16,
                shade: 0.01,
                time: 0 // 不自动关闭
            });
            $(this).addClass("layui-btn-disabled");
            $.post("/mycamunda/check/start/" + dekey, param, function (response) {
                layer.close(loadIndex)
                if (response.success) {
                    layer.msg('操作成功', {
                        icon: 1,
                        time: 500
                    }, function () {
                        location.reload()
                    });
                } else {
                    layui.layer.alert("申请审核失败：" + response.message, function () {
                        location.reload()
                    });
                }

            }, 'json').fail(function () {
                console.log("error");
                $(this).removeClass("layui-btn-disabled");
            });

        })

    }
}



