package lk.jiat.globemed.service.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Simple invoker that executes commands and keeps history (for potential undo).
 */
public class CommandInvoker {

    private final Deque<Command> history = new ArrayDeque<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }

    public Command popLast() {
        return history.poll();
    }

    public void clearHistory() {
        history.clear();
    }
}
