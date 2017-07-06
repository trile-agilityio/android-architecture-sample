package com.sample.androidarchitecture.networking.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;
import timber.log.Timber;

/**
 * A Generic common class used by API responses.
 *
 * @param <T>
 */
public class ResponseApi<T> {

    private static final Pattern LINK_PATTERN = Pattern
            .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"");
    private static final Pattern PAGE_PATTERN = Pattern.compile("page=(\\d)+");
    private static final String NEXT_LINK = "next";

    public int code;

    @Nullable
    public final T body;

    @Nullable
    public final String message;

    @NonNull
    public final Map<String, String> links;

    /**
     * ResponseApi constructor.
     *
     * @param body
     * @param message
     * @param links
     */
    public ResponseApi(T body, String message, @NonNull Map<String, String> links) {
        this.body = body;
        this.message = message;
        this.links = links;
    }

    /**
     * ResponseApi constructor.
     *
     * @param error
     */
    public ResponseApi(Throwable error) {
        code = 500;
        body = null;
        message = error.getMessage();
        links = Collections.emptyMap();
    }

    /**
     * ResponseApi constructor.
     *
     * @param response
     */
    public ResponseApi(Response<T> response) {
        code = response.code();

        if (response.isSuccessful()) {
            body = response.body();
            message = null;
        } else {
            String message = null;

            if (response.body() != null) {
                message = response.errorBody().toString();
            }

            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }

            this.message = message;
            body = null;
        }

        String linkHeader = response.headers().get("link");

        if (linkHeader == null) {
            links = Collections.emptyMap();

        } else {

            links = new ArrayMap<>();
            Matcher matcher = LINK_PATTERN.matcher(linkHeader);

            while (matcher.find()) {
                int count = matcher.groupCount();
                if (count == 2) {
                    links.put(matcher.group(2), matcher.group(1));
                }
            }
        }
    }

    /**
     * Check response is successful.
     *
     * @return
     */
    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    /**
     * Get next page.
     *
     * @return
     */
    public Integer getNextPage() {
        String next = links.get(NEXT_LINK);

        if (next == null) {
            return null;
        }

        Matcher matcher = PAGE_PATTERN.matcher(next);
        if (!matcher.find() || matcher.groupCount() != 1) {
            return null;
        }

        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            Timber.w("cannot parse next page from %s", next);
            return null;
        }
    }
}