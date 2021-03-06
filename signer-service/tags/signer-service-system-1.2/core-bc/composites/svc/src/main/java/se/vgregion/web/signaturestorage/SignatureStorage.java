package se.vgregion.web.signaturestorage;

import java.io.IOException;
import java.net.URI;

import se.vgregion.signera.signature._1.SignatureEnvelope;

/**
 * Defines an interface for storing signatures.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public interface SignatureStorage {
    /**
     * Submits the signature to subitUri. Implementors of this method should provide a way to store the signature
     * using the specified submitUri. The method should return a callback url as a hint to where it wants the
     * client to redirect to. If null or an empty string is returned we don't care what the client to with the
     * response.
     * 
     * @param submitUri
     *            the uri to submit the signature to.
     * @param envelope
     *            an envelope containing signature information
     * @return a callback url, null or empty if callback handling is not applicable
     * @throws SignatureStoreageException
     *             if signature submission is failed
     * @throws IOException
     *             if an i/o error arises
     */
    String submitSignature(URI submitUri, SignatureEnvelope envelope)
            throws SignatureStoreageException, IOException;
}
