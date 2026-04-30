package cn.jxufe.valuexu.softwarestoreserver.config;

import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constant {
    public static final List<HttpMethod> AUTH_WHITELIST_METHODS = List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.PATCH, HttpMethod.HEAD);

    private Constant() {
    }
}
