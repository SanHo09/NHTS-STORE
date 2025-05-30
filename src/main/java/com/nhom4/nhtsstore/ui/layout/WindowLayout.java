package com.nhom4.nhtsstore.ui.layout;


import com.nhom4.nhtsstore.NhtsStoreApplication;
import com.nhom4.nhtsstore.ui.LoadingDialog;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class WindowLayout implements Initializable {
    @FXML public FlowPane rootPane;
    @Getter
    @Setter
    private JFrame mainFrame;
    private final ThemeManager themeManager;
    public WindowLayout(ThemeManager themeManager) {
        this.themeManager = themeManager;
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
        closeWindowButton.setOnMouseEntered(e -> {
            closeWindowButton.setGraphic(IconUtil.createFxImageViewFromSvg("/icons/MaterialSymbolsCloseSmall.svg", 24, 24, color -> Color.RED));
        });

        closeWindowButton.setOnMouseExited(e -> {
            closeWindowButton.setGraphic(IconUtil.createFxImageViewFromSvg("/icons/MaterialSymbolsCloseSmall.svg", 24, 24, color -> Color.decode("#333333")));
        });

        // Window control actions
        minimizeWindowButton.setOnAction(e -> minimizeWindow());
        closeWindowButton.setOnAction(e -> closeWindow());
        JavaFxThemeUtil.setupThemeListener(rootPane, themeManager);
    }

    @FXML
    public MFXButton minimizeWindowButton;
    @FXML
    public MFXButton closeWindowButton;

    private void minimizeWindow() {
        mainFrame.setState(Frame.ICONIFIED);
    }

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private void closeWindow() {
        Platform.runLater(() -> {
            //ensures all Spring beans are properly destroyed through their lifecycle callbacks
            // before the application shuts down completely
            applicationContext.close();
            if(applicationContext.isClosed()){
                mainFrame.dispose();
                Platform.exit();
                System.exit(0);
            }

        });
    }
}
