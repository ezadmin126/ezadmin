/*! ezadmin v1.0.0-SNAPSHOT |
 https://gitee.com/ezadmin/index.html
 | MIT /license */
var table;
var laytable;
var json = cacheConfig();

$(document).ready(function () {
//获取form表单下所有name属性值
    function getobj() {
        var elms = $("#searchForm [name]"); //formid 包含name属性的所有元素
        var obj = {};
        $.each(elms, function (i, item) {
            var name = $(item).attr("name");
            var type = $(item).attr("type");
            if (name != 'LIST_ID' && type != 'hidden') {
                obj[name] = "";
            }

        });
        return obj;
    }

    initForm();

    layui.use(['laypage', 'layer', 'form'], function () {
        var laypage = layui.laypage
            , layer = layui.layer;
        if ($(".dataTables_empty").length == 0 && $("#PAGE_LAYUI").length > 0) {
            $.get($("#contextName").val() + "/ezadmin/list/count-" + $("#ENCRYPT_LIST_ID").val() + "?" + getSearchParams(), function (data) {


                if(data.data.page.currentPage>=data.data.page.totalPage){
                    $(".nextpage").removeClass("page-button");
                    $(".nextpage").addClass("layui-btn-disabled");
                }else{
                    $(".nextpage").addClass("page-button");
                    $(".nextpage").removeClass("layui-btn-disabled");
                }

                laypage.render({
                    elem: 'PAGE_LAYUI',
                    theme: '#1E9FFF'
                    , count: data.data.page.totalRecord
                    , curr: data.data.page.currentPage
                    , limit: data.data.page.perPageInt
                    , limits: [10, 20, 30, 40, 50, 100]
                    , layout: ['count', 'prev', 'page', 'next', 'limit', 'skip']
                    , jump: function (obj, first) {
                        if (!first) {
                            $("#perPageInt").val(obj.limit)
                            $("#currentPage").val(obj.curr)
                            $("#searchForm").submit();
                        }
                    }
                });
            })
        } else {
            laypage.render({
                elem: 'PAGE_LAYUI',
                theme: '#1E9FFF'
                , count: 0
                , curr: 1
                , limit: 10
                , limits: [10, 20, 30, 40, 50, 100]
                , layout: ['count', 'prev', 'page', 'next', 'limit', 'skip']
                , jump: function (obj, first) {
                    if (!first) {
                        $("#perPageInt").val(obj.limit)
                        $("#currentPage").val(obj.curr)
                        $("#searchForm").submit();
                    }
                }
            });
        }
    });

    $('body').on('click', function (e) {
        if (($(e.target).attr('id') != 'custom-cols-span')
            && !$(e.target).hasClass('custom-cols')) {
            $("#custom-cols-span").hide();
        }
    });


    $("#downBtn").click(function () {
        $(this).hide();
        $("#upBtn").show();
        $("#_SEARCH_ITEM_DISPLAY").val(1);
        calculateSearchItemDisplay();
    })
    $("#upBtn").click(function () {
        $(this).hide();
        $("#downBtn").show();
        $("#_SEARCH_ITEM_DISPLAY").val(0);
        calculateSearchItemDisplay();
    })

    var config = {
        onfocusout: function (element) {
            $(element).valid();
        },
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.parent().append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
        },
        submitHandler: function (form) {
           // console.log("start submit form")
            if (typeof submitHandle === "function") {
                if (submitHandle()) {
                    $("#submitBtn").addClass("layui-btn-disabled");
                    $("#submitBtn").unbind("click")
                    return true;
                } else {
                    return false;
                }
            }
            if ($("#submitBtn").hasClass("layui-btn-disabled")) {
                alert('请勿重复提交');
                return false;
            }
            $("#submitBtn").addClass("layui-btn-disabled");
            $("#submitBtn").unbind("click")
            return true;
        }
    };
    if ($("#validateRules").val() != null && $("#validateRules").val() != '') {
        config.rules = JSON.parse($("#validateRules").val());
    }
    if ($("#validateMessages").val() != null && $("#validateMessages").val() != '') {
        config.messages = JSON.parse($("#validateMessages").val());
    }
    $("#searchForm").validate(config);

    $("#submitBtn").click(function () {
        $("#currentPage").val(1);
        $("#searchForm").submit();
    })
    layui.use(['table', 'form', 'layCascader'], function () {
        var form = layui.form;
        var layCascader = layui.layCascader;
        $("#resetBtn").click(function () {

            $("#resetBtn").addClass("layui-btn-disabled");

            var elms = $("#searchForm [name]"); //formid 包含name属性的所有元素
            var obj = {};
            $.each(elms, function (i, item) {
                var type = $(item).attr("type");
                if (type != 'hidden') {
                    $(item).val("");
                }
            });

            // $(".J-lv-value").val('')
            // $(".J-text").text('请选择')
            if ($(".el-tag").length > 0) {
                $(".el-icon-arrow-down").click();
            }

            var xms = xmSelect.get();
            for (let i = 0; i < xms.length; i++) {
                xms[i].setValue([])
            }
            $("#currentPage").val(1)

            form.val("searchForm", getobj());

            $("#searchForm").submit();
        })
    })

    //
    $(document).on("click",".page-button:not(layui-btn-disabled)",function(){
        $(this).addClass(".layui-btn-disabled");
        $("#currentPage").val($(this).attr("page"));
        $("#searchForm").submit();
    })

    // $('.page-button').not(".disabled").click(function () {
    //     $("#currentPage").val($(this).attr("page"));
    //     $("#searchForm").submit();
    // })

    $(".jumpPage").keyup(function (event) {
        if (event.keyCode == 13) {
            $("#currentPage").val($("#jumpPage").val())
            $("#searchForm").submit();
        }
    });

    $(".perPageIntSelect").change(function (event) {
        $("#currentPage").val(1)
        $("#perPageInt").val($(this).val())
        $("#submitBtn").click();
    });

    //回车搜索
    $("#searchForm").find('input').each(function () {
        $(this).keyup(function (event) {
            if (event.keyCode == 13) {
                $("#submitBtn").click();
            }
        });
    })


    $(document).on('click', '.order-icon', function () {
        //排序
        var name = $(this).attr("ITEM_NAME");
        var value = $(this).attr("ITEM_VALUE");
        if (value == '0') {
            value = 1;
        } else if (value == 1) {
            value = '';
        } else {
            value = 0;
        }
        $("#orderBy").attr("name", name);

        $("#orderBy").val(value);
        $("#searchForm").submit();
    })
    $("#export").click(function () {
        var url="/ezadmin/list/export-"+$("#ENCRYPT_LIST_ID").val()+'?'+getSearchParams();
       openBlank(url)
    })




    $(".ITEM_CHECK_BOX").each(function () {
        $(this).change(function (e) {
            e.stopPropagation();
            e.preventDefault();
            //
            var checked = $(this).is(':checked');
            var itemId = $(this).attr("item_check_id");
            if (checked) {
                $('[list-item_id="' + itemId + '"]').show();
            } else {
                $('[list-item_id="' + itemId + '"]').hide();
            }
        })
    })

    $(document).on('click', '.list-head-checkbox', function () {
        if (this.checked) {
            $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', true);
        } else {
            $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', false);
        }
    })

    renderTable();


    $('.viewer-image').each(function () {
        $(this).on('load', function () {
            // 在图片加载完成后执行的操作
            if (table) {
                table.draw();
            }
            if (laytable) {
                laytable.resize();
            }
        });
    })


    $("#customColAndSearch").click(function () {
        var index = layer.open({
            title: "设置",
            area: ['90%', '80%'],
            type: 2,
            shade: 0.1,
            shadeClose: true,
            anim: 0,
            content: $("#contextName").val() + "/ezadmin/list/selectCols-" + $("#ENCRYPT_LIST_ID").val()+"?IS_DEBUG="+$("#IS_DEBUG").val(),
            moveOut: true,
            btnAlign: 'c',
            closeBtn: 1,
            btn: ['保存', '设为默认', '取消'],
            yes: function (index, layero) {
                var body = layer.getChildFrame('body', index);
                body.find("#save").click();
                layer.closeAll()
                location.reload();
                return true;
            },
            btn2: function (index, layero) {
                if (confirm('确定要初始化排序？')) { //只有当点击confirm框的确定时，该层才会关闭
                    //
                    var key = 'EZ_CONFIG_' + $("#ENCRYPT_LIST_ID").val();
                    localStorage.removeItem(key);
                    layer.close(index)
                    location.reload();
                }
                return true;
            }, function(index) {
                layer.close(index)
            }
        });
    })

    //如下功能 暂时都不需要 1.表头搜索  2 单元格编辑
    function renderTableSearchItem(layero, itemName) {
        var theadItemInput = $(layero).find("[name=" + itemName + "]");
        layui.use('form', function () {
            var form = layui.form;
            form.render(theadItemInput);

        });
        if ($(theadItemInput).hasClass("ez-xmselect-table")) {
            renderXmselect($(theadItemInput));
        }
        if ($(theadItemInput).hasClass("ez-laycascader-table")) {
            renderCascader($(theadItemInput));
        }
        if ($(theadItemInput).length == 0 &&
            $(layero).find('.ez-daterange-parent').length > 0) {
            renderDateParent($(layero).find('.ez-daterange-parent'));
        }
    }


    $("body").on("dblclick", ".ez-view-group-parent", function (e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).find(".ez-view-group").each(function () {
            if ($(this).is(":hidden")) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
        if (table) {
            table.draw();
        }
    })

    $("body").on("click", ".saverow", function (e) {
        e.stopPropagation();
        e.preventDefault();
        let text = $(this).parents("td").find(".td-text");
        let edit = $(this).parents("td").find(".td-edit");
        var _name = $(this).parents("td").attr("item_name");

        var editFormItem = $(edit).find("[name=" + _name + "]");

        let url = $(this).attr("editExpress");
        let data = {};
        data.ID = $(this).parents("tr").find("input[name='row_data_hidden_ID']").val();
        data[_name] = editFormItem.val();
        layer.confirm('确认保存?', {icon: 3, title: '提示'}, function (index) {
            $.post(url, data, function (re) {
                var to_show_value = editFormItem.val();
                if (editFormItem.is("select")) {
                    to_show_value = editFormItem.find("option:selected").text();
                }
                if (re.success) {
                    if (text.is(":hidden")) {
                        text.html(to_show_value);
                        text.show();
                    } else {
                        text.hide();
                    }
                    if (edit.is(":hidden")) {
                        edit.show();
                    } else {
                        edit.hide();
                    }
                } else {
                    layer.alert("error");
                }
            })
            layer.close(index);
        });

    })
});


