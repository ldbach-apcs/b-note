package vn.ldbach.bnote

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Created by Duy-Bach on 1/14/2018.
 */
class ItemTouchCallback(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlag = ItemTouchHelper.UP + ItemTouchHelper.DOWN
        val swipeFlag = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlag, swipeFlag)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        adapter.onItemMove(viewHolder?.adapterPosition!!, target?.adapterPosition!!)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        adapter.onItemDismiss(viewHolder?.adapterPosition!!)
    }
}