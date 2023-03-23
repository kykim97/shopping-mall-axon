package shopping.mall.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import shopping.mall.query.*;

@RestController
public class DeliveryViewQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public DeliveryViewQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/deliveries")
    public CompletableFuture findAll(DeliveryViewQuery query) {
        return queryGateway
            .query(
                query,
                ResponseTypes.multipleInstancesOf(DeliveryReadModel.class)
            )
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<DeliveryReadModel>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<DeliveryReadModel> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/deliveries/{id}")
    public CompletableFuture findById(@PathVariable("id") String id) {
        DeliveryViewSingleQuery query = new DeliveryViewSingleQuery();
        query.setId(id);

        return queryGateway
            .query(
                query,
                ResponseTypes.optionalInstanceOf(DeliveryReadModel.class)
            )
            .thenApply(resource -> {
                if (!resource.isPresent()) {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(
                    hateoas(resource.get()),
                    HttpStatus.OK
                );
            })
            .exceptionally(ex -> {
                throw new RuntimeException(ex);
            });
    }

    EntityModel<DeliveryReadModel> hateoas(DeliveryReadModel resource) {
        EntityModel<DeliveryReadModel> model = EntityModel.of(resource);

        model.add(Link.of("/deliveries/" + resource.getId()).withSelfRel());

        model.add(
            Link
                .of("/deliveries/" + resource.getId() + "/canceldelivery")
                .withRel("canceldelivery")
        );

        model.add(
            Link
                .of("/deliveries/" + resource.getId() + "/events")
                .withRel("events")
        );

        return model;
    }

    @MessageMapping("deliveries.all")
    public Flux<DeliveryReadModel> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new DeliveryViewQuery(),
            DeliveryReadModel.class
        );
    }

    @MessageMapping("deliveries.{id}.get")
    public Flux<DeliveryReadModel> subscribeSingle(
        @DestinationVariable String id
    ) {
        DeliveryViewSingleQuery query = new DeliveryViewSingleQuery();
        query.setId(id);

        return reactorQueryGateway.subscriptionQuery(
            query,
            DeliveryReadModel.class
        );
    }
}
