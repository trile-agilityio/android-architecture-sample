package com.sample.androidarchitecture.util.common;

import android.os.SystemClock;
import android.util.ArrayMap;

import java.util.concurrent.TimeUnit;

/**
 * Utility class that decides whether we should fetch some data or not.
 */
public class RateLimiter<KEY> {

    private ArrayMap<KEY, Long> timestamps = new ArrayMap<>();
    private final long timeOut;

    public RateLimiter(long timeOut, TimeUnit timeUnit) {
        this.timeOut = timeUnit.toMillis(timeOut);
    }

    /**
     * Check should fetch some data or not.
     *
     * @param key
     * @return
     */
    public synchronized boolean shouldFetch(KEY key) {
        Long lastFetch = timestamps.get(key);
        long now = now();

        if (lastFetch == null) {
            timestamps.put(key, now);
            return true;
        }

        if (now - lastFetch > timeOut) {
            timestamps.put(key, now);
            return true;
        }

        return false;
    }

    /**
     * Get time now.
     *
     * @return
     */
    private long now() {
        return SystemClock.uptimeMillis();
    }

    /**
     * Reset
     *
     * @param key
     */
    public synchronized void reset(KEY key) {
        timestamps.remove(key);
    }
}
