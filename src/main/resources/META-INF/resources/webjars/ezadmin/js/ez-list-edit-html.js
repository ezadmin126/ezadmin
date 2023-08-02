$(document).ready(function () {
    var listId = $("#LIST_ID").val();
    var encryptListId = $("input[name=ENCRYPT_LIST_ID]").val();
    $(".layui-form-label").css("z-index", 1000000);


    if (encryptListId != null && encryptListId != "") {

        $("#submitBtn").parent().html($("<button type=\"button\" class=\" addSearch layui-btn layui-btn-normal layui-btn-sm  \"><i class=\"layui-icon-add-1 layui-icon\"></i>新增搜索</button>" +
            "<button type=\"button\" class=\" navadd layui-btn layui-btn-normal layui-btn-sm  \"><i class=\"layui-icon-add-1 layui-icon\"></i>新增导航</button>" +
            "<button type=\"button\" class=\" tablebuttonadd layui-btn layui-btn-normal layui-btn-sm  \"><i class=\"layui-icon-add-1 layui-icon\"></i>新增表头按钮</button>" +
            "<button type=\"button\" class=\" rowbuttonadd layui-btn layui-btn-normal layui-btn-sm  \"><i class=\"layui-icon-add-1 layui-icon\"></i>新增行按钮</button>" +
            "<button type=\"button\" class=\" coladd layui-btn layui-btn-normal layui-btn-sm  \"><i class=\"layui-icon-add-1 layui-icon\"></i>新增数据列</button>" +
            "<button type=\"button\" class=\"editList layui-btn layui-btn-normal layui-btn-sm \"><i class=\"layui-icon-edit layui-icon\"></i>编辑</button>" +
            "<a target='_blank'  href='/ezadmin/list/trace-" + encryptListId + "' type=\"button\" class=\"  layui-btn layui-btn-normal layui-btn-sm \"><i class=\"layui-icon-survey layui-icon\"></i>日志调试</a>" +
            "<button    type=\"button\" class=\"saveButton layui-btn layui-btn-normal layui-btn-sm \"><i class=\"layui-icon-video layui-icon\"></i>预览</button>" +
            "<button    type=\"button\" class=\"clear layui-btn layui-btn-normal layui-btn-sm \"><i class=\"layui-icon-refresh layui-icon\"></i>清理缓存</button>"));
    } else {
        $("#submitBtn").parent().html($(
            "<button type=\"button\" class=\"editList layui-btn layui-btn-normal layui-btn-sm \"><i class=\"layui-icon-edit layui-icon\"></i>编辑</button>"
        ));
    }


    $("#searchForm .list-item").show();

    $(".order-icon").removeClass('order-icon');
    $(".edit-table-head ").unbind("click");

    $(".layui-fluid").append("<div class=\"layui-card\">\n" +
        "  <div class=\"layui-card-header\">快速生成</div>\n" +
        "  <div class=\"layui-card-body\">\n" +
        "\t <form method='post' class=\"layui-form layui-form-pane\" action=\"" + $("#contextName").val() + "/ezadmin/list/fastlist.html\">\n" +
        "\t  <div class=\"layui-form-item\" pane>\n" +
        "\t\t<label class=\"layui-form-label\">是否生成表单</label>\n" +
        "\t\t<div class=\"layui-input-block\">\n" +
        "\t\t  <input type=\"checkbox\" name=\"generateForm\" value=1 lay-skin=\"switch\" lay-text=\"开启|关闭\" title=\"生成表单\" >\n" +
        "\t\t</div>\n" +
        "\t  </div>\n" +
        "\t  <div class=\"layui-form-item\" pane>\n" +
        "\t\t<label class=\"layui-form-label\">URL唯一值</label>\n" +
        "\t\t<div class=\"layui-input-block\">\n" +
        "\t\t  <input type=\"text\" class='layui-input' name=\"encodeId\"    >\n" +
        "\t\t</div>\n" +
        "\t  </div>\n" +
        "\t  <div class=\"layui-form-item\" pane>\n" +
        "\t\t<label class=\"layui-form-label\">表达式</label>\n" +
        "\t\t<div class=\"layui-input-block\">\n" +
        "\t\t\t<textarea name=\"fasttext\" style='height:300px' placeholder=\"根据sql生成一个包含导出的列表，如果选择生成表单，则根据字段名+第一个表名生成一个表单\" class=\"layui-textarea\"></textarea>\n" +
        "\t\t</div>\n" +
        "\t  </div>\n" +
        "\t  <div class=\"layui-form-item\">\n" +
        "\t\t<div class=\"layui-input-block\">\n" +
        "\t\t\t<button  type='submit' class=\"  layui-btn layui-btn-primary layui-border-blue \"    >SQL快速生成列表</button> 生成后请重启,修改文件无需重启，只需单独编译，可以使用idea的自动编译功能\n" +
        "\t\t\t  \n" +
        "\t\t</div>\n" +
        "\t  </div>\n" +
        "\t </form>\n" +
        " </div>\n" +
        "</div>");

    if (encryptListId != null && encryptListId != "") {
        if ($(".layui-tab-title").length <= 0) {
            $(".searchWrap").prepend('<ul class="layui-tab-title">'
                + '</ul>');
        } else {
            $(".layui-tab-title").find("li").addClass("addNav");
            $(".layui-tab-title").find("a").attr("href", "#");

            $(".layui-tab-title").find("a").append("<i class='navedit edit layui-icon-edit layui-icon'></i>"
                + "<i class='navdelete edit layui-icon-delete layui-icon   '></i>"
                + "<i class='navadd pre edit  layui-icon-return layui-icon    '></i>"
                + "<i class='navmove move layui-icon-transfer layui-icon   '></i>");
        }


    }

    $(".tableButton").children().each(function (item) {
        $(this).append("<i class=\"tablebuttonedit layui-icon-edit layui-icon   edit \" title='编辑'></i>");
        $(this).append("<i class=\"tablebuttondelete layui-icon-delete layui-icon   edit\" title='删除'></i>");
        $(this).append("<i class=\"tablebuttonadd pre layui-icon-return layui-icon    edit\" title='新增'></i>");
        $(this).append("<i class=\"tablebuttonmove layui-icon-transfer layui-icon move \" title='拖拽移动'></i>");
    })


    $(".rowButtons").find("button").each(function (item) {

        $(this).removeClass("ezopenbutton");
        $(this).append("<i class=\"rowbuttonedit layui-icon-edit layui-icon   edit \" title='编辑'></i>");
        $(this).append("<i class=\"rowbuttondelete layui-icon-delete layui-icon  edit\" title='删除'></i>");
        $(this).append("<i class=\"rowbuttonadd pre layui-icon-return layui-icon    edit\" title='新增'></i>");
        $(this).append("<i class=\"rowbuttonmove layui-icon-transfer layui-icon move \" title='拖拽移动'></i>");
    })
    $(".rowButtons").css("max-width", "175px")
    $("th").css("min-width", "200px")
    // $("th").eq(0).css("max-width","100px")
    // $("th").eq(0).css("min-width","auto")


    // $(".dropdown-menu").addClass('dragula-container');

    $("#searchForm .layui-form-item").addClass('dragula-container');

    $("#searchForm .layui-form-label").append("<i class=\"layui-icon-edit layui-icon editSearch edit \" title='编辑'></i>");
    $("#searchForm .layui-form-label").append("<i class=\"layui-icon-delete layui-icon deleteSearch edit\" title='删除'></i>");

    $("#searchForm .edit-unionsearch").append("<i class=\"layui-icon-edit layui-icon editSearch edit \" title='编辑'></i>");
    $("#searchForm .edit-unionsearch").append("<i class=\"layui-icon-delete layui-icon deleteSearch edit\" title='删除'></i>");


    $("#searchForm .layui-form-label").css("min-width", "200px");


    $(".edit-table-head").after("<i class=\"coledit edit layui-icon-edit layui-icon \" title='编辑'></i>");
    $(".edit-table-head").after("<i class=\"coldelete edit layui-icon-delete layui-icon\" title='删除' ></i>");


    $(".edit").css("margin", "5px").css("cursor", "pointer");
    $(".move").css("margin", "5px").css("cursor", "move");


    var drake = dragula({
        isContainer: function (el) {
            return el.classList.contains('dragula-container');
        },
        // copy: true,
        //    removeOnSpill: true,
        revertOnSpill: true,
        //copySortSource: true,
        moves: function (el, container, handle) {
            // console.log(handle.classList)
            return handle.classList.contains('rowmove');
        }, accepts: function (el, target) {
            // console.log(target.classList)
            return target.classList.contains('dragula-container');
        }
    }).on('drop', function (el) {
        var s = '';
        $(".layui-form-item .list-item").each(function (i, v) {

            s += $(v).attr("ez-name") + ',';
        })
        $.get($("#contextName").val() + "/ezadmin/core/updateItemSort.html?type=search&ENCRYPT_LIST_ID=" + encryptListId + "&items=" + s, function (data) {
            if (data.success) {
                // location.reload();
                console.log("排序成功");
            }
        })

    })
    var drakeNav = dragula({
        isContainer: function (el) {
            return el.classList.contains('layui-tab-title');
        },
        // copy: true,
        //    removeOnSpill: true,
        revertOnSpill: true,
        //copySortSource: true,
        moves: function (el, container, handle) {
            // console.log(handle.classList)
            return handle.classList.contains('navmove');
        }, accepts: function (el, target) {
            // console.log(target.classList)
            return target.classList.contains('layui-tab-title');
        }
    }).on('drop', function (el) {
        var s = '';

        $("#list-tab li").each(function (i, v) {
            if ($(v).attr("name") == null) {
                s += $(v).text() + ',';
            } else {
                s += $(v).attr("name") + ',';
            }
        })
        console.log("nav sort " + s)
        $.get($("#contextName").val() + "/ezadmin/core/updateItemSort.html?type=nav&ENCRYPT_LIST_ID=" + encryptListId + "&items=" + s, function (data) {
            if (data.success) {
                // location.reload();
                console.log("排序成功");
            }
        })

    })
    try {


        var drakeColumn = dragula({
            isContainer: function (el) {
                return el.tagName == 'tr';
            },
            // copy: true,
            //    removeOnSpill: true,
            revertOnSpill: true,
            //copySortSource: true,
            moves: function (el, container, handle) {
                // console.log(handle.classList)
                return handle.classList.contains('colmove');
            }, accepts: function (el, target) {
                // console.log(target.classList)
                return target.tagName == 'tr';
            }
        }).on('drop', function (el) {
            var s = '';
            console.log(el)
            debugger
            $(".dataTables_scrollHeadInner .list-item").each(function (i, v) {
                s += $(v).attr("item_name") + ',';
            })
            console.log(s);
            $.get($("#contextName").val() + "/ezadmin/core/updateItemSort.html?type=column&ENCRYPT_LIST_ID=" + encryptListId + "&items=" + s, function (data) {
                if (data.success) {
                    // location.reload();
                    console.log("排序成功");
                }
            })
        })


    } catch (e) {
    }

    var draketablebutton = dragula({
        isContainer: function (el) {
            return el.classList.contains('tableButton');
        },
        revertOnSpill: true,
        moves: function (el, container, handle) {
            return handle.classList.contains('tablebuttonmove');
        }, accepts: function (el, target) {
            return target.classList.contains('tableButton');
        }
    }).on('drop', function (el) {
        var s = '';
        $(".tableButton button").each(function (i, v) {
            s += $(v).text() + ',';
        })
        $.post($("#contextName").val() + "/ezadmin/core/updateItemSort.html",
            {
                type: 'tablebutton',
                ENCRYPT_LIST_ID: encryptListId,
                items: s,
            }, function (result) {
                if (data.success) {
                    // location.reload();
                    console.log("排序成功" + s);
                }
            });
    })

    var drakerowbutton = dragula({
        isContainer: function (el) {
            return el.classList.contains('layui-btn-container ');
        },
        revertOnSpill: true,
        moves: function (el, container, handle) {
            return handle.classList.contains('rowbuttonmove');
        }, accepts: function (el, target) {
            return target.classList.contains('layui-btn-container ');
        }
    }).on('drop', function (el) {
        var s = '';
        $(".rowButtons button").each(function (i, v) {
            s += $(v).attr("item_name") + ',';
        })
        $.post($("#contextName").val() + "/ezadmin/core/updateItemSort.html",
            {
                type: 'rowbutton',
                ENCRYPT_LIST_ID: encryptListId,
                items: s,
            }, function (result) {
                if (data.success) {
                    // location.reload();
                    console.log("排序成功" + s);
                }
            });
    })


    $('body').on('click', function (e) {
        if ($(e.target).hasClass('editList')) {
            var index = layer.open({
                title: '列表配置',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-listCoreHtml?ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + encryptListId,
                moveOut: true
                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                    $(body).find('#submitbtn').click();
                    editCallBack()
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , cancel: function () {
                    //右上角关闭回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
            });
        }
        ;

        if ($(e.target).hasClass('navedit')) {
            var itemName = $(e.target).parents("li").attr("item_name");


            if (itemName == undefined || itemName == '') {
                layer.confirm('未找到配置名称，确定新增吗？', {icon: 3}, function () {
                    itemName = 'newItem';
                    var index = layer.open({
                        title: '添加导航',
                        type: 2,
                        shade: 0.2,
                        maxmin: true,
                        shadeClose: false,
                        area: ['90%', '90%'],
                        content: '/ezadmin/form/form-listNavHtml?ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + itemName,
                        moveOut: true
                        , btn: ['保存', '关闭']
                        , yes: function (index, layero) {
                            var body = layer.getChildFrame('body', index);
                            var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                            $(body).find('#submitbtn').click();
                            editCallBack()
                        }
                        , btn2: function (index, layero) {
                            //按钮【按钮二】的回调
                            //return false 开启该代码可禁止点击该按钮关闭
                        }
                    });
                }, function () {
                });
            } else {
                var index = layer.open({
                    title: '添加导航',
                    type: 2,
                    shade: 0.2,
                    maxmin: true,
                    shadeClose: false,
                    area: ['90%', '90%'],
                    content: '/ezadmin/form/form-listNavHtml?ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + itemName,
                    moveOut: true
                    , btn: ['保存', '关闭']
                    , yes: function (index, layero) {
                        var body = layer.getChildFrame('body', index);
                        var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                        $(body).find('#submitbtn').click();
                        editCallBack()
                    }
                    , btn2: function (index, layero) {
                        //按钮【按钮二】的回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }
        }
        if ($(e.target).hasClass('navadd')) {
            var itemName = 'newItem';

            var index = layer.open({
                title: '添加导航',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-listNavHtml?ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + itemName,
                moveOut: true
                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                    $(body).find('#submitbtn').click();
                    editCallBack()
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
            });
        }
        if ($(e.target).hasClass('navdelete')) {
            var itemId = $(e.target).parents("li").attr("item_name");
            if (itemId != undefined && itemId != '') {
                if (confirm('确定要删除么')) { //只有当点击confirm框的确定时，该层才会关闭
                    $.get($("#contextName").val() + "/ezadmin/form/doDelete-listNavHtml?ENCRYPT_LIST_ID=" + encryptListId + "&ID="
                        + (itemId === undefined ? "undefined" : itemId), function (data) {
                        if (data.success) {
                            alert("删除成功");
                            location.reload();
                        }
                    })
                }
            } else {
                alert("item_name配置不正确，请手动修改配置文件")
            }
        }

        if ($(e.target).hasClass('editSearch')) {
            var itemName = $(e.target).parents(".list-item").attr("ez-name") || '';

            if (itemName == undefined || itemName == '') {
                layer.confirm('未找到配置名称，确定新增吗？', {icon: 3}, function () {
                    itemName = 'newItem';
                    var index = layer.open({
                        title: '添加搜索项',
                        type: 2,
                        shade: 0.2,
                        maxmin: true,
                        shadeClose: false,
                        area: ['90%', '90%'],
                        content: '/ezadmin/form/form-listSearchHtml?PLUGIN_CODE=input-text&LIST_ID=' + listId + '&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                            + itemName,
                        moveOut: true

                        , btn: ['保存', '关闭']
                        , yes: function (index, layero) {
                            var body = layer.getChildFrame('body', index);
                            // var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                            $(body).find('#submitbtn').click();
                            editCallBack()
                        }
                        , btn2: function (index, layero) {
                            //按钮【按钮二】的回调
                            //return false 开启该代码可禁止点击该按钮关闭
                        }
                    });
                }, function () {
                });
            } else {
                var index = layer.open({
                    title: '添加搜索项',
                    type: 2,
                    shade: 0.2,
                    maxmin: true,
                    shadeClose: false,
                    area: ['90%', '90%'],
                    content: '/ezadmin/form/form-listSearchHtml?PLUGIN_CODE=input-text&LIST_ID=' + listId + '&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                        + itemName,
                    moveOut: true

                    , btn: ['保存', '关闭']
                    , yes: function (index, layero) {
                        var body = layer.getChildFrame('body', index);
                        // var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                        $(body).find('#submitbtn').click();
                        editCallBack()
                    }
                    , btn2: function (index, layero) {
                        //按钮【按钮二】的回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }


        }
        if ($(e.target).hasClass('addSearch')) {
            var itemName = 'newItem';

            var index = layer.open({
                title: '添加搜索项',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-listSearchHtml?PLUGIN_CODE=input-text&LIST_ID=' + listId + '&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                    + itemName,
                moveOut: true

                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    // var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                    $(body).find('#submitbtn').click();
                    editCallBack()
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
            });

        }
        if ($(e.target).hasClass('deleteSearch')) {
            var itemName = $(e.target).parents(".list-item").attr("ez-name") || '';
            if (itemName == '') {
                layer.alert("无法删除，未找到该配置项,请尝试在html配置页面中，手动删除");
                return;
            }
            if (confirm('确定要删除' + itemName + '么')) { //只有当点击confirm框的确定时，该层才会关闭
                $.get($("#contextName").val() + "/ezadmin/form/doDelete-listSearchHtml?ENCRYPT_LIST_ID=" + encryptListId + "&ID="
                    + itemName, function (data) {
                    if (data.success) {
                        alert("删除成功");
                        location.reload();
                    }
                })
            }
        }

        if ($(e.target).hasClass('tablebuttonedit')) {
            var itemName = $(e.target).parents("button").attr("item_name") || $(e.target).parents("span").attr("item_name")
                || $(e.target).attr("item_name");

            $(e.target).attr("item_url", "#");
            $(e.target).unbind("click");
            if (itemName == undefined || itemName == '') {
                layer.confirm('未找到配置名称，确定新增吗？', {icon: 3}, function () {
                    itemName = 'newItem';
                    var index = layer.open({
                        title: '添加按钮',
                        type: 2,
                        shade: 0.2,
                        maxmin: true,
                        shadeClose: false,
                        area: ['90%', '90%'],
                        content: '/ezadmin/form/form-listTableButtonHtml?&PLUGIN_CODE=button-table&OPEN_TYPE=MODEL&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                        ,
                        moveOut: true

                        , btn: ['保存', '关闭']
                        , yes: function (index, layero) {
                            var body = layer.getChildFrame('body', index);
                            var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                            $(body).find('#submitbtn').click();
                            editCallBack()
                        }
                        , btn2: function (index, layero) {
                            //按钮【按钮二】的回调
                            //return false 开启该代码可禁止点击该按钮关闭
                        }
                    });
                }, function () {
                });
            } else {
                var index = layer.open({
                    title: '添加按钮',
                    type: 2,
                    shade: 0.2,
                    maxmin: true,
                    shadeClose: false,
                    area: ['90%', '90%'],
                    content: '/ezadmin/form/form-listTableButtonHtml?&PLUGIN_CODE=button-table&OPEN_TYPE=MODEL&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                        + (itemName === undefined ? '' : itemName),
                    moveOut: true

                    , btn: ['保存', '关闭']
                    , yes: function (index, layero) {
                        var body = layer.getChildFrame('body', index);
                        var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                        $(body).find('#submitbtn').click();
                        editCallBack()
                    }
                    , btn2: function (index, layero) {
                        //按钮【按钮二】的回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }
        }
        ;
        if ($(e.target).hasClass('tablebuttonadd')) {
            var itemName = 'newItem';
            $(e.target).attr("item_url", "#");
            $(e.target).unbind("click");

            var index = layer.open({
                title: '添加按钮',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-listTableButtonHtml?&PLUGIN_CODE=button-table&OPEN_TYPE=MODEL&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                    + itemName,
                moveOut: true

                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                    $(body).find('#submitbtn').click();
                    editCallBack()
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
            });

        }
        ;
        if ($(e.target).hasClass('tablebuttondelete')) {
            var itemName = $(e.target).parents("button").attr("item_name") || $(e.target).parents("span").attr("item_name")
                || $(e.target).attr("item_name");
            if (confirm('确定要删除么')) { //只有当点击confirm框的确定时，该层才会关闭
                $.get($("#contextName").val() + "/ezadmin/form/doDelete-listTableButtonHtml?ENCRYPT_LIST_ID=" + encryptListId + "&ID="
                    + itemName, function (data) {
                    if (data.success) {
                        alert("删除成功");
                        location.reload();
                    }
                })
            }
        }

        if ($(e.target).hasClass('coledit')) {
            var itemName = $(e.target).parents("th").attr("item_name") || $(e.target).parents("th").attr("data-field") || $(e.target).attr("item_name");
            if (itemName == undefined || itemName == '') {
                layer.confirm('未找到配置名称，确定新增吗？', {icon: 3}, function () {
                    itemName = 'newItem'
                    var index = layer.open({
                        title: '添加列',
                        type: 2,
                        shade: 0.2,
                        maxmin: true,
                        shadeClose: false,
                        area: ['90%', '90%'],
                        content: '/ezadmin/form/form-listColumnHtml?HEAD_PLUGIN_CODE=th&BODY_PLUGIN_CODE=td-text&ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + (itemName === undefined ? '' : itemName),
                        moveOut: true

                        , btn: ['保存', '关闭']
                        , yes: function (index, layero) {
                            var body = layer.getChildFrame('body', index);
                            var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                            $(body).find('#submitbtn').click();
                            editCallBack()
                        }
                        , btn2: function (index, layero) {
                            //按钮【按钮二】的回调
                            //return false 开启该代码可禁止点击该按钮关闭
                        }
                    });
                }, function () {
                });
            } else {
                var index = layer.open({
                    title: '添加列',
                    type: 2,
                    shade: 0.2,
                    maxmin: true,
                    shadeClose: false,
                    area: ['90%', '90%'],
                    content: '/ezadmin/form/form-listColumnHtml?HEAD_PLUGIN_CODE=th&BODY_PLUGIN_CODE=td-text&ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + (itemName === undefined ? '' : itemName),
                    moveOut: true

                    , btn: ['保存', '关闭']
                    , yes: function (index, layero) {
                        var body = layer.getChildFrame('body', index);
                        var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                        $(body).find('#submitbtn').click();
                        editCallBack()
                    }
                    , btn2: function (index, layero) {
                        //按钮【按钮二】的回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }
        }
        if ($(e.target).hasClass('coladd')) {

            var itemName = 'newItem'
            var index = layer.open({
                title: '添加列',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-listColumnHtml?HEAD_PLUGIN_CODE=th&BODY_PLUGIN_CODE=td-text&ENCRYPT_LIST_ID=' + encryptListId + '&ID=' + (itemName === undefined ? '' : itemName),
                moveOut: true

                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                    $(body).find('#submitbtn').click();
                    editCallBack()
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
            });

        }
        if ($(e.target).hasClass('coldelete')) {
            var itemName = $(e.target).parents("th").attr("item_name") || $(e.target).parents("th").attr("data-field");
            if (confirm('确定要删除么')) { //只有当点击confirm框的确定时，该层才会关闭
                $.get($("#contextName").val() + "/ezadmin/form/doDelete-listColumnHtml?ENCRYPT_LIST_ID=" + encryptListId + "&ID="
                    + (itemName === undefined ? "undefined" : itemName), function (data) {
                    if (data.success) {
                        alert("删除成功");
                        location.reload();
                    } else {
                        alert('删除失败' + e.message)
                    }
                })
            }
        }


        if ($(e.target).hasClass('rowbuttonedit')) {
            var itemName = $(e.target).parents("button").attr("item_name") || '';
            var k = 'ID';

            $(e.target).attr("item_url", "#");
            $(e.target).unbind("click");
            if (itemName == undefined || itemName == '') {
                layer.confirm('未找到配置名称，确定新增吗？', {icon: 3}, function () {
                    itemName = 'newItem';
                    var index = layer.open({
                        title: '添加按钮',
                        type: 2,
                        shade: 0.2,
                        maxmin: true,
                        shadeClose: false,
                        area: ['90%', '90%'],
                        content: '/ezadmin/form/form-listRowButtonHtml?PLUGIN_CODE=button-single&OPEN_TYPE=MODEL&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                            + (itemName === undefined ? '' : itemName),
                        moveOut: true

                        , btn: ['保存', '关闭']
                        , yes: function (index, layero) {
                            var body = layer.getChildFrame('body', index);
                            var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                            $(body).find('#submitbtn').click();
                            editCallBack()
                        }
                        , btn2: function (index, layero) {
                            //按钮【按钮二】的回调
                            //return false 开启该代码可禁止点击该按钮关闭
                        }
                    });
                }, function () {
                });
            } else {
                var index = layer.open({
                    title: '添加按钮',
                    type: 2,
                    shade: 0.2,
                    maxmin: true,
                    shadeClose: false,
                    area: ['90%', '90%'],
                    content: '/ezadmin/form/form-listRowButtonHtml?PLUGIN_CODE=button-single&OPEN_TYPE=MODEL&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                        + (itemName === undefined ? '' : itemName),
                    moveOut: true

                    , btn: ['保存', '关闭']
                    , yes: function (index, layero) {
                        var body = layer.getChildFrame('body', index);
                        var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                        $(body).find('#submitbtn').click();
                        editCallBack()
                    }
                    , btn2: function (index, layero) {
                        //按钮【按钮二】的回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }
        }
        if ($(e.target).hasClass('rowbuttonadd')) {
            var itemName = 'newItem';
            var k = 'ID';


            var index = layer.open({
                title: '添加按钮',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: false,
                area: ['90%', '90%'],
                content: '/ezadmin/form/form-listRowButtonHtml?PLUGIN_CODE=button-single&OPEN_TYPE=MODEL&ENCRYPT_LIST_ID=' + encryptListId + '&ID='
                    + (itemName === undefined ? '' : itemName),
                moveOut: true

                , btn: ['保存', '关闭']
                , yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();

                    $(body).find('#submitbtn').click();
                    editCallBack()
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调
                    //return false 开启该代码可禁止点击该按钮关闭
                }
            });

        }
        if ($(e.target).hasClass('rowbuttondelete')) {
            var itemName = $(e.target).parents("button").attr("item_name");
            if (confirm('确定要删除么')) { //只有当点击confirm框的确定时，该层才会关闭
                $.get($("#contextName").val() + "ezadmin/form/doDelete-listRowButtonHtml?ENCRYPT_LIST_ID=" + encryptListId + "&ID="
                    + (itemName === undefined ? "undefined" : itemName), function (data) {
                    if (data.success) {
                        alert("删除成功");
                        location.reload();
                    }
                })
            }
        }
    });


    $(".saveButton").click(function () {
        var url = '/ezadmin/list/list-' + encryptListId + '?' + getSearchParams();
        openBlank(url);
    })

    function getIds(cl) {
        var itemids = '';
        $("." + cl).each(function () {
            if ($(this).attr('item_id') > 0) {
                itemids += "," + $(this).attr("item_id");
            }
        })
        return itemids;
    }

    function editCallBack() {
        layer.closeAll();
        location.reload();
    }

    if (table) {
        table.draw();
    }


    $(".ezopenbutton").attr("item_url", "#");
    $(".ezopenbutton").attr("url", "#");
    $(".ezopenbutton").unbind("click")

    $("body").on("click", ".clear", function () {
        $.ajax($("#contextName").val() + "/ezadmin/clear.html");
        $.ajax($("#contextName").val() + "/ezadmin/clear.html");
        alert("OK");
    })

});