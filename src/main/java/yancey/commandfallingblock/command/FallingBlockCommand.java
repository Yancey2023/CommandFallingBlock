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
import net.minecraft.command.argument.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.util.DataBlock;
import yancey.commandfallingblock.util.DataFallingBlock;
import yancey.commandfallingblock.mixin.BlockStateArgumentAccessor;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

//#if MC>=12000
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
//#else
//$$ import net.minecraft.text.TranslatableText;
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

    @SuppressWarnings("SpellCheckingInspection")
    //#if MC>=12000
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("command.commandfallingblock.fallingblock.failedToCalculate"));
    //#else
    //$$ private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("command.commandfallingblock.fallingblock.failedToCalculate"));
    //#endif

    public static void register(
            //#if MC>=12000
            CommandRegistryAccess commandRegistryAccess,
            //#endif
            CommandDispatcher<ServerCommandSource> dispatcher) {
        Executor moveFromPos = FallingBlockCommand::moveFromPos;
        Executor moveFromPosByTick = FallingBlockCommand::moveFromPosByTick;
        Executor moveToPosByTick = FallingBlockCommand::moveToPosByTick;
        Executor moveToPosByYMove = FallingBlockCommand::moveToPosByYMove;
        Executor moveFromPosToPosByMotionY = FallingBlockCommand::moveFromPosToPosByMotionY;
        Executor moveFromPosToPosByTick = FallingBlockCommand::moveFromPosToPosByTick;
        //#if MC>=12000
        dispatcher.register(literal("fallingblock")
                .requires(source -> source.hasPermissionLevel(2))
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
        );
        //#else
        //$$ dispatcher.register(literal("fallingblock")
        //$$         .requires(source -> source.hasPermissionLevel(2))
        //$$         .then(add("moveFromPos", posStart(motion(hasGravity(blockAndAge(false, moveFromPos))))))
        //$$         .then(add("moveFromBlockPos", blockPosStart(motion(hasGravity(blockAndAge(true, moveFromPos))))))
        //$$         .then(add("moveFromPosByTick", posStart(motion(hasGravity(tickMove(blockAndAge(false, moveFromPosByTick)))))))
        //$$         .then(add("moveFromPosByTick", blockPosStart(motion(hasGravity(tickMove(blockAndAge( true, moveFromPosByTick)))))))
        //$$         .then(add("moveToPosByTick", posEnd(motion(hasGravity(tickMove(blockAndAge(false, moveToPosByTick)))))))
        //$$         .then(add("moveToBlockPosByTick", blockPosEnd(motion(hasGravity(tickMove(blockAndAge( true, moveToPosByTick)))))))
        //$$         .then(add("moveToPosByYMove", posEnd(motion(yMove(hasGravity(blockAndAge(false, moveToPosByYMove)))))))
        //$$         .then(add("moveToBlockPosByYMove", blockPosEnd(motion(yMove(hasGravity(blockAndAge(true, moveToPosByYMove)))))))
        //$$         .then(add("moveFromPosToPosByMotionY", posStart(posEnd(motionY(blockAndAge(false, moveFromPosToPosByMotionY))))))
        //$$         .then(add("moveFromBlockPosToBlockPosByMotionY", blockPosStart(blockPosEnd(motionY(blockAndAge(true, moveFromPosToPosByMotionY))))))
        //$$         .then(add("moveFromPosToPosByTick", posStart(posEnd(hasGravity(tickMove(blockAndAge( false, moveFromPosToPosByTick)))))))
        //$$         .then(add("moveFromBlockPosToBlockPosByTick", blockPosStart(blockPosEnd(hasGravity(tickMove(blockAndAge( true, moveFromPosToPosByTick)))))))
        //$$ );
        //#endif
    }

    private static LiteralArgumentBuilder<ServerCommandSource> add(String str, RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return literal(str).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, PosArgument> posStart(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("start", Vec3ArgumentType.vec3(false)).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, PosArgument> posEnd(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("end", Vec3ArgumentType.vec3(false)).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, PosArgument> blockPosStart(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("start", BlockPosArgumentType.blockPos()).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, PosArgument> blockPosEnd(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("end", BlockPosArgumentType.blockPos()).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, Double> motionY(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("motionY", DoubleArgumentType.doubleArg()).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, Double> motion(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("motionX", DoubleArgumentType.doubleArg()).then(argument("motionY", DoubleArgumentType.doubleArg()).then(argument("motionZ", DoubleArgumentType.doubleArg()).then(argument)));
    }

    private static RequiredArgumentBuilder<ServerCommandSource, Double> yMove(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("yMove", DoubleArgumentType.doubleArg()).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, Boolean> hasGravity(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("hasGravity", BoolArgumentType.bool()).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, Integer> tickMove(RequiredArgumentBuilder<ServerCommandSource, ?> argument) {
        return argument("tickMove", IntegerArgumentType.integer()).then(argument);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, BlockStateArgument> blockAndAge(
            //#if MC>=12000
            CommandRegistryAccess commandRegistryAccess,
            //#endif
            boolean isBlockPos,
            Executor executor
    ) {
        //#if MC>=12000
        BlockStateArgumentType blockStateArgumentType = BlockStateArgumentType.blockState(commandRegistryAccess);
        //#else
        //$$ BlockStateArgumentType blockStateArgumentType = BlockStateArgumentType.blockState();
        //#endif
        return argument("block", blockStateArgumentType).executes(context -> {
            checkAndRun(context, executor.execute(context, isBlockPos, false));
            return Command.SINGLE_SUCCESS;
        }).then(argument("age", IntegerArgumentType.integer(-1)).executes(context -> {
            checkAndRun(context, executor.execute(context, isBlockPos, true));
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static void checkAndRun(CommandContext<ServerCommandSource> context, DataFallingBlock dataFallingBlock) throws CommandSyntaxException {
        if (dataFallingBlock == null) {
            throw FAILED_EXCEPTION.create();
        } else {
            dataFallingBlock.run(context.getSource().getWorld());
        }
    }

    private static DataBlock getDataBlock(CommandContext<ServerCommandSource> context) {
        BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");
        return new DataBlock(blockStateArgument.getBlockState(), ((BlockStateArgumentAccessor) blockStateArgument).getData());
    }

    private static Vec3d getStart(CommandContext<ServerCommandSource> context, boolean isBlockPos) throws CommandSyntaxException {
        return isBlockPos ? Vec3d.ofBottomCenter(BlockPosArgumentType.getLoadedBlockPos(context, "start")) : Vec3ArgumentType.getVec3(context, "start");
    }

    private static Vec3d getEnd(CommandContext<ServerCommandSource> context, boolean isBlockPos) throws CommandSyntaxException {
        return isBlockPos ? Vec3d.ofBottomCenter(BlockPosArgumentType.getLoadedBlockPos(context, "end")) : Vec3ArgumentType.getVec3(context, "end");
    }

    private static Vec3d getMotion(CommandContext<ServerCommandSource> context) {
        return new Vec3d(
                DoubleArgumentType.getDouble(context, "motionX"),
                getMotionY(context),
                DoubleArgumentType.getDouble(context, "motionZ")
        );
    }

    private static double getMotionY(CommandContext<ServerCommandSource> context) {
        return DoubleArgumentType.getDouble(context, "motionY");
    }

    private static boolean getHasGravity(CommandContext<ServerCommandSource> context) {
        return BoolArgumentType.getBool(context, "hasGravity");
    }

    private static double getYMove(CommandContext<ServerCommandSource> context) {
        return DoubleArgumentType.getDouble(context, "yMove");
    }

    private static int getTickMove(CommandContext<ServerCommandSource> context) {
        return IntegerArgumentType.getInteger(context, "tickMove");
    }

    private static int getAge(CommandContext<ServerCommandSource> context, boolean hasAge) {
        return hasAge ? IntegerArgumentType.getInteger(context, "age") : -1;
    }

    private static DataFallingBlock moveFromPos(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPos(getDataBlock(context), getStart(context, isBlockPos), getMotion(context), getHasGravity(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveFromPosByTick(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPosByTick(getDataBlock(context), getStart(context, isBlockPos), getMotion(context), getHasGravity(context), getTickMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveToPosByTick(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveToPosByTick(getDataBlock(context), getEnd(context, isBlockPos), getMotion(context), getHasGravity(context), getTickMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveToPosByYMove(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveToPosByYMove(getDataBlock(context), getEnd(context, isBlockPos), getMotion(context), getHasGravity(context), getYMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveFromPosToPosByTick(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPosToPosByTick(getDataBlock(context), getStart(context, isBlockPos), getEnd(context, isBlockPos), getHasGravity(context), getTickMove(context), getAge(context, hasAge));
    }

    private static DataFallingBlock moveFromPosToPosByMotionY(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException {
        return DataFallingBlock.moveFromPosToPosByMotionY(getDataBlock(context), getStart(context, isBlockPos), getEnd(context, isBlockPos), getMotionY(context), getAge(context, hasAge));
    }

    private interface Executor {
        DataFallingBlock execute(CommandContext<ServerCommandSource> context, boolean isBlockPos, boolean hasAge) throws CommandSyntaxException;
    }

}