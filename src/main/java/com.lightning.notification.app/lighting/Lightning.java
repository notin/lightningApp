package com.lightning.notification.app.lighting;
import lombok.Data;

@Data
public class Lightning {
     private int flashType;
     private long strikeTime;
     private double latitude;
     private double longitude;
     private int peakAmps;
     private String reserved;
     private int icHeight;
     private long receivedTime;
     private int numberOfSensors;
     private int multiplicity;
}