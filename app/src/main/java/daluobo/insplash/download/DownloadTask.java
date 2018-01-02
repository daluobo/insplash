package daluobo.insplash.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import daluobo.insplash.db.model.DownloadItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<DownloadItem, Integer, String> {
    public static final String TAG = "DownloadTask";

    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;

    private DownloadItem mDownloadItem;
    private DownloadListener mListener;

    public DownloadTask(DownloadListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(DownloadItem... downloadItems) {
        InputStream inputStream = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            long downloadedLength = 0;
            mDownloadItem = downloadItems[0];
            String fileName = mDownloadItem.name;
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();

            File fileDir = new File(directory + "/Insplash");
            createDir(fileDir);
            file = new File(directory + "/Insplash/" + fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }

            long contentLength = getContentLength(mDownloadItem.url);
            mDownloadItem.length = contentLength;
            if (contentLength == 0) {
                return DownloadState.FAILED;
            } else if (contentLength == downloadedLength) {
                return DownloadState.SUCCESS;
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(mDownloadItem.url)
                    .build();

            Response response = client.newCall(request).execute();

            if (response != null) {
                inputStream = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);

                byte[] bytes = new byte[1024];
                int total = 0;
                int len;

                while ((len = inputStream.read(bytes)) != -1) {
                    if (isCanceled) {
                        return DownloadState.CANCELED;
                    } else if (isPaused) {
                        return DownloadState.PAUSED;
                    } else {
                        total += len;
                        savedFile.write(bytes, 0, len);

                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                return DownloadState.SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
        return DownloadState.FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            mListener.onProgress(mDownloadItem, progress);
            lastProgress = progress;
            Log.d(TAG, progress + "");
        }
    }

    @Override
    protected void onPostExecute(String type) {
        switch (type) {
            case DownloadState.SUCCESS:
                mListener.onSuccess(mDownloadItem, DownloadState.SUCCESS);
                break;
            case DownloadState.FAILED:
                mListener.onFailed(mDownloadItem, DownloadState.FAILED);
                break;
            case DownloadState.PAUSED:
                mListener.onPaused(mDownloadItem, DownloadState.PAUSED);
                break;
            case DownloadState.CANCELED:
                mListener.onCanceled(mDownloadItem, DownloadState.CANCELED);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }

    public static void createDir(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                //dir exists
            } else {
                //the same name file exists, can not create dir
            }
        } else {
            file.mkdir();
        }
    }
}
