/*! ezadmin v1.0.0-SNAPSHOT |
 https://gitee.com/ezadmin/index.html
 | MIT /license */
var holiday = [[], []];
$(function (){

    if ($("#holiday") != null && $("#holiday").val() != null) {
        try{
            holiday = JSON.parse($("#holiday").val());
        }catch (e) {
            console.log(e)
        }
    }

    $(document).on('click', '.ezopenbutton', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var btn = $(this);
        var url = (btn.attr("item_url") || btn.attr("url"));
        if (url == '#') {
            return;
        }
        var openType = btn.attr("ITEM_OPEN_TYPE") || btn.attr("opentype");
        var area = btn.attr("area");
        var title = btn.attr("ITEM_OPEN_TITLE") || btn.attr("windowname") || "打开";
        ezopen(openType, title, url, area);
        return false;
    })
    $(document).on("click",".ezopenredirect",function(e){
        openBlank("/ezredirect.html?url="+encodeURIComponent($(this).attr("item_url")));
        e.preventDefault();
        e.stopPropagation();
        return false;
    })


    $(document).on('click', '.viewer-image', function (e) {

        var _this = $(this);
        var pid = _this.attr("pid");
        ;
        var data = [];
        var item = {};
        item.alt = _this.attr("alt");
        item.pid = _this.attr("pid");
        item.src = _this.attr("orgsrc")||_this.attr("src");
        item.thumb = _this.attr("orgsrc");
        data.push(item);
        layer.photos({
            shade: 0.5,
            photos: {
                "title": _this.attr("title"),
                "start": 0,
                "data": data
            },
            footer: false // 是否隐藏底部栏 --- 2.8+
            ,success:function(){
                if($(".layui-layer-photos .layer-layer-photos-main img").eq(0).height()>500){
                    $(".layui-layer-photos .layer-layer-photos-main img").eq(0).css("height","500px");
                    $(".layui-layer-photos .layer-layer-photos-main img").eq(0).css("width","auto");

                    $(".layui-layer-photos").eq(0).css("top",$(window).height()/2-250)


                }
            }
        });
        e.preventDefault();
        e.stopPropagation();
        return false;
    })
    $(document).on('click', '.parent-viewer-image', function (e) {

        var _this = $(this);
        var pid = _this.attr("pid");
        ;
        var data = [];
        var item = {};
        item.alt = _this.attr("alt");
        item.pid = _this.attr("pid");
        item.src = _this.attr("orgsrc")||_this.attr("src");
        item.thumb = _this.attr("orgsrc");
        data.push(item);
        parent.window.layer.photos({
            shade: 0.5,
            photos: {
                "title": _this.attr("title"),
                "start": 0,
                "data": data
            },
            footer: false // 是否隐藏底部栏 --- 2.8+
            ,success:function(){
                if($(".layui-layer-photos .layer-layer-photos-main img").eq(0).height()>500){
                    $(".layui-layer-photos .layer-layer-photos-main img").eq(0).css("height","500px");
                    $(".layui-layer-photos .layer-layer-photos-main img").eq(0).css("width","auto");

                    $(".layui-layer-photos").eq(0).css("top",$(window).height()/2-250)


                }
            }
        });
        e.preventDefault();
        e.stopPropagation();
        return false;
    })
//行选择事件
    $(document).on('click', '.ezrowselectbutton', function (e) {

        var name = $(this).attr("fname");
        var ezcallback = $(this).attr("ez_callback") || $(this).attr("item_url") || $(this).attr("url");
        // var doc=$(itemUrl,window.parent.document);
        var row = $(this).parents("tr");
        var ids = getJsonRowIds(row);
        var lines = getJsonRowIdAndNames(row, name);
        window.parent[ezcallback] && window.parent[ezcallback](ids, lines);
        e.preventDefault();
        e.stopPropagation();
        return false;
    })
//弹框选择
    $(document).on('click', '.ezcheckbutton', function (e) {
        console.log("checked")
        debugger;
        var ezcallback = $(this).attr("ez_callback") || $(this).attr("item_url") || $(this).attr("url");
        // var doc=$(ezcallback,window.parent.document);
        var ids = getJsonCheckIds();
        var lines = getJsonCheckIdAndNames();
        window.parent[ezcallback] && window.parent[ezcallback](ids, lines);
        e.preventDefault();
        e.stopPropagation();
        return false;
    })
})

