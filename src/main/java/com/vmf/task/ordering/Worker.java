/**
 * Copyright 2018 Vinícius M. Freitas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vmf.task.ordering;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents the that will execute the tasks.
 * @param <T> task class.
 */
class Worker<T extends Task> implements Runnable {

    private final LinkedBlockingQueue<T> tasks = new LinkedBlockingQueue<>();
    private volatile boolean stopped;

    void schedule(T task) {
        tasks.add(task);
    }

    void stop() {
        stopped = true;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                T task = tasks.take();
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
