package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class NoteHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView content;
    public FrameLayout noteHeader;

    public NoteHolder(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.note_time);
        content=itemView.findViewById(R.id.note_content);
        noteHeader=itemView.findViewById(R.id.note_header);
    }
}
