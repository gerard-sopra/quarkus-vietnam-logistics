package sopra.steria.inventory;

import jakarta.persistence.*;
import lombok.Data;
import sopra.steria.base.Base;

import java.util.UUID;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @Enumerated(EnumType.STRING)
    @Column(name = "supply_type", nullable = false)
    private SupplyType supplyType;

    @Column(nullable = false)
    private Integer quantity;
}
