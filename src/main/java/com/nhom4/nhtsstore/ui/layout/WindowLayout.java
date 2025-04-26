package com.nhom4.nhtsstore.ui.layout;


import com.nhom4.nhtsstore.utils.IconUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class WindowLayout implements Initializable {
    @Getter
    @Setter
    private JFrame mainFrame;
    public WindowLayout() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeWindowButton.setText("");
        minimizeWindowButton.setText("");

        ImageView minimizeIcon = IconUtil.createFxImageViewFromSvg("/icons/MingcuteMinimizeLine.svg", 24, 24, color -> Color.decode("#333333"));
        ImageView closeIcon = IconUtil.createFxImageViewFromSvg("/icons/MaterialSymbolsCloseSmall.svg", 24, 24, color -> Color.decode("#333333"));

        minimizeWindowButton.setGraphic(minimizeIcon);
        closeWindowButton.setGraphic(closeIcon);

//        // Add style classes for hover effects
//        minimizeWindowButton.getStyleClass().add("window-control-button");
//        closeWindowButton.getStyleClass().add("window-control-button");
//
//        // Add hover effect for close button
        closeWindowButton.setOnMouseEntered(e -> {
            closeWindowButton.setGraphic(IconUtil.createFxImageViewFromSvg("/icons/MaterialSymbolsCloseSmall.svg", 24, 24, color -> Color.RED));
        });

        closeWindowButton.setOnMouseExited(e -> {
            closeWindowButton.setGraphic(IconUtil.createFxImageViewFromSvg("/icons/MaterialSymbolsCloseSmall.svg", 24, 24, color -> Color.decode("#333333")));
        });

        // Window control actions
        minimizeWindowButton.setOnAction(e -> minimizeWindow());
        closeWindowButton.setOnAction(e -> closeWindow());
    }

    @FXML
    public MFXButton minimizeWindowButton;
    @FXML
    public MFXButton closeWindowButton;

    private void minimizeWindow() {
        mainFrame.setState(Frame.ICONIFIED);
    }

    private void closeWindow() {
        mainFrame.dispose();
        System.exit(0);
    }
}
