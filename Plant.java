import java.util.List;
import javafx.scene.paint.Color; 
import java.util.Random;

/**
 * A class representing plants and their behaviours.
 *
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Plant extends Animal
{
    private int age;
    private static final int MAX_AGE = 100;
    private int count; 
    private int bites;
    private double SEED_PROBABILITY = 0.05;
    private int MAX_PLANT_COUNT = 3000; // limits plants to 3000
    private int PLANT_COUNT;
    
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for objects of class Plant
     */
    public Plant(Field field, Location location, Color col, String sex, boolean firstGeneration, String parentGene) {
        super(field, location, col, null, false, null);
        age = 0;
        count = 0;
        bites = 0;
        PLANT_COUNT = 5000;
    }   
    
    /**
     * After five generations, plants regrow.
     * @param newPlants A list to return newly grown plants.
     */
    public void act(List<Animal> newPlants) {
        incrementAge();
        count++;
        
        if (isAlive()){
            pollinate(newPlants);
        }
        
        //ensure plant grows every 5 timesteps
        
        if (count>=5) {
            grow();
            count = 0;
        }
    }
    
    /**
     * Increases the age.
     * This could result in the plant's death.
     */
    private void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead();
            System.out.println("Died of old age");
        }
    }
    
    /**
     * Increases the number of bites taken out of the plant.
     */
    protected void incrementBites() {
        bites++;
        if(bites > 10) {
            setDead();
        }
    }
    
    /**
     * Grows the plant by a bit.
     */
    public void grow() {
        bites = bites - 1;
    }
    
    /**
     * Kills the plant.
     */
    @Override
    public void setDead() {
        Location location = getLocation();
        Field field = getField();
        setStatus(false);
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Has the plants undergo asexual reproduction.
     * @param newPlants A list to return newly born plants.
     */
    private void pollinate(List<Animal> newPlants) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = numberOfSeeds();
        
        if (PLANT_COUNT >= MAX_PLANT_COUNT){
            return;
        }
        
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            List<Animal> neighbors = field.getLivingNeighbours(getLocation());
            Plant youngPlant = new Plant(field, getLocation(), Color.GREEN, null, false, null);
            newPlants.add(youngPlant);
        }
    }

    /**
     * Generates a number representing the number of seeds created if reproduction is possible.
     * @return The number of seeds.
     */
    private int numberOfSeeds() {
        int seeds = 0;
        if(rand.nextDouble() <= SEED_PROBABILITY) {
            seeds = rand.nextInt(3) + 1;
        }
        return seeds;
    }
    
    /**
     * Sets the number of existing plants.
     */
    public void setPlantPopulation(int count){
        PLANT_COUNT = count;
    }

}