package kz.edu.se2517.delivery.demo;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.planning.*;
import kz.edu.se2517.delivery.selection.StrategySelector;
import kz.edu.se2517.delivery.strategy.*;
import java.util.*;

public final class DeliveryApplication {
    public static void main(String[] args) {
        List<Route> routes = DemoData.routes();
        DeliveryRequest request = DemoData.standardRequest();
        RoutePlanner planner = new RoutePlanner(new BalancedRouteStrategy(0.40,0.30,0.10,0.20));
        List<RouteStrategy> strategies = List.of(new FastestRouteStrategy(),new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),new BalancedRouteStrategy(0.40,0.30,0.10,0.20));
        System.out.println("DELIVERY REQUEST #" + request.id());
        new StrategyComparison().compare(strategies,routes,request).forEach((name,result) ->
                System.out.printf(Locale.ROOT,"%s: %s | %d min | %.2f | %s%n",name,result.selectedRoute().id(),
                        result.selectedRoute().effectiveMinutes(),result.selectedRoute().totalCost(request),result.explanation()));
        System.out.println("\nRuntime switching on the same RoutePlanner:");
        for (RouteStrategy s : List.of(new FastestRouteStrategy(),new EcoFriendlyRouteStrategy(),new CheapestRouteStrategy())) {
            planner.setStrategy(s);
            System.out.println(planner.plan(routes,request).strategyName()+" -> "+planner.plan(routes,request).selectedRoute().id());
        }
        RouteStrategy automatic = new StrategySelector().select(new DeliveryRequest("106",DeliveryPriority.URGENT,8000,60,false,false,10));
        System.out.println("Automatic strategy for urgent request: " + automatic.getStrategyName());
    }
}
