package com.prave.tools.decisionmaker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MainActivity extends AppCompatActivity{

    Spinner spinner;
    ListView listView;
    ImageButton go,add;
    final List<DecisionItem> items=new Vector<>();
    String[] availableTimes={"1 次","3 次","5 次","9 次","99 次","999","9999","99999"};
    String[] choices;
    final boolean[] shown=new boolean[7];
    String[] breakfast={"包子","饼子菜","面包","馍馍菜","麦片牛奶","饼干","其他"};
    String[] lunch={"米饭套餐","盖浇饭","拌面","牛肉面","馕包肉","米粉","其他"};
    String[] dinner={"馍馍菜","擀面皮","米粉","牛肉面","砂锅","方便面","其他"};
    String[] defau={"第一项","第二项","第三项","第四项","第五项","第六项",""};
    String[] critical={"米粉","方便面","粉丝"};
    String[] topics={"早餐","午餐","晚餐","常规"};
    String[][] topicContent={breakfast,lunch,dinner,defau};
    class DecisionItemAdapter extends BaseAdapter{
        Context context;

        public DecisionItemAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view=View.inflate(context,R.layout.item,null);
            final EditText et=view.findViewById(R.id.item_name_0x19941022);
            final Button percent=view.findViewById(R.id.change_percent);
            ImageButton del=view.findViewById(R.id.remove);
            percent.setText("权值："+items.get(position).weight);
            et.setText(items.get(position).item);
            et.setSelectAllOnFocus(true);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String fo=s.toString();
                    items.get(position).item=fo;
                    if(checkCritical(fo))
                        items.get(position).weight=1;

                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int strpos=0;
                    for (int i = 0; i <choices.length ; i++) {
                        if(choices[i].equals(items.get(position).item))
                            strpos=i;
                    }
                    items.remove(position);
                    shown[strpos]=false;
                    listView.setAdapter(new DecisionItemAdapter(MainActivity.this));
                }
            });
            percent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View focusing=getWindow().getDecorView().findFocus();
                    if(focusing!=null && focusing.toString().contains("item_name_0x19941022"))
                        focusing.clearFocus();
                    AlertDialog.Builder inputWeight=new AlertDialog.Builder(context);
                    final EditText etp=new EditText(context);
                    etp.setInputType(3);
                    etp.setTextSize(25);
                    etp.setMaxLines(1);
                    inputWeight.
                            setTitle("输入权值：").
                            setView(etp).
                            setPositiveButton("确定 ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                if(etp.length()!=0) {
                                    int cp=Integer.parseInt(etp.getText().toString());
                                    if(checkCritical(items.get(position).item )&& cp>1){
                                        AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);
                                        ab.setTitle("提示");
                                        StringBuilder sb=new StringBuilder("“"+items.get(position).item+"”的权重不能超过 1 哦。\n");
                                        ab.setMessage(sb.toString());
                                        ab.setPositiveButton("好哒", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        ab.show();
                                    }
                                    else
                                        items.get(position).weight = Integer.parseInt(etp.getText().toString());
                                }
                            }catch (Exception ignored){}
                        }
                    });
                    inputWeight.show();
                    etp.requestFocus();
                }
            });
            return view;
        }
    }

    class DecisionItem{
        String item;
        int weight;

        public DecisionItem(String item, int weight) {
            this.item = item;
            this.weight = weight;
        }

        public DecisionItem(String item) {
            this(item,2);
        }
        public DecisionItem(){
            this("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlertDialog.Builder choiceChoicer=new AlertDialog.Builder(MainActivity.this);
        final RadioGroup rg=new RadioGroup(MainActivity.this);
        rg.setGravity(Gravity.CENTER);
        rg.setPadding(0,80,0,60);
        TextView zhanwei=new TextView(MainActivity.this);
        zhanwei.setHeight(100);
        final List<RadioButton> rbs=new Vector<>();
        for (int i = 0; i <topics.length ; i++) {
            RadioButton radioButton=new RadioButton(MainActivity.this);
            radioButton.setText(topics[i]);
            radioButton.setTextSize(20);
            radioButton.setPadding(0,20,0,5);
            radioButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rg.addView(radioButton);
            rbs.add(radioButton);
        }
        choiceChoicer.setView(rg)
                .setTitle("选择随机类型")
                .setCancelable(false)
                .setIcon(R.drawable.icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selection=3;
                for (int i = 0; i < rbs.size(); i++) {
                    if(rbs.get(i).isChecked())
                        selection=i;
                }
                choices=topicContent[selection];
                Message message=new Message();
                Bundle b=new Bundle();
                b.putInt("key",rg.getCheckedRadioButtonId());
                message.setData(b);
                handler.handleMessage(message);
            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
        spinner=findViewById(R.id.times);
        listView=findViewById(R.id.choice_items);
        go=findViewById(R.id.go);
        add=findViewById(R.id.add);
    }

    void init(){
        for (int i = 0; i < choices.length-1; i++) {
            shown[i]=true;
            DecisionItem def=new DecisionItem(choices[i]);
            if(checkCritical(def.item))
                def.weight=1;
            items.add(def);
        }
        listView.setAdapter(new DecisionItemAdapter(MainActivity.this));
        spinner.setAdapter(new ArrayAdapter<>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,availableTimes));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int siz=7;
                for (int i = 0; i < shown.length; i++) {
                    if(!shown[i]) {
                        siz = i;
                        break;
                    }
                }
                String toaddname="";
                if(siz<choices.length) {
                    shown[siz] = true;
                    toaddname=choices[siz];
                }
                items.add(new DecisionItem(toaddname));
                listView.setAdapter(new DecisionItemAdapter(MainActivity.this));
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);
                ab.setCancelable(false);
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                if(items.size()==0){
                    ab.setTitle("错误").setMessage("尚未设置随机项目！");
                }
                else{
                    //gather data
                    boolean exception=false;
                    int randTimes=Integer.parseInt(availableTimes[spinner.getSelectedItemPosition()].contains("次")?
                            availableTimes[spinner.getSelectedItemPosition()].substring(0,availableTimes[spinner.getSelectedItemPosition()].length()-2):
                            availableTimes[spinner.getSelectedItemPosition()]);
                    int[] result=new int[randTimes];
                    for (DecisionItem di:items){
                        if(di.item.equals("")||di.weight==0){
                            exception=true;
                            ab.setTitle("错误").setMessage("随机项目为空或者权值为0");
                            break;
                        }
                    }
                    if(!exception){
                        for (int i = 0; i <randTimes ; i++) {
                            result[i]=Rando(items);
                        }
                        ab.setTitle("随机结果");
                        StringBuilder res=new StringBuilder("随机结果如下：\n");
                        if(randTimes<=15) {
                            for (int re : result) {
                                res.append("\t\t").append(items.get(re).item).append("\n");
                            }
                            res.append("结果陈列完毕。");
                        }
                        else{
                            int[] stat=Statics(result);
                            for (int i = 0; i < stat.length; i++) {
                                int maxp=maxPos(stat);
                                res.append("\t\t").append(items.get(maxp).item).append("，").append(stat[maxp]).append("次；").append("\n");
                                stat[maxp]=0;
                            }
                            res.append("结果统计完毕。");
                        }
                        ab.setMessage(res.toString());
                    }
                }
                ab.show();
            }
        });
    }

    int Rando(List<DecisionItem> its){
        Random r=new Random();
        int[] weisum=new int[its.size()];
        weisum[0]=its.get(0).weight;
        for (int i = 1; i <its.size() ; i++) {
            weisum[i]=weisum[i-1]+its.get(i).weight;
        }
        int result=r.nextInt(weisum[its.size()-1]);
        for (int i = 0; i < weisum.length; i++) {
            if(result<weisum[i]){
                return i;
            }
        }
        return -1;
    }

    int[] Statics(int[] result){
        int[] stat=new int[items.size()];
        for(int i:result)
            stat[i]++;
        return stat;
    }
    int maxPos(int[] a){
        int max=Arrays.stream(a).max().getAsInt();
        for (int i = 0; i < a.length; i++) {
            if(a[i]==max)
                return i;
        }
        return -1;
    }

    boolean checkCritical(String s){
        for (String t:critical)
            if(s.contains(t))
                return true;
        return false;
    }

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            init();
        }
    };

}
