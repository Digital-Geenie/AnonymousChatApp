package com.app.helper;

public class UserBean {

    private String userId;
    private String userName;
    private String timeStamp;
    private String locationLatitude;
    private String locationLongitude;
    private String topic;
    private String brief;
    private String detail;

    public UserBean(String userId, String userName, String timeStamp, String locationLatitude, String locationLongitude, String topic,
                    String brief, String detail) {
        this.userId = userId;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.topic = topic;
        this.brief = brief;
        this.detail = detail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
