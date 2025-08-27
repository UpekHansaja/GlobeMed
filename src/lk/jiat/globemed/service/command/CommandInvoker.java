package lk.jiat.globemed.service.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandInvoker {

    private final Deque<Command> history = new ArrayDeque<>();

    public void executeCommand(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    // You could implement undo per-command if command supports it
    public boolean hasHistory() {
        return !history.isEmpty();
    }
}
