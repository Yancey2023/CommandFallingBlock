package yancey.commandfallingblock.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

@Environment(value = EnvType.CLIENT)
public class RenderBetterFallingBlock extends EntityRenderer<EntityBetterFallingBlock> {

    private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
    private final BlockRenderManager blockRenderManager;

    public RenderBetterFallingBlock(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(EntityBetterFallingBlock entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        BlockState blockState = entity.dataBlock.blockState;
        BlockRenderType renderType = blockState.getRenderType();
        World world = entity.getWorld();
        if (renderType == BlockRenderType.MODEL) {
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            blockRenderManager.getModelRenderer().render(
                    world,
                    blockRenderManager.getModel(blockState),
                    blockState,
                    BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    false,
                    Random.create(),
                    blockState.getRenderingSeed(entity.getFallingBlockPos()),
                    OverlayTexture.DEFAULT_UV
            );
            matrixStack.pop();
        } else if (entity.blockEntity != null) {
            entity.blockEntity.setWorld(world);
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            blockEntityRenderDispatcher.renderEntity(entity.blockEntity, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        } else {
            return;
        }
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Identifier getTexture(EntityBetterFallingBlock entityBetterFallingBlock) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
