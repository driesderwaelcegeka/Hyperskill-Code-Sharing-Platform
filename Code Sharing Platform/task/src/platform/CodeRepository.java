package platform;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends CrudRepository<Code, Long> {
    Code findCodeById(Long id);

    Code findCodeByUuid(String uuid);

    List<Code> findAll();

    List<Code> findAllByTimeRestrictedAndViewsRestricted(boolean timeRestricted, boolean viewsRestricted);
}
