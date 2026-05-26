package ui;

import serialization.EmbroideryPattern;
import serialization.PatternSerializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Maps Ukrainian names (uppercase) to pre-generated JSON pattern files
 * stored in the {@code patterns/} directory next to the running JAR / class root.
 *
 * <p>Lookup is case-insensitive and also handles common Latin transliterations
 * so English-keyboard users can type "ANNA", "SERHII", etc.
 */
public final class NamePatternLibrary {

    /** Ukrainian name (uppercase) -> filename without extension */
    private static final Map<String, String> NAME_TO_FILE = new LinkedHashMap<>();

    /** Latin filename (uppercase) -> Ukrainian name, for reverse/transliteration lookup */
    private static final Map<String, String> FILE_TO_NAME = new LinkedHashMap<>();

    static {
        // ---- А ----
        put("\u0410\u041B\u0406\u041D\u0410", "ALINA");
        put("\u0410\u041B\u0406\u0421\u0410", "ALISA");
        put("\u0410\u041B\u041B\u0410", "ALLA");
        put("\u0410\u041D\u0410\u0421\u0422\u0410\u0421\u0406\u042F", "ANASTASIIA");
        put("\u0410\u041D\u0410\u0422\u041E\u041B\u0406\u0419", "ANATOLII");
        put("\u0410\u041D\u0414\u0420\u0406\u0419", "ANDRII");
        put("\u0410\u041D\u041D\u0410", "ANNA");
        put("\u0410\u041D\u0422\u041E\u041D", "ANTON");
        put("\u0410\u0420\u0422\u0415\u041C", "ARTEM");
        // ---- Б ----
        put("\u0411\u041E\u0413\u0414\u0410\u041D", "BOHDAN");
        put("\u0411\u041E\u0413\u0414\u0410\u041D\u0410", "BOHDANA");
        // ---- В ----
        put("\u0412\u0410\u041B\u0415\u041D\u0422\u0418\u041D", "VALENTYN");
        put("\u0412\u0410\u041B\u0415\u041D\u0422\u0418\u041D\u0410", "VALENTYNA");
        put("\u0412\u0410\u041B\u0415\u0420\u0406\u042F", "VALERIIA");
        put("\u0412\u0410\u041B\u0415\u0420\u0406\u0419", "VALERII");
        put("\u0412\u0410\u0421\u0418\u041B\u042C", "VASYL");
        put("\u0412\u0410\u0421\u0418\u041B\u0406\u0421\u0410", "VASILISA");
        put("\u0412\u041B\u0410\u0414", "VLAD");
        put("\u0412\u041B\u0410\u0414\u0418\u0421\u041B\u0410\u0412", "VLADYSLAV");
        put("\u0412\u041E\u041B\u041E\u0414\u0418\u041C\u0418\u0420", "VOLODYMYR");
        put("\u0412\u0406\u0422\u0410\u041B\u0406\u0419", "VITALII");
        put("\u0412\u0406\u041A\u0422\u041E\u0420", "VIKTOR");
        put("\u0412\u0406\u041A\u0422\u041E\u0420\u0406\u042F", "VIKTORIIA");
        // ---- Г ----
        put("\u0413\u0410\u041D\u041D\u0410", "HANNA");
        put("\u0413\u0410\u041B\u0418\u041D\u0410", "HALYNA");
        put("\u0413\u041B\u0406\u0411", "HLIB");
        put("\u0413\u0415\u041D\u041D\u0410\u0414\u0406\u0419", "HENNADII");
        // ---- Д ----
        put("\u0414\u0410\u041D\u0418\u041B\u041E", "DANYLO");
        put("\u0414\u0410\u041D\u0406\u0406\u041B", "DANIIL");
        put("\u0414\u0415\u041D\u0418\u0421", "DENYS");
        put("\u0414\u041C\u0418\u0422\u0420\u041E", "DMYTRO");
        // ---- Є ----
        put("\u0404\u0412\u0410", "EVA");
        put("\u0404\u0412\u0413\u0415\u041D", "EVHEN");
        put("\u0404\u0412\u0413\u0415\u041D\u0406\u0419", "EVHENII");
        put("\u0404\u0412\u0413\u0415\u041D\u0406\u042F", "EVHENIIA");
        // ---- З ----
        put("\u0417\u041E\u042F", "ZOIA");
        put("\u0417\u041E\u0420\u042F\u041D\u0410", "ZORIANA");
        put("\u0417\u041B\u0410\u0422\u0410", "ZLATA");
        put("\u0417\u0410\u0425\u0410\u0420", "ZAKHAR");
        put("\u0417\u0410\u0425\u0410\u0420\u0406\u0419", "ZAKHARII");
        // ---- І ----
        put("\u0406\u0412\u0410\u041D", "IVAN");
        put("\u0406\u0413\u041E\u0420", "IHOR");
        put("\u0406\u041B\u041B\u042F", "ILLIA");
        put("\u0406\u041D\u041D\u0410", "INNA");
        put("\u0406\u0420\u0418\u041D\u0410", "IRYNA");
        // ---- К ----
        put("\u041A\u0410\u0422\u0415\u0420\u0418\u041D\u0410", "KATERYNA");
        put("\u041A\u0418\u0420\u0418\u041B\u041E", "KYRYLO");
        // ---- Л ----
        put("\u041B\u0406\u0414\u0406\u042F", "LIDIIA");
        put("\u041B\u0406\u041B\u0406\u042F", "LILIIA");
        put("\u041B\u0410\u0420\u0418\u0421\u0410", "LARYSA");
        put("\u041B\u0415\u0412", "LEV");
        put("\u041B\u0415\u041E\u041D\u0406\u0414", "LEONID");
        put("\u041B\u042E\u0411\u041E\u0412", "LIUBOV");
        put("\u041B\u042E\u0411\u041E\u041C\u0418\u0420", "Liubomyr");
        put("\u041B\u042E\u0411\u041E\u041C\u0418\u0420\u0410", "LIUBOMYRA");
        put("\u041B\u042E\u0414\u041C\u0418\u041B\u0410", "LIUDMYLA");
        // ---- М ----
        put("\u041C\u0410\u041A\u0421\u0418\u041C", "MAKSYM");
        put("\u041C\u0410\u0420\u0406\u042F", "MARIIA");
        put("\u041C\u0410\u0420\u0406\u041D\u0410", "MARYNA");
        put("\u041C\u0410\u0420\u041A", "MARK");
        put("\u041C\u0410\u0420\u041A\u0406\u042F\u041D", "MARKIIAN");
        put("\u041C\u0410\u0420\u0413\u0410\u0420\u0418\u0422\u0410", "MARHARYTA");
        put("\u041C\u0410\u0420\u0422\u0410", "MARTA");
        put("\u041C\u0410\u0420\u2019\u042F\u041D\u0410", "MARIANA");
        put("\u041C\u0410\u0422\u0412\u0406\u0419", "MATVII");
        put("\u041C\u0418\u041A\u041E\u041B\u0410", "MYKOLA");
        put("\u041C\u0418\u041A\u0418\u0422\u0410", "MYKYTA");
        put("\u041C\u0418\u0425\u0410\u0419\u041B\u041E", "MYKHAILO");
        put("\u041C\u0418\u0420\u041E\u041D", "MYRON");
        // ---- Н ----
        put("\u041D\u0410\u0417\u0410\u0420", "NAZAR");
        put("\u041D\u0410\u0414\u0406\u042F", "NADIIA");
        put("\u041D\u0410\u0422\u0410\u041B\u0406\u042F", "NATALIIA");
        put("\u041D\u0406\u041D\u0410", "NINA");
        // ---- О ----
        put("\u041E\u041A\u0421\u0410\u041D\u0410", "OKSANA");
        put("\u041E\u041B\u0415\u0413", "OLEH");
        put("\u041E\u041B\u0415\u041A\u0421\u0410\u041D\u0414\u0420", "OLEKSANDR");
        put("\u041E\u041B\u0415\u041A\u0421\u0410\u041D\u0414\u0420\u0410", "OLEKSANDRA");
        put("\u041E\u041B\u0415\u041D\u0410", "OLENA");
        put("\u041E\u041B\u0415\u0421\u042F", "OLESIA");
        put("\u041E\u041B\u0415\u041A\u0421\u0406\u0419", "OLEKSII");
        put("\u041E\u041B\u042C\u0413\u0410", "OLHA");
        put("\u041E\u0420\u0418\u0421\u042F", "ORYSIA");
        // ---- П ----
        put("\u041F\u0410\u0412\u041B\u041E", "PAVLO");
        put("\u041F\u0415\u0422\u0420\u041E", "PETRO");
        put("\u041F\u041E\u041B\u0406\u041D\u0410", "POLINA");
        put("\u041F\u041E\u0422\u0410\u041F", "POTAP");
        // ---- Р ----
        put("\u0420\u041E\u041C\u0410\u041D", "ROMAN");
        // ---- С ----
        put("\u0421\u0415\u0420\u0413\u0406\u0419", "SERHII");
        put("\u0421\u041E\u0424\u0406\u042F", "SOFIIA");
        put("\u0421\u0412\u0406\u0422\u041B\u0410\u041D\u0410", "SVITLANA");
        // ---- Т ----
        put("\u0422\u0410\u041C\u0410\u0420\u0410", "TAMARA");
        put("\u0422\u0410\u0420\u0410\u0421", "TARAS");
        put("\u0422\u0415\u0422\u042F\u041D\u0410", "TETIANA");
        put("\u0422\u0418\u041C\u041E\u0424\u0406\u0419", "TYMOFII");
        // ---- У ----
        put("\u0423\u041B\u042F\u041D\u0410", "ULIANA");
        // ---- Х ----
        put("\u0425\u0420\u0418\u0421\u0422\u0418\u041D\u0410", "KHRYSTYNA");
        // ---- Я ----
        put("\u042F\u041A\u0406\u0412", "YAKIV");
        put("\u042F\u041D\u0410", "YANA");
        put("\u042F\u0420\u0418\u041D\u0410", "YARYNA");
        put("\u042F\u0420\u041E\u0421\u041B\u0410\u0412", "YAROSLAV");
        // ---- Ю ----
        put("\u042E\u041B\u0406\u042F", "YULIIA");
        put("\u042E\u0420\u0406\u0419", "YURII");
    }

