package com.direwolf20.buildinggadgets.blocks.templatemanager;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.network.PacketHandler;
import com.direwolf20.buildinggadgets.network.PacketTemplateManagerLoad;
import com.direwolf20.buildinggadgets.network.PacketTemplateManagerPaste;
import com.direwolf20.buildinggadgets.network.PacketTemplateManagerSave;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TemplateManagerGUI extends GuiContainer {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    private GuiTextField nameField;

    private TemplateManagerTileEntity te;
    private TemplateManagerContainer container;

    private static final ResourceLocation background = new ResourceLocation(BuildingGadgets.MODID, "textures/gui/testcontainer.png");

    public TemplateManagerGUI(TemplateManagerTileEntity tileEntity, TemplateManagerContainer container) {
        super(container);
        this.te = tileEntity;
        this.container = container;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
        //The parameters of GuiButton are(id, x, y, width, height, text);
        this.buttonList.add(new GuiButton(1, 160, 45, 30, 20, "Save"));
        this.buttonList.add(new GuiButton(2, 160, 67, 30, 20, "Load"));
        this.buttonList.add(new GuiButton(3, 125, 45, 30, 20, "Copy"));
        this.buttonList.add(new GuiButton(4, 125, 67, 30, 20, "Paste"));
        this.nameField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
        this.nameField.setMaxStringLength(50);
        this.nameField.setVisible(true);
        //NOTE: the id always has to be different or else it might get called twice or never!
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        this.nameField.drawTextBox();
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id == 1) {
            PacketHandler.INSTANCE.sendToServer(new PacketTemplateManagerSave(te.getPos(), nameField.getText()));
        } else if (b.id == 2) {
            PacketHandler.INSTANCE.sendToServer(new PacketTemplateManagerLoad(te.getPos()));
        } else if (b.id == 3) {
            TemplateManagerCommands.CopyTemplate(container);
        } else if (b.id == 4) {
            String CBString = getClipboardString();
            //System.out.println("CBString Length: " + CBString.length());
            //System.out.println(CBString);
            try {
                NBTTagCompound tagCompound = JsonToNBT.getTagFromJson(CBString);
                //BlockMapIntState MapIntState = CopyPasteTool.getBlockMapIntState(tagCompound);
                int[] stateArray = tagCompound.getIntArray("stateIntArray");
                //int[] posArray = tagCompound.getIntArray("posIntArray");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                CompressedStreamTools.writeCompressed(tagCompound, baos);
                //ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                //NBTTagCompound newTag = CompressedStreamTools.readCompressed(bais);
                //System.out.println("BAOS Size: " + baos.size());

                //Anything larger than below is likely to overflow the max packet size, crashing your client.
                if (stateArray.length <= 12000 && baos.size() < 31000) {
                    PacketHandler.INSTANCE.sendToServer(new PacketTemplateManagerPaste(tagCompound, te.getPos(), true, true));
                } else {
                    Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(TextFormatting.RED + new TextComponentTranslation("message.gadget.pastetoobig").getUnformattedComponentText()), false);
                }

            } catch (Throwable t) {
                System.out.println(t);
                Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(TextFormatting.RED + new TextComponentTranslation("message.gadget.pastefailed").getUnformattedComponentText()), false);
            }

        }
    }

    /*public static void sendSplitArrays(int[] stateArray, int[] posArray, Map<Short, IBlockState> stateMap) {

        System.out.println("PosArray Length: " + posArray.length);
    }*/

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {

        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.nameField.mouseClicked(mouseX, mouseY, mouseButton)) {
            nameField.setFocused(true);
        } else {
            nameField.setFocused(false);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
