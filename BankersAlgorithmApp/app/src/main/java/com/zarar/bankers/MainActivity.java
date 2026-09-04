package com.zarar.bankers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.*;

public class MainActivity extends Activity {
    EditText input; TextView result;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_main);
        input=findViewById(R.id.input); result=findViewById(R.id.result);
        ((Button)findViewById(R.id.checkButton)).setOnClickListener(v -> runBanker());
    }
    void runBanker() {
        try {
            String[] rows=input.getText().toString().trim().split(";");
            if(rows.length<1) throw new Exception("Enter at least one process row.");
            int n=rows.length, m=rows[0].trim().split("\\s+").length;
            int[][] need=new int[n][m];
            for(int i=0;i<n;i++) { String[] x=rows[i].trim().split("\\s+"); if(x.length!=m) throw new Exception("All rows must have the same number of values."); for(int j=0;j<m;j++) need[i][j]=Integer.parseInt(x[j]); }
            int[] available=new int[m]; Arrays.fill(available, 3);
            boolean[] done=new boolean[n]; ArrayList<Integer> seq=new ArrayList<>();
            boolean progress=true;
            while(progress && seq.size()<n) { progress=false; for(int i=0;i<n;i++) if(!done[i] && canRun(need[i],available)) { for(int j=0;j<m;j++) available[j]+=need[i][j]; done[i]=true; seq.add(i); progress=true; } }
            if(seq.size()==n) result.setText("SAFE STATE ✓\nSafe sequence: " + format(seq));
            else result.setText("UNSAFE STATE ✗\nNo complete safe sequence exists for the entered data.");
        } catch(Exception e) { result.setText("Input error: "+e.getMessage()+"\nExample: 3 3 2; 1 2 2; 2 1 1"); }
    }
    boolean canRun(int[] need,int[] avail) { for(int j=0;j<need.length;j++) if(need[j]>avail[j]) return false; return true; }
    String format(List<Integer> s) { StringBuilder x=new StringBuilder(); for(int i=0;i<s.size();i++){ if(i>0)x.append(" → "); x.append("P").append(s.get(i)); } return x.toString(); }
}
