package cn.huangchengxi.funnytrip.activity.tips;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TipsCallback extends ItemTouchHelper.Callback {
    private OnRemoved onRemoved;
    public TipsCallback(OnRemoved onRemoved) {
        this.onRemoved=onRemoved;
    }
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        onRemoved.onRemoved(viewHolder.getAdapterPosition());
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int removef=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(0,removef);
    }
    public interface OnRemoved{
        void onRemoved(int position);
    }
}
