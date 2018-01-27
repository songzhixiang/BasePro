package www.andysong.com.basepro.example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import www.andysong.com.basepro.MainActivity;
import www.andysong.com.basepro.R;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ExChangeTabikaeru extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_FILE_PICKER = 0x1233;
    private static final int REQUEST_CODE_REQUEST_PERMISSIONS = 0x1784;

    private static final int OFFSET_CLOVER = 0xc70;
    private static final int OFFSET_TICKETS = 0xc74;

    private EditText cloverInput;
    private EditText ticketsInput;
    private Button cloverButton;
    private Button ticketsButton;

    private File dataDir;
    private File archive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_activity);
        File cacheDir = getExternalCacheDir();
        if (cacheDir == null) {
            Toast.makeText(this, "shared storage is not currently available.",
                    Toast.LENGTH_LONG).show();
            throw new RuntimeException("shared storage is not currently available.");
        }
        dataDir = cacheDir.getParentFile().getParentFile();
        archive = new File(dataDir, "jp.co.hit_point.tabikaeru/files/GameData.sav");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyStoragePermissions(this);
    }

    private void pickArchive() {
        new LFilePicker()
                .withActivity(ExChangeTabikaeru.this)
                .withRequestCode(REQUEST_CODE_FILE_PICKER)
                .withTitle(getString(R.string.archive_pick))
                .withBackgroundColor("#3F51B5")
                .withFileFilter(new String[]{"sav"})
                .withMutilyMode(false)
                .withChooseMode(true)
                .withStartPath(dataDir.getAbsolutePath())
                .start();
    }

    private void initView() {
        cloverInput = findViewById(R.id.et_clover);
        ticketsInput = findViewById(R.id.et_tickets);
        cloverButton = findViewById(R.id.save_clover);
        ticketsButton = findViewById(R.id.save_tickets);
        cloverButton.setOnClickListener(this);
        ticketsButton.setOnClickListener(this);
        cloverInput.addTextChangedListener(new MyTextWatcher(cloverButton));
        ticketsInput.addTextChangedListener(new MyTextWatcher(ticketsButton));
    }

    private void initData() {
        if (archive.exists()) {
            if (archive.canWrite()) {
                String cloverData = getString(R.string.number, readInt(archive, OFFSET_CLOVER));
                String ticketsData = getString(R.string.number, readInt(archive, OFFSET_TICKETS));
                cloverButton.setTag(cloverData);
                ticketsButton.setTag(ticketsData);
                cloverInput.setText(cloverData);
                ticketsInput.setText(ticketsData);
            } else {
                showToast(R.string.archive_permission_denied);
            }
        } else {
            pickArchive();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                }, REQUEST_CODE_REQUEST_PERMISSIONS);
            } else {
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSIONS) {
            initData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILE_PICKER && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
            archive = new File(list.get(0));
            initData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_archive_pick:
                pickArchive();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            String s = null;
            boolean ret = false;
            switch (v.getId()) {
                case R.id.save_clover:
                    s = cloverInput.getText().toString();
                    ret = writeInt(archive, OFFSET_CLOVER, Integer.parseInt(s));
                    break;
                case R.id.save_tickets:
                    s = ticketsInput.getText().toString();
                    ret = writeInt(archive, OFFSET_TICKETS, Integer.parseInt(s));
                    break;
            }
            v.setTag(s);
            v.setEnabled(!ret);
            showToast(ret ? R.string.success_msg : R.string.failure_msg);
        } catch (NumberFormatException e) {
            showToast(R.string.number_err_msg);
        }
    }

    /**
     * Big Endian to Little Endian
     */
    private static int bigToLittle(int v) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.asIntBuffer().put(v);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    /**
     * Little Endian to Big Endian
     */
    private static int littleToBig(int v) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.asIntBuffer().put(v);
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getInt();
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    private static int readInt(File archive, int offset) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(archive, "r");
            r.seek(offset);
            return littleToBig(r.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(r);
        }
        return -1;
    }

    private static boolean writeInt(File archive, int offset, int value) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(archive, "rwd");
            r.seek(offset);
            r.writeInt(bigToLittle(value));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(r);
        }
        return false;
    }

    private static class MyTextWatcher implements TextWatcher {
        private Button button;

        MyTextWatcher(Button button) {
            this.button = button;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            button.setEnabled(!s.toString().equals(button.getTag()));
        }
    }
}

