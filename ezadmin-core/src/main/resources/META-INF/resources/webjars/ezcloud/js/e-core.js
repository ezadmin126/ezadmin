$(function(){
    $(".config-listcore").find("input,textarea").not("[type=radio]")
        .not("[type=checkbox]").on("change",function() {
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
})