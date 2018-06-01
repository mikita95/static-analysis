package analyzer.event;

import org.springframework.context.ApplicationListener;

import java.util.Collection;

public interface AnalysisEventListener extends ApplicationListener<AnalysisEvent> {
    Collection<AnalysisEvent> getAnalysisEvents();
}
