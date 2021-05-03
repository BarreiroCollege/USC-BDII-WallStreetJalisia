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

package com.sun.javafx.webkit;

import com.sun.webkit.security.WCMessageDigest;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WCMessageDigestImpl extends WCMessageDigest {
    private final MessageDigest digest;

    public WCMessageDigestImpl(String algorithm) throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance(algorithm);
    }

    @Override
    public void addBytes(ByteBuffer input) {
        digest.update(input);
    }

    @Override
    public byte[] computeHash() {
        return digest.digest();
    }
}
