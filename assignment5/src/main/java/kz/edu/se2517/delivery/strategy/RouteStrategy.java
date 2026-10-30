package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.DeliveryRequest;
import kz.edu.se2517.delivery.domain.Route;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.List;

public interface RouteStrategy {
    RouteDecision selectRoute(List<Route> routes, DeliveryRequest request);
    String getStrategyName();
}
