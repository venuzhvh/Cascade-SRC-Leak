/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockChest$Type
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.Cascade;
import cascade.event.events.ModelRenderEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityType;
import cascade.util.render.Interpolation;
import cascade.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.block.BlockChest;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public class ESP
extends Module {
    public static boolean isRendering;
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> monsters = this.register(new Setting<Boolean>("Monsters", false));
    public Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", false));
    public Setting<Boolean> vehicles = this.register(new Setting<Boolean>("Vehicles", false));
    public Setting<Boolean> misc = this.register(new Setting<Boolean>("Other", false));
    public Setting<Boolean> items = this.register(new Setting<Boolean>("Items", false));
    public Setting<Boolean> storage = this.register(new Setting<Boolean>("Storage", false));
    public Setting<Float> storageRange = this.register(new Setting<Object>("Storage-Range", Float.valueOf(1000.0f), Float.valueOf(0.0f), Float.valueOf(1000.0f), v -> this.storage.getValue()));
    public Setting<Float> lineWidth = this.register(new Setting<Float>("Width", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    public Setting<Boolean> hurt = this.register(new Setting<Boolean>("Hurt", false));
    public Setting<Color> color = this.register(new Setting<Color>("Color", new Color(255, 255, 255, 255)));
    public Setting<Color> invisibleColor = this.register(new Setting<Color>("InvisibleColor", new Color(180, 180, 255, 255)));
    public Setting<Color> friendColor = this.register(new Setting<Color>("FriendColor", new Color(50, 255, 50, 255)));

    public ESP() {
        super("ESP", Module.Category.VISUAL, "Highlights entities through walls");
    }

    @SubscribeEvent
    public void invoke(ModelRenderEvent.Pre event) {
        if (this.isDisabled()) {
            return;
        }
        if (!this.isValid((Entity)event.getEntity())) {
            return;
        }
        this.render(event);
        Color clr = this.getEntityColor((Entity)event.getEntity());
        this.renderOne(this.lineWidth.getValue().floatValue());
        this.render(event);
        GlStateManager.func_187441_d((float)this.lineWidth.getValue().floatValue());
        this.renderTwo();
        this.render(event);
        GlStateManager.func_187441_d((float)this.lineWidth.getValue().floatValue());
        this.renderThree();
        this.renderFour(clr);
        this.render(event);
        GlStateManager.func_187441_d((float)this.lineWidth.getValue().floatValue());
        this.renderFive();
        event.setCanceled(true);
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (this.isDisabled()) {
            return;
        }
        if (this.storage.getValue().booleanValue()) {
            this.drawTileEntities();
        }
        if (this.items.getValue().booleanValue()) {
            boolean fancyGraphics = ESP.mc.gameSettings.fancyGraphics;
            ESP.mc.gameSettings.fancyGraphics = false;
            isRendering = true;
            float gammaSetting = ESP.mc.gameSettings.gammaSetting;
            ESP.mc.gameSettings.gammaSetting = 100.0f;
            Entity renderEntity = RenderUtil.getEntity();
            Frustum frustum = Interpolation.createFrustum(renderEntity);
            for (Entity entity : ESP.mc.theWorld.loadedEntityList) {
                AxisAlignedBB bb;
                if (!(entity instanceof EntityItem) || entity.isDead || !frustum.isBoundingBoxInFrustum(bb = entity.func_174813_aQ())) continue;
                GL11.glPushMatrix();
                Vec3d i = Interpolation.interpolateEntity(entity);
                RenderUtil.drawNametag(((EntityItem)entity).getEntityItem().getDisplayName(), i.xCoord, i.yCoord, i.zCoord, 0.003f, -1, false);
                RenderUtil.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            isRendering = false;
            ESP.mc.gameSettings.gammaSetting = gammaSetting;
            ESP.mc.gameSettings.fancyGraphics = fancyGraphics;
        }
    }

    private void render(ModelRenderEvent.Pre event) {
        event.getModel().render((Entity)event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());
    }

    boolean isValid(Entity entity) {
        Entity renderEntity = RenderUtil.getEntity();
        return entity != null && !entity.isDead && !entity.equals((Object)renderEntity) && (EntityType.isAnimal(entity) && this.animals.getValue() != false || EntityType.isMonster(entity) && this.monsters.getValue() != false || entity instanceof EntityEnderCrystal && this.misc.getValue() != false || entity instanceof EntityPlayer && this.players.getValue() != false || EntityType.isVehicle(entity) && this.vehicles.getValue() != false);
    }

    void drawTileEntities() {
        Frustum frustum = new Frustum();
        EntityPlayerSP renderEntity = mc.func_175606_aa() == null ? ESP.mc.thePlayer : mc.func_175606_aa();
        try {
            double x = renderEntity.posX;
            double y = renderEntity.posY;
            double z = renderEntity.posZ;
            frustum.setPosition(x, y, z);
            for (TileEntity tileEntity : ESP.mc.theWorld.loadedTileEntityList) {
                if (!(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityEnderChest) || ESP.mc.thePlayer.getDistance((double)tileEntity.func_174877_v().func_177958_n(), (double)tileEntity.func_174877_v().func_177956_o(), (double)tileEntity.func_174877_v().func_177952_p()) > (double)this.storageRange.getValue().floatValue()) continue;
                double posX = (double)tileEntity.func_174877_v().func_177958_n() - Interpolation.getRenderPosX();
                double posY = (double)tileEntity.func_174877_v().func_177956_o() - Interpolation.getRenderPosY();
                double posZ = (double)tileEntity.func_174877_v().func_177952_p() - Interpolation.getRenderPosZ();
                AxisAlignedBB bb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(posX, posY, posZ);
                if (tileEntity instanceof TileEntityChest) {
                    TileEntityChest adjacent = null;
                    if (((TileEntityChest)tileEntity).adjacentChestXNeg != null) {
                        adjacent = ((TileEntityChest)tileEntity).adjacentChestXNeg;
                    }
                    if (((TileEntityChest)tileEntity).adjacentChestXPos != null) {
                        adjacent = ((TileEntityChest)tileEntity).adjacentChestXPos;
                    }
                    if (((TileEntityChest)tileEntity).adjacentChestZNeg != null) {
                        adjacent = ((TileEntityChest)tileEntity).adjacentChestZNeg;
                    }
                    if (((TileEntityChest)tileEntity).adjacentChestZPos != null) {
                        adjacent = ((TileEntityChest)tileEntity).adjacentChestZPos;
                    }
                    if (adjacent != null) {
                        bb = bb.union(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset((double)adjacent.func_174877_v().func_177958_n() - Interpolation.getRenderPosX(), (double)adjacent.func_174877_v().func_177956_o() - Interpolation.getRenderPosY(), (double)adjacent.func_174877_v().func_177952_p() - Interpolation.getRenderPosZ()));
                    }
                }
                GL11.glPushMatrix();
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glDisable((int)3553);
                GL11.glEnable((int)2848);
                GL11.glDisable((int)2929);
                GL11.glDepthMask((boolean)false);
                this.colorTileEntityInside(tileEntity);
                RenderUtil.drawBox(bb);
                this.colorTileEntity(tileEntity);
                RenderUtil.drawOutline(bb, this.lineWidth.getValue().floatValue());
                GL11.glDisable((int)2848);
                GL11.glEnable((int)3553);
                GL11.glEnable((int)2929);
                GL11.glDepthMask((boolean)true);
                GL11.glDisable((int)3042);
                RenderUtil.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void colorTileEntityInside(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            if (((TileEntityChest)tileEntity).getChestType() == BlockChest.Type.TRAP) {
                RenderUtil.color(new Color(250, 54, 0, 60));
            } else {
                RenderUtil.color(new Color(234, 183, 88, 60));
            }
        } else if (tileEntity instanceof TileEntityEnderChest) {
            RenderUtil.color(new Color(174, 0, 255, 60));
        }
    }

    protected void colorTileEntity(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            if (((TileEntityChest)tileEntity).getChestType() == BlockChest.Type.TRAP) {
                RenderUtil.color(new Color(250, 54, 0, 255));
            } else {
                RenderUtil.color(new Color(234, 183, 88, 255));
            }
        } else if (tileEntity instanceof TileEntityEnderChest) {
            RenderUtil.color(new Color(174, 0, 255, 255));
        }
    }

    protected Color getEntityColor(Entity entity) {
        if (entity instanceof EntityItem) {
            return new Color(255, 255, 255, 255);
        }
        if (EntityType.isVehicle(entity) && this.vehicles.getValue().booleanValue()) {
            return new Color(200, 100, 0, 255);
        }
        if (EntityType.isAnimal(entity) && this.animals.getValue().booleanValue()) {
            return new Color(0, 200, 0, 255);
        }
        if (EntityType.isMonster(entity) || EntityType.isAngry(entity) && this.monsters.getValue().booleanValue()) {
            return new Color(200, 60, 60, 255);
        }
        if (entity instanceof EntityEnderCrystal && this.misc.getValue().booleanValue()) {
            return new Color(200, 100, 200, 255);
        }
        if (entity instanceof EntityPlayer && this.players.getValue().booleanValue()) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.isInvisible()) {
                return this.invisibleColor.getValue();
            }
            if (Cascade.friendManager.isFriend(player)) {
                return this.friendColor.getValue();
            }
            return this.color.getValue();
        }
        return this.color.getValue();
    }

    protected void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            this.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    protected void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)ESP.mc.displayWidth, (int)ESP.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencilDepthBufferID);
    }

    public void renderOne(float lineWidth) {
        this.checkSetupFBO();
        GL11.glPushMatrix();
        GL11.glEnable((int)32823);
        GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
        GL11.glPushAttrib((int)1048575);
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GlStateManager.func_187401_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.func_179132_a((boolean)false);
        GL11.glLineWidth((float)lineWidth);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2960);
        GL11.glClear((int)1024);
        GL11.glClearStencil((int)15);
        GL11.glStencilFunc((int)512, (int)1, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public void renderTwo() {
        GL11.glStencilFunc((int)512, (int)0, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public void renderThree() {
        GL11.glStencilFunc((int)514, (int)1, (int)15);
        GL11.glStencilOp((int)7680, (int)7680, (int)7680);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public void renderFour(Color color) {
        RenderUtil.color(color);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)10754);
        GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
    }

    public void renderFive() {
        GL11.glPolygonOffset((float)1.0f, (float)2000000.0f);
        GL11.glDisable((int)10754);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2960);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glPopAttrib();
        GL11.glPolygonOffset((float)1.0f, (float)2000000.0f);
        GL11.glDisable((int)32823);
        GL11.glPopMatrix();
    }

    public boolean shouldHurt() {
        return this.isEnabled() && isRendering && this.hurt.getValue() != false;
    }
}

