package analyzer.event;

import com.github.javaparser.Range;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

public class AnalysisEvent extends ApplicationEvent {

    private final Type type;
    private String className;
    private String message;
    private Range range;

    public AnalysisEvent(final Object source, final Type type) {
        super(source);
        this.type = Objects.requireNonNull(type, "Event type is not specified");
    }

    public Optional<String> getClassName() {
        return Optional.ofNullable(className);
    }

    public AnalysisEvent setClassName(@Nullable final String className) {
        this.className = className;
        return this;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public AnalysisEvent setMessage(@Nullable final String message) {
        this.message = message;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Optional<Range> getRange() {
        return Optional.ofNullable(range);
    }

    public AnalysisEvent setRange(@Nullable final Range range) {
        this.range = range;
        return this;
    }

    @Override
    public String toString() {
        return String.format("[%s] Class %s%s: %s",
                getType(),
                getClassName().orElse("unknown class"),
                getRange().map(range -> " at position " + range).orElse(""),
                getMessage().orElse("unknown message"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisEvent that = (AnalysisEvent) o;
        return type == that.type &&
                Objects.equals(className, that.className) &&
                Objects.equals(message, that.message) &&
                Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, className, message, range);
    }

    public enum Type {
        ERROR,
        WARNING,
        INFO
    }

}