var shortcut = [
    {
        text: "昨天",
        value: function () {
            var now = new Date();
            now.setDate(now.getDate() - 1);
            return now;
        }()
    },
    {
        text: "今天", value: function () {
            const now = new Date();
            return now;
        }()
    },
    {
        text: "本月月初",
        value: function () {
            // 获取当前日期
            const today = new Date();
            const year = today.getFullYear();
            const month = today.getMonth();
            // 创建一个新的 Date 对象，设置为本月1号
            const firstDayOfMonth = new Date(year, month, 1);
            return firstDayOfMonth;
        }()
    },
    {
        text: "上月月初",
        value: function () {
            // 获取当前日期
            const today = new Date();
// 获取上个月的年份和月份
            const year = today.getFullYear();
            const month = today.getMonth() - 1;
// 创建一个新的 Date 对象，设置为上个月1号
            const firstDayOfLastMonth = new Date(year, month, 1);
// 获取上个月的最后一天
            const lastDayOfLastMonth = new Date(year, month + 1, 0);
            return [firstDayOfLastMonth];
        }()
    },
    {
        text: "上月月末",
        value: function () {
            // 获取当前日期
            const today = new Date();
// 获取上个月的年份和月份
            const year = today.getFullYear();
            const month = today.getMonth() - 1;
// 创建一个新的 Date 对象，设置为上个月1号
            const firstDayOfLastMonth = new Date(year, month, 1);
// 获取上个月的最后一天
            const lastDayOfLastMonth = new Date(year, month + 1, 0);
            return [lastDayOfLastMonth];
        }()
    }, {
        text: "前30天",
        value: function () {
            const today = new Date();
            const thirtyDaysAgo = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
            return thirtyDaysAgo;
        }()
    },
    {
        text: "今年年初",
        value: function () {
            const now = new Date();
            const year = now.getFullYear();
            const januaryFirst = new Date(year, 0, 1);
            return januaryFirst;
        }()
    },
    {
        text: "去年年初",
        value: function () {
            const now = new Date();
            const year = now.getFullYear();
            const januaryFirst = new Date(year - 1, 0, 1);
            return januaryFirst;
        }()
    },
    {
        text: "去年年末",
        value: function () {
            const now = new Date();
            const year = now.getFullYear();
            const januaryFirst = new Date(year - 1, 11, 31);
            return januaryFirst;
        }()
    }
];

var rangeShortCut=[{
    text: "昨天",
    value: function(){
        var today = new Date();
        const preday = new Date(today.getTime() - (  24 * 60 * 60 * 1000));
        return [
            preday,
            preday
        ];
    }
} ,
    {
        text: "上周",
        value: function(){
            // 创建一个新的 Date 对象
            var today = new Date();

// 设置为本周第一天（星期日）
            today.setDate(today.getDate() - (today.getDay() + 6) % 7); // 将今天的日期调整为本周第一天

// 设置为上周最后一天（星期六）
            var lastWeekEnd = new Date(today.getTime());
            lastWeekEnd.setDate(today.getDate() - 1);
            const lastWeekStart = new Date(lastWeekEnd.getTime() - (6 * 24 * 60 * 60 * 1000));

            return [
                lastWeekStart,
                lastWeekEnd
            ];
        }
    },
    {
        text: "本周",
        value: function(){
            // 创建一个新的 Date 对象
            var today = new Date();
            // 设置为本周第一天（星期日）
            today.setDate(today.getDate() - (today.getDay() + 6) % 7); // 将今天的日期调整为本周第一天
            return [
                today,
                new Date()
            ];
        }
    },
    {
        text: "最近7天",
        value: function(){
            var today = new Date();
            const thirtyDaysAgo = new Date(today.getTime() - (7 * 24 * 60 * 60 * 1000));
            return [
                thirtyDaysAgo,
                new Date()
            ];
        }
    },
    {
        text: "最近30天",
        value: function(){
            var today = new Date();
            const thirtyDaysAgo = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
            return [
                thirtyDaysAgo,
                new Date()
            ];
        }
    },
    {
        text: "上个月",
        value: function(){
            var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth();
            return [
                new Date(year, month - 1, 1),
                new Date(year, month, 0, 23, 59, 59)
            ];
        }
    },
    {
        text: "这个月",
        value: function(){
            var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth();
            return [
                new Date(year, month, 1),
                new Date(year, month + 1, 0, 23, 59, 59)
            ];
        }
    }
    ,
    {
        text: "去年",
        value: function(){
            const now = new Date();
            const year = now.getFullYear();
            const januaryFirst = new Date(year - 1, 0, 1);
            const last = new Date(year - 1, 11, 31);
            return [
                januaryFirst,
                last
            ];
        }
    },
    {
        text: "今年",
        value: function(){
            const now = new Date();
            const year = now.getFullYear();
            const januaryFirst = new Date(year, 0, 1);
            return [
                januaryFirst,
                now
            ];
        }
    }
]
function initForm() {
    layui.use(['form', 'laydate', 'table', 'dropdown', 'colorpicker'], function () {
        var
            form = layui.form,
            table = layui.table;
        form.render();
        var form = layui.form;
        // 自定义表单验证提示信息
        form.verify({
            required: function (value, item) { // value 为当前输入框的值，item 为当前输入框的 DOM 对象
                if (!value) {
                    return '请输入' + (item.getAttribute("label")||'');
                }
            }
        });
        var colorpicker = layui.colorpicker;
        colorpicker.render({
            elem: '.colorpick',
            predefine: true,
            alpha: true,
            done: function (color) {
                console.log(this.elem, color);
                $(this.elem).removeClass("layui-inline");
                $(this.elem).parent().find("input").val(color);
            }, change: function (color) { //颜色改变的回调
                if (colorChange) {
                    colorChange(color);
                }
            }
        });
        $(".colorpick").removeClass("layui-inline");


        $(".J-reset").click(function () {
            form.val("searchForm", getobj());
        })

        var laydate = layui.laydate;
        $(".ez-daterange-parent").each(function () {
            var _this = $(this);
            renderDateParent(_this);
        })
        $(".layui-date-input").each(function () {
            var _this = $(this);
            laydate.render({
                elem: _this
            });
        })


        $(".layui-date-narmal").each(function () {
            var _this = $(this);
            laydate.render({
                elem: _this,
                type: 'date',
                shortcuts: shortcut,
                holidays: holiday
            });
        })
        $(".layui-datetime-narmal").each(function () {
            var _this = $(this);
            laydate.render({
                elem: _this,
                type: 'datetime',
                shortcuts: shortcut,
                holidays: holiday
            });
        })

        //联动日期区间

        $(".daterangeinput").each(function () {
            var _this = $(this);
            laydate.render({
                elem: _this,
                type: 'date',
                range: true,
                holidays: holiday,
                weekStart: 1,
                shortcuts:rangeShortCut
            });
        })
        $(".datetimerangeinput").each(function () {
            var _this = $(this);
            laydate.render({
                elem: _this,
                type: 'datetime',
                holidays: holiday,
                weekStart: 1,
                range: true,
            });
        })
    })

    $(document).on("click",".ez-help",function(e){
        var url = $(this).attr("src");
        var tips = $(this).attr("tips");
        var id = $(this).attr("id");
        if (url) {
            layer.open({
                type: 2,
                title: '说明',
                shadeClose: true,
                shade: 0.3,
                maxmin: true, //开启最大化最小化按钮
                area: ['90%', '90%'],
                content: url
            });
        }
        if (tips) {
            layer.tips(tips, '#' + id, {
                tips: [1, '#3595CC'],
                time: 4000
            });
        }
        e.preventDefault();
        e.stopPropagation();
        return false;
    })


    $(".ez-xmselect").each(function () {
        var xmel = $(this);
        renderXmselect(xmel);
    })
    $(".ez-laycascader").each(function () {
        var _this = $(this);
        renderCascader(_this);
    })
}

