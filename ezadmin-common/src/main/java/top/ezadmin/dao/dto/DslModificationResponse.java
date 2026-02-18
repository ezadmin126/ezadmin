package top.ezadmin.dao.dto;

import java.util.List;

/**
 * DSL 修改响应
 */
public class DslModificationResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 修改后的 DSL 内容（JSON 字符串）
     */
    private String modifiedDsl;

    /**
     * 修改摘要信息
     */
    private ModificationSummary summary;

    public DslModificationResponse() {
    }

    public DslModificationResponse(boolean success, String message, String modifiedDsl, ModificationSummary summary) {
        this.success = success;
        this.message = message;
        this.modifiedDsl = modifiedDsl;
        this.summary = summary;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModifiedDsl() {
        return modifiedDsl;
    }

    public void setModifiedDsl(String modifiedDsl) {
        this.modifiedDsl = modifiedDsl;
    }

    public ModificationSummary getSummary() {
        return summary;
    }

    public void setSummary(ModificationSummary summary) {
        this.summary = summary;
    }

    /**
     * 修改摘要信息
     */
    public static class ModificationSummary {
        /**
         * 阶段1：AI 分析出需要修改的节点列表
         */
        private List<String> affectedNodes;

        /**
         * 阶段2：每个节点的修改详情
         */
        private List<NodeModification> nodeModifications;

        /**
         * AI 处理说明
         */
        private String explanation;

        public ModificationSummary() {
        }

        public ModificationSummary(List<String> affectedNodes, List<NodeModification> nodeModifications, String explanation) {
            this.affectedNodes = affectedNodes;
            this.nodeModifications = nodeModifications;
            this.explanation = explanation;
        }

        public List<String> getAffectedNodes() {
            return affectedNodes;
        }

        public void setAffectedNodes(List<String> affectedNodes) {
            this.affectedNodes = affectedNodes;
        }

        public List<NodeModification> getNodeModifications() {
            return nodeModifications;
        }

        public void setNodeModifications(List<NodeModification> nodeModifications) {
            this.nodeModifications = nodeModifications;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    /**
     * 节点修改信息
     */
    public static class NodeModification {
        /**
         * 节点路径，例如：search[0], column[2], cardList[0].fieldList[1]
         */
        private String nodePath;

        /**
         * 修改类型：add（新增）、update（修改）、delete（删除）
         */
        private String action;

        /**
         * 修改说明
         */
        private String description;

        public NodeModification() {
        }

        public NodeModification(String nodePath, String action, String description) {
            this.nodePath = nodePath;
            this.action = action;
            this.description = description;
        }

        public String getNodePath() {
            return nodePath;
        }

        public void setNodePath(String nodePath) {
            this.nodePath = nodePath;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 创建成功响应
     */
    public static DslModificationResponse success(String modifiedDsl, ModificationSummary summary) {
        DslModificationResponse response = new DslModificationResponse();
        response.setSuccess(true);
        response.setModifiedDsl(modifiedDsl);
        response.setSummary(summary);
        return response;
    }

    /**
     * 创建失败响应
     */
    public static DslModificationResponse failure(String message) {
        DslModificationResponse response = new DslModificationResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
