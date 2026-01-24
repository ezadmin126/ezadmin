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
        return this.registry['global'].get(name);
    },
    set(name, value) {
        this.registry['global'].register(name, value);
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
            "data": dataset
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
    const jsonStr = _this.getAttribute('data-dataJson');
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
        case '_BLANK_PARAM_COLUMN':
            if (appendUrl.indexOf('?') <= 0) {
                appendUrl += '?' + searchParams ;
            } else {
                appendUrl += '&' + searchParams ;
            }
            var encryptListIdElement = document.getElementById('ENCRYPT_LIST_ID');
            var key = 'EZ_CONFIG_' + (encryptListIdElement ? encryptListIdElement.value : '');
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
                var loaderElements = document.querySelectorAll('.layuimini-loader');
                for (var i = 0; i < loaderElements.length; i++) {
                    loaderElements[i].style.display = 'block';
                }
                fetch(appendUrl)
                    .then(response => response.json())
                    .then(function (result) {
                        var loaderElements = document.querySelectorAll('.layuimini-loader');
                        for (var i = 0; i < loaderElements.length; i++) {
                            loaderElements[i].style.display = 'none';
                        }
                        if (result.success) {
                            layer.alert("操作成功",  function (index) {
                                location.reload();
                            })
                        } else {
                            layer.alert("操作失败:" + result.message)
                        }
                    })
                    .catch(function(error) {
                        console.error('Error:', error);
                        var loaderElements = document.querySelectorAll('.layuimini-loader');
                        for (var i = 0; i < loaderElements.length; i++) {
                            loaderElements[i].style.display = 'none';
                        }
                    });
                layer.close(index);
            });

            // openTab(title,appendUrl)
            break;
        default:
            openModel(appendUrl, title, area);
            console.log('无opentype默认model')
    }
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
]