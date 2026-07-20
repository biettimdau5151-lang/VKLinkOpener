package com.example.vklinkopener;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editUrl;
    Button btnAdd, btnImport, btnClear;
    TextView tvCount;
    RecyclerView recyclerUrls;
    ArrayList<String> urlList;
    UrlAdapter adapter;

    private final ActivityResultLauncher<Intent> filePickerLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    importFromFile(uri);
                }
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUrl = findViewById(R.id.editUrl);
        btnAdd = findViewById(R.id.btnAdd);
        btnImport = findViewById(R.id.btnImport);
        btnClear = findViewById(R.id.btnClear);
        tvCount = findViewById(R.id.tvCount);
        recyclerUrls = findViewById(R.id.recyclerUrls);

        urlList = new ArrayList<>();
        adapter = new UrlAdapter(urlList, position -> {
            urlList.remove(position);
            adapter.notifyItemRemoved(position);
            updateCount();
        });

        recyclerUrls.setLayoutManager(new LinearLayoutManager(this));
        recyclerUrls.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String url = editUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Nhap link VK truoc!", Toast.LENGTH_SHORT).show();
                return;
            }
            addUrl(url);
            editUrl.setText("");
        });

        btnImport.setOnClickListener(v -> showImportDialog());

        btnClear.setOnClickListener(v -> {
            if (urlList.isEmpty()) {
                Toast.makeText(this, "Danh sach trong!", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                .setTitle("Xoa tat ca?")
                .setMessage("Xoa " + urlList.size() + " link?")
                .setPositiveButton("Xoa", (d, w) -> {
                    urlList.clear();
                    adapter.notifyDataSetChanged();
                    updateCount();
                    Toast.makeText(this, "Da xoa tat ca!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huy", null)
                .show();
        });

        handleShareIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleShareIntent(intent);
    }

    private void handleShareIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && !sharedText.isEmpty()) {
                String[] lines = sharedText.split("\\s+");
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("http://") || line.startsWith("https://")) {
                        addUrl(line);
                    }
                }
                Toast.makeText(this, "Da nhan link tu app khac!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showImportDialog() {
        String[] options = {
            "Paste tu bo nho",
            "Nhap nhieu link (moi dong 1 link)",
            "Chon file .txt"
        };

        new AlertDialog.Builder(this)
            .setTitle("Import link VK")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        importFromClipboard();
                        break;
                    case 1:
                        showMultiInputDialog();
                        break;
                    case 2:
                        pickTxtFile();
                        break;
                }
            })
            .show();
    }

    private void pickTxtFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        filePickerLauncher.launch(intent);
    }

    private void importFromFile(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            is.close();
            importUrls(sb.toString());
        } catch (Exception e) {
            Toast.makeText(this, "Loi doc file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void importFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            String text = clip.getItemAt(0).getText().toString();
            importUrls(text);
        } else {
            Toast.makeText(this, "Bo nho trong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMultiInputDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(5);
        input.setHint("https://vkvideo.ru/video1\nhttps://vkvideo.ru/video2\nhttps://vkvideo.ru/video3");
        input.setHintTextColor(0xFF666666);
        input.setTextColor(0xFFFFFFFF);
        input.setBackgroundColor(0xFF16213e);
        input.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(this)
            .setTitle("Nhap nhieu link (moi dong 1 link)")
            .setView(input)
            .setPositiveButton("Import", (d, w) -> {
                String text = input.getText().toString();
                importUrls(text);
            })
            .setNegativeButton("Huy", null)
            .show();
    }

    private void importUrls(String text) {
        String[] lines = text.split("[\\r\\n]+");
        int count = 0;
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                addUrl(line);
                count++;
            }
        }
        Toast.makeText(this, "Da import " + count + " link!", Toast.LENGTH_SHORT).show();
    }

    private void addUrl(String url) {
        if (!urlList.contains(url)) {
            urlList.add(url);
            adapter.notifyItemInserted(urlList.size() - 1);
            recyclerUrls.scrollToPosition(urlList.size() - 1);
            updateCount();
        }
    }

    private void updateCount() {
        tvCount.setText(urlList.size() + " link");
    }
}
