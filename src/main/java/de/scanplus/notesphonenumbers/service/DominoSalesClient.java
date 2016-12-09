package de.scanplus.notesphonenumbers.service;

import de.scanplus.notesphonenumbers.util.JSON;
import de.scanplus.notesphonenumbers.data.AddressData;
import de.scanplus.notesphonenumbers.data.AddressLink;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.net.ssl.SSLSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DominoSalesClient {

    private static final Logger LOG = LogManager.getLogger(DominoSalesClient.class);

    private static final String ENV_DOMINO_SEARCH_HOST = "DOMINO_SEARCH_HOST";
    private static final String ENV_DOMINO_SEARCH_URL = "DOMINO_SEARCH_URL";
    private final String ENV_DOMINO_USER = "DOMINO_USER";
    private final String ENV_DOMINO_PASS = "DOMINO_PASS";

    private final String dominoSearchHost;
    private final String dominoSearchUrl;
    private final String dominoUser;
    private final String dominoPass;

    private HttpClient httpClient;
    private HttpClientContext clientContext;

    private final boolean ready;

    public DominoSalesClient() {
        this.dominoUser = System.getenv(ENV_DOMINO_USER);
        this.dominoPass = System.getenv(ENV_DOMINO_PASS);
        this.dominoSearchHost = System.getenv(ENV_DOMINO_SEARCH_HOST);
        this.dominoSearchUrl = System.getenv(ENV_DOMINO_SEARCH_URL);

        this.ready = StringUtils.isNotBlank(dominoUser)
                && StringUtils.isNotBlank(dominoPass)
                && StringUtils.isNotBlank(dominoSearchHost)
                && StringUtils.isNotBlank(dominoSearchUrl)
                && this.buildHTTPClient()
                && this.buildHTTPContext();
    }

    public boolean isReady() {
        return ready;
    }

    public List<AddressLink> loadLinkList(final Integer start, final Integer count) {
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(dominoSearchHost)
                    .setPath(dominoSearchUrl)
                    .setParameter("start", start.toString())
                    .setParameter("count", count.toString())
                    .setParameter("entrycount", "false")
                    .build();
            HttpGet getList = new HttpGet(uri);
            LOG.info("GET (list) started");
            HttpResponse resp = this.httpClient.execute(getList, this.clientContext);
            LOG.info("GET (list) responded with: " + resp.getStatusLine());
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error("Server returned status code: " + resp.getStatusLine().getStatusCode());
                return new ArrayList<>();
            }
            InputStream is = resp.getEntity().getContent();
            List<AddressLink> list = JSON.parseList(is);
            is.close();
            return list;
        } catch (URISyntaxException | IOException ex) {
            LOG.error("", ex);
            return new ArrayList<>();
        }
    }

    public boolean saveAddressData(AddressData ad, AddressLink al) {
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(dominoSearchHost)
                    .setPath(al.getLink())
                    .build();

            HttpPatch patchAddressData = new HttpPatch(uri);
            patchAddressData.setHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(JSON.writeToJSON(ad), ContentType.APPLICATION_JSON);
            patchAddressData.setEntity(se);
            LOG.info("PATCH started with: " + JSON.writeToJSON(ad));
            HttpResponse resp = this.httpClient.execute(patchAddressData, this.clientContext);
            LOG.info("PATCH responded with: " + resp.getStatusLine());
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error("Server returned status code: " + resp.getStatusLine().getStatusCode());
                return false;
            }
            EntityUtils.consumeQuietly(resp.getEntity());
            return true;
        } catch (URISyntaxException | IOException ex) {
            LOG.error("", ex);
            return false;
        }
    }

    public AddressData loadAddressData(AddressLink al) {
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(dominoSearchHost)
                    .setPath(al.getLink())
                    .build();
            HttpGet getAddressData = new HttpGet(uri);
            LOG.info("GET (data) started for: " + al.getLink());
            HttpResponse resp = this.httpClient.execute(getAddressData, this.clientContext);
            LOG.info("GET (data) responded with: " + resp.getStatusLine());
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error("Server returned status code: " + resp.getStatusLine().getStatusCode());
                return null;
            }
            InputStream is = resp.getEntity().getContent();
            AddressData ad = JSON.parseAddressData(is);
            is.close();
            return ad;
        } catch (URISyntaxException | IOException ex) {
            LOG.error("", ex);
            return null;
        }
    }

    private boolean buildHTTPClient() {
        try {
            this.httpClient = HttpClientBuilder.create().setSSLHostnameVerifier((String hostname, SSLSession session) -> true)
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (X509Certificate[] chain, String authType) -> true).build())
                    .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(30).setConnectionRequestTimeout(30).build())
                    .build();
            return true;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex) {
            LOG.error("Could not build HTTP Client!", ex);
            return false;
        }
    }

    private boolean buildHTTPContext() {
        HttpHost targetHost = new HttpHost(dominoSearchHost, 80, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(new String(Base64.getDecoder().decode(dominoUser)),
                        new String(Base64.getDecoder().decode(dominoPass))));

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        this.clientContext = HttpClientContext.create();
        this.clientContext.setCredentialsProvider(credsProvider);
        this.clientContext.setAuthCache(authCache);
        return true;
    }

}
