package patterns.designpatterns;

import java.util.ArrayList;
import java.util.List;

/**
 * 8. COMPOSITE PATTERN (Structural)
 *
 * Composes objects into tree structures to represent part-whole hierarchies.
 * Clients treat individual objects and compositions uniformly.
 *
 * When to use:
 * - File systems (files and directories)
 * - Organization hierarchies (employees and departments)
 * - UI component trees (panels containing buttons, labels, sub-panels)
 *
 * Key idea: Leaf and Composite share the same interface. Composite holds children.
 */
public class CompositePattern {

    // ======================== Component Interface ========================
    interface FileSystemComponent {
        String name();
        long size();
        void display(String indent);
    }

    // ======================== Leaf ========================
    static class File implements FileSystemComponent {
        private final String name;
        private final long size;

        File(String name, long size) { this.name = name; this.size = size; }

        @Override
        public String name() { return name; }
        @Override
        public long size() { return size; }
        @Override
        public void display(String indent) {
            System.out.println(indent + "📄 " + name + " (" + size + " KB)");
        }
    }

    // ======================== Composite ========================
    static class Directory implements FileSystemComponent {
        private final String name;
        private final List<FileSystemComponent> children = new ArrayList<>();

        Directory(String name) { this.name = name; }

        public Directory add(FileSystemComponent component) {
            children.add(component);
            return this;
        }

        @Override
        public String name() { return name; }

        @Override
        public long size() {
            return children.stream().mapToLong(FileSystemComponent::size).sum();
        }

        @Override
        public void display(String indent) {
            System.out.println(indent + "📁 " + name + "/ (" + size() + " KB)");
            for (FileSystemComponent child : children) {
                child.display(indent + "  ");
            }
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Composite Pattern ===\n");

        // new Directory("project") → composite node with ArrayList<FileSystem> children; add() returns this for chaining; display() recurses children
        Directory root = new Directory("project");
        // new Directory() → creates object
        Directory src = new Directory("src");
        // new Directory() → creates object
        Directory test = new Directory("test");

        // new File("Main.java", 15) → leaf node implementing FileSystem; size() returns 15; display() prints name — no children
        // add() returns this (fluent API); builds tree: src contains 3 files — composite adds leaves
        src.add(new File("Main.java", 15))
           .add(new File("Utils.java", 8))
           .add(new File("Config.java", 3));

        // new Directory("controllers") → composite within composite; src.add(controllers) nests tree deeper — recursive structure
        Directory controllers = new Directory("controllers");
        controllers.add(new File("UserController.java", 12))
                   .add(new File("OrderController.java", 10));
        src.add(controllers);

        test.add(new File("MainTest.java", 6))
            .add(new File("UtilsTest.java", 4));

        root.add(src).add(test)
            .add(new File("pom.xml", 2))
            .add(new File("README.md", 1));

        // display("") → recursive: prints own name, then for-each child calls child.display(indent + "  ") — tree traversal with increasing indent
        root.display("");

        // size() → composite sums children: for (FileSystem f : children) total += f.size(); leaf returns own size — recursive aggregation
        System.out.println("\nTotal project size: " + root.size() + " KB");
        System.out.println("Benefit: Files and directories share the same interface — uniform treatment.");
    }
}
