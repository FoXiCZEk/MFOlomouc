package cz.foxiczek.mfolomouc;

import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by liskbpet on 13. 9. 2016.
 */
class WWWData {


    public String getWWWData(String data, String liga){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        HttpGet httpget = new HttpGet("http://www.mfolomouc.cz/" + data);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String[] output = new String[1];
        try
        {
            CloseableHttpResponse httpresponse = httpclient.execute(httpget);
            HttpEntity httpentity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent()));
            StringBuilder total = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                total.append(line);
            }
            output[0] = total.toString();
            String tabulka = output[0];
            int start = tabulka.indexOf(liga);
            int end = tabulka.indexOf("','",start);
            tabulka = tabulka.substring(start,end);
            tabulka = tabulka.replaceAll("<br>","\n");
            tabulka = tabulka.replaceAll("&nbsp;&nbsp;"," ");
            tabulka = tabulka.replaceAll("</b>","");

            if(data.startsWith("rozlosovani")){
                if(tabulka.contains("class='rozlos'") || tabulka.contains("class='den'")) {
                    tabulka = dataWorksRozlosovani(tabulka);
                    output[0] = tabulka;
                }
                else{
                    output[0] = "ROZLOSOVANI NENALEZENO";
                }
            }
            else {
                if(data.contains("vysledky")) {
                    if(tabulka.contains("class='vysl'")){
                        tabulka = dataWorks(tabulka);
                        output[0] = tabulka;
                    }
                    else {
                        output[0] = "VYSLEDKY NENALEZENY";
                    }
                }else{
                    if(tabulka.contains("class='vysl'")) {
                        tabulka = dataWorks(tabulka);
                        output[0] = tabulka;
                    }
                    else{
                        output[0] = "TABULKY NENALEZENY";
                    }
                }
            }
            EntityUtils.consume(httpentity);
            httpresponse.close();
            httpclient.close();
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }


        return output[0];
    }



    public String getAdresar(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        HttpGet httpget = new HttpGet("http://www.mfolomouc.cz/category/adresar-hrist/");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String[] output = new String[1];
        try
        {
            CloseableHttpResponse httpresponse = httpclient.execute(httpget);
            HttpEntity httpentity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent()));
            StringBuilder total = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                total.append(line);
            }
            output[0] = total.toString();
            String temp = output[0];
            int indexS = temp.indexOf("povrch");
            int indexE = temp.indexOf("</div>",indexS);
            temp = temp.substring(indexS,indexE);
            output[0] = adresarLinksRemove(temp);


            httpresponse.close();
            httpclient.close();


        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }

        return output[0];
    }

    private String adresarLinksRemove(String input) {
        while (input.contains("<a href")) {
            String temp;
            int indexS = input.indexOf("<a href");
            int indexE = input.indexOf("\"", indexS);
            temp = input.substring(indexS, indexE +1);
            input = input.replace(temp, "");
            indexS = input.indexOf("target=");
            indexE = input.indexOf("\">");
            temp = input.substring(indexS -2 ,indexE +1);
            input = input.replace(temp,"=");

            //input.replaceAll("<a href=","");
            //input.replaceAll("target=","");
            //input.replaceAll("_blank","");

        }

        input = input.replaceAll("</a>", "");
        input = input.replaceAll("&#8211;","");
        input = input.replaceAll("<strong>","");
        input = input.replaceAll("</strong>","");
        input = input.replaceAll("<p>","");

        input = input.replaceAll("</span>", "");
        while(input.contains("span")){
            String temp2;
            int indexS = input.indexOf("<span");
            int indexE = input.indexOf(">",indexS);
            temp2 = input.substring(indexS, indexE);
            input = input.replace(temp2, "");
        }


        input = input.replaceAll("\\s+", " ");

        input = input.replaceAll("</p>","\n");

        input = input.replaceAll(">", "");

        input = "Hriste \t" + input;
        //rozrazeni do tabulek

        input = input.replaceAll(" - ", "-");
        input = input.replaceAll("-", " ");
        return input;
    }


    private String dataWorks(String input){

        input = input.replaceAll("<tr>","");
        input = input.replaceAll("<tr class='vysl'>","");
        input = input.replaceAll("<tr class='lichy'>","");
        input = input.replaceAll("</td>",";");
        input = input.replaceAll("<td>","");
        input = input.replaceAll("<td class='vysl'>","");
        input = input.replaceAll("\\s+", " ");

        input = input.replaceAll("</tr>","\n");
        int index =input.indexOf("</table>");
        input = input.substring(0,index);




        return input;
    }

    private String dataWorksRozlosovani(String input){
        input = input.replaceAll("<tr>","");
        input = input.replaceAll("<tr class='vysl'>","");
        input = input.replaceAll("<tr class='lichy'>","");
        input = input.replaceAll("</tr>","\n");
        input = input.replaceAll("<td>","");
        input = input.replaceAll("<td class='datum'>"," ");
        input = input.replaceAll("<td class='den'>"," ");
        input = input.replaceAll("<td class='stadion'>"," ");
        input = input.replaceAll("<td class='sudi'>"," ");
        input = input.replaceAll("</td>",";");
        int index =input.indexOf("</table>");
        input = input.replaceAll("</a>","");
        input = input.substring(0,index);
        input = removeLinks(input);
        return input;
    }

    private String removeLinks(String input){
        while(input.contains("<a href")){
            String temp;
            int indexS = input.indexOf("<a href");
            int indexE = input.indexOf(">",indexS);
            temp = input.substring(indexS, indexE);
            input = input.replace(temp," ");

        }
        int index =input.indexOf("</table>");
        input = input.substring(0,index);
        input = input.replaceAll(">","");

        return input;


    }


    public String getVypisy(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        HttpGet httpget = new HttpGet("http://www.mfolomouc.cz/category/zapisy-z-vv-a-dk/");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String[] output = new String[1];
        try
        {
            CloseableHttpResponse httpresponse = httpclient.execute(httpget);
            HttpEntity httpentity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent()));
            StringBuilder total = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                total.append(line);
            }
            output[0] = total.toString();
            String temp = output[0];
            int indexS = temp.indexOf("art-PostHeader");
            int indexE = temp.indexOf("cleared",indexS);
            temp = temp.substring(indexS,indexE);
            temp = temp.replaceAll("<p>", "");
            temp = temp.replaceAll("<strong>", "");
            temp = temp.replaceAll("</strong>", "");

            temp = temp.replaceAll("\\s+", " ");
            temp = temp.replaceAll("</p>", "\n");
            while(temp.contains("<span")){
                int indS = temp.indexOf("<span");
                int indE = temp.indexOf(">",indS);
                String tmp = temp.substring(indS, indE);
                temp = temp.replaceAll(tmp, "");

            }
            temp = temp.replaceAll("</span>", "");

            output[0] = removeHeaderVypis(temp);
            output[0] = output[0].replaceAll(">","");
            output[0] = output[0].replaceAll("&#8211;","");
            output[0] = output[0].replaceAll("</div","");
            output[0] = output[0].replaceAll("<div","");
            output[0] = output[0].replaceAll("class=","");
            output[0] = output[0].replaceAll("\\|","\n");
            httpresponse.close();
            httpclient.close();


        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }

        return output[0];
    }

    public String removeHeaderVypis(String input){
        String temp;
        int indexS;
        int indexE;
        indexS = input.indexOf("art-");
        indexE = input.indexOf("odkaz");
        temp = input.substring(indexS, indexE);
        input = input.replaceFirst(temp, "");

        indexS = input.indexOf("odkaz");
        indexE = input.indexOf(">");
        temp = input.substring(indexS, indexE + 1);
        input = input.replaceFirst(temp, "");
        input = input.replaceAll("</a>", "");

        indexS = input.indexOf("<img");
        indexE = input.indexOf("Nehera");
        temp = input.substring(indexS, indexE + 1);
        input = input.replaceFirst(temp, "");

        indexS = input.indexOf("</h2");
        indexE = input.indexOf("Zapsal");
        temp = input.substring(indexS, indexE -1);
        input = input.replaceFirst(temp, "");

        indexS = input.indexOf("<img");
        indexE = input.indexOf("Přítomni");
        temp = input.substring(indexS, indexE);
        input = input.replaceFirst(temp, "");
        return input;
    }



}



