### 依赖项目

> [Hearthstone-Script](https://github.com/xjw580/Hearthstone-Script)项目的插件
### 目的
主要提高完成成就任务的效率
### 效果
基于战场计算权重的策略,例如在手牌对应种族就加权重,通过配置绑定到组,然后通过组id关联到权重表(CardWeight)的weight,依赖数据也是

### 权重的组成

组权重由weightHandlerStrategy.db配置,总权重由combo权重(表combo_info)+组权重(表weight_group+WeightCondition)+单卡权重决定

### combo_info特别说明

comboWeight多种语义(对扩展和维护有麻烦,暂时没空整理)  
1.当换牌只用到正负,负数互斥  
2.最后打出作为打出顺序使用  
3.作为一起打出的组,组加权

### 扩展(简单测试没问题)

放在\plugin\WeightHandlerStrategy目录下
服务发现使用java的SPI与Koin
1.组条件扩展  
lin.serviceLoader.weightRule.WeightCondition  
2.自定义权重信息  
lin.serviceLoader.cardInfoProvide.CardWeightInfoProvide  
3.卡牌数据添加(用于没ui用编码方式来配置信息)
lin.serviceLoader.parse.ParseCardWeightInfo  
4.单卡权重规则  
lin.serviceLoader.weightRule.CardRule  
5.生命周期  
lin.lifecycle

### 项目结构说明

1.程序入口  
lin.domain.ComboDomain  
2.战场信息  
lin.domain.MyWarManage  
3.模块信息(Koin)  
lin.domain.ModulesSetting

### 问题

1.发现卡牌还是有问题(地标不会触发发现事件,会触发发现,也不会正确抉择,底层问题)  
2.由于一开始只想写个打出权重,由于不太理想,写了部分攻击/发现/换牌逻辑,为了快速实现
耦合在打出逻辑的基础数据里(CardWeightInfo,ComboCard)  
3.由于没UI,导致配置信息要使用编码或者数据库

### 未来

大概就这样,兴起使然,现在没兴趣了


