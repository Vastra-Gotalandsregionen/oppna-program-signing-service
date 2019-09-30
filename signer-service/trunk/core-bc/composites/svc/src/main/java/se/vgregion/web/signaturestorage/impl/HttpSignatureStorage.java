package se.vgregion.web.signaturestorage.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

/**
 * An Http-implementation of {@link SignatureStorage}. Has functionality to submit a signature to an http server
 * using http or https.
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class HttpSignatureStorage implements SignatureStorage {

    private Marshaller marshaller;
    private HttpClient httpClient;

    /**
     * Constructs an instance of {@link HttpSignatureStorage}.
     *
     * @param httpClient an http client to use when submiting the signature
     * @param marshaller marshaller to use when "serializing" to xml
     */
    public HttpSignatureStorage(HttpClient httpClient, Marshaller marshaller) {
        this.httpClient = httpClient;
        this.marshaller = marshaller;
    }

    /*
     * (non-Javadoc)
     *
     * @see se.vgregion.web.signaturestorage.SignatureStorage#submitSignature(java.net.URI, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String submitSignature(URI submitUri, SignatureEnvelope envelope)
            throws SignatureStoreageException, IOException {
        if (submitUri == null) {
            throw new IllegalArgumentException("Submit Uri name is not allowed to be null");
        }

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        marshaller.marshal(envelope, new StreamResult(boas));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(boas.toString(StandardCharsets.UTF_8)))
                .uri(submitUri)
                .header("Content-type", "text/xml")
                .build();

        String returnLocation = StringUtils.EMPTY;
        try {
            HttpResponse httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            HttpStatus responseStatus = HttpStatus.valueOf(httpResponse.statusCode());
            if (FOUND.value() == responseStatus.value()) {
                returnLocation = httpResponse.headers().firstValue("Location").get();
            } else if (OK.value() != responseStatus.value()) {
                String reasonPhrase = responseStatus.getReasonPhrase();
                throw new SignatureStoreageException("Invalid status code: " + responseStatus.toString() + " - "
                        + reasonPhrase);
            }
        } catch (InterruptedException e) {
            throw new SignatureStoreageException(e);
        }

        return returnLocation;
    }
}
