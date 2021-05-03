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

package com.sun.webkit.perf;

import com.sun.webkit.security.WCMessageDigest;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class WCMessageDigestPerfLogger extends WCMessageDigest {
    private static final Logger log =
            Logger.getLogger(WCMessageDigestPerfLogger.class.getName());

    private static final PerfLogger logger = PerfLogger.getLogger(log);

    final private WCMessageDigest digest;

    public WCMessageDigestPerfLogger(WCMessageDigest digest) {
        this.digest = digest;
    }

    public synchronized static boolean isEnabled() {
        return logger.isEnabled();
    }

    @Override
    public void addBytes(ByteBuffer input) {
        logger.resumeCount("ADDBYTES");
        digest.addBytes(input);
        logger.suspendCount("ADDBYTES");
    }

    @Override
    public byte[] computeHash() {
        logger.resumeCount("COMPUTEHASH");
        byte[] result = digest.computeHash();
        logger.suspendCount("COMPUTEHASH");
        return result;
    }
}
