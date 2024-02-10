package fa.training.fjb04.ims.util.page;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
@Data
public class Page<T> implements Serializable {

    private final Integer totalPage;

    private final Integer pageIndex ;

    private final Collection<T> data;

    private final Integer pageSize ;

    private final String role;
}
