package yancey.commandfallingblock.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12000
import net.minecraft.util.math.random.Random;
//#else
//$$ import java.util.Random;
//#endif

//#if MC>=11802
import net.minecraft.client.render.entity.EntityRendererFactory;
//#else
//$$ import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

//#if MC>=12102
import net.minecraft.block.Blocks;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.EmptyBlockRenderView;
//#else
//$$ import net.minecraft.client.texture.SpriteAtlasTexture;
//$$ import net.minecraft.util.Identifier;
//$$ import net.minecraft.world.World;
//#endif

//#if MC>=12104
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
//#endif

@Environment(value = EnvType.CLIENT)
public class RenderBetterFallingBlock
        //#if MC>=12102
        extends EntityRenderer<EntityBetterFallingBlock, RenderBetterFallingBlock.BetterFallingBlockEntityRenderState>
        //#else
        //$$ extends EntityRenderer<EntityBetterFallingBlock>
        //#endif

{

    //#if MC>=11802
    private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
    //#else
    //$$ private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = BlockEntityRenderDispatcher.INSTANCE;
    //#endif

    //#if MC>=12000
    private final BlockRenderManager blockRenderManager;
    //#endif

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

        //#if MC>=12000
        this.blockRenderManager = context.getBlockRenderManager();
        //#endif
    }

    //#if MC>=12102
    @Override
    public BetterFallingBlockEntityRenderState createRenderState() {
        return new BetterFallingBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(EntityBetterFallingBlock entity, BetterFallingBlockEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        //#if MC>=12104
        state.tickDelta = tickDelta;
        //#endif
        state.fallingBlockPos = entity.getFallingBlockPos();
        state.currentPos = BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        state.blockState = entity.dataBlock.blockState;
        state.world = entity.getWorld();
        state.blockEntity = entity.blockEntity;
        if (state.blockEntity != null) {
            state.blockEntity.setWorld(entity.getWorld());
        }
    }

    //#endif

    @Override
    public void render(
            //#if MC>=12102
            BetterFallingBlockEntityRenderState renderState,
            //#else
            //$$ EntityBetterFallingBlock entity,
            //$$ float yaw,
            //$$ float tickDelta,
            //#endif
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light
    ) {
        //#if MC>=12102
        BlockState blockState = renderState.blockState;
        //#else
        //$$ BlockState blockState = entity.dataBlock.blockState;
        //#endif
        BlockRenderType renderType = blockState.getRenderType();
        //#if MC>=12102
        BlockRenderView world = renderState.world;
        //#elseif MC>=11802
        //$$ World world = entity.getWorld();
        //#else
        //$$ World world = entity.world;
        //#endif
        //#if MC>=12102
        BlockEntity blockEntity = renderState.blockEntity;
        //#else
        //$$ BlockEntity blockEntity = entity.blockEntity;
        //#endif
        if (renderType == BlockRenderType.MODEL) {
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            //#if MC<12000
            //$$ BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            //#endif
            blockRenderManager.getModelRenderer().render(
                    world,
                    blockRenderManager.getModel(blockState),
                    blockState,
                    //#if MC>=12102
                    renderState.currentPos,
                    //#elseif MC>=12000
                    //$$ BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
                    //#else
                    //$$ new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
                    //#endif
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    false,
                    //#if MC>=12000
                    Random.create(),
                    //#else
                    //$$ new Random(),
                    //#endif
                    //#if MC>=12102
                    blockState.getRenderingSeed(renderState.fallingBlockPos),
                    //#else
                    //$$ blockState.getRenderingSeed(entity.getFallingBlockPos()),
                    //#endif
                    OverlayTexture.DEFAULT_UV
            );
            matrixStack.pop();
        } else if (blockEntity != null) {
            //#if MC>=12102
            //#elseif MC>=11802
            //$$ blockEntity.setWorld(world);
            //#else
            //$$ entity.blockEntity.setLocation(world, entity.getFallingBlockPos());
            //#endif

            //#if MC>=12104
            BlockEntityRenderer<BlockEntity> blockEntityBlockEntityRenderer = blockEntityRenderDispatcher.get(blockEntity);
            if (blockEntityBlockEntityRenderer != null) {
                matrixStack.push();
                matrixStack.translate(-0.5, 0.0, -0.5);
                blockEntityBlockEntityRenderer.render(blockEntity, renderState.tickDelta, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
            }
            //#else
            //$$ matrixStack.push();
            //$$ matrixStack.translate(-0.5, 0.0, -0.5);
            //$$ blockEntityRenderDispatcher.renderEntity(blockEntity, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
            //$$ matrixStack.pop();
            //#endif

        } else {
            return;
        }
        //#if MC>=12102
        super.render(renderState, matrixStack, vertexConsumerProvider, light);
        //#else
        //$$ super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
        //#endif
    }

    //#if MC<12102
    //$$ @Override
    //$$ @SuppressWarnings("deprecation")
    //$$ public Identifier getTexture(EntityBetterFallingBlock entityBetterFallingBlock) {
    //$$     return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    //$$ }
    //#endif

    //#if MC>=12102
    public static class BetterFallingBlockEntityRenderState extends EntityRenderState {
        //#if MC>=12104
        public float tickDelta;
        //#endif
        public BlockPos fallingBlockPos;
        public BlockPos currentPos;
        public BlockState blockState;
        public BlockRenderView world;
        public BlockEntity blockEntity;

        public BetterFallingBlockEntityRenderState() {
            //#if MC>=12104
            this.tickDelta = 0;
            //#endif
            this.fallingBlockPos = BlockPos.ORIGIN;
            this.currentPos = BlockPos.ORIGIN;
            this.blockState = Blocks.SAND.getDefaultState();
            this.world = EmptyBlockRenderView.INSTANCE;
            this.blockEntity = null;
        }
    }
    //#endif

}


