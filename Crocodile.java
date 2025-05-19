import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.LinkedList;
import javafx.scene.paint.Color; 

/**
 * A simple model of a crocodile.
 * Crocodiles age, move, breed, eat prey, contract disease, and die.
 * 
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Crocodile extends Animal {

    private static int BREEDING_AGE;
    private static int MAX_AGE;
    private static double BREEDING_PROBABILITY;
    private static double DISEASE_PROBABILITY;
    private static double METABOLISM;
    private static int MAX_LITTER_SIZE;
    private static int SQUIRREL_FOOD_VALUE = 8;
    private static int CAPYBARA_FOOD_VALUE = 12;
    private static int DEER_FOOD_VALUE = 15;
    private static int MAX_FOOD_LEVEL = 35;
    private static Random rand = Randomizer.getRandom();
    
    private int age;
    private double foodLevel;
    private boolean disease;
    private int disease_count;
    
    /**
     * Create a new crocodile. A crocodile may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the crocodile will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param color The color the crocodile is represented as.
     * @param sex The gender of the crocodile.
     * @param firstGeneration If true, the crocodile will have a random gene set.
     * @param parentGene The parent gene of the crocodile.
     */
    public Crocodile(boolean randomAge, Field field, Location location, Color col, String sex, boolean firstGeneration, String parentGene) {
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
     * This is what the crocodile does most of the time - it runs 
     * around. 
     * Sometimes it will breed, die of old age, die of disease, or starve to death.
     * @param newCrocodiles A list to return newly born crocodiles.
     */
    public void act(List<Animal> newCrocodiles) {
        incrementAge();
        incrementHunger();
        
        if(isAlive()) {
            giveBirth(newCrocodiles);            
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
     * This could result in the crocodile's death.
     */
    private void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Makes this crocodile more hungry. 
     * This could result in the crocodile's death.
     */
    private void incrementHunger() {
        foodLevel = foodLevel - METABOLISM;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Increases the disease count.
     * This could result in the crocodile's death.
     */
    private void incrementDiseaseCount(){
        disease_count++;
        if (disease_count >= 40){
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
            if (neighbour instanceof Crocodile) {
                Crocodile sNeighbour = (Crocodile) neighbour;
                if (sNeighbour.isAlive()){
                    sNeighbour.tryGainDisease();
                }
            }
        }
    }    
    
    /**
     * Looks for food adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Squirrel) {
                Squirrel food = (Squirrel) animal;
                if(food.isAlive() && food.flee()) { 
                    food.setDead();
                    eat("SQUIRREL");
                    return where;
                }
            }
            else if(animal instanceof Capybara) {
                Capybara food = (Capybara) animal;
                if(food.isAlive() && food.flee()) { 
                    food.setDead();
                    eat("CAPYBARA");
                    return where;
                }
            }
            else if(animal instanceof Deer) {
                Deer food = (Deer) animal;
                if(food.isAlive() && food.flee()) { 
                    food.setDead();
                    eat("DEER");
                    return where;
                }
            }
        }
        return null;
    
    }
    
    /**
     * Ensures max limit of foodLevel is maintained.
     */
    private void eat(String animal){
        if (animal.equals("SQUIRREL") && foodLevel < MAX_FOOD_LEVEL - SQUIRREL_FOOD_VALUE){
            foodLevel += SQUIRREL_FOOD_VALUE;
        }
        else if (animal.equals("CAPYBARA") && foodLevel < MAX_FOOD_LEVEL - CAPYBARA_FOOD_VALUE){
            foodLevel += CAPYBARA_FOOD_VALUE;
        }
        else if (animal.equals("DEER") && foodLevel < MAX_FOOD_LEVEL - DEER_FOOD_VALUE){
            foodLevel += DEER_FOOD_VALUE;
        }
        else {
            foodLevel = MAX_FOOD_LEVEL;
        }
    }
    
    /**
     * Checks whether or not this crocodile is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCrocodiles A list to return newly born crocodiles.
     */
    private void giveBirth(List<Animal> newCrocodiles) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            List<Animal> neighbors = field.getLivingNeighbours(getLocation());
    
            Crocodile mate = null;
            for (Animal neighbor : neighbors) {
                if (neighbor instanceof Crocodile && !this.getSex().equals(neighbor.getSex())) {
                    mate = (Crocodile) neighbor;
                    break;
                }
            }
    
            if (mate != null) {
                String parent1Gene = this.getGene();
                String parent2Gene = mate.getGene();
                String newGene = crossoverGenes(parent1Gene, parent2Gene);
    
                newGene = mutateGene(newGene);
    
                boolean isMale = Math.random() < 0.5;
    
                Crocodile young = new Crocodile(false, field, loc, getColor(), isMale ? "Male" : "Female", false, newGene);
                newCrocodiles.add(young);
            }
        }
    }
        
    /**
     * Generates a number representing the number of births,
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
     * A crocodile can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Takes the integers from the 14 digit gene string and assigns them as the crocodile's life cycle values.
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