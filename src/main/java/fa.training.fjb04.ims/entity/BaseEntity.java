package fa.training.fjb04.ims.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity<ID> extends AbstractAuditingEntity {
}
