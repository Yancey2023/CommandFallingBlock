package yancey.commandfallingblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.data.DataFallingBlock;
import yancey.commandfallingblock.mixin.BlockStateArgumentAccessor;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FallingBlockCommand {

    /*
    fallingblock moveFromPos <start> <motionX> <motionY> <motionZ> <hasGravity> <block>
    fallingblock moveFromBlockPos <posStart> <motion> <hasGravity> <block>
    fallingblock moveFromPosByTick <start> <motionX> <motionY> <motionZ> <hasGravity> <tick> <block>
    fallingblock moveFromBlockPosByTick <posStart> <motion> <hasGravity> <tick> <block>
    fallingblock moveToPosByTick <end> <motionX> <motionY> <motionZ> <hasGravity> <tick> <block>
    fallingblock moveToBlockPosByTick <posEnd> <motion> <hasGravity> <tick> <block>
    fallingblock moveToPosByYMove <end> <motionX> <motionY> <motionZ> <yMove> <hasGravity> <block>
    fallingblock moveToBlockPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block>
    fallingblock moveFromPosToPosByMotionY <start> <end> <motionY> <block>
    fallingblock moveFromBlockPosToBlockPosByMotionY <posStart> <posEnd> <motionY> <block>
    fallingblock moveFromPosToPosByTick <posStart> <posEnd> <tick> <block>
    fallingblock moveFromBlockPosToBlockPosByTick <posStart> <posEnd> <tick> <block>
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("fallingblock")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("moveFromPos")
                        .then(argument("start", Vec3ArgumentType.vec3(false))
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg()))
                                                .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                .executes(context -> {
                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                            DataFallingBlock.moveFromPos(
                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                    Vec3ArgumentType.getVec3(context, "start"),
                                                                                    new Vec3d(
                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                    ),
                                                                                    BoolArgumentType.getBool(context, "hasGravity")
                                                                            ).run(context.getSource().getWorld());
                                                                            return 1;
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveFromBlockPos")
                        .then(argument("start", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg()))
                                                .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                .executes(context -> {
                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                            DataFallingBlock.moveFromBlockPos(
                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "start"),
                                                                                    new Vec3d(
                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                    ),
                                                                                    BoolArgumentType.getBool(context, "hasGravity")
                                                                            ).run(context.getSource().getWorld());
                                                                            return 1;
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveFromPosByTick")
                        .then(argument("start", Vec3ArgumentType.vec3(false))
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
                                                        .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                                .then(argument("tick", IntegerArgumentType.integer(0))
                                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                                .executes(context -> {
                                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                                            DataFallingBlock.moveFromPosByTick(
                                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                                    Vec3ArgumentType.getVec3(context, "start"),
                                                                                                    new Vec3d(
                                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                                    ),
                                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                                                            ).run(context.getSource().getWorld());
                                                                                            return 1;
                                                                                        }
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveFromBlockPosByTick")
                        .then(argument("start", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
                                                        .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                                .then(argument("tick", IntegerArgumentType.integer(0))
                                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                                .executes(context -> {
                                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                                            DataFallingBlock.moveFromBlockPosByTick(
                                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "start"),
                                                                                                    new Vec3d(
                                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                                    ),
                                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                                                            ).run(context.getSource().getWorld());
                                                                                            return 1;
                                                                                        }
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveToPosByTick")
                        .then(argument("end", Vec3ArgumentType.vec3(false))
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
                                                        .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                                .then(argument("tick", IntegerArgumentType.integer(0))
                                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                                .executes(context -> {
                                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                                            DataFallingBlock.moveToPosByTick(
                                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                                    Vec3ArgumentType.getVec3(context, "end"),
                                                                                                    new Vec3d(
                                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                                    ),
                                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                                                            ).run(context.getSource().getWorld());
                                                                                            return 1;
                                                                                        }
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveToBlockPosByTick")
                        .then(argument("end", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
                                                        .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                                .then(argument("tick", IntegerArgumentType.integer(0))
                                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                                .executes(context -> {
                                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                                            DataFallingBlock.moveToBlockPosByTick(
                                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "end"),
                                                                                                    new Vec3d(
                                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                                    ),
                                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                                                            ).run(context.getSource().getWorld());
                                                                                            return 1;
                                                                                        }
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveToPosByYMove")
                        .then(argument("end", Vec3ArgumentType.vec3(false))
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
                                                        .then(argument("yMove", IntegerArgumentType.integer())
                                                                .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                                .executes(context -> {
                                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                                            DataFallingBlock dataFallingBlock = DataFallingBlock.moveToPosByYMove(
                                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                                    Vec3ArgumentType.getVec3(context, "end"),
                                                                                                    new Vec3d(
                                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                                    ),
                                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                                    IntegerArgumentType.getInteger(context, "tick")
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
                                        )
                                )
                        )
                ).then(literal("moveToBlockPosByYMove")
                        .then(argument("end", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
                                                        .then(argument("yMove", IntegerArgumentType.integer())
                                                                .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                                .executes(context -> {
                                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                                            DataFallingBlock dataFallingBlock = DataFallingBlock.moveToBlockPosByYMove(
                                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "end"),
                                                                                                    new Vec3d(
                                                                                                            DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                            DoubleArgumentType.getDouble(context, "motionZ")
                                                                                                    ),
                                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                                    IntegerArgumentType.getInteger(context, "tick")
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
                                        )
                                )
                        )
                ).then(literal("moveFromPosToPosByMotionY")
                        .then(argument("start", Vec3ArgumentType.vec3(false))
                                .then(argument("end", Vec3ArgumentType.vec3(false))
                                        .then(argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                        .executes(context -> {
                                                                    BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                    DataFallingBlock dataFallingBlock = DataFallingBlock.moveFromPosToPosByMotionY(
                                                                            new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                            Vec3ArgumentType.getVec3(context, "start"),
                                                                            Vec3ArgumentType.getVec3(context, "end"),
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
                ).then(literal("moveFromBlockPosToBlockPosByMotionY")
                        .then(argument("start", BlockPosArgumentType.blockPos())
                                .then(argument("end", BlockPosArgumentType.blockPos())
                                        .then(argument("motionY", DoubleArgumentType.doubleArg())
                                                .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                        .executes(context -> {
                                                                    BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                    DataFallingBlock dataFallingBlock = DataFallingBlock.moveFromBlockPosToBlockPosByMotionY(
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
                ).then(literal("moveFromPosToPosByTick")
                        .then(argument("start", Vec3ArgumentType.vec3(false))
                                .then(argument("end", Vec3ArgumentType.vec3(false))
                                        .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                .then(argument("tick", IntegerArgumentType.integer(0))
                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                .executes(context -> {
                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                            DataFallingBlock.moveFromPosToPosByTick(
                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                    Vec3ArgumentType.getVec3(context, "start"),
                                                                                    Vec3ArgumentType.getVec3(context, "end"),
                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                                            ).run(context.getSource().getWorld());
                                                                            return 1;
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).then(literal("moveFromBlockPosToBlockPosByTick")
                        .then(argument("start", Vec3ArgumentType.vec3())
                                .then(argument("end", Vec3ArgumentType.vec3())
                                        .then(CommandManager.argument("hasGravity", BoolArgumentType.bool())
                                                .then(argument("tick", IntegerArgumentType.integer(0))
                                                        .then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
                                                                .executes(context -> {
                                                                            BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
                                                                            DataFallingBlock.moveFromBlockPosToBlockPosByTick(
                                                                                    new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData()),
                                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "start"),
                                                                                    BlockPosArgumentType.getLoadedBlockPos(context, "end"),
                                                                                    BoolArgumentType.getBool(context, "hasGravity"),
                                                                                    IntegerArgumentType.getInteger(context, "tick")
                                                                            ).run(context.getSource().getWorld());
                                                                            return 1;
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}