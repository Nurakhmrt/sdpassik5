package kz.edu.se2517.delivery;

import kz.edu.se2517.delivery.demo.DemoData;
import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.exception.NoSuitableRouteException;
import kz.edu.se2517.delivery.planning.*;
import kz.edu.se2517.delivery.selection.StrategySelector;
import kz.edu.se2517.delivery.strategy.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RoutePlanningTest {
    private final List<Route> routes = DemoData.routes();
    private final DeliveryRequest standard = DemoData.standardRequest();
    private Route route(String id) { return routes.stream().filter(r -> r.id().equals(id)).findFirst().orElseThrow(); }

    @Test void fastestAccountsForTrafficDelay() { assertEquals("R2",new FastestRouteStrategy().selectRoute(routes,standard).selectedRoute().id()); }
    @Test void cheapestUsesRequestSpecificPrioritySurcharge() { assertEquals("R3",new CheapestRouteStrategy().selectRoute(routes,standard).selectedRoute().id()); }
    @Test void ecoStrategyMinimizesWeightedCarbonAndDistance() { assertEquals("R3",new EcoFriendlyRouteStrategy().selectRoute(routes,standard).selectedRoute().id()); }
    @Test void balancedStrategyReturnsAnEligibleCandidate() { assertTrue(routes.stream().anyMatch(r -> r.id().equals(new BalancedRouteStrategy(.4,.3,.1,.2).selectRoute(routes,standard).selectedRoute().id()))); }
    @Test void balancedWeightsMustSumToOne() { assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(.4,.3,.1,.1)); }
    @Test void balancedRejectsNegativeWeight() { assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(-.1,.5,.3,.3)); }
    @Test void balancedRejectsNanWeight() { assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(Double.NaN,.4,.3,.3)); }
    @Test void samePlannerCanSwitchStrategiesThreeTimes() {
        RoutePlanner p=new RoutePlanner(new FastestRouteStrategy());
        p.plan(routes,standard); p.setStrategy(new EcoFriendlyRouteStrategy()); assertEquals("Eco friendly",p.plan(routes,standard).strategyName());
        p.setStrategy(new CheapestRouteStrategy()); assertEquals("Cheapest",p.plan(routes,standard).strategyName());
        p.setStrategy(new BalancedRouteStrategy(.4,.3,.1,.2)); assertEquals("Balanced",p.plan(routes,standard).strategyName());
    }
    @Test void selectorChoosesFastestForUrgent() { assertInstanceOf(FastestRouteStrategy.class,new StrategySelector().select(req(DeliveryPriority.URGENT,8000,60,false))); }
    @Test void selectorChoosesCheapestForEconomy() { assertInstanceOf(CheapestRouteStrategy.class,new StrategySelector().select(req(DeliveryPriority.ECONOMY,8000,60,false))); }
    @Test void selectorChoosesEcoForEcoPreferredStandard() { assertInstanceOf(EcoFriendlyRouteStrategy.class,new StrategySelector().select(req(DeliveryPriority.STANDARD,8000,60,true))); }
    @Test void selectorChoosesBalancedForOrdinaryStandard() { assertInstanceOf(BalancedRouteStrategy.class,new StrategySelector().select(standard)); }
    @Test void unavailableRouteCannotWinEvenWhenItWouldBeFastest() {
        RouteDecision d=new FastestRouteStrategy().selectRoute(routes,standard); assertNotEquals("R7",d.selectedRoute().id()); assertEquals("Route unavailable",d.rejectedRoutes().get("R7"));
    }
    @Test void budgetConstraintRejectsExpensiveRoutes() {
        DeliveryRequest low=req(DeliveryPriority.STANDARD,1600,60,false); RouteDecision d=new CheapestRouteStrategy().selectRoute(routes,low);
        assertTrue(d.rejectedRoutes().containsValue("Budget exceeded")); assertTrue(d.selectedRoute().totalCost(low)<=1600);
    }
    @Test void deadlineConstraintRejectsSlowRoutes() {
        DeliveryRequest tight=req(DeliveryPriority.STANDARD,8000,25,false); RouteDecision d=new FastestRouteStrategy().selectRoute(routes,tight);
        assertTrue(d.rejectedRoutes().containsValue("Deadline exceeded")); assertTrue(d.selectedRoute().effectiveMinutes()<=25);
    }
    @Test void noSuitableRouteIsExplicitFailure() { assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(routes,req(DeliveryPriority.URGENT,1,1,false))); }
    @Test void emptyRouteListIsExplicitFailure() { assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(List.of(),standard)); }
    @Test void routeRejectsNegativeDistance() { assertThrows(IllegalArgumentException.class,()->new Route("bad",DemoData.HUB,DemoData.DESTINATION,-1,10,1,1,1,0,true,true)); }
    @Test void hazardousRequestExcludesProhibitedRoute() {
        DeliveryRequest hazardous=new DeliveryRequest("H",DeliveryPriority.STANDARD,8000,60,false,true,10);
        RouteDecision d=new CheapestRouteStrategy().selectRoute(routes,hazardous); assertEquals("Hazardous cargo prohibited",d.rejectedRoutes().get("R5"));
    }
    @Test void fifthSafestStrategySelectsAValidRoute() { assertNotNull(new SafestRouteStrategy().selectRoute(routes,standard).selectedRoute()); }
    @Test void comparisonRunsMultipleAlgorithmsForSameRequest() {
        Map<String,RouteDecision> m=new StrategyComparison().compare(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy()),routes,standard);
        assertEquals(Set.of("Fastest","Cheapest"),m.keySet()); assertNotEquals(m.get("Fastest").selectedRoute(),m.get("Cheapest").selectedRoute());
    }
    @Test void decisionExplainsWinnerAndRejectedRoutes() {
        RouteDecision d=new FastestRouteStrategy().selectRoute(routes,standard); assertTrue(d.explanation().contains("traffic delay")); assertTrue(d.rejectedRoutes().containsKey("R7"));
    }
    @Test void requestRequiresPriority() { assertThrows(IllegalArgumentException.class,()->new DeliveryRequest("x",null,10,10,false,false,10)); }
    private DeliveryRequest req(DeliveryPriority p,double budget,int minutes,boolean eco) { return new DeliveryRequest("T",p,budget,minutes,eco,false,10); }
}
