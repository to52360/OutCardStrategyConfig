package lin.config.cardConfig

import lin.bean.CardWeightInfo
import lin.serviceLoader.parse.ParseCardWeightInfo
import org.koin.core.component.KoinComponent

/**
 * todo-future 存在问题,配置都绑定一个具体类,每次加配置都要改,同时变为上帝类
 * 正在迁移到ConfigDispatcher
 * [lin.config.ConfigDispatcher]
 */
class CardConfigBind(infoMap: Map<String, CardWeightInfo>) : KoinComponent {
    init {
        val parseCardWeightInfo = getKoin().getAll<ParseCardWeightInfo>()
        //todo-future 插入combo策略,应用不同的排序策略
        parseCardWeightInfo.forEach { it.parse(infoMap) }

    }
}