package se.vgregion.web.http;

import org.springframework.beans.factory.FactoryBean;
import se.vgregion.ssl.ConvenientSslContextFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.http.HttpClient;

public class HttpClientFactory implements FactoryBean {

    private HttpClient httpClient;
    private String trustStore;
    private String trustStorePassword;
    private String trustStoreType;
    private String keyStore;
    private String keyStorePassword;
    private String keyStoreType;

    public HttpClientFactory(String trustStore, String trustStorePassword, String trustStoreType, String keyStore,
                             String keyStorePassword, String keyStoreType) {
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.trustStoreType = trustStoreType;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.keyStoreType = keyStoreType;
    }

    @Override
    public Object getObject() throws Exception {
        if (httpClient == null) {
            SSLParameters sslParameters = new SSLParameters();

            ConvenientSslContextFactory contextFactory = new ConvenientSslContextFactory(trustStore, trustStorePassword,
                    trustStoreType, keyStore, keyStorePassword, keyStoreType);

            SSLContext sslContext = contextFactory.createSslContext();
            httpClient = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParameters).build();
        }

        return httpClient;
    }

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
