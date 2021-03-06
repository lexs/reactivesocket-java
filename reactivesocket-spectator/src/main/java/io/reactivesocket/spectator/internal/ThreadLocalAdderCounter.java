/*
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivesocket.spectator.internal;


import com.netflix.spectator.api.Counter;
import com.netflix.spectator.api.Id;
import com.netflix.spectator.api.Measurement;
import com.netflix.spectator.api.Registry;
import com.netflix.spectator.api.Spectator;
import io.reactivesocket.util.Clock;

import java.util.Collections;

/**
 * A {@link Counter} implementation that uses {@link ThreadLocalAdderCounter}
 */
public class ThreadLocalAdderCounter implements Counter {

    private final ThreadLocalAdder adder = new ThreadLocalAdder();
    private final Id id;

    public ThreadLocalAdderCounter(String name, String monitorId) {
        this(Spectator.globalRegistry(), name, monitorId);
    }

    public ThreadLocalAdderCounter(Registry registry, String name, String monitorId, String... tags) {
        id = registry.createId(name, SpectatorUtil.mergeTags(tags, "id", monitorId));
        registry.register(this);
    }

    @Override
    public void increment() {
        adder.increment();
    }

    @Override
    public void increment(long amount) {
        adder.increment(amount);
    }

    @Override
    public long count() {
        return adder.get();
    }

    @Override
    public Id id() {
        return id;
    }

    @Override
    public Iterable<Measurement> measure() {
        long now = Clock.now();
        long v = adder.get();
        return Collections.singleton(new Measurement(id, now, v));
    }

    @Override
    public boolean hasExpired() {
        return false;
    }
}