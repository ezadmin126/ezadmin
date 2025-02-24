
function appendCheckButton(dekey,checkStatus,callback){
    var param={};
    //param.definitionKey=dekey;
    param.id=$("#ID").val();
    if($("#ID").val()!=''){
        $.post("/mycamunda/check/exist/"+dekey , param, function(response) {

                if(response.success){
                    //存在这个审核流
                    if(checkStatus==1) {
                        $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn approve   layui-bg-blue ">  审核通过  </button>
                         <button  type="button"   class="  layui-btn    layui-bg-blue reject ">  驳回  </button>
                        `);
                    }
                } else {
                    if(checkStatus==2||checkStatus==5) {
                        $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn  start layui-bg-blue ">
                            申请审核
                            </button>`);
                    }
                }

          //  $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn    layui-bg-blue history ">  审核记录  </button>`);
            callback();
        }, 'json').fail(function () {
            console.log("error");
        });

        $(document).on("click",".approve",function(){
            if($(this).hasClass("layui-btn-disabled")){
                return false;
            }
            $(this).addClass("layui-btn-disabled");
            $(".reject").addClass("layui-btn-disabled");
            param.pass=true;
            var loadIndex = layer.msg('加载中', {
                icon: 16,
                shade: 0.01,
                time: 0 // 不自动关闭
            });
            $.post("/mycamunda/check/complete/"+dekey , param, function(response) {
                if(response.success){
                    layui.layer.alert("操作成功",function(){
                        location.reload()
                    });
                }else{
                    layui.layer.alert("操作失败："+response.message,function(){
                        location.reload()
                    });
                }

            }, 'json').fail(function () {
                console.log("error");
                $(this).removeClass("layui-btn-disabled");
                $(".reject").removeClass("layui-btn-disabled");
                layui.layer.close(loadIndex)
            });

        })
        $(document).on("click",".reject",function(){
            param.pass=false;
            if($(this).hasClass("layui-btn-disabled")){
                return false;
            }
            $(this).addClass("layui-btn-disabled");
            $(".approve").addClass("layui-btn-disabled");
            var loadIndex = layer.msg('加载中', {
                icon: 16,
                shade: 0.01,
                time: 0 // 不自动关闭
            });
            layer.prompt({title: '请输入审核意见', formType: 2}, function(value, index, elem){
                if(value === '') return elem.focus();
                param.comment=layui.util.escape(value);
                $.post("/mycamunda/check/complete/"+dekey , param, function(response) {
                    if(response.success){
                        layui.layer.alert("操作成功",function(){
                            location.reload()
                        });
                    }else{
                        layui.layer.alert("操作失败："+response.message,function(){
                            location.reload()
                        });
                    }

                }, 'json').fail(function () {
                    console.log("error");
                    $(this).removeClass("layui-btn-disabled");
                    $(".approve").removeClass("layui-btn-disabled");
                    layer.close(loadIndex);
                });
                // 关闭 prompt
                layer.close(index);
            });
        })
        $(document).on("click",".history",function(){
            param.pass=false;
            openModel("/mycamunda/check/history/"+dekey+"?id="+param.id)
        })
        $(document).on("click",".start",function(){
            if($(this).hasClass("layui-btn-disabled")){
                return false;
            }
            $(this).addClass("layui-btn-disabled");
            $.post("/mycamunda/check/start/"+dekey , param, function(response) {
                if(response.success){
                    layui.layer.alert("申请审核成功",function(){
                        location.reload()
                    });
                }else{
                    layui.layer.alert("申请审核失败："+response.message,function(){
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