package cn.huangchengxi.funnytrip.activity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.InterestRVAdapter;
import cn.huangchengxi.funnytrip.item.InterestItem;

public class SetInterestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private InterestRVAdapter adapter;
    private List<InterestItem> list;
    private String interest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_intereast);
        init();
    }
    private void init(){
        recyclerView=findViewById(R.id.interest_rv);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        save=findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do save process
                String interests="";
                for (int i=0;i<list.size();i++){
                    if (list.get(i).isSelected()){
                        interests+=interests.equals("")?list.get(i).getName():" "+list.get(i).getName();
                    }
                }
                Intent intent=new Intent();
                intent.putExtra("interests",interests);
                setResult(1,intent);
                finish();
            }
        });
        list=new ArrayList<>();
        list.add(new InterestItem("电影",false));
        list.add(new InterestItem("追剧",false));
        list.add(new InterestItem("上网",false));
        list.add(new InterestItem("游戏",false));
        list.add(new InterestItem("街机",false));
        list.add(new InterestItem("二次元",false));
        list.add(new InterestItem("摄影",false));
        list.add(new InterestItem("数码",false));
        list.add(new InterestItem("睡觉",false));
        list.add(new InterestItem("家里宅",false));
        list.add(new InterestItem("赚钱",false));
        list.add(new InterestItem("唱K",false));
        list.add(new InterestItem("听歌",false));
        list.add(new InterestItem("阅读",false));
        list.add(new InterestItem("写作",false));
        list.add(new InterestItem("书法",false));
        list.add(new InterestItem("绘画",false));
        list.add(new InterestItem("手工艺",false));
        list.add(new InterestItem("园艺",false));
        list.add(new InterestItem("魔术",false));
        list.add(new InterestItem("吉他",false));
        list.add(new InterestItem("贝斯",false));
        list.add(new InterestItem("钢琴",false));
        list.add(new InterestItem("键盘",false));
        list.add(new InterestItem("架子鼓",false));
        list.add(new InterestItem("跑步",false));
        list.add(new InterestItem("健身",false));
        list.add(new InterestItem("足球",false));
        list.add(new InterestItem("篮球",false));
        list.add(new InterestItem("羽毛球",false));
        list.add(new InterestItem("网球",false));
        list.add(new InterestItem("乒乓球",false));
        list.add(new InterestItem("台球",false));
        list.add(new InterestItem("单车",false));
        list.add(new InterestItem("溜冰",false));
        list.add(new InterestItem("轮滑",false));
        list.add(new InterestItem("滑板",false));
        list.add(new InterestItem("跑酷",false));
        list.add(new InterestItem("滑雪",false));
        list.add(new InterestItem("爬山",false));
        list.add(new InterestItem("武术",false));
        list.add(new InterestItem("跳舞",false));
        list.add(new InterestItem("购物",false));
        list.add(new InterestItem("逛街",false));
        list.add(new InterestItem("旅游",false));
        list.add(new InterestItem("化妆",false));
        list.add(new InterestItem("宠物",false));
        list.add(new InterestItem("美食",false));
        list.add(new InterestItem("咖啡",false));
        list.add(new InterestItem("红酒",false));
        list.add(new InterestItem("收藏",false));
        list.add(new InterestItem("表演",false));
        list.add(new InterestItem("曲艺",false));
        list.add(new InterestItem("烹饪",false));
        list.add(new InterestItem("烘培",false));
        list.add(new InterestItem("雕刻",false));
        list.add(new InterestItem("裁缝",false));
        list.add(new InterestItem("设计",false));

        interest=getIntent().getStringExtra("interests");
        if (interest!=null && !interest.equals("null") && !interest.equals("")){
            String[] ins=interest.split(" ");
            for (String name:ins){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getName().equals(name)){
                        list.get(i).setSelected(true);
                    }
                }
            }
        }

        adapter=new InterestRVAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
