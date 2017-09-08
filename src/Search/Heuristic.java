package Search;


import RushHour.RushHour;

public class Heuristic {

    public static Double zeroHeuristic(SearchNode node) {
        return 0.0;
    }

    public static Double manhattenHeuristic(SearchNode node) {
        RushHour rushHourNode = (RushHour) node;
        return (double) rushHourNode.distanceToGoal();
    }

    public static Double simpleBlockingHeuristic(SearchNode node) {
        RushHour rushHourNode = (RushHour) node;
        return (double) rushHourNode.getBlockingCars();
    }

    public static Double advancedHeuristic(SearchNode node) {
        RushHour rushHourNode = (RushHour) node;
        return (double) rushHourNode.distanceToGoal() + rushHourNode.getBlockingCars();
    }

}