function istrue(c) {
    return (c || 'true') == 'true' || (c || '1') == '1';
}

function renderCascader(cas) {
    layui.use('layCascader', function () {
        try {
            var layCascader = layui.layCascader;
            var _this = $(cas);
            var url = _this.attr("url");
            var value = _this.attr("ez_value") || 'VALUE';
            var label = _this.attr("ez_treelabel")|| _this.attr("ez_label") || 'LABEL';
            var children = _this.attr("ez_children") || 'CHILDREN';
            var multiple = istrue(_this.attr("multi"));
            var itemsJson = _this.attr("itemsJson");
            var disable_flag = _this.attr("disable_flag");

            var itemPlaceholder = _this.attr("placeholder") || '请选择';
            var paramValue = _this.val() || '[]';
            var collapseTags = istrue(_this.attr("collapsetags"));
            var showAllLevels = istrue(_this.attr("showalllevels"));
            var span = ('true' == _this.attr("span"));

            if (url) {

                $.post(url, {}, function(response) {
                    if(response.success){
                        var res = response.data;
                        var prop = {};
                        prop.value = value;
                        prop.label = label;
                        prop.children = children;
                        prop.multiple = multiple;

                        var cc = layCascader({
                            elem: _this[0],
                            props: prop,
                            filterable: true,
                            clearable: true,
                            placeholder: itemPlaceholder,
                            collapseTags: collapseTags,
                            showAllLevels: showAllLevels,
                            disabled : disable_flag=="true",
                            value: paramValue,
                            options: res,
                            filterMethod: function (node, val) {//重写搜索方法。
                                if (val == node.data[label]) {//把value相同的搜索出来
                                    return true;
                                }
                                if ((node.data[label] + node.data[label]).indexOf(val) != -1) {//名称中包含的搜索出来
                                    return true;
                                }
                                //  console.log(node.data.orgName+node.data.orgNames+'##'+(node.data.orgId+'').indexOf(val));
                                return !ezpingyin(val, (node.data[label] + node.data[label]), (node.data[value] + ''));
                            },
                        });
                        if (span) {
                            //回显 laycascader
                            var c = $("#" + _this.attr("id")).parent();
                            var text = ''
                            if (multiple) {
                                text = c.find(".el-cascader").text();

                            } else {
                                text = c.find(".el-input__inner").attr("label")

                            }
                            c.html(
                                "<span class='layui-text form-block-span'>" + text + "</span>")
                        }
                    }else{

                    }

                }, 'json').fail(function () {
                    console.log("error");
                });

            } else if (itemsJson) {
//放到插件里面处理
            }
        } catch (e) {

        }

    })

}

var ezpingyin=function(value,text,id){
    var { pinyin } = pinyinPro;
    console.log(value+"\t"+text+"\t"+id)
    var result;
    if (escape(value).indexOf("%u") != -1) { //汉字
        result = text.indexOf(value) > -1;
    } else {
        
        value=value.toLowerCase();
        try{
        //var firstLetter=pinyinUtil.getFirstLetter(text, false).toLowerCase() ;
        var firstLetter=pinyin(text, { pattern: 'first', toneType: 'none', type: 'array'}).join('');
        //var pingyin=pinyinUtil.getPinyin(text,'',false,false).toLowerCase()
        var pingyinq=pinyin(text, { toneType: 'none', type: 'array' }).join('');

            console.log(value+"\t"+text+"\t"+id+"\t"+firstLetter+"\t"+pingyinq)
        // console.log(firstLetter+"\t"+pingyin+"\t"+value+"\t"+text);
        result = firstLetter.indexOf(value) > -1
            || pingyinq.indexOf(value) > -1
            || text.toLowerCase().indexOf(value) > -1
            || (id === undefined ? false : id.indexOf(value) > -1);
        }catch (e) {
            console.log(e);
        }
    }
    if (result == true) {
        return false;
    } else {
        return true;
    }
}
window.ezpingyin = ezpingyin;

