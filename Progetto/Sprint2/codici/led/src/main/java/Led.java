package main.java;

import javax.swing.*;
import java.awt.*;

public class Led {
    private enum LedState { OFF, ON, BLINKING }
    private volatile LedState currentState = LedState.OFF;
    private JFrame frame;
    private JPanel panel;
    private volatile boolean blinkState = false;
    private Thread blinkingThread;
    private volatile boolean keepBlinking = false;

    public void createGUI() {
        frame = new JFrame("LED Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized(Led.this) {
                    switch(currentState) {
                        case OFF:
                            g.setColor(Color.GRAY);
                            break;
                        case ON:
                            g.setColor(Color.RED);
                            break;
                        case BLINKING:
                            g.setColor(blinkState ? Color.GREEN : Color.GRAY);
                            break;
                    }
                }
                g.fillOval(50, 50, 100, 100);
            }
        };
        
        frame.add(panel);
        frame.setSize(200, 200);
        frame.setVisible(true);
    }

    public synchronized void changeLed(int n) {
        switch(n) {
            case 1: // SPENTO (GRIGIO)
                stopBlinking();
                currentState = LedState.OFF;
                panel.repaint();
                System.out.println("LED spento");
                break;
                
            case 2: // ACCESO (ROSSO)
                stopBlinking();
                currentState = LedState.ON;
                panel.repaint();
                System.out.println("LED acceso");
                break;
                
            case 3: // LAMPEGGIO (VERDE)
                if(currentState != LedState.BLINKING) {
                    stopBlinking();
                    currentState = LedState.BLINKING;
                    keepBlinking = true;
                    blinkingThread = new Thread(this::blink);
                    blinkingThread.start();
                }
                break;
                
            default:
                System.out.println("Comando non riconosciuto");
        }
    }

    private void blink() {
        try {
            while(keepBlinking && currentState == LedState.BLINKING) {
                synchronized(this) {
                    if(!keepBlinking || currentState != LedState.BLINKING) break;
                    blinkState = !blinkState;
                    panel.repaint();
                    
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Lampeggio interrotto");
        } finally {
            synchronized(this) {
                if(!keepBlinking) {
//                    currentState = LedState.OFF;
//                    panel.repaint();
                }
            }
        }
    }

    private synchronized void stopBlinking() {
        if(blinkingThread != null && blinkingThread.isAlive()) {
            keepBlinking = false;
            blinkingThread.interrupt();
        }
    }
}