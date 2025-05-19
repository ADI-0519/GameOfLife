import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;
import java.util.Iterator;

/**
 * A simple model of a deer.
 * Deers age, move, breed, eat plants, contract disease, and die.
 * 
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Deer extends Animal {

    public static int BREEDING_AGE;
    public static int MAX_AGE;
    public static double BREEDING_PROBABILITY;
    public static double DISEASE_PROBABILITY;
    public static double METABOLISM;
    public static int MAX_LITTER_SIZE;
    public static int PLANT_FOOD_VALUE = 3;
    public static int MAX_FOOD_LEVEL = 20;

    private static final Random rand = Randomizer.getRandom();
    
    private int age;
    private double foodLevel;
    private boolean disease;
    private int disease_count;

    /**
     * Create a new squirrel. A deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param color The color the deer is represented as.
     * @param sex The gender of the deer.
     * @param firstGeneration If true, the deer will have a random gene set.
     * @param parentGene The parent gene of the deer.
     */
    public Deer(boolean randomAge, Field field, Location location, Color col, String sex, boolean firstGeneration, String parentGene) {
        super(field, location, col, sex, firstGeneration, parentGene);
        this.disease = false;
        foodLevel = MAX_FOOD_LEVEL;
        
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }
    
    /**
     * This is what the deer does most of the time - it runs 
     * around. 
     * Sometimes it will breed, die of old age, die of disease, or starve to death.
     * @param newDeers A list to return newly born deers.
     */
    public void act(List<Animal> newDeers) {
        incrementAge();
        incrementHunger();
        
        if(isAlive()) {
            giveBirth(newDeers);            
            // Try to move into a free location.
            Location newLocation = findFood();
        
            if(newLocation == null) {
                newLocation = getField().getFreeAdjacentLocation(getLocation());
            }
            
            // Try to move into a free location
            
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
            
            if (!(disease)){
                tryGainDisease();
            }
            else{
                spreadDisease();
                incrementDiseaseCount();
            }
            
        }
    }

    /**
     * Increases the age.
     * This could result in the deer's death.
     */
    private void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Decreases food level.
     * This could result in the deer's death.
     */
    private void incrementHunger() {
        foodLevel = foodLevel - METABOLISM;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Increases the disease count.
     * This could result in the deer's death.
     */
    private void incrementDiseaseCount(){
        disease_count++;
        if (disease_count >= 20){
            setDead();
        }
    }
    
    /**
     * Could gain disease.
     */
    private void tryGainDisease(){
        if (rand.nextDouble() <= DISEASE_PROBABILITY){
            disease = true;
            disease_count = 0;
        }
    }
        
    /**
     * Spreads disease to others.
     */
    private void spreadDisease(){
        Field field = getField();
        List<Animal> neighbours = field.getLivingNeighbours(getLocation());
        
        for (Animal neighbour : neighbours) {
            if (neighbour instanceof Deer) {
                Deer sNeighbour = (Deer) neighbour;
                if (sNeighbour.isAlive()) {
                    sNeighbour.tryGainDisease();
                }
            }
        }
    }
    
    /**
     * Finds food in adjacent locations.
     * @return Location gives location of where to find the food.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Plant) {
                Plant p = (Plant) animal;
                if(p.isAlive()) { 
                    p.incrementBites();
                    eat();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Ensures max limit of foodLevel is maintained.
     */
    private void eat(){
        if (foodLevel < MAX_FOOD_LEVEL - PLANT_FOOD_VALUE){
            foodLevel += PLANT_FOOD_VALUE;
        }
        else {
            foodLevel = MAX_FOOD_LEVEL;
        }
    }
    
    /**
     * @return Returns whether or not the prey successfully flees from the predator.
     */
    public boolean flee() {
        if (rand.nextDouble() > 0.4) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Check whether or not this deer is to give birth at this step.
     * New births will be made into free adjacent locations. Checks deer is of opposite gender and mixes the genes.
     * @param newDeers A list to return newly born deers.
     */
    private void giveBirth(List<Animal> newDeers) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            List<Animal> neighbors = field.getLivingNeighbours(getLocation());
    
            Deer mate = null;
            for (Animal neighbor : neighbors) {
                if (neighbor instanceof Deer && !this.getSex().equals(neighbor.getSex())) { 
                    mate = (Deer) neighbor;
                    break;
                }
            }
    
            if (mate != null) {
                String parent1Gene = this.getGene();
                String parent2Gene = mate.getGene();
                String newGene = crossoverGenes(parent1Gene, parent2Gene);
                
                newGene = mutateGene(newGene);
    
                boolean isMale = Math.random() < 0.5;
    
                Deer young = new Deer(false, field, loc, getColor(), isMale ? "Male" : "Female", false, newGene);
                newDeers.add(young);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * A deer can breed if it has reached the breeding age.
     * @return true if the Deer can breed, false otherwise.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Takes the integers from the 14 digit gene string and assigns them as the deer's life cycle values.
     */
    @Override
    public void parseGene(String gene) {
        BREEDING_AGE = Integer.parseInt(gene.substring(0, 2));
        MAX_AGE = Integer.parseInt(gene.substring(2, 5));
        BREEDING_PROBABILITY = (Integer.parseInt(gene.substring(5, 7))) / 100.0;
        MAX_LITTER_SIZE = Integer.parseInt(gene.substring(7, 9));
        DISEASE_PROBABILITY = Integer.parseInt(gene.substring(9, 11)) / 100.0;
        METABOLISM = (Integer.parseInt(gene.substring(11, 14))) / 100.0;
    }
}