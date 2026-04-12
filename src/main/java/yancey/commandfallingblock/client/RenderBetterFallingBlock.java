package yancey.commandfallingblock.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12109
//#elseif MC>=12000
//$$ import net.minecraft.util.RandomSource;
//#else
//$$ import java.util.Random;
//#endif

//#if MC>=11802
import net.minecraft.client.renderer.entity.EntityRendererProvider;
//#else
//$$ import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//#endif

//#if MC>=26.1
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.multiplayer.ClientLevel;
//#elseif MC>=12109
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.client.renderer.entity.state.EntityRenderState;
//$$ import net.minecraft.client.renderer.state.CameraRenderState;
//$$ import net.minecraft.world.level.EmptyBlockAndTintGetter;
//#elseif MC>=12102
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.client.renderer.entity.state.EntityRenderState;
//$$ import net.minecraft.world.level.BlockAndTintGetter;
//$$ import net.minecraft.world.level.EmptyBlockAndTintGetter;
//#else
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import net.minecraft.world.level.Level;
//#endif

//#if MC>=12104
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//#else
//$$ import net.minecraft.world.level.block.RenderShape;
//#endif

//#if MC>=12105&&MC<12109
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.world.level.biome.Biome;
//$$ import net.minecraft.world.level.ColorResolver;
//$$ import net.minecraft.world.level.lighting.LevelLightEngine;
//$$ import net.minecraft.world.level.material.FluidState;
//$$ import org.jetbrains.annotations.Nullable;
//#endif

//#if MC>=12109
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
//#else
//$$ import net.minecraft.client.renderer.texture.OverlayTexture;
//$$ import net.minecraft.client.renderer.ItemBlockRenderTypes;
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//$$ import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
//$$ import net.minecraft.client.renderer.block.BlockRenderDispatcher;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

