package kz.edu.se2517.delivery.planning;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.strategy.RouteStrategy;
import java.util.*;

public final class RoutePlanner {
    private RouteStrategy strategy;
    public RoutePlanner(RouteStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(RouteStrategy strategy) { this.strategy = Objects.requireNonNull(strategy, "Strategy is required"); }
    public RouteStrategy getStrategy() { return strategy; }
    public RouteDecision plan(List<Route> routes, DeliveryRequest request) { return strategy.selectRoute(routes, request); }
}
