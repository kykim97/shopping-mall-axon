package shopping.mall.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderPlacedEvent {

    private String id;
    private String productId;
    private Integer qty;
    private String customerId;
    private String status;
}
