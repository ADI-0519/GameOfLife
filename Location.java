/**
 * Represent a location in a rectangular grid.
 *
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class Location {

    private int row;
    private int col;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Implement content equality.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }

    /**
     * Return a string of the form row,column
     * @return A string representation of the location.
     */
    public String toString() {
        return row + "," + col;
    }

    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode() {
        return (row << 16) + col;
    }

    /**
     * @return The row.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The column.
     */
    public int getCol() {
        return col;
    }
}