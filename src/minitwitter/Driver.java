package minitwitter;

import javax.swing.SwingUtilities;

import minitwitter.ui.AdminControlPanel;

/**
 * Program entry point.
 *
 * <p>Launches the {@link AdminControlPanel} (the SINGLETON main window) on the
 * Swing event-dispatch thread.</p>
 */
public class Driver {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> AdminControlPanel.getInstance().setVisible(true));
    }
}
