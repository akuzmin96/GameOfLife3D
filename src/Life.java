/**
 * Created by Anton on 9/21/2016.
 */

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;

public class Life extends Application implements EventHandler<ActionEvent>
{

  private Cell[][][] currentState = new Cell[32][32][32];
  private boolean[][][] nextState = new boolean[32][32][32];
  private final Group root = new Group();
  private final Xform cube = new Xform();
  private final Xform cameraXform = new Xform();
  private final Xform cameraXform2 = new Xform();
  private final Xform cameraXform3 = new Xform();
  private PerspectiveCamera camera = new PerspectiveCamera(true);
  private Button pauseButton;
  private Button randomButton;
  private Button preset1;
  private Button preset2;
  private Button preset3;
  private Button preset4;
  private Button preset5;
  private Presets presets;
  private TextField r1Text, r2Text, r3Text, r4Text;
  private int r1, r2, r3, r4;
  private RotateLoop loop;
  private Zoom scene;
  
  private static final double CAMERA_INITIAL_DISTANCE = -125;
  private static final double CAMERA_INITIAL_X_ANGLE = 20.0;
  private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  
  private void buildCamera()
  {
    root.getChildren().add(cameraXform);
    cameraXform.getChildren().add(cameraXform2);
    cameraXform2.getChildren().add(cameraXform3);
    cameraXform3.getChildren().add(camera);
    cameraXform3.setRotateZ(180.0);
  
    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
  }
  
  private void buildCube()
  {
    for(int x = 1; x < 31; x++)
    {
      for(int y = 1; y < 31; y++)
      {
        for(int z = 1; z < 31; z++)
        {
          currentState[x][y][z].cellBox.setTranslateX(x - 15);
          currentState[x][y][z].cellBox.setTranslateY(y - 15);
          currentState[x][y][z].cellBox.setTranslateZ(z - 15);
          cube.getChildren().add(currentState[x][y][z].cellBox);
        }
      }
    }
  }

  @Override
  public void start(Stage primaryStage)
  {
    BorderPane borderPane = new BorderPane();
    presets = new Presets();
    
    scene = new Zoom(borderPane, CAMERA_INITIAL_DISTANCE);
    primaryStage.setTitle("Game of Life in 3D");
    primaryStage.setScene(scene);
  
    SubScene subScene = new SubScene(root, 850, 600, true, SceneAntialiasing.BALANCED);
    subScene.setFill(Color.GRAY);
    subScene.setCamera(camera);
    
    HBox hbox = new HBox();
    borderPane.setTop(hbox);
    borderPane.setCenter(subScene);
    root.getChildren().add(cube);
    buildCamera();
    initializeGame();
    buildCube();
    setUpHBox(hbox);
    primaryStage.show();
    loop = new RotateLoop();
    loop.start();
  }
  
  private void setUpHBox(HBox hbox)
  {
    r1Text = new TextField("8");
    r1Text.setPrefSize(40, 5);
    r2Text = new TextField("16");
    r2Text.setPrefSize(40, 5);
    r3Text = new TextField("20");
    r3Text.setPrefSize(40, 5);
    r4Text = new TextField("6");
    r4Text.setPrefSize(40, 5);

    pauseButton = new Button("Pause");
    pauseButton.setOnAction(this);
    randomButton = new Button("Random");
    randomButton.setOnAction(this);
    preset1 = new Button("Preset 1");
    preset1.setOnAction(this);
    preset2 = new Button("Preset 2");
    preset2.setOnAction(this);
    preset3 = new Button("Preset 3");
    preset3.setOnAction(this);
    preset4 = new Button("Preset 4");
    preset4.setOnAction(this);
    preset5 = new Button("Preset 5");
    preset5.setOnAction(this);
  
    r1Text.setDisable(true);
    r2Text.setDisable(true);
    r3Text.setDisable(true);
    r4Text.setDisable(true);

    hbox.getChildren().addAll(new Label("R1:"), r1Text);
    hbox.getChildren().addAll(new Label("R2:"), r2Text);
    hbox.getChildren().addAll(new Label("R3:"), r3Text);
    hbox.getChildren().addAll(new Label("R4:"), r4Text);
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.getChildren().addAll(randomButton,
            preset1, preset2, preset3, preset4, preset5, pauseButton);
  }

