import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.paint.Color; 

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Simulator {

    private static final double CAPYBARA_CREATION_PROBABILITY = 0.10;
    private static final double JAGUAR_CREATION_PROBABILITY = 0.020;
    private static final double DEER_CREATION_PROBABILITY = 0.15;
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.05;
    private static final double CROCODILE_CREATION_PROBABILITY = 0.015;
    
    private List<Animal> animals;
    private Field field;
    private int step;
    
    /**
     * Creates a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        reset();
    }
    
    /**
     * Runs the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each animal.
     */
    public void simulateOneStep() {
        step++;
        List<Animal> newAnimals = new ArrayList<>();   

        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        updatePlantPopulation(field.getPlant());
        
        animals.addAll(newAnimals);
    }
    
    /**
     * Updates the plant population.
     */
    public void updatePlantPopulation(int count) {
        for (Animal animal : animals) {
            if (animal instanceof Plant) {
                ((Plant) animal).setPlantPopulation(count);
            }
        }
    }
    
    /**
     * Resets the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        animals.clear();
        populate();
    }
    
    /**
     * Randomly populates the field with animals.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CROCODILE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Crocodile crocodile = new Crocodile(true, field, location, Color.DARKGREEN, Animal.getSex(), true, null);
                    animals.add(crocodile);
                }
                else if(rand.nextDouble() <= JAGUAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Jaguar jaguar = new Jaguar(true, field, location, Color.GOLD, Animal.getSex(), true, null);
                    animals.add(jaguar);
                }
                else if(rand.nextDouble() <= CAPYBARA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Capybara capybara = new Capybara(true, field, location, Color.SADDLEBROWN, Animal.getSex(), true, null);
                    animals.add(capybara);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location, Color.PERU, Animal.getSex(), true, null);
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, field, location, Color.DIMGRAY, Animal.getSex(), true, null);
                    animals.add(squirrel);
                }
                else{
                    Location location = new Location(row, col);
                    Plant plant = new Plant(field, location, Color.FORESTGREEN, null, false, null);
                    animals.add(plant);
                }
            }
        }

    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    public void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
    
    public Field getField() {
        return field;
    }

    public int getStep() {
        return step;
    }
}