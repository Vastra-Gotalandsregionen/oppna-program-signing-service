package se.vgregion.ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.web.security.services.ServiceIdService;

import javax.annotation.PostConstruct;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Class for managing tasks associated with {@link Ticket}s, like creation and verification. To ensure that a
 * {@link Ticket} is valid it is created with a signature which can verify only if the timestamp of the
 * {@link Ticket} is not modified. Therefore it is impossible to tamper with the timestamp.
 * <p/>
 * The class is singleton since verification of a {@link Ticket} can only be made by the same instance which
 * signed it.
 *
 * @author Anders Asplund
 * @author Patrik Bergström
 */
@Service
public final class TicketManager {

    private static final Logger LOG = LoggerFactory.getLogger(TicketManager.class);
    private static final Long MILLIS_IN_A_MINUTE = 1000L * 60;
    private static final Long KEEP_ALIVE = 5 * MILLIS_IN_A_MINUTE; // 5 minutes;
    private static final String KEY_ALGORITHM = "DSA";
    private static final int KEY_SIZE = 1024;
    private static final String SIGNATURE_ALGORITHM = "SHA256withDSA";

    @Value("${ticket.sign.key.private}")
    private String privateKeyBase64;

    @Value("${ticket.sign.key.public}")
    private String publicKeyBase64;

    private ServiceIdService serviceIdService;

    private KeyPair keyPair;
    private Signature signature;

    // May be used to generate static keys used in application.
    public static void main(String[] args) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            kpg.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = kpg.generateKeyPair();

            byte[] encodedPrivateKey = keyPair.getPrivate().getEncoded();
            byte[] encodedPublicKey = keyPair.getPublic().getEncoded();

            String privateKeyBase64 = Base64.getEncoder().encodeToString(encodedPrivateKey);
            String publicKeyBase64 = Base64.getEncoder().encodeToString(encodedPublicKey);

            System.out.println(privateKeyBase64);
            System.out.println(publicKeyBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TicketManager() {

    }

    @PostConstruct
    public void init() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            PrivateKey privateKey = keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64))
            );

            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64));

            PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

            keyPair = new KeyPair(publicKey, privateKey);
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setServiceIdService(ServiceIdService serviceIdService) {
        this.serviceIdService = serviceIdService;
    }

    /**
     * Solves a {@link Ticket}, i.e. creates a new {@link Ticket} which is signed. To solve a {@link Ticket} a
     * serviceId must be provided to authenticate the requester.
     *
     * @param serviceId serviceId
     * @return the new {@link Ticket}
     * @throws TicketException if the serviceId is not valid
     */
    public Ticket solveTicket(String serviceId) throws TicketException {
        boolean exists = serviceIdService.containsServiceId(serviceId);
        if (!exists) {
            throw new TicketException(String.format("Service-id %s cannot be found.", serviceId));
        }

        long due = System.currentTimeMillis() + KEEP_ALIVE;
        final int thousand = 1000;
        due = due / thousand * thousand; //round to whole seconds

        byte[] signatureBytes = createSignature(due);
        Ticket ticket = new Ticket(due, signatureBytes);
        return ticket;
    }

    private byte[] createSignature(Long due) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();
            signature.initSign(privateKey);
            signature.update(dueToBytes(due));
            return signature.sign();
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verifySignature(Ticket ticket) {
        try {
            signature.initVerify(keyPair.getPublic());
            signature.update(dueToBytes(ticket.getDue()));
            return signature.verify(ticket.getSignature());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies the {@link Ticket} according to its date and signature.
     *
     * @param ticket the {@link Ticket}
     * @return <code>true</code> if the {@link Ticket} is valid or <code>false</code> otherwise
     */
    public TicketVerifyResponse verifyTicket(Ticket ticket) {
        return new TicketVerifyResponse(verifyDue(ticket), verifySignature(ticket));
    }

    private boolean verifyDue(Ticket ticket) {
        final long now = System.currentTimeMillis();
        Long due = ticket.getDue();
        return (!isNull(due) && due >= now);
    }

    private static boolean isNull(Object o) {
        return o == null;
    }

    private byte[] dueToBytes(Long due) {
        return due.toString().getBytes();
    }

    public static class TicketVerifyResponse {

        private boolean dueOk;
        private boolean signatureOk;

        public TicketVerifyResponse(boolean dueOk, boolean signatureOk) {
            this.dueOk = dueOk;
            this.signatureOk = signatureOk;
        }

        public boolean isDueOk() {
            return dueOk;
        }

        public boolean isSignatureOk() {
            return signatureOk;
        }

        public boolean verifyOk() {
            return dueOk && signatureOk;
        }
    }
}
