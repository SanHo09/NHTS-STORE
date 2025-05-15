package com.nhom4.nhtsstore.ui.page.setting;

import com.nhom4.nhtsstore.configuration.setting.SettingsConfig;
import com.nhom4.nhtsstore.enums.Theme;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.UIUtils;
import javafx.embed.swing.JFXPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

@Controller
public class SettingPanel extends JPanel implements RoutablePanel, LanguageManager.LanguageChangeListener {

    private final ThemeManager themeManager;
    private final SettingsConfig settingsConfig;
    private final LanguageManager languageManager;
    private ToggleSwitch themeToggle;
    private JLabel themeLabel;
    private JComboBox<String> windowModeCombo;
    private JComboBox<String> screenSizeCombo;
    private JComboBox<String> languageCombo;
    private JFrame mainFrame;
    
    // Labels that need to be updated on language change
    private JLabel titleLabel;
    private JLabel themeTitleLabel;
    private JLabel modeLabel;
    private JLabel sizeLabel;
    private JLabel languageLabel;

    @Autowired
    public SettingPanel(ThemeManager themeManager, SettingsConfig settingsConfig, LanguageManager languageManager) {
        this.themeManager = themeManager;
        this.settingsConfig = settingsConfig;
        this.languageManager = languageManager;
        languageManager.addLanguageChangeListener(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main settings container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Add title
        titleLabel = new JLabel(languageManager.getText("settings.title"));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        // Create appearance panel with all settings
        JPanel appearancePanel = createAppearancePanel();

        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(appearancePanel);
        mainPanel.add(Box.createVerticalGlue());

        // Add some padding around the edges
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add to this panel
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    private JPanel createAppearancePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder(languageManager.getText("settings.appearance")));

        // Theme Section
        JPanel themeSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themeSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        themeTitleLabel = new JLabel(languageManager.getText("settings.theme") + ":");
        themeTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        themeLabel = new JLabel("");
        toggleTheme();
        themeToggle = new ToggleSwitch();
        themeToggle.setSelected(themeManager.getCurrentTheme() == Theme.DARK);

        themeToggle.addItemListener(e -> {
            themeManager.toggleTheme();
            toggleTheme();
        });

        themeSection.add(themeTitleLabel);
        themeSection.add(Box.createHorizontalStrut(20));
        themeSection.add(themeLabel);
        themeSection.add(Box.createHorizontalStrut(10));
        themeSection.add(themeToggle);

        // Window Mode Section
        JPanel modeSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modeSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        modeLabel = new JLabel(languageManager.getText("settings.window_mode") + ":");
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        windowModeCombo = new JComboBox<>(new String[]{
            languageManager.getText("settings.windowed"), 
            languageManager.getText("settings.full_screen")
        });
        
        // Load saved window mode
        String savedWindowMode = settingsConfig.getWindowMode();
        boolean isFullScreen = "Full Screen".equals(savedWindowMode);
        windowModeCombo.setSelectedIndex(isFullScreen ? 1 : 0);
        
        windowModeCombo.addActionListener(e -> {
            boolean fullScreen = windowModeCombo.getSelectedIndex() == 1;
            String windowMode = fullScreen ? "Full Screen" : "Windowed";
            // Save the window mode setting
            settingsConfig.saveWindowMode(windowMode);
            
            if (mainFrame != null) {
                // Get the selected screen size first
                String selectedSize = (String) screenSizeCombo.getSelectedItem();
                String[] dimensions = selectedSize.split("x");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);

                if (fullScreen) {
                    mainFrame.dispose();
                    mainFrame.setUndecorated(true);
                    // Set the size before maximizing
                    mainFrame.setSize(width, height);
                    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    mainFrame.setVisible(true);
                    
                    // Update custom window controls
                    try {
                        // Use reflection to access updateWindowControls method
                        mainFrame.getClass().getMethod("updateWindowControls", boolean.class)
                                .invoke(mainFrame, true);
                    } catch (Exception ex) {
                        // Fallback to the old method if updateWindowControls is not available
                        Component[] components = mainFrame.getContentPane().getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JFXPanel) {
                                comp.setVisible(true);
                                break;
                            }
                        }
                    }
                    
                    // Disable screen size selection in full screen mode
                    screenSizeCombo.setEnabled(false);
                } else {
                    mainFrame.dispose();
                    mainFrame.setUndecorated(false);
                    mainFrame.setExtendedState(JFrame.NORMAL);
                    mainFrame.setSize(width, height);
                    mainFrame.setLocationRelativeTo(null); // Center the window
                    
                    // Update custom window controls
                    try {
                        // Use reflection to access updateWindowControls method
                        mainFrame.getClass().getMethod("updateWindowControls", boolean.class)
                                .invoke(mainFrame, false);
                    } catch (Exception ex) {
                        // Fallback to the old method if updateWindowControls is not available
                        Component[] components = mainFrame.getContentPane().getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JFXPanel) {
                                comp.setVisible(false);
                                break;
                            }
                        }
                    }
                    
                    mainFrame.setVisible(true);
                    
