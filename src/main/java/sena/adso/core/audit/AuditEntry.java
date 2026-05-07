package sena.adso.core.audit;

import java.time.Instant;

/**
 * DTO que representa una entrada de auditoría.
 * Se construye en el Servlet, se completa en el AuditInterceptor,
 * y se persiste async vía AsyncAuditWriter.
 */
public class AuditEntry {

    private Long userId;
    private String username;
    private String actionName;
    private String entityType;
    private Long entityId;
    private String status;
    private String requestData;
    private String responseData;
    private String errorMessage;
    private Long startTime;
    private Long executionTimeMs;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;

    public AuditEntry() {
        this.startTime = System.currentTimeMillis();
        this.createdAt = Instant.now();
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // ============================================================
    // MÉTODOS DE CONVENIENCIA
    // ============================================================

    /**
     * Calcula el tiempo de ejecución basado en startTime y el momento actual.
     */
    public void calculateExecutionTime() {
        if (this.startTime != null) {
            this.executionTimeMs = System.currentTimeMillis() - this.startTime;
        }
    }

    @Override
    public String toString() {
        return "AuditEntry{" +
                "actionName='" + actionName + '\'' +
                ", userId=" + userId +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", status='" + status + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}