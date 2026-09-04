package com.zarar.bankers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.*;

public class MainActivity extends Activity {
    EditText input;
    TextView result;
    DatabaseHelper database;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input);
        result = findViewById(R.id.result);
        database = new DatabaseHelper(this);

        ((Button)findViewById(R.id.checkButton)).setOnClickListener(v -> runBanker());
        ((Button)findViewById(R.id.historyButton)).setOnClickListener(v -> result.setText(database.getHistory()));
        ((Button)findViewById(R.id.clearButton)).setOnClickListener(v -> {
            database.clearHistory();
            result.setText("Database history cleared.");
        });
    }

    void runBanker() {
        String rawInput = input.getText().toString().trim();
        try {
            String[] rows = rawInput.split(";");
            if (rows.length < 1 || rawInput.isEmpty()) throw new Exception("Enter at least one process row.");
            int n = rows.length, m = rows[0].trim().split("\\s+").length;
            int[][] need = new int[n][m];
            for (int i = 0; i < n; i++) {
                String[] x = rows[i].trim().split("\\s+");
                if (x.length != m) throw new Exception("All rows must have the same number of values.");
                for (int j = 0; j < m; j++) {
                    need[i][j] = Integer.parseInt(x[j]);
                    if (need[i][j] < 0) throw new Exception("Values cannot be negative.");
                }
            }

            int[] available = new int[m];
            Arrays.fill(available, 3);
            boolean[] done = new boolean[n];
            ArrayList<Integer> seq = new ArrayList<>();
            boolean progress = true;

            while (progress && seq.size() < n) {
                progress = false;
                for (int i = 0; i < n; i++) {
                    if (!done[i] && canRun(need[i], available)) {
                        for (int j = 0; j < m; j++) available[j] += need[i][j];
                        done[i] = true;
                        seq.add(i);
                        progress = true;
                    }
                }
            }

            if (seq.size() == n) {
                String safeSequence = format(seq);
                result.setText("SAFE STATE ✓\nSafe sequence: " + safeSequence + "\n\nSaved to SQLite database.");
                database.insertRun(rawInput, "SAFE", safeSequence);
            } else {
                result.setText("UNSAFE STATE ✗\nNo complete safe sequence exists.\n\nSaved to SQLite database.");
                database.insertRun(rawInput, "UNSAFE", "No safe sequence");
            }
        } catch (Exception e) {
            result.setText("Input error: " + e.getMessage() + "\nExample: 3 3 2; 1 2 2; 2 1 1");
        }
    }

    boolean canRun(int[] need, int[] avail) {
        for (int j = 0; j < need.length; j++) if (need[j] > avail[j]) return false;
        return true;
    }

    String format(List<Integer> s) {
        StringBuilder x = new StringBuilder();
        for (int i = 0; i < s.size(); i++) {
            if (i > 0) x.append(" → ");
            x.append("P").append(s.get(i));
        }
        return x.toString();
    }
}
