package vn.ldbach.bnote

/**
 * Repeat interval
 */
enum class AlarmInterval(val delay: Long) {
    NO_REPEAT(delay = 0),
    DAILY_REPEAT(delay = 1000 * 24 * 60 * 60),
    WEEKLY_REPEAT(delay = 7 * DAILY_REPEAT.getValue()),
    YEARLY_REPEAT(delay = 365 * DAILY_REPEAT.getValue());

    fun getValue(): Long = delay
}