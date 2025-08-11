package org.tkit.quarkus.rs.context.token;

import java.util.HashMap;
import java.util.Map;

public class TokenParserRequest {

    private final String rawToken;

    private boolean verify;

    private boolean issuerEnabled;

    private String issuerSuffix;

    private String issuerUrl;

    private String type;

    private final Map<String, IssuerParserRequest> issuerParserRequests = new HashMap<>();

    public TokenParserRequest(String rawToken) {
        this.rawToken = rawToken;
    }

    public String getRawToken() {
        return rawToken;
    }

    public String getIssuerUrl() {
        return issuerUrl;
    }

    public void setIssuerUrl(String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    public TokenParserRequest issuerUrl(String issuerSuffix) {
        setIssuerUrl(issuerSuffix);
        return this;
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

    public void addIssuerParserRequest(String name, IssuerParserRequest issuerParserRequest) {
        issuerParserRequests.put(name, issuerParserRequest);
    }

    public Map<String, IssuerParserRequest> getIssuerParserRequests() {
        return issuerParserRequests;
    }

    public static class IssuerParserRequest {

        private String url;

        private String publicKeyLocationUrl;

        private String publicKeyLocationSuffix;

        private boolean publicKeyLocationEnabled;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public IssuerParserRequest url(String url) {
            setUrl(url);
            return this;
        }

        public void setPublicKeyLocationSuffix(String publicKeyLocationSuffix) {
            this.publicKeyLocationSuffix = publicKeyLocationSuffix;
        }

        public String getPublicKeyLocationSuffix() {
            return publicKeyLocationSuffix;
        }

        public IssuerParserRequest publicKeyLocationSuffix(String publicKeyLocationSuffix) {
            setPublicKeyLocationSuffix(publicKeyLocationSuffix);
            return this;
        }

        public boolean getPublicKeyLocationEnabled() {
            return publicKeyLocationEnabled;
        }

        public void setPublicKeyLocationEnabled(boolean publicKeyLocationEnabled) {
            this.publicKeyLocationEnabled = publicKeyLocationEnabled;
        }

        public IssuerParserRequest publicKeyLocationEnabled(boolean publicKeyLocationEnabled) {
            setPublicKeyLocationEnabled(publicKeyLocationEnabled);
            return this;
        }

        public String getPublicKeyLocationUrl() {
            return publicKeyLocationUrl;
        }

        public void setPublicKeyLocationUrl(String publicKeyLocationUrl) {
            this.publicKeyLocationUrl = publicKeyLocationUrl;
        }

        public IssuerParserRequest publicKeyLocationUrl(String publicKeyLocationUrl) {
            setPublicKeyLocationUrl(publicKeyLocationUrl);
            return this;
        }

        @Override
        public String toString() {
            return "IssuerParserRequest{" +
                    "url='" + url + '\'' +
                    ", publicKeyLocationUrl='" + publicKeyLocationUrl + '\'' +
                    ", publicKeyLocationSuffix='" + publicKeyLocationSuffix + '\'' +
                    ", publicKeyLocationEnabled=" + publicKeyLocationEnabled +
                    '}';
        }
    }
}
