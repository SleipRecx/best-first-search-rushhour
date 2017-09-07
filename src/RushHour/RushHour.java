package RushHour;

import Search.SearchNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RushHour implements SearchNode {

    final static int ROW_COUNT = 6;
    final static int COL_COUNT = 6;

    private Double g = 0.0;
    private Double h = 0.0;

    private SearchNode parent;

    private Set<Car> cars = new LinkedHashSet<>();

    public RushHour() {}

    public RushHour(String filename) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(line -> {
                int[] quad = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
                cars.add(new Car(quad));
            });
        }
    }

    @Override
    public Set<SearchNode> generateSuccessors(){
        Set<Move> moves = new HashSet<>();
        getCars().forEach(car -> {
            Move m1 = new Move(car, car.getX(), car.getY() + 1, this);
            Move m2 = new Move(car, car.getX(), car.getY() - 1, this);
            Move m3 = new Move(car, car.getX() + 1, car.getY(), this);
            Move m4 = new Move(car, car.getX() - 1, car.getY(), this);
            if (m1.isLegal()) moves.add(m1);
            if (m2.isLegal()) moves.add(m2);
            if (m3.isLegal()) moves.add(m3);
            if (m4.isLegal()) moves.add(m4);
        });
        Set<SearchNode> successors = new HashSet<>();
        moves.forEach(move -> successors.add(performMove(move)));
        return successors;
    }

    @Override
    public SearchNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SearchNode parent) {
        this.parent = parent;
    }

    @Override
    public Double h() {
        return (double) (getBlockingCars() + distanceToGoal());
    }

    @Override
    public Double g() {
        return 1.0;
    }

    @Override
    public Boolean isSolution() {
        return getCarZero().getCoordinatesOccupied().stream()
                .filter(cord -> cord[0] == 5 && cord[1] == 2).collect(Collectors.toSet()).size() > 0;
    }

    public boolean isOpen(int x, int y) {
        return cars.stream()
                .map(Car::getCoordinatesOccupied)
                .flatMap(Collection::stream)
                .filter(cord -> cord[0] == x && cord[1] == y)
                .collect(Collectors.toSet()).isEmpty();
    }

    public int getBlockingCars() {
        List<Car> blockingCars = cars.stream().filter(car -> !car.equals(getCarZero())).filter(car -> {
            for (int[] cord: car.getCoordinatesOccupied()) {
                if (cord[1] == 2 && cord[0] > getCarZero().getX()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return blockingCars.size();
    }

    private int distanceToGoal() {
        return COL_COUNT - getCarZero().getX()+1;
    }

    public Optional<Car> getCarAt(int x, int y) {
        for (Car car: cars) {
            for (int[] cord: car.getCoordinatesOccupied()) {
                if (cord[0] == x && cord[1] == y) {
                    return Optional.of(car);
                }
            }
        }
        return Optional.empty();
    }

    private Car getCarZero() {
        return cars.iterator().next();
    }

    private Set<Car> getCars() {
        return cars;
    }

    public void print() {
        String[][] board = new String[ROW_COUNT][COL_COUNT];
        for (String[] row : board) {
            Arrays.fill(row, "*");
        }
        int counter = 0;
        for (Car car : cars) {
            for (int[] cord: car.getCoordinatesOccupied()) {
                int x = cord[0];
                int y = cord[1];
                board[y][x] = Integer.toString(counter);
            }
            counter++;
        }
        for (String[] row : board) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    private RushHour performMove(Move move) {
        RushHour newState = cloneBoard();
        newState.cars.forEach(car -> {
            if (car.getX() == move.getCar().getX() && car.getY() == move.getCar().getY()) {
                car.setX(move.getDestX());
                car.setY(move.getDestY());
            }
        });
        return newState;
    }

    private RushHour cloneBoard() {
        RushHour newState = new RushHour();
        cars.forEach(car -> {
            Car newCar = new Car(car.getQuadFormat());
            newState.getCars().add(newCar);
        });
        return newState;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof RushHour){
            RushHour toCompare = (RushHour) o;
            return this.toString().equals(toCompare.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return cars != null ? cars.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RushHour{" +
                "cars=" + cars +
                '}';
    }

    public Double getG() {
        return g;
    }

    public void setG(Double g) {
        this.g = g;
    }

    public Double getH() {
        return h;
    }

    public void setH(Double h) {
        this.h = h;
    }
}
