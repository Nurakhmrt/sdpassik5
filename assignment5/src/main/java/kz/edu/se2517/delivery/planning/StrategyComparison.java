package kz.edu.se2517.delivery.planning;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.strategy.RouteStrategy;
import java.util.*;

public final class StrategyComparison {
    public Map<String, RouteDecision> compare(List<RouteStrategy> strategies, List<Route> routes, DeliveryRequest request) {
        if (strategies == null || strategies.isEmpty()) throw new IllegalArgumentException("At least one strategy is required");
        Map<String, RouteDecision> result = new LinkedHashMap<>();
        for (RouteStrategy strategy : strategies) result.put(strategy.getStrategyName(), strategy.selectRoute(routes, request));
        return Collections.unmodifiableMap(result);
    }
}
