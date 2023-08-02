layui.use(['element','code','layer'],function (){
    var $=layui.$;
    layui.code({
        about: false,
        title: '模版代码',
        height: '300px',
        tools: ['full', 'window', 'copy']
    });
    $("body").append("    <ul id=\"dianti\" class=\"layui-menu site-dir layui-layer-wrap\"   > </ul>\n")
    $(".layui-card").each(function(){
        var li= "<li >"+
            "<a href='#"+$(this).attr("id")+"' ><cite  >"+$(this).find(".layui-card-header").text()+"</cite></a>"
        "</li>"
        $("#dianti").append(li);
    })

    if($("#dianti").length>0&&$("#dianti").children().length>1){
        layer.open({
            title: '目录'
            ,skin:'layui-layer-dir'
            ,type:1
            ,offset: 'r'
            ,shade:0
            ,fixed:true
            ,content:  $("#dianti")
        });

    }
})