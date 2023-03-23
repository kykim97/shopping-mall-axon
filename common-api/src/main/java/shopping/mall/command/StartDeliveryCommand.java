package shopping.mall.command;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
@Data
public class StartDeliveryCommand {

    private String id; // Please comment here if you want user to enter the id directly
    private String orderId;
    private String customerId;
    private String address;
    private String status;
}
