package com.lightning.notification.app.service;

import com.google.gson.Gson;
import com.lightning.notification.app.lighting.Asset;
import com.lightning.notification.app.lighting.Lightning;
import org.apache.commons.lang3.tuple.MutablePair;
import com.lightning.notification.app.utils.TileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class LightningProcessingService {
    public void processFiles() {
        String lightningResource = "src/main/resources/lightning.json";
        String assetsResource = "src/main/resources/assets.json";

        HashMap<String, Lightning> lightnings = new HashMap<>();
        HashMap<String, Asset> assets =  new HashMap<>();

        //Read File Content
        try (Stream<String> stream = Files.lines(Paths.get(lightningResource))) {
            stream.forEach(x-> addToList(lightnings, x));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Read Asset Content
        try (Stream<String> stream = Files.lines(Paths.get(assetsResource))) {
            stream.forEach(x-> {
                setAssets(assets, x);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        lightnings.keySet().stream().filter(x->assets.containsKey(x)).forEach(x-> getPrintln(assets, x));
    }

    private void getPrintln(HashMap<String, Asset> assets, String x) {
        System.out.println("lightning alert for "+ assets.get(x).getAssetOwner()+":"+ assets.get(x).getAssetName());
    }

    private void setAssets(HashMap<String, Asset> assets, String x) {
        Asset[] assetArray = new Gson().fromJson(x, Asset[].class);
        Arrays.stream(assetArray).forEach(asset->assets.put(asset.getQuadKey(),asset));
    }

    private void addToList(HashMap<String, Lightning> lightnings, String x) {
        Lightning lightning = new Gson().fromJson(x, Lightning.class);
        MutablePair<Integer, Integer> pixels = TileSystem.latLongToPixelXY(lightning.getLatitude(), lightning.getLongitude(), 12);
        MutablePair<Integer, Integer> tiles = TileSystem.pixelXYToTileXY(pixels.getLeft(), pixels.getRight());
        String s = TileSystem.tileXYToQuadKey(tiles.getLeft(), tiles.getRight(), 12);
        if(!lightnings.containsKey(s)){
            lightnings.put(s, lightning);
        }
    }

}
