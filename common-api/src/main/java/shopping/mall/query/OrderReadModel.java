package shopping.mall.query;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Entity
@Table(name = "Order_table")
@Data
@Relation(collectionRelation = "orders")
public class OrderReadModel {

    @Id
    private String id;

    private String productId;

    private Integer qty;

    private String customerId;

    private String status;
}
