package lin.strategy

import lin.dao.v1.ComboCard

interface Payoff {
    //收益组
    fun payoffs():(ComboCard)->Boolean
}