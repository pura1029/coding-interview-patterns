package patterns.designpatterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 11. COMMAND PATTERN (Behavioral)
 *
 * Encapsulates a request as an object, allowing you to parameterize clients,
 * queue requests, log them, and support undo/redo operations.
 *
 * When to use:
 * - Undo/redo functionality (text editors, drawing apps)
 * - Task queues, job scheduling
 * - Macro recording (execute a sequence of commands)
 * - Decoupling the sender from the receiver
 *
 * Key idea: Wrap each action in a Command object with execute() and undo().
 */
public class CommandPattern {

    // ======================== Command Interface ========================
    interface Command {
        void execute();
        void undo();
        String description();
    }

    // ======================== Receiver ========================
    static class TextEditor {
        private final StringBuilder content = new StringBuilder();

        public void insert(String text) { content.append(text); }
        public void delete(int length) {
            if (length <= content.length()) content.delete(content.length() - length, content.length());
        }
        public String getContent() { return content.toString(); }
    }

    // ======================== Concrete Commands ========================
    static class InsertCommand implements Command {
        private final TextEditor editor;
        private final String text;

        InsertCommand(TextEditor editor, String text) { this.editor = editor; this.text = text; }

        @Override
        public void execute() { editor.insert(text); }
        @Override
        public void undo() { editor.delete(text.length()); }
        @Override
        public String description() { return "Insert '" + text + "'"; }
    }

    static class DeleteCommand implements Command {
        private final TextEditor editor;
        private final int length;
        private String deleted;

        DeleteCommand(TextEditor editor, int length) { this.editor = editor; this.length = length; }

        @Override
        public void execute() {
            String content = editor.getContent();
            deleted = content.substring(Math.max(0, content.length() - length));
            editor.delete(length);
        }
        @Override
        public void undo() { editor.insert(deleted); }
        @Override
        public String description() { return "Delete " + length + " chars"; }
    }

    // ======================== Invoker (with Undo/Redo) ========================
    static class CommandManager {
        private final Stack<Command> undoStack = new Stack<>();
        private final Stack<Command> redoStack = new Stack<>();

        public void execute(Command cmd) {
            cmd.execute();
            undoStack.push(cmd);
            redoStack.clear();
            System.out.println("  Executed: " + cmd.description());
        }

        public void undo() {
            if (undoStack.isEmpty()) { System.out.println("  Nothing to undo"); return; }
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
            System.out.println("  Undone: " + cmd.description());
        }

        public void redo() {
            if (redoStack.isEmpty()) { System.out.println("  Nothing to redo"); return; }
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
            System.out.println("  Redone: " + cmd.description());
        }
    }

    // ======================== Macro Command ========================
    static class MacroCommand implements Command {
        private final String name;
        private final List<Command> commands = new ArrayList<>();

        MacroCommand(String name) { this.name = name; }

        public MacroCommand add(Command cmd) { commands.add(cmd); return this; }

        @Override
        public void execute() { commands.forEach(Command::execute); }
        @Override
        public void undo() {
            for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
        }
        @Override
        public String description() { return "Macro[" + name + "]"; }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Command Pattern ===\n");

        // new TextEditor() → receiver with StringBuilder content; new CommandManager() → invoker with ArrayDeque<>() undoStack + redoStack
        TextEditor editor = new TextEditor();
        // new CommandManager() → creates object
        CommandManager mgr = new CommandManager();

        System.out.println("--- Building text with undo/redo ---");
        // new InsertCommand(editor, "Hello") → encapsulates action as object; execute() calls editor.insert(); mgr pushes to undoStack
        mgr.execute(new InsertCommand(editor, "Hello"));
        System.out.println("  Content: \"" + editor.getContent() + "\"");

        mgr.execute(new InsertCommand(editor, " World"));
        System.out.println("  Content: \"" + editor.getContent() + "\"");

        mgr.execute(new InsertCommand(editor, "!"));
        System.out.println("  Content: \"" + editor.getContent() + "\"");

        System.out.println("\n--- Undo ---");
        // undo() → if (!undoStack.isEmpty()) pop command, call command.undo(), push to redoStack — conditional stack operation
        mgr.undo();
        System.out.println("  Content: \"" + editor.getContent() + "\"");
        mgr.undo();
        System.out.println("  Content: \"" + editor.getContent() + "\"");

        System.out.println("\n--- Redo ---");
        // redo() → if (!redoStack.isEmpty()) pop command, call command.execute(), push back to undoStack — mirror of undo
        mgr.redo();
        System.out.println("  Content: \"" + editor.getContent() + "\"");

        System.out.println("\n--- Macro Command ---");
        // new MacroCommand("greeting") → composite command with ArrayList<Command>; .add() chains InsertCommands — command aggregation
        TextEditor editor2 = new TextEditor();
        // new MacroCommand() → creates object
        MacroCommand macro = new MacroCommand("greeting")
            .add(new InsertCommand(editor2, "Dear "))
            .add(new InsertCommand(editor2, "User"))
            .add(new InsertCommand(editor2, ", welcome!"));
        // macro.execute() → for-each command in list: command.execute() — batch execution; undo() reverses in opposite order
        macro.execute();
        System.out.println("  Content: \"" + editor2.getContent() + "\"");
        macro.undo();
        System.out.println("  After undo: \"" + editor2.getContent() + "\"");
    }
}