function renderXmselect(xm) {
    try {
        var xmel = $(xm);
        var initdata = xmel.attr("itemsJson");
        var initvalue = xmel.attr("value");
        var itemName = xmel.attr("name");
        var itemPlaceholder = xmel.attr("itemPlaceholder");
        xmSelect.render({
            el: xmel[0],
            language: 'zn',
            filterable: true,
            toolbar: {
                show: true,
                list: ['ALL', 'REVERSE', 'CLEAR']
            },
            filterMethod: function (val, item, index, prop) {//重写搜索方法。
                if (val == item.K) {//把value相同的搜索出来
                    return true;
                }
                if (item.V.indexOf(val) != -1) {//名称中包含的搜索出来
                    return true;
                }
                return !ezpingyin(val, item.V, item.K);
            },
            style: {
                height: '26px',
            },theme: {
                color: '#1e9fff',
            },
            prop: {
                name: 'V',
                value: 'K',
            },
            name: itemName,
            tips: itemPlaceholder,
            data: JSON.parse(initdata),
            initValue: JSON.parse(initvalue)
            ,model: {
                label: {
                    type: 'block',
                    block: {
                        //最大显示数量, 0:不限制
                        showCount: xmel.attr("showCount")||5,
                        //是否显示删除图标
                        showIcon: true,
                    }
                }
            }
        })
    } catch (e) {

    }
}


function renderDateParent(_this) {
    var date = _this.find(".ez-daterange>input");
    if (date.length == 2) {
        var start = date[0];
        var end = date[1];
        renderDate(start, end, "date");
    }
    var datetime = _this.find(".ez-datetimerange>input");
    if (datetime.length == 2) {
        var start = datetime[0];
        var end = datetime[1];
        renderDate(start, end, "datetime");
    }
}

function renderDate(dateStart, dateEnd, type) {
    try {
        if (dateStart == undefined || dateStart.length < 1
            || dateEnd == undefined || dateEnd.length < 1) {
            return;
        }
        if (type == undefined) {
            type = 'date'
        }

        layui.use(['laydate'], function () {
            var laydate = layui.laydate;
            var starDate = laydate.render({
                elem: dateStart, type: type,
                shortcuts: shortcut,
                holidays: holiday,
                done: function (value, data) {
                    if (value != "") {
                        endDate.config.min = {
                            year: data.year,
                            month: data.month - 1,
                            date: data.date,
                            hours: type == 'date' ? 0 : data.hours,
                            minutes: type == 'date' ? 0 : data.minutes,
                            seconds: type == 'date' ? 0 : data.seconds,
                        }
                    }
                }
            });
            //常规用法
            var endDate = laydate.render({
                elem: dateEnd, type: type,
                shortcuts: shortcut,
                holidays: holiday,
                done: function (value, data) {
                    if (value != "") {
                        starDate.config.max = {
                            year: data.year,
                            month: data.month - 1,
                            date: data.date,
                            hours: type == 'date' ? 0 : data.hours,
                            minutes: type == 'date' ? 0 : data.minutes,
                            seconds: type == 'date' ? 0 : data.seconds,
                        }
                    }
                }
            });

            if (dateStart.value != '') {
                var date = new Date(dateStart.value);
                var year = date.getFullYear();
                var month = date.getMonth();
                var day = date.getDate();
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();
                endDate.config.min = {
                    year: year,
                    month: month,
                    date: day,
                    hours: type == 'date' ? 0 : data.hours,
                    minutes: type == 'date' ? 0 : data.minutes,
                    seconds: type == 'date' ? 0 : data.seconds,
                }
            }
            if (dateEnd.value != '') {
                var date = new Date(dateEnd.value);
                var year = date.getFullYear();
                var month = date.getMonth();
                var day = date.getDate();
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();
                starDate.config.max = {
                    year: year,
                    month: month,
                    date: day,
                    hours: type == 'date' ? 0 : data.hours,
                    minutes: type == 'date' ? 0 : data.minutes,
                    seconds: type == 'date' ? 0 : data.seconds,
                }
            }

        })
    } catch (e) {
    }
}


function val(targetCentent, name) {
    try {
        var label = targetCentent.find("[name=" + name + "]").val()
        return label;
    } catch (e) {
        console.log(e)
        return "";
    }
}

function setVal(targetCentent, name, value) {
    try {
        targetCentent.find("[name=" + name + "]").val(value)
    } catch (e) {
        console.log(e)
    }
}

