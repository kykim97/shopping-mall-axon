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
public class OrderAggregate {

    @AggregateIdentifier
    private String id;

    private String productId;
    private Integer qty;
    private String customerId;
    private String status;

    public OrderAggregate() {}

    @CommandHandler
    public OrderAggregate(OrderCommand command) {
        OrderPlacedEvent event = new OrderPlacedEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getId() == null) event.setId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(CancelCommand command) {
        OrderCanceledEvent event = new OrderCanceledEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(OrderPlacedEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(OrderCanceledEvent event) {
        //TODO: business logic here

    }
}
