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
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12001
import net.minecraft.util.math.random.Random;
//#else
//$$ import java.util.Random;
//#endif

//#if MC>=11802
import net.minecraft.client.render.entity.EntityRendererFactory;
//#else
//$$ import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

@Environment(value = EnvType.CLIENT)
public class RenderBetterFallingBlock extends EntityRenderer<EntityBetterFallingBlock> {

    //#if MC>=11802
    private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
    //#else
    //$$ private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = BlockEntityRenderDispatcher.INSTANCE;
    //#endif
    private final BlockRenderManager blockRenderManager;

    public RenderBetterFallingBlock(
            //#if MC>=11802
            EntityRendererFactory.Context context
            //#else
            //$$ EntityRenderDispatcher dispatcher
            //#endif
    ) {
        //#if MC>=11802
        super(context);
        //#else
        //$$ super(dispatcher);
        //#endif
        this.shadowRadius = 0.5f;
        //#if MC>=12001
        this.blockRenderManager = context.getBlockRenderManager();
        //#else
        //$$ this.blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        //#endif
    }

    @Override
    public void render(EntityBetterFallingBlock entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        BlockState blockState = entity.dataBlock.blockState;
        BlockRenderType renderType = blockState.getRenderType();
        //#if MC>=11802
        World world = entity.getWorld();
        //#else
        //$$ World world = entity.world;
        //#endif
        if (renderType == BlockRenderType.MODEL) {
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            blockRenderManager.getModelRenderer().render(
                    world,
                    blockRenderManager.getModel(blockState),
                    blockState,
                    //#if MC>=12001
                    BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
                    //#else
                    //$$ new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
                    //#endif
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    false,
                    //#if MC>=12001
                    Random.create(),
                    //#else
                    //$$ new Random(),
                    //#endif
                    blockState.getRenderingSeed(entity.getFallingBlockPos()),
                    OverlayTexture.DEFAULT_UV
            );
            matrixStack.pop();
        } else if (entity.blockEntity != null) {
            //#if MC>=11802
            entity.blockEntity.setWorld(world);
            //#else
            //$$ entity.blockEntity.setLocation(world, entity.getFallingBlockPos());
            //#endif
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
