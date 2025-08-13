package sh.jfm.springbootdemos.aiagent.adoption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

/// Simple example of Retrieval Augmented Generation (R.A.G.). Cats are loaded into the vector store so the model
/// can suggest the right cats to adopt.
///
/// Loads data from the database into the vector store.
@Configuration
public class VectorStoreLoader {
    public final Logger log = LoggerFactory.getLogger(getClass());

    public VectorStoreLoader(
            JdbcClient db,
            CatRepository repository,
            VectorStore vectorStore
    ) {
        if (!isVectorStoreEmpty(db)) {
            return;
        }

        log.info("Initializing vector store with data from database");
        addCatsToVectorStore(repository, vectorStore);
    }

    private static boolean isVectorStoreEmpty(JdbcClient db) {
        return db
                       .sql("select count(*) from vector_store")
                       .query(Integer.class)
                       .single() == 0;
    }

    private static void addCatsToVectorStore(CatRepository repository, VectorStore vectorStore) {
        repository.findAll().forEach(cat -> vectorStore.add(
                List.of(
                        new Document("id: %s, name: %s, description: %s".formatted(
                                cat.id(), cat.name(), cat.description()
                        )))
        ));
    }
}
