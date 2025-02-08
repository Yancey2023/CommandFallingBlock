简体中文 | [English](README.md)

# CommandFallingBlock

## 简介

CommandFallingBlock是一个fabric的模组，增加了`fallingblock`指令来实现方便地生成下落的方块。

支持的游戏版本：`1.16.5`, `1.18.2`, `1.20.*`, `1.21`, `1.21.1`, `1.21.2`, `1.21.3`, `1.21.4`

## 模组怎么用

#### 指令

- `fallingblock moveFromPos <posStart> <motion> <hasGravity> <block> [age]`  
  `fallingblock moveFromBlockPos <posStart> <motion> <hasGravity> <block> [age]`  
  类似于原版掉落方块的效果，碰到障碍物会停止，并且控制初速度和是否受重力影响


- `fallingblock moveFromPosByTick <posStart> <motion> <hasGravity> <tickMove> <block> [age]`  
  `fallingblock moveFromBlockPosByTick <posStart> <motion> <hasGravity> <tickMove> <block> [age]`  
  只运动固定的时间，从某个坐标开始运动，并且控制初速度和是否受重力影响


- `fallingblock moveToPosByTick <posEnd> <motion> <hasGravity> <tickMove> <block> [age]`  
  `fallingblock moveToBlockPosByTick <posEnd> <motion> <hasGravity> <tickMove> <block> [age]`  
  只运动固定的时间，运动到某个坐标，并且控制初速度和是否受重力影响


- `fallingblock moveToPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block> [age]`  
  `fallingblock moveToBlockPosByYMove <posEnd> <motion> <yMove> <hasGravity> <block> [age]`  
  运动到某个坐标，并且控制y轴移动的距离，并且控制初速度和是否受重力影响  
  (受重力影响时y轴移动距离有偏差)


- `fallingblock moveFromPosToPosByMotionY <posStart> <posEnd> <motionY> <block> [age]`  
  `fallingblock moveFromBlockPosToBlockPosByMotionY <posStart> <posEnd> <motionY> <block> [age]`  
  从某个坐标运动到某个坐标，并且控制y轴初速度，一定受重力影响  
  (受重力影响时初始位置有偏差)


- `fallingblock moveFromPosToPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]`  
  `fallingblock moveFromBlockPosToBlockPosByTick <posStart> <posEnd> <hasGravity> <tickMove> <block> [age]`  
  只运动固定的时间，从某个坐标运动到某个坐标，并且控制y轴初速度，并且控制初速度和是否受重力影响  
  (受重力影响时初始位置有偏差)

#### 参数解释

- `posStart` - 掉落方块生成位置的坐标


- `posEnd` - 掉落方块目的地的坐标


- `motion` - 刚生成的时候各个方向的初速度


- `motionY` - 刚生成的时候y轴方向的初速度


- `yMove` - 初位置到目的地的y轴坐标差(末位置y-初位置y，可负数)


- `hasGravity` - 是否受重力影响(不受重力影响是匀速直线运动)


- `tickMove` - 到达目的地的时间(单位是游戏刻)


- `block` - 方块


- `age` - 掉落方块实体的最大存在时间(单位是游戏刻)

## 关于作者

作者：Yancey  
QQ：1709185482  
Github: https://github.com/Yancey2023/CommandFallingBlock

## 你可能关心的问题

- **中间有障碍物怎么办？**  
  该模组的掉落方块不需要考虑碰撞箱对掉落方块的影响，毕竟可以穿墙的掉落方块谁不爱？


- **目的地下面是不是实体方块怎么办？**  
  到达目的地时没有接触地面？只接直接原地变成方块


- **这个模组的掉落方块会和原版或其他模组冲突吗？**  
  为了避免这个问题，我并没有使用原版的掉落方块，而是自己加了一个实体


- **掉落方块会不会很卡**  
  如果输入的是目的地，就要通过模拟路径获得出发的位置，性能理论上会比直接使用summon指令要差，但是经过测试其实一点也不卡
