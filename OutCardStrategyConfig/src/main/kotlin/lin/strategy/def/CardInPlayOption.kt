package lin.strategy.def


import lin.strategy.ComboStrategy

class CardInPlayOption :ComboStrategy{
    override fun id(): String {
        return "1"
    }
}