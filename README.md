English | [简体中文](README_CN.md)

# CommandFallingBlock

## Description

CommandFallingBlock, a fabric mod in minecraft, adds `fallingblock` command to summon falling block conveniently.

supported minecraft versions: `1.16.5`, `1.18.2`, `1.20.x`, `1.21 - 1.21.8`

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
  move from a position, you can control the y-axis initial speed, it must have gravity, the initial position is biased


- `fallingblock moveFromPosToPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]`  
  `fallingblock moveFromBlockPosToBlockPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]`  
  move from a position to another position, you can control how many ticks it becomes a block, it must have gravity, the
  initial position is biased

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
  If the destination is given as input, the starting position must be found by simulating the path. While theoretically
  less performant than directly using the summon command, testing shows it doesn't cause stuttering.

## More feature

The following is all about the falling block summoned by `fallingblock` command, rather than the vanilla falling block.

- It can render blocks rendering by entity render, such as chests.


- It will not send NBT that are not used for rendering to client.
  For example, the text NBT in signs will send and the item NBT in chests will not send.


- When reaching its destination and needing to set block, it will be killed after 2 ticks to prevent flickering.
