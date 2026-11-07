package me.ddggdd135.infinityexpansionautomation.items;

import static io.github.mooy1.infinityexpansion.items.SlimefunExtension.VOID_CAPACITOR;
import static io.github.mooy1.infinityexpansion.items.blocks.Blocks.INFINITY_FORGE;
import static io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems.ENHANCED_AUTO_CRAFTER;
import static io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems.VANILLA_AUTO_CRAFTER;

import io.github.mooy1.infinityexpansion.infinitylib.machines.MachineRecipeType;
import io.github.mooy1.infinityexpansion.items.blocks.InfinityWorkbench;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import java.lang.reflect.Field;
import me.ddggdd135.guguslimefunlib.items.AdvancedCustomItemStack;
import me.ddggdd135.infinityexpansionautomation.slimefun.InfinityCrafter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InfinityExpansionAutomationItems {
    public static SlimefunItemStack INFINITY_CRAFTER = new SlimefunItemStack(
            "INFINITY_CRAFTER",
            new AdvancedCustomItemStack(
                    Material.CRAFTING_TABLE,
                    "&c无&6尽&e自&a动&9合成台",
                    LoreBuilder.powerPerSecond(1000000),
                    LoreBuilder.powerBuffer(10000000)));
    public static SlimefunItemStack INFINITY_INFUSER = new SlimefunItemStack(
            "INFINITY_INFUSER",
            new AdvancedCustomItemStack(
                    Material.LODESTONE,
                    "&c无&6尽&e自&a动&9注入器",
                    LoreBuilder.powerPerSecond(1000000),
                    LoreBuilder.powerBuffer(10000000)));

    public static void onSetup(SlimefunAddon slimefunAddon) {
        new InfinityCrafter(
                        InfinityExpansionAutomationItemGroups.MAIN_ITEM_GROUP,
                        INFINITY_CRAFTER,
                        RecipeType.ENHANCED_CRAFTING_TABLE,
                        new ItemStack[] {
                            INFINITY_FORGE, ENHANCED_AUTO_CRAFTER, INFINITY_FORGE,
                            VANILLA_AUTO_CRAFTER, VOID_CAPACITOR, VANILLA_AUTO_CRAFTER,
                            INFINITY_FORGE, ENHANCED_AUTO_CRAFTER, INFINITY_FORGE
                        },
                        InfinityWorkbench.TYPE)
                .setEnergyConsumption(500000)
                .register(slimefunAddon);
        new InfinityCrafter(
                        InfinityExpansionAutomationItemGroups.MAIN_ITEM_GROUP,
                        INFINITY_INFUSER,
                        RecipeType.ENHANCED_CRAFTING_TABLE,
                        new ItemStack[] {
                            INFINITY_FORGE, ENHANCED_AUTO_CRAFTER, INFINITY_FORGE,
                            VANILLA_AUTO_CRAFTER, VOID_CAPACITOR, VANILLA_AUTO_CRAFTER,
                            INFINITY_FORGE, ENHANCED_AUTO_CRAFTER, INFINITY_FORGE
                        },
                        mobDataInfuserType())
                .setEnergyConsumption(500000)
                .register(slimefunAddon);
    }

    // 新版 InfinityExpansion 把 MobDataInfuser.TYPE 改为包私有，配方注册在原实例上，只能反射取出
    private static MachineRecipeType mobDataInfuserType() {
        try {
            Field field = Class.forName("io.github.mooy1.infinityexpansion.items.mobdata.MobDataInfuser")
                    .getDeclaredField("TYPE");
            field.setAccessible(true);
            return (MachineRecipeType) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to access MobDataInfuser.TYPE", e);
        }
    }
}
