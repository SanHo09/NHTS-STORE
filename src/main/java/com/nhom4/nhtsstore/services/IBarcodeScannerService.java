package com.nhom4.nhtsstore.services;

import java.util.function.Consumer;

public interface IBarcodeScannerService {
    void scanWithWebcam(Consumer<String> onBarcodeDetected);
    void scanWithPhoneCamera(Consumer<String> onBarcodeDetected);
    void stopScanning();
}
