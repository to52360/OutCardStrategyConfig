package lin.bean

/**
 * @param depIds 数据库用String,用","分割
 */
class ComboInfo(val infoId:Int, val bindId: Double, val isBefore: Boolean, val depIds: Array<Double>, val comboWeight: Double)