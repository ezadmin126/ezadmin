$(function () {
    $('#searchbox').keypress(function (e) {
        var key = e.which; //e.which是按键的值
        if (key == 13) {
            $("#searchForm").submit();
        }
    });
});
