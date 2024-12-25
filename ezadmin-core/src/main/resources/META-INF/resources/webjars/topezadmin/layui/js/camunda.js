$(function(){
    var dekey=$("body").attr("definitionKey");
    if(dekey){
        var param={};
        //param.definitionKey=dekey;
        param.id=$("#ID").val();
        if($("#ID").val()!=''){
            $.post("/mycamunda/check/exist/"+dekey , param, function(response) {
                if(response.success){
                    var res = response.data;
                    $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn approve   layui-bg-blue ">  审核通过  </button>
                         <button  type="button"   class="  layui-btn    layui-bg-blue reject ">  驳回  </button>
                         <button  type="button"   class="  layui-btn    layui-bg-blue history ">  审核记录  </button>
                        `);
                } else {
                    $("#submitButtonContainer").append(`<button  type="button"   class="  layui-btn  start layui-bg-blue ">
                            申请审核
                            </button><button  type="button"   class="  layui-btn    layui-bg-blue history ">  审核记录  </button>`);
                }
            }, 'json').fail(function () {
                console.log("error");
            });

            $(document).on("click",".approve",function(){
                param.pass=true;
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
                });

            })
            $(document).on("click",".reject",function(){
                param.pass=false;

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
                });

            })

        }
    }
})