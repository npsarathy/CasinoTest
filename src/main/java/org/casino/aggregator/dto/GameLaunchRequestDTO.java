package org.casino.aggregator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameLaunchRequestDTO {
    String signature;
    Params params;

    public Params getParams() {
        return params;
    }
    public void setParams(Params params) {
        this.params = params;
    }
    public void setParams(String token, String gameId) {
        this.params = new Params(token, gameId);
    }

    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

}
