package org.example.datalabelingtool.domain.samples.entity;

public enum SampleStatus {
    CREATED("STATUS_CREATED"),
    REQUESTED_UPDATE("STATUS_REQUESTED_UPDATE"),
    REQUESTED_DELETE("STATUS_REQUESTED_DELETE"),
    APPROVED("STATUS_APPROVED"),
    UPDATED("STATUS_UPDATED"),
    DELETED("STATUS_DELETED"),
    REJECTED("STATUS_REJECTED");

    private final String status;

    SampleStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
