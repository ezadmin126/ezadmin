$(document).ready(function () {
    //$(".layui-card-body").addClass("dragula-container");

    var id = $("#ID").val();

    $("#submitbtn").unbind("click");


    $("[editor='formitem']").find("label")
        .append("<i class=\"rowedit layui-icon-edit layui-icon edit\"></i>" +
            "<i class='rowdelete edit layui-icon layui-icon-delete    '></i>");
    $("[editor='formitem']").find("label").append("<i class=\"rowadd pre edit  layui-icon-return layui-icon\" title='右侧插入'></i>");
    $("[editor='formitem']").find("label").append("<i class=\"rowmove layui-icon-transfer layui-icon move\"></i>");
    $(".edit").css("margin", "5px").css("cursor", "pointer");
    $(".move").css("margin", "5px").css("cursor", "move");
    $(".layui-form-label").css("min-width", "200px");
    $(".layui-input-block").css("margin-left", "240px");
    var drake = dragula({
        isContainer: function (el) {
            return el.classList.contains('dragula-container');
        },
        // copy: true,
        //  removeOnSpill: true,
        revertOnSpill: true,
        //copySortSource: true,
        moves: function (el, container, handle) {

            return handle.classList.contains('rowmove');
        }, accepts: function (el, target) {

            return true;
        }
    }).on('drop', function (el) {
        let encryptFormId = $("#FORM_ID").val();

        var data = {};
        var config = [];
        $("#inputForm").find(".layui-card").each(function () {
            var json = {};
            json['group'] = $(this).find(".layui-card-header").text();
            var itemString = '';
            $(this).find(".formitem").each(function (i, v) {
                itemString += $(v).attr("item_name") + ',';
            })
            json['items'] = itemString;
            config.push(json);
        })
        data['list'] = JSON.stringify(config);
        data['type'] = 'formitem';
        data['ENCRYPT_FORM_ID'] = encryptFormId;
        debugger;
        $.getJSON($("#contextName").val() + "/ezadmin/core/updateItemSort.html", data, function (result) {
            if (data.success) {
                console.log("排序成功");
            }
        });
    })
    $('body').on('click', function (e) {
        if ($(e.target).hasClass('rowedit')) {
            editFormItem(e);
        }
        if ($(e.target).hasClass('rowadd')) {
            addFormItem(e);
        }
        if ($(e.target).hasClass('rowdelete')) {
            deleteFormItem(e);
        }
        if ($(e.target).hasClass('rowmove')) {
            alert('拖动排序')
        }
    })

    function editFormItem(e) {
        if ($(e.target).parents(".formitem").length > 0) {
            var itemName = $(e.target).parents(".formitem").attr("item_name")
            var itemLabel = $(e.target).parent().text();
            var formId = $("#FORM_ID").val()
            var index = layer.open({
                title: '列表配置',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-formItem?ID=' + formId + '&item_name=' + itemName + '&label=' + itemLabel,
                moveOut: true
                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                    $(body).find('#submitbtn').click();
                }
            });
        }
    }

    function addFormItem(e) {
        if ($(e.target).parents(".formitem").length > 0) {
            var itemName = $(e.target).parents(".formitem").attr("item_name")
            var itemLabel = $(e.target).parent().text();
            var formId = $("#FORM_ID").val()
            var index = layer.open({
                title: '列表配置',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-formItem?ID=' + formId + '&pre=' + itemName + '&label=' + itemLabel,
                moveOut: true
                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                    $(body).find('#submitbtn').click();
                }
            });
        }
    }

    function deleteFormItem(e) {
        if ($(e.target).parents(".formitem").length > 0) {
            var itemName = $(e.target).parents(".formitem").attr("item_name");
            var formId = $("#FORM_ID").val()
            if (confirm('确定要删除么')) { //只有当点击confirm框的确定时，该层才会关闭
                $.get($("#contextName").val() + "/ezadmin/form/doDelete-formItem?item_name=" + itemName + "&ID=" + formId, function (data) {
                    if (data.success) {
                        layer.alert("删除成功");
                        location.reload();
                    }
                })
            }
        }
    }
});