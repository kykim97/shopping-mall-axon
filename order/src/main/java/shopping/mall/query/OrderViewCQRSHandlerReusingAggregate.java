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
@ProcessingGroup("orderView")
public class OrderViewCQRSHandlerReusingAggregate {

    @Autowired
    private OrderReadModelRepository repository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public List<OrderReadModel> handle(OrderViewQuery query) {
        return repository.findAll();
    }

    @QueryHandler
    public Optional<OrderReadModel> handle(OrderViewSingleQuery query) {
        return repository.findById(query.getId());
    }

    @EventHandler
    public void whenOrderPlaced_then_CREATE(OrderPlacedEvent event)
        throws Exception {
        OrderReadModel entity = new OrderReadModel();
        OrderAggregate aggregate = new OrderAggregate();
        aggregate.on(event);

        BeanUtils.copyProperties(aggregate, entity);

        repository.save(entity);

        queryUpdateEmitter.emit(OrderViewQuery.class, query -> true, entity);
    }

    @EventHandler
    public void whenOrderCanceled_then_UPDATE(OrderCanceledEvent event)
        throws Exception {
        repository
            .findById(event.getId())
            .ifPresent(entity -> {
                OrderAggregate aggregate = new OrderAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                queryUpdateEmitter.emit(
                    OrderViewSingleQuery.class,
                    query -> query.getId().equals(event.getId()),
                    entity
                );
            });
    }
}
