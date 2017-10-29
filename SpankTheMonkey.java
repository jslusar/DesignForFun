import java.awt.Robot;
import com.leapmotion.leap.*;
import java.awt.event.*;
import com.leapmotion.leap.Gesture.State;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.AWTException;

public class SpankTheMonkey {
  public static void main(String[] args) {
    SpankTheMonkeyListener listener = new SpankTheMonkeyListener();
    Controller controller = new Controller();
    controller.addListener(listener);
    System.out.println("Press Enter to quit...");

    try {
      System.in.read();
    } catch(Exception e) {
      e.printStackTrace();
    }
    controller.removeListener(listener);
  }
}

class SpankTheMonkeyListener extends Listener {
  private Robot robot;
  private boolean mouseDown = false;
  int xMin = -200;
  int xMax = 200;
  int yMin = 100;
  int yMax = 300;
  double grabThreshold = .97;
  int screenWidth;
  int screenHeight;

  public void onInit(Controller controller) {
    System.out.println("Initialized");
    Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    screenWidth = screenDimension.width;
    screenHeight = screenDimension.height;
    try{
      robot = new Robot();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onConnect(Controller controller) {
    System.out.println("Connected");
    controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
  }

  // prints when detector stops working/is no longer connected to computer
  public void onDisconnect(Controller controller) {
    System.out.println("Disconnected");
  }

  // prints when you stop running the detector/program
  public void onExit(Controller controller) {
    System.out.println("Exited");
  }

  public void onFrame(Controller controller) {
    Frame frame = controller.frame();
    HandList hands = frame.hands();
    Hand hand = null;
    for (Hand h : hands) {
      if (h.isRight()) {
        hand = h;
        break;
      }
    }
    if (hand == null) { // Only support right hands
      return;
    }

    float grabStrength = hand.grabStrength();

    float handX = hand.palmPosition().getX();
    float handY = hand.palmPosition().getY();

    if (grabStrength < grabThreshold) {
      if (!mouseDown) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        mouseDown = true;
      }
    } else {
      if (mouseDown) {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        mouseDown = false;
      }
    }

    double xRatio = (handX - xMin) / (xMax - xMin);
    double yRatio = (handY - yMin) / (yMax - yMin);

    int screenX = (int)(screenWidth * xRatio);
    int screenY = screenHeight - (int)(screenHeight * yRatio);

    robot.mouseMove(screenX, screenY);
  }
}
