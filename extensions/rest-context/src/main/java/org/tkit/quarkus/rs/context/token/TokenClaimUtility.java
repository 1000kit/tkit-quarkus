package org.tkit.quarkus.rs.context.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.smallrye.jwt.JsonUtils;

/**
 * Token claim utility
 */
public class TokenClaimUtility {

    /**
     * Default string split separator
     */
    public static final String DEFAULT_TOKEN_SEPARATOR = " ";

    private TokenClaimUtility() {
    }

    /**
     * Finds claim string list for path.
     *
     * @param token token web token
     * @param path the path of claims
     * @return the corresponding list of string or null
     */
    public static List<String> findClaimStringList(JsonWebToken token, String[] path) {
        return findClaimStringList(token, path, DEFAULT_TOKEN_SEPARATOR);
    }

    /**
     * Finds claim string list for path.
     *
     * @param token token web token
     * @param path the path of claims
     * @param listSeparator split string value to list with this separator
     * @return the corresponding list of string or null
     */
    public static List<String> findClaimStringList(JsonWebToken token, String[] path, String listSeparator) {
        JsonValue claimValue = findClaimValue(token, path);
        if (claimValue == null) {
            return List.of();
        }
        if (claimValue instanceof JsonArray jsonArray) {
            return convertJsonArrayToList(jsonArray);
        }
        var tmp = claimValue.toString();
        if (tmp.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(tmp.split(listSeparator));
    }

    /**
     * Finds claim value for path.
     *
     * @param token token web token
     * @param path the path of claims
     * @return the corresponding list of string or null
     */
    public static JsonValue findClaimValue(JsonWebToken token, String[] path) {
        if (token == null) {
            return null;
        }
        if (path == null || path.length == 0) {
            return null;
        }
        return findClaimValue(JsonUtils.wrapValue(token.getClaim(path[0])), path, 1);
    }

    private static JsonValue findClaimValue(JsonValue json, String[] path, int step) {
        if (path == null) {
            return json;
        }
        if (json == null) {
            return null;
        }
        if (step >= path.length) {
            return json;
        }
        if (json instanceof JsonObject) {
            JsonValue claimValue = json.asJsonObject().get(path[step].replace("\"", ""));
            return findClaimValue(claimValue, path, step + 1);
        }
        return json;
    }

    public static List<String> convertJsonArrayToList(JsonArray claimValue) {
        List<String> list = new ArrayList<>(claimValue.size());
        for (int i = 0; i < claimValue.size(); i++) {
            String item = claimValue.getString(i);
            if (item.isBlank()) {
                continue;
            }
            list.add(item);
        }
        return list;
    }
}
