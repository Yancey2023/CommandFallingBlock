package yancey.commandfallingblock.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockEntityAccessor {

    //#if MC>=12105
    @Accessor("blockState")
    //#else
    //$$ @Accessor("block")
    //#endif
    void setBlockState(BlockState block);

}
