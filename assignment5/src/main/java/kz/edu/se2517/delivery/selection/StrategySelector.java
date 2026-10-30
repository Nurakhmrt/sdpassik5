package kz.edu.se2517.delivery.selection;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.strategy.RouteStrategy;
import java.util.*;
import java.util.function.Function;

public final class StrategySelector {
    private final Map<DeliveryPriority, Function<DeliveryRequest, RouteStrategy>> rules = new EnumMap<>(DeliveryPriority.class);
    public StrategySelector() {
        register(DeliveryPriority.URGENT, r -> new kz.edu.se2517.delivery.strategy.FastestRouteStrategy());
        register(DeliveryPriority.ECONOMY, r -> new kz.edu.se2517.delivery.strategy.CheapestRouteStrategy());
        register(DeliveryPriority.STANDARD, r -> r.ecoPreferred()
                ? new kz.edu.se2517.delivery.strategy.EcoFriendlyRouteStrategy()
                : new kz.edu.se2517.delivery.strategy.BalancedRouteStrategy(0.40,0.30,0.10,0.20));
    }
    public void register(DeliveryPriority priority, Function<DeliveryRequest, RouteStrategy> factory) {
        rules.put(Objects.requireNonNull(priority), Objects.requireNonNull(factory));
    }
    public RouteStrategy select(DeliveryRequest request) {
        Objects.requireNonNull(request, "Delivery request is required");
        Function<DeliveryRequest, RouteStrategy> factory = rules.get(request.priority());
        if (factory == null) throw new IllegalStateException("No strategy rule for " + request.priority());
        return Objects.requireNonNull(factory.apply(request), "Strategy rule returned null");
    }
}
