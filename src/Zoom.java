import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

/**
 * Created by Anton on 9/25/2016.
 */
public class Zoom extends Scene implements EventHandler<KeyEvent>
{
  private double cameraDistance;
  
  public Zoom(BorderPane bp, double cameraDistance)
  {
    super(bp, 850, 650);
    this.cameraDistance = cameraDistance;
    this.setOnKeyPressed(this);
  }
  
  @Override
  public void handle(KeyEvent e)
  {
    if(e.getCode() == KeyCode.X)
    {
      cameraDistance -= 1;
    }
    if(e.getCode() == KeyCode.C)
    {
      cameraDistance += 1;
    }
  }
  
  public double getCameraDistance()
  {
    return cameraDistance;
  }
}