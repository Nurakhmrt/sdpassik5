package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.*;

public final class BalancedRouteStrategy extends AbstractRouteStrategy {
    private final double timeWeight, costWeight, distanceWeight, emissionWeight;
    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        double[] w = {timeWeight, costWeight, distanceWeight, emissionWeight};
        if (Arrays.stream(w).anyMatch(x -> !Double.isFinite(x) || x < 0) || Math.abs(Arrays.stream(w).sum() - 1.0) > 1e-9)
            throw new IllegalArgumentException("Balanced weights must be finite, non-negative, and sum to 1.0");
        this.timeWeight=timeWeight; this.costWeight=costWeight; this.distanceWeight=distanceWeight; this.emissionWeight=emissionWeight;
    }
    private double min(List<Route> rs, java.util.function.ToDoubleFunction<Route> f) { return rs.stream().mapToDouble(f).min().orElse(0); }
    private double max(List<Route> rs, java.util.function.ToDoubleFunction<Route> f) { return rs.stream().mapToDouble(f).max().orElse(0); }
    private double norm(double v, double lo, double hi) { return hi == lo ? 0 : (v - lo) / (hi - lo); }
    @Override public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        validateInput(routes, request);
        List<Route> eligible = routes.stream().filter(r -> rejection(r, request) == null).toList();
        double t0=min(eligible,Route::effectiveMinutes), t1=max(eligible,Route::effectiveMinutes);
        double c0=min(eligible,r->r.totalCost(request)), c1=max(eligible,r->r.totalCost(request));
        double d0=min(eligible,Route::distanceKm), d1=max(eligible,Route::distanceKm);
        double e0=min(eligible,Route::carbonEmissionKg), e1=max(eligible,Route::carbonEmissionKg);
        return decide(routes, request, r -> score(r,request,t0,t1,c0,c1,d0,d1,e0,e1),
                r -> String.format(Locale.ROOT,"Normalized weighted score %.3f (time %.2f, cost %.2f, distance %.2f, emissions %.2f)", score(r,request,t0,t1,c0,c1,d0,d1,e0,e1),timeWeight,costWeight,distanceWeight,emissionWeight));
    }
    private double score(Route r, DeliveryRequest request,double t0,double t1,double c0,double c1,double d0,double d1,double e0,double e1) {
        return timeWeight*norm(r.effectiveMinutes(),t0,t1)+costWeight*norm(r.totalCost(request),c0,c1)+distanceWeight*norm(r.distanceKm(),d0,d1)+emissionWeight*norm(r.carbonEmissionKg(),e0,e1);
    }
    @Override public String getStrategyName() { return "Balanced"; }
}
