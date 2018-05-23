# Tasks Ordering

This project aims to solve serial and parallel task execution. What it does is to group tasks with a common token and then execute this group serially.
For that, each task declares a serial token which will be used to determine in which thread the task will be executed.

#### Lets suppose we have a command class like this:
```
public class Command {
    private final Integer deviceId;
    private final String value;

    public Command(Integer deviceId, String value) {
        this.deviceId = deviceId;
        this.value = value;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public String getValue() {
        return value;
    }
}
```
and a task class like this:
```
public class CommandTask implements Task {

    private final Command command;

    public CommandTask(Command command) {
        this.command = command;
    }

    @Override
    public int getSerialToken() {
        return command.getDeviceId();
    }

    @Override
    public void run() {
        // process the command...
    }
}
```

using the classes above we could do:

```
TasksOrderingExecutor<CommandTask> executor = TasksOrderingExecutor.forClass(CommandTask.class);

CommandTask task1 = new CommandTask(new Command(1, "anything"));
CommandTask task2 = new CommandTask(new Command(1, "anything"));
CommandTask task3 = new CommandTask(new Command(2, "anything"));

executor.submit(task1);
executor.submit(task2);
executor.submit(task3);
```

#### In this case, how the tasks are going to be execute?

- When 'task1' is submitted a thread will be created for the deviceId 1 and after that the task will be executed.
- When 'task2' is submitted it will verify that a thread for deviceId 1 already exists and therefore will use the same thread used to process 'task1' to process 'task2'.
- When 'task3' is submitted a thread will be created for the deviceId 2 and after that the task will be executed.

You see? We choose the thread which will execute the task based on the serial token. We pile the tasks that use the same token and create new thread for non existing ones.