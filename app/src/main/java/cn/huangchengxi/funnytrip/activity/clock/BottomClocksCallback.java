package cn.huangchengxi.funnytrip.activity.clock;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


public class BottomClocksCallback extends ItemTouchHelper.Callback {
    private OnItemRemoved onItemRemoved;
    public BottomClocksCallback(OnItemRemoved onItemRemoved){
        this.onItemRemoved=onItemRemoved;
    }
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        onItemRemoved.onRemoved(viewHolder.getAdapterPosition());
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipe=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(0,swipe);
    }
    public interface OnItemRemoved{
        boolean onRemoved(int position);
    }
}