  @Override
  public void handle(ActionEvent event)
  {
    Object source = event.getSource();
    
    if(source == pauseButton && pauseButton.getText().equals("Pause"))
    {
      pauseButton.setText("Start");
      r1Text.setDisable(false);
      r2Text.setDisable(false);
      r3Text.setDisable(false);
      r4Text.setDisable(false);
      loop.stop();
    }
    else if(source == pauseButton && pauseButton.getText().equals("Start"))
    {
      pauseButton.setText("Pause");
      r1Text.setDisable(true);
      r2Text.setDisable(true);
      r3Text.setDisable(true);
      r4Text.setDisable(true);
      loop.start();
    }
    if(source == randomButton)
    {
      presets.reset(currentState, nextState);
      presets.randomPreset(currentState);
    }
    if(source == preset1)
    {
      presets.reset(currentState, nextState);
      presets.preset1(currentState);
    }
    if(source == preset2)
    {
      presets.reset(currentState, nextState);
      presets.preset2(currentState);
    }
    if(source == preset3)
    {
      presets.reset(currentState, nextState);
      presets.preset3(currentState);
    }
    if(source == preset4)
    {
      presets.reset(currentState, nextState);
      presets.preset4(currentState);
    }
    if(source == preset5)
    {
      presets.reset(currentState, nextState);
      presets.preset5(currentState);
    }
  }

  private class RotateLoop extends AnimationTimer
  {
    int frame = 0;
    long updateTime = 0;
    int lastFrame = 0;
    @Override
    public void handle(long time)
    {
      frame ++;
      cube.ry.setAngle(cameraXform.ry.getAngle() - (0.5 * frame));
      cube.rx.setAngle(cameraXform.rx.getAngle() - (0.5 * frame));
      camera.setTranslateZ(scene.getCameraDistance());
      if(time - updateTime >= 1_000_000_000)
      {
        System.out.println(frame - lastFrame);
        updateConditions();
        updateGame();
        updateTime = time;
        lastFrame = frame;
      }
      
      if(time - updateTime >= 900_000_000)
      {
        clearBoard();
      }
    }
  }

  private void updateConditions()
  {
    try{
      r1 = Integer.parseInt(r1Text.getText());
      r2 = Integer.parseInt(r2Text.getText());
      r3 = Integer.parseInt(r3Text.getText());
      r4 = Integer.parseInt(r4Text.getText());
    }
    catch(NumberFormatException e)
    {
    }
  }

  private void updateGame()
  {
    for(int x = 1; x < 31; x++)
    {
      for (int y = 1; y < 31; y++)
      {
        for (int z = 1; z < 31; z++)
        {
          int neighbors = currentState[x][y][z].getNeighbors(currentState);
          boolean status = currentState[x][y][z].getStatus();
          if (status && (neighbors > r3 || neighbors < r4))
          {
            nextState[x][y][z] = false;
          }
          else if (!status && (neighbors >= r1 && neighbors <= r2))
          {
            nextState[x][y][z] = true;
          }
          else
          {
            nextState[x][y][z] = status;
          }
        }
      }
    }
  
    for(int x = 1; x < 31; x++)
    {
      for (int y = 1; y < 31; y++)
      {
        for (int z = 1; z < 31; z++)
        {
          boolean status = nextState[x][y][z];
          if(currentState[x][y][z].getStatus() != status)
          {
            if(status)
            {
              currentState[x][y][z].setAlive();
            }
            else
            {
              currentState[x][y][z].setDead();
            }
          }
        }
      }
    }
  }
  
  private void clearBoard()
  {
    for(int x = 1; x < 31; x++)
    {
      for(int y = 1; y < 31; y++)
      {
        for(int z = 1; z < 31; z++)
        {
          if(!currentState[x][y][z].getStatus())
          {
            currentState[x][y][z].cellBox.setVisible(false);
          }
        }
      }
    }
  }

  private void initializeGame()
  {
    for(int x = 1; x < 32; x++)
    {
      for(int y = 1; y < 32; y++)
      {
        for(int z = 1; z < 32; z++)
        {
          currentState[x][y][z] = new Cell(false, x, y, z);
        }
      }
    }
    presets.randomPreset(currentState);
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
