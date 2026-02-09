/**
 * EzInspector - 页面元素检查插件
 * 类似 F12 开发者模式的元素选择工具
 * @version 1.0.0
 */
(function(window, document) {
    'use strict';

    // 避免重复初始化
    if (window.EzInspector) {
        console.warn('EzInspector already initialized');
        return;
    }

    /**
     * 主类
     */
    class EzInspector {
        constructor(options = {}) {
            this.options = {
                apiEndpoint: options.apiEndpoint || '/api/inspector/submit',
                hotkey: options.hotkey || 'ctrl+shift+i',
                theme: options.theme || 'light',
                position: options.position || 'bottom-right',
                autoStart: options.autoStart || true,
                zIndex: options.zIndex || 999999
            };

            this.state = {
                active: false,
                selectedElement: null,
                hoveredElement: null,
                panelVisible: false
            };

            this.elements = {
                overlay: null,
                panel: null,
                highlighter: null
            };

            this.init();
        }

        /**
         * 初始化
         */
        init() {
            this.createHighlighter();
            this.createPanel();
            this.bindEvents();

            if (this.options.autoStart) {
                this.activate();
            }

            console.log('页面配置助手已初始化');
        }

        /**
         * 创建高亮遮罩层
         */
        createHighlighter() {
            const highlighter = document.createElement('div');
            highlighter.className = 'ez-inspector-highlighter';
            highlighter.style.cssText = `
                position: absolute;
                pointer-events: none;
                z-index: ${this.options.zIndex - 1};
                display: none;
                box-sizing: border-box;
                border: 2px solid #1890ff;
                background-color: rgba(24, 144, 255, 0.1);
                transition: all 0.1s ease;
            `;
            document.body.appendChild(highlighter);
            this.elements.highlighter = highlighter;
        }

        /**
         * 创建主面板
         */
        createPanel() {
            // 创建面板内容
            const contentHtml = `
                <div class="ez-inspector-content" style="padding: 15px;">
                    <div class="layui-form" lay-filter="ezInspectorForm">
                        <div class="layui-form-item">
                            <label class="layui-form-label">页面地址</label>
                            <div class="layui-input-block">
                                <div class="ez-inspector-url" style="
                                    padding: 8px 12px;
                                    background: #f5f5f5;
                                    border-radius: 2px;
                                    font-size: 12px;
                                    word-break: break-all;
                                    color: #333;
                                    line-height: 1.5;
                                "></div>
                            </div>
                        </div>

                        <div class="layui-form-item">
                            <label class="layui-form-label">选中组件</label>
                            <div class="layui-input-block">
                                <div class="ez-inspector-element-info" style="
                                    padding: 8px 12px;
                                    background: #f5f5f5;
                                    border-radius: 2px;
                                    font-size: 12px;
                                    color: #333;
                                    line-height: 1.5;
                                ">未选择组件</div>
                            </div>
                        </div>

                        <div class="layui-form-item">
                            <label class="layui-form-label">组件代码</label>
                            <div class="layui-input-block">
                                <div class="ez-inspector-code" style="
                                    padding: 12px;
                                    background: #1e1e1e;
                                    border-radius: 2px;
                                    overflow-x: auto;
                                     height: 150px;
                                    overflow-y: auto;
                                ">
                                    <pre style="
                                        margin: 0;
                                        font-family: 'Consolas', 'Monaco', monospace;
                                        font-size: 12px;
                                        color: #d4d4d4;
                                        line-height: 1.5;
                                    "><code>点击带有 item_name 属性的组件开始选择...</code></pre>
                                </div>
                            </div>
                        </div>

                        <div class="layui-form-item layui-form-text">
                            <label class="layui-form-label">修改需求</label>
                            <div class="layui-input-block">
                                <textarea class="layui-textarea ez-inspector-note" placeholder="请描述您希望对此组件进行的修改，AI 将根据您的需求自动调整配置..."></textarea>
                            </div>
                        </div>

                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <button type="button" class="layui-btn layui-btn-primary ez-inspector-reselect">
                                    <i class="layui-icon layui-icon-refresh"></i> 重新选择
                                </button>
                                <button type="button" class="layui-btn layui-btn-normal ez-inspector-copy">
                                    <i class="layui-icon layui-icon-file"></i> 复制代码
                                </button>
                                <button type="button" class="layui-btn layui-btn-primary ez-inspector-submit">
                                    <i class="layui-icon layui-icon-ok"></i> 提交需求
                                </button>
                            </div>
                        </div>

                        <div class="ez-inspector-status" style="display: none; margin-top: 10px;"></div>
                    </div>
                </div>
            `;

            // 将内容添加到临时容器
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = contentHtml;
            tempDiv.style.display = 'none';
            document.body.appendChild(tempDiv);

            // 保存临时容器的引用
            this.elements.panel = tempDiv;

            // 不再需要makePanelDraggable，layui的layer自带拖拽功能
        }

        /**
         * 获取面板位置样式 - 已废弃，使用layer的offset
         */
        getPositionStyle() {
            // layer使用offset控制位置
            return '';
        }

        /**
         * 绑定全局事件
         */
        bindEvents() {
            // 快捷键
            document.addEventListener('keydown', (e) => {
                if (this.checkHotkey(e)) {
                    e.preventDefault();
                    this.toggle();
                }
            });

            // 元素悬停和点击
            document.addEventListener('mouseover', this.handleMouseOver.bind(this), true);
            document.addEventListener('mouseout', this.handleMouseOut.bind(this), true);
            document.addEventListener('click', this.handleClick.bind(this), true);
        }

        /**
         * 绑定面板事件
         */
        bindPanelEvents() {
            const panel = this.elements.panel;
            if (!panel) return;

            // 重新选择
            const reselectBtn = panel.querySelector('.ez-inspector-reselect');
            if (reselectBtn) {
                reselectBtn.addEventListener('click', () => {
                    // 移除之前选中元素的高亮
                    if (this.state.selectedElement) {
                        this.state.selectedElement.style.border = '';
                    }

                    // 清除选中状态
                    this.state.selectedElement = null;

                    // 清空元素信息显示
                    panel.querySelector('.ez-inspector-element-info').textContent = '未选择组件';
                    panel.querySelector('.ez-inspector-code pre code').textContent = '点击带有 item_name 属性的组件开始选择...';

                    this.showStatus('请点击页面上的组件重新选择', 'info');
                });
            }

            // 复制代码
            const copyBtn = panel.querySelector('.ez-inspector-copy');
            if (copyBtn) {
                copyBtn.addEventListener('click', () => {
                    this.copyCode();
                });
            }

            // 提交
            const submitBtn = panel.querySelector('.ez-inspector-submit');
            if (submitBtn) {
                submitBtn.addEventListener('click', () => {
                    this.submit();
                });
            }
        }

        /**
         * 使面板可拖拽 - 已废弃，layer自带拖拽功能
         */
        makePanelDraggable() {
            // layer自带拖拽功能，无需额外实现
        }

        /**
         * 检查快捷键
         */
        checkHotkey(e) {
            const hotkey = this.options.hotkey.toLowerCase();
            const keys = hotkey.split('+');

            let requireCtrl = keys.includes('ctrl') || keys.includes('cmd');
            let requireShift = keys.includes('shift');
            let requireAlt = keys.includes('alt');
            let key = keys.find(k => !['ctrl', 'cmd', 'shift', 'alt'].includes(k));

            return (
                (!requireCtrl || e.ctrlKey || e.metaKey) &&
                (!requireShift || e.shiftKey) &&
                (!requireAlt || e.altKey) &&
                e.key.toLowerCase() === key
            );
        }

        /**
         * 鼠标悬停处理
         */
        handleMouseOver(e) {
            if (!this.state.active) return;
            if (this.isInspectorElement(e.target)) return;

            // 检查元素是否有 item_name 属性
            const targetElement = this.findElementWithItemName(e.target);
            if (!targetElement) return;

            this.state.hoveredElement = targetElement;
            this.highlightElement(targetElement);
        }

        /**
         * 鼠标移出处理
         */
        handleMouseOut(e) {
            if (!this.state.active) return;
            if (this.isInspectorElement(e.target)) return;

            this.state.hoveredElement = null;
            this.hideHighlight();
        }

        /**
         * 点击处理
         */
        handleClick(e) {
            if (!this.state.active) return;
            if (this.isInspectorElement(e.target)) return;

            // 检查元素是否有 item_name 属性
            const targetElement = this.findElementWithItemName(e.target);
            if (!targetElement) return;

            e.preventDefault();
            e.stopPropagation();

            this.selectElement(targetElement);
        }

        /**
         * 判断是否是检查器自身的元素
         */
        isInspectorElement(element) {
            return element.closest('.ez-inspector-panel') ||
                element.classList.contains('ez-inspector-highlighter');
        }

        /**
         * 查找带有 item_name 属性的元素（向上查找）
         */
        findElementWithItemName(element) {
            let current = element;
            while (current && current !== document.body) {
                if (current.hasAttribute && current.hasAttribute('item_name')) {
                    return current;
                }
                current = current.parentElement;
            }
            return null;
        }

        /**
         * 高亮元素
         */
        highlightElement(element) {
            const rect = element.getBoundingClientRect();
            const highlighter = this.elements.highlighter;

            highlighter.style.display = 'block';
            highlighter.style.left = (rect.left + window.scrollX) + 'px';
            highlighter.style.top = (rect.top + window.scrollY) + 'px';
            highlighter.style.width = rect.width + 'px';
            highlighter.style.height = rect.height + 'px';
        }

        /**
         * 隐藏高亮
         */
        hideHighlight() {
            this.elements.highlighter.style.display = 'none';
        }

        /**
         * 选择元素
         */
        selectElement(element) {
            // 移除之前选中元素的边框
            if (this.state.selectedElement) {
                this.state.selectedElement.style.border = '';
            }

            this.state.selectedElement = element;
            this.hideHighlight();

            // 添加简单的边框表示选中状态
            if (element) {
                element.style.border = '2px solid #52c41a';
            }

            this.showPanel();
            this.updatePanel();
        }

        /**
         * 更新面板内容
         */
        updatePanel() {
            const element = this.state.selectedElement;
            if (!element) return;

            // 更新 URL
            const url = window.location.href;
            // 更新元素信息
            const elementInfo = this.getElementInfo(element);
            // 更新 HTML 代码
            const code = this.formatHTML(element.outerHTML);

            // 如果layer已打开，更新layer中的内容
            if (this.state.currentLayero) {
                const $layero = layui.jquery(this.state.currentLayero);
                $layero.find('.ez-inspector-url').text(url);
                $layero.find('.ez-inspector-element-info').text(elementInfo);
                $layero.find('.ez-inspector-code pre code').text(code);
            } else {
                // 如果layer未打开，更新临时容器中的内容
                const panel = this.elements.panel;
                if (panel) {
                    const urlEl = panel.querySelector('.ez-inspector-url');
                    const infoEl = panel.querySelector('.ez-inspector-element-info');
                    const codeEl = panel.querySelector('.ez-inspector-code pre code');

                    if (urlEl) urlEl.textContent = url;
                    if (infoEl) infoEl.textContent = elementInfo;
                    if (codeEl) codeEl.textContent = code;
                }
            }
        }

        /**
         * 获取元素信息
         */
        getElementInfo(element) {
            const tagName = element.tagName.toLowerCase();
            const id = element.id ? `#${element.id}` : '';
            const classes = element.className ? `.${element.className.split(' ').join('.')}` : '';
            const itemName = element.getAttribute('item_name');
            const itemNameStr = itemName ? ` [item_name="${itemName}"]` : '';
            return `<${tagName}${id}${classes}>${itemNameStr}`;
        }

        /**
         * 格式化 HTML 代码
         */
        formatHTML(html) {
            // 简单的格式化，添加换行和缩进
            let formatted = html;
            let indent = 0;
            let result = '';

            // 限制长度，避免过大的 HTML
            if (formatted.length > 5000) {
                formatted = formatted.substring(0, 5000) + '\n... (代码过长，已截断)';
            }

            return formatted;
        }

        /**
         * 显示面板
         */
        showPanel() {
            // 检查是否已有打开的弹窗
            if (this.state.layerIndex) {
                return;
            }

            const self = this;

            // 检查layui是否可用
            if (typeof layui === 'undefined' || typeof layer === 'undefined') {
                console.error('layui或layer未加载，请确保页面已引入layui');
                alert('layui未加载，无法打开面板');
                return;
            }

            // 使用layui的layer打开弹窗
            this.state.layerIndex = layer.open({
                type: 1,
                title: '页面配置修改助手',
                area: ['600px', '600px'],
                shade:0,
                maxmin: true,
                moveOut: true,
                offset:'rb',
                closeBtn:0,
                content: this.elements.panel.innerHTML,
                success: function(layero, index) {
                    self.state.panelVisible = true;
                    self.state.currentLayero = layero;

                    // 绑定事件到新创建的layer内容
                    self.bindLayerEvents(layero);
                },
                cancel: function(index) {
                    self.state.panelVisible = false;
                    self.state.layerIndex = null;
                    self.state.currentLayero = null;
                    return true;
                },
                end: function() {
                    self.state.panelVisible = false;
                    self.state.layerIndex = null;
                    self.state.currentLayero = null;
                }
            });
        }

        /**
         * 绑定layer内容的事件
         */
        bindLayerEvents(layero) {
            const $layero = layui.jquery(layero);

            // 重新选择
            $layero.find('.ez-inspector-reselect').on('click', () => {
                // 移除之前选中元素的边框
                if (this.state.selectedElement) {
                    this.state.selectedElement.style.border = '';
                }

                // 清除选中状态
                this.state.selectedElement = null;

                // 清空元素信息显示
                $layero.find('.ez-inspector-element-info').text('未选择组件');
                $layero.find('.ez-inspector-code pre code').text('点击带有 item_name 属性的组件开始选择...');

                this.showLayerStatus(layero, '请点击页面上的组件重新选择', 'info');
            });

            // 复制代码
            $layero.find('.ez-inspector-copy').on('click', () => {
                this.copyCodeFromLayer(layero);
            });

            // 提交
            $layero.find('.ez-inspector-submit').on('click', () => {
                this.submitFromLayer(layero);
            });
        }

        /**
         * 在layer中显示状态消息
         */
        showLayerStatus(layero, message, type = 'info') {
            const $layero = layui.jquery(layero);
            const statusEl = $layero.find('.ez-inspector-status');
            const colors = {
                info: { bg: '#e6f7ff', color: '#1890ff', border: '#91d5ff' },
                success: { bg: '#f6ffed', color: '#52c41a', border: '#b7eb8f' },
                error: { bg: '#fff2f0', color: '#ff4d4f', border: '#ffccc7' },
                warning: { bg: '#fffbe6', color: '#faad14', border: '#ffe58f' }
            };
            const style = colors[type] || colors.info;

            statusEl.css({
                display: 'block',
                background: style.bg,
                color: style.color,
                border: `1px solid ${style.border}`,
                padding: '8px 12px',
                borderRadius: '2px',
                fontSize: '12px',
                marginTop: '10px'
            });
            statusEl.text(message);

            setTimeout(() => {
                statusEl.fadeOut();
            }, 3000);
        }

        /**
         * 隐藏面板
         */
        hidePanel() {
            if (this.state.layerIndex) {
                layer.close(this.state.layerIndex);
                this.state.layerIndex = null;
                this.state.currentLayero = null;
            }
            this.state.panelVisible = false;
        }

        /**
         * 显示状态消息
         */
        showStatus(message, type = 'info') {
            const statusEl = this.elements.panel.querySelector('.ez-inspector-status');
            const colors = {
                info: '#1890ff',
                success: '#52c41a',
                error: '#ff4d4f',
                warning: '#faad14'
            };

            statusEl.style.display = 'block';
            statusEl.style.background = colors[type] + '22';
            statusEl.style.color = colors[type];
            statusEl.textContent = message;

            setTimeout(() => {
                statusEl.style.display = 'none';
            }, 3000);
        }

        /**
         * 复制代码
         */
        copyCode() {
            if (!this.state.selectedElement) {
                if (this.state.currentLayero) {
                    this.showLayerStatus(this.state.currentLayero, '请先选择一个组件', 'warning');
                } else {
                    this.showStatus('请先选择一个组件', 'warning');
                }
                return;
            }

            const code = this.state.selectedElement.outerHTML;

            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(code).then(() => {
                    if (this.state.currentLayero) {
                        this.showLayerStatus(this.state.currentLayero, '代码已复制到剪贴板', 'success');
                    } else {
                        this.showStatus('代码已复制到剪贴板', 'success');
                    }
                }).catch(() => {
                    this.fallbackCopy(code);
                });
            } else {
                this.fallbackCopy(code);
            }
        }

        /**
         * 从layer中复制代码
         */
        copyCodeFromLayer(layero) {
            if (!this.state.selectedElement) {
                this.showLayerStatus(layero, '请先选择一个组件', 'warning');
                return;
            }

            const code = this.state.selectedElement.outerHTML;

            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(code).then(() => {
                    this.showLayerStatus(layero, '代码已复制到剪贴板', 'success');
                }).catch(() => {
                    this.fallbackCopyWithLayer(code, layero);
                });
            } else {
                this.fallbackCopyWithLayer(code, layero);
            }
        }

        /**
         * 降级复制方法
         */
        fallbackCopy(text) {
            const textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.style.position = 'fixed';
            textarea.style.left = '-9999px';
            document.body.appendChild(textarea);
            textarea.select();

            try {
                document.execCommand('copy');
                if (this.state.currentLayero) {
                    this.showLayerStatus(this.state.currentLayero, '代码已复制到剪贴板', 'success');
                } else {
                    this.showStatus('代码已复制到剪贴板', 'success');
                }
            } catch (err) {
                if (this.state.currentLayero) {
                    this.showLayerStatus(this.state.currentLayero, '复制失败，请手动复制', 'error');
                } else {
                    this.showStatus('复制失败，请手动复制', 'error');
                }
            }

            document.body.removeChild(textarea);
        }

        /**
         * 降级复制方法(带layer参数)
         */
        fallbackCopyWithLayer(text, layero) {
            const textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.style.position = 'fixed';
            textarea.style.left = '-9999px';
            document.body.appendChild(textarea);
            textarea.select();

            try {
                document.execCommand('copy');
                this.showLayerStatus(layero, '代码已复制到剪贴板', 'success');
            } catch (err) {
                this.showLayerStatus(layero, '复制失败，请手动复制', 'error');
            }

            document.body.removeChild(textarea);
        }

        /**
         * 提交数据
         */
        submit() {
            let note = '';
            if (this.state.currentLayero) {
                note = layui.jquery(this.state.currentLayero).find('.ez-inspector-note').val().trim();
            } else {
                const noteEl = this.elements.panel.querySelector('.ez-inspector-note');
                note = noteEl ? noteEl.value.trim() : '';
            }

            // 允许没有选中元素时提交（只提交用户输入）
            if (!this.state.selectedElement && !note) {
                if (this.state.currentLayero) {
                    this.showLayerStatus(this.state.currentLayero, '请描述您的修改需求或选择一个组件', 'warning');
                } else {
                    this.showStatus('请描述您的修改需求或选择一个组件', 'warning');
                }
                return;
            }

            const data = this.collectData(note);

            console.log('准备提交数据:', data);

            // 触发自定义事件，供外部监听
            const event = new CustomEvent('ezinspector:submit', {
                detail: data
            });
            window.dispatchEvent(event);

            if (this.state.currentLayero) {
                this.showLayerStatus(this.state.currentLayero, '修改需求已提交，AI 正在处理中...', 'success');
            } else {
                this.showStatus('修改需求已提交，AI 正在处理中...', 'success');
            }

            // 如果配置了 API 端点，这里可以实现实际提交
            // this.sendToBackend(data);
        }

        /**
         * 从layer提交数据
         */
        submitFromLayer(layero) {
            const note = layui.jquery(layero).find('.ez-inspector-note').val().trim();

            // 允许没有选中元素时提交（只提交用户输入）
            if (!this.state.selectedElement && !note) {
                this.showLayerStatus(layero, '请描述您的修改需求或选择一个组件', 'warning');
                return;
            }

            const data = this.collectData(note);

            console.log('准备提交数据:', data);

            // 触发自定义事件，供外部监听
            const event = new CustomEvent('ezinspector:submit', {
                detail: data
            });
            window.dispatchEvent(event);

            this.showLayerStatus(layero, '修改需求已提交，AI 正在处理中...', 'success');

            // 如果配置了 API 端点，这里可以实现实际提交
            // this.sendToBackend(data);
        }

        /**
         * 收集数据
         */
        collectData(note) {
            const element = this.state.selectedElement;

            const data = {
                url: window.location.href,
                timestamp: new Date().toISOString(),
                userNote: note,
                pageInfo: {
                    title: document.title,
                    viewport: `${window.innerWidth}x${window.innerHeight}`,
                    userAgent: navigator.userAgent
                }
            };

            // 如果有选中的元素，添加元素信息
            if (element) {
                data.element = {
                    html: element.outerHTML,
                    tagName: element.tagName,
                    className: element.className,
                    id: element.id,
                    itemName: element.getAttribute('item_name') || '',
                    xpath: this.getXPath(element),
                    cssSelector: this.getCSSSelector(element),
                    textContent: element.textContent.substring(0, 200) // 限制长度
                };
            } else {
                data.element = null;
            }

            return data;
        }

        /**
         * 获取元素的 XPath
         */
        getXPath(element) {
            if (element.id) {
                return `//*[@id="${element.id}"]`;
            }

            const parts = [];
            while (element && element.nodeType === Node.ELEMENT_NODE) {
                let index = 0;
                let sibling = element.previousSibling;

                while (sibling) {
                    if (sibling.nodeType === Node.ELEMENT_NODE &&
                        sibling.nodeName === element.nodeName) {
                        index++;
                    }
                    sibling = sibling.previousSibling;
                }

                const tagName = element.nodeName.toLowerCase();
                const pathIndex = index ? `[${index + 1}]` : '';
                parts.unshift(tagName + pathIndex);

                element = element.parentNode;
            }

            return parts.length ? '/' + parts.join('/') : '';
        }

        /**
         * 获取元素的 CSS Selector
         */
        getCSSSelector(element) {
            if (element.id) {
                return `#${element.id}`;
            }

            const path = [];
            while (element && element.nodeType === Node.ELEMENT_NODE) {
                let selector = element.nodeName.toLowerCase();

                if (element.className) {
                    selector += '.' + element.className.trim().split(/\s+/).join('.');
                }

                path.unshift(selector);

                if (element.parentNode) {
                    element = element.parentNode;
                } else {
                    break;
                }
            }

            return path.join(' > ');
        }

        /**
         * 激活检查器
         */
        activate() {
            if (this.state.active) return;

            this.state.active = true;
            document.body.style.cursor = 'crosshair';

            // 激活时显示面板
            this.showPanel();

            // 更新 URL
            if (this.state.currentLayero) {
                layui.jquery(this.state.currentLayero).find('.ez-inspector-url').text(window.location.href);
                this.showLayerStatus(this.state.currentLayero, '配置助手已启动，请选择要修改的组件', 'info');
            } else {
                const panel = this.elements.panel;
                if (panel) {
                    const urlEl = panel.querySelector('.ez-inspector-url');
                    if (urlEl) urlEl.textContent = window.location.href;
                }
            }

            console.log('页面配置助手已激活');
        }

        /**
         * 停用检查器
         */
        deactivate() {
            this.state.active = false;

            // 移除选中元素的边框样式
            if (this.state.selectedElement) {
                this.state.selectedElement.style.border = '';
            }

            this.state.selectedElement = null;
            this.state.hoveredElement = null;

            document.body.style.cursor = '';
            this.hideHighlight();

            console.log('页面配置助手已停用');
        }

        /**
         * 切换激活状态
         */
        toggle() {
            if (this.state.active) {
                this.deactivate();
            } else {
                this.activate();
            }
        }

        /**
         * 销毁实例
         */
        destroy() {
            // 移除选中元素的边框样式
            if (this.state.selectedElement) {
                this.state.selectedElement.style.border = '';
            }

            this.deactivate();

            if (this.elements.highlighter) {
                this.elements.highlighter.remove();
            }
            if (this.elements.panel) {
                this.elements.panel.remove();
            }

            // 关闭layer弹窗
            if (this.state.layerIndex) {
                layer.close(this.state.layerIndex);
            }

            console.log('页面配置助手已销毁');
        }
    }

    // 静态方法：初始化
    const API = {
        instance: null,
        options: null,

        init(options) {
            if (this.instance) {
                console.warn('EzInspector already exists, destroying old instance');
                this.instance.destroy();
            }
            this.options = options || {};
            this.instance = new EzInspector(this.options);
            return this.instance;
        },

        getInstance() {
            return this.instance;
        },

        activate() {
            if (!this.instance) {
                this.init();
            }
            if (this.instance) {
                this.instance.activate();
            }
        },

        deactivate() {
            if (this.instance) {
                this.instance.deactivate();
            }
        },

        toggle() {
            if (!this.instance) {
                this.init();
            }
            if (this.instance) {
                this.instance.toggle();
            }
        },

        destroy() {
            if (this.instance) {
                this.instance.destroy();
                this.instance = null;
            }
        }
    };

    // 暴露到全局
    window.EzInspector = API;

    // 自动初始化并激活
    function autoInit() {
        if (typeof layui !== 'undefined' && typeof layer !== 'undefined') {
            // 如果layui已加载，直接激活
            console.log('页面配置助手：正在自动初始化...');
            API.init({ autoStart: true });
        } else {
            // 等待layui加载
            const checkLayui = setInterval(() => {
                if (typeof layui !== 'undefined' && typeof layer !== 'undefined') {
                    clearInterval(checkLayui);
                    console.log('页面配置助手：正在自动初始化...');
                    API.init({ autoStart: true });
                }
            }, 100);

            // 5秒后超时
            setTimeout(() => {
                clearInterval(checkLayui);
                if (!API.instance) {
                    console.error('页面配置助手：Layui 未加载，无法启动');
                }
            }, 5000);
        }
    }

    // 等待 DOM 加载完成后再初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', autoInit);
    } else {
        // DOM 已经加载完成
        autoInit();
    }

})(window, document);
