package yancey.commandfallingblock.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;
import yancey.commandfallingblock.util.DataBlock;
import yancey.commandfallingblock.util.DataFallingBlock;
import yancey.commandfallingblock.mixin.BlockInputAccessor;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

//#if MC>=12000
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
//#else
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#endif

//#if MC>=12111
import net.minecraft.commands.Commands;
//#endif

public class FallingBlockCommand {

    /*
    fallingblock moveFromPos <posStart> <motion> <hasGravity> <block> [age]
    fallingblock moveFromBlockPos <posStart> <motion> <hasGravity> <block> [age]
    fallingblock moveFromPosByTick <posStart> <motion> <hasGravity> <tickMove> <block> [age]
    fallingblock moveFromBlockPosByTick <posStart> <motion> <hasGravity> <tickMove> <block> [age]
    fallingblock moveToPosByTick <posEnd> <motion> <hasGravity> <tickMove> <block> [age]
    fallingblock moveToBlockPosByTick <posEnd> <motion> <hasGravity> <tickMove> <block> [age]
    fallingblock moveToPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block> [age]
    fallingblock moveToBlockPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block> [age]
    fallingblock moveFromPosToPosByMotionY <posStart> <posEnd> <motionY> <block> [age]
    fallingblock moveFromBlockPosToBlockPosByMotionY <posStart> <posEnd> <motionY> <block> [age]
    fallingblock moveFromPosToPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]
    fallingblock moveFromBlockPosToBlockPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]
    */

