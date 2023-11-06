package yancey.commandfallingblock.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.Random;

@Environment(value = EnvType.CLIENT)
public class RenderBetterFallingBlock extends EntityRenderer<EntityBetterFallingBlock> {

    public RenderBetterFallingBlock(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(EntityBetterFallingBlock entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        BlockState blockState = entity.dataBlock.blockState;
        BlockRenderType renderType = blockState.getRenderType();
        if (renderType == BlockRenderType.MODEL) {
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                    entity.world,
                    MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState),
                    blockState,
                    new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    false,
                    new Random(),
                    blockState.getRenderingSeed(entity.getFallingBlockPos()),
                    OverlayTexture.DEFAULT_UV
            );
            matrixStack.pop();
        } else if (entity.blockEntity != null) {
            entity.blockEntity.setLocation(entity.world, entity.getFallingBlockPos());
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(entity.blockEntity, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        } else {
            return;
        }
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(EntityBetterFallingBlock entityBetterFallingBlock) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
