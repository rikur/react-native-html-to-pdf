/*
 * Created on 11/15/17.
 * Written by Islam Salah with assistance from members of Blink22.com
 */

package android.print;

import com.facebook.react.bridge.ReadableMap;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

/**
 * Converts HTML to PDF.
 * <p>
 * Can convert only one task at a time, any requests to do more conversions
 * before ending the current task are ignored.
 */
public class PdfConverter implements Runnable {

  private static final String TAG = "PdfConverter";
  private static PdfConverter sInstance;

  private Context mContext;
  private String mHtmlString;
  private File mPdfFile;
  private PrintAttributes mPdfPrintAttrs;
  private boolean mIsCurrentlyConverting;
  private WebView mWebView;

  private PdfConverter() {}

  public static synchronized PdfConverter getInstance() {
    if (sInstance == null)
      sInstance = new PdfConverter();

    return sInstance;
  }

  @Override
  public void run() {
    mWebView = new WebView(mContext);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
          throw new RuntimeException("call requires API level 19");
        else {
          PrintDocumentAdapter documentAdapter =
              mWebView.createPrintDocumentAdapter();
          documentAdapter.onLayout(
              null, getPdfPrintAttrs(), null,
              new PrintDocumentAdapter.LayoutResultCallback() {}, null);
          documentAdapter.onWrite(
              new PageRange[] {PageRange.ALL_PAGES}, getOutputFileDescriptor(),
              null, new PrintDocumentAdapter.WriteResultCallback() {
                @Override
                public void onWriteFinished(PageRange[] pages) {
                  destroy();
                }
              });
        }
      }
    });
    mWebView.loadDataWithBaseURL(null, mHtmlString, "text/html", "utf-8",null);    
  }

  public PrintAttributes getPdfPrintAttrs() {
    return mPdfPrintAttrs != null ? mPdfPrintAttrs : getDefaultPrintAttrs();
  }

  public void setPdfPrintAttrs(PrintAttributes printAttrs) {
    this.mPdfPrintAttrs = printAttrs;
  }

  public void convert(Context context, String htmlString, File file,
                      final ReadableMap options) {
    if (context == null)
      throw new IllegalArgumentException("context can't be null");
    if (htmlString == null)
      throw new IllegalArgumentException("htmlString can't be null");
    if (file == null)
      throw new IllegalArgumentException("file can't be null");

    if (mIsCurrentlyConverting)
      return;

    setOptions(options);

    mContext = context;
    mHtmlString = htmlString;
    mPdfFile = file;
    mIsCurrentlyConverting = true;
    runOnUiThread(this);
  }

  private ParcelFileDescriptor getOutputFileDescriptor() {
    try {
      mPdfFile.createNewFile();
      return ParcelFileDescriptor.open(
          mPdfFile, ParcelFileDescriptor.MODE_TRUNCATE |
                        ParcelFileDescriptor.MODE_READ_WRITE);
    } catch (Exception e) {
      Log.d(TAG, "Failed to open ParcelFileDescriptor", e);
    }
    return null;
  }

  private PrintAttributes getDefaultPrintAttrs() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
      return null;

    return new PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.NA_GOVT_LETTER)
        .setResolution(new PrintAttributes.Resolution(
            "RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .build();
  }

  private PrintAttributes.MediaSize getMediaSize(String size,
                                                 String orientation) {
    PrintAttributes.MediaSize mediaSize = null;
    switch (size) {
    case "A0":
      mediaSize = PrintAttributes.MediaSize.ISO_A0;
      break;
    case "A1":
      mediaSize = PrintAttributes.MediaSize.ISO_A1;
      break;
    case "A2":
      mediaSize = PrintAttributes.MediaSize.ISO_A2;
      break;
    case "A3":
      mediaSize = PrintAttributes.MediaSize.ISO_A3;
      break;
    case "A4":
      mediaSize = PrintAttributes.MediaSize.ISO_A4;
      break;
    case "A5":
      mediaSize = PrintAttributes.MediaSize.ISO_A5;
      break;
    case "A6":
      mediaSize = PrintAttributes.MediaSize.ISO_A6;
      break;
    case "A7":
      mediaSize = PrintAttributes.MediaSize.ISO_A7;
      break;
    case "A8":
      mediaSize = PrintAttributes.MediaSize.ISO_A8;
      break;
    case "UsGovernmentLetter":
      mediaSize = PrintAttributes.MediaSize.NA_GOVT_LETTER;
      break;
    case "UsLetter":
      mediaSize = PrintAttributes.MediaSize.NA_LETTER;
      break;
    case "UsLegal":
      mediaSize = PrintAttributes.MediaSize.NA_LEGAL;
      break;
    default:
      mediaSize = PrintAttributes.MediaSize.ISO_A4;
      break;
    }
    if (orientation.equals("Landscape")) {
      return mediaSize.asLandscape();
    }
    return mediaSize;
  }

  private void setOptions(final ReadableMap options) {

    ReadableMap page = options.hasKey("page") ? options.getMap("page") : null;
    String size = "A4";
    String orientation = "Portrait";
    if (page != null) {
      size = page.hasKey("size") ? page.getString("size") : size;
      orientation = page.hasKey("orientation") ? page.getString("orientation")
                                               : orientation;
      Log.d(TAG, String.format("size.......: %s", size));
      Log.d(TAG, String.format("orientation: %s", orientation));
    }

    PrintAttributes.MediaSize mediaSize = getMediaSize(size, orientation);
    PrintAttributes printAttributes =
        new PrintAttributes.Builder()
            .setMediaSize(mediaSize)
            .setResolution(new PrintAttributes.Resolution(
                "RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build();

    setPdfPrintAttrs(printAttributes);
  }

  private void runOnUiThread(Runnable runnable) {
    Handler handler = new Handler(mContext.getMainLooper());
    handler.post(runnable);
  }

  private void destroy() {
    mContext = null;
    mHtmlString = null;
    mPdfFile = null;
    mPdfPrintAttrs = null;
    mIsCurrentlyConverting = false;
    mWebView = null;
  }
}
