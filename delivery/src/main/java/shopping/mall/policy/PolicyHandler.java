package shopping.mall.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopping.mall.aggregate.*;
import shopping.mall.command.*;
import shopping.mall.event.*;

@Service
@ProcessingGroup("delivery")
public class PolicyHandler {

    @Autowired
    CommandGateway commandGateway;

    @EventHandler
    //@DisallowReplay
    public void wheneverOrderPlaced_StartDelivery(
        OrderPlacedEvent orderPlaced
    ) {
        System.out.println(orderPlaced.toString());

        StartDeliveryCommand command = new StartDeliveryCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverOrderCanceled_CencelDelivery(
        OrderCanceledEvent orderCanceled
    ) {
        System.out.println(orderCanceled.toString());

        CencelDeliveryCommand command = new CencelDeliveryCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }
}
