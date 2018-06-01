package analyzer.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

public class AnalysisEvent extends ApplicationEvent {

    private final Type type;
    private String className;
    private String message;

    public AnalysisEvent(final Object source, final Type type) {
        super(source);
        Objects.requireNonNull(type, "Event type is not specified");
        this.type = type;
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

    @Override
    public String toString() {
        return String.format("[%s] Class %s: %s",
                getType(),
                getClassName().orElse("unknown class"),
                getMessage().orElse("unknown message"));
    }

    public enum Type {
        ERROR,
        WARNING
    }

}
