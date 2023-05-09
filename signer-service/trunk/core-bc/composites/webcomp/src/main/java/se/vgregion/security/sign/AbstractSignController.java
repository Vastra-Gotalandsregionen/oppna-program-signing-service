package se.vgregion.security.sign;

import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

/**
 * Abstract class providing common signing functionality for concrete controller implementations.
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public abstract class AbstractSignController {

    @Autowired
    private SignatureService signatureService;
    @Autowired
    private Repository<ELegType, String> eLegTypes;
    @Autowired
    private TicketManager ticketManager;

    /**
     * Constructor forces implementors to provide a {@link SignatureService} and a {@link Repository} with
     * {@link ELegType}s.
     *
     * @param signatureService a signatureService
     * @param eLegTypes        eLegTypes
     * @param ticketManager    the {@link TicketManager} to use
     */
    public AbstractSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes,
                                  TicketManager ticketManager) {
        this.signatureService = signatureService;
        this.eLegTypes = eLegTypes;
        this.ticketManager = ticketManager;
    }

    protected SignatureService getSignatureService() {
        return signatureService;
    }

    protected Repository<ELegType, String> geteLegTypes() {
        return eLegTypes;
    }

    protected TicketManager getTicketManager() {
        return ticketManager;
    }

    /**
     * Returns an unmodifiable collection of {@link ELegType}s.
     *
     * @return an unmodifiable collection of {@link ELegType}s.
     */
    public Collection<ELegType> getClientTypes() {
        return Collections.unmodifiableCollection(eLegTypes.findAll());
    }

    /**
     * Preparation for signing, ie. encode <code>tbs</code> - To Be Signed and generate <code>nonce</code>. The
     * returned string should be the name of a pki client and should mapped to a view with the same name.
     *
     * @param signData data used during signing
     * @return the name of the pki client
     * @throws SignatureException if preparation fails
     */
    public String prepareSign(SignatureData signData) throws SignatureException {
        encodeTbs(signData);
        if (!(signData.getPkiClient().equals(PkiClient.MOBILE_BANKID) || signData.getPkiClient().equals(PkiClient.MTLS))) {
            String nonce = signatureService.generateNonce(signData.getPkiClient());
            signData.setNonce(nonce);
        }
        return signData.getPkiClient().toString();
    }

    /**
     * After signing the signature must be verified to ensure that the certificate used for signing is valid.
     *
     * @param signData data used during the verification
     * @return true if signature is valid, throws an {@link SignatureException} otherwise
     * @throws SignatureException if validation fails
     */
    public boolean verifySignature(SignatureData signData) throws SignatureException {
        signatureService.verifySignature(signData);
        return true;
    }

    private void encodeTbs(SignatureData signData) throws SignatureException {
        String encodedTbs = signatureService.encodeTbs(signData.getTbs(), signData.getPkiClient());
        signData.setEncodedTbs(encodedTbs);
    }
}
