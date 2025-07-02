package yancey.commandfallingblock.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockEntityAccessor {

    //@formatter:off
    @Accessor
    //#if MC>=12105
    void setBlockState(BlockState block);
    //#else
    //$$ void setBlock(BlockState block);
    //#endif
    //@formatter:on

}
