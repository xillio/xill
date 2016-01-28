package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import nl.xillio.migrationtool.gui.RobotTab;

import java.util.List;

public class CloseAppStopRobotsDialog extends FXMLDialog {
    private final List<RobotTab> tabs;
    private boolean preventClose = false;

    public CloseAppStopRobotsDialog(List<RobotTab> tabs) {
        super("/fxml/dialogs/CloseAppStopRobots.fxml");

        this.tabs = tabs;
        boolean mul = tabs.size() > 1;
        setTitle(tabs.size() + " robot" + (mul ? "s are" : " is") + " still running, stop " + (mul ? "them" : "it") + "?");
    }

    @FXML
    private void yesBtnPressed(final ActionEvent event) {
        // Stop all robots.
        tabs.forEach(tab -> tab.getEditorPane().getControls().stop());
        this.close();
    }

    @FXML
    private void noBtnPressed(final ActionEvent event) {
        preventClose = true;
        this.close();
    }

    public boolean getPreventClose() {
        return preventClose;
    }
}
