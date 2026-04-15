/**
 * Represents a location in the Vehicle Routing Problem, extending to both the depot and customers.
 */
public class Customer {
    private final int id;
    private final double x;
    private final double y;
    private final int demand;

    public Customer(int id, double x, double y, int demand) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDemand() {
        return demand;
    }

    /**
     * Calculates the Euclidean distance from this customer to another customer.
     *
     * @param other The other customer.
     * @return The distance to the other customer.
     */
    public double distanceTo(Customer other) {
        int dx = (int)(this.x - other.x);
        int dy = (int)(this.y - other.y);
        // Using Math.round to often match CVRP instances integer distance requirements,
        // although real euclidean can be used depending on the instance type.
        // For standard CVRPLIB, it is usually 2D Euclidean rounded to nearest integer or exact euclidean.
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", demand=" + demand +
                '}';
    }
}
