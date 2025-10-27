package lin.domain.use

data class UseContext(
    var reFindCombo: Boolean = false,
    var isChange: Boolean = false,
    var extAwait: Long = 0,
    var useResult: Boolean = false
)