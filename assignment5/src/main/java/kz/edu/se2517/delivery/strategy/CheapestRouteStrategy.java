package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.List;
import java.util.Locale;

public final class CheapestRouteStrategy extends AbstractRouteStrategy {
    @Override public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        return decide(routes, request, r -> r.totalCost(request), r -> String.format(Locale.ROOT,
                "Cost = base %.2f + distance %.1f km × %.2f/km + priority surcharge %.2f = %.2f",
                r.baseCost(), r.distanceKm(), r.costPerKm(), request.priority().surcharge(), r.totalCost(request)));
    }
    @Override public String getStrategyName() { return "Cheapest"; }
}
