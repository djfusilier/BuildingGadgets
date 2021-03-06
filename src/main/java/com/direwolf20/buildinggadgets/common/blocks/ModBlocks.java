package com.direwolf20.buildinggadgets.common.blocks;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.Config;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlock;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockPowder;
import com.direwolf20.buildinggadgets.common.blocks.EffectBlock;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@GameRegistry.ObjectHolder(BuildingGadgets.MODID)
public class ModBlocks {

    @GameRegistry.ObjectHolder("effectblock")
    public static EffectBlock effectBlock;
    @GameRegistry.ObjectHolder("constructionblock")
    public static ConstructionBlock constructionBlock;
    @GameRegistry.ObjectHolder("constructionblockpowder")
    public static ConstructionBlockPowder constructionBlockPowder;
    @GameRegistry.ObjectHolder("templatemanager")
    public static TemplateManager templateManager;

    @SideOnly(Side.CLIENT)
    public static void initColorHandlers() {
        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        if (Config.enablePaste) {constructionBlock.initColorHandler(blockColors);}
    }
}