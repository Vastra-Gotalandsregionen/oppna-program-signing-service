package se.vgregion.security.sign;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import se.funktionstjanster.grp.service.v1_0.CollectResponseType;
import se.funktionstjanster.grp.service.v1_0.ProgressStatusType;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.ServiceIdService;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.*;

public class WebSignControllerTest {
    @Mock
    private SignatureService signatureService;
    @Mock
    private Repository<ELegType, String> eLegTypes;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Exception exception;

    private WebSignController signController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signController = new WebSignController(signatureService, eLegTypes, TicketManager.getInstance());
        signController.setInternalNetworks("xxx, yyy");
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void shouldReturnUnmodifiableCollectionOfClientTypes() throws Exception {
        // Given
        final Collection<ELegType> allTypes = Arrays.asList(new ELegType("a", "a", "a", PkiClient.NEXUS_PERSONAL_4),
                new ELegType("b", "b", "b", PkiClient.NEXUS_PERSONAL_4));
        given(eLegTypes.findAll()).willReturn(allTypes);

        // When
        Collection<ELegType> actualTypes = signController.getClientTypes();

        // Then
        assertEquals(allTypes.size(), actualTypes.size());
        actualTypes.add(new ELegType("c", "c", "c", PkiClient.NETMAKER_NETID_4));
    }

    @Test
    public final void shouldBindELegTypeToWebDataBinder() throws Exception {
        // Given
        final WebDataBinder binder = new WebDataBinder(null);
        // When
        signController.initBinder(binder);
        // Then
        assertEquals(ELegTypeEditor.class, binder.findCustomEditor(ELegType.class, "").getClass());
    }

    @Test
    public final void shouldPrepareSigningIfNoClientTypeIsSpecified() throws Exception {
        // Given
        final Model model = new ExtendedModelMap();
        final SignatureData signData = new SignatureData();
        setATicketOnSignatureData(signData);
        given(request.getRemoteHost()).willReturn("example.com");
        // When
        String viewName = signController.prepareSignNoClientType(signData, model, request);
        // Then
        assertEquals("clientTypeSelection", viewName);
        assertTrue(model.asMap().containsKey("signData"));
        assertTrue(model.asMap().containsValue(signData));
    }

    private void setATicketOnSignatureData(SignatureData signData) throws TicketException {
        TicketManager ticketManager = TicketManager.getInstance();
        ServiceIdService service = mock(ServiceIdService.class);
        given(service.containsServiceId(eq("existingServiceId"))).willReturn(true);
        ticketManager.setServiceIdService(service);
        signData.setTicket(new TicketDto(ticketManager.solveTicket("existingServiceId")).toString());
    }

    @Test
    public final void shouldPrepareForSignClientRequest() throws Exception {
        // Given
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String nonce = "nonce";
        final Model model = new ExtendedModelMap();
        signData.setClientType(new ELegType("", "", "", clientType));
        setATicketOnSignatureData(signData);

        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.generateNonce(any(PkiClient.class))).willReturn(nonce);
        given(request.isSecure()).willReturn(false);
        given(request.getServerName()).willReturn("example.com");
        given(request.getServerPort()).willReturn(7080);
        given(request.getContextPath()).willReturn("/ss");

        // When
        String viewName = signController.prepareSign(signData, model, request);

