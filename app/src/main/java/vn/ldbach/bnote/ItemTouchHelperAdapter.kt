package vn.ldbach.bnote

/**
 * Created by Duy-Bach on 1/14/2018.
 */
interface ItemTouchHelperAdapter {
    fun onItemMove(from: Int, to: Int)
    fun onItemDelete(where: Int)
}