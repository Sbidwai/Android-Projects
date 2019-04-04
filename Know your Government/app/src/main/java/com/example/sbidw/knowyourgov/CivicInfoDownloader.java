package com.example.sbidw.knowyourgov;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CivicInfoDownloader extends AsyncTask<Person, Void, String> {

    MainActivity mainActivity;

    private int count;


    private String url = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private String urlString = "&address=";
    private String apikey = "AIzaSyCm-IOt1gloqz8VOXmIzfX_Fy6PdtfJGGM";
    private String completeURL = url + apikey + urlString;

    private static final String TAG="CivicInfoDownloader";

    String addLine1 = null, addLine2 = null;
    String city1 = null, state1 = null, zip1 = null;
    String  photo = null, urlsForObject = null;
    String address1 = null;
    String postname = null;
    String personName = null, phone = null, email = null, personParty = null;
    String location;

    ArrayList<Person> personArrayList = new ArrayList<Person>();
    HashMap<String, ArrayList<Person>> listHashMap = new HashMap<String, ArrayList<Person>>();

    public CivicInfoDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Person... params) {

        String completeURL = this.completeURL +params[0].getZipcode();

        Uri dataUri = Uri.parse(completeURL);
        String urlUsed = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlUsed);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            sb.append("[{\"company_name\": \"NULL\", \"company_symbol\": \"123\", \"listing_exchange\": \"NULL\"}]");
            return sb.toString();
        }

       return sb.toString();


    }

    @Override
    protected void onPostExecute(String s) {

        parseJSON(s);
        mainActivity.setPersonList(listHashMap);

    }


    private HashMap<String, ArrayList<Person>> parseJSON(String s) {

        ArrayList<Person> personList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(s);
            JSONObject response = jsonObject.getJSONObject("normalizedInput");
            count = response.length();
            String city = jsonObject.getJSONObject("normalizedInput").getString("city");
            String state = jsonObject.getJSONObject("normalizedInput").getString("state");
            String zipcode = jsonObject.getJSONObject("normalizedInput").getString("zip");

            if(city!=null || state!=null || zipcode!=null)
            {
                location = city +", "+ state +" "+ zipcode;
            }
            JSONObject jsonObject1 = new JSONObject(s);
            JSONObject jsonObject2 = new JSONObject(s);

            JSONArray ListofOffices = jsonObject1.getJSONArray("offices");

            for(int i1 = 0; i1 < ListofOffices.length(); i1++)
            {
               postname = ListofOffices.getJSONObject(i1).getString("name");
                JSONArray offices = (ListofOffices.getJSONObject(i1).getJSONArray("officialIndices"));

                for (int j = 0; j < ListofOffices.getJSONObject(i1).getJSONArray("officialIndices").length(); j++)
                {
                    Person person = new Person();
                    int id11 = offices.getInt(j);

                    person.setPersonDesignation(postname);

                    personName = jsonObject1.getJSONArray("officials").getJSONObject(id11).getString("name");
                    person.setPersonName(personName);
                    try
                    {
                        if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("party"))
                        {
                            personParty = jsonObject1.getJSONArray("officials").getJSONObject(id11).getString("party");
                        }
                        else {
                            personParty = "Unknown";
                        }
                        person.setParty(personParty);
                    }

                    catch (JSONException e)
                    {
                        Log.e(TAG, "doInBackground3: ", e);
                    }
                    if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("address"))
                    {
                        try
                        {
                            {
                                JSONObject line1 = jsonObject2.getJSONArray("officials").getJSONObject(id11).getJSONArray("address").getJSONObject(0);
                                if (line1.has("line1")) {
                                    addLine1 = line1.getString("line1");
                                }
                                if (line1.has("line2")) {
                                    addLine2 = line1.getString("line2");
                                }
                                if (line1.has("city")) {
                                    city1 = line1.getString("city");
                                }
                                if (line1.has("state")) {
                                    state1 = line1.getString("state");
                                }
                                if (line1.has("line2")) {
                                    zip1 = line1.getString("zip");
                                }
                                person.setLineOne(addLine1);
                                person.setLineTwo(addLine2);
                                person.setCity(city1);
                                person.setState(state1);
                                person.setZipcode(zip1);

                           }
                        }
                        catch (JSONException e)
                        {
                            Log.e(TAG, "JSONEXCEPTION: ", e);
                        }
                    }
                    if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("phones"))
                    {

                        phone = (jsonObject1.getJSONArray("officials").getJSONObject(id11).getJSONArray("phones").get(0).toString());
                        person.setPhone(phone);

                    }
                    if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("urls"))
                    {

                        urlsForObject =  jsonObject1.getJSONArray("officials").getJSONObject(id11).getJSONArray("urls").get(0).toString();

                        person.setUrls(urlsForObject);
                        person.setWebsite(urlsForObject);


                    }

                    if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("photoUrl"))
                    {

                        photo = jsonObject1.getJSONArray("officials").getJSONObject(id11).getString("photoUrl");
                        person.setPhotourls(photo);

                    }
                    else
                    {
                        photo = "NoPhoto";
                        person.setPhotourls(photo);
                    }

                    if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("emails"))
                    {

                        email = jsonObject1.getJSONArray("officials").getJSONObject(id11).getJSONArray("emails").get(0).toString();
                        person.setEmail(email);

                    }

                    if(jsonObject1.getJSONArray("officials").getJSONObject(id11).has("channels"))
                    {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("GooglePlus","90000");
                        hashMap.put("Facebook","90000");
                        hashMap.put("Twitter","90000");
                        hashMap.put("YouTube","90000");

                        JSONArray LinkObjects = jsonObject1.getJSONArray("officials").getJSONObject(id11).getJSONArray("channels");

                        for ( int i = 0; i < LinkObjects.length(); i++)
                        {

                            if (LinkObjects.getJSONObject(i).getString("type").equals("GooglePlus")) {
                                hashMap.put("GooglePlus", LinkObjects.getJSONObject(i).getString("id"));
                            }

                            if (LinkObjects.getJSONObject(i).getString("type").equals("Facebook")) {
                                hashMap.put("Facebook", LinkObjects.getJSONObject(i).getString("id"));
                            }
                            if (LinkObjects.getJSONObject(i).getString("type").equals("Twitter")) {
                                hashMap.put("Twitter", LinkObjects.getJSONObject(i).getString("id"));
                            }

                            if (LinkObjects.getJSONObject(i).getString("type").equals("YouTube")) {
                                hashMap.put("YouTube", LinkObjects.getJSONObject(i).getString("id"));
                            }


                        }

                        person.setChannels(hashMap);


                    }

                    personList.add(person);

                    listHashMap.put(location, personList);
                }
            }

            return listHashMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
