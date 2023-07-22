package ihh.potionreplacer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final String DEFAULT_CONFIG = "{\"minecraft:poison\":[{\"id\":\"minecraft:poison\",\"duration\":900}],\"minecraft:long_turtle_master\":[{\"id\":\"minecraft:slowness\",\"duration\":400,\"amplifier\":3},{\"id\":\"minecraft:resistance\",\"duration\":400,\"amplifier\":2}]}";

    static Map<Potion, List<MobEffectInstance>> getEffectMap(JsonObject json) {
        Map<Potion, List<MobEffectInstance>> result = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            List<MobEffectInstance> list = new ArrayList<>();
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                list.add(new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(object.get("id").getAsString())), getOrDefault(object, "duration", 0), getOrDefault(object, "amplifier", 0)));
            }
            result.put(ForgeRegistries.POTIONS.getValue(new ResourceLocation(entry.getKey())), list);
        }
        return result;
    }

    static String read() {
        File file = new File(FMLPaths.CONFIGDIR.get().toString() + File.separator + PotionReplacer.MOD_ID + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                if (!file.exists() || file.isDirectory() || !file.canWrite()) {
                    return error("Error retrieving config: The config file cannot be created or accessed!");
                } else {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(DEFAULT_CONFIG);
                    } catch (IOException e) {
                        return error("Error writing default config: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                return error("Error writing default config: " + e.getMessage());
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);
            return builder.toString();
        } catch (IOException e) {
            return error("Error reading config: " + e.getMessage());
        }
    }

    private static String error(String message) {
        PotionReplacer.LOGGER.error(message);
        return DEFAULT_CONFIG;
    }

    private static int getOrDefault(JsonObject json, String key, int other) {
        try {
            return json.get(key).getAsInt();
        } catch (NumberFormatException | NullPointerException e) {
            return other;
        }
    }
}
