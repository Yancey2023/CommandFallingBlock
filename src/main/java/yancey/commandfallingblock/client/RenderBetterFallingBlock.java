package yancey.commandfallingblock.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

@Environment(value= EnvType.CLIENT)
public class RenderBetterFallingBlock extends EntityRenderer<EntityBetterFallingBlock> {

    private final BlockRenderManager blockRenderManager;

    public RenderBetterFallingBlock(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderManager();
    }
    
    @Override
    public void render(EntityBetterFallingBlock entityBetterFallingBlock, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BlockState blockState = entityBetterFallingBlock.getBlockState();
        if (blockState.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        World world = entityBetterFallingBlock.getWorld();
        if (blockState == world.getBlockState(entityBetterFallingBlock.getBlockPos()) || blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        matrixStack.push();
        BlockPos blockPos = BlockPos.ofFloored(entityBetterFallingBlock.getX(), entityBetterFallingBlock.getBoundingBox().maxY, entityBetterFallingBlock.getZ());
        matrixStack.translate(-0.5, 0.0, -0.5);
        this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(blockState), blockState, blockPos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, Random.create(), blockState.getRenderingSeed(entityBetterFallingBlock.getFallingBlockPos()), OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(entityBetterFallingBlock, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(EntityBetterFallingBlock entityBetterFallingBlock) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
