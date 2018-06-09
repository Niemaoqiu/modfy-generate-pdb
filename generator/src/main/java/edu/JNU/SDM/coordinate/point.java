package edu.JNU.SDM.coordinate;


import java.awt.*;

public class point implements shape {
    private int x;
    private int y;
    public point(){};
    public point(int wndWidth,int wndHeight){
        setScreenLocation(wndWidth,wndHeight);
    };

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setScreenLocation(int wndWidth,int wndHeight) {
        Dimension screensize= Toolkit.getDefaultToolkit().getScreenSize();
        setX((int)screensize.getWidth()/2-wndWidth/2);
        setY((int)screensize.getHeight()/2-wndHeight/2);
    }
}
