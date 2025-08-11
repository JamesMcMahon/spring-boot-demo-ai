package sh.jfm.springbootdemos.aiagent;

import org.springframework.data.annotation.Id;

public record Cat(@Id int id, String name, String description) {
}
