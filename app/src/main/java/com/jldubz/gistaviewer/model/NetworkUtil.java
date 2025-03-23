package com.jldubz.gistaviewer.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import okhttp3.Headers;
import retrofit2.Response;

public class NetworkUtil {
    public static String onGitHubResponseError(Response response) {
        if (response.code() == 403) {
            Headers headers = response.headers();
            Set<String> headerNames = headers.names();
            long rateLimitReset = 0;

            for (String headerName : headerNames) {
                String headerValue = headers.get(headerName);
                if (headerValue == null) {
                    continue;
                }
                if (headerName.equalsIgnoreCase("X-RateLimit-Reset")) {
                    rateLimitReset = Long.parseLong(headerValue);
                    break;
                }
            }
            if (rateLimitReset > 0) {
                Date resetDate = new Date(rateLimitReset * 1000);
                String resetTimeText = DateFormat.getTimeInstance().format(resetDate);
                return "Rate limit exceeded. Try again after " + resetTimeText;
            }
        }
        return response.message();
    }
}
