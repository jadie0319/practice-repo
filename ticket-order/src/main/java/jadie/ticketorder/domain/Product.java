package jadie.ticketorder.domain;

import jakarta.persistence.*;
import org.springframework.web.bind.annotation.BindParam;

import java.util.Set;

@Entity
public class Product {
    @Id
    private Long id;

    @ElementCollection
    @CollectionTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"))
    private Set<CategoryId> categoryIdSet;
}
