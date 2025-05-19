package com.nhom4.nhtsstore.services.impl;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.nhom4.nhtsstore.services.IBarcodeScannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class BarcodeScannerService implements IBarcodeScannerService {
    private JDialog scannerDialog;
    private Timer detectionTimer;
    private Webcam activeWebcam;
    private Thread phoneStreamThread;
    private volatile boolean isRunning = false;

    @Override
    public void scanWithWebcam(Consumer<String> onBarcodeDetected) {
        Component parent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (parent == null) {
            parent = new JFrame();
        }
        Frame parentFrame = (parent instanceof Frame) ?
                (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        scannerDialog = new JDialog(parentFrame, "Barcode Scanner", true);
        scannerDialog.setLayout(new BorderLayout());
        scannerDialog.setSize(640, 480);
        scannerDialog.setLocationRelativeTo(parent);

        JPanel cameraPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Initializing camera...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton captureButton = new JButton("Capture");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        buttonPanel.add(cancelButton);

        scannerDialog.add(cameraPanel, BorderLayout.CENTER);
        scannerDialog.add(statusLabel, BorderLayout.NORTH);
        scannerDialog.add(buttonPanel, BorderLayout.SOUTH);

        SwingWorker<Webcam, Void> worker = new SwingWorker<>() {
            @Override
            protected Webcam doInBackground() throws Exception {
                Webcam webcam = Webcam.getDefault();
                if (webcam != null) {
                    Dimension[] sizes = webcam.getViewSizes();
                    Dimension size = sizes[sizes.length - 1];
                    webcam.setViewSize(size);
                    webcam.open();
                    activeWebcam = webcam;
                }
                return webcam;
            }

            @Override
            protected void done() {
                try {
                    Webcam webcam = get();
                    if (webcam == null) {
                        statusLabel.setText("No camera detected");
                        captureButton.setEnabled(false);
                        return;
                    }

                    WebcamPanel webcamPanel = new WebcamPanel(webcam);
                    webcamPanel.setFPSDisplayed(true);
                    webcamPanel.setMirrored(false);

                    cameraPanel.removeAll();
                    cameraPanel.add(webcamPanel, BorderLayout.CENTER);
                    cameraPanel.revalidate();

                    statusLabel.setText("Camera ready - aim at barcode");
                    isRunning = true;

                    detectionTimer = new Timer(200, e -> {
                        if (!isRunning || !webcam.isOpen()) return;

                        BufferedImage image = webcam.getImage();
                        if (image != null) {
                            String barcode = scanBarcode(image);
                            if (barcode != null) {
                                stopScanning();
                                onBarcodeDetected.accept(barcode);
                            }
                        }
                    });
                    detectionTimer.start();

                    captureButton.addActionListener(e -> {
                        BufferedImage image = webcam.getImage();
                        if (image != null) {
                            String barcode = scanBarcode(image);
                            if (barcode != null) {
                                stopScanning();
                                onBarcodeDetected.accept(barcode);
                            } else {
                                statusLabel.setText("No barcode detected. Try again.");
                            }
                        }
                    });

                    cancelButton.addActionListener(e -> {
                        stopScanning();
                    });

                    scannerDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            stopScanning();
                        }
                    });

                } catch (Exception e) {
                    log.error("Error initializing camera", e);
                    statusLabel.setText("Error initializing camera: " + e.getMessage());
                    captureButton.setEnabled(false);
                }
            }
        };
        worker.execute();

        scannerDialog.setVisible(true);
    }

    @Override
    public void scanWithPhoneCamera(Consumer<String> onBarcodeDetected) {
        Component parent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (parent == null) {
            parent = new JFrame();
        }
        Frame parentFrame = (parent instanceof Frame) ?
                (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        String ipAddress = JOptionPane.showInputDialog(parent,
                "Enter IP Camera address (e.g., http://192.168.1.100:8080/video)",
                "http://192.168.1.29:8080/video");

        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }

        scannerDialog = new JDialog(parentFrame, "Phone Camera Scanner", true);
        scannerDialog.setLayout(new BorderLayout());
        scannerDialog.setSize(640, 480);
        scannerDialog.setLocationRelativeTo(parent);

        JPanel cameraPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Connecting to phone camera...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton captureButton = new JButton("Capture");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        buttonPanel.add(cancelButton);

        scannerDialog.add(cameraPanel, BorderLayout.CENTER);
        scannerDialog.add(statusLabel, BorderLayout.NORTH);
        scannerDialog.add(buttonPanel, BorderLayout.SOUTH);

        PhoneCameraPanel phoneCameraPanel = new PhoneCameraPanel(ipAddress, barcode -> {
            SwingUtilities.invokeLater(() -> {
                stopScanning();
                onBarcodeDetected.accept(barcode);
            });
        });

        cameraPanel.add(phoneCameraPanel, BorderLayout.CENTER);
        isRunning = true;

        captureButton.addActionListener(e -> {
            BufferedImage image = phoneCameraPanel.getCurrentFrame();
            if (image != null) {
                String barcode = scanBarcode(image);
                if (barcode != null) {
                    stopScanning();
                    onBarcodeDetected.accept(barcode);
                } else {
                    statusLabel.setText("No barcode detected. Try again.");
                }
            }
        });

        cancelButton.addActionListener(e -> stopScanning());

        scannerDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopScanning();
            }
        });

        scannerDialog.setVisible(true);
    }

    @Override
    public void stopScanning() {
        isRunning = false;

        if (detectionTimer != null) {
            detectionTimer.stop();
            detectionTimer = null;
        }

        if (activeWebcam != null && activeWebcam.isOpen()) {
            activeWebcam.close();
            activeWebcam = null;
        }

        if (phoneStreamThread != null && phoneStreamThread.isAlive()) {
            phoneStreamThread.interrupt();
            phoneStreamThread = null;
        }

        if (scannerDialog != null && scannerDialog.isVisible()) {
            scannerDialog.dispose();
            scannerDialog = null;
        }
    }

    private String scanBarcode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            MultiFormatReader reader = new MultiFormatReader();
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(
                    BarcodeFormat.EAN_13, BarcodeFormat.EAN_8,
                    BarcodeFormat.UPC_A, BarcodeFormat.UPC_E,
                    BarcodeFormat.CODE_39, BarcodeFormat.CODE_128
            ));

            Result result = reader.decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException e) {
            // No barcode found
            return null;
        } catch (Exception e) {
            log.error("Error scanning barcode", e);
            return null;
        }
    }

    // Inner class to handle phone camera stream
    private class PhoneCameraPanel extends JPanel {
        private BufferedImage currentFrame;
        private final String ipAddress;
        private final Consumer<String> onBarcodeDetected;

        public PhoneCameraPanel(String ipAddress, Consumer<String> onBarcodeDetected) {
            this.ipAddress = ipAddress;
            this.onBarcodeDetected = onBarcodeDetected;
            setPreferredSize(new Dimension(640, 480));
            startCapture();
        }

        private void startCapture() {
            phoneStreamThread = new Thread(() -> {
                HttpURLConnection connection = null;
                InputStream inputStream = null;

                try {
                    // Check if URL is for MJPEG stream or a static image
                    if (ipAddress.contains("shot.jpg")) {
                        // Handle IP Webcam static image mode (pulls individual frames)
                        captureStaticFrames();
                        return;
                    }

                    // For MJPEG streams
                    URL url = new URL(ipAddress);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    boolean inImageData = false;
                    String boundary = null;

                    // First find the boundary
                    StringBuilder headerData = new StringBuilder();
                    while (isRunning && boundary == null) {
                        bytesRead = inputStream.read(buffer, 0, buffer.length);
                        if (bytesRead == -1) break;

                        headerData.append(new String(buffer, 0, bytesRead));
                        String data = headerData.toString();
                        int boundaryIndex = data.indexOf("boundary=");
                        if (boundaryIndex != -1) {
                            int newlineIndex = data.indexOf("\n", boundaryIndex);
                            if (newlineIndex != -1) {
                                boundary = "--" + data.substring(boundaryIndex + 9, newlineIndex).trim();
                                log.info("Found stream boundary: {}", boundary);
                                break;
                            }
                        }

                        // Avoid keeping too much data if boundary not found
                        if (headerData.length() > 8192) {
                            log.warn("Could not find boundary in header, falling back to static mode");
                            captureStaticFrames();
                            return;
                        }
                    }

                    // Now parse the images using the boundary
                    while (isRunning) {
                        bytesRead = inputStream.read(buffer, 0, buffer.length);
                        if (bytesRead == -1) break;

                        for (int i = 0; i < bytesRead; i++) {
                            if (inImageData) {
                                // Check for boundary which signals end of image
                                if (i + boundary.length() <= bytesRead) {
                                    boolean isBoundary = true;
                                    for (int j = 0; j < boundary.length(); j++) {
                                        if (buffer[i + j] != boundary.charAt(j)) {
                                            isBoundary = false;
                                            break;
                                        }
                                    }

                                    if (isBoundary) {
                                        inImageData = false;

                                        // Process the complete image
                                        if (imageBuffer.size() > 0) {
                                            try {
                                                byte[] imageData = imageBuffer.toByteArray();
                                                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
                                                if (image != null) {
                                                    currentFrame = image;
                                                    SwingUtilities.invokeLater(this::repaint);

                                                    // Check for barcode
                                                    String barcode = scanBarcode(currentFrame);
                                                    if (barcode != null && isRunning) {
                                                        SwingUtilities.invokeLater(() ->
                                                                onBarcodeDetected.accept(barcode));
                                                        return;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                log.warn("Error processing frame: {}", e.getMessage());
                                            }

                                            imageBuffer.reset();
                                        }
                                        i += boundary.length() - 1;
                                    } else {
                                        imageBuffer.write(buffer[i]);
                                    }
                                } else {
                                    imageBuffer.write(buffer[i]);
                                }
                            } else {
                                // Look for start of JPEG image (0xFF 0xD8)
                                if (i + 1 < bytesRead &&
                                        (buffer[i] & 0xFF) == 0xFF &&
                                        (buffer[i + 1] & 0xFF) == 0xD8) {
                                    inImageData = true;
                                    imageBuffer.reset();
                                    imageBuffer.write(buffer[i]);
                                    imageBuffer.write(buffer[i + 1]);
                                    i++; // Skip the second byte we just wrote
                                }
                            }
                        }

                        // Small pause to avoid overwhelming CPU
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    // Thread was interrupted, normal exit
                } catch (Exception e) {
                    log.error("Error in MJPEG stream processing: {}", e.getMessage());
                    // Fall back to static image mode
                    captureStaticFrames();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                        if (connection != null) connection.disconnect();
                    } catch (Exception e) {
                        log.warn("Error closing stream resources: {}", e.getMessage());
                    }
                }
            });
            phoneStreamThread.start();
        }

        // Fallback method for cameras that don't support MJPEG streams
        private void captureStaticFrames() {
            try {
                // Use static JPEG image URL (for IP Webcam app this is typically /shot.jpg)
                String staticUrl = ipAddress;
                if (!staticUrl.endsWith("/shot.jpg") && !staticUrl.contains("?action=snapshot")) {
                    // Try to convert streaming URL to static image URL
                    staticUrl = staticUrl.replaceAll("/video.*", "/shot.jpg");
                }

                log.info("Using static image mode with URL: {}", staticUrl);
                URL url = new URL(staticUrl);

                while (isRunning) {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(3000);
                        conn.setReadTimeout(3000);

                        try (InputStream in = conn.getInputStream()) {
                            BufferedImage image = ImageIO.read(in);
                            if (image != null) {
                                currentFrame = image;
                                SwingUtilities.invokeLater(this::repaint);

                                // Check for barcode
                                String barcode = scanBarcode(currentFrame);
                                if (barcode != null) {
                                    SwingUtilities.invokeLater(() -> onBarcodeDetected.accept(barcode));
                                    return;
                                }
                            }
                        }

                        // Wait before fetching next frame
                        Thread.sleep(200);
                    } catch (Exception e) {
                        log.warn("Error reading static frame: {}", e.getMessage());
                        Thread.sleep(1000); // Longer wait after error
                    }
                }
            } catch (Exception e) {
                log.error("Static image capture failed: {}", e.getMessage());
            }
        }

        public BufferedImage getCurrentFrame() {
            return currentFrame;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentFrame != null) {
                g.drawImage(currentFrame, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.drawString("Connecting to camera...", 20, getHeight()/2);
            }
        }
    }
}
