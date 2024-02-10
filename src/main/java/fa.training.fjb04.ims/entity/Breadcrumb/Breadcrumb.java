package fa.training.fjb04.ims.entity.Breadcrumb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Component
public class Breadcrumb {
    private String name;
    private String url;
}
