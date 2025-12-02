package lin.config

import lin.config.find.def.BindInfo

interface BindInfoProvider {
    fun provide(): List<BindInfo>
}