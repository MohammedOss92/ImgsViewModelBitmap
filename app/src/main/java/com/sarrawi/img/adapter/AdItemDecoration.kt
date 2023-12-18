package com.sarrawi.img.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AdItemDecoration(private val adInterval: Int, private val adHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) + 1 // تحديد موقع العنصر + 1 لأن الترقيم يبدأ من 0

        // إعداد الهوامش العلوية واليمنى واليسرى
        outRect.top = 0
        outRect.right = 0
        outRect.left = 0

        // تحديد هامش الأسفل بناءً على الموقع داخل كل مجموعة 4
        if (position % adInterval == 1) {
            // هامش أصغر للعنصر الأول في كل مجموعة 4
            outRect.bottom = adHeight / 2
        } else if (position % adInterval == 2) {
            // هامش أكبر للعنصر الثاني في كل مجموعة 4
            outRect.bottom = adHeight
        } else {
            // هامش أصغر لبقية العناصر
            outRect.bottom = 0
        }
    }

}
