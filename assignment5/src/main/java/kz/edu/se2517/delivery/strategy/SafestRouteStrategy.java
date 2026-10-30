package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.List;

/** Prefers short travel time and low carbon impact while respecting the same hard constraints. */
public final class SafestRouteStrategy extends AbstractRouteStrategy {
    @Override public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        return decide(routes, request, r -> r.effectiveMinutes() * 0.6 + r.carbonEmissionKg() * 10.0,
                r -> "Safety proxy score = 0.6 × " + r.effectiveMinutes() + " min + 10 × " + r.carbonEmissionKg() + " kg CO2; lowest wins");
    }
    @Override public String getStrategyName() { return "Safest"; }
}
