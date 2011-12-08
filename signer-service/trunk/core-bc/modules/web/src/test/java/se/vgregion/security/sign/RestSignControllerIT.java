package se.vgregion.security.sign;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import se.vgregion.signera.signature._1.SignatureFormat;
import se.vgregion.signera.signature._1.SignatureVerificationRequest;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.security.services.ServiceIdService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

/**
 * @author Patrik Bergström
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:root-context-test.xml")
public class RestSignControllerIT {

    private String baseAddress = "http://localhost:9000/service";

    @Autowired
    private RestSignController controller;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        TicketManager ticketManager = TicketManager.getInstance();
        ServiceIdService service = mock(ServiceIdService.class);
        when(service.containsServiceId(eq("existingServiceId"))).thenReturn(true);
        when(service.containsServiceId(eq("nonExistingServiceId"))).thenReturn(false);
        ticketManager.setServiceIdService(service);

//        controller = new RestSignController(Mockito.mock(SignatureService.class), mock(Repository.class),
//                ticketManager);

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(RestSignController.class);
        sf.setResourceProvider(RestSignController.class, new SingletonResourceProvider(controller));
        sf.setAddress(baseAddress);
        sf.create();
    }

    @Test
    public void testVerifySignatureWithXmlDigSig() throws IOException, JAXBException {
                //Read a signature
        InputStream signatureStream = this.getClass().getClassLoader().getResourceAsStream("X509Signature.txt");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(signatureStream, baos);

        //Create the SignatureVerificationRequest
        SignatureVerificationRequest request = new SignatureVerificationRequest();
        request.setSignature(baos.toString());
        request.setSignatureFormat(SignatureFormat.XMLDIGSIG);

        //Marshal the SignatureVerificationRequest
        JAXBContext jc = JAXBContext.newInstance(SignatureVerificationRequest.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jc.createMarshaller().marshal(request, os);

        //Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<String>(os.toString(), headers);

        RestTemplate template = new RestTemplate();

        //Create proxy
//        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) template.getRequestFactory();
//        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888)));

        ResponseEntity<String> response = template.exchange(baseAddress + "/verifySignature", HttpMethod.POST,
                entity, String.class);

        String body = response.getBody();

        System.out.println(body);
    }

    @Test
    public void testVerifySignatureWithCmsSignature() throws IOException, JAXBException {
                //Read a signature
        InputStream signatureStream = this.getClass().getClassLoader().getResourceAsStream("CMSSignature.txt");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(signatureStream, baos);

        //Create the SignatureVerificationRequest
        SignatureVerificationRequest request = new SignatureVerificationRequest();
        request.setSignature(baos.toString());
        request.setSignatureFormat(SignatureFormat.CMS);

        //Marshal the SignatureVerificationRequest
        JAXBContext jc = JAXBContext.newInstance(SignatureVerificationRequest.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jc.createMarshaller().marshal(request, os);

        //Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<String>(os.toString(), headers);

        RestTemplate template = new RestTemplate();

        //Create proxy
//        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) template.getRequestFactory();
//        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888)));

        ResponseEntity<String> response = template.exchange(baseAddress + "/verifySignature", HttpMethod.POST,
                entity, String.class);

        String body = response.getBody();

        System.out.println(body);
    }

}
