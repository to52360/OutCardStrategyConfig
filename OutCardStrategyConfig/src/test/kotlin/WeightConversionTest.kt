import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import lin.dao.temp.ComboWeightGroup

import kotlin.test.BeforeTest
import kotlin.test.Test

class WeightConversionTest {
    private val weightData = mutableListOf<LikeTrie.Entry<CardWeight>>()
    @Test
    fun test(){
      val comboWeightGroup  = ComboWeightGroup(weightData)
    }
    @BeforeTest
    fun before(){
        weightData.add(LikeTrie.Entry(key = "1", value = CardWeight(4.0,9.0,0.0)))
        weightData.add(LikeTrie.Entry(key = "2", value = CardWeight(4.0,7.0,0.0)))
        weightData.add(LikeTrie.Entry(key = "3", value = CardWeight(4.1,8.0,0.0)))
        weightData.add(LikeTrie.Entry(key = "4", value = CardWeight(4.1,8.0,0.0)))
        weightData.add(LikeTrie.Entry(key = "3", value = CardWeight(2.0,7.0,0.0)))
        weightData.add(LikeTrie.Entry(key = "4", value = CardWeight(2.0,6.0,0.0)))
    }
}