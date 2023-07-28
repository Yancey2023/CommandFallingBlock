package yancey.commandfallingblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.data.DataFallingBlock;
import yancey.commandfallingblock.mixin.BlockStateArgumentAccessor;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FallingBlockCommand {

    /*
    fallingblock moveFromPos <start> <motionX> <motionY> <motionZ> <block>
    fallingblock moveFromPosByTick <start> <motionX> <motionY> <motionZ> <tick> <block>
    fallingblock moveFromPosToPos <start> <end> <motionY> <block>
    fallingblock moveToPosByTick <end> <motionX> <motionY> <motionZ> <tick> <block>
    fallingblock moveToPosByYMove <end> <motionX> <motionY> <motionZ> <yMove> <block>
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("fallingblock")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("moveFromPos")
                        .then(argument("start", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motion", Vec3ArgumentType.vec3()))
                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                        .executes(context -> {
                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                            DataFallingBlock.moveFromBlockPos(
                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                    BlockPosArgumentType.getLoadedBlockPos(context, "start"),
                                                    Vec3ArgumentType.getVec3(context, "motion")
                                            ).run(context.getSource().getWorld());
                                            return 1;
                                        })
                                )
                        )
                ).then(literal("moveFromPosByTick")
                        .then(argument("start", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motion", Vec3ArgumentType.vec3()))
                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                        .then(argument("tick", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                            DataFallingBlock.moveFromBlockPosByTick(
                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "start"),
                                                                    Vec3ArgumentType.getVec3(context, "motion"),
                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                            ).run(context.getSource().getWorld());
                                                            return 1;
                                                        }
                                                )
                                        )
                                )
                        )
                ).then(literal("moveFromPosToPos")
                        .then(argument("start", BlockPosArgumentType.blockPos())
                                .then(argument("end", BlockPosArgumentType.blockPos())
                                        .then(argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                        .executes(context -> {
                                                                    BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                    DataFallingBlock dataFallingBlock = DataFallingBlock.moveFromBlockPosToBlockPos(
                                                                            new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                            BlockPosArgumentType.getLoadedBlockPos(context, "start"),
                                                                            BlockPosArgumentType.getLoadedBlockPos(context, "end"),
                                                                            DoubleArgumentType.getDouble(context, "motionY")
                                                                    );
                                                                    if (dataFallingBlock == null) {
                                                                        throw new CommandException(Text.translatable("command.fallingblock.failToCalculate"));
                                                                    }
                                                                    dataFallingBlock.run(context.getSource().getWorld());
                                                                    return 1;
                                                                }
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveToPosByTick")
                        .then(argument("end", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motion", Vec3ArgumentType.vec3())
                                        .then(argument("tick", IntegerArgumentType.integer(0))
                                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                        .executes(context -> {
                                                                    BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                    DataFallingBlock.moveToBlockPosByTick(
                                                                            new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                            BlockPosArgumentType.getLoadedBlockPos(context, "end"),
                                                                            Vec3ArgumentType.getVec3(context, "motion"),
                                                                            IntegerArgumentType.getInteger(context, "tick")
                                                                    ).run(context.getSource().getWorld());
                                                                    return 1;
                                                                }
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveToPosByYMove")
                        .then(argument("end", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motion", Vec3ArgumentType.vec3())
                                        .then(argument("yMove", IntegerArgumentType.integer())
                                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                        .executes(context -> {
                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                            DataFallingBlock.moveToBlockPosByYMove(
                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "end"),
                                                                    Vec3ArgumentType.getVec3(context, "motion"),
                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                            ).run(context.getSource().getWorld());
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                )
        );
    }
}