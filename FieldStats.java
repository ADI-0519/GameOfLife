import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state
 * of a field. It is flexible: it will create and maintain a counter
 * for any class of object that is found within the field.
 *
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class FieldStats {
    
    private HashMap<Class, Counter> counters;
    private boolean countsValid;

    /**
     * Constructs a FieldStats object.  Set up a collection for counters for
     * each type of animal that we might find
     */
    public FieldStats() {
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Gets details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field) {
        StringBuffer buffer = new StringBuffer();
        if (!countsValid) {
            generateCounts(field);
        }
        for (Class key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    /**
     * Invalidates the current set of statistics; reset all
     * counts to zero.
     */
    public void reset() {
        countsValid = false;
        for (Class key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increments the count for one class of animal
     * @param animalClass The class of animal to increment.
     */
    public void incrementCount(Class animalClass) {
        Counter count = counters.get(animalClass);

        if (count == null) {
            // We do not have a counter for this species yet. Create one.
            count = new Counter(animalClass.getName());
            counters.put(animalClass, count);
        }
        count.increment();
    }

    /**
     * Indicates that an animal count has been completed.
     */
    public void countFinished() {
        countsValid = true;
    }

    /**
     * Determines whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than one animal form alive
     */
    public boolean isViable(Field field) {
        int nonZero = 0;
        if (!countsValid) {
            generateCounts(field);
        }
        for (Class key : counters.keySet()) {
            Counter info = counters.get(key);
            if (info.getCount() > 0) {
                nonZero++;
            }
        }

        return nonZero >= 1;
    }

    /**
     * Generates counts of the number of animals.
     * These are not kept up to date.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field) {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);

                if (animal != null) {
                    incrementCount(animal.getClass());
                }
            }
        }
        countsValid = true;
    }
}