// InfoList Footer Script
(function () {
    console.log('InfoList footer loaded');

    // 添加页面底部的统计信息
    var stats = {
        loadTime: new Date().toISOString(),
        pageUrl: window.location.href
    };

    if (window.layui) {
        layui.use(['layer', 'jquery'], function () {
            var $ = layui.jquery;

            // 添加页面加载完成提示
            $(document).ready(function () {
                console.log('Page fully loaded at:', stats.loadTime);
            });
        });
    }
})();