//计算搜索栏是否隐藏
function calculateSearchItemDisplay() {

    let search = json.search == undefined ? [] : json.search;
    //如果配置为展示全部，则优先级最高。
    var count = 0;

    //搜索项的排序
    if (search.length > 0) {
        for (var i = search.length; i > 0; i--) {
            var item = $(".searchcontent").find('[ez-name="' + search[i - 1] + '"]').detach();
            if (!item.hasClass("list-item-hidden")) {
                item.show();
                $(".searchcontent").prepend(item);
            } else {
                $(".searchcontent").append(item);
            }
        }
    }

    if ($("#_SEARCH_ITEM_DISPLAY").val() == 1) {
        $(".searchcontent > .list-item").not(".list-item-hidden").each(function () {
            if (search.length > 0) {
                if (search.includes($(this).attr("ez-name"))) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            } else {
                $(this).css("display","block");
            }
        })
    } else {
        $(".searchcontent > .list-item").not(".list-item-hidden").each(function () {
            if (search.length > 0) {
                if (count < 8 && search.includes($(this).attr("ez-name"))) {
                    count++;
                    $(this).show();
                } else {
                    $(this).hide();
                }
            } else {
                if (count < 8) {
                    count++;
                    $(this).show();
                } else {
                    $(this).hide();
                }
            }
        })
    }
}

