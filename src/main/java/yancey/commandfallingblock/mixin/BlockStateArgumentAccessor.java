package yancey.commandfallingblock.mixin;

import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStateArgument.class)
public interface BlockStateArgumentAccessor {

    @Accessor("data")
    NbtCompound getData();

}
