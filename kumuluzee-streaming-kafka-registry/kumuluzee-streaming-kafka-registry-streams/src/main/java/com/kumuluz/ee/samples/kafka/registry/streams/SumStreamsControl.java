/*
 *  Copyright (c) 2014-2020 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kumuluz.ee.samples.kafka.registry.streams;

import com.kumuluz.ee.streaming.common.annotations.StreamProcessorController;
import com.kumuluz.ee.streaming.kafka.utils.streams.StreamsController;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.concurrent.CountDownLatch;

/**
 * @author cen1
 */
@ApplicationScoped
public class SumStreamsControl {

    @StreamProcessorController(id="price-sum")
    StreamsController sumStreams;

    public void startStream(@Observes @Initialized(ApplicationScoped.class) Object init) {
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-price-sum-shutdown-hook") {
            @Override
            public void run() {
                sumStreams.close();
                latch.countDown();
            }
        });

        try {
            sumStreams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
