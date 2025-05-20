package com.nhom4.nhtsstore.services;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IBarcodeScannerService {
    void scanWithWebcam(Consumer<String> onBarcodeDetected);
    void scanWithPhoneCamera(Consumer<String> onBarcodeDetected);
    void scanWithWebcam(Consumer<String> onBarcodeDetected, BiConsumer<String, JLabel> onStatusUpdate);
    void scanWithPhoneCamera(Consumer<String> onBarcodeDetected, BiConsumer<String, JLabel> onStatusUpdate);
    void stopScanning();
}