//获取表格中所有ID
function getAllIds() {
    var ids = "";
    $("input[name='row_data_id']").each(function () {
        ids = ids + $(this).val() + ","
    })
    console.log("获取到业务ID: " + ids + '0');
    return ids + '0';
}

//获取表格中所有选中ID  name为list-body-checkbox
function getCheckIdsUrl() {
    var goodsIdArr = "-1";
    $("input[name='list-body-checkbox']:not(:disabled)").each(function () {
        if (this.checked) {
            goodsIdArr += ',' + $(this).attr("_CHECK_ID_VALUE");
        }
    })
    if (goodsIdArr == "-1") {
        return "";
    }
    return "_CHECKD_IDS=" + encodeURI(goodsIdArr);
}

function getJsonCheckIds() {

    var lines = [];
    $("input[name='list-body-checkbox']:not(:disabled)").each(function () {
        if (this.checked) {
            lines.push($(this).attr("_CHECK_ID_VALUE"));
        }
    })
    const uniqueArray = [...new Set(lines)];
    return JSON.stringify(uniqueArray);
}
function getCheckedIds() {
    var lines = [];
    $("input[name='list-body-checkbox']:not(:disabled):checked").each(function () {
            lines.push($(this).attr("_CHECK_ID_VALUE"));
    })
    return  [...new Set(lines)];
}

function getJsonCheckIdAndNames() {

    var lines = [];
    $("input[name='list-body-checkbox']:not(:disabled)").each(function () {
        if (this.checked) {
            var line = {};

            line.ID = $(this).attr("_CHECK_ID_VALUE");

            var inputs = $(this).parent().find("[type=hidden]");

            for (let i = 0; i < inputs.length; i++) {
                var cname = $(inputs[i]).attr("item_name");
                var value = $(inputs[i]).val();
                line[cname] = value;
            }

            lines.push(line);
        }
    })
    return JSON.stringify(lines);
}

function getJsonRowIds(row) {

    var lines = [];

    lines.push(row.find("[name='row_data_hidden_ID']").val());
    return JSON.stringify(lines);
}

function getJsonRowIdAndNames(row, name) {
    var lines = [];
    var line = {};
    line.ID = row.find("[name='row_data_hidden_ID']").val();

    var inputs = row.find("[type=hidden]");

    for (let i = 0; i < inputs.length; i++) {
        var cname = $(inputs[i]).attr("item_name");
        var value = $(inputs[i]).val();
        line[cname] = value;
    }
    lines.push(line);
    return JSON.stringify(lines);
}

var $searchWrap = $("#searchForm")
var getSearchParams = function () {
    var params = [];
    $("#searchForm").find('input,select').each(function () {
        if ($(this).attr('name')) {
            params.push($(this).attr('name') + '=' + encodeURI($(this).val()));
        }
    })
    return params.join('&');
};
var getSearchParamsNoParentId = function () {
    var params = [];
    $("#searchForm").find('input,select').each(function () {
        if ($(this).attr('name')&&$(this).attr('name')!='PARENT_ID') {
            params.push($(this).attr('name') + '=' + encodeURI($(this).val()));
        }
    })
    return params.join('&');
};

function openModel(url, name, area) {
    var json = ['90%', '90%'];
    if (area !== undefined && area != '') {
        try {
            json = area.split(",");
        } catch (e) {
            json = area;
        }
        if (json.length == 1) {
            json = area;
        }
    }
    name = name == undefined ? "窗口" : name;
    var index = layer.open({
        title: name,
        type: 2,
        shade: 0.2,
        maxmin: true,
        shadeClose: true,
        area: json,
        content: url,
        moveOut: true,
        success: function(layero, indexyyy, that){

            var body = layer.getChildFrame('body', indexyyy);
            if($(body).find('#submitButtonContainer').length>0){
                $(body).find('#submitButtonContainer').append("<button class='layui-btn  layui-btn-primary' id='closeParent' type='button'>取消</button>");
                $(body).on("click","#closeParent",function(e){
                    layui.layer.close(indexyyy);
                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                })
            }
        }
    });
    return index;
}
function openForm(url, name, area) {
    var json = ['100%', '100%'];
    if (area !== undefined && area != '') {
        try {
            json = area.split(",");
        } catch (e) {
            json = area;
        }
        if (json.length == 1) {
            json = area;
        }
    }
    name = name == undefined ? "窗口" : name;

    var index = layer.open({
        title: name,
        type: 2,
        shade: 0.2,
        maxmin: true,
        btnAlign:'c',
        shadeClose: true,
        area: json,
        content: url,
        moveOut: true,
        success: function(layero, indexyyy, that){
            var body = layer.getChildFrame('body', indexyyy);
            try{
                $(body).find('#submitbtnProxy').after("<button class='layui-btn  layui-btn-primary' id='closeParent' type='button'>取消</button>");
                $(body).on("click","#closeParent",function(e){
                    layui.layer.close(indexyyy);
                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                })
            }catch (e) {
            }
        }
    });
    return index;
}

