/*! ezadmin v1.0.0-SNAPSHOT |
 Home:https://gitee.com/ezadmin/index.html
 Doc:https://ezadmin.gitee.io/index.html/
 | MIT /license */
console.log(" Home:https://gitee.com/ezadmin/index.html\n" +
    " Doc:https://ezadmin.gitee.io/index.html/")
layui.config({
    base: $("#contextName").val() + "/webjars/topezadmin/layui/js/layuimini/",
    version: true
}).extend({
    miniAdmin: "miniAdmin", // layuimini后台扩展
    miniMenu: "miniMenu", // layuimini菜单扩展
    miniTab: "miniTab"//, // layuimini tab扩展
    //miniTheme: "miniTheme"
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
    var fname = (name == undefined || name == "") ? "打开" : name;
    layui.use(['form', 'miniTab'], function () {
        var  miniTab = layui.miniTab;
        // 打开新的窗口
        miniTab.openNewTabByIframe({
            href: url,
            title: fname,
        });
    });
}