    private static void put(String ukrainianName, String filename) {
        NAME_TO_FILE.put(ukrainianName.toUpperCase(Locale.ROOT), filename);
        FILE_TO_NAME.putIfAbsent(filename.toUpperCase(Locale.ROOT), ukrainianName);
    }

    private NamePatternLibrary() {}

    /**
     * Returns the set of all recognised Ukrainian names (uppercase).
     */
    public static Set<String> allNames() {
        return NAME_TO_FILE.keySet();
    }

    /**
     * Tries to find a pattern file for the given text.
     * The text may be either a Ukrainian name or its Latin transliteration.
     *
     * @return the loaded {@link EmbroideryPattern}, or {@code null} if no match.
     */
    public static EmbroideryPattern findPattern(String text) {
        if (text == null || text.isBlank()) return null;

        String upper = text.strip().toUpperCase(Locale.ROOT);

        // 1. Direct Ukrainian lookup
        String filename = NAME_TO_FILE.get(upper);

        // 2. Latin transliteration lookup
        if (filename == null) {
            filename = FILE_TO_NAME.containsKey(upper) ? upper : null;
            if (filename == null) {
                // check if the input matches any file key directly
                for (String key : FILE_TO_NAME.keySet()) {
                    if (key.equalsIgnoreCase(upper)) {
                        filename = key;
                        break;
                    }
                }
            }
        }

        if (filename == null) return null;

        // Resolve path: look next to the class root / JAR, then fall back to cwd
        Path patternsDir = resolvePatternsDir();
        final String resolvedFilename = filename; // must be effectively final for lambda
        Path jsonFile = patternsDir.resolve(resolvedFilename + ".json");

        // Try case-insensitive match if exact case not found
        if (!Files.exists(jsonFile)) {
            try {
                jsonFile = Files.list(patternsDir)
                        .filter(p -> p.getFileName().toString().equalsIgnoreCase(resolvedFilename + ".json"))
                        .findFirst().orElse(null);
                if (jsonFile == null) return null;
            } catch (IOException e) {
                return null;
            }
        }

        try {
            return new PatternSerializer().load(jsonFile);
        } catch (IOException e) {
            return null;
        }
    }

    private static Path resolvePatternsDir() {
        // 1. Try next to the class files (works when running from project root)
        URL location = NamePatternLibrary.class.getProtectionDomain().getCodeSource().getLocation();
        if (location != null) {
            try {
                Path base = Paths.get(location.toURI());
                // If running from a .jar, sibling directory
                if (base.toString().endsWith(".jar")) {
                    base = base.getParent();
                }
                Path candidate = base.resolve("patterns");
                if (Files.isDirectory(candidate)) return candidate;
                // Go up one level (e.g., when classes are in out/production/...)
                candidate = base.getParent().resolve("patterns");
                if (Files.isDirectory(candidate)) return candidate;
            } catch (URISyntaxException ignored) {}
        }
        // 2. Fall back to current working directory
        return Paths.get("patterns");
    }

    private static class PatternSerializer extends serialization.PatternSerializer {}
}