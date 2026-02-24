package yancey.commandfallingblock.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {

    @Accessor
    void setBlockState(BlockState blockState);

}
