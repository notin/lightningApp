package com.lightning.notification.app;

import com.lightning.notification.app.service.LightningProcessingService;
import org.testng.annotations.Test;

public class LightningTest {
    @Test
    public void testLightning(){
        LightningProcessingService processingService = new LightningProcessingService();
        processingService.processFiles();
    }
}
