package jadie.ticketorder.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Immutable
@Subselect(
        """
            select 
                o.order_number as id,
                o.orderer_name as ordererName,
            from purchase_order o inner join order_line ol
                on o.order_number = ol.order_number
                cross join product p
            where
                ol.line_idx = 0
                and ol.product_id = p.product_id
            
        """
)
@Synchronize({"purchase_order", "order_line", "product"})
public class OrderSummaryView {
    @Id
    private Long id;
    private String name;

}
