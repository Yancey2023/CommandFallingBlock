package yancey.commandfallingblock.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12000&&MC<12109
//$$ import net.minecraft.util.math.random.Random;
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
//#if MC<12109
//$$ import net.minecraft.world.BlockRenderView;
//#endif
import net.minecraft.world.EmptyBlockRenderView;
//#else
//$$ import net.minecraft.client.texture.SpriteAtlasTexture;
//$$ import net.minecraft.util.Identifier;
//$$ import net.minecraft.world.World;
//#endif

//#if MC>=12104
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
//#else
//$$ import net.minecraft.block.BlockRenderType;
//#endif

//#if MC>=12105&&MC<12109
//$$ import net.minecraft.fluid.FluidState;
//$$ import net.minecraft.registry.entry.RegistryEntry;
//$$ import net.minecraft.util.math.Direction;
//$$ import net.minecraft.world.biome.Biome;
//$$ import net.minecraft.world.biome.ColorResolver;
//$$ import net.minecraft.world.chunk.light.LightingProvider;
//$$ import org.jetbrains.annotations.Nullable;
//#endif

//#if MC>=12109
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
//#else
//$$ import net.minecraft.block.BlockState;
//$$ import net.minecraft.client.render.OverlayTexture;
//$$ import net.minecraft.client.render.RenderLayers;
//$$ import net.minecraft.client.render.VertexConsumerProvider;
//$$ import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
//$$ import net.minecraft.client.render.block.BlockRenderManager;
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
    private static final BlockEntityRenderManager blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
    //#else
    //$$ private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = BlockEntityRenderDispatcher.INSTANCE;
    //#endif

    //#if MC>=12000&&MC<12109
    //$$ private final BlockRenderManager blockRenderManager;
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

        //#if MC>=12000&&MC<12109
        //$$ this.blockRenderManager = context.getBlockRenderManager();
        //#endif
    }

    //#if MC>=12109
    public <T extends BlockEntityRenderState> boolean renderBlockEntity(
            BetterFallingBlockEntityRenderState renderState,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraState
    ) {
        BlockEntityRenderer<BlockEntity, T> blockEntityBlockEntityRenderer = blockEntityRenderDispatcher.get(renderState.blockEntity);
        if (blockEntityBlockEntityRenderer != null) {
            T blockEntityRenderState = blockEntityBlockEntityRenderer.createRenderState();
            blockEntityBlockEntityRenderer.updateRenderState(
                    renderState.blockEntity,
                    blockEntityRenderState,
                    renderState.tickDelta,
                    cameraState.pos,
                    null
            );
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            blockEntityBlockEntityRenderer.render(
                    blockEntityRenderState,
                    matrixStack,
                    queue,
                    cameraState
            );
            matrixStack.pop();
            return true;
        } else {
            return false;
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
            //#if MC>=12109
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraState
            //#else
            //$$ VertexConsumerProvider vertexConsumerProvider,
            //$$ int light
            //#endif
    ) {
        //#if MC>=12109
        //#elseif MC>=12102
        //$$ BlockState blockState = renderState.blockState;
        //#else
        //$$ BlockState blockState = entity.dataBlock.blockState();
        //#endif
        //#if MC>=12105
        //#elseif MC>=12102
        //$$ BlockRenderView world = renderState.world;
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
        boolean isBlockEntityRenderer = false;
        if (blockEntity != null) {
            //#if MC>=12102
            //#elseif MC>=11802
            //$$ blockEntity.setWorld(world);
            //#else
            //$$ entity.blockEntity.setLocation(world, entity.getFallingBlockPos());
            //#endif

            //#if MC>=12109
            if (renderBlockEntity(renderState, matrixStack, queue, cameraState)) {
                isBlockEntityRenderer = true;
            }
            //#elseif MC>=12104
            //$$ BlockEntityRenderer<BlockEntity> blockEntityBlockEntityRenderer = blockEntityRenderDispatcher.get(blockEntity);
            //$$ if (blockEntityBlockEntityRenderer != null) {
            //$$    matrixStack.push();
            //$$    matrixStack.translate(-0.5, 0.0, -0.5);
            //$$    blockEntityBlockEntityRenderer.render(
            //$$            blockEntity,
            //$$            renderState.tickDelta,
            //$$            matrixStack,
            //$$            vertexConsumerProvider,
            //$$            light,
            //$$            OverlayTexture.DEFAULT_UV
            //$$            //#if MC>=12105
            //$$            ,
            //$$            blockEntityRenderDispatcher.camera.getPos()
            //$$            //#endif
            //$$    );
            //$$    matrixStack.pop();
            //$$    isBlockEntityRenderer = true;
            //$$ }
            //#else
            //$$ isBlockEntityRenderer = blockState.getRenderType() != BlockRenderType.MODEL;
            //$$ if (isBlockEntityRenderer) {
            //$$     matrixStack.push();
            //$$     matrixStack.translate(-0.5, 0.0, -0.5);
            //$$     blockEntityRenderDispatcher.renderEntity(blockEntity, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
            //$$     matrixStack.pop();
            //$$  }
            //#endif
        }
        if (!isBlockEntityRenderer) {
            matrixStack.push();
            matrixStack.translate(-0.5, 0.0, -0.5);
            //#if MC>=12109
            queue.submitMovingBlock(matrixStack, renderState.movingBlockRenderState);
            //#else
            //$$ //#if MC<12000
            //$$ //$$ BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            //$$ //#endif
            //$$ blockRenderManager.getModelRenderer().render(
            //$$         //#if MC>=12105
            //$$         renderState,
            //$$         //#else
            //$$         //$$ world,
            //$$         //#endif
            //$$         //#if MC>=12105
            //$$         blockRenderManager.getModel(blockState).getParts(Random.create(blockState.getRenderingSeed(renderState.fallingBlockPos))),
            //$$         //#else
            //$$         //$$ blockRenderManager.getModel(blockState),
            //$$         //#endif
            //$$         blockState,
            //$$         //#if MC>=12102
            //$$         renderState.entityBlockPos,
            //$$         //#elseif MC>=12000
            //$$         //$$ BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
            //$$         //#else
            //$$         //$$ new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
            //$$         //#endif
            //$$         matrixStack,
            //$$         vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
            //$$         false,
            //$$         //#if MC>=12105
            //$$         //#elseif MC>=12000
            //$$         //$$ Random.create(),
            //$$         //#else
            //$$         //$$ new Random(),
            //$$         //#endif
            //$$         //#if MC>=12105
            //$$         //#elseif MC>=12102
            //$$         //$$ blockState.getRenderingSeed(renderState.fallingBlockPos),
            //$$         //#else
            //$$         //$$ blockState.getRenderingSeed(entity.getFallingBlockPos()),
            //$$         //#endif
            //$$         OverlayTexture.DEFAULT_UV
            //$$ );
            //#endif
            matrixStack.pop();
        }
        //#if MC>=12109
        super.render(renderState, matrixStack, queue, cameraState);
        //#elseif MC>=12102
        //$$ super.render(renderState, matrixStack, vertexConsumerProvider, light);
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
        //#if MC>=12109
        state.movingBlockRenderState.fallingBlockPos = entity.getFallingBlockPos();
        state.movingBlockRenderState.entityBlockPos = BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        state.movingBlockRenderState.blockState = entity.dataBlock.blockState();
        state.movingBlockRenderState.world = entity.getEntityWorld();
        state.movingBlockRenderState.biome = entity.getEntityWorld().getBiome(state.movingBlockRenderState.entityBlockPos);
        //#else
        //$$ state.fallingBlockPos = entity.getFallingBlockPos();
        //$$ state.entityBlockPos = BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        //$$ state.blockState = entity.dataBlock.blockState();
        //$$ state.world = entity.getWorld();
        //$$ //#if MC>=12105
        //$$ state.biome = entity.getWorld().getBiome(state.fallingBlockPos);
        //$$ //#endif
        //#endif
        state.blockEntity = entity.blockEntity;
        if (state.blockEntity != null) {
            state.blockEntity.setWorld(entity.getEntityWorld());
        }
    }
    //#endif

    //#if MC>=12102
    public static class BetterFallingBlockEntityRenderState extends EntityRenderState
            //#if MC>=12105&&MC<12109
            //$$ implements BlockRenderView
            //#endif
    {
        //#if MC>=12104
        public float tickDelta;
        //#endif
        public BlockEntity blockEntity;
        //#if MC>=12109
        public MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
        //#else
        //$$ public BlockPos fallingBlockPos;
        //$$ public BlockPos entityBlockPos;
        //$$ public BlockState blockState;
        //$$ //#if MC>=12105
        //$$ @Nullable
        //$$ public RegistryEntry<Biome> biome;
        //$$ //#endif
        //$$ public BlockRenderView world;
        //#endif

        public BetterFallingBlockEntityRenderState() {
            //#if MC>=12104
            this.tickDelta = 0;
            //#endif
            //#if MC>=12109
            movingBlockRenderState.fallingBlockPos = BlockPos.ORIGIN;
            movingBlockRenderState.entityBlockPos = BlockPos.ORIGIN;
            movingBlockRenderState.blockState = Blocks.SAND.getDefaultState();
            movingBlockRenderState.world = EmptyBlockRenderView.INSTANCE;
            movingBlockRenderState.biome = null;
            //#else
            //$$ this.fallingBlockPos = BlockPos.ORIGIN;
            //$$ this.entityBlockPos = BlockPos.ORIGIN;
            //$$ this.blockState = Blocks.SAND.getDefaultState();
            //$$ this.world = EmptyBlockRenderView.INSTANCE;
            //$$ //#if MC>=12105
            //$$ this.biome = null;
            //$$ //#endif
            //#endif
            this.blockEntity = null;
        }

        //#if MC>=12105&&MC<12109
        //$$ @Override
        //$$ public float getBrightness(Direction direction, boolean shaded) {
        //$$     return this.world.getBrightness(direction, shaded);
        //$$ }
        //$$
        //$$ @Override
        //$$ public LightingProvider getLightingProvider() {
        //$$     return this.world.getLightingProvider();
        //$$ }
        //$$
        //$$ @Override
        //$$ public int getColor(BlockPos pos, ColorResolver colorResolver) {
        //$$     return this.biome == null ? -1 : colorResolver.getColor(this.biome.value(), pos.getX(), pos.getZ());
        //$$ }
        //$$
        //$$ @Override
        //$$ public BlockEntity getBlockEntity(BlockPos pos) {
        //$$     return null;
        //$$ }
        //$$
        //$$ @Override
        //$$ public BlockState getBlockState(BlockPos pos) {
        //$$     return pos.equals(this.entityBlockPos) ? this.blockState : Blocks.AIR.getDefaultState();
        //$$ }
        //$$
        //$$ @Override
        //$$ public FluidState getFluidState(BlockPos pos) {
        //$$     return this.getBlockState(pos).getFluidState();
        //$$ }
        //$$
        //$$ @Override
        //$$ public int getHeight() {
        //$$     return 1;
        //$$ }
        //$$
        //$$ @Override
        //$$ public int getBottomY() {
        //$$     return this.entityBlockPos.getY();
        //$$ }
        //#endif
    }
    //#endif

}
