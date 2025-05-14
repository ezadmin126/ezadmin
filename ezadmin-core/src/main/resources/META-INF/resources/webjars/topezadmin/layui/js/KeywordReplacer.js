/**
 * 基于AC自动机算法的关键词替换工具类
 * 用于高效地识别文本中的关键词并进行替换
 */
class KeywordReplacer {
    /**
     * 构造函数
     * @param {Array} keywords 关键词数组，用于初始化替换器
     */
    constructor(keywords) {
        this.root = this._createTrieNode();
        this.keywords = [];
        
        if (keywords && keywords.length > 0) {
            this.addKeywords(keywords);
            this._buildFailureLinks();
        }
    }
    
    /**
     * 创建Trie树节点
     * @returns {Object} 新的Trie节点
     * @private
     */
    _createTrieNode() {
        return {
            children: {},
            isEnd: false,
            fail: null,
            word: null
        };
    }
    
    /**
     * 添加关键词到Trie树
     * @param {String|Array} keywords 单个关键词或关键词数组
     */
    addKeywords(keywords) {
        if (!Array.isArray(keywords)) {
            keywords = [keywords];
        }
        
        // 添加到关键词列表
        for (const keyword of keywords) {
            if (typeof keyword === 'string' && keyword.length > 0 && !this.keywords.includes(keyword)) {
                this.keywords.push(keyword);
            }
        }
        
        // 重建Trie树
        this.root = this._createTrieNode();
        
        // 将所有关键词添加到Trie树
        for (const keyword of this.keywords) {
            let node = this.root;
            
            for (const char of keyword) {
                if (!node.children[char]) {
                    node.children[char] = this._createTrieNode();
                }
                node = node.children[char];
            }
            
            node.isEnd = true;
            node.word = keyword;
        }
        
        // 构建失败指针
        this._buildFailureLinks();
    }
    
    /**
     * 构建AC自动机的失败指针
     * @private
     */
    _buildFailureLinks() {
        const queue = [];
        
        // 第一层节点的失败指针指向根节点
        for (const char in this.root.children) {
            const node = this.root.children[char];
            node.fail = this.root;
            queue.push(node);
        }
        
        // BFS构建其他节点的失败指针
        while (queue.length > 0) {
            const current = queue.shift();
            
            for (const char in current.children) {
                const child = current.children[char];
                let failNode = current.fail;
                
                // 查找失败指针
                while (failNode !== null && !failNode.children[char]) {
                    failNode = failNode.fail;
                }
                
                if (failNode === null) {
                    child.fail = this.root;
                } else {
                    child.fail = failNode.children[char];
                }
                
                queue.push(child);
            }
        }
    }
    
    /**
     * 移除关键词
     * @param {String} keyword 要移除的关键词
     */
    removeKeyword(keyword) {
        const index = this.keywords.indexOf(keyword);
        if (index > -1) {
            this.keywords.splice(index, 1);
            // 重建Trie树和失败指针
            this.addKeywords([]);
        }
    }
    
    /**
     * 清空所有关键词
     */
    clearKeywords() {
        this.keywords = [];
        this.root = this._createTrieNode();
    }
    
    /**
     * 获取当前所有关键词
     * @returns {Array} 关键词数组
     */
    getKeywords() {
        return this.keywords.slice();
    }
    
    /**
     * 使用AC自动机查找所有匹配
     * @param {String} text 要搜索的文本
     * @returns {Array} 匹配结果数组，每项包含 {keyword, start, end}
     * @private
     */
    _findMatches(text) {
        const matches = [];
        let currentNode = this.root;
        
        for (let i = 0; i < text.length; i++) {
            const char = text.charAt(i);
            
            // 查找匹配的节点
            while (currentNode !== this.root && !currentNode.children[char]) {
                currentNode = currentNode.fail;
            }
            
            if (currentNode.children[char]) {
                currentNode = currentNode.children[char];
            } else {
                continue;
            }
            
            // 检查是否找到完整的关键词
            let checkNode = currentNode;
            while (checkNode !== this.root) {
                if (checkNode.isEnd) {
                    const keyword = checkNode.word;
                    matches.push({
                        keyword: keyword,
                        start: i - keyword.length + 1,
                        end: i + 1
                    });
                }
                checkNode = checkNode.fail;
            }
        }
        
        return matches;
    }
    
    /**
     * 替换文本中的关键词，在关键词前后添加空格
     * @param {String} text 要处理的文本
     * @returns {String} 替换后的文本
     */
    replaceKeywords(text) {
        if (!text || this.keywords.length === 0) {
            return text;
        }
        
        const matches = this._findMatches(text);
        
        // 如果没有匹配，直接返回原文本
        if (matches.length === 0) {
            return text;
        }
        
        // 按照起始位置排序匹配结果（从后往前，避免位置变化）
        matches.sort((a, b) => b.start - a.start);
        
        // 创建文本副本
        let result = text;
        
        // 从后向前替换，避免位置变化
        for (const match of matches) {
            const beforeText = result.substring(0, match.start);
            const afterText = result.substring(match.end);
            
            // 检查关键词前后是否已经有空格
            const needSpaceBefore = match.start > 0 && result.charAt(match.start - 1) !== ' ';
            const needSpaceAfter = match.end < result.length && result.charAt(match.end) !== ' ';
            
            // 构建替换后的文本
            result = beforeText + 
                     (needSpaceBefore ? ' ' : '') + 
                     match.keyword + 
                     (needSpaceAfter ? ' ' : '') + 
                     afterText;
        }
        
        return result;
    }
    
    /**
     * 获取文本中包含的所有关键词
     * @param {String} text 要分析的文本
     * @returns {Array} 文本中包含的关键词数组
     */
    extractKeywords(text) {
        if (!text || this.keywords.length === 0) {
            return [];
        }
        
        const matches = this._findMatches(text);
        const uniqueKeywords = new Set();
        
        for (const match of matches) {
            uniqueKeywords.add(match.keyword);
        }
        
        return Array.from(uniqueKeywords);
    }
}

// 导出关键词替换工具类
if (typeof module !== 'undefined' && module.exports) {
    module.exports = KeywordReplacer;
} 