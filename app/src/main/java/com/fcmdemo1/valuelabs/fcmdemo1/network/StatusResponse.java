package mobileapp.axiom.com.partnerapp2.network;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusResponse {
    @JsonProperty("StatusCode")
    private int statusCode;
    @JsonProperty("MessageCode")
    private String messageCode;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("IsLocked")
    private int isLocked;
    @JsonProperty("ServerTime")
    private String serverTime;


    @JsonProperty("StatusCode")
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty("StatusCode")
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("MessageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("MessageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("Message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("IsLocked")
    public int getIsLocked() {
        return isLocked;
    }

    @JsonProperty("IsLocked")
    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    @JsonProperty("ServerTime")
    public String getServerTime() {
        return serverTime;
    }

    @JsonProperty("ServerTime")
    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "statusCode=" + statusCode +
                ", messageCode='" + messageCode + '\'' +
                ", message='" + message + '\'' +
                ", isLocked=" + isLocked +
                ", serverTime='" + serverTime + '\'' +
                '}';
    }
}