function cacheConfig() {
    var key = 'EZ_CONFIG_' + $("#ENCRYPT_LIST_ID").val();
    var jsonconfig = localStorage.getItem(key);
    if (jsonconfig != undefined) {
        var json = JSON.parse(jsonconfig);
        return json;
    }
    return {};
}

function selfConfig() {
    //根据本地缓存，去除部分字段
    try {
        var key = 'EZ_CONFIG_' + $("#ENCRYPT_LIST_ID").val();
        var json = cacheConfig();

        var search = json.search == undefined ? [] : json.search;
        var column = json.column == undefined ? [] : json.column;
        var fixNumber = json.fixNumber;
        var fixNumberRight = json.fixNumberRight;
        if (parseInt(fixNumber).toString() != 'NaN') {
            $("#fixNumber").val(fixNumber)
        } else {
            // $("#fixNumber").val(0)
        }
        if (parseInt(fixNumberRight).toString() != 'NaN') {
            $("#fixNumberRight").val(fixNumberRight)
        } else {
            //  $("#fixNumberRight").val(0)
        }


        //最后原框架功能，超过7个的选项自动隐藏
        calculateSearchItemDisplay();


        if (column.length > 0) {
            for (var i = 0; i < column.length; i++) {
                $("#mytable thead tr").each(function () {
                    $(this).find('th[ITEM_NAME="' + column[i] + '"]').detach().appendTo($(this));
                })
                $("#mytable tbody tr").each(function () {
                    $(this).find('td[ITEM_NAME="' + column[i] + '"]').detach().appendTo($(this));
                })
                $("#mytable tfoot tr").each(function () {
                    $(this).find('th[ITEM_NAME="' + column[i] + '"]').detach().appendTo($(this));
                })
            }
            $("#mytable thead tr").each(function () {
                $(this).find('.rowButtons').detach().appendTo($(this));
            })

            $("#mytable tbody tr").each(function () {
                $(this).find('.rowButtons').detach().appendTo($(this));
            })

            $("#mytable th").not(".fixedCol").each(function () {
                if (column.includes($(this).attr("item_name"))) {
                    $(this).show();
                } else {
                    $(this).remove();
                    // var laydata=$(this).attr("lay-data");
                    // var json=JSON.parse(laydata);
                    // json.hide=true;
                    // $(this).attr("lay-data",JSON.stringify(json))
                }
            })
            $("#mytable td").not(".fixedCol").each(function () {
                if (column.includes($(this).attr("item_name"))) {
                    $(this).show();
                } else {
                    $(this).remove();
                }
            })
        }


    } catch (E) {
        console.log(E);
        //$("#mytable tfoot").remove();
    }

   // $('.table-treegrid').treegrid();

}


