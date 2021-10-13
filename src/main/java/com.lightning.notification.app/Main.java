package com.lightning.notification.app;

import com.lightning.notification.app.service.LightningProcessingService;

public class Main {

    public static void main (String[] args) {
        new LightningProcessingService().processFiles();
    }
}
