(function (window, document, $) {
    'use strict';

    if (window.EZ_INPUT_POP_SELECT && window.EZ_INPUT_POP_SELECT.__loaded) {
        return;
    }

    var api = window.EZ_INPUT_POP_SELECT || {};
    api.instances = api.instances || {};
    api.__loaded = true;

    function parseJson(value) {
        try {
            return typeof value === 'string' ? JSON.parse(value || '{}') : (value || {});
        } catch (e) {
            return {};
        }
    }

    function getContextUrl(rawUrl) {
        if (!rawUrl) {
            return '';
        }
        if (/^(https?:)?\/\//.test(rawUrl)) {
            return rawUrl;
        }
        var contextNameElement = document.getElementById('contextName');
        var contextName = contextNameElement ? contextNameElement.value : '';
        if (contextName && rawUrl.indexOf(contextName + '/') !== 0) {
            return contextName + rawUrl;
        }
        return rawUrl;
    }

    function joinUrl(rawUrl, params) {
        var query = [];
        for (var key in params) {
            if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null && params[key] !== '') {
                query.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
            }
        }
        if (query.length === 0) {
            return rawUrl;
        }
        return rawUrl + (rawUrl.indexOf('?') >= 0 ? '&' : '?') + query.join('&');
    }

    function normalizeResponse(res) {
        var data = res || {};
        var responseHead = data.head || data.fields || [];
        var responseBody = Array.isArray(data.data) ? data.data : [];
        var payload = data.data || data;
        if (payload && payload.data && (payload.data.head || payload.data.body)) {
            payload = payload.data;
        }
        if (payload && payload.head) {
            responseHead = payload.head;
        } else if (payload && payload.fields) {
            responseHead = payload.fields;
        }
        if (payload && payload.body) {
            responseBody = payload.body;
        } else if (payload && payload.list) {
            responseBody = payload.list;
        } else if (payload && payload.data && Array.isArray(payload.data)) {
            responseBody = payload.data;
        }
        return {
            success: data.success !== false,
            head: responseHead,
            body: responseBody,
            count: data.count || (data.data && data.data.count) || (payload && payload.count) || 0,
            message: data.message || data.msg || ''
        };
    }

    function escapeHtml(value) {
        if (value === undefined || value === null) {
            return '';
        }
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function createInstance(itemName) {
        var formItem = $('.layui-form-item[component="input-pop-select"][item_name="' + itemName + '"]');
        if (formItem.length === 0) {
            return null;
        }
        var valueInput = document.getElementById('input-pop-select-value-' + itemName);
        if (!valueInput) {
            return null;
        }

        var props = parseJson($(valueInput).attr('data-propsJson'));
        var label = $('.ez-input-pop-select-btn[data-target="' + itemName + '"]').attr('data-label') || '';
        var idField = props.idField || 'ID';
        var multiple = props.multiple !== false && props.multiple !== 'false';
        var title = props.title || ('请选择' + (label || ''));
        var area = props.area || [props.width || '90%', props.height || '90%'];
        var pageUrl = props.url || props.pageUrl || '';
        var selectedRows = {};
        var lastValue = null;

        function getValueInput() {
            return document.getElementById('input-pop-select-value-' + itemName);
        }

        function getSelectedIds() {
            var input = getValueInput();
            if (!input || !input.value) {
                return [];
            }
            return input.value.split(',').filter(function (id) {
                return id !== '';
            });
        }

        function syncInputValue(options) {
            options = options || {};
            var ids = [];
            for (var id in selectedRows) {
                if (selectedRows.hasOwnProperty(id)) {
                    ids.push(id);
                }
            }
            var input = getValueInput();
            if (input) {
                input.value = ids.join(',');
                if (!options.deferRender) {
                    lastValue = input.value;
                }
                if (options.silent) {
                    return;
                }
                try {
                    $(input).trigger('change');
                } catch (e) {
                }
            }
        }

        function getDisplayColumns(head, body) {
            var configuredFields = (props.displayFields || props.showFields || props.echoFields || '').split(',').map(function (field) {
                return field.trim();
            }).filter(function (field) {
                return field !== '';
            });
            var labelMap = {};
            if (head && head.length > 0) {
                head.forEach(function (column) {
                    var field = column.item_name || column.field || column.name;
                    if (field) {
                        labelMap[field] = column.label || column.title || field;
                    }
                });
            }
            if (configuredFields.length > 0) {
                return configuredFields.map(function (field) {
                    return {
                        field: field,
                        label: labelMap[field] || field
                    };
                });
            }
            var columns = [];
            if (head && head.length > 0) {
                head.forEach(function (column) {
                    var field = column.item_name || column.field || column.name;
                    if (!field || field === idField || field.indexOf('LAY_') === 0) {
                        return;
                    }
                    columns.push({
                        field: field,
                        label: column.label || column.title || field
                    });
                });
            }
            if (columns.length === 0 && body && body.length > 0) {
                Object.keys(body[0]).forEach(function (field) {
                    if (field !== idField && field.indexOf('LAY_') !== 0) {
                        columns.push({
                            field: field,
                            label: field
                        });
                    }
                });
            }
            return columns;
        }

        function renderSelectedTable(head, body) {
            var wrap = document.getElementById('input-pop-select-wrap-' + itemName);
            var table = document.getElementById('input-pop-select-table-' + itemName);
            var clearButton = $('.ez-input-pop-select-clear[data-target="' + itemName + '"]');
            if (!wrap || !table) {
                return;
            }
            var rows = body || [];
            var columns = getDisplayColumns(head, rows);
            if (rows.length === 0) {
                wrap.style.display = 'none';
                clearButton.hide();
                if (window.layui && layui.table && layui.table.reload) {
                    try {
                        layui.table.reload('input-pop-select-table-' + itemName, {data: []});
                    } catch (e) {
                        table.innerHTML = '';
                    }
                } else {
                    table.innerHTML = '';
                }
                return;
            }
            wrap.style.display = '';
            clearButton.show();
            var tableCols = columns.map(function (column) {
                return {
                    field: column.field,
                    title: column.label,
                    minWidth: 120
                };
            });
            tableCols.push({
                title: '操作',
                width: 70,
                fixed: 'right',
                templet: function (row) {
                    var rowId = row[idField];
                    return '<i class="layui-icon layui-icon-delete ez-input-pop-select-delete" data-id="' + escapeHtml(rowId) + '" style="cursor: pointer; color: #ff5722;" title="删除"></i>';
                }
            });
            layui.table.render({
                elem: '#input-pop-select-table-' + itemName,
                id: 'input-pop-select-table-' + itemName,
                data: rows,
                cols: [tableCols],
                page: false,
                limit: rows.length,
                skin: 'line',
                even: true,
                cellMinWidth: 120,
                text: {none: '暂无已选数据'}
            });
        }

        function refreshSelectedTable(head) {
            var rows = [];
            for (var id in selectedRows) {
                if (selectedRows.hasOwnProperty(id)) {
                    rows.push(selectedRows[id]);
                }
            }
            renderSelectedTable(head || props.head || [], rows);
        }

        function buildInitDataUrl() {
            var rawUrl = props.initUrl || props.dataUrl || pageUrl;
            if (!rawUrl) {
                return '';
            }
            var dataUrl = rawUrl.replace('list/page-', 'list/data-');
            var questionIndex = dataUrl.indexOf('?');
            var path = questionIndex >= 0 ? dataUrl.substring(0, questionIndex) : dataUrl;
            var query = questionIndex >= 0 ? dataUrl.substring(questionIndex) : '';
            return path + query;
        }

        function loadRowsByIds(ids, callback) {
            var initUrl = buildInitDataUrl();
            if (!initUrl || !ids || ids.length === 0) {
                selectedRows = {};
                renderSelectedTable([], []);
                if (typeof callback === 'function') {
                    callback();
                }
                return;
            }
            var params = {};
            params[idField] = ids.join(',');
            $.get(getContextUrl(joinUrl(initUrl, params)), function (res) {
                var normalized = normalizeResponse(res);
                if (!normalized.success) {
                    layui.layer.msg(normalized.message || '初始化选择数据失败', {icon: 2});
                    return;
                }
                selectedRows = {};
                (normalized.body || []).forEach(function (row) {
                    var rowId = row[idField];
                    if (rowId !== undefined && rowId !== null) {
                        selectedRows[String(rowId)] = row;
                    }
                });
                refreshSelectedTable(normalized.head);
                if (typeof callback === 'function') {
                    callback();
                }
            });
        }

        function initFromInputValue() {
            var input = getValueInput();
            if (!input || input.value === lastValue) {
                return;
            }
            lastValue = input.value;
            var ids = getSelectedIds();
            if (ids.length === 0) {
                selectedRows = {};
                renderSelectedTable([], []);
                return;
            }
            loadRowsByIds(ids);
        }

        function ensureSelectedRowsLoaded(callback) {
            var ids = getSelectedIds();
            var missing = false;
            ids.forEach(function (id) {
                if (!selectedRows[String(id)]) {
                    missing = true;
                }
            });
            if (ids.length > 0 && missing) {
                loadRowsByIds(ids, callback);
                return;
            }
            callback();
        }

        function createCacheKey() {
            return 'input_pop_select:' + window.location.pathname + ':' + itemName + ':' + new Date().getTime();
        }

        function saveStorage(cacheKey, rows) {
            localStorage.setItem(cacheKey, JSON.stringify(rows || {}));
        }

        function getStorageRows(cacheKey) {
            try {
                return JSON.parse(localStorage.getItem(cacheKey) || '{}') || {};
            } catch (e) {
                return {};
            }
        }

        function removeStorage(cacheKey) {
            if (cacheKey) {
                localStorage.removeItem(cacheKey);
            }
        }

        function clearSelection() {
            selectedRows = {};
            syncInputValue();
            refreshSelectedTable();
        }

        function openSelectLayer() {
            if (!pageUrl) {
                layui.layer.msg('请配置选择列表页面 url', {icon: 2});
                return;
            }
            var cacheKey = createCacheKey();
            saveStorage(cacheKey, selectedRows);
            window.EZ_POP_SELECT = {
                cacheKey: cacheKey,
                idField: idField,
                multiple: multiple
            };
            layui.layer.open({
                type: 2,
                title: title,
                area: area,
                content: getContextUrl(pageUrl),
                btn: ['确定', '取消'],
                yes: function (index) {
                    selectedRows = getStorageRows(cacheKey);
                    syncInputValue({deferRender: true, silent: true});
                    api.initFromInputValue(itemName);
                    removeStorage(cacheKey);
                    layui.layer.close(index);
                },
                cancel: function () {
                    removeStorage(cacheKey);
                },
                end: function () {
                    removeStorage(cacheKey);
                }
            });
        }

        function bindEvents() {
            var buttonSelector = '.ez-input-pop-select-btn[data-target="' + itemName + '"]';
            $(document).off('click.inputPopSelect.' + itemName, buttonSelector)
                .on('click.inputPopSelect.' + itemName, buttonSelector, function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    initFromInputValue();
                    ensureSelectedRowsLoaded(openSelectLayer);
                });

            var clearSelector = '.ez-input-pop-select-clear[data-target="' + itemName + '"]';
            $(document).off('click.inputPopSelectClear.' + itemName, clearSelector)
                .on('click.inputPopSelectClear.' + itemName, clearSelector, function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    clearSelection();
                });

            $('#input-pop-select-wrap-' + itemName).off('click.inputPopSelectDelete')
                .on('click.inputPopSelectDelete', '.ez-input-pop-select-delete', function () {
                    var id = String($(this).attr('data-id'));
                    delete selectedRows[id];
                    syncInputValue();
                    refreshSelectedTable();
                });

            $('#input-pop-select-value-' + itemName)
                .off('change.inputPopSelectValue.' + itemName)
                .on('change.inputPopSelectValue.' + itemName, function () {
                    initFromInputValue();
                });
        }

        bindEvents();

        return {
            itemName: itemName,
            initFromInputValue: initFromInputValue,
            clearSelection: clearSelection,
            setValue: function (value) {
                var input = getValueInput();
                if (!input) {
                    return;
                }
                input.value = value === undefined || value === null ? '' : String(value);
                initFromInputValue();
            }
        };
    }

    api.register = function (name, instance) {
        this.instances[name] = instance;
    };

    api.get = function (name) {
        return this.instances[name];
    };

    api.init = function (name) {
        var names = [];
        if (name) {
            names.push(name);
        } else {
            $('.layui-form-item[component="input-pop-select"]').each(function () {
                var itemName = $(this).attr('item_name');
                if (itemName) {
                    names.push(itemName);
                }
            });
        }
        for (var i = 0; i < names.length; i++) {
            var instance = this.get(names[i]) || createInstance(names[i]);
            if (instance) {
                this.register(names[i], instance);
                instance.initFromInputValue();
            }
        }
    };

    api.initFromInputValue = function (name) {
        var instance = this.get(name) || createInstance(name);
        if (instance) {
            this.register(name, instance);
            instance.initFromInputValue();
        }
    };

    api.initFormValue = function (data) {
        data = data || {};
        this.init();
        for (var name in this.instances) {
            if (!this.instances.hasOwnProperty(name)) {
                continue;
            }
            if (data.hasOwnProperty(name)) {
                this.setValue(name, data[name]);
            } else {
                this.instances[name].initFromInputValue();
            }
        }
    };

    api.clear = function (name) {
        var instance = this.get(name);
        if (instance && instance.clearSelection) {
            instance.clearSelection();
        }
    };

    api.setValue = function (name, value) {
        var instance = this.get(name) || createInstance(name);
        if (instance) {
            this.register(name, instance);
            instance.setValue(value);
        }
    };

    window.EZ_INPUT_POP_SELECT = api;
})(window, document, window.jQuery);