//#if MC>=12111
import org.jspecify.annotations.NonNull;
//#else
//$$ import org.jetbrains.annotations.NotNull;
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
    private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    //#else
    //$$ private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = BlockEntityRenderDispatcher.instance;
    //#endif

    //#if MC>=12000&&MC<12109
    //$$ private final BlockRenderDispatcher blockRenderDispatcher;
    //#endif

    public RenderBetterFallingBlock(
            //#if MC>=11802
            EntityRendererProvider.Context context
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
        //$$ this.blockRenderDispatcher = context.getBlockRenderDispatcher();
        //#endif
    }

    //#if MC>=12109
    public <T extends BlockEntityRenderState> boolean renderBlockEntity(
            BetterFallingBlockEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraState
    ) {
        BlockEntityRenderer<BlockEntity, T> blockEntityBlockEntityRenderer = blockEntityRenderDispatcher.getRenderer(state.blockEntity);
        if (blockEntityBlockEntityRenderer != null) {
            T blockEntityRenderState = blockEntityBlockEntityRenderer.createRenderState();
            blockEntityBlockEntityRenderer.extractRenderState(
                    state.blockEntity,
                    blockEntityRenderState,
                    state.tickDelta,
                    cameraState.pos,
                    null
            );
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            blockEntityBlockEntityRenderer.submit(
                    blockEntityRenderState,
                    poseStack,
                    submitNodeCollector,
                    cameraState
            );
            poseStack.popPose();
            return true;
        } else {
            return false;
        }
    }
    //#endif

    @Override
    public void submit(
            //#if MC>=12102
            BetterFallingBlockEntityRenderState state,
            //#else
            //$$ EntityBetterFallingBlock entity,
            //$$ float yaw,
            //$$ float tickDelta,
            //#endif
            //#if MC>=12111
            @NonNull
            //#endif
            PoseStack poseStack,
            //#if MC>=12109
            //#if MC>=12111
            @NonNull
            //#endif
            SubmitNodeCollector submitNodeCollector,
            //#if MC>=12111
            @NonNull
            //#endif
            CameraRenderState cameraState
            //#else
            //$$ MultiBufferSource multiBufferSource,
            //$$ int light
            //#endif
    ) {
        //#if MC>=12109
        //#elseif MC>=12102
        //$$ BlockState blockState = state.blockState;
        //#else
        //$$ BlockState blockState = entity.dataBlock.blockState();
        //#endif
        //#if MC>=12105
        //#elseif MC>=12102
        //$$ BlockAndTintGetter level = state.level;
        //#elseif MC>=11802
        //$$ Level level = entity.level();
        //#else
        //$$ Level level = entity.level;
        //#endif
        //#if MC>=12102
        BlockEntity blockEntity = state.blockEntity;
        //#else
        //$$ BlockEntity blockEntity = entity.blockEntity;
        //#endif
        boolean isBlockEntityRenderer = false;
        if (blockEntity != null) {
            //#if MC>=12102
            //#elseif MC>=11802
            //$$ blockEntity.setLevel(level);
            //#else
            //$$ blockEntity.setLevelAndPosition(level, entity.getFallingBlockPos());
            //#endif

            //#if MC>=12109
            if (renderBlockEntity(state, poseStack, submitNodeCollector, cameraState)) {
                isBlockEntityRenderer = true;
            }
            //#elseif MC>=12104
            //$$ BlockEntityRenderer<BlockEntity> blockEntityBlockEntityRenderer = blockEntityRenderDispatcher.getRenderer(blockEntity);
            //$$ if (blockEntityBlockEntityRenderer != null) {
            //$$    poseStack.pushPose();
            //$$    poseStack.translate(-0.5, 0.0, -0.5);
            //$$    blockEntityBlockEntityRenderer.render(
            //$$            blockEntity,
            //$$            state.tickDelta,
            //$$            poseStack,
            //$$            multiBufferSource,
            //$$            light,
            //$$            OverlayTexture.NO_OVERLAY
            //$$            //#if MC>=12105
            //$$            ,
            //$$            blockEntityRenderDispatcher.camera.getPosition()
            //$$            //#endif
            //$$    );
            //$$    poseStack.popPose();
            //$$    isBlockEntityRenderer = true;
            //$$ }
            //#else
            //$$ isBlockEntityRenderer = blockState.getRenderShape() != RenderShape.MODEL;
            //$$ if (isBlockEntityRenderer) {
            //$$     poseStack.pushPose();
            //$$     poseStack.translate(-0.5, 0.0, -0.5);
            //$$     blockEntityRenderDispatcher.renderItem(blockEntity, poseStack, multiBufferSource, light, OverlayTexture.NO_OVERLAY);
            //$$     poseStack.popPose();
            //$$  }
            //#endif
        }
        if (!isBlockEntityRenderer) {
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            //#if MC>=12109
            submitNodeCollector.submitMovingBlock(poseStack, state.movingBlockRenderState);
            //#else
            //$$ //#if MC<12000
            //$$ //$$ BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
            //$$ //#endif
            //$$ blockRenderDispatcher.getModelRenderer().tesselateBlock(
            //$$         //#if MC>=12105
            //$$         state,
            //$$         //#else
            //$$         //$$ level,
            //$$         //#endif
            //$$         //#if MC>=12105
            //$$         blockRenderDispatcher.getBlockModel(blockState).collectParts(RandomSource.create(blockState.getSeed(state.fallingBlockPos))),
            //$$         //#else
            //$$         //$$ blockRenderDispatcher.getBlockModel(blockState),
            //$$         //#endif
            //$$         blockState,
            //$$         //#if MC>=12102
            //$$         state.entityBlockPos,
            //$$         //#elseif MC>=12000
            //$$         //$$ BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
            //$$         //#else
            //$$         //$$ new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ()),
            //$$         //#endif
            //$$         poseStack,
            //$$         multiBufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)),
            //$$         false,
            //$$         //#if MC>=12105
            //$$         //#elseif MC>=12000
            //$$         //$$ RandomSource.create(),
            //$$         //#else
            //$$         //$$ new Random(),
            //$$         //#endif
            //$$         //#if MC>=12105
            //$$         //#elseif MC>=12102
            //$$         //$$ blockState.getSeed(state.fallingBlockPos),
            //$$         //#else
            //$$         //$$ blockState.getSeed(entity.getFallingBlockPos()),
            //$$         //#endif
            //$$         OverlayTexture.NO_OVERLAY
            //$$ );
            //#endif
            poseStack.popPose();
        }
        //#if MC>=12109
        super.submit(state, poseStack, submitNodeCollector, cameraState);
        //#elseif MC>=12102
        //$$ super.render(state, poseStack, multiBufferSource, light);
        //#else
        //$$ super.render(entity, yaw, tickDelta, poseStack, multiBufferSource, light);
        //#endif
    }

    //#if MC<12102
    //$$ @Override
    //$$ @SuppressWarnings("deprecation")
    //$$ public @NotNull ResourceLocation getTextureLocation(EntityBetterFallingBlock entityBetterFallingBlock) {
    //$$     return TextureAtlas.LOCATION_BLOCKS;
    //$$ }
    //#endif

    //#if MC>=12102
    @Override
    public
    //#if MC>=12111
    @NonNull
    //#else
    //$$ @NotNull
    //#endif
    BetterFallingBlockEntityRenderState createRenderState() {
        return new BetterFallingBlockEntityRenderState();
    }

    @Override
    //#if MC>=12105
    @SuppressWarnings("resource")
    //#endif
    public void extractRenderState(
            //#if MC>=12111
            @NonNull
            //#else
            //$$ @NotNull
            //#endif
            EntityBetterFallingBlock entity,
            //#if MC>=12111
            @NonNull
            //#else
            //$$ @NotNull
            //#endif
            BetterFallingBlockEntityRenderState state,
            float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        //#if MC>=12104
        state.tickDelta = tickDelta;
        //#endif
        //#if MC>=12109
        state.movingBlockRenderState.randomSeedPos = entity.getFallingBlockPos();
        state.movingBlockRenderState.blockPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        state.movingBlockRenderState.blockState = entity.dataBlock.blockState();
        //#if MC>=26.1
        if (entity.level() instanceof ClientLevel clientLevel) {
            state.movingBlockRenderState.biome = clientLevel.getBiome(state.movingBlockRenderState.blockPos);
            state.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
            state.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
        }
        //#else
        //$$ state.movingBlockRenderState.level = entity.level();
        //$$ state.movingBlockRenderState.biome = entity.level().getBiome(state.movingBlockRenderState.blockPos);
        //#endif
        //#else
        //$$ state.fallingBlockPos = entity.getFallingBlockPos();
        //$$ state.entityBlockPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        //$$ state.blockState = entity.dataBlock.blockState();
        //$$ state.level = entity.level();
        //$$ //#if MC>=12105
        //$$ state.biome = entity.level().getBiome(state.fallingBlockPos);
        //$$ //#endif
        //#endif
        state.blockEntity = entity.blockEntity;
        if (state.blockEntity != null) {
            state.blockEntity.setLevel(entity.level());
        }
    }
    //#endif

    //#if MC>=12102
    public static class BetterFallingBlockEntityRenderState extends EntityRenderState
            //#if MC>=12105&&MC<12109
            //$$ implements BlockAndTintGetter
            //#endif
    {
        //#if MC>=12104
        public float tickDelta;
        //#endif
        public BlockEntity blockEntity;
        //#if MC>=12109
        public final MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
        //#else
        //$$ public BlockPos fallingBlockPos;
        //$$ public BlockPos entityBlockPos;
        //$$ public BlockState blockState;
        //$$ //#if MC>=12105
        //$$ @Nullable
        //$$ public Holder<Biome> biome;
        //$$ //#endif
        //$$ public BlockAndTintGetter level;
        //#endif

        public BetterFallingBlockEntityRenderState() {
            //#if MC>=12104
            this.tickDelta = 0;
            //#endif
            //#if MC>=12109
            movingBlockRenderState.randomSeedPos = BlockPos.ZERO;
            movingBlockRenderState.blockPos = BlockPos.ZERO;
            movingBlockRenderState.blockState = Blocks.SAND.defaultBlockState();
            //#if MC<26.1
            //$$ movingBlockRenderState.level = EmptyBlockAndTintGetter.INSTANCE;
            //#endif
            movingBlockRenderState.biome = null;
            //#else
            //$$ this.fallingBlockPos = BlockPos.ZERO;
            //$$ this.entityBlockPos = BlockPos.ZERO;
            //$$ this.blockState = Blocks.SAND.defaultBlockState();
            //$$ this.level = EmptyBlockAndTintGetter.INSTANCE;
            //$$ //#if MC>=12105
            //$$ this.biome = null;
            //$$ //#endif
            //#endif
            this.blockEntity = null;
        }

        //#if MC>=12105&&MC<12109
        //$$ @Override
        //$$ public float getShade(Direction direction, boolean shaded) {
        //$$     return this.level.getShade(direction, shaded);
        //$$ }
        //$$
        //$$ @Override
        //$$ public @NotNull LevelLightEngine getLightEngine() {
        //$$     return this.level.getLightEngine();
        //$$ }
        //$$
        //$$ @Override
        //$$ public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
        //$$     return this.biome == null ? -1 : colorResolver.getColor(this.biome.value(), pos.getX(), pos.getZ());
        //$$ }
        //$$
        //$$ @Override
        //$$ public BlockEntity getBlockEntity(BlockPos pos) {
        //$$     return null;
        //$$ }
        //$$
        //$$ @Override
        //$$ public @NotNull BlockState getBlockState(BlockPos pos) {
        //$$     return pos.equals(this.entityBlockPos) ? this.blockState : Blocks.AIR.defaultBlockState();
        //$$ }
        //$$
        //$$ @Override
        //$$ public @NotNull FluidState getFluidState(BlockPos pos) {
        //$$     return this.getBlockState(pos).getFluidState();
        //$$ }
        //$$
        //$$ @Override
        //$$ public int getHeight() {
        //$$     return 1;
        //$$ }
        //$$
        //$$ @Override
        //$$ public int getMinY() {
        //$$     return this.entityBlockPos.getY();
        //$$ }
        //#endif
    }
    //#endif

}
