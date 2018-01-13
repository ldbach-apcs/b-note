package vn.ldbach.bnote

/**
 * Created by Duy-Bach on 1/13/2018.
 */
enum class EarlyNotifyTime(val earlyTime: Long) {
    NO_NOTIFY(earlyTime = -1),
    EXACT_TIME(earlyTime = 0),
    ONE_HOUR(earlyTime = -1000 * 60 * 60),
    ONE_DAY(earlyTime = 24 * ONE_HOUR.getValue()),
    ONE_WEEK(earlyTime = 7 * ONE_DAY.getValue());

    fun getValue(): Long = earlyTime
}