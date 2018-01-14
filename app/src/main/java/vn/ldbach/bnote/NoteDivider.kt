package vn.ldbach.bnote

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Divider for note view
 */
class NoteDivider(private val divider: Drawable) : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        if (parent == null || c == null) {
            super.onDraw(c, parent, state)
            return
        }

        val childCount = parent.childCount
        for (idx in 0..childCount - 2) {
            val child = parent.getChildAt(idx)

            val param = child.layoutParams as RecyclerView.LayoutParams

            val dividerTop = child.bottom + param.bottomMargin
            val dividerBot = dividerTop + 1

            divider.setBounds(0, dividerTop, parent.width, dividerBot)
            divider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent?.getChildAdapterPosition(view) == 0) return
        outRect?.top = 1
    }
}