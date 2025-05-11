package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.NhtsStoreApplication;
import com.nhom4.nhtsstore.configuration.setting.SettingsConfig;
import com.nhom4.nhtsstore.ui.layout.WindowLayout;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import com.nhom4.nhtsstore.ui.page.setting.SettingPanel;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.JFXPanel;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

@Controller
public class MainFrame extends JFrame {
    private final ApplicationState appState;
    private final ObjectFactory<MainPanel> mainPanelFactory;
    private final LoginPanel loginPanel;
    private final MainPanel mainPanel;
    private final ThemeManager themeManager;
    private final SettingPanel settingPanel;
    private final SettingsConfig settingsConfig;
    private final LanguageManager languageManager;
    private JFXPanel jfxWindowPanel;

    MainFrame(ApplicationState appState,
              ObjectFactory<MainPanel> mainPanelFactory,
              LoginPanel loginPanel, 
              MainPanel mainPanel, 
              ThemeManager themeManager,
              SettingPanel settingPanel,
              SettingsConfig settingsConfig,
              LanguageManager languageManager) {
        this.appState = appState;
        this.mainPanelFactory = mainPanelFactory;
        this.loginPanel = loginPanel;
        this.mainPanel = mainPanel;
        this.themeManager = themeManager;
        this.settingPanel = settingPanel;
        this.settingsConfig = settingsConfig;
        this.languageManager = languageManager;
    }

    public void init() {
        // Make sure the application is visible in the taskbar
        setType(Type.NORMAL);
        
        setTitle(languageManager.getText("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set application icon
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(NhtsStoreApplication.class.getResource("/NHTS_Store_logo_256x256.png")));
        setIconImage(icon.getImage());
        
        // Apply saved window settings
        applyWindowSettings();

        // Pass MainFrame instance to SettingPanel
        settingPanel.setMainFrame(this);
        
        // Add language change listener to update window title
        languageManager.addLanguageChangeListener(() -> {
            setTitle(languageManager.getText("app.title"));
        });

        // Setup custom window controls panel
        setupWindowControls();

        // Setup content panels
        setupContentPanels();
        
        // Add window listener to properly handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }
    
    private void setupWindowControls() {
        // Only setup custom window controls if in fullscreen mode
        if ("Full Screen".equals(settingsConfig.getWindowMode())) {
            addCustomWindowControls();
        }
    }

    private void addCustomWindowControls() {
        if (jfxWindowPanel == null) {
            JavaFxSwing.runAndWait(() -> {
                jfxWindowPanel = JavaFxSwing.createJFXPanelWithController("/fxml/WindowLayout.fxml",appState.getApplicationContext(),
                        (WindowLayout controller) -> {
                            controller.setMainFrame(this);
                        }
                );
                add(jfxWindowPanel, BorderLayout.NORTH);
                revalidate();
                repaint();
            });
        } else {
            jfxWindowPanel.setVisible(true);
            revalidate();
            repaint();
        }
    }

    private void removeCustomWindowControls() {
        if (jfxWindowPanel != null) {
            jfxWindowPanel.setVisible(false);
            revalidate();
            repaint();
        }
    }

    private void setupContentPanels() {
        CardLayout cardLayout = new CardLayout();
        JPanel cardContainer = new JPanel(cardLayout);
        cardContainer.add(loginPanel, "login");
        cardContainer.add(mainPanel, "main");
        appState.authenticatedProperty().addListener((obs, old, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                cardLayout.show(cardContainer, isAuthenticated ? "main" : "login");
            });
        });

        add(cardContainer, BorderLayout.CENTER);
    }
    
    private void applyWindowSettings() {
        String windowMode = settingsConfig.getWindowMode();
        String screenSize = settingsConfig.getScreenSize();
        
        // Parse screen size
        String[] dimensions = screenSize.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        
        // Apply settings
        if ("Full Screen".equals(windowMode)) {
            setUndecorated(true);
            setSize(width, height);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            toFront();

        } else {
            setUndecorated(false);
            setSize(width, height);
            setLocationRelativeTo(null); // Center the window
        }
    }
    
    /**
     * Update the window decoration visibility based on window mode
     */
    public void updateWindowControls(boolean isFullScreen) {
        if (isFullScreen) {
            addCustomWindowControls();
        } else {
            removeCustomWindowControls();
        }
    }
}