package com.nettakrim.fishing_ruler;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Data {
    private File data = null;

    public Data() {
        load();
    }

    private void ResolveDataFile() {
        if (data != null) return;
        data = FabricLoader.getInstance().getConfigDir().resolve(FishingRulerClient.MODID+".txt").toFile();
    }

    public void save() {
        ResolveDataFile();
        try {
            FileWriter writer = new FileWriter(data);
            writer.write(String.valueOf(FishingRulerClient.biteSound));
            writer.close();
        } catch (IOException e) {
            FishingRulerClient.LOGGER.info("Failed to save data");
        }
    }

    public void load() {
        ResolveDataFile();
        try {
            data.createNewFile();
            Scanner scanner = new Scanner(data);

            if (scanner.hasNext()) {
                FishingRulerClient.biteSound = Identifier.of(scanner.nextLine());
            }

            scanner.close();
        } catch (IOException e) {
            FishingRulerClient.LOGGER.info("Failed to load data");
        }
    }
}