function openFull(url, name,yestxt,yesfunc,notxt,nofunc) {
    var json = ['100%', '100%'];
    name = name == undefined ? "窗口" : name;
    var index = layer.open({
        title: name,
        type: 2,
        shade: 0.2,
        maxmin: true,
        btnAlign:'c',
        shadeClose: true,
        area: json,
        content: url,
        moveOut: true,
        btn: [yestxt, notxt],
        // 按钮1 的回调
        btn1: function(index111, layero, that){
            yesfunc(index111, layero, that);
        },
        btn2: function(index222, layero, that){
            nofunc(index222, layero, that);
        },
        success: function(layero, indexyyy, that){
            var body = layer.getChildFrame('body', indexyyy);
           // $(body).find('#submitButtonContainer').html('');
        }
    });
    return index;
}

function openModelSelect(url, name ) {
    name = name == undefined ? "窗口" : name;
    var index = layer.open({
        title: name,
        type: 2,
        shade: 0.2,
        maxmin: true,
        shadeClose: true,
        area: ['90%', '90%'],
        content: url,
        moveOut: true ,
        btn: ['确定'],
        // 按钮1 的回调
        btn1: function(indexxx, layero, that){
            var body = layer.getChildFrame('body', indexxx);
            $(body).find('#check_click_选择').click();
        }
    });
    return index;
}

function openModelReload(url, name, cancel) {
    name = name == undefined ? "窗口" : name;
    var index = layer.open({
        title: name,
        type: 2,
        shade: 0.2,
        maxmin: true,
        shadeClose: true,
        area: ['90%', '90%'],
        content: url,
        moveOut: true,
        cancel: cancel
    });
}


function openTab(title, link) {
    if(link==undefined){
        link=title;
        title="查看";
    }
    try{
        if(window.top === window.self){
            return openBlank(link);
        }
    }catch (e) {
        console.log(e);
    }
    var uniqueName = link.replace('./', '').replace(/["&'./:=%?[\]]/gi, '-').replace(/(--)/gi, '');
    window.parent.postMessage({
        from: 'ez',
        name: title,
        url: link,
        id: "tab-" + uniqueName
    }, '*');
}

function openBlank(appendUrl) {
    var a = document.createElement("a");
    a.setAttribute("href", appendUrl);
    a.setAttribute("target", "_blank");
    a.setAttribute("id", "camnpr");
    document.body.appendChild(a);
    a.click();
    a.remove();
}

/**
 *
 * @param  parent.refreshCard.prod
 */
function refreshCard(card_item_name){
    card_item_name=card_item_name.toLowerCase();
    if(card_item_name&&card_item_name.indexOf('parent.')>=0){
        card_item_name = card_item_name.replace('parent.','');
        window.parent.refreshCard(card_item_name);
        return;
    }
    if(card_item_name.indexOf('refreshcard')>=0){
        layer.closeAll();
        const parts = card_item_name.split('.');
        if (parts.length === 2 && parts[0] === "refreshcard") {
            const cardItemName = parts[1]; // 获取第二个值，如 "prod"
            // 找到对应的 iframe 并刷新
            $("[card_item_name=" + cardItemName + "]").find("iframe").attr("src", function() {
                const currentSrc = $(this).attr("src");
                return currentSrc + (currentSrc.includes('?') ? '&' : '?') + 't=' + new Date().getTime();
            });
            console.log("已刷新 card_item_name=" + cardItemName + " 的 iframe");
        } else {
            layer.message("参数配置错误，应为 reloadcard.xxx");
        }
    }
}



//parentid,id,name

function flatToTree(arr, parentId) {
    try {
        let tree = [];
        arr.forEach(item => {
            if (item.PARENT_ID == parentId) {
                let children = flatToTree(arr, item.ID);
                if (children.length > 0) {
                    item.CHILDREN = children;
                }
                tree.push(item);
            }
        });
        return tree;
    } catch (e) {
        console.log(e);
        return [];
    }

}


function watermark(settings) {
    if ($(".mask_div").length > 0) {
        return;
    }
    //默认设置
    var defaultSettings = {
        watermark_txt: "",
        watermark_x: 20, //水印起始位置x轴坐标
        watermark_y: 20, //水印起始位置Y轴坐标
        watermark_rows: 0, //水印行数
        watermark_cols: 0, //水印列数
        watermark_x_space: 100, //水印x轴间隔
        watermark_y_space: 50, //水印y轴间隔
        watermark_color: '#aaa', //水印字体颜色
        watermark_alpha: 0.1, //水印透明度
        watermark_fontsize: '14px', //水印字体大小
        watermark_font: '宋体', //水印字体
        watermark_width: 210, //水印宽度
        watermark_height: 80, //水印长度
        watermark_angle: 20 //水印倾斜度数
    };
    if (arguments.length === 1 && typeof arguments[0] === "object") {
        var src = arguments[0] || {};
        for (key in src) {
            if (src[key] && defaultSettings[key] && src[key] === defaultSettings[key]) continue;
            else if (src[key]) defaultSettings[key] = src[key];
        }
    }
    var oTemp = document.createDocumentFragment();
    //获取页面最大宽度
    var page_width = Math.max(document.body.scrollWidth, document.body.clientWidth);
    var cutWidth = page_width * 0.0150;
    var page_width = page_width - cutWidth;
    //获取页面最大高度
    var page_height = Math.max(document.body.scrollHeight, document.body.clientHeight) + 30;
    page_height = Math.max(page_height, window.innerHeight - 30);
    //如果将水印列数设置为0，或水印列数设置过大，超过页面最大宽度，则重新计算水印列数和水印x轴间隔
    if (defaultSettings.watermark_cols == 0 || (parseInt(defaultSettings.watermark_x + defaultSettings.watermark_width * defaultSettings.watermark_cols + defaultSettings.watermark_x_space * (defaultSettings.watermark_cols - 1)) > page_width)) {
        defaultSettings.watermark_cols = parseInt((page_width - defaultSettings.watermark_x + defaultSettings.watermark_x_space) / (defaultSettings.watermark_width + defaultSettings.watermark_x_space));
        defaultSettings.watermark_x_space = parseInt((page_width - defaultSettings.watermark_x - defaultSettings.watermark_width * defaultSettings.watermark_cols) / (defaultSettings.watermark_cols - 1));
    }
    //如果将水印行数设置为0，或水印行数设置过大，超过页面最大长度，则重新计算水印行数和水印y轴间隔
    if (defaultSettings.watermark_rows == 0 || (parseInt(defaultSettings.watermark_y + defaultSettings.watermark_height * defaultSettings.watermark_rows + defaultSettings.watermark_y_space * (defaultSettings.watermark_rows - 1)) > page_height)) {
        defaultSettings.watermark_rows = parseInt((defaultSettings.watermark_y_space + page_height - defaultSettings.watermark_y) / (defaultSettings.watermark_height + defaultSettings.watermark_y_space));
        defaultSettings.watermark_y_space = parseInt(((page_height - defaultSettings.watermark_y) - defaultSettings.watermark_height * defaultSettings.watermark_rows) / (defaultSettings.watermark_rows - 1));
    }
    var x;
    var y;
    for (var i = 0; i < defaultSettings.watermark_rows - 1; i++) {
        y = defaultSettings.watermark_y + (defaultSettings.watermark_y_space + defaultSettings.watermark_height) * i;
        for (var j = 0; j < defaultSettings.watermark_cols; j++) {
            x = defaultSettings.watermark_x + (defaultSettings.watermark_width + defaultSettings.watermark_x_space) * j;
            var mask_div = document.createElement('div');
            mask_div.id = 'mask_div' + i + j;
            mask_div.className = 'mask_div';
            mask_div.appendChild(document.createTextNode(defaultSettings.watermark_txt));
            //设置水印div倾斜显示
            mask_div.style.webkitTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
            mask_div.style.MozTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
            mask_div.style.msTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
            mask_div.style.OTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
            mask_div.style.transform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
            mask_div.style.visibility = "";
            mask_div.style.position = "absolute";
            mask_div.style.left = x + 'px';
            mask_div.style.top = y + 'px';
            mask_div.style.overflow = "hidden";
            mask_div.style.zIndex = "9999";
            //让水印不遮挡页面的点击事件
            mask_div.style.pointerEvents = 'none';
            mask_div.style.opacity = defaultSettings.watermark_alpha;
            mask_div.style.fontSize = defaultSettings.watermark_fontsize;
            mask_div.style.fontFamily = defaultSettings.watermark_font;
            mask_div.style.color = defaultSettings.watermark_color;
            mask_div.style.textAlign = "center";
            mask_div.style.width = defaultSettings.watermark_width + 'px';
            mask_div.style.height = defaultSettings.watermark_height + 'px';
            mask_div.style.display = "block";
            oTemp.appendChild(mask_div);
        }
        ;
    }
    ;
    document.body.appendChild(oTemp);
}
function getNow() {
    var d = new Date();
    var year = d.getFullYear();
    var month = change(d.getMonth() + 1);
    var day = change(d.getDate());
    var hour = change(d.getHours());
    var minute = change(d.getMinutes());
    var second = change(d.getSeconds());

    function change(t) {
        if (t < 10) {
            return "0" + t;
        } else {
            return t;
        }
    }

    var time = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second + '';
    return time;
}



function ezopen(openType, title, appendUrl, area) {
    appendUrl = ($("#contextName").val()||'') + appendUrl;
    if(openType&&openType.indexOf('parent.')>=0){
        openType = openType.replace('parent.','');
        parent.window.ezopen(openType, title, appendUrl, area);
        return;
    }
    switch (openType) {
        case 'APPEND_PARAM':
            if (appendUrl != null && appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + getSearchParams() + '&' + getCheckIdsUrl();
            } else {
                appendUrl += '&' + getSearchParams() + '&' + getCheckIdsUrl();
            }
            openModel(appendUrl, title, area);
            break;
        case 'MODEL':
            openModel(appendUrl, title, area);
            break;
        case 'FORM':
            openForm(appendUrl, title, area);
            break;
        case 'FULL':
            openFull(appendUrl, title ,'确定', function(index111, layero, that){
            var body = layer.getChildFrame('body', index111);

            $(body).find('#submitbtn').click();
        },'取消',
           function(index222, layero, that){
                layer.close(index222);
            });
            break;
        case 'CONFIRM_MODEL':
            layer.confirm('确认操作?', {icon: 3, title: '提示'}, function (index) {
                layer.close(index);
                openModel(appendUrl, title, area);
            })
            break;
        case '_BLANK':
            openBlank(appendUrl);
            break;
        case '_BLANK_PARAM':
            if (appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + getSearchParams() + '&' + getCheckIdsUrl();
            } else {
                appendUrl += '&' + getSearchParams() + '&' + getCheckIdsUrl();
            }
            openBlank(appendUrl);
            break;
        case '_BLANK_PARAM_COLUMN':
            if (appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + getSearchParams() + '&' + getCheckIdsUrl();
            } else {
                appendUrl += '&' + getSearchParams() + '&' + getCheckIdsUrl();
            }
            var key = 'EZ_CONFIG_' + $("#ENCRYPT_LIST_ID").val();
            var jsonconfig = localStorage.getItem(key);
            if (jsonconfig != undefined) {
                var json = JSON.parse(jsonconfig);
                var search = json.search;
                var column = json.column;
                if (jsonconfig != column && column.length > 0) {
                    var columnurl = "";
                    for (let index = 0; index < column.length; index++) {
                        columnurl += column[index] + ","
                    }
                    appendUrl += "_BLANK_PARAM_COLUMN=" + columnurl;
                }
            }
            openBlank(appendUrl);
            break;
        case 'LOCATION':
            location.href = appendUrl;
            break;
        case 'PARENT':
            openTab(title, appendUrl)

            break;
        case 'AJAX':
            $.getJSON(appendUrl, function (result) {
                if (result.success) {
                    layer.alert("操作成功", function (index) {
                        location.reload();
                    })
                } else {
                    layer.alert("操作失败")
                }
            })
            // openTab(title,appendUrl)
            break;
        case 'CONFIRM_AJAX':
            var title = title;
            layer.confirm(title, {icon: 3, title: '提示'}, function (index) {
                $(".layuimini-loader").show();
                $.getJSON(appendUrl, function (result) {
                    $(".layuimini-loader").hide();
                    if (result.success) {
                        layer.alert("操作成功", function (index) {
                            location.reload();
                        })
                    } else {
                        layer.alert("操作失败:" + result.message)
                    }
                })
                layer.close(index);
            });

            // openTab(title,appendUrl)
            break;
        default:
            openModel(appendUrl, title, area);
            console.log('无opentype默认model')
    }
}


//判断是否是手机
function IsMobile() {
    return $("#_EZ_MOBILE_FLAG").val() === '1'
}

function toFloat(num) {
    var result = parseFloat(num);
    if (isNaN(result)) {
        return 0;
    }
    return result;
}

function isNumeric(value) {
    return !isNaN(value) && !isNaN(parseFloat(value));
}



function isPositiveInteger(value) {
    // 判断是否全是数字
    if (/^\d+$/.test(value)) {
        // 判断是否大于0
        return parseInt(value, 10) > 0;
    } else {
        return false;
    }
}


function copy(text) {
    // navigator.clipboard.writeText(text)
    //     .then(() => layer.msg('复制成功'))
    //     .catch(err => layer.msg('复制失败：', err));
    layui.lay.clipboard.writeText({
        text: text,
        done: function() {
            layer.msg('已复制', {icon: 1});
        },
        error: function() {
            layer.msg('复制失败', {icon: 2});
        }
    });
}

function parse(f) {
    navigator.clipboard.readText()
        .then(text => f(text))
        .catch(err => layer.msg('读取失败：', err));

}

function mypost(url,param,success){
    $.post(url , param,function(response) {
        if(response.success){
            success(response);
        }else{
             alert("服务端错误："+response.message);
        }
    } , 'json').fail(function (xhr, status, error)  {
         alert("网络异常");
         console.log(xhr.responseText+JSON.stringify(error)+status);
    });
}

function lazyImage(){

    // 选择所有需要懒加载的图片
    const images = document.querySelectorAll('img[data-original]');

    // 现代浏览器使用 Intersection Observer
    if ('IntersectionObserver' in window) {
        const observerOptions = {
            rootMargin: '200px', // 提前200px加载
            threshold: 0.01
        };

        const observer = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    const src = img.getAttribute('data-original');
                    if (src) {
                        img.src = src;
                       // img.removeAttribute('data-original');
                        observer.unobserve(img); // 停止观察已加载的图片
                    }
                }
            });
        }, observerOptions);

        images.forEach(img => observer.observe(img));

    } else {
        // 旧浏览器回退方案：使用滚动和视口检测
        const isInViewport = (element) => {
            const rect = element.getBoundingClientRect();
            return (
                rect.top < window.innerHeight && rect.bottom > 0 &&
                rect.left < window.innerWidth && rect.right > 0
            );
        };

        const lazyLoad = () => {
            images.forEach(img => {
                if (img.hasAttribute('data-original') && isInViewport(img)) {
                    img.src = img.getAttribute('data-original');
                    img.removeAttribute('data-original');
                }
            });
        };

        // 防抖优化滚动和调整大小事件
        const debounce = (func, wait = 100) => {
            let timeout;
            return (...args) => {
                clearTimeout(timeout);
                timeout = setTimeout(() => func.apply(this, args), wait);
            };
        };

        window.addEventListener('scroll', debounce(lazyLoad));
        window.addEventListener('resize', debounce(lazyLoad));
        lazyLoad(); // 初始化加载可视图片
    }
}

