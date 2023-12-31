/*! ezadmin v1.0.0-SNAPSHOT |
 Home:https://gitee.com/ezadmin/index.html
 Doc:https://ezadmin.gitee.io/index.html/
 | MIT /license */
console.log(" Home:https://gitee.com/ezadmin/index.html\n" +
    " Doc:https://ezadmin.gitee.io/index.html/")
layui.config({
    base: $("#contextName").val() + "/webjars/ezadmin/layui/js/layuimini/",
    version: true
}).extend({
    miniAdmin: "miniAdmin", // layuimini后台扩展
    miniMenu: "miniMenu", // layuimini菜单扩展
    miniTab: "miniTab", // layuimini tab扩展
    miniTheme: "miniTheme", // layuimini 主题扩展
    miniTongji: "miniTongji", // layuimini 统计扩展
});


function bindEvent(element, eventName, eventHandler) {
    if (element.addEventListener) {
        element.addEventListener(eventName, eventHandler, false);
    } else if (element.attachEvent) {
        element.attachEvent('on' + eventName, eventHandler);
    }
}

// Listen to message from child window
bindEvent(window, 'message', function (e) {
    var uni = e.data.id == undefined ? e.data.url : e.data.id;
    var uniqueName = uni.replace('./', '').replace(/["&'./:=?[\]%]/gi, '-').replace(/(--)/gi, '');
    createSingleTab(e.data.name, e.data.url);
});

function createSingleTab(name, url) {
    var fname = name == undefined || name == "" ? "打开" : name;
    // var uniqueName = url.replace('./', '').replace(/["&'./:=?[\]%]/gi, '-').replace(/(--)/gi, '');
    layui.use(['form', 'miniTab'], function () {
        var form = layui.form,
            layer = layui.layer,
            miniTab = layui.miniTab;

        // 打开新的窗口
        miniTab.openNewTabByIframe({
            href: url,
            title: fname,
        });
       // $("[lay-id='/login/login.html?welcome=1']").find(".layui-tab-close").remove();
    });
}