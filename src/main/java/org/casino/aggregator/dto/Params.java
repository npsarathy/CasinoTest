package org.casino.aggregator.dto;

public class Params {
    String token;
    String gameid;

    public Params(String token, String gameId) {
        this.token = token;
        this.gameid = gameId;
    }

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
