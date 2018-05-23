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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a tasks ordering executor.
 * @param <T> task class.
 */
public class TasksOrderingExecutor<T extends Task> {
    private final Map<Integer, Worker<T>> workers = new HashMap<>();
    private final Class<T> classType;

    private TasksOrderingExecutor(Class<T> classType) {
        this.classType = classType;
    }

    /**
     * Creates a new tasks ordering executor.
     * @param classType task class type.
     * @param <T> task class.
     * @return
     */
    public static <T extends Task> TasksOrderingExecutor<T> forClass(Class<T> classType) {
        return new TasksOrderingExecutor<T>(classType);
    }

    /**
     * Submits a task.
     * @param task to be submitted.
     */
    public void submit(T task) {
        Worker<T> worker = getWorker(task.getSerialToken());
        worker.schedule(task);
    }

    /**
     * Stops all submitted tasks.
     */
    public void stop() {
        for (Worker<T> worker : workers.values())
            worker.stop();
    }

    private Worker<T> getWorker(Integer serialToken) {
        Worker<T> worker = workers.getOrDefault(serialToken, null);
        return worker != null ? worker : createNewWorker(serialToken);
    }

    private Worker<T> createNewWorker(Integer serialToken) {
        Worker<T> worker = new Worker<>();
        Thread thread = new Thread(worker, String.format("Class=%s;ID=%d", classType.getSimpleName(), serialToken));
        thread.start();
        workers.put(serialToken, worker);
        return worker;
    }
}
