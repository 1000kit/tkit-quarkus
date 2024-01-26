package org.tkit.quarkus.rs.context.token;

public class TokenParserRequest {

    private final String rawToken;

    private boolean verify;

    private boolean issuerEnabled;

    private String issuerSuffix;

    private String type;

    public TokenParserRequest(String rawToken) {
        this.rawToken = rawToken;
    }

    public String getRawToken() {
        return rawToken;
    }

    public boolean isIssuerEnabled() {
        return issuerEnabled;
    }

    public void setIssuerEnabled(boolean issuerEnabled) {
        this.issuerEnabled = issuerEnabled;
    }

    public TokenParserRequest issuerEnabled(boolean issuerEnabled) {
        setIssuerEnabled(issuerEnabled);
        return this;
    }

    public String getIssuerSuffix() {
        return issuerSuffix;
    }

    public void setIssuerSuffix(String issuerSuffix) {
        this.issuerSuffix = issuerSuffix;
    }

    public TokenParserRequest issuerSuffix(String issuerSuffix) {
        setIssuerSuffix(issuerSuffix);
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TokenParserRequest type(String type) {
        setType(type);
        return this;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public TokenParserRequest verify(boolean verify) {
        setVerify(verify);
        return this;
    }
}
