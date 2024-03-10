package it.nathanub.RewardADs.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Api {
	private GetConfig config = new GetConfig();
	int coins = 0;
	
	public int getCoin(Player player) {
		CompletableFuture<JSONObject> future = new CompletableFuture<>();

		sendRequest("coin/" + config.getCode() + "/" + player.getName(), new ApiResponseListener() {
		    @Override
		    public void onResponse(String response) {
		        try {
		            JSONParser parser = new JSONParser();
		            JSONArray jsonArray = (JSONArray) parser.parse(response);
		            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		            future.complete(jsonObject);
		        } catch (ParseException e) {
		            future.completeExceptionally(new RuntimeException("Failed to parse JSON: " + e.getMessage()));
		        }
		    }

		    @Override
		    public void onFailure(String response) {
		        future.completeExceptionally(new RuntimeException("Failed to get coins"));
		    }
		});

		try {
		    JSONObject object = future.get();
		    coins = Integer.parseInt((String) object.get("coins_player"));
		} catch(InterruptedException e) {
		    e.printStackTrace();
		} catch(ExecutionException e) {
			e.printStackTrace();
		}

		return coins;
    }
	
	public CompletableFuture<JSONArray> getCoins() {
        CompletableFuture<JSONArray> future = new CompletableFuture<>();
        
        sendRequest("coins/" + config.getCode(), new ApiResponseListener() {
			@Override
			public void onResponse(String response) {
				try {
					JSONParser parser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) parser.parse(response);
                    future.complete(jsonArray);
				 } catch(ParseException e) {
	                    future.completeExceptionally(new RuntimeException("Failed to parse JSON: " + e.getMessage()));
	             }
			}

			@Override
            public void onFailure(String response) {
                future.completeExceptionally(new RuntimeException("Failed to get coins"));
            }
        });

        return future;
    }
	
	public void decreaseCoins(Player player, int amount) {
        sendRequest("decreasecoins/" + config.getCode() + "/" + player.getName() + "/" + amount, new ApiResponseListener() {
			@Override
			public void onResponse(String response) {}

			@Override
            public void onFailure(String response) {}
        });
    }
	
	public CompletableFuture<JSONObject> getServer() {
        CompletableFuture<JSONObject> future = new CompletableFuture<>();

        sendRequest("server/" + config.getCode(), new ApiResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) parser.parse(response);
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    future.complete(jsonObject);
                } catch(ParseException e) {
                    future.completeExceptionally(new RuntimeException("Failed to parse JSON: " + e.getMessage()));
                }
            }

            @Override
            public void onFailure(String response) {
                future.completeExceptionally(new RuntimeException("Failed to get server"));
            }
        });

        return future;
    }
	
	public void setEarnPerAD() {
        sendRequest("earnperad/" + config.getCode() + "/" + config.getEarnPerAD(), new ApiResponseListener() {
			@Override
			public void onResponse(String response) {}

			@Override
			public void onFailure(String response) {}
        });
    }
	
	private void sendRequest(String table, final ApiResponseListener listener) {
        String url = "https://url/" + table;

        new Thread(() -> {
            try {
                InputStream certInputStream = Api.class.getResourceAsStream("/res/certificate.crt");
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) cf.generateCertificate(certInputStream);

                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("server", cert);

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                URL urlObj = new URL(url);
                HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    if (listener != null) {
                        listener.onResponse(response.toString());
                    }
                } else {
                    if (listener != null) {
                        listener.onFailure("Failed to fetch data. Response code: " + responseCode);
                    }
                }

                connection.disconnect();

            } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onFailure("Failed to fetch data. Exception: " + e.getMessage());
                }
            }
        }).start();
    }

    public interface ApiResponseListener {
        void onResponse(String response);
        void onFailure(String response);
    }
}