        // Then
        assertEquals(clientType.toString(), viewName);
        assertTrue(model.asMap().containsKey("signData"));
        assertTrue(model.asMap().containsValue(signData));
        assertTrue(model.asMap().containsKey("postbackUrl"));
        assertEquals("http://example.com:7080/ss/sign", model.asMap().get("postbackUrl"));
    }

    @Test
    public final void shouldPrepareForSecureSignClientRequest() throws Exception {
        // Given
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String nonce = "nonce";
        final Model model = new ExtendedModelMap();
        signData.setClientType(new ELegType("", "", "", clientType));
        setATicketOnSignatureData(signData);

        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.generateNonce(any(PkiClient.class))).willReturn(nonce);
        given(request.isSecure()).willReturn(true);
        given(request.getServerName()).willReturn("example.com");
        given(request.getServerPort()).willReturn(7080);
        given(request.getContextPath()).willReturn("/ss");

        // When
        String viewName = signController.prepareSign(signData, model, request);

        // Then
        assertEquals(clientType.toString(), viewName);
        assertTrue(model.asMap().containsKey("signData"));
        assertTrue(model.asMap().containsValue(signData));
        assertTrue(model.asMap().containsKey("postbackUrl"));
        assertEquals("https://example.com:7080/ss/sign", model.asMap().get("postbackUrl"));
    }

    @Test
    public final void shouldVerifyAndSaveSignatureWithRedirect() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String expectedRedirectUrl = "http://www.example.com";
        signData.setClientType(new ELegType("", "", "", clientType));

        // Given
        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.save(any(SignatureData.class))).willReturn(expectedRedirectUrl);

        // When
        String redirectUrl = signController.verifyAndSaveSignature(signData);

        // Then
        assertEquals("redirect:" + expectedRedirectUrl, redirectUrl);
    }

    @Test
    public final void shouldVerifyAndSaveSignatureWithNoRedirect() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        signData.setClientType(new ELegType("", "", "", clientType));

        // Given
        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.save(any(SignatureData.class))).willReturn(null);

        // When
        String redirectUrl = signController.verifyAndSaveSignature(signData);

        // Then
        assertEquals("verified", redirectUrl);
    }

    @Test
    public final void shouldHandleAllKindOfErrors() {
        // When
        ModelAndView modelAndView = signController.handleException(exception, request);
        // Then
        assertEquals("errorHandling", modelAndView.getViewName());
        assertTrue(modelAndView.getModelMap().containsKey("class"));
        assertTrue(modelAndView.getModelMap().containsValue(ClassUtils.getShortName(exception.getClass())));
    }

    @Test
    public final void signMobileBankId() throws Exception {

        // Given
        TicketManager ticketManager = TicketManager.getInstance();
        ServiceIdService serviceIdService = mock(ServiceIdService.class);
        ticketManager.setServiceIdService(serviceIdService);

        given(serviceIdService.containsServiceId(anyString())).willReturn(true);

        SignatureData signatureData = new SignatureData();
        signatureData.setTicket(new TicketDto(ticketManager.solveTicket("someString")).toString());

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        given(signatureService.sendMobileSignRequest(any(SignatureData.class), anyString())).willReturn("theOrderRef");
        given(servletRequest.getHeader(eq("User-Agent"))).willReturn("salkdfjasklj Mobile aslkjdfalkj");

        Model model = mock(Model.class);

        // When
        signController.signMobileBankId(signatureData, servletRequest, model);

        // Then
        verify(model).addAttribute("isMobileDevice", true);
    }

    @Test
    public final void checkMobileBankIdResponseComplete() throws Exception {

        // Given
        Model model = mock(Model.class);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CollectResponseType collectResponseType = new CollectResponseType();
        collectResponseType.setProgressStatus(ProgressStatusType.COMPLETE);

        given(servletResponse.getOutputStream()).willReturn(new DelegatingServletOutputStream(baos));
        given(signatureService.collectRequest(eq("theOrderRef"))).willReturn(collectResponseType);

        // When
        signController.checkMobileBankIdResponse("theOrderRef", signController.objectToString(new SignatureData()),
                model, servletResponse);

        // Then
        assertTrue(baos.toString().contains("COMPLETE"));
    }

    @Test
    public final void checkMobileBankIdResponseOutstanding() throws Exception {

        // Given
        Model model = mock(Model.class);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CollectResponseType collectResponseType = new CollectResponseType();
        collectResponseType.setProgressStatus(ProgressStatusType.OUTSTANDING_TRANSACTION);

        given(servletResponse.getOutputStream()).willReturn(new DelegatingServletOutputStream(baos));
        given(signatureService.collectRequest(eq("theOrderRef"))).willReturn(collectResponseType);

        // When
        signController.checkMobileBankIdResponse("theOrderRef", signController.objectToString(new SignatureData()),
                model, servletResponse);

        // Then
        assertTrue(baos.toString().contains("OUTSTANDING_TRANSACTION"));
    }
}
