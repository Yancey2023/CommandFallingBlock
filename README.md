# CommandFallingBlock

## 简介

CommandFallingBlock是一个fabric的模组，增加了`fallingblock`指令来实现方便得生成掉落方块，目前只兼容我的世界1.20.1。

## 模组怎么用

#### 指令

- `fallingblock moveFromPos <posStart> <motion> <block>`  
类似于原版掉落方块的效果，碰到障碍物会停止，并且控制刚生成时的加速度


- `fallingblock moveFromPosByTick <posStart> <motion> <tick> <block>`  
只运动固定的时间，从某个坐标开始运动，并且控制刚生成时的加速度


- `fallingblock moveToPosByTick <posEnd> <motion> <tick> <block>`  
只运动固定的时间，运动到某个坐标，并且控制刚生成时的加速度


- `fallingblock moveToPosByYMove <posEnd> <motion> <yMove> <block>`  
运动到某个坐标，并且控制y轴移动的距离，并且控制刚生成时的加速度


- `fallingblock moveFromPosToPos <posStart> <posEnd> <motionY> <block>`  
从某个坐标运动到某个坐标，并且控制刚生成的时候y轴的加速度

#### 参数解释

- `posStart` - 掉落方块生成位置的坐标


- `posEnd` - 掉落方块目的地的坐标


- `motion` - 刚生成的时候各个方向的初速度


- `motionY` - 刚生成的时候y轴方向的初速度


- `yMove` - 初位置到目的地的y轴坐标差(末位置y-初位置y，可负数)


- `tick` - 到达目的地的时间(单位是游戏刻)


- `block` - 方块

#### 注意事项

posStart和posEnd检测到输入的x轴或z轴坐标是整数，会自动加0.5，因此可以当作方块坐标写  
比如：输入`2 3.2 4.0` 会变成 `2.5 3.2 4.0`

## 关于作者

作者：Yancey  
bilibili：Minecraft_Yancey（可以来关注我，如果可以顺便给我视频三连就更好了）  
QQ群：766625597（进群聊聊天，作者很活跃的哦）

## 你可能关心的问题

- **中间有障碍物怎么办？**  
该模组的掉落方块不需要考虑碰撞箱对掉落方块的影响，毕竟可以穿墙的掉落方块谁不爱？


- **目的地下面是不是实体方块怎么办？**  
到达目的地时没有接触地面？只接直接原地变成方块


- **这个模组的掉落方块会和原版或其他模组冲突吗？**  
为了避免这个问题，我并没有使用原版的掉落方块，而是自己加了一个实体


- **掉落方块会不会很卡**  
如果输入的是目的地，就要通过模拟路径获得出发的位置，性能理论上会比直接summon要差，但是经过测试其实一点也不卡