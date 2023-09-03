English | [简体中文](README.md)
# CommandFallingBlock

## Description

CommandFallingBlock, a fabric mod in minecraft 1.20.1, adds `fallingblock` command to summon falling block conveniently.

## How to use

#### commands

- `fallingblock moveFromPos <posStart> <motion> <hasGravity> <block> [age]`  
`fallingblock moveFromBlockPos <posStart> <motion> <hasGravity> <block> [age]`  
like the vanilla, move from a position


- `fallingblock moveFromPosByTick <posStart> <motion> <hasGravity> <tickMove> <block> [age]`  
`fallingblock moveFromBlockPosByTick <posStart> <motion> <hasGravity> <tickMove> <block> [age]`  
move from a position, you can control how many ticks it becomes a block


- `fallingblock moveToPosByTick <posEnd> <motion> <hasGravity> <tickMove> <block> [age]`  
`fallingblock moveToBlockPosByTick <posEnd> <motion> <hasGravity> <tickMove> <block> [age]`  
move to a position, you can control how many ticks it becomes a block


- `fallingblock moveToPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block> [age]`  
`fallingblock moveToBlockPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block> [age]`  
move to a position, you can control the y-axis movement distance, which is biased when it has gravity


- `fallingblock moveFromPosToPosByMotionY <posStart> <posEnd> <motionY> <block> [age]`  
`fallingblock moveFromBlockPosToBlockPosByMotionY <posStart> <posEnd> <motionY> <block> [age]`  
move to a position, you can control the y-axis initial speed, it must have gravity, the initial position is biased


- `fallingblock moveFromPosToPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]`  
`fallingblock moveFromBlockPosToBlockPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]`  
  move to a position, you can control how many ticks it becomes a block, it must have gravity, the initial position is biased

#### param

- `posStart` - start position


- `posEnd` - target position


- `motion` - initial speed


- `motionY` - the y-axis initial speed


- `yMove` - the y-axis movement distance


- `hasGravity` - if it has gravity


- `tickMove` - how many ticks it becomes a block


- `block` - block


- `age` - max tick it can live

## About

Author：Yancey  
QQ：1709185482  
Github: https://github.com/Yancey2023/CommandFallingBlock

## FAQ

- **What about if it crashes an obstacle?**  
It will pass through obstacles directly. You don't need to worry about it.


- **What about if the destination is not on the ground?**  
It will also become block.


- **Will the falling block of this mod conflict with the vanilla or other mods?**  
To avoid it, I add an entity type for the falling block of this mod


- **Will the performance become worse?**  
If the input is the destination, the starting position needs to be obtained by simulating the path. Theoretically, the performance will be worse than using the summon command directly, but after testing, it will not cause stuttering