package sh.jfm.springbootdemos.aiagent.adoption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
class CatAdoptionScheduler {
    private final Logger log = LoggerFactory.getLogger(CatAdoptionScheduler.class);

    @Tool(description = """
            schedule an appointment to pickup or adopt a
            cat from a Tabby Road location""")
    String schedule(int catId, String catName) {
        log.info("Scheduling adoption for cat {}", catName);
        return Instant
                .now()
                .plus(3, ChronoUnit.DAYS)
                .toString();
    }
}
