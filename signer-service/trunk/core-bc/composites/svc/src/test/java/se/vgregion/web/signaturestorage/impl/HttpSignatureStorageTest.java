package se.vgregion.web.signaturestorage.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.springframework.oxm.Marshaller;
import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.signera.signature._1.SignatureFormat;
import se.vgregion.web.security.services.SignatureEnvelopeFactory;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.mock;
import static org.mockserver.model.HttpRequest.request;

public class HttpSignatureStorageTest {
    private static final String SIGNATURE_NAME = "signaturename";
    private static final SignatureFormat SIGNATURE_FORMAT = SignatureFormat.CMS;
    private static final String SIGNATURE = "signature";
    private static final String REDIRECT_URI = "http://localhost:8765";
    private static URI anyUri;

    private SignatureEnvelope envelope;

    private HttpSignatureStorage signatureStorage;
    @Mock
    private Marshaller marshaller = mock(Marshaller.class);

    static {
        try {
            anyUri = new URI(REDIRECT_URI);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClientAndServer mockServer;

    @BeforeClass
    public static void startServer() {
        mockServer = ClientAndServer.startClientAndServer(8765);
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    @Before
    public void setUp() throws Exception {
        signatureStorage = new HttpSignatureStorage(HttpClient.newBuilder().build(), marshaller);
        envelope = SignatureEnvelopeFactory.createSignatureEnvelope(SIGNATURE_NAME, SIGNATURE_FORMAT, SIGNATURE);
    }

    @Test
    public final void shouldReturnRedirectUriIfSubmitResponseIssuesAnRedirect() throws Exception {
        // Given
        mockServer.when(request(), Times.once())
                .respond(org.mockserver.model.HttpResponse.response()
                        .withHeader("Location", REDIRECT_URI)
                        .withStatusCode(302));

        // When
        String actualRedirectUri = signatureStorage.submitSignature(anyUri, envelope);

        // Then
        assertEquals(REDIRECT_URI, actualRedirectUri);
    }

    @Test
    public final void shouldReturnEmptyStringIfSubmitResponseIsSuccessful() throws Exception {
        // Given
        mockServer.when(request(), Times.once())
                .respond(org.mockserver.model.HttpResponse.response()
                        .withHeader("Location", "")
                        .withStatusCode(302));

        // When
        String actualRedirectUri = signatureStorage.submitSignature(anyUri, envelope);

        // Then
        assertEquals(StringUtils.EMPTY, actualRedirectUri);
    }

    @Test(expected = SignatureStoreageException.class)
    public final void shouldThrowSignatureStoreageExceptionIfSubmitFails() throws Exception {
        // Given
        mockServer.when(request(), Times.once())
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(400));

        // When
        signatureStorage.submitSignature(anyUri, envelope);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSubmitUriIsNull() throws Exception {
        signatureStorage.submitSignature(null, envelope);
    }

}
