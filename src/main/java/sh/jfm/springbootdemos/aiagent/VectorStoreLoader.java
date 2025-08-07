package sh.jfm.springbootdemos.aiagent;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

@Configuration
public class VectorStoreLoader {
    public VectorStoreLoader(
            JdbcClient db,
            CatRepository repository,
            VectorStore vectorStore
    ) {
        if (isVectorStoreEmpty(db)) {
            addCatsToVector(repository, vectorStore);
        }
    }

    private static boolean isVectorStoreEmpty(JdbcClient db) {
        return db
                       .sql("select count(*) from vector_store")
                       .query(Integer.class)
                       .single() == 0;
    }

    private static void addCatsToVector(CatRepository repository, VectorStore vectorStore) {
        repository.findAll().forEach(cat -> vectorStore.add(
                List.of(
                        new Document("id: %s, name: %s, description: %s".formatted(
                                cat.id(), cat.name(), cat.description()
                        )))
        ));
    }
}
