package org.casino.aggregator.dto;

public class AggregatorLaunchRequestDTO {
    String token;
    String gameid;

    public String getGameid() {
        return gameid;
    }
    public void setGameid(String gameid) {
        this.gameid = gameid;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
