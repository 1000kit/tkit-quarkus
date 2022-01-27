package org.tkit.quarkus.context;

import java.io.Serializable;

public class RequestData implements Serializable {
    
    private String principal;
    
    public String getPrincipal() {
        return principal;
    }
    
    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}
