import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position stores an Animal.
 *
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Field {
    private static final Random rand = Randomizer.getRandom();
    private int depth, width;
    private Animal[][] field;
    private int plantCount = 0; 

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width) {
        this.depth = depth;
        this.width = width;
        field = new Animal[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear() {
        plantCount = 0; 
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }
    
    public int getPlant(){
        int plantcount = 0;
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                if (field[row][col] instanceof Plant){
                    plantcount++;
                }
            }
        }
        return plantcount;
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location) {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Animal animal, Location location) {
        field[location.getRow()][location.getCol()] = animal;
    }

    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getObjectAt(Location location) {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getObjectAt(int row, int col) {
        return field[row][col];
    }

    /**
     * Count the number of plants in the field.
     * @return The total number of plants.
     */
    public int getPlantCount() {
        return plantCount;  
    }

    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location) {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location) {
        assert location != null : "Null location passed to adjacentLocations";
        
        List<Location> locations = new LinkedList<>();
        if (location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for (int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if (nextRow >= 0 && nextRow < depth) {
                    for (int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        
                        // Exclude invalid locations and the original location.
                        if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Get a shuffled list of living neighbours
     * @param location Get locations adjacent to this.
     * @return A list of living neighbours
     */
    public List<Animal> getLivingNeighbours(Location location) {

      assert location != null : "Null location passed to adjacentLocations";
      List<Animal> neighbours = new LinkedList<>();

      if (location != null) {
        List<Location> adjLocations = adjacentLocations(location);

        for (Location loc : adjLocations) {
          Animal animal = field[loc.getRow()][loc.getCol()];
          if (animal != null && animal.isAlive())
            neighbours.add(animal);
        }
        Collections.shuffle(neighbours, rand);
      }
      return neighbours;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location) {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(((getObjectAt(next)) == null) || (getObjectAt(next) instanceof Plant)) {
                free.add(next);
            }
        }
        return free;
    }
    
    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location getFreeAdjacentLocation(Location location) {
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }
}