package shopping.mall.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.*;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import shopping.mall.command.*;
import shopping.mall.event.*;
import shopping.mall.query.*;

@Aggregate
@Data
@ToString
public class DeliveryAggregate {

    @AggregateIdentifier
    private String id;

    private String orderId;
    private String customerId;
    private String address;
    private String status;

    public DeliveryAggregate() {}

    @CommandHandler
    public DeliveryAggregate(StartDeliveryCommand command) {
        DeliveryStartedEvent event = new DeliveryStartedEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getId() == null) event.setId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(CancelDeliveryCommand command) {
        DeliveryCanceledEvent event = new DeliveryCanceledEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(DeliveryStartedEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(DeliveryCanceledEvent event) {
        //TODO: business logic here

    }
}
