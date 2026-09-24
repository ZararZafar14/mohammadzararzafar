package com.zarar.bankers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    EditText availableInput, allocationInput, maxInput;
    TextView result;
    DatabaseHelper database;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        availableInput = findViewById(R.id.availableInput);
        allocationInput = findViewById(R.id.allocationInput);
        maxInput = findViewById(R.id.maxInput);
        result = findViewById(R.id.result);
        database = new DatabaseHelper(this);
        findViewById(R.id.checkButton).setOnClickListener(v -> runBanker());
        findViewById(R.id.sampleButton).setOnClickListener(v -> loadSample());
        findViewById(R.id.historyButton).setOnClickListener(v -> result.setText(database.getHistory()));
        findViewById(R.id.clearButton).setOnClickListener(v -> { database.clearHistory(); result.setText("Database history cleared."); });
        loadSample();
    }

    private void loadSample() {
        availableInput.setText("3 3 2");
        allocationInput.setText("0 1 0; 2 0 0; 3 0 2; 2 1 1; 0 0 2");
        maxInput.setText("7 5 3; 3 2 2; 9 0 2; 2 2 2; 4 3 3");
        result.setText("Sample loaded. Press Check Safe State.");
    }

    void runBanker() {
        try {
            String availableRaw = availableInput.getText().toString().trim();
            String allocationRaw = allocationInput.getText().toString().trim();
            String maxRaw = maxInput.getText().toString().trim();
            int[] available = parseVector(availableRaw);
            int[][] allocation = parseMatrix(allocationRaw);
            int[][] maximum = parseMatrix(maxRaw);
            int n = allocation.length, m = available.length;
            if (n == 0) throw new Exception("Enter at least one process.");
            if (maximum.length != n) throw new Exception("Allocation and Maximum must have the same number of processes.");
            for (int i = 0; i < n; i++) {
                if (allocation[i].length != m || maximum[i].length != m) throw new Exception("Every row must contain exactly " + m + " resources.");
                for (int j = 0; j < m; j++) {
                    if (allocation[i][j] < 0 || maximum[i][j] < 0 || available[j] < 0) throw new Exception("Values cannot be negative.");
                    if (maximum[i][j] < allocation[i][j]) throw new Exception("Maximum cannot be less than Allocation for P" + i + ".");
                }
            }
            int[][] need = new int[n][m];
            for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) need[i][j] = maximum[i][j] - allocation[i][j];
            int[] work = Arrays.copyOf(available, m);
            boolean[] finish = new boolean[n];
            ArrayList<Integer> sequence = new ArrayList<>();
            boolean progress = true;
            while (progress && sequence.size() < n) {
                progress = false;
                for (int i = 0; i < n; i++) if (!finish[i] && canRun(need[i], work)) {
                    for (int j = 0; j < m; j++) work[j] += allocation[i][j];
                    finish[i] = true; sequence.add(i); progress = true;
                }
            }
            String snapshot = "Available: " + availableRaw + "\nAllocation: " + allocationRaw + "\nMaximum: " + maxRaw;
            String details = "Need Matrix:\n" + formatMatrix(need);
            if (sequence.size() == n) {
                String safe = formatSequence(sequence);
                result.setText("SAFE STATE ✓\n\nSafe sequence: " + safe + "\n\n" + details + "Available after completion: " + Arrays.toString(work));
                database.insertRun(snapshot, "SAFE", safe);
            } else {
                ArrayList<Integer> blocked = new ArrayList<>();
                for (int i = 0; i < n; i++) if (!finish[i]) blocked.add(i);
                String blockedText = formatSequence(blocked);
                result.setText("UNSAFE STATE ✗\n\nNo complete safe sequence exists.\nBlocked processes: " + blockedText + "\n\n" + details);
                database.insertRun(snapshot, "UNSAFE", "Blocked: " + blockedText);
            }
        } catch (Exception e) { result.setText("Input error: " + e.getMessage()); }
    }

    private int[] parseVector(String raw) throws Exception {
        if (raw.isEmpty()) throw new Exception("Available resources are required.");
        String[] p = raw.split("\\s+"); int[] a = new int[p.length];
        for (int i = 0; i < p.length; i++) a[i] = Integer.parseInt(p[i]);
        return a;
    }

    private int[][] parseMatrix(String raw) throws Exception {
        if (raw.isEmpty()) throw new Exception("Matrix input is required.");
        String[] rows = raw.split(";"); int[][] matrix = new int[rows.length][]; int columns = -1;
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i].trim(); if (row.isEmpty()) throw new Exception("Empty process row found.");
            String[] p = row.split("\\s+"); if (columns == -1) columns = p.length;
            if (p.length != columns) throw new Exception("All matrix rows must have the same number of values.");
            matrix[i] = new int[columns]; for (int j = 0; j < columns; j++) matrix[i][j] = Integer.parseInt(p[j]);
        }
        return matrix;
    }

    private boolean canRun(int[] need, int[] work) { for (int j = 0; j < need.length; j++) if (need[j] > work[j]) return false; return true; }
    private String formatSequence(List<Integer> s) { if (s.isEmpty()) return "None"; StringBuilder x = new StringBuilder(); for (int i=0;i<s.size();i++){ if(i>0)x.append(" → "); x.append("P").append(s.get(i)); } return x.toString(); }
    private String formatMatrix(int[][] matrix) { StringBuilder x=new StringBuilder(); for(int i=0;i<matrix.length;i++) x.append("P").append(i).append(": ").append(Arrays.toString(matrix[i])).append("\n"); return x.toString(); }
}
