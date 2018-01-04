package com.example.namta.vericekme;

import android.os.Debug;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    ListView liste; //global değişkenim
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liste=(ListView)findViewById(R.id.lstview); //xml kısmına bağladık

        WebServisIleListeyiDoldır(); //yapacağım işlemler için oluşturduğum methodum

    }

    private void WebServisIleListeyiDoldır() {
        //uygulama yanıt vermiyor hatasının önüne geçmek için
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String dobiz_url = "http://www.tcmb.gov.tr/kurlar/today.xml"; //merkez bankasının xml dosyasını çekeceğiz

        List<String> doviz_list = new ArrayList<>(); //oluşturduğum döviz listesi
        HttpURLConnection baglanti = null; //web servise ulaşmak için bağlantı oluşturdum. başlangıç değeri null

        try {
            URL url = new URL(dobiz_url);

            baglanti=(HttpURLConnection) url.openConnection(); //bağlantıyı açıyoruz

            int baglanti_durumu=baglanti.getResponseCode();//bağlantı başarılı mı kontrol ediyoz. integer bir değer döner

            if(baglanti_durumu==HttpURLConnection.HTTP_OK){ //HTTP_NOTFOUND da bağlantı başarısız mı diye kontrol eder.

                BufferedInputStream stream=new BufferedInputStream(baglanti.getInputStream());
                DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance(); //kendi sınıf içerisinde nesne örneklemesi yapar newinstance();
                DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();

                Document document =documentBuilder.parse(stream); // document değişkenim var, parse ettim, //iki farklı paket var import tan

                NodeList dovizNodeList = document.getElementsByTagName("Currency");//liste tagname le ulaştık. id le ulaşmak gibi id si yok bunun

                for(int i=0; i<dovizNodeList.getLength(); i++ ) { //bütün etiketlere ulaşmak için for. kaç tane etiket varsa length

                    Element element = (Element) dovizNodeList.item(i); //3farklı paket var

                    NodeList nodeListBirim = element.getElementsByTagName("Unit");
                    NodeList nodeListParaBirimi = element.getElementsByTagName("Isim");
                    NodeList nodeListAlis = element.getElementsByTagName("ForexBuying");
                    NodeList nodeListSatis = element.getElementsByTagName("ForexSelling");

                    String birim = nodeListBirim.item(0).getFirstChild().getNodeValue();//ilk eleman, ilk çocuk,ilk değer
                    String parabirimi = nodeListParaBirimi.item(0).getFirstChild().getNodeValue();
                    String alis = nodeListAlis.item(0).getFirstChild().getNodeValue();
                    String satis = nodeListSatis.item(0).getFirstChild().getNodeValue();
                    if (element.getAttribute("Kod").equals("USD") || element.getAttribute("Kod").equals("DKK") || element.getAttribute("Kod").equals("EUR")) {
                        doviz_list.add(birim + " " + parabirimi + " Alış: " + alis + " Satış: " + satis);
                    }
                }
            }
        }catch (Exception e){
            Log.e("Xml parse hatası",e.getLocalizedMessage().toString()); //hata mesajı

        }finally { //hata olsun olmasın çalışacak. internet bağlantısı varsa
            if(baglanti !=null){
                baglanti.disconnect();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,doviz_list);
            liste.setAdapter(adapter); //adaptörü listeye set
        }

    }
}
