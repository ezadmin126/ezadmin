/**
 * 注册中心定义
 * @type {*|{registry(*): *, get: {}}}
 */
window.Global = window.Global || {
    registries: {},
    // 创建或获取一个注册表
    registry(name) {
        if (!this.registries[name]) {
            this.registries[name] = {
                items: {},
                register(key, fn) {
                    this.items[key] = fn;
                },
                get(key) {
                    return this.items[key] || null;
                }
            };
        }
        return this.registries[name];
    },
    get(name) {
        return this.registry('global').get(name);
    },
    set(name, value) {
        this.registry('global').register(name, value);
    },
};
const eventMap = Object.create(null);

Global.onClick = function (name, handler) {
    eventMap[name] = handler;
};
// 统一事件委托
document.addEventListener('click', function (e) {
    const el = e.target.closest('[data-click]');
    if (!el) return;

    const action = el.dataset.click;
    const fn = eventMap[action];

    if (typeof fn === 'function') {
        fn.call(el, e);
    }
});
//         item.alt = _this.attr("alt");
//         item.pid = _this.attr("pid");
//         item.src = _this.attr("orgsrc") || _this.attr("src");
//         item.thumb = _this.attr("orgsrc");
Global.onClick('viewer-image', function (e) {
    var dataset = this.dataset;
    var _this=this;
    if (this.__lock) return;
    this.__lock = true;
    setTimeout(() => this.__lock = false, 500);
    var imgConfig={
        shade: 0.5,
        photos: {
            "title": _this.getAttribute("title"),
            "start": 0,
            "data": [
                {
                    "alt": _this.getAttribute("title"),
                    "pid": 0,
                    "src": _this.getAttribute("src")||dataset.original
                }
            ]
        },
        footer: false // 是否隐藏底部栏 --- 2.8+
        , success: function () {
            // if ($(".layui-layer-photos .layer-layer-photos-main img").eq(0).height() > 500) {
            //     $(".layui-layer-photos .layer-layer-photos-main img").eq(0).css("height", "500px");
            //     $(".layui-layer-photos .layer-layer-photos-main img").eq(0).css("width", "auto");
            //     $(".layui-layer-photos").eq(0).css("top", $(window).height() / 2 - 250)
            // }
        }
    };
    if(dataset.parent==true){
        parent.window.layer.photos(imgConfig);
    }else{
        layer.photos(imgConfig);
    }
    e.preventDefault();
    e.stopPropagation();
    return false;
});


Global.onClick('ez-button', function (e) {
    var _this=this;
    if (this.__lock) return;
    this.__lock = true;
    setTimeout(() => this.__lock = false, 500);
    const jsonStr = _this.getAttribute('data-propsjson');
    let cfg;
    try {
        cfg = JSON.parse(jsonStr);
    } catch (err) {
        cfg = {};
    }
    ezopen(cfg.opentype, cfg.windowname, cfg.url, cfg.area);

    e.preventDefault();
    e.stopPropagation();
    return false;
});

/**
 * 转成数字数组
 * @param val
 * @returns {number[]|*|*[]}
 */
Global.toNumberArray = function (val) {
    if (Array.isArray(val)) return val.map(Number);
    if (typeof val !== 'string') return [];
    return val
        .split(',')
        .map(v => Number(v.trim()))
        .filter(Number.isFinite);
};

/**
 * 合并两个json 以第一个为准
 * @param json1
 * @param json2
 * @returns {*}
 */
Global.deepUnion =function (json1, json2) {
    // 如果任意一个不是对象，直接取 json1（以第一个为准）
    if (json1 === null || typeof json1 !== 'object') return json1;
    if (json2 === null || typeof json2 !== 'object') return json1;

    const result = { ...json2 }; // 先复制 json2 的所有字段

    for (const key in json1) {
        if (json1.hasOwnProperty(key)) {
            if (
                typeof json1[key] === 'object' && json1[key] !== null &&
                typeof json2[key] === 'object' && json2[key] !== null &&
                !Array.isArray(json1[key]) && !Array.isArray(json2[key])
            ) {
                // 嵌套对象 → 递归合并
                result[key] = Global.deepUnion(json1[key], json2[key]);
            } else {
                // 非对象 或 数组 → 以第一个为准
                result[key] = json1[key];
            }
        }
    }

    return result;
}
/**
 * list转树结构
 * @param list
 * @param idKey 默认id
 * @param parentKey 默认parentId
 * @param childrenKey 默认children
 * @returns {*[]}
 */
Global.listToTree=function(list, idKey = 'id', parentKey = 'parentId', childrenKey = 'children') {
    const tree = [];
    const lookup = {};

    // 初始化所有节点
    list.forEach(item => {
        lookup[item[idKey]] = { ...item, [childrenKey]: [] };
    });

    // 建树
    list.forEach(item => {
        const id = item[idKey];
        const parentId = item[parentKey];

        if (parentId == null || parentId === 0 || !lookup[parentId]) {
            // 根节点
            tree.push(lookup[id]);
        } else {
            // 子节点
            lookup[parentId][childrenKey].push(lookup[id]);
        }
    });

    return tree;
}

