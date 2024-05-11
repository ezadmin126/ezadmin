$(function(){
    $(".config-listcore").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());
        switch (name){
            case 'listname':
                $('#listname').html( $(this).val());
                break;
            case 'append_head':
                $('#append_head').html( $(this).val());
                break;
            case 'append_foot':
                $('#append_foot').html( $(this).val());
                break;
            case 'select_express':
                active.find('#select_express').html( $(this).val());
                break;
            case 'count_express':
                active.find('#count_express').html( $(this).val());
                break;
            case 'orderby_express':
                active.find('#orderby_express').html( $(this).val());
                break;
            case 'groupby_express':
                active.find('#groupby_express').html( $(this).val());
                break;
            case 'displayorder_express':
                active.find('#displayorder_express').html( $(this).val());
                break;
        }
    });


    $("#left-search").find(".ez-plugin").each(function(){
        let html=$($(this).find(".template").html());
        var value=html.attr("ez-type");
        var name=$(this).find("span").eq(0).text();
        $(".search_type_select").each(function(){
            $(this).append("<option value="+value+">"+name+"</option>");
        })
    })

    $("#left-colitem").find(".ez-plugin").each(function(){
        let html=$($(this).find(".template").html());
        var value=html.attr("ez-type");
        var name=$(this).find("span").eq(0).text();
        $("#col_type_select").append("<option value="+value+">"+name+"</option>");
    })

    $(".list-item-hidden").show()
    $(".list-item-hidden").css("color","red");

})