    //#if MC>=12000
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("command.commandfallingblock.fallingblock.failedToCalculate"));
    //#else
    //$$ private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("command.commandfallingblock.fallingblock.failedToCalculate"));
    //#endif

    public static void register(
            //#if MC>=12000
            CommandBuildContext commandRegistryAccess,
            //#endif
            CommandDispatcher<CommandSourceStack> dispatcher) {
        Executor moveFromPos = FallingBlockCommand::moveFromPos;
        Executor moveFromPosByTick = FallingBlockCommand::moveFromPosByTick;
        Executor moveToPosByTick = FallingBlockCommand::moveToPosByTick;
        Executor moveToPosByYMove = FallingBlockCommand::moveToPosByYMove;
        Executor moveFromPosToPosByMotionY = FallingBlockCommand::moveFromPosToPosByMotionY;
        Executor moveFromPosToPosByTick = FallingBlockCommand::moveFromPosToPosByTick;
        dispatcher.register(literal("fallingblock")
                //#if MC>=12111
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                //#else
                //# .requires(source -> source.hasPermission(2))
                //#endif
                //#if MC>=12000
                .then(add("moveFromPos", posStart(motion(hasGravity(blockAndAge(commandRegistryAccess, false, moveFromPos))))))
                .then(add("moveFromBlockPos", blockPosStart(motion(hasGravity(blockAndAge(commandRegistryAccess, true, moveFromPos))))))
                .then(add("moveFromPosByTick", posStart(motion(hasGravity(tickMove(blockAndAge(commandRegistryAccess, false, moveFromPosByTick)))))))
                .then(add("moveFromPosByTick", blockPosStart(motion(hasGravity(tickMove(blockAndAge(commandRegistryAccess, true, moveFromPosByTick)))))))
                .then(add("moveToPosByTick", posEnd(motion(hasGravity(tickMove(blockAndAge(commandRegistryAccess, false, moveToPosByTick)))))))
                .then(add("moveToBlockPosByTick", blockPosEnd(motion(hasGravity(tickMove(blockAndAge(commandRegistryAccess, true, moveToPosByTick)))))))
                .then(add("moveToPosByYMove", posEnd(motion(yMove(hasGravity(blockAndAge(commandRegistryAccess, false, moveToPosByYMove)))))))
                .then(add("moveToBlockPosByYMove", blockPosEnd(motion(yMove(hasGravity(blockAndAge(commandRegistryAccess, true, moveToPosByYMove)))))))
                .then(add("moveFromPosToPosByMotionY", posStart(posEnd(motionY(blockAndAge(commandRegistryAccess, false, moveFromPosToPosByMotionY))))))
                .then(add("moveFromBlockPosToBlockPosByMotionY", blockPosStart(blockPosEnd(motionY(blockAndAge(commandRegistryAccess, true, moveFromPosToPosByMotionY))))))
                .then(add("moveFromPosToPosByTick", posStart(posEnd(hasGravity(tickMove(blockAndAge(commandRegistryAccess, false, moveFromPosToPosByTick)))))))
                .then(add("moveFromBlockPosToBlockPosByTick", blockPosStart(blockPosEnd(hasGravity(tickMove(blockAndAge(commandRegistryAccess, true, moveFromPosToPosByTick)))))))
                //#else
                //$$ .then(add("moveFromPos", posStart(motion(hasGravity(blockAndAge(false, moveFromPos))))))
                //$$ .then(add("moveFromBlockPos", blockPosStart(motion(hasGravity(blockAndAge(true, moveFromPos))))))
                //$$ .then(add("moveFromPosByTick", posStart(motion(hasGravity(tickMove(blockAndAge(false, moveFromPosByTick)))))))
                //$$ .then(add("moveFromPosByTick", blockPosStart(motion(hasGravity(tickMove(blockAndAge( true, moveFromPosByTick)))))))
                //$$ .then(add("moveToPosByTick", posEnd(motion(hasGravity(tickMove(blockAndAge(false, moveToPosByTick)))))))
                //$$ .then(add("moveToBlockPosByTick", blockPosEnd(motion(hasGravity(tickMove(blockAndAge( true, moveToPosByTick)))))))
                //$$ .then(add("moveToPosByYMove", posEnd(motion(yMove(hasGravity(blockAndAge(false, moveToPosByYMove)))))))
                //$$ .then(add("moveToBlockPosByYMove", blockPosEnd(motion(yMove(hasGravity(blockAndAge(true, moveToPosByYMove)))))))
                //$$ .then(add("moveFromPosToPosByMotionY", posStart(posEnd(motionY(blockAndAge(false, moveFromPosToPosByMotionY))))))
                //$$ .then(add("moveFromBlockPosToBlockPosByMotionY", blockPosStart(blockPosEnd(motionY(blockAndAge(true, moveFromPosToPosByMotionY))))))
                //$$ .then(add("moveFromPosToPosByTick", posStart(posEnd(hasGravity(tickMove(blockAndAge( false, moveFromPosToPosByTick)))))))
                //$$ .then(add("moveFromBlockPosToBlockPosByTick", blockPosStart(blockPosEnd(hasGravity(tickMove(blockAndAge( true, moveFromPosToPosByTick)))))))
                //#endif
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add(String str, RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return literal(str).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Coordinates> posStart(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("start", Vec3Argument.vec3(false)).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Coordinates> posEnd(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("end", Vec3Argument.vec3(false)).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Coordinates> blockPosStart(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("start", BlockPosArgument.blockPos()).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Coordinates> blockPosEnd(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("end", BlockPosArgument.blockPos()).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Double> motionY(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("motionY", DoubleArgumentType.doubleArg()).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Double> motion(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("motionX", DoubleArgumentType.doubleArg()).then(argument("motionY", DoubleArgumentType.doubleArg()).then(argument("motionZ", DoubleArgumentType.doubleArg()).then(argument)));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Double> yMove(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("yMove", DoubleArgumentType.doubleArg()).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Boolean> hasGravity(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("hasGravity", BoolArgumentType.bool()).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Integer> tickMove(RequiredArgumentBuilder<CommandSourceStack, ?> argument) {
        return argument("tickMove", IntegerArgumentType.integer()).then(argument);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, BlockInput> blockAndAge(
            //#if MC>=12000
            CommandBuildContext commandRegistryAccess,
            //#endif
            boolean isBlockPos,
            Executor executor
    ) {
        //#if MC>=12000
        BlockStateArgument blockStateArgument = BlockStateArgument.block(commandRegistryAccess);
        //#else
        //$$ BlockStateArgument blockStateArgument = BlockStateArgument.block();
        //#endif
        return argument("block", blockStateArgument).executes(context -> {
            checkAndRun(context, executor.execute(context, isBlockPos, false));
            return Command.SINGLE_SUCCESS;
        }).then(argument("age", IntegerArgumentType.integer(-1)).executes(context -> {
            checkAndRun(context, executor.execute(context, isBlockPos, true));
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static void checkAndRun(CommandContext<CommandSourceStack> context, DataFallingBlock dataFallingBlock) throws CommandSyntaxException {
        if (dataFallingBlock == null) {
            throw FAILED_EXCEPTION.create();
        } else {
            dataFallingBlock.run(context.getSource().getLevel());
        }
    }

    private static DataBlock getDataBlock(CommandContext<CommandSourceStack> context) {
        BlockInput blockStateArgument = BlockStateArgument.getBlock(context, "block");
        return new DataBlock(blockStateArgument.getState(), ((BlockInputAccessor) blockStateArgument).getTag());
    }

    private static Vec3 getStart(CommandContext<CommandSourceStack> context, boolean isBlockPos) throws CommandSyntaxException {
        return isBlockPos ? Vec3.atBottomCenterOf(BlockPosArgument.getLoadedBlockPos(context, "start")) : Vec3Argument.getVec3(context, "start");
    }

    private static Vec3 getEnd(CommandContext<CommandSourceStack> context, boolean isBlockPos) throws CommandSyntaxException {
        return isBlockPos ? Vec3.atBottomCenterOf(BlockPosArgument.getLoadedBlockPos(context, "end")) : Vec3Argument.getVec3(context, "end");
    }

    private static Vec3 getMotion(CommandContext<CommandSourceStack> context) {
        return new Vec3(
                DoubleArgumentType.getDouble(context, "motionX"),
                getMotionY(context),
                DoubleArgumentType.getDouble(context, "motionZ")
        );
    }

    private static double getMotionY(CommandContext<CommandSourceStack> context) {
        return DoubleArgumentType.getDouble(context, "motionY");
    }

    private static boolean getHasGravity(CommandContext<CommandSourceStack> context) {
        return BoolArgumentType.getBool(context, "hasGravity");
    }

    private static double getYMove(CommandContext<CommandSourceStack> context) {
        return DoubleArgumentType.getDouble(context, "yMove");
    }

    private static int getTickMove(CommandContext<CommandSourceStack> context) {
        return IntegerArgumentType.getInteger(context, "tickMove");
    }

    private static int getAge(CommandContext<CommandSourceStack> context, boolean hasAge) {
        return hasAge ? IntegerArgumentType.getInteger(context, "age") : -1;
    }

    private static DataFallingBlock moveFromPos(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPos(getDataBlock(context), getStart(context, isBlockPos), getMotion(context), getHasGravity(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveFromPosByTick(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPosByTick(getDataBlock(context), getStart(context, isBlockPos), getMotion(context), getHasGravity(context), getTickMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveToPosByTick(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveToPosByTick(getDataBlock(context), getEnd(context, isBlockPos), getMotion(context), getHasGravity(context), getTickMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveToPosByYMove(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveToPosByYMove(getDataBlock(context), getEnd(context, isBlockPos), getMotion(context), getHasGravity(context), getYMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveFromPosToPosByTick(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPosToPosByTick(getDataBlock(context), getStart(context, isBlockPos), getEnd(context, isBlockPos), getHasGravity(context), getTickMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveFromPosToPosByMotionY(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPosToPosByMotionY(getDataBlock(context), getStart(context, isBlockPos), getEnd(context, isBlockPos), getMotionY(context), getAge(context, hasAge));
    }

    private interface Executor {
        DataFallingBlock execute(CommandContext<CommandSourceStack> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException;
    }

}