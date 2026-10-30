package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.DeliveryRequest;
import kz.edu.se2517.delivery.domain.Route;
import kz.edu.se2517.delivery.exception.NoSuitableRouteException;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.*;
import java.util.function.ToDoubleFunction;

abstract class AbstractRouteStrategy implements RouteStrategy {
    protected record Candidate(Route route, double score, String reason) {}
    protected List<Route> validateInput(List<Route> routes, DeliveryRequest request) {
        Objects.requireNonNull(request, "Delivery request is required");
        if (routes == null || routes.isEmpty()) throw new NoSuitableRouteException("No candidate routes were provided");
        if (routes.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Route list cannot contain null");
        return routes;
    }
    protected String rejection(Route route, DeliveryRequest request) {
        if (!route.available()) return "Route unavailable";
        if (request.hazardousCargo() && !route.hazardousCargoAllowed()) return "Hazardous cargo prohibited";
        if (route.totalCost(request) > request.maxBudget()) return "Budget exceeded";
        if (route.effectiveMinutes() > request.maxDeliveryMinutes()) return "Deadline exceeded";
        if (route.carbonEmissionKg() > request.maxEmissionKg()) return "Emission limit exceeded";
        return null;
    }
    protected RouteDecision decide(List<Route> routes, DeliveryRequest request, ToDoubleFunction<Route> score,
                                  java.util.function.Function<Route, String> reason) {
        validateInput(routes, request);
        List<Candidate> eligible = new ArrayList<>();
        Map<String, String> rejected = new LinkedHashMap<>();
        for (Route route : routes) {
            String rejection = rejection(route, request);
            if (rejection != null) rejected.put(route.id(), rejection);
            else eligible.add(new Candidate(route, score.applyAsDouble(route), reason.apply(route)));
        }
        Candidate winner = eligible.stream().min(Comparator.comparingDouble(Candidate::score).thenComparing(c -> c.route().id()))
                .orElseThrow(() -> new NoSuitableRouteException("No route satisfies the delivery constraints"));
        return new RouteDecision(winner.route(), getStrategyName(), winner.reason(),
                List.of("Score: " + String.format(Locale.ROOT, "%.3f", winner.score())), rejected);
    }
}