                    // Enable screen size selection in windowed mode
                    screenSizeCombo.setEnabled(true);
                }
            }
        });

        modeSection.add(modeLabel);
        modeSection.add(Box.createHorizontalStrut(10));
        modeSection.add(windowModeCombo);

        // Screen Size Section
        JPanel sizeSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizeSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sizeLabel = new JLabel(languageManager.getText("settings.screen_size") + ":");
        sizeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        screenSizeCombo = new JComboBox<>(new String[]{"1280x720", "1366x768", "1440x900", "1600x900", "1920x1080"});
        
        // Load saved screen size
        String savedScreenSize = settingsConfig.getScreenSize();
        screenSizeCombo.setSelectedItem(savedScreenSize);
        
        // Initially disable screen size if in full screen mode
        screenSizeCombo.setEnabled(!("Full Screen".equals(settingsConfig.getWindowMode())));
        
        screenSizeCombo.addActionListener(e -> {
            String selectedSize = (String) screenSizeCombo.getSelectedItem();
            // Save the screen size setting
            settingsConfig.saveScreenSize(selectedSize);
            
            if (mainFrame != null && "Windowed".equals(settingsConfig.getWindowMode())) {
                String[] dimensions = selectedSize.split("x");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);
                
                mainFrame.setSize(width, height);
                mainFrame.setLocationRelativeTo(null); // Center the window
            }
        });

        sizeSection.add(sizeLabel);
        sizeSection.add(Box.createHorizontalStrut(10));
        sizeSection.add(screenSizeCombo);
        
        // Language Section
        JPanel languageSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languageSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        languageLabel = new JLabel(languageManager.getText("settings.language") + ":");
        languageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Create array of display names
        String[] languages = {
            languageManager.getText("settings.language.english"),
            languageManager.getText("settings.language.vietnamese")
        };
        
        languageCombo = new JComboBox<>(languages);
        
        // Set selected language
        int selectedIndex = languageManager.getCurrentLanguage() == LanguageManager.Language.ENGLISH ? 0 : 1;
        languageCombo.setSelectedIndex(selectedIndex);
        
        languageCombo.addActionListener(e -> {
            int index = languageCombo.getSelectedIndex();
            LanguageManager.Language language = index == 0 
                ? LanguageManager.Language.ENGLISH 
                : LanguageManager.Language.VIETNAMESE;
                
            // Set the new language
            languageManager.setLanguage(language);
            
            // Show confirmation
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    this, 
                    languageManager.getText("notify.saved"),
                    languageManager.getText("settings.language"),
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        });
        
        languageSection.add(languageLabel);
        languageSection.add(Box.createHorizontalStrut(10));
        languageSection.add(languageCombo);

        // Add all sections to the panel with proper spacing
        panel.add(themeSection);
        panel.add(Box.createVerticalStrut(15));
        panel.add(modeSection);
        panel.add(Box.createVerticalStrut(15));
        panel.add(sizeSection);
        panel.add(Box.createVerticalStrut(15));
        panel.add(languageSection);

        return panel;
    }

    private void toggleTheme() {
        Theme currentTheme = themeManager.getCurrentTheme();
        if (currentTheme == Theme.DARK) {
            themeLabel.setText(languageManager.getText("settings.dark_mode"));
            themeLabel.setIcon(UIUtils.toIcon("MaterialSymbolsDarkMode.svg",themeManager.getThemeIconColor()));
        } else {
            themeLabel.setText(languageManager.getText("settings.light_mode"));
            themeLabel.setIcon(UIUtils.toIcon("MaterialSymbolsLightMode.svg",themeManager.getThemeIconColor()));
        }
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        
        // Apply saved settings when the frame is set
        SwingUtilities.invokeLater(() -> {
            applySettings();
        });
    }
    
    private void applySettings() {
        if (mainFrame != null) {
            // Apply window mode
            String windowMode = settingsConfig.getWindowMode();
            String screenSize = settingsConfig.getScreenSize();
            
            boolean isFullScreen = "Full Screen".equals(windowMode);
            windowModeCombo.setSelectedIndex(isFullScreen ? 1 : 0);
            screenSizeCombo.setSelectedItem(screenSize);
            
            // The action listeners will take care of applying the settings
        }
    }

    @Override
    public void onNavigate(RouteParams params) {
    }
    
    @Override
    public void onLanguageChanged() {
        // Update all labels with new language
        updateComponentTexts();
    }
    
    private void updateComponentTexts() {
        SwingUtilities.invokeLater(() -> {
            // Update panel title
            titleLabel.setText(languageManager.getText("settings.title"));
            
            // Update setting labels
            themeTitleLabel.setText(languageManager.getText("settings.theme") + ":");
            modeLabel.setText(languageManager.getText("settings.window_mode") + ":");
            sizeLabel.setText(languageManager.getText("settings.screen_size") + ":");
            languageLabel.setText(languageManager.getText("settings.language") + ":");
            
            // Update the theme toggle text
            toggleTheme();
            
            // Update dropdown values
            DefaultComboBoxModel<String> windowModel = new DefaultComboBoxModel<>(new String[]{
                languageManager.getText("settings.windowed"),
                languageManager.getText("settings.full_screen")
            });
            boolean isFullScreen = windowModeCombo.getSelectedIndex() == 1;
            windowModeCombo.setModel(windowModel);
            windowModeCombo.setSelectedIndex(isFullScreen ? 1 : 0);
            
            // Update language dropdown
            DefaultComboBoxModel<String> languageModel = new DefaultComboBoxModel<>(new String[]{
                languageManager.getText("settings.language.english"),
                languageManager.getText("settings.language.vietnamese")
            });
            int selectedLanguage = languageCombo.getSelectedIndex();
            languageCombo.setModel(languageModel);
            languageCombo.setSelectedIndex(selectedLanguage);
            
            // Update the panel border if possible
            JPanel parent = (JPanel) getParent();
            if (parent != null && parent.getBorder() instanceof TitledBorder) {
                ((TitledBorder) parent.getBorder()).setTitle(languageManager.getText("settings.appearance"));
                parent.repaint();
            }
        });
    }
}