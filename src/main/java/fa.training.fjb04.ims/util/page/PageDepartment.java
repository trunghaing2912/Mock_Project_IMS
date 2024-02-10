package fa.training.fjb04.ims.util.page;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
@Data
public class PageDepartment<T>implements Serializable {

    private final Collection<T> department;
}
