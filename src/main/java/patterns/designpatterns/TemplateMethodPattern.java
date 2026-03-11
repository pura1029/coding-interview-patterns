package patterns.designpatterns;

/**
 * 14. TEMPLATE METHOD PATTERN (Behavioral)
 *
 * Defines the skeleton of an algorithm in a base class, letting subclasses
 * override specific steps without changing the algorithm's structure.
 *
 * When to use:
 * - Multiple classes share the same algorithmic structure but differ in specific steps
 * - Data processing pipelines (parse → validate → transform → output)
 * - Game loops, report generators, build systems
 *
 * Key idea: Base class defines the template (final method); subclasses override hooks.
 */
public class TemplateMethodPattern {

    // ======================== Abstract Template ========================
    static abstract class DataProcessor {

        public final void process(String data) {
            System.out.println("  [" + name() + "] Processing: " + data);
            String parsed = parse(data);
            String validated = validate(parsed);
            String transformed = transform(validated);
            output(transformed);
            System.out.println();
        }

        abstract String name();
        abstract String parse(String raw);
        abstract String validate(String parsed);
        abstract String transform(String validated);

        void output(String result) {
            System.out.println("    Output: " + result);
        }
    }

    // ======================== Concrete Implementations ========================
    static class CSVProcessor extends DataProcessor {
        @Override
        String name() { return "CSV"; }

        @Override
        String parse(String raw) {
            String parsed = raw.replace(",", " | ");
            System.out.println("    Parsed CSV: " + parsed);
            return parsed;
        }

        @Override
        String validate(String parsed) {
            System.out.println("    Validated: all fields present");
            return parsed;
        }

        @Override
        String transform(String validated) {
            String upper = validated.toUpperCase();
            System.out.println("    Transformed to uppercase: " + upper);
            return upper;
        }
    }

    static class JSONProcessor extends DataProcessor {
        @Override
        String name() { return "JSON"; }

        @Override
        String parse(String raw) {
            String parsed = raw.replace("{", "").replace("}", "").replace("\"", "");
            System.out.println("    Parsed JSON: " + parsed);
            return parsed;
        }

        @Override
        String validate(String parsed) {
            boolean valid = parsed.contains(":");
            System.out.println("    Validated: " + (valid ? "has key-value pairs" : "INVALID"));
            return parsed;
        }

        @Override
        String transform(String validated) {
            String trimmed = validated.trim();
            System.out.println("    Transformed (trimmed): " + trimmed);
            return trimmed;
        }
    }

    static class XMLProcessor extends DataProcessor {
        @Override
        String name() { return "XML"; }

        @Override
        String parse(String raw) {
            String parsed = raw.replaceAll("<[^>]+>", "").trim();
            System.out.println("    Parsed XML tags removed: " + parsed);
            return parsed;
        }

        @Override
        String validate(String parsed) {
            System.out.println("    Validated: well-formed content");
            return parsed;
        }

        @Override
        String transform(String validated) {
            String result = "Processed[" + validated + "]";
            System.out.println("    Transformed: " + result);
            return result;
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Template Method Pattern ===\n");

        DataProcessor csv = new CSVProcessor();
        csv.process("Alice,30,Engineer");

        DataProcessor json = new JSONProcessor();
        json.process("{\"name\": \"Bob\", \"age\": 25}");

        DataProcessor xml = new XMLProcessor();
        xml.process("<user><name>Carol</name></user>");

        System.out.println("Benefit: Algorithm structure stays fixed; only specific steps are customized.");
    }
}
