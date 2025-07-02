package lin.strategy

import lin.dao.ComboCard

interface Payoff {
    //收益组
    fun payoffs():(ComboCard)->Boolean
}