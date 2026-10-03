package alabaster.hearthandharvest.config;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Stand-in for NeoForge's {@code ModConfigSpec}, which Hearth and Harvest's 1.21.1 {@code Config}
 * was written against. Same shape ({@code Builder.comment(..).define(..)} /
 * {@code defineInRange(..)}, values read with {@code get()}), so every option, default, range and
 * comment carries over unchanged.
 * <p>
 * Stored as {@code config/hearthandharvest-common.toml} (the file name NeoForge used): one
 * {@code "key" = value} line per option with its comment above. Loaded once at startup; missing or
 * invalid entries fall back to the default (out-of-range numbers are clamped) and the file is
 * rewritten so new options appear in it.
 */
public final class HHConfigSpec {
    private final List<Value<?>> values;

    private HHConfigSpec(List<Value<?>> values) {
        this.values = values;
    }

    public abstract static class Value<T> implements Supplier<T> {
        final String key;
        final String comment;
        final T defaultValue;
        T value;

        Value(String key, String comment, T defaultValue) {
            this.key = key;
            this.comment = comment;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        @Override
        public T get() {
            return value;
        }

        public T getDefault() {
            return defaultValue;
        }

        abstract T parse(String raw);

        abstract String range();
    }

    public static final class BooleanValue extends Value<Boolean> {
        BooleanValue(String key, String comment, boolean defaultValue) {
            super(key, comment, defaultValue);
        }

        @Override
        Boolean parse(String raw) {
            if (raw.equalsIgnoreCase("true")) return true;
            if (raw.equalsIgnoreCase("false")) return false;
            throw new IllegalArgumentException(raw);
        }

        @Override
        String range() {
            return null;
        }
    }

    public static final class IntValue extends Value<Integer> {
        private final int min;
        private final int max;

        IntValue(String key, String comment, int defaultValue, int min, int max) {
            super(key, comment, defaultValue);
            this.min = min;
            this.max = max;
        }

        @Override
        Integer parse(String raw) {
            return Math.max(min, Math.min(max, Integer.parseInt(raw)));
        }

        @Override
        String range() {
            return min + " ~ " + max;
        }
    }

    public static final class DoubleValue extends Value<Double> {
        private final double min;
        private final double max;

        DoubleValue(String key, String comment, double defaultValue, double min, double max) {
            super(key, comment, defaultValue);
            this.min = min;
            this.max = max;
        }

        @Override
        Double parse(String raw) {
            return Math.max(min, Math.min(max, Double.parseDouble(raw)));
        }

        @Override
        String range() {
            return min + " ~ " + max;
        }
    }

    public static final class Builder {
        private final List<Value<?>> values = new ArrayList<>();
        private String pendingComment = "";

        public Builder comment(String comment) {
            this.pendingComment = comment;
            return this;
        }

        private <V extends Value<?>> V add(V value) {
            values.add(value);
            pendingComment = "";
            return value;
        }

        public BooleanValue define(String key, boolean defaultValue) {
            return add(new BooleanValue(key, pendingComment, defaultValue));
        }

        public IntValue defineInRange(String key, int defaultValue, int min, int max) {
            return add(new IntValue(key, pendingComment, defaultValue, min, max));
        }

        public DoubleValue defineInRange(String key, double defaultValue, double min, double max) {
            return add(new DoubleValue(key, pendingComment, defaultValue, min, max));
        }

        public HHConfigSpec build() {
            return new HHConfigSpec(List.copyOf(values));
        }
    }

    /** Reads {@code config/<fileName>}, applying stored values, then writes it back complete. */
    public void load(String fileName) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        Map<String, String> stored = new HashMap<>();
        if (Files.exists(path)) {
            try {
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    String trimmed = line.strip();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                    int eq = trimmed.indexOf('=');
                    if (eq < 0) continue;
                    String key = trimmed.substring(0, eq).strip();
                    if (key.length() >= 2 && key.startsWith("\"") && key.endsWith("\"")) {
                        key = key.substring(1, key.length() - 1);
                    }
                    stored.put(key, trimmed.substring(eq + 1).strip());
                }
            } catch (IOException e) {
                HearthAndHarvest.LOGGER.warn("Could not read {}, using defaults", path, e);
            }
        }
        StringBuilder out = new StringBuilder();
        for (Value<?> value : values) {
            String raw = stored.get(value.key);
            if (raw != null) {
                try {
                    setParsed(value, raw);
                } catch (RuntimeException e) {
                    HearthAndHarvest.LOGGER.warn("Invalid value '{}' for {} in {}, using default", raw, value.key, fileName);
                }
            }
            for (String commentLine : value.comment.split("\n")) {
                if (!commentLine.isBlank()) out.append("# ").append(commentLine.strip()).append('\n');
            }
            if (value.range() != null) out.append("# Range: ").append(value.range()).append('\n');
            out.append('"').append(value.key).append("\" = ").append(value.get()).append("\n\n");
        }
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, out.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            HearthAndHarvest.LOGGER.warn("Could not write {}", path, e);
        }
    }

    private static <T> void setParsed(Value<T> value, String raw) {
        value.value = value.parse(raw);
    }
}
