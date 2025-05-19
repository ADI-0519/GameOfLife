import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.Group; 
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.HBox; 
import javafx.scene.paint.Color; 
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

/**
 * A graphical view of the simulation grid. The view displays a rectangle for
 * each location.
 *
 * @author Aditya Ranjan and Abdrakhman Salmenov
 */
public class SimulatorView extends Application {

    public static final int GRID_WIDTH = 100;
    public static final int GRID_HEIGHT = 80;    
    public static final int WIN_WIDTH = 650;
    public static final int WIN_HEIGHT = 650;  
    
    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final Color PLANT_COLOR = Color.GREEN;

    private final String GENERATION_PREFIX = "Generation: ";
    private final String POPULATION_PREFIX = "Population: ";

    private Label genLabel, population, infoLabel;

    private FieldCanvas fieldCanvas;
    private FieldStats stats;
    private Simulator simulator;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    @Override
    public void start(Stage stage) {
                
        stats = new FieldStats();
        fieldCanvas = new FieldCanvas(WIN_WIDTH - 50, WIN_HEIGHT - 50);
        fieldCanvas.setScale(GRID_HEIGHT, GRID_WIDTH); 
        simulator = new Simulator(GRID_HEIGHT, GRID_WIDTH);

        Group root = new Group();
        
        genLabel = new Label(GENERATION_PREFIX);
        infoLabel = new Label("  ");
        population = new Label(POPULATION_PREFIX);

        BorderPane bPane = new BorderPane(); 
        HBox infoPane = new HBox();
        HBox popPane = new HBox();
        

        infoPane.setSpacing(10);
        infoPane.getChildren().addAll(genLabel, infoLabel);       
        popPane.getChildren().addAll(population); 
        
        bPane.setTop(infoPane);
        StackPane canvasWrapper = new StackPane(fieldCanvas);
        bPane.setCenter(canvasWrapper);

        VBox bottomBox = new VBox(2);
        bottomBox.setAlignment(Pos.BASELINE_LEFT);
        bottomBox.setTranslateY(-30);
        
        HBox popBox = new HBox(population);
        popBox.setAlignment(Pos.BASELINE_LEFT);
        
        HBox legendBox = createLegend();
        legendBox.setAlignment(Pos.BASELINE_LEFT);
        
        bottomBox.getChildren().addAll(popBox, legendBox);
        bPane.setBottom(bottomBox);
        
        root.getChildren().add(bPane);
        Scene scene = new Scene(root, WIN_WIDTH, WIN_HEIGHT); 
        
        stage.setScene(scene);          
        stage.setTitle("Predator/Prey Simulation");
        updateCanvas(simulator.getStep(), simulator.getField());
        
        Field field = simulator.getField();
        
        stage.show();     
    }

    /**
     * Displays a short information label at the top of the window.
     */
    public void setInfoText(String text) {
        infoLabel.setText(text);
    }

    /**
     * Shows the current status of the field.
     * @param generation The current generation.
     * @param field The field whose status is to be displayed.
     */ 
    public void updateCanvas(int generation, Field field) {
        genLabel.setText(GENERATION_PREFIX + generation);
        stats.reset();
        
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);
                
                if (animal != null && animal.isAlive()) {
                    stats.incrementCount(animal.getClass());
                    fieldCanvas.drawMark(col, row, animal.getColor());
                }
                else {
                    fieldCanvas.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        
        stats.countFinished();
        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
    }
    
    /**
     * Displays a legend alongside the population of the animals to colour-coordinate the boxes with the animals.
     */
    private HBox createLegend() {
        HBox legendBox = new HBox(10);
        legendBox.setSpacing(15);
    
        addLegendItem(legendBox, "Crocodile", Color.DARKGREEN);
        addLegendItem(legendBox, "Jaguar", Color.GOLD);
        addLegendItem(legendBox, "Squirrel", Color.DIMGRAY);
        addLegendItem(legendBox, "Capybara", Color.SADDLEBROWN);
        addLegendItem(legendBox, "Deer", Color.PERU);
        addLegendItem(legendBox, "Plant", Color.FORESTGREEN);
    
        return legendBox;
    }
    
    /**
     * Combines the labels with the colour boxes.
     * @param Takes the legend box, the name of the animal, and the colour of the box as parameters.
     */
    private void addLegendItem(HBox legendBox, String name, Color color) {
        Rectangle colorBox = new Rectangle(15, 15, color);
        Text label = new Text(name);
        
        HBox itemBox = new HBox(5);
        itemBox.getChildren().addAll(colorBox, label);
    
        legendBox.getChildren().add(itemBox);
    }

    /**
     * Determines whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field) {
        return stats.isViable(field);
    }

    /**
     * Runs the simulation from its current state for the given number of
     * generations.  Stop before the given number of generations if the
     * simulation ceases to be viable.
     * @param numGenerations The number of generations to run for.
     */
    public void simulate(int numStep) {
        new Thread(() -> {
            for (int gen = 1; gen <= numStep; gen++) {
                simulator.simulateOneStep();    
                simulator.delay(100);
                Platform.runLater(() -> {
                    updateCanvas(simulator.getStep(), simulator.getField());
                });
            }
            
        }).start();
    }

    /**
     * Resets the simulation to a starting position.
     */
    public void reset() {
        simulator.reset();
        updateCanvas(simulator.getStep(), simulator.getField());
    }
    
    /**
     * Application main that loads and initializes the specified Application class 
     * on the JavaFX Application Thread.
     */
    public static void main(String args[]){           
        launch(args);      
   } 
}