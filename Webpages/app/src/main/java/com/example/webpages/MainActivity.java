package aaaaaacom.example.webpages;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.example.webpages.R;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    private WebView viewArea;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewArea = findViewById(R.id.the_view);
//----Load zoomed out to fit
        viewArea.getSettings().setLoadWithOverviewMode(true);
//----Make the viewport "normal", rather than the size of the webview
        viewArea.getSettings().setUseWideViewPort(false);
        viewArea.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url) {
                view.loadUrl(url);
                return(false);
            }
        });
//----Turn on the zoom controls
        viewArea.getSettings().setBuiltInZoomControls(true);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        EditText urlOrHTMLInput;
        String urlOrHTML;

        switch (view.getId()) {
            case R.id.view_button:
                urlOrHTMLInput = findViewById(R.id.url_or_html);
                urlOrHTML = urlOrHTMLInput.getText().toString().trim();
                if (urlOrHTML.startsWith("http:")) {
                    viewArea.loadUrl(urlOrHTML);
                } else {
                    viewArea.loadData(urlOrHTML,"text/html","utf-8");
                }
                break;
            default:
                break;
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================