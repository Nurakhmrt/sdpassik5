package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.List;

public final class FastestRouteStrategy extends AbstractRouteStrategy {
    @Override public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        return decide(routes, request, Route::effectiveMinutes,
                r -> "Estimated time " + r.estimatedMinutes() + " min + traffic delay " + r.trafficDelayMinutes() + " min");
    }
    @Override public String getStrategyName() { return "Fastest"; }
}
