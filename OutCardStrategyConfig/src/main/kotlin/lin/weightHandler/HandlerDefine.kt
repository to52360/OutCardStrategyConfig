package lin.weightHandler

import lin.dao.v1.ComboCard

interface WeightHandler{
    fun cardWeightProcess(useAbleCards:List<ComboCard>, allCards:List<ComboCard>)
}