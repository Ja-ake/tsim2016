package com.jakespringer.reagne.gfx;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import com.jakespringer.reagne.Reagne;
import static org.lwjgl.opengl.GL11.*;

public class LWJGLWindow {
    private int width;
    private int height;
    private double centerx;
    private double centery;
    private String title;

    public void initialize(int w, int h, String t) {
        title = t;
        width = w;
        height = h;
        setDisplayMode(width, height, false);
        {
            try {
                if (Display.isCreated()) return;
                Display.setVSyncEnabled(false);
                Display.setResizable(false);
                Display.setTitle(title);
                Display.create();
                Keyboard.create();
                Mouse.create();
            } catch (LWJGLException ex) {
                ex.printStackTrace();
            }
        }

        setViewport(-width / 2, width / 2, -height / 2, height / 2);
        // glOrtho(-width/2, width/2, -height/2, height/2, -1, 1);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
        
        glEnable(GL_BLEND);
        glEnable(GL_COLOR);
        glEnable(GL_ALPHA);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_FLAT);

        update();
    }

    public void setDisplayMode(int width, int height, boolean fullscreen) {
        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen)) { return; }
        try {
            DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (DisplayMode current : modes) {
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }
            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }

    public void setViewport(double x0, double x1, double y0, double y1) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(x0, x1, y0, y1, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        centerx = (x0 + x1) / 2;
        centery = (y0 + y1) / 2;
    }

    public void update() {
        if (Display.isCloseRequested()) Reagne.stop();

        Display.update();
        Display.sync(60);
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
