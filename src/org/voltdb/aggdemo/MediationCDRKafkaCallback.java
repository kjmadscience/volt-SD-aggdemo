package org.voltdb.aggdemo;

import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.voltdb.voltutil.stats.SafeHistogramCache;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2023 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Simple callback that complains if something went badly wrong.
 *
 */
public class MediationCDRKafkaCallback implements org.apache.kafka.clients.producer.Callback {

    public static SafeHistogramCache shc = SafeHistogramCache.getInstance();
    ActiveSession pseudoRandomSession;
    BlockingQueue<ActiveSession> burstingSessionQueue;

    public MediationCDRKafkaCallback(ActiveSession pseudoRandomSession,
            BlockingQueue<ActiveSession> burstingSessionQueue) {
        this.pseudoRandomSession = pseudoRandomSession;
        this.burstingSessionQueue = burstingSessionQueue;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, java.lang.Exception exception) {
        if (exception != null) {
            MediationDataGenerator.msg("MediationCDRKafkaCallback:" + exception.getMessage());
        } else {
            try {
                if (pseudoRandomSession != null && pseudoRandomSession.getAndDecrementRemainingActvity() > 0) {

                    // Add entry back to queue if we're not finished with it....
                    burstingSessionQueue.add(pseudoRandomSession);
                    shc.incCounter(MediationDataGenerator.SESSION_RETURNED_TO_QUEUE);

                }

            } catch (IllegalStateException e) {
                shc.incCounter(MediationDataGenerator.SESSION_QUEUE_FULL);
            }

        }

    }

}
