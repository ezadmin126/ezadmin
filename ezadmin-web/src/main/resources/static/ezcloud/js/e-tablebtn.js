$(function(){
    $(".config-buttoncore").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("keyup",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());
        if(name=='label'){
            active.find("button").html($(this).val());
            active.find("label").html($(this).val());
        }
    });
})