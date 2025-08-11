package sh.jfm.springbootdemos.aiagent;

import org.springframework.data.repository.ListCrudRepository;

public interface CatRepository extends ListCrudRepository<Cat, Integer> {
}
