



$(function(){
    $(".config-colcore").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("change",function() {
        let name = $(this).attr("name");
        let active = $(".active");
        active.attr("ez-" + name, $(this).val());
        switch (name){
            case 'label':
                active.find("div").eq(0).html($(this).val());
                break;
        }
    });
})