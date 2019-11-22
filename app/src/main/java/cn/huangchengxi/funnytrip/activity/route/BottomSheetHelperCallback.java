package cn.huangchengxi.funnytrip.activity.route;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetHelperCallback extends ItemTouchHelper.Callback {
    private OnItemTouchMoveListener onItemTouchMoveListener;

    public BottomSheetHelperCallback(OnItemTouchMoveListener onItemTouchMoveListener) {
        this.onItemTouchMoveListener=onItemTouchMoveListener;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        onItemTouchMoveListener.onItemRemove(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType()!=target.getItemViewType()){
            return false;
        }

        boolean result=onItemTouchMoveListener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return result;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swipeFlags=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        int flags=makeMovementFlags(dragFlags,swipeFlags);

        return flags;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
            //viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.grey,null));
            viewHolder.itemView.setScaleX(1.05f);
            viewHolder.itemView.setScaleY(1.05f);
        }else if (actionState==ItemTouchHelper.ACTION_STATE_IDLE){
            onItemTouchMoveListener.onIdel();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.white,null));
        viewHolder.itemView.setScaleY(1.0f);
        viewHolder.itemView.setScaleX(1.0f);
        super.clearView(recyclerView, viewHolder);
    }

    public interface OnItemTouchMoveListener{
        boolean onItemMove(int fromPosition,int toPosition);
        boolean onItemRemove(int position);
        void onIdel();
    }
}
