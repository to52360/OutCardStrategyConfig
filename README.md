### 依赖项目

> [Hearthstone-Script](https://github.com/xjw580/Hearthstone-Script)项目的插件

## ⚠️ 免责申明

本项目仅供学习交流 **`Java`**、**`Kotlin`** 以及 **`炉石传说`** 玩法，不得用于任何违反法律法规及游戏协议的地方！🚨😡

## 📖 协议

本项目遵循 **[GPL3.0开源协议](LICENSE)** 及 **[禁止商用附加协议](LICENSE1)**

### 目的

能够实现更为复杂和可扩展的权重规则
### 效果
基于战场计算权重的策略,例如在手牌对应种族就加权重,通过配置绑定到组,然后通过组id关联到权重表(CardWeight)的weight,依赖数据也是

### 权重的组成

组权重由weightHandlerStrategy.db配置  
总权重由combo权重(表combo_info)+组权重(表weight_group+WeightCondition)+单卡权重决定

### combo_info特别说明

comboWeight多种语义(对扩展和维护有麻烦,暂时没空整理)  
1.当换牌只用到正负,负数互斥  
2.最后打出作为打出顺序使用  
3.作为一起打出的组,组加权

### 扩展(简单测试没问题)

放在\plugin\WeightHandlerStrategy目录下
服务发现使用java的SPI与Koin
1.组权重规则扩展  
lin.serviceLoader.weightRule.WeightCondition  
2.自定义权重信息获取  
lin.serviceLoader.cardInfoProvide.CardWeightInfoProvide  
3.卡牌权重数据属性配置(用于没ui用编码方式来配置信息)
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
4.配置类(没有UI,用常量作为)  
lin.domain.context.ComboDefValue  
5.查找Combo策略  
lin.domain.combo.FindStrategy
6.使用策略
lin.domain.combo.UseStrategy

### 问题

1.发现卡牌还是有问题(地标不会触发发现事件,会触发发现,也不会正确抉择,底层问题)  
2.由于一开始只想写个打出权重,由于不太理想,写了部分攻击/发现/换牌逻辑,为了快速实现
耦合在打出逻辑的基础数据里(CardWeightInfo,ComboCard)  
3.由于没UI,导致配置信息要使用编码或者数据库,无必要提醒和限制,导致容易配错  
4.共用ComboCard模型导致难于调试,额外的状态导致增加扩展和维护复杂性

### 未来

1.大概就这样,效果不太好,需要更好的效果需要工作量有点多,且无法确定我的策略的方向是正确的  
2.实践理论的项目,现在找到了问题,算是完成了找问题的目标




