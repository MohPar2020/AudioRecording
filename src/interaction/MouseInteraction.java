package interaction;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseInteraction {

    public void click() throws AWTException {
        Robot bot = new Robot();
        Point location = this.getMousePosition();
        
        int x = (int) location.getX();
        int y = (int) location.getY();
        //System.out.println("[" + x + ", " + y + "]");                                
        bot.mouseMove(x, y);
        
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private Point getMousePosition() {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        
        return b;
    }
}
