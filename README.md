### 依赖项目

> [Hearthstone-Script](https://github.com/xjw580/Hearthstone-Script)项目的插件
### 目的
主要提高完成成就任务的效率
### 效果
基于战场计算权重的策略,例如在手牌对应种族就加权重,通过配置绑定到组,然后通过组id关联到权重表(CardWeight)的weight,依赖数据也是
### 权重调整
组权重由weightHandlerStrategy.db配置,总权重由combo权重+组权重+单卡权重决定
### 权重规则扩展(没测试)
使用java的SPI
对应接口:
lin.serviceLoader.weightRule.WeightCondition


