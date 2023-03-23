package shopping.mall.command;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
@Data
public class OrderCommand {

    private String id; // Please comment here if you want user to enter the id directly
    private String productId;
    private Integer qty;
    private String customerId;
    private String status;
}
