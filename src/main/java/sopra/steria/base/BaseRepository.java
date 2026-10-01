package sopra.steria.base;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class BaseRepository implements PanacheRepositoryBase<Base, UUID> {
}
