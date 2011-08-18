package se.vgregion.security.sign;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

/**
 * Web implementation of {@link AbstractSignController}. This implementation is used for standard web access to the
 * signer service. To access the signer service as RESTfull WebService use {@link RestSignController}.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 * @see RestSignController
 */
@Controller
public class WebSignController extends AbstractSignController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSignController.class);

    /**
     * Constructs an instance of WebSignController.
     * 
     * @param signatureService
     *            a signatureService
     * @param eLegTypes
     *            a repository of e-legitimations
     */
    @Autowired
    public WebSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes) {
        super(signatureService, eLegTypes);
    }

    /**
     * Setup an {@link java.beans.PropertyEditor.PropertyEditor} to handle conversion of a {@link String}
     * representing an {@link ELegType} to ELegType.
     * 
     * @param binder
     *            WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ELegType.class, new ELegTypeEditor(geteLegTypes()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.security.sign.AbstractSignController#getClientTypes()
     */
    @Override
    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return super.getClientTypes();
    }

    /**
     * If a pki client type is missing in the request provide a list to select from to the client.
     * 
     * @param model
     *            the model
     * @param signData
     *            signature data
     * @return name of the view which displays a list of pki clients
     */
    @RequestMapping(value = "/prepare", method = POST, params = { "tbs", "submitUri" })
    public String prepareSignNoClientType(@ModelAttribute SignatureData signData, Model model) {
        model.addAttribute("signData", signData);
        return "clientTypeSelection";
    }

    /**
     * Preparation for signing, ie. encode <code>tbs</code> - To Be Signed and generate <code>nonce</code>. The
     * returned string should be the name of a pki client and should mapped to a view with the same name.
     * 
     * @param signData
     *            data used during signing
     * @param model
     *            the model
     * @param req
     *            the HttpServletRequest
     * @return the name of the pki client
     * 
     * @throws SignatureException
     *             if preparation fails
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/prepare", method = POST, params = { "tbs", "submitUri", "clientType" })
    public String prepareSign(@ModelAttribute SignatureData signData, Model model, HttpServletRequest req)
            throws SignatureException {
        model.addAttribute("postbackUrl", getPkiPostBackUrl(req));
        model.addAttribute("signData", signData);
        return super.prepareSign(signData);
    }

    /**
     * Verifies and submits the signature to submitUri.
     * 
     * @param signData
     *            data used during verification
     * @return name of view to show to the client
     * @throws SignatureException
     *             if validation or submission fails
     */
    @RequestMapping(value = "/verify", method = POST, params = { "encodedTbs", "submitUri",
            "clientType", "signature" })
    public String verifyAndSaveSignature(@ModelAttribute SignatureData signData) throws SignatureException {
        super.verifySignature(signData);
        String redirectLocation = getSignatureService().save(signData);
        if (!StringUtils.isBlank(redirectLocation)) {
            System.out.printf("WebSignController.verifyAndSaveSignature(%s)\n", "redirect:" + redirectLocation);
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }

    /**
     * Cancel the signing and informs the consumer.
     * 
     * @param signData
     *            data used during verification
     * @return name of view to show to the client
     * @throws SignatureException
     *             if validation or submission fails
     */
    @RequestMapping(value = "/cancel", method = POST)
    public String cancelSignature(@ModelAttribute SignatureData signData, HttpServletResponse response)
            throws SignatureException {
        System.out.println(signData);
        String redirectLocation = getSignatureService().abort(signData);
        if (!StringUtils.isBlank(redirectLocation)) {
            System.out.printf("WebSignController.cancelSignature(%s)\n", redirectLocation);
            return "redirect:" + redirectLocation;
        }
        return "errorForm";
    }

    private String getPkiPostBackUrl(HttpServletRequest req) {
        StringBuilder pkiPostUrl = new StringBuilder();
        String verifyUrl = "http" + (req.isSecure() ? "s" : "") + "://" + req.getServerName() + ":"
                + req.getServerPort() + req.getContextPath() + "/sign";
        pkiPostUrl.append(verifyUrl);

        return pkiPostUrl.toString();
    }

    /**
     * Handles all exceptions so that no stacktraces is displayed on a web page. Logs the complete stacktrace in
     * the configured log.
     * 
     * @param ex
     *            the exception
     * @param request
     *            the httpServletRequest
     * @return a {@link ModelAndView} with an error message and the view to display
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        LOGGER.error("Generic Error Handling", ex);
        ModelMap model = new ModelMap();
        model.addAttribute("class", ClassUtils.getShortName(ex.getClass()));
        return new ModelAndView("errorHandling", model);
    }

}
