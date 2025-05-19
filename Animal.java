import java.util.List;
import javafx.scene.paint.Color;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public abstract class Animal {
    
    private boolean alive;
    private Field field;
    private Location location;
    private Color color = Color.BLACK;
    private String gene;
    private static String sex;
    
    /**
     * Create a new animal at location in field.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param color The color the animal is represented as.
     * @param sex The gender of the animal.
     * @param firstGeneration If true, the animal will have a random gene set.
     * @param parentGene The parent gene of the animal.
     */
    public Animal(Field field, Location location, Color col, String sex, boolean firstGeneration, String parentGene) {
        alive = true;
        this.field = field;
        setLocation(location);
        setColor(col);
        if(firstGeneration) {
            setRandomGene();
        }
        else {
            this.gene = parentGene;
        }
        
        this.sex = setSex();
        if (this.gene != null) {
            parseGene(this.gene);
            mutateGene(this.gene);   
        }
    }
    
    /**
     * Makes this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Checks whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive() {
        return alive;
    }

    /**
     * Indicates that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead() {
        alive = false;
        if(location != null) {
            field.clear(location);
            Plant plant = new Plant(field, location, Color.GREEN, null, false, null);
            location = null;
            field = null;
        }
    }

    /**
     * Returns the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation() {
        return location;
    }
    
    protected void setStatus(boolean b) {
        alive = b;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation) {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Returns the animal's field.
     * @return The animal's field.
     */
    protected Field getField() {
        return field;
    }
    
    /**
     * @param Takes the color of the animal.
     * Changes the color of the animal.
     */
    public void setColor(Color col) {
        color = col;
    }

    /**
     * @return Returns the animal's color.
     */
    public Color getColor() {
        return color;
    }
    
    public static String setSex() {
        return (Randomizer.getRandom().nextBoolean()) ? "Male" : "Female";
    }
    
    public static String getSex() {
        return sex;
    }
    
    /**
     * Generates a random string of 14 integers for the gene
     */
    public void setRandomGene() {
        Random rand = Randomizer.getRandom();
        
        String randomBreedingAge = String.format("%02d", rand.nextInt(79) + 12);
        
        String randomLifeSpan = String.format("%03d", rand.nextInt(111) + 10);
        
        String randomBreedingProbability = String.format("%02d", rand.nextInt(51) + 0);
        
        String randomLitterSize = String.format("%02d", rand.nextInt(12) + 1);
        
        String randomDiseaseProbability = String.format("%02d", rand.nextInt(51) + 0);
        
        String randomMetabolism = String.format("%03d", rand.nextInt(76) + 25);
        
        gene = randomBreedingAge + randomLifeSpan + randomBreedingProbability 
               + randomLitterSize + randomDiseaseProbability + randomMetabolism;
               
        parseGene(gene);
    }
    
    /**
     * @return Returns the gene.
     */
    public String getGene() {
        return gene;
    }
    
    /**
     * @param Takes the 14-digit string of the gene.
     * Overridden in subclasses.
     */
    public void parseGene(String gene) {
    }
    
    /**
     * @param Takes the genes of both parents.
     * @return Returns the resulted gene consisting of the father's and mother's genes equally.
     * Performs genetic crossover.
     */
    public static String crossoverGenes(String motherGene, String fatherGene) {
        return motherGene.substring(0, 7) + fatherGene.substring(7);
    }
    
    /**
     * @param Takes the gene of the animal.
     * @return Returns the mutated gene of the animal.
     * Introduces mutation with a 20% probability for each digit.
     */
    public static String mutateGene(String gene) {
        Random rand = Randomizer.getRandom();
        StringBuilder mutatedGene = new StringBuilder(gene);
        
        for (int i = 0; i < gene.length(); i++) {
            if (rand.nextDouble() < 0.2) {
                int originalDigit = Character.getNumericValue(gene.charAt(i));
                int newDigit = (rand.nextBoolean()) ? Math.min(originalDigit + 1, 9) : Math.max(originalDigit - 1, 0);
                mutatedGene.setCharAt(i, Character.forDigit(newDigit, 10));
            }
        }
        
        return mutatedGene.toString();
    }
}