package se.vgregion.web.security.services;

import java.io.IOException;
import java.net.URI;
import java.security.SignatureException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.web.security.verification.SignatureValidator;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

@Service
public class SignatureService implements ApplicationContextAware {
    private SignatureStorage storage = null;

    private ApplicationContext applicationContext;

    @Autowired
    private SignatureValidator validator;

    public String buildPkiPostBackUri(String tbs, String submitUri) {
        StringBuilder pkiPostUrl = new StringBuilder();
        String verifyUrl = "/sign/verify?submitUri=";
        pkiPostUrl.append(verifyUrl);
        pkiPostUrl.append(submitUri);
        pkiPostUrl.append("&tbs=");
        pkiPostUrl.append(Base64.decodeBase64(tbs));

        return pkiPostUrl.toString();
    }

    public String save(String tbs, URI submitUrl, String signature) throws SignatureException {
        return save(tbs, submitUrl, signature, UUID.nameUUIDFromBytes(signature.getBytes()).toString());
    }

    public String save(String tbs, URI submitUrl, String signature, String signatureName)
            throws SignatureException {
        validator.validate(signature, tbs, PkiClient.NEXUS_PERSONAL_4X);

        setupIOBackend(submitUrl.getScheme());
        byte[] pkcs7 = Base64.decodeBase64(signature);

        if (storage == null) {
            throw new SignatureException(new IllegalStateException(
                    "No storage is configured for the specified protocol"));
        }
        String forwardString = null;
        try {
            forwardString = storage.save(submitUrl, pkcs7, signatureName);
        } catch (SignatureStoreageException e) {
            throw new SignatureException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SignatureException(e.getMessage(), e);
        }
        return forwardString;
    }

    private void setupIOBackend(String protocol) {
        String beanName = protocol + "-signature-storage";
        if (applicationContext.containsBean(beanName)) {
            storage = (SignatureStorage) applicationContext.getBean(beanName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
