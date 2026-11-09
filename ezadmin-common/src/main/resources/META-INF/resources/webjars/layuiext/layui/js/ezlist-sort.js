/**
 * 列表页「排序设置」：搜索项 / 表格列 的顺序由用户在弹框中配置，仅保存在本地 localStorage。
 * 不依赖 listTemplate 的改动：通过包装 table.render / treeTable.render，在表格渲染之前
 *   1. 按本地配置重排搜索项 DOM 与表格列
 *   2. 在 defaultToolbar 里追加「排序设置」按钮
 * 配置存放在列表原有的 EZ_CONFIG_NEW<ENCRYPT_LIST_ID> 中：
 *   searchOrder / colOrder     顺序
 *   searchHidden / colHidden   不显示的搜索项 key / 列 field（隐藏搜索项时会清空其值，不参与查询）
 * colwidth 等其它配置不受影响。弹框接管了 layui 自带的列筛选（工具栏里的 filter 会被移除）。
 */
(function () {
    "use strict";
    if (!window.jQuery || !window.layui) return;
    var $ = window.jQuery;
    var TABLE_ID = 'dataTable';

    function storageKey() {
        return 'EZ_CONFIG_NEW' + $("#ENCRYPT_LIST_ID").val();
    }

    function readCfg() {
        try {
            return JSON.parse(localStorage.getItem(storageKey()) || '{}') || {};
        } catch (e) {
            return {};
        }
    }

    function writeCfg(cfg) {
        try {
            localStorage.setItem(storageKey(), JSON.stringify(cfg));
        } catch (e) {
        }
    }

    // 按 keys 排序，未出现在 keys 里的保持原相对顺序追加在后
    function sortByKeys(arr, keys, keyFn) {
        var map = {}, ordered = [], left = [];
        $.each(arr, function (i, o) {
            map[keyFn(o)] = o;
        });
        $.each(keys, function (i, k) {
            if (map.hasOwnProperty(k)) {
                ordered.push(map[k]);
                delete map[k];
            }
        });
        $.each(arr, function (i, o) {
            if (map.hasOwnProperty(keyFn(o))) left.push(o);
        });
        return ordered.concat(left);
    }

    // ---------- 搜索项 ----------
    // 搜索项根节点是组件模板里的 .component-col（隐藏项另带 form-item-hidden）
    var ITEM_SEL = '.component-col';

    function itemKey($it) {
        return $it.find('.layui-form-item[item_name]').first().attr('item_name')
            || $it.find('[name]').first().attr('name')
            || $.trim($it.find('.layui-form-label').first().text());
    }

    function itemLabel($it) {
        var t = $.trim($it.find('.layui-form-label').first().text()).replace(/[：:]\s*$/, '');
        if (t) return t;
        // unioninput 等没有 label 元素的组件：取组件配置里的 label，加"下拉搜索"
        var cfg = {};
        try {
            cfg = JSON.parse($it.attr('data-config-json') || '{}');
        } catch (e) {
        }
        if (cfg.label) {
            return /^union/.test(cfg.component || '') ? cfg.label + '下拉搜索' : cfg.label;
        }
        return itemKey($it);
    }

    // 用户在弹框里"不显示"的搜索项：打 ez-user-hidden，同时复用模板的 form-item-hidden，
    // 这样 calculateSearchItemDisplay 的 show()/行数计算都会自动忽略它
    var USER_HIDDEN = 'ez-user-hidden';

    // 可配置的搜索项：排除组件本身的隐藏域（hidden 组件），包含用户隐藏的
    function sortableItems() {
        return $(".searchcontent").children(ITEM_SEL).not('.form-item-hidden:not(.' + USER_HIDDEN + ')').toArray();
    }

    // 各行容器的"槽位"不变，组件隐藏域原地不动，其余项按新顺序依次填入槽位
    function applySearchOrder(keys) {
        if (!keys || keys.length === 0) return false;
        var ordered = sortByKeys(sortableItems(), keys, function (el) {
            return itemKey($(el));
        });
        var idx = 0;
        $(".searchcontent").each(function () {
            var $row = $(this), next = [];
            $row.children().each(function () {
                var $c = $(this);
                var native = $c.hasClass('form-item-hidden') && !$c.hasClass(USER_HIDDEN);
                next.push($c.is(ITEM_SEL) && !native ? ordered[idx++] : this);
            });
            $.each(next, function (i, el) {
                $row.append(el);
            });
        });
        return true;
    }

    function clearItemValue($it) {
        $it.find('[name]').each(function () {
            var t = (this.getAttribute('type') || '').toLowerCase();
            if (t === 'checkbox' || t === 'radio') {
                this.checked = false;
            } else {
                this.value = '';
            }
        });
        try {
            var key = itemKey($it);
            if (window.xmSelect) {
                var ins = window.xmSelect.get('#ez-xmselect' + key, true);
                if (ins) ins.setValue([]);
            }
        } catch (e) {
        }
    }

    // 应用"不显示"：隐藏并清空值，返回是否有隐藏项
    function applySearchHidden(keys) {
        keys = keys || [];
        var set = {};
        $.each(keys, function (i, k) {
            set[k] = true;
        });
        $.each(sortableItems(), function (i, el) {
            var $el = $(el);
            if (set[itemKey($el)]) {
                $el.addClass('form-item-hidden ' + USER_HIDDEN).hide();
                clearItemValue($el);
            }
        });
        return keys.length > 0;
    }

    // 查询前再清一遍：重置按钮/URL 默认值可能把隐藏项的值又填回去
    function clearHiddenValues() {
        $('.' + USER_HIDDEN).each(function () {
            var $it = $(this), dirty = false;
            $it.find('[name]').each(function () {
                var t = (this.getAttribute('type') || '').toLowerCase();
                if ((t === 'checkbox' || t === 'radio') ? this.checked : this.value) dirty = true;
            });
            if (dirty) clearItemValue($it);
        });
    }

    // ---------- 表格列 ----------
    function isDataCol(c) {
        return c && c.field && !c.type && !c.toolbar && c.field !== 'rowbutton';
    }

    function colTitle(c) {
        return c.fieldTitle || $('<div>').html(c.title || '').text() || c.field;
    }

    function applyColOrder(cols, keys) {
        var slots = [], items = [];
        $.each(cols, function (i, c) {
            if (isDataCol(c)) {
                slots.push(i);
                items.push(c);
            }
        });
        if (items.length === 0) return items;
        if (keys && keys.length) {
            items = sortByKeys(items, keys, function (c) {
                return c.field;
            });
            $.each(slots, function (i, pos) {
                cols[pos] = items[i];
            });
        }
        return items;
    }

    // ---------- 弹框 ----------
    function enableListDrag($ul) {
        var dragEl = null;
        $ul.on('dragstart', 'li', function (e) {
            dragEl = this;
            $(this).addClass('ez-dragging');
            e.originalEvent.dataTransfer.effectAllowed = 'move';
            e.originalEvent.dataTransfer.setData('text/plain', '');
        });
        $ul.on('dragover', 'li', function (e) {
            if (!dragEl || this === dragEl) return;
            e.preventDefault();
            $(this).addClass('ez-drag-over');
        });
        $ul.on('dragleave', 'li', function () {
            $(this).removeClass('ez-drag-over');
        });
        $ul.on('drop', 'li', function (e) {
            e.preventDefault();
            if (!dragEl || this === dragEl) return;
            var after = dragEl.compareDocumentPosition(this) & Node.DOCUMENT_POSITION_FOLLOWING;
            if (after) {
                $(this).after(dragEl);
            } else {
                $(this).before(dragEl);
            }
        });
        $ul.on('dragend', 'li', function () {
            $ul.find('li').removeClass('ez-dragging ez-drag-over');
            dragEl = null;
        });
    }

    function injectStyle() {
        if (document.getElementById('ez-sort-style')) return;
        var css = '.ez-sort-wrap{display:flex;gap:16px;padding:12px}'
            + '.ez-sort-col{flex:1;min-width:0}'
            + '.ez-sort-col h4{margin:0 0 8px;font-weight:600}'
            + '.ez-sort-list{list-style:none;margin:0;padding:0;max-height:420px;overflow:auto;border:1px solid #e6e6e6}'
            + '.ez-sort-list li{display:flex;align-items:center;padding:5px 10px;border-bottom:1px solid #f2f2f2;background:#fff;user-select:none}'
            + '.ez-sort-list li .ez-handle{cursor:move;color:#999;margin-right:6px}'
            + '.ez-sort-list li .ez-mv{cursor:pointer;color:#1e9fff;padding:0 4px;margin-right:2px}'
            + '.ez-sort-list li .ez-mv:hover{background:#e8f4ff}'
            + '.ez-sort-list li label{flex:1;min-width:0;display:flex;align-items:center;cursor:pointer;margin-left:6px}'
            + '.ez-sort-list li label input{margin-right:6px}'
            + '.ez-sort-list li label span{overflow:hidden;text-overflow:ellipsis;white-space:nowrap}'
            + '.ez-sort-list li.ez-off label span{color:#bbb;text-decoration:line-through}'
            + '.ez-sort-list li.ez-empty{color:#999}'
            + '.ez-sort-list li.ez-dragging{opacity:.4}'
            + '.ez-sort-list li.ez-drag-over{outline:1px dashed #1e9fff}'
            + '.ez-sort-tip{padding:0 12px 8px;color:#999;font-size:12px}';
        $('<style id="ez-sort-style"></style>').text(css).appendTo('head');
    }

    function esc(t) {
        return $('<div>').text(t == null ? '' : t).html();
    }

    function rowHtml(key, label, shown) {
        return '<li draggable="true" data-key="' + esc(key) + '"' + (shown ? '' : ' class="ez-off"') + '>'
            + '<span class="ez-handle" title="拖动排序">&#9776;</span>'
            + '<a class="ez-mv" data-d="down" title="下移">&darr;</a>'
            + '<a class="ez-mv" data-d="up" title="上移">&uarr;</a>'
            + '<a class="ez-mv" data-d="top" title="置顶">&#8607;</a>'
            + '<label><input type="checkbox"' + (shown ? ' checked' : '') + '><span>' + esc(label) + '</span></label>'
            + '</li>';
    }

    function bindList($ul) {
        enableListDrag($ul);
        $ul.on('click', '.ez-mv', function () {
            var $li = $(this).closest('li');
            var d = $(this).attr('data-d');
            if (d === 'top') {
                $li.parent().prepend($li);
            } else if (d === 'up') {
                $li.prev('li').before($li);
            } else {
                $li.next('li').after($li);
            }
        });
        $ul.on('change', 'input[type=checkbox]', function () {
            $(this).closest('li').toggleClass('ez-off', !this.checked);
        });
    }

    // 当前表格的渲染状态：prepare 时记录，供弹框读取当前列
    var state = {opts: null, cols: null, dataCols: []};

    function openDialog() {
        injectStyle();
        var dataCols = state.dataCols;
        var sHtml = '', cHtml = '';
        $.each(sortableItems(), function (i, el) {
            var $el = $(el);
            sHtml += rowHtml(itemKey($el), itemLabel($el), !$el.hasClass(USER_HIDDEN));
        });
        $.each(dataCols, function (i, c) {
            cHtml += rowHtml(c.field, colTitle(c), c.hide !== true);
        });
        var empty = '<li class="ez-empty">无</li>';
        var html = '<div class="ez-sort-wrap">'
            + '<div class="ez-sort-col"><h4>搜索项</h4><ul class="ez-sort-list" id="ezSortSearch">' + (sHtml || empty) + '</ul></div>'
            + '<div class="ez-sort-col"><h4>表格列</h4><ul class="ez-sort-list" id="ezSortCols">' + (cHtml || empty) + '</ul></div>'
            + '</div><div class="ez-sort-tip">拖动 ☰ 或点 ↓ ↑ ↟（置顶）调整顺序；取消勾选即不显示（不显示的搜索项会清空其值，不参与查询）。<br>列宽可在列表表头直接拖动设置。</div>';

        function read(sel) {
            var all = [], off = [];
            $(sel).find('li[data-key]').each(function () {
                var k = $(this).attr('data-key');
                all.push(k);
                if (!$(this).find('input[type=checkbox]').prop('checked')) off.push(k);
            });
            return {all: all, off: off};
        }

        layui.layer.open({
            type: 1,
            title: '设置',
            area: ['780px', '620px'],
            shadeClose: false,
            content: html,
            btn: ['确定', '恢复默认', '取消'],
            success: function () {
                bindList($('#ezSortSearch'));
                bindList($('#ezSortCols'));
            },
            yes: function () {
                var cfg = readCfg(), s = read('#ezSortSearch'), c = read('#ezSortCols');
                cfg.searchOrder = s.all;
                cfg.searchHidden = s.off;
                cfg.colOrder = c.all;
                cfg.colHidden = c.off;
                writeCfg(cfg);
                location.reload();
            },
            btn2: function () {
                // 只清顺序/显隐，不动列宽等其它配置
                var cfg = readCfg();
                delete cfg.searchOrder;
                delete cfg.searchHidden;
                delete cfg.colOrder;
                delete cfg.colHidden;
                writeCfg(cfg);
                // 老的 layui 列筛选记录（全局共用的 table-filter-test）也一并清掉，否则恢复后仍可能被隐藏
                $.each(dataCols, function (i, c) {
                    try {
                        layui.data('table-filter-test', {key: c.field, remove: true});
                    } catch (e) {
                    }
                });
                location.reload();
                return false;
            }
        });
    }

    // ---------- 渲染前处理 ----------
    function prepare(opts) {
        var cfg = readCfg();

        var changed = applySearchOrder(cfg.searchOrder);
        changed = applySearchHidden(cfg.searchHidden) || changed;
        if (changed && typeof window.calculateSearchItemDisplay === 'function') {
            // calculateSearchItemDisplay 不是幂等的：上一次被它折叠(display:none)的项 offset().top 为 0 会被忽略，
            // 再算就会误判成"不超过 2 行"而隐藏展开按钮。所以先全部显示再重算
            $(".searchcontent").children(ITEM_SEL).not('.form-item-hidden').show();
            window.calculateSearchItemDisplay();
        }

        // 查询前清空被隐藏搜索项的值
        if (!window.__ezSortMergeWrapped && typeof window.mergeWhereParams === 'function') {
            var rawMerge = window.mergeWhereParams;
            window.mergeWhereParams = function () {
                clearHiddenValues();
                return rawMerge.apply(this, arguments);
            };
            window.__ezSortMergeWrapped = true;
        }

        if (opts.cols && opts.cols[0]) {
            state.opts = opts;
            state.cols = opts.cols[0];
            state.dataCols = applyColOrder(state.cols, cfg.colOrder);
            if ($.isArray(cfg.colHidden)) {
                $.each(state.dataCols, function (i, c) {
                    c.hide = $.inArray(c.field, cfg.colHidden) >= 0;
                });
            }
        }

        if ($.isArray(opts.defaultToolbar)) {
            // 显隐交给弹框，去掉 layui 自带的列筛选，避免两套配置打架
            opts.defaultToolbar = $.grep(opts.defaultToolbar, function (t) {
                return t !== 'filter';
            });
            var has = false;
            $.each(opts.defaultToolbar, function (i, t) {
                if (t && t.layEvent === 'EZ_SORT_SETTING') has = true;
            });
            if (!has) {
                opts.defaultToolbar.push({
                    title: '设置',
                    layEvent: 'EZ_SORT_SETTING',
                    icon: 'layui-icon-set',
                    onClick: openDialog
                });
            }
        }
    }

    function wrap(mod) {
        if (!mod || mod.__ezSortWrapped || typeof mod.render !== 'function') return;
        var raw = mod.render;
        mod.render = function (opts) {
            try {
                if (opts && opts.id === TABLE_ID) prepare(opts);
            } catch (e) {
                console.error('ezlist-sort error', e);
            }
            return raw.apply(this, arguments);
        };
        mod.__ezSortWrapped = true;
    }

    layui.use(['table', 'treeTable', 'layer'], function () {
        wrap(layui.table);
        wrap(layui.treeTable);
    });

    // 尽早恢复搜索项顺序（DOM 就绪即做，幂等；渲染前 prepare 里还会兜底一次）
    $(function () {
        var cfg = readCfg();
        applySearchOrder(cfg.searchOrder);
        applySearchHidden(cfg.searchHidden);
    });
})();