function renderTable() {
    console.log("table render");
    if (table) {
        table.destroy();
    }
    try {
        if (typeof (selfConfig) == "function") {
            selfConfig();
        }
    } catch (e) {
        console.log('selfConfig error', e)
    }
    try {
        if (typeof (userConfig) == "function") {
            userConfig();
        }

    } catch (e) {
        console.log('userConfig error', e)
    }

    layui.use(function () {
        var table2 = layui.table;
        var treeTable = layui.treeTable;
        var form=layui.form;
        //实现checkbox 半选效果
        form.on('checkbox(list-head-checkbox)', function(data){
            var elem = data.elem; // 获得 checkbox 原始 DOM 对象
            var checked = elem.checked; // 获得 checkbox 选中状态
            if (checked) {
                $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', true);
            } else {
                $("input[name='list-body-checkbox']:not(:disabled)").prop('checked', false);
            }
            form.render();
        });
        form.on('checkbox(list-body-checkbox)', function(data){
            var cl=$(".layui-table-box").find("[name=list-body-checkbox]:checked").length;
            var al=$(".layui-table-box").find("[name=list-body-checkbox]").length;
            if(cl<al&&cl>0){
                $('.list-head-checkbox').prop('indeterminate', true);
            }else{
                $('.list-head-checkbox').prop('indeterminate', false);
            }
            if(al==cl){
                $(".list-head-checkbox").prop('checked', true);
            }else{
                $(".list-head-checkbox").prop('checked', false);

            }
            form.render();
        });

        //转换静态表格
        laytable = table2.init('mytable', {
            // height: $(document).height()-$(".searchWrap").height()-150 , //设置高度
            escape: false,
            autoSort: false,
            cellMinWidth:110,
            className: $("#mytable").attr("class")
            , limit: $("#perPageInt").val() //注意：请务必确保 limit 参数（默认：10）是与你服务端限定的数据条数一致
            ,initSort: {
                field: $("#orderBy").attr("name"), // 按 id 字段排序
                type:  $("#orderBy").attr("value") // 降序排序
            }
            //支持所有基础参数
            , done: function (res, curr, count) {
                try {
                    if (typeof (afterAllDataLoad) == "function") {
                        afterAllDataLoad();
                    }
                    $('.layuimini-loader').fadeOut();
                    $("[name=DISPLAY_ORDER_INPUT]").each(function () {
                        var oldValue = $(this).val()
                        $(this).blur(function () {
                            var id = $(this).attr("data-id");
                            var order = $(this).val();
                            if (!isPositiveInteger(order)) {
                                layui.layer.alert("请输入正整数")
                                return;
                            }
                            if (order != undefined && order != '' && oldValue != order) {
                                $.get("/ezadmin/list/doOrder-" + $("#ENCRYPT_LIST_ID").val() + "?orderId=" + id + "&displayOrder=" + order, function (data) {
                                    if (data.success) {
                                        location.reload();
                                    } else {
                                        layui.layer.alert('操作失败')
                                    }
                                })
                            }
                        })
                    })

                    $("[item_name='删除']").addClass("layui-border-red");
                    $("[item_name='修改']").addClass("layui-border-blue");
                    $("[item_name='编辑']").addClass("layui-border-blue");
                    watermark({"watermark_txt": $("#EZ_SESSION_USER_NAME_KEY").val() + getNow()});
                } catch (e) {
                    console.log(e)
                }

            }
        });
        laytable.on('sort(mytable)', function(obj){
            console.log(obj.field); // 当前排序的字段名
            console.log(obj.type); // 当前排序类型：desc（降序）、asc（升序）、null（空对象，默认排序）
            console.log(this); // 当前排序的 th 对象
            $("#orderBy").attr("name", obj.field+"_ORDER");

            $("#orderBy").val(obj.type);
            $("#submitBtn").click();
            // 尽管我们的 table 自带排序功能，但并没有请求服务端。
            // 有些时候，你可能需要根据当前排序的字段，重新向后端发送请求，从而实现服务端排序，如：

        });
        if($("#coldata").val()!=undefined){
            var json=JSON.parse($("#coldata").val());
            var col=[];
            col.push(json);

            var inst = treeTable.render({
                elem: '#treetable',
                url: '/ezadmin/list/treedata-'+$("#ENCRYPT_LIST_ID").val()+'?'+getSearchParams() , // 此处为静态模拟数据，实际使用时需换成真实接口
                //maxHeight: '501px',
                cols: col,
                defaultToolbar:'',
                done: function (res, curr, count) {
                    watermark({"watermark_txt": $("#EZ_SESSION_USER_NAME_KEY").val()+ getNow()});
                }
            });
        }
    });
}

function refreshOrder(item_id) {
    $.get("/ezadmin/list/doOrder-" + $("#ENCRYPT_LIST_ID").val() + "?orderId=" + item_id + "&displayOrder=0", function (data) {
        if (data.success) {
            location.reload();
        } else {
            layui.layer.alert('重新计算排序失败')
        }
    })
}










