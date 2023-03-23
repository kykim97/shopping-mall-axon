package shopping.mall.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopping.mall.aggregate.*;
import shopping.mall.event.*;

@Service
@ProcessingGroup("deliveryView")
public class DeliveryViewCQRSHandlerReusingAggregate {

    @Autowired
    private DeliveryReadModelRepository repository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public List<DeliveryReadModel> handle(DeliveryViewQuery query) {
        return repository.findAll();
    }

    @QueryHandler
    public Optional<DeliveryReadModel> handle(DeliveryViewSingleQuery query) {
        return repository.findById(query.getId());
    }

    @EventHandler
    public void whenDeliveryStarted_then_CREATE(DeliveryStartedEvent event)
        throws Exception {
        DeliveryReadModel entity = new DeliveryReadModel();
        DeliveryAggregate aggregate = new DeliveryAggregate();
        aggregate.on(event);

        BeanUtils.copyProperties(aggregate, entity);

        repository.save(entity);

        queryUpdateEmitter.emit(DeliveryViewQuery.class, query -> true, entity);
    }

    @EventHandler
    public void whenDeliveryCanceled_then_UPDATE(DeliveryCanceledEvent event)
        throws Exception {
        repository
            .findById(event.getId())
            .ifPresent(entity -> {
                DeliveryAggregate aggregate = new DeliveryAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                queryUpdateEmitter.emit(
                    DeliveryViewSingleQuery.class,
                    query -> query.getId().equals(event.getId()),
                    entity
                );
            });
    }
}
