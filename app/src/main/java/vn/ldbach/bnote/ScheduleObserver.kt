package vn.ldbach.bnote

/**
 * Created by Duy-Bach on 1/13/2018.
 */
interface ScheduleObserver {
    fun onFinish(newItem: NoteItem)
}