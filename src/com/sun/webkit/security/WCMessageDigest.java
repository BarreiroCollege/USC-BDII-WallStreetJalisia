/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.webkit.security;

import com.sun.javafx.webkit.WCMessageDigestImpl;
import com.sun.webkit.perf.WCMessageDigestPerfLogger;
import java.nio.ByteBuffer;

public abstract class WCMessageDigest {
    /**
     * Creates the instance of WCMessageDigest for the given algorithm.
     * @param algorithm the name of the algorithm like SHA-1, SHA-256.
     */
    protected static WCMessageDigest getInstance(String algorithm) {
        try {
            WCMessageDigest digest = new WCMessageDigestImpl(algorithm);
            return WCMessageDigestPerfLogger.isEnabled() ? new WCMessageDigestPerfLogger(digest) : digest;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Update the digest using the specified ByteBuffer.
     */
    public abstract void addBytes(ByteBuffer input);

    /**
     * Returns the computed hash value.
     */
    public abstract byte[] computeHash();
}
