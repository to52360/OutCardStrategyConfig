package lin.serviceLoader.weightRule

import lin.bean.CardWeightInfo

/**
 * 注入数据参考组,标记
 * 注入分组具体数据
 */
interface DepByWeightInfos {
    /**
     * select  自定义初始化方法,有没有采用工厂模式
     * 条件(condition)依赖权重组信息
     *
     */
    fun initByWeightInfos(cardWeightInfoList: List<List<CardWeightInfo>>)

}

/**
 * 依赖分组id
 */
interface DepByWeightGroupId {
    /**
     * select  自定义初始化方法,有没有采用工厂模式
     * 条件(condition)依赖权重组信息
     *
     */
    fun initByGroupIds(groupIds: Array<Double>)

}


interface DepByWeightInfo : DepByWeightInfos {
    /**
     * select  自定义初始化方法,有没有采用工厂模式
     * 条件(condition)依赖权重组信息
     *
     */
    override fun initByWeightInfos(cardWeightInfoList: List<List<CardWeightInfo>>) {
        initByWeightInfo(cardWeightInfoList.first())
    }

    fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>)

}