Global.getAllValues = function (tree) {
    const values = [];

    function traverse(nodes) {
        if (!nodes || !Array.isArray(nodes)) return;

        nodes.forEach(node => {
            if (node.value !== undefined) {
                values.push(node.value);
            }
            if (node.children && node.children.length > 0) {
                traverse(node.children);
            }
        });
    }

    traverse(tree);
    return values;
}







/**
 * 逻辑上都认为是true的情况
 * @param c
 * @returns {boolean}
 */
Global.logicTrue = function (c) {
    return ['true', '1', 'yes', 'on']
        .includes(String(c).toLowerCase());
};


// 安全解析JSON字符串或返回已解析的对象
Global.safeParseJSON=function(data, defaultValue) {
    if (!data) {
        return defaultValue || null;
    }
    // 如果已经是对象，直接返回
    if (typeof data === 'object') {
        return data;
    }
    // 如果是字符串，尝试解析
    if (typeof data === 'string') {
        try {
            return JSON.parse(data);
        } catch (e) {
            console.error('JSON解析失败:', e, '原始数据:', data);
            return defaultValue || null;
        }
    }
    return defaultValue || null;
}

var holiday = [[], []];
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
var rangeShortCut = [{
    text: "昨天",
    value: function () {
        var today = new Date();
        const preday = new Date(today.getTime() - (24 * 60 * 60 * 1000));
        return [
            preday,
            preday
        ];
    }
},
    {
        text: "上周",
        value: function () {
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
        value: function () {
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
        value: function () {
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
        value: function () {
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
        value: function () {
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
        value: function () {
            var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth();
            return [
                new Date(year, month, 1),
                new Date(year, month + 1, 0, 23, 59, 59)
            ];
        }
    },
    {
        text: "去年",
        value: function () {
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
        value: function () {
            const now = new Date();
            const year = now.getFullYear();
            const januaryFirst = new Date(year, 0, 1);
            return [
                januaryFirst,
                now
            ];
        }
    }
];


function ezopen(openType, title, appendUrl, area) {
    var contextNameElement = document.getElementById('contextName');
    appendUrl = (contextNameElement ? contextNameElement.value : '') + appendUrl;
    if (openType && openType.indexOf('parent.') >= 0) {
        openType = openType.replace('parent.', '');
        parent.window.ezopen(openType, title, appendUrl, area);
        return;
    }
    var params = [];
    $("form").find('input,select').each(function () {
        if ($(this).attr('name')) {
            params.push($(this).attr('name') + '=' + encodeURI($(this).val()));
        }
    })
    var searchParams = params.join('&');



    switch (openType) {
        case 'APPEND_PARAM':
            if (appendUrl != null && appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + searchParams  ;
            } else {
                appendUrl += '&' + searchParams  ;
            }
            openModel(appendUrl, title, area);
            break;
        case 'MODAL':
            openModel(appendUrl, title, area);
            break;
        case 'FORM':
            openForm(appendUrl, title, area);
            break;
        case 'FULL':
            openFull(appendUrl, title, '确定', function (index111, layero, that) {
                    var body = layer.getChildFrame('body', index111);

                    var submitBtn = body.querySelector('#submitbtn');
                    if (submitBtn) {
                        submitBtn.click();
                    }
                }, '取消',
                function (index222, layero, that) {
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
                appendUrl += '?' + searchParams ;
            } else {
                appendUrl += '&' + searchParams ;
            }
            openBlank(appendUrl);
            break;
        // case '_BLANK_PARAM_COLUMN':
        //     if (appendUrl.indexOf('?') <= 0) {
        //         appendUrl += '?' + searchParams ;
        //     } else {
        //         appendUrl += '&' + searchParams ;
        //     }
        //     var encryptListIdElement = document.getElementById('ENCRYPT_LIST_ID');
        //     var key = 'EZ_CONFIG_' + (encryptListIdElement ? encryptListIdElement.value : '');
        //     var jsonconfig = localStorage.getItem(key);
        //     if (jsonconfig != undefined) {
        //         var json = JSON.parse(jsonconfig);
        //         var search = json.search;
        //         var column = json.column;
        //         if (jsonconfig != column && column.length > 0) {
        //             var columnurl = "";
        //             for (let index = 0; index < column.length; index++) {
        //                 columnurl += column[index] + ","
        //             }
        //             appendUrl += "_BLANK_PARAM_COLUMN=" + columnurl;
        //         }
        //     }
        //     openBlank(appendUrl);
        //     break;
        case 'LOCATION':
            location.href = appendUrl;
            break;
        case 'PARENT':
            openTab(title, appendUrl)
            break;
        case 'AJAX':
            fetch(appendUrl)
                .then(response => response.json())
                .then(function (result) {
                    if (result.success) {
                        layer.msg("操作成功",{time:500}, function (index) {
                            location.reload();
                        })
                    } else {
                        layer.msg("操作失败:" + result.message)
                    }
                })
                .catch(function(error) {
                    console.error('Error:', error);
                });
            // openTab(title,appendUrl)
            break;
        case 'CONFIRM_AJAX':
            var title = title;
            layer.confirm(title, {icon: 3, title: '提示'}, function (index) {
                var loadingIndex=layer.load(3, {shade: [1, '#FFF']});
                fetch(appendUrl)
                    .then(response => response.json())
                    .then(function (result) {
                        layer.close(loadingIndex);
                        if (result.success) {
                            layer.msg("操作成功",{time:500}, function (index) {
                                location.reload();
                            })
                        } else {
                            layer.alert("操作失败:" + result.message)
                        }
                    })
                    .catch(function(error) {
                        console.error('Error:', error);
                        layer.close(loadingIndex);
                    });
                layer.close(index);
            });
            break;
        case 'CONFIRM_AJAX_LIST':
            var title = title;
            layer.confirm(title, {icon: 3, title: '提示'}, function (index) {
                var loadingIndex=layer.load(3, {shade: [1, '#FFF']});
                fetch(appendUrl)
                    .then(response => response.json())
                    .then(function (result) {
                        layer.close(loadingIndex);
                        if (result.success) {
                            Global.get("table").reload({where: layui.form.val('searchForm')});
                            layer.msg("操作成功")
                        } else {
                            layer.alert("操作失败:" + result.message)
                        }
                    })
                    .catch(function(error) {
                        console.error('Error:', error);
                        layer.close(loadingIndex);
                    });
                layer.close(index);
            });
            break;
        default:
            openModel(appendUrl, title, area);
            console.log('无opentype默认model')
    }
}

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
        success: function (layero, indexyyy, that) {

            var body = layer.getChildFrame('body', indexyyy);
            if ($(body).find('#submitButtonContainer').length > 0) {
                $(body).find('#submitButtonContainer').append("<button class='layui-btn  layui-btn-primary' id='closeParent' type='button'>取消</button>");
                $(body).on("click", "#closeParent", function (e) {
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
        btnAlign: 'c',
        shadeClose: true,
        area: json,
        content: url,
        moveOut: true,
        success: function (layero, indexyyy, that) {
            var body = layer.getChildFrame('body', indexyyy);
            try {
                $(body).find('#submitbtnProxy').after("<button class='layui-btn  layui-btn-primary' id='closeParent' type='button'>取消</button>");
                $(body).on("click", "#closeParent", function (e) {
                    layui.layer.close(indexyyy);
                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                })
            } catch (e) {
            }
        }
    });
    return index;
}

function openFull(url, name, yestxt, yesfunc, notxt, nofunc) {
    var json = ['100%', '100%'];
    name = name == undefined ? "窗口" : name;
    var index = layer.open({
        title: name,
        type: 2,
        shade: 0.2,
        maxmin: true,
        btnAlign: 'c',
        shadeClose: true,
        area: json,
        content: url,
        moveOut: true,
        btn: [yestxt, notxt],
        // 按钮1 的回调
        btn1: function (index111, layero, that) {
            yesfunc(index111, layero, that);
        },
        btn2: function (index222, layero, that) {
            nofunc(index222, layero, that);
        },
        success: function (layero, indexyyy, that) {
            var body = layer.getChildFrame('body', indexyyy);
            // $(body).find('#submitButtonContainer').html('');
        }
    });
    return index;
}

function openModelSelect(url, name) {
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
        btn: ['确定'],
        // 按钮1 的回调
        btn1: function (indexxx, layero, that) {
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
    if (link == undefined) {
        link = title;
        title = "查看";
    }
    try {
        if (window.top === window.self) {
            return openBlank(link);
        }
    } catch (e) {
        console.log(e);
    }

    // 检查父窗口和当前窗口的域名是否一致
    let parentHost = '';
    let currentHost = window.location.host;
    let useAbsolute = false;
    try {
        parentHost = window.parent.location.host;
        if (parentHost !== currentHost) {
            useAbsolute = true;
        }
    } catch (e) {
        // 跨域无法访问，必须用绝对地址
        useAbsolute = true;
    }

    // 如果需要，转成绝对地址
    if (useAbsolute && !/^https?:\/\//i.test(link)) {
        // 相对路径转绝对路径
        let a = document.createElement('a');
        a.href = link;
        link = a.href;
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
function refreshCard(card_item_name) {
    card_item_name = card_item_name.toLowerCase();
    if (card_item_name && card_item_name.indexOf('parent.') >= 0) {
        card_item_name = card_item_name.replace('parent.', '');
        window.parent.refreshCard(card_item_name);
        return;
    }
    if (card_item_name.indexOf('refreshcard') >= 0) {
        layer.closeAll();
        const parts = card_item_name.split('.');
        if (parts.length === 2 && parts[0] === "refreshcard") {
            const cardItemName = parts[1]; // 获取第二个值，如 "prod"
            // 找到对应的 iframe 并刷新
            $("[card_item_name=" + cardItemName + "]").find("iframe").attr("src", function () {
                const currentSrc = $(this).attr("src");
                return currentSrc + (currentSrc.includes('?') ? '&' : '?') + 't=' + new Date().getTime();
            });
            console.log("已刷新 card_item_name=" + cardItemName + " 的 iframe");
        } else {
            layer.message("参数配置错误，应为 reloadcard.xxx");
        }
    }
}


