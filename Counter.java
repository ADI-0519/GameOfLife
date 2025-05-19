/**
 * Provides a counter for a participant in the simulation.
 * This includes an identifying string and a count of how
 * many participants of this type currently exist within
 * the simulation.
 *
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Counter {
    
    private String name;
    private int count;

    /**
     * Provides a name for one of the simulation types.
     * @param name  A class of animal
     */
    public Counter(String name) {
        this.name = name;
        count = 0;
    }

    /**
     * @return The short description of this type.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The current count for this type.
     */
    public int getCount() {
        return count;
    }

    /**
     * Increments the current count by one.
     */
    public void increment() {
        count++;
    }

    /**
     * Resets the current count to zero.
     */
    public void reset() {
        count = 0;
    }
}