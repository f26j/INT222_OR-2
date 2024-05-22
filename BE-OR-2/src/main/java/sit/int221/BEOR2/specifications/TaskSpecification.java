package sit.int221.BEOR2.specifications;

import org.springframework.data.jpa.domain.Specification;
import sit.int221.BEOR2.entities.Task;

import java.util.List;

public class TaskSpecification {
    public static Specification<Task> hasStatuses(List<String> statuses) {
        return (root, query, criteriaBuilder) -> root.get("status").get("name").in(statuses);
    }
}
