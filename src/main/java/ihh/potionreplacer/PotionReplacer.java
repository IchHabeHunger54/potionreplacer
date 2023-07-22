package ihh.potionreplacer;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

@Mod(PotionReplacer.MOD_ID)
public class PotionReplacer {
    public static final String MOD_ID = "potionreplacer";
    public static final Logger LOGGER = LogManager.getLogger();

    public PotionReplacer() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PotionReplacer::load);
    }

    private static void load(FMLLoadCompleteEvent e) {
        for (Map.Entry<Potion, List<MobEffectInstance>> entry : Config.getEffectMap(GsonHelper.parse(Config.read()).getAsJsonObject()).entrySet()) {
            entry.getKey().effects = ImmutableList.<MobEffectInstance>builder().addAll(entry.getValue()).build();
        }
    }
}
