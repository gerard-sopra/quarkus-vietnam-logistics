package sopra.steria.inventory;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class InventoryRepository implements PanacheRepositoryBase<Inventory, UUID> {

    public List<Inventory> findByBaseId(UUID baseId) {
        return list("base.id", baseId);
